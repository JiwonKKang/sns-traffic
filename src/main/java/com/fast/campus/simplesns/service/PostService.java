package com.fast.campus.simplesns.service;

import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SimpleSnsApplicationException;
import com.fast.campus.simplesns.model.Post;
import com.fast.campus.simplesns.model.entity.PostEntity;
import com.fast.campus.simplesns.model.entity.UserEntity;
import com.fast.campus.simplesns.repository.PostEntityRepository;
import com.fast.campus.simplesns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;

    public void create(String title, String body, String userName) {

        postEntityRepository.save(PostEntity.of(title, body, getUserEntityOrThrow(userName)));

    }

    public Post modify(Integer postId, String title, String body, String userName) {
        UserEntity userEntity = getUserEntityOrThrow(userName);
        PostEntity postEntity = getPostEnittyOrThrow(postId);

        postEntity.setTitle(title);
        postEntity.setBody(body);


        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));

    }

    public void delete(Integer postId, String userName) {

        getUserEntityOrThrow(userName);
        getPostEnittyOrThrow(postId);

        postEntityRepository.deleteById(postId);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(Pageable pageable, String userName) {
        UserEntity userEntity = getUserEntityOrThrow(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);

    }

    private PostEntity getPostEnittyOrThrow(Integer postId) {
        return postEntityRepository.findById(postId)
                .orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.POST_NOT_FOUND));
    }

    private UserEntity getUserEntityOrThrow(String userName) {
        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not found", userName)));
    }

}
