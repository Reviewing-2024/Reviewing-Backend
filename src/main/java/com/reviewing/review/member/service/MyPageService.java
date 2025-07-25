package com.reviewing.review.member.service;

import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.member.entity.Member;
import com.reviewing.review.member.domain.MyReviewResponseDto;
import com.reviewing.review.member.repository.MyPageRepository;
import com.reviewing.review.review.domain.ReviewStateType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
@Transactional
public class MyPageService {

    private final MyPageRepository myPageRepository;

    public List<MyReviewResponseDto> findMyReviewsByStatus(String status, Long memberId) {
        return myPageRepository.findMyReviewsByStatus(checkReviewState(status), memberId);
    }

    public ReviewStateType checkReviewState(String reviewState) {
        for (ReviewStateType value : ReviewStateType.values()) {
            if (value.getReviewState().equals(reviewState)) {
                return value;
            }
        }
        // 예외 처리
        return null;
    }

    public void updateUserNickname(Long memberId, String nickName) {
        myPageRepository.updateUserNickname(memberId, nickName);
    }

    public Member findMemberById(Long memberId) {
        return myPageRepository.findMemberById(memberId);
    }

    public List<CourseResponseDto> findWishCourseByMember(Long memberId) {
        return myPageRepository.findWishCourseByMember(memberId);
    }

}
