package com.example.finalproject.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.finalproject.model.dto.BoardDto;
import com.example.finalproject.model.dto.CommentsDto;
import com.example.finalproject.service.BoardService;
import com.example.finalproject.service.CommentsService;
import com.example.finalproject.service.S3Uploader;

@Controller
public class BoardController {

    private final BoardService boardService;
    private final S3Uploader s3Uploader;
    private final CommentsService commentsService;

    public BoardController(BoardService boardService, S3Uploader s3Uploader, CommentsService commentsService) {
        this.boardService = boardService;
        this.s3Uploader = s3Uploader;
        this.commentsService = commentsService;
    }

    @GetMapping("/board")
    public String board(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();
        model.addAttribute("userid", userid);
        return "board";
    }

    @PostMapping("/boardsave")
    public String boardsave(@ModelAttribute BoardDto boardDto, 
                            @RequestParam("dogphoto") MultipartFile dogphoto,
                            @RequestParam(value = "dogphoto2", required = false) MultipartFile dogphoto2,
                            @RequestParam(value = "dogphoto3", required = false) MultipartFile dogphoto3,
                            @RequestParam(value = "dogphoto4", required = false) MultipartFile dogphoto4) {
        try {
            if (!dogphoto.isEmpty()) {
                String photoUrl = s3Uploader.upload(dogphoto,"profile-image");
                boardDto.setDogphotoUrl(photoUrl);
            } else {
                throw new RuntimeException("The first dog photo must be provided.");
            }

            if (dogphoto2 != null && !dogphoto2.isEmpty()) {
                String photoUrl2 = s3Uploader.upload(dogphoto2,"profile-image");
                boardDto.setDogphotoUrl2(photoUrl2);
            }

            if (dogphoto3 != null && !dogphoto3.isEmpty()) {
                String photoUrl3 = s3Uploader.upload(dogphoto3,"profile-image");
                boardDto.setDogphotoUrl3(photoUrl3);
            }

            if (dogphoto4 != null && !dogphoto4.isEmpty()) {
                String photoUrl4 = s3Uploader.upload(dogphoto4,"profile-image");
                boardDto.setDogphotoUrl4(photoUrl4);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        boardService.insertBoard(boardDto);
        return "redirect:/boardlist";
    }

    @GetMapping("/boardlist")
    public String boardList(
        @RequestParam(name="searchType", required = false) String searchType, 
        @RequestParam(name="keyword", required = false) String keyword, 
        @RequestParam(name="sortType", defaultValue = "latest") String sortType, 
        Model model
    ) {
        List<BoardDto> boardPage;
        if (keyword == null || keyword.isEmpty()) {
            boardPage = boardService.getAllBoards(sortType);
        } else {
            boardPage = boardService.searchBoard(searchType, keyword, sortType);
        }
    
        if (keyword == null) {
            keyword = "";
        }
    
        model.addAttribute("boardPage", boardPage);
        model.addAttribute("sortType", sortType);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
    
        model.addAttribute("searchTypeIsTitle", "title".equals(searchType));
        model.addAttribute("searchTypeIsDogname", "dogname".equals(searchType));
        model.addAttribute("searchTypeIsBreed", "breed".equals(searchType));
        model.addAttribute("searchTypeIsUserid", "userid".equals(searchType));
    
        model.addAttribute("sortTypeIsLatest", "latest".equals(sortType));
        model.addAttribute("sortTypeIsLikes", "likes".equals(sortType));
        model.addAttribute("sortTypeIsViews", "views".equals(sortType));
    
        return "boardlist";
    }    

    // @GetMapping("/boardlist")
    // public String boardList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "latest") String sortType, Model model) {
    //     int size = 10000000;
    //     Page<BoardDto> boardPage = boardService.getAllBoards(page, size, sortType);

    //     model.addAttribute("boardPage", boardPage);
    //     model.addAttribute("currentPage", page);
    //     model.addAttribute("nextPage", page + 1);
    //     model.addAttribute("prevPage", page - 1);
    //     model.addAttribute("hasNext", boardPage.hasNext());
    //     model.addAttribute("hasPrev", boardPage.hasPrevious());
    //     model.addAttribute("sortType", sortType);
    //     return "boardlist";
    // }

    @GetMapping("/boarddetail/{boardid}")
    public String getBoardDetail(@PathVariable(name = "boardid") Long boardid, Model model) {
        Optional<BoardDto> boardOptional = boardService.getBoardById(boardid);
        List<CommentsDto> comments = commentsService.getCommentsByBoardId(boardid);
        if (boardOptional.isPresent()) {
            model.addAttribute("boardDetail", boardOptional.get());
            model.addAttribute("comments", comments);
            return "boarddetail";
        } else {
            return "redirect:/boardlist"; 
        }
    }

    @PostMapping("/like/{boardid}")
    public ResponseEntity<String> likeBoard(@PathVariable(name = "boardid") Long boardid, @RequestBody String userid) {
        boolean likeSuccessful = boardService.likeBoard(boardid, userid);
        if (likeSuccessful) {
            return ResponseEntity.ok("true");
        } else {
            return ResponseEntity.ok("false");
        }
    }

    @PostMapping("/increase-view/{boardId}")
    public ResponseEntity<Void> increaseViewCount(@PathVariable(name = "boardId") Long boardId) {
        boardService.increaseViewCount(boardId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/deleteBoard/{boardid}")
    public ResponseEntity<String> deleteBoard(@PathVariable(name = "boardid") Long boardid) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
    
        Optional<BoardDto> boardOptional = boardService.getBoardById(boardid);
        if (boardOptional.isPresent()) {
            BoardDto boardDto = boardOptional.get();
            if (boardDto.getUserid().equals(currentUsername)) {
                try {
                    deleteImagesFromS3(boardDto);
                    boardService.delete(boardid);
                    return ResponseEntity.ok("삭제 성공");
                } catch (Exception e) {
                    return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
                }
            } else {
                return ResponseEntity.status(403).body("권한이 없습니다.");
            }
        } else {
            return ResponseEntity.status(404).body("게시글을 찾을 수 없습니다.");
        }
    }

    @GetMapping("/boardedit/{boardid}")
    public String getBoardEdit(@PathVariable(name = "boardid") Long boardid, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        Optional<BoardDto> boardOptional = boardService.getBoardById(boardid);
        if (boardOptional.isPresent()) {
            BoardDto boardDto = boardOptional.get();
            if (boardDto.getUserid().equals(currentUsername)) {
                model.addAttribute("boardDetail", boardDto);
                return "boardedit";
            } else {
                return "redirect:/boardlist?error=unauthorized";
            }
        } else {
            return "redirect:/boardlist"; 
        }
    }

    // @PostMapping("/boardupdate/{boardid}")
    // public String updateBoard(@PathVariable Long boardid,
    //                           @ModelAttribute BoardDto boardDto,
    //                           @RequestParam("dogphoto") MultipartFile dogphoto,
    //                           @RequestParam(value = "dogphoto2", required = false) MultipartFile dogphoto2,
    //                           @RequestParam(value = "dogphoto3", required = false) MultipartFile dogphoto3,
    //                           @RequestParam(value = "dogphoto4", required = false) MultipartFile dogphoto4) {
    //     Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    //     String currentUsername = authentication.getName();
    
    //     Optional<BoardDto> boardOptional = boardService.getBoardById(boardid);
    //     if (boardOptional.isPresent()) {
    //         BoardDto existingBoard = boardOptional.get();
    //         if (existingBoard.getUserid().equals(currentUsername)) {
    //             try {
    //                 if (!dogphoto.isEmpty()) {
    //                     // S3에 기존 이미지 삭제
    //                     if (existingBoard.getDogphotoUrl() != null) {
    //                         s3Uploader.deleteFile(getFileNameFromUrl(existingBoard.getDogphotoUrl()),"profile-image");
    //                     }
    //                     String photoUrl = s3Uploader.upload(dogphoto,"profile-image");
    //                     boardDto.setDogphotoUrl(photoUrl);
    //                 }
    
    //                 if (dogphoto2 != null && !dogphoto2.isEmpty()) {
    //                     // S3에 기존 이미지 삭제
    //                     if (existingBoard.getDogphotoUrl2() != null) {
    //                         s3Uploader.deleteFile(getFileNameFromUrl(existingBoard.getDogphotoUrl2()),"profile-image");
    //                     }
    //                     String photoUrl2 = s3Uploader.upload(dogphoto2,"profile-image");
    //                     boardDto.setDogphotoUrl2(photoUrl2);
    //                 } else {
    //                     if (existingBoard.getDogphotoUrl2() != null) {
    //                         s3Uploader.deleteFile(getFileNameFromUrl(existingBoard.getDogphotoUrl2()),"profile-image");
    //                     }
    //                     boardDto.setDogphotoUrl2(null);
    //                 }
    
    //                 if (dogphoto3 != null && !dogphoto3.isEmpty()) {
    //                     // S3에 기존 이미지 삭제
    //                     if (existingBoard.getDogphotoUrl3() != null) {
    //                         s3Uploader.deleteFile(getFileNameFromUrl(existingBoard.getDogphotoUrl3()),"profile-image");
    //                     }
    //                     String photoUrl3 = s3Uploader.upload(dogphoto3,"profile-image");
    //                     boardDto.setDogphotoUrl3(photoUrl3);
    //                 } else {
    //                     if (existingBoard.getDogphotoUrl3() != null) {
    //                         s3Uploader.deleteFile(getFileNameFromUrl(existingBoard.getDogphotoUrl3()),"profile-image");
    //                     }
    //                     boardDto.setDogphotoUrl3(null);
    //                 }
    
    //                 if (dogphoto4 != null && !dogphoto4.isEmpty()) {
    //                     // S3에 기존 이미지 삭제
    //                     if (existingBoard.getDogphotoUrl4() != null) {
    //                         s3Uploader.deleteFile(getFileNameFromUrl(existingBoard.getDogphotoUrl4()),"profile-image");
    //                     }
    //                     String photoUrl4 = s3Uploader.upload(dogphoto4,"profile-image");
    //                     boardDto.setDogphotoUrl4(photoUrl4);
    //                 } else {
    //                     if (existingBoard.getDogphotoUrl4() != null) {
    //                         s3Uploader.deleteFile(getFileNameFromUrl(existingBoard.getDogphotoUrl4()),"profile-image");
    //                     }
    //                     boardDto.setDogphotoUrl4(null);
    //                 }
    //             } catch (IOException e) {
    //                 e.printStackTrace();
    //             }
    //             try {
    //                 boardService.updateBoard(boardid, boardDto);
    //             } catch (IOException e) {
    //                 // catch block
    //                 e.printStackTrace();
    //             }
    //             return "redirect:/boarddetail/" + boardid;
    //         } else {
    //             return "redirect:/boardlist?error=unauthorized";
    //         }
    //     } else {
    //         return "redirect:/boardlist";
    //     }
    // }

    @PostMapping("/boardupdate/{boardid}")
    public String updateBoard(@PathVariable(name = "boardid") Long boardid, 
                            @ModelAttribute BoardDto boardDto, 
                            @RequestParam(value = "dogphoto", required = false) MultipartFile dogphoto,
                            @RequestParam(value = "dogphoto2", required = false) MultipartFile dogphoto2,
                            @RequestParam(value = "dogphoto3", required = false) MultipartFile dogphoto3,
                            @RequestParam(value = "dogphoto4", required = false) MultipartFile dogphoto4) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<BoardDto> boardOptional = boardService.getBoardById(boardid);
        if (boardOptional.isPresent()) {
            BoardDto existingBoard = boardOptional.get();
            String p1 = existingBoard.getDogphotoUrl();
            String p2 = existingBoard.getDogphotoUrl2();
            String p3 = existingBoard.getDogphotoUrl3();
            String p4 = existingBoard.getDogphotoUrl4();

            if (existingBoard.getUserid().equals(currentUsername)) {
                try {
                    if (!dogphoto.isEmpty()) {
                        String photoUrl = s3Uploader.upload(dogphoto, "profile-image");
                        boardDto.setDogphotoUrl(photoUrl);
                    }
                    else if (boardDto.getDogphotoUrl() == null) {
                        boardDto.setDogphotoUrl(null);
                        if (p1!=null) {
                            s3Uploader.deleteFile(p1, "profile-image");
                        }
                    }
                    else if (boardDto.getDogphotoUrl().equals(existingBoard.getDogphotoUrl())) {
                        boardDto.setDogphotoUrl(p1);
                    }

                    if (!dogphoto2.isEmpty()) {
                        String photoUrl2 = s3Uploader.upload(dogphoto2, "profile-image");
                        boardDto.setDogphotoUrl2(photoUrl2);
                    } 
                    else if (boardDto.getDogphotoUrl2() == null) { 
                        boardDto.setDogphotoUrl2(null);
                        if (p2!=null) {
                            s3Uploader.deleteFile(p2, "profile-image");
                        }
                    } else if (boardDto.getDogphotoUrl2().equals(existingBoard.getDogphotoUrl2())) {
                        boardDto.setDogphotoUrl2(p2);
                    }

                    
                    if (!dogphoto3.isEmpty()) {
                        String photoUrl3 = s3Uploader.upload(dogphoto3, "profile-image");
                        boardDto.setDogphotoUrl3(photoUrl3);
                    } else if (boardDto.getDogphotoUrl3() == null) {
                        boardDto.setDogphotoUrl3(null);
                        if (p3!=null) {
                            s3Uploader.deleteFile(p3, "profile-image");
                        }
                    } else if (boardDto.getDogphotoUrl3().equals(existingBoard.getDogphotoUrl3())) {
                        boardDto.setDogphotoUrl3(p3);
                    }

                    if (!dogphoto4.isEmpty()) {
                        String photoUrl4 = s3Uploader.upload(dogphoto4, "profile-image");
                        boardDto.setDogphotoUrl4(photoUrl4);
                    } else if (boardDto.getDogphotoUrl4() == null) {
                        boardDto.setDogphotoUrl4(null);
                        if (p4!=null) {
                            s3Uploader.deleteFile(p4, "profile-image");
                        }
                    } else if (boardDto.getDogphotoUrl4().equals(existingBoard.getDogphotoUrl4())) {
                        boardDto.setDogphotoUrl4(p4);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                boardService.updateBoard(boardid, boardDto);
                return "redirect:/boarddetail/" + boardid;
            } else {
                return "redirect:/boardlist?error=unauthorized";
            }
        } else {
            return "redirect:/boardlist"; 
        }
    }

    @PostMapping("/comments")
    public String addComment(@ModelAttribute CommentsDto commentsDto) {
        commentsService.addComment(commentsDto);
        return "redirect:/boarddetail/" + commentsDto.getBoardId();
    }

    @GetMapping("/deleteComment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable(name = "commentId") Long commentId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        boolean deleteSuccessful = commentsService.deleteComment(commentId, currentUsername);
        if (deleteSuccessful) {
            return ResponseEntity.ok("댓글 삭제 성공");
        } else {
            return ResponseEntity.status(403).body("댓글 삭제 실패: 권한이 없거나 댓글을 찾을 수 없습니다.");
        }
    }

    private void deleteImagesFromS3(BoardDto boardDto) {
        try {
            if (boardDto.getDogphotoUrl() != null) {
                s3Uploader.deleteFile(getFileNameFromUrl(boardDto.getDogphotoUrl()),"profile-image");
            }
            if (boardDto.getDogphotoUrl2() != null) {
                s3Uploader.deleteFile(getFileNameFromUrl(boardDto.getDogphotoUrl2()),"profile-image");
            }
            if (boardDto.getDogphotoUrl3() != null) {
                s3Uploader.deleteFile(getFileNameFromUrl(boardDto.getDogphotoUrl3()),"profile-image");
            }
            if (boardDto.getDogphotoUrl4() != null) {
                s3Uploader.deleteFile(getFileNameFromUrl(boardDto.getDogphotoUrl4()),"profile-image");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}
