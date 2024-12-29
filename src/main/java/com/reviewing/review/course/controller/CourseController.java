package com.reviewing.review.course.controller;

import com.reviewing.review.config.jwt.JwtTokenProvider;
import com.reviewing.review.course.domain.CategoryRequestDto;
import com.reviewing.review.course.domain.CategoryResponseDto;
import com.reviewing.review.course.domain.CourseResponseDto;
import com.reviewing.review.course.domain.Platform;
import com.reviewing.review.course.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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

    @GetMapping("/")
    public ResponseEntity<List<CourseResponseDto>> findAllCoursesBySorting(
            @RequestParam(value = "sort", required = false) String sortType,
            HttpServletRequest request) {

        List<CourseResponseDto> courses;

        String jwtHeader = request.getHeader("Authorization");

        if (jwtHeader == null) {
            courses = courseService.findAllCoursesBySorting(sortType);
            return ResponseEntity.ok().body(courses);
        }

        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        courses = courseService.findAllCoursesBySorting(sortType, memberId);
        return ResponseEntity.ok().body(courses);
    }

    @GetMapping("/platform")
    public ResponseEntity<List<Platform>> findPlatforms(){
        List<Platform> platforms = courseService.findPlatforms();
        return ResponseEntity.ok().body(platforms);
    }

    @GetMapping("/platform/category")
    public ResponseEntity<List<CategoryResponseDto>> findCategoriesByPlatform(
            @RequestBody CategoryRequestDto categoryRequestDto) {
        List<CategoryResponseDto> categories = courseService.findCategories(
                categoryRequestDto.getPlatform());

        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/courses/{platform}")
    public ResponseEntity<List<CourseResponseDto>> findCoursesByPlatform(
            @PathVariable String platform,
            @RequestParam(value = "sort", required = false) String sortType
            , HttpServletRequest request) {

        List<CourseResponseDto> courses;

        String jwtHeader = request.getHeader("Authorization");

        if (jwtHeader == null) {
            courses = courseService.findCoursesByPlatform(platform, sortType);
            return ResponseEntity.ok().body(courses);
        }

        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        courses = courseService.findCoursesByPlatform(platform, sortType, memberId);

        return ResponseEntity.ok().body(courses);
    }

    @GetMapping("/courses/{platform}/{category}")
    public ResponseEntity<List<CourseResponseDto>> findCoursesByPlatformAndCategory(
            @PathVariable("platform") String platform, @PathVariable("category") String category,
            @RequestParam(value = "sort", required = false) String sortType
            , HttpServletRequest request) {

        List<CourseResponseDto> courses;

        String jwtHeader = request.getHeader("Authorization");

        if (jwtHeader == null) {
            courses = courseService.findCoursesByPlatformAndCategory(platform,
                    category, sortType);
            return ResponseEntity.ok().body(courses);
        }

        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        courses = courseService.findCoursesByPlatformAndCategory(platform,
                category, sortType, memberId);

        return ResponseEntity.ok().body(courses);
    }

    @PostMapping("/courses/{courseId}/wish")
    public ResponseEntity<CourseResponseDto> createCourseWish(@PathVariable Long courseId,
            @RequestParam(value = "wished") boolean wished,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        if (wished) { // wished=true -> 강의 찜 취소
            courseService.removeCourseWish(courseId, memberId);
            CourseResponseDto courseResponseDto = courseService.findCourseById(courseId,memberId);
            return ResponseEntity.ok().body(courseResponseDto);
        }

        // wished=false -> 강의 찜
        courseService.createCourseWish(courseId, memberId);
        CourseResponseDto courseResponseDto = courseService.findCourseById(courseId,memberId);
        return ResponseEntity.ok().body(courseResponseDto);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseResponseDto> findCourseById(@PathVariable Long courseId,
            HttpServletRequest request) {

        String jwtHeader = request.getHeader("Authorization");
        String token = jwtHeader.replace("Bearer ", "");
        Long memberId = jwtTokenProvider.getMemberIdByRefreshToken(token);

        CourseResponseDto courseResponseDto = courseService.findCourseById(courseId, memberId);

        return ResponseEntity.ok().body(courseResponseDto);
    }

}
