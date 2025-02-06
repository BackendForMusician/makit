package com.example.makit.feedUpload.Repository;

import com.example.makit.feedUpload.Entity.FeedEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<FeedEntity, Long> {
}
