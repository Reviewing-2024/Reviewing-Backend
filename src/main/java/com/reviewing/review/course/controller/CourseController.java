package com.reviewing.review.course.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.course.domain.CategoryRequestDto;
import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.entity.Platform;
import com.reviewing.review.course.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/platform")
    public ResponseEntity<List<Platform>> findPlatforms(){
        List<Platform> platforms = courseService.findPlatforms();
        return ResponseEntity.ok().body(platforms);
    }

    @GetMapping("/platform/category")
    public ResponseEntity<List<CategoryResponseDto>> findCategoriesByPlatform(
            @RequestParam String platform) {
        List<CategoryResponseDto> categories = courseService.findCategories(platform);

        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseResponseDto> findCourseById(@PathVariable UUID courseId,
            HttpServletRequest request) {

        CourseResponseDto courseResponseDto = courseService.findCourseById(courseId);

        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null) {
            return ResponseEntity.ok().body(courseResponseDto);
        }
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        return ResponseEntity.ok()
                .body(courseService.checkCourseWished(courseResponseDto, memberId));
    }

    @GetMapping("/course")
    public ResponseEntity<CourseResponseDto> findCourseBySlug(@RequestParam("courseSlug") String courseSlug,
            HttpServletRequest request) {

        CourseResponseDto courseResponseDto = courseService.findCourseBySlug(courseSlug);

        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null) {
            return ResponseEntity.ok().body(courseResponseDto);
        }
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        return ResponseEntity.ok()
                .body(courseService.checkCourseWished(courseResponseDto, memberId));
    }

    @GetMapping("/courses/search")
    public ResponseEntity<List<CourseResponseDto>> searchCourses(
            @RequestParam(value = "keyword") String keyword,
            @RequestParam(value = "lastCourseId", required = false) UUID lastCourseId,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");

        List<CourseResponseDto> courses = courseService.searchCoursesByKeyword(keyword,
                lastCourseId);

        if (jwtHeader == null) {
            return ResponseEntity.ok().body(courses);
        }

        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        return ResponseEntity.ok().body(courseService.checkCoursesWished(courses, memberId));
    }

    @GetMapping("/")
    public ResponseEntity<List<CourseResponseDto>> findAllCoursesBySorting(
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "lastCourseId", required = false) UUID lastCourseId,
            @RequestParam(value = "lastRating", required = false) BigDecimal lastRating,
            @RequestParam(value = "lastComments", required = false) Integer lastComments,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");

        List<CourseResponseDto> courses = courseService.findAllCoursesBySorting(sortType,
                lastCourseId, lastRating, lastComments);

        if (jwtHeader == null) {
            return ResponseEntity.ok().body(courses);
        }

        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        return ResponseEntity.ok().body(courseService.checkCoursesWished(courses, memberId));
    }

    @GetMapping("/courses/{platform}")
    public ResponseEntity<List<CourseResponseDto>> findCoursesByPlatform(
            @PathVariable String platform,
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "lastCourseId", required = false) UUID lastCourseId,
            @RequestParam(value = "lastRating", required = false) BigDecimal lastRating,
            @RequestParam(value = "lastComments", required = false) Integer lastComments,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");

        List<CourseResponseDto> courses = courseService.findCoursesByPlatform(platform, sortType,
                lastCourseId, lastRating, lastComments);

        if (jwtHeader == null) {
            return ResponseEntity.ok().body(courses);
        }

        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        return ResponseEntity.ok().body(courseService.checkCoursesWished(courses, memberId));
    }

    @GetMapping("/courses/{platform}/{category}")
    public ResponseEntity<List<CourseResponseDto>> findCoursesByPlatformAndCategory(
            @PathVariable("platform") String platform, @PathVariable("category") String category,
            @RequestParam(value = "sort", required = false) String sortType,
            @RequestParam(value = "lastCourseId", required = false) UUID lastCourseId,
            @RequestParam(value = "lastRating", required = false) BigDecimal lastRating,
            @RequestParam(value = "lastComments", required = false) Integer lastComments,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");

        List<CourseResponseDto> courses = courseService.findCoursesByPlatformAndCategory(platform,
                category, sortType, lastCourseId, lastRating, lastComments);

        if (jwtHeader == null) {
            return ResponseEntity.ok().body(courses);
        }

        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        return ResponseEntity.ok().body(courseService.checkCoursesWished(courses, memberId));
    }

    @PostMapping("/courses/{courseId}/wish")
    public ResponseEntity<CourseResponseDto> createCourseWish(@PathVariable UUID courseId,
            @RequestParam(value = "wished") boolean wished,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);
        if (memberId == null) {
            return ResponseEntity.status(600).body(null);
        }

        if (wished) { // wished=true -> 강의 찜 취소
            if (!courseService.checkCourseWishedByMember(courseId, memberId)) {
                return ResponseEntity.status(605).body(null);
            }
            courseService.removeCourseWish(courseId, memberId);
            // 수정
            CourseResponseDto courseResponseDto = courseService.findCourseById(courseId);
            return ResponseEntity.ok()
                    .body(courseService.checkCourseWished(courseResponseDto, memberId));
        }

        // wished=false -> 강의 찜
        if (courseService.checkCourseWishedByMember(courseId, memberId)) {
            return ResponseEntity.status(606).body(null);
        }
        courseService.createCourseWish(courseId, memberId);
        // 수정
        CourseResponseDto courseResponseDto = courseService.findCourseById(courseId);
        return ResponseEntity.ok()
                .body(courseService.checkCourseWished(courseResponseDto, memberId));
    }

}
