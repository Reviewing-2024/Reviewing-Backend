package com.reviewing.review.review.service;

import static com.slack.api.model.block.Blocks.actions;
import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.divider;
import static com.slack.api.model.block.Blocks.header;
import static com.slack.api.model.block.Blocks.image;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.button;

import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.repository.CourseRepository;
import com.reviewing.review.member.entity.Member;
import com.reviewing.review.member.repository.MemberRepository;
import com.reviewing.review.review.entity.Review;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.TextObject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SlackService {

    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    @Value("${slack.bot-token}")
    private String token;

    @Value(value="${slack.channel.monitor}")
    private String channel;

    public void sendMessageToSlack(Review review) {

        List<TextObject> textObjects = new ArrayList<>();
        textObjects.add(markdownText("*작성자:* " + review.getMember().getNickname() + "\n"));
        textObjects.add(markdownText("*강의 이름:* " + review.getCourse().getTitle() + "\n"));
        textObjects.add(markdownText("*강의 url:* " + review.getCourse().getUrl() + "\n"));
        textObjects.add(markdownText("*리뷰 평접:* " + review.getRating() + "\n"));
        textObjects.add(markdownText("*리뷰 내용:* " + review.getContents() + "\n"));

        MethodsClient methods = Slack.getInstance().methods(token);

        List<LayoutBlock> blocks = new ArrayList<>();

        blocks.add(header(header -> header.text(plainText("🎉새로운 리뷰가 작성되었습니다!"))));
        blocks.add(divider());
        blocks.add(section(section -> section.fields(textObjects)));

        blocks.add(image(img -> img
                .imageUrl(review.getCertification())
                .altText("증명 자료 이미지")
        ));

//        blocks.add(actions(a -> a.elements(List.of(
//                button(b -> b
//                        .text(plainText("✅ 승인"))
//                        .style("primary")
//                        .value(review.getId().toString())
//                        .actionId("approve_review")),
//                button(b -> b
//                        .text(plainText("❌ 거절"))
//                        .style("danger")
//                        .value(review.getId().toString())
//                        .actionId("reject_review"))
//        ))));

        ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(channel)
                .blocks(blocks).build();

        try {
            methods.chatPostMessage(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send message to Slack", e);
        }
    }

}
