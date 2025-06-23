package com.github.cinari4.springinfralab.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class LocalRedisBasicTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * String (Value) 자료구조의 SET, GET, DELETE 동작 검증
     */
    @Test
    public void testSetGetDelete() {
        UUID randomKey = UUID.randomUUID();
        String key = "basic:" + randomKey;
        UUID randomVal = UUID.randomUUID();
        String value = "basic:" +  randomVal;

        // 1) SET: 'basic:testKey' 키에 'basic:testValue' 저장
        redisTemplate.opsForValue().set(key, value);
        // 2) GET: 저장된 값 조회 후 실제 값과 일치하는지 검증
        String result = redisTemplate.opsForValue().get(key);
        assertEquals(value, result, "Redis GET should return the value set");

        // 3) DELETE: 키 삭제 후 조회 시 null 반환 확인
        Boolean deleted = redisTemplate.delete(key);
        assertTrue(deleted, "Redis DELETE should return true for existing key");
        assertNull(redisTemplate.opsForValue().get(key), "After delete, GET should return null");
    }

    /**
     * Hash 자료구조의 HSET, HGET, size, entries, HDEL 및 전체 삭제 동작 검증
     */
    @Test
    public void testHashOperations() {
        UUID randomKey = UUID.randomUUID();
        String hashKey = "hash:" + randomKey;
        String field1 = "field1";
        String value1 = "value1";
        String field2 = "field2";
        String value2 = "value2";

        // 1) HSET: hash:test 키에 field1, field2 필드와 값 저장
        redisTemplate.opsForHash().put(hashKey, field1, value1);
        redisTemplate.opsForHash().put(hashKey, field2, value2);

        // 2) HGET: 각 필드 조회하여 저장된 값 일치하는지 확인
        Object result1 = redisTemplate.opsForHash().get(hashKey, field1);
        Object result2 = redisTemplate.opsForHash().get(hashKey, field2);
        assertEquals(value1, result1, "HGET should return the first field value");
        assertEquals(value2, result2, "HGET should return the second field value");

        // 3) size(): 해시 내 필드 개수 조회 (2개)
        Long size = redisTemplate.opsForHash().size(hashKey);
        assertEquals(2L, size, "Hash size should be 2 after two HSETs");

        // 4) entries(): 해시 전체 필드-값 맵 조회
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(hashKey);
        assertEquals(2, entries.size(), "HGETALL should return two entries");
        assertEquals(value1, entries.get(field1));
        assertEquals(value2, entries.get(field2));

        // 5) HDEL: 단일 필드 삭제 후 size 및 값 null 확인
        Long removed = redisTemplate.opsForHash().delete(hashKey, field1);
        assertEquals(1L, removed, "HDEL should remove one field");
        assertNull(redisTemplate.opsForHash().get(hashKey, field1), "After HDEL, field1 should be null");
        assertEquals(1L, redisTemplate.opsForHash().size(hashKey), "Hash size should be 1 after deleting one field");

         // 6) delete(): 해시 전체 삭제 후 조회 시 null 확인
        redisTemplate.delete(hashKey);
        assertNull(redisTemplate.opsForHash().get(hashKey, field2), "After deleting key, HGET should return null");
    }

    /**
     * Sorted Set 자료구조의 ZADD, zCard, score, range, removeRangeByScore 기능 검증
     */
    @Test
    public void testSortedSetOperations() {
        UUID randomKey = UUID.randomUUID();
        String key = "zset:" + randomKey;

        // 1) ZADD: zset:test 키에 member1~3 추가 (각각 score 1.0,2.0,3.0)
        redisTemplate.opsForZSet().add(key, "member1", 1.0);
        redisTemplate.opsForZSet().add(key, "member2", 2.0);
        redisTemplate.opsForZSet().add(key, "member3", 3.0);

        // 2) zCard: 전체 멤버 개수 조회 (3개)
        Long card = redisTemplate.opsForZSet().zCard(key);
        assertEquals(3L, card, "Sorted set size should be 3 after ZADD");

        // 3) score: 특정 멤버(member2)의 score 조회 및 검증
        Double score = redisTemplate.opsForZSet().score(key, "member2");
        assertNotNull(score, "ZSCORE should not return null for existing member2");
        assertEquals(Double.valueOf(2.0), score, "ZSCORE should return correct score for member2");

        // 4) range: 전체 멤버를 score 오름차순으로 조회
        Set<String> range = redisTemplate.opsForZSet().range(key, 0, -1);
        assertNotNull(range, "ZRANGE should not return null");
        assertEquals(3, range.size(), "ZRANGE should return 3 members");
        Iterator<String> it = range.iterator();
        assertEquals("member1", it.next());
        assertEquals("member2", it.next());
        assertEquals("member3", it.next());

        // 5) removeRangeByScore: score <=2.0인 멤버 제거 후 개수 및 남은 멤버 확인
        Long removed = redisTemplate.opsForZSet().removeRangeByScore(key, Double.NEGATIVE_INFINITY, 2.0);
        assertEquals(2L, removed, "removeRangeByScore should remove two members");
        assertEquals(1L, redisTemplate.opsForZSet().zCard(key), "Sorted set size should be 1 after removal");

        // 6) delete + range: 키 삭제 후 range 조회 시 비어있는지 확인
        redisTemplate.delete(key);
        Set<String> empty = redisTemplate.opsForZSet().range(key, 0, -1);
        assertTrue(empty == null || empty.isEmpty(), "After delete, range should be empty");
    }
}
