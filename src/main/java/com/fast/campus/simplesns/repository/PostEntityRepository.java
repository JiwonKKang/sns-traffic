package com.fast.campus.simplesns.repository;

import com.fast.campus.simplesns.model.entity.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {
}
