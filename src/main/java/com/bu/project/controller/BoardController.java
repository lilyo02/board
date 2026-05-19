package com.bu.project.controller;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import com.bu.project.entity.Board;
import com.bu.project.service.BoardService;

@Controller
public class BoardController {
	@Autowired
	private BoardService boardService;
	
	@GetMapping("/")
	public String main() {
	    return "redirect:/board/list";
	}
	
	@GetMapping("/board/list")
	public String list(Model m,
	                   @RequestParam(value = "type", required = false) String type,
	                   @RequestParam(value = "keyword", required = false) String keyword,
	                   @PageableDefault(page = 0, size = 5,
	                           sort = "id",
	                           direction = Sort.Direction.DESC)
	                   Pageable pageable) {

	    Page<Board> boardList;

	    if (keyword != null && !keyword.trim().isEmpty()) {
	        boardList = boardService.searchBoardList(type, keyword, pageable);
	    } else {
	        boardList = boardService.getBoardList(pageable);
	    }

	    m.addAttribute("boardList", boardList);
	    m.addAttribute("type", type);
	    m.addAttribute("keyword", keyword);

	    return "list";
	}
	
	@GetMapping("/board/write")
	public String writeForm() {
		return "write";
	}
	
	@Value("${file.upload-dir}")
	private String uploadDir;
	
	@PostMapping("/board/write")
	public String write(Board board, @RequestParam("file") MultipartFile file) throws Exception {
		board.setViewCount(0);
		board.setCreatedAt(LocalDateTime.now());
		
		if(!file.isEmpty()) {
			String originalFileName = file.getOriginalFilename();
			String reFileName = UUID.randomUUID().toString() + "_" + originalFileName;
			
			File saveFolder = new File(uploadDir);
			if(!saveFolder.exists()) {
				saveFolder.mkdirs();
			}
			
			File saveFile = new File(uploadDir + reFileName);
			file.transferTo(saveFile);
			
			board.setOriginalFileName(originalFileName);
			board.setReFileName(reFileName);
			board.setFilePath(uploadDir);
		}
		
		boardService.saveBoard(board);
		
		return "redirect:/board/list";
	}
	
	@GetMapping("/board/view")
	public String view(Long id, Model m) {
		Board board = boardService.getBoardAndIncreaseViewCount(id);
		m.addAttribute("board", board);
		
		return "view";
	}
	
	@GetMapping("/board/update")
	public String updateForm(Long id, Model m) {
		Board board = boardService.getBoard(id);
		m.addAttribute("board", board);
		
		return "update";
	}
	
	@PostMapping("/board/update")
	public String update(Board board) {
		Board oldBoard = boardService.getBoard(board.getId());
		
		board.setCreatedAt(oldBoard.getCreatedAt());
		board.setViewCount(oldBoard.getViewCount());
		
		boardService.saveBoard(board);
		
		return "redirect:/board/view?id=" + board.getId();
	}
	
	@PostMapping("/board/delete")
	public String delete(Long id) {
		boardService.deleteBoard(id);
		return "redirect:/board/list";
	}
	
	@GetMapping("/board/download")
	public ResponseEntity<Resource> download(Long id) throws Exception {

	    Board board = boardService.getBoard(id);

	    UrlResource resource =
	            new UrlResource("file:" + board.getFilePath() + board.getReFileName());

	    String encodedFileName =
	            UriUtils.encode(board.getOriginalFileName(), StandardCharsets.UTF_8);

	    return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                    "attachment; filename=\"" + encodedFileName + "\"")
	            .body(resource);
	}

}
