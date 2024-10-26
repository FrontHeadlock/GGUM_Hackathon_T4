package com.ardkyer.rion.controller;

import com.amazonaws.services.s3.model.S3Object;
import com.ardkyer.rion.entity.Comment;
import com.ardkyer.rion.entity.Video;
import com.ardkyer.rion.entity.User;
import com.ardkyer.rion.entity.Reservation;
import com.ardkyer.rion.entity.Hashtag;
import com.ardkyer.rion.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.io.IOException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management API")
@RequiredArgsConstructor
public class ProductController {

    private final VideoService videoService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final CommentService commentService;
    private final FollowService followService;

    // Request/Response DTOs
    @Getter @Setter
    public static class ReservationRequest {
        private Integer quantity;
    }

    @Getter @Setter
    public static class ProductResponse {
        private Long id;
        private String title;
        private String description;
        private Integer totalQuantity;
        private Integer availableQuantity;
        private String status;
        private UserInfo user;
        private boolean isFollowedByCurrentUser;
        private List<String> hashtags;
        private String imageUrl;

        @Getter @Setter
        public static class UserInfo {
            private Long id;
            private String username;
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        List<Video> videos = videoService.getAllVideos();
        List<ProductResponse> response = videos.stream()
                .map(video -> convertToProductResponse(video, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product details", description = "Retrieves details of a specific product")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "ID of the product") @PathVariable Long id) {  // Authentication 제거
        return videoService.getVideoById(id)
                .map(video -> ResponseEntity.ok(convertToProductResponse(video, null)))  // null 전달
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a product", description = "Creates a new product with image")
    public ResponseEntity<ProductResponse> createProduct(
            @ModelAttribute ProductCreateRequest request) throws IOException {

        if (!request.getImage().getContentType().startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }

        // 테스트용 임시 사용자 생성 또는 조회
        User currentUser = userService.findByUsername("as12"); // 데이터베이스에 있는 사용자명으로 변경

        Video video = new Video();
        video.setTitle(request.getTitle());
        video.setDescription(request.getDescription());
        video.setUser(currentUser);
        video.setTotalQuantity(request.getTotalQuantity());
        video.setAvailableQuantity(request.getTotalQuantity());
        video.setReservationStatus(Video.ReservationStatus.AVAILABLE);

        Set<String> hashtagSet = extractHashtags(request.getDescription(), request.getHashtags());
        video = videoService.uploadVideo(video, request.getImage(), hashtagSet);

        return ResponseEntity.ok(convertToProductResponse(video, null));  // null 전달
    }

    @Getter @Setter
    public static class ProductCreateRequest {
        private String title;
        private String description;
        private MultipartFile image;
        private Integer totalQuantity;
        private String hashtags;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile file,
            @RequestParam("totalQuantity") Integer totalQuantity) throws IOException {  // Authentication 제거

        Optional<Video> videoOptional = videoService.getVideoById(id);
        if (videoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Video video = videoOptional.get();
        // 인증 체크 제거

        video.setTitle(title);
        video.setDescription(description);
        video.setTotalQuantity(totalQuantity);

        if (file != null && !file.isEmpty()) {
            if (!file.getContentType().startsWith("image/")) {
                return ResponseEntity.badRequest().build();
            }
            video = videoService.updateVideoWithImage(video, file);
        } else {
            video = videoService.updateVideo(video);
        }

        return ResponseEntity.ok(convertToProductResponse(video, null));  // null 전달
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes an existing product")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {  // Authentication 제거
        try {
            Video video = videoService.getVideoById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            videoService.deleteVideo(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/reserve")
    @Operation(summary = "Reserve a product", description = "Creates a reservation for a product")
    public ResponseEntity<?> createReservation(
            @PathVariable Long id,
            @RequestBody ReservationRequest request) {  // Authentication 제거
        try {
            // 테스트용 임시 사용자
            User currentUser = userService.findByUsername("as12"); // 데이터베이스에 있는 사용자명으로 변경

            Video video = videoService.getVideoById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Product not found"));

            if (request.getQuantity() == null || request.getQuantity() <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid quantity"));
            }

            if (request.getQuantity() > video.getAvailableQuantity()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Insufficient stock"));
            }

            Reservation reservation = reservationService.reserve(video, currentUser, request.getQuantity());

            return ResponseEntity.ok(Map.of(
                    "reservationId", reservation.getId(),
                    "remainingQuantity", video.getAvailableQuantity()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/images/{fileName:.+}")
    @Operation(summary = "Get product image", description = "Retrieves a product image")
    public ResponseEntity<InputStreamResource> getProductImage(
            @Parameter(description = "Name of the image file")
            @PathVariable String fileName) {
        S3Object s3Object = videoService.getVideoFile(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(s3Object.getObjectMetadata().getContentLength());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(s3Object.getObjectContent()));
    }

    private ProductResponse convertToProductResponse(Video video, Authentication authentication) {
        ProductResponse response = new ProductResponse();
        response.setId(video.getId());
        response.setTitle(video.getTitle());
        response.setDescription(video.getDescription());
        response.setTotalQuantity(video.getTotalQuantity());
        response.setAvailableQuantity(video.getAvailableQuantity());
        response.setStatus(video.getReservationStatus().name());

        List<String> hashtagStrings = video.getHashtags().stream()
                .map(Hashtag::getName)
                .collect(Collectors.toList());
        response.setHashtags(hashtagStrings);

        response.setImageUrl("/api/products/images/" + video.getImageUrl());

        ProductResponse.UserInfo userInfo = new ProductResponse.UserInfo();
        userInfo.setId(video.getUser().getId());
        userInfo.setUsername(video.getUser().getUsername());
        response.setUser(userInfo);

        // 인증 관련 부분 제거
        response.setFollowedByCurrentUser(false);

        return response;
    }

    private Set<String> extractHashtags(String description, String additionalHashtags) {
        Set<String> hashtagSet = Arrays.stream(description.split(" "))
                .filter(tag -> tag.startsWith("#"))
                .collect(Collectors.toSet());

        if (additionalHashtags != null && !additionalHashtags.trim().isEmpty()) {
            hashtagSet.addAll(Arrays.stream(additionalHashtags.split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet()));
        }

        return hashtagSet;
    }
}