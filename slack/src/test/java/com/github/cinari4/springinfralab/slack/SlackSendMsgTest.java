package com.github.cinari4.springinfralab.slack;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.ActionsBlock;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.PlainTextObject;
import com.slack.api.model.block.element.BlockElement;
import com.slack.api.model.block.element.ButtonElement;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SlackSendMsgTest {
    @Test
    void slackSendMsgTest() throws SlackApiException, IOException {
        String token = "testToken";
        String channel = "testChannel";
        MethodsClient methods = Slack.getInstance().methods(token);

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channel)
                .text("Hello World!")
                .build();

        methods.chatPostMessage(request);

    }

    @Test
    void slackSendTemplatedMsgTest() throws SlackApiException, IOException {
        String token = "testToken";
        String channel = "testChannel";

        String actionText = "Player Detail";
        String actionUrl = "https://example.com/player/123";

        // 버튼 엘리먼트 생성
        ButtonElement button = ButtonElement.builder()
                .text(PlainTextObject.builder().text(actionText).build())
                .url(actionUrl)
                .actionId("player_detail")
                .build();

        // 액션 블록에 버튼 추가
        ActionsBlock actionsBlock = ActionsBlock.builder()
                .elements(Collections.singletonList(button))
                .build();

        List<LayoutBlock> blocks = Collections.singletonList(actionsBlock);

        // 메시지 전송 요청 구성
        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channel)
                .blocks(blocks)
                .text("플레이어 상세 보기 버튼")
                .build();

        // 메시지 전송
        Slack.getInstance().methods(token).chatPostMessage(request);
    }
}
