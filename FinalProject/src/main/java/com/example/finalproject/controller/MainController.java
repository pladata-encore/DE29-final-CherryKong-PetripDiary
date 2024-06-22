package com.example.finalproject.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.service.S3Uploader;
import com.example.finalproject.service.UserService;

@Controller
public class MainController {

    @Autowired
    private UserService userService;
    @Autowired
    private S3Uploader S3Uploader;


    @GetMapping("/index")
    public String index(Authentication authentication, Model model) {
        if (authentication != null) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            model.addAttribute("username", userDetails.getUsername());
        }
        return "index";
    }

    @GetMapping("/loginPage")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "비밀번호가 틀렸습니다.");
        }
        return "loginPage";
    }
    
    @GetMapping("/joinPage")
    public String joinPage() {
        return "joinPage";
    }

    @PostMapping("/join")
    public String join(@ModelAttribute UserEntity userEntity) {
        userService.joinUserDto(userEntity);
        return "redirect:/loginPage";
    }

    @GetMapping("/recTravelPlan")
    public String recTravelPlan(@RequestParam(value = "result") String places, Model model) {
        //얘 고치자
        model.addAttribute("places", places);
        return "recTravelPlan";
    }

    @GetMapping("/travelPlan")
    public String travelPlan() {
        return "travelPlan";
    }

    @GetMapping("/travelInform")
    public String travelInform() {
        return "travelInform";
    }

    @GetMapping("/data")
    public ResponseEntity<String> getData() {
        try {
            ClassPathResource resource = new ClassPathResource("static/data/data_sample.json");
            Path path = resource.getFile().toPath();
            String content = Files.readString(path);
            return new ResponseEntity<>(content, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/putPlaceImage")
    public String putPlaceImage(@RequestPart("image") MultipartFile multipartFile, RedirectAttributes redirectAttributes) {

        try {
            S3Uploader.upload(multipartFile,"rec-place");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "파일 업로드 실패");
            return "redirect:/index";
        }
        return "redirect:/index";
        }

    }



