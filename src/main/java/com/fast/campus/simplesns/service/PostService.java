package com.fast.campus.simplesns.service;

import com.fast.campus.simplesns.exception.ErrorCode;
import com.fast.campus.simplesns.exception.SimpleSnsApplicationException;
import com.fast.campus.simplesns.model.AlarmArgs;
import com.fast.campus.simplesns.model.AlarmType;
import com.fast.campus.simplesns.model.Comment;
import com.fast.campus.simplesns.model.Post;
import com.fast.campus.simplesns.model.entity.*;
import com.fast.campus.simplesns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final AlarmService alarmService;

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

        UserEntity userEntity = getUserEntityOrThrow(userName);
        PostEntity postEntity = getPostEnittyOrThrow(postId);

        if (!postEntity.getUser().equals(userEntity)) {
            throw new SimpleSnsApplicationException(ErrorCode.INVALID_PERMISSION);
        }

        alarmEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    public Page<Post> my(Pageable pageable, String userName) {
        UserEntity userEntity = getUserEntityOrThrow(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);

    }

    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("postId is %d", postId)));
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("userName is %s", userName)));

        commentEntityRepository.save(CommentEntity.of(comment, postEntity, userEntity));

        // create alarm
        AlarmEntity alarm = alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postId)));
        alarmService.send(postEntity.getUser().getId(), alarm.getId());
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("postId is %d", postId)));
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("postId is %d", postId)));
        UserEntity userEntity = userEntityRepository.findByUserName(userName)
                .orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("userName is %s", userName)));

        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SimpleSnsApplicationException(ErrorCode.ALREADY_LIKED_POST, String.format("userName %s already like the post %s", userName, postId));
        });

        likeEntityRepository.save(LikeEntity.of(postEntity, userEntity));

        // create alarm
        AlarmEntity alarm = alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postId)));
        alarmService.send(postEntity.getUser().getId(), alarm.getId());

    }

    public Long getLikeCount(Integer postId) {
        PostEntity postEntity = postEntityRepository.findById(postId)
                .orElseThrow(() -> new SimpleSnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("postId is %d", postId)));
        return likeEntityRepository.countByPost(postEntity);
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
