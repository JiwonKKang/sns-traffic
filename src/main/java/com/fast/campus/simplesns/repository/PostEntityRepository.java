package com.fast.campus.simplesns.repository;

import com.fast.campus.simplesns.model.entity.PostEntity;
import com.fast.campus.simplesns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    Page<PostEntity> findAllByUser(UserEntity user, Pageable pageable);

}
