package com.fast.campus.simplesns.repository;

import com.fast.campus.simplesns.model.Post;
import com.fast.campus.simplesns.model.entity.AlarmEntity;
import com.fast.campus.simplesns.model.entity.LikeEntity;
import com.fast.campus.simplesns.model.entity.PostEntity;
import com.fast.campus.simplesns.model.entity.UserEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


public interface AlarmEntityRepository extends JpaRepository<AlarmEntity, Integer> {

    Page<AlarmEntity> findAllByUserId(Integer userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE AlarmEntity entity SET entity.removedAt = NOW() WHERE entity.post = :post")
    void deleteAllByPost(@Param("post") PostEntity post);

}
