package com.bu.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bu.project.entity.Board;
import com.bu.project.repository.BoardRepository;

@Service
public class BoardService {
	@Autowired
	private BoardRepository boardRepo;
	
	public List<Board> getBoardList() {
		return boardRepo.findAll();
	}
	
	public void saveBoard(Board board) {
		boardRepo.save(board);
	}
	
	public Board getBoard(Long id) {
		return boardRepo.findById(id).get();
	}
	
	public void deleteBoard(Long id) {
		boardRepo.deleteById(id);
	}
	
	public Board getBoardAndIncreaseViewCount(Long id) {
		Board board = boardRepo.findById(id).get();
		
		board.setViewCount(board.getViewCount() + 1);
		boardRepo.save(board);
		
		return board;
	}
	
	public Page<Board> getBoardList(Pageable pageable) {
		return boardRepo.findAll(pageable);
	}
	
	public Board getFile(Long id) {
	    return boardRepo.findById(id).get();
	}
	
	public Page<Board> searchBoardList(String type, String keyword, Pageable pageable) {

	    if ("title".equals(type)) {
	        return boardRepo.findByTitleContaining(keyword, pageable);
	    }

	    if ("content".equals(type)) {
	        return boardRepo.findByContentContaining(keyword, pageable);
	    }

	    return boardRepo.findByTitleContainingOrContentContaining(
	            keyword, keyword, pageable);
	}
}
