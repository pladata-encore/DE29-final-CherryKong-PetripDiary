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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.finalproject.model.dto.FindPetDto;
import com.example.finalproject.service.CommentsService;
import com.example.finalproject.service.FindPetService;
import com.example.finalproject.service.S3Uploader;

@Controller
public class FindPetController {
    
    private final S3Uploader s3Uploader;
    private final FindPetService findPetService;

    public FindPetController(FindPetService findPetService, S3Uploader s3Uploader, CommentsService commentsService) {
        this.s3Uploader = s3Uploader;
        this.findPetService = findPetService;
    }

    @GetMapping("/findPet")
    public String findPet(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();
        model.addAttribute("userid", userid);
        return "findPet";
    }
    
    @PostMapping("/findPetSave")
    public String findPetSave(@ModelAttribute FindPetDto findPetDto, 
                            @RequestParam("dogphoto") MultipartFile dogphoto,
                            @RequestParam(value = "dogphoto2", required = false) MultipartFile dogphoto2,
                            @RequestParam(value = "dogphoto3", required = false) MultipartFile dogphoto3,
                            @RequestParam(value = "dogphoto4", required = false) MultipartFile dogphoto4) {
        try {
            if (!dogphoto.isEmpty()) {
                String photoUrl = s3Uploader.upload(dogphoto,"profile-image");
                findPetDto.setDogphotoUrl(photoUrl);
            } else {
                throw new RuntimeException("The first dog photo must be provided.");
            }

            if (dogphoto2 != null && !dogphoto2.isEmpty()) {
                String photoUrl2 = s3Uploader.upload(dogphoto2,"profile-image");
                findPetDto.setDogphotoUrl2(photoUrl2);
            }

            if (dogphoto3 != null && !dogphoto3.isEmpty()) {
                String photoUrl3 = s3Uploader.upload(dogphoto3,"profile-image");
                findPetDto.setDogphotoUrl3(photoUrl3);
            }

            if (dogphoto4 != null && !dogphoto4.isEmpty()) {
                String photoUrl4 = s3Uploader.upload(dogphoto4,"profile-image");
                findPetDto.setDogphotoUrl4(photoUrl4);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        findPetService.insert(findPetDto);
        return "redirect:/findPetList";
    }

    @GetMapping("/findPetList")
    public String findPetList(
        @RequestParam(name="searchType", required = false) String searchType, 
        @RequestParam(name="keyword", required = false) String keyword, 
        Model model) {
        List<FindPetDto> findPetList;
        if (keyword == null || keyword.isEmpty()) {
            findPetList = findPetService.getAllFindPet();
        } else {
            findPetList = findPetService.searchFindPet(searchType, keyword);
        }

        if (keyword == null) {
            keyword = "";
        }

        model.addAttribute("findPetList", findPetList);
        model.addAttribute("searchType", searchType);
        model.addAttribute("keyword", keyword);
        
        model.addAttribute("searchTypeIsTitle", "title".equals(searchType));
        model.addAttribute("searchTypeIsPetName", "petName".equals(searchType));
        model.addAttribute("searchTypeIsPetBreed", "petBreed".equals(searchType));
        model.addAttribute("searchTypeIsLostPlace", "lostPlace".equals(searchType));
        model.addAttribute("searchTypeIsUserid", "userid".equals(searchType));

        return "findPetList";
    }

    @GetMapping("/findPetDetail/{boardId}")
    public String findPetDetail(@PathVariable(name = "boardId") Long boardId, Model model) {
        Optional<FindPetDto> findPetOptional = findPetService.getFindPetById(boardId);
        if (findPetOptional.isPresent()){
            model.addAttribute("findPetDetail", findPetOptional.get());
            return "findPetDetail";
        } else {
            return "redirect:/findPetList";
        }
    }

    @GetMapping("/findPetDelete/{boardId}") 
    public ResponseEntity<String> findPetDelete(@PathVariable(name = "boardId") Long boardId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        
        Optional<FindPetDto> findPetOptional = findPetService.getFindPetById(boardId);
        if (findPetOptional.isPresent()) {
            FindPetDto findPetDto = findPetOptional.get();
            if (findPetDto.getUserid().equals(currentUsername)) {
                try {
                    findPetService.delete(boardId);
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

}
