package com.fast.campus.simplesns.controller;

import com.fast.campus.simplesns.controller.request.PostCreateRequest;
import com.fast.campus.simplesns.controller.request.PostModifyRequest;
import com.fast.campus.simplesns.controller.response.PostResponse;
import com.fast.campus.simplesns.controller.response.Response;
import com.fast.campus.simplesns.model.Post;
import com.fast.campus.simplesns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId, @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(postId, request.getTitle(), request.getBody(), authentication.getName());
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(@PathVariable Integer postId, Authentication authentication) {
        postService.delete(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(Pageable pageable) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> list(Pageable pageable, Authentication authentication) {
        return Response.success(postService.my(pageable, authentication.getName()).map(PostResponse::fromPost));
    }
}
