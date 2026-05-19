package com.bu.project.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bu.project.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	Page<Board> findByTitleContaining(String keyword, Pageable pageable);

	Page<Board> findByContentContaining(String keyword, Pageable pageable);

	Page<Board> findByTitleContainingOrContentContaining(
	        String titleKeyword, String contentKeyword, Pageable pageable);
}
