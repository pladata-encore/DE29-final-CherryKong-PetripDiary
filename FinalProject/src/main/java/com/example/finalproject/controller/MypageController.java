package com.example.finalproject.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.finalproject.config.auth.AuthUserDto;
import com.example.finalproject.model.entity.BoardEntity;
import com.example.finalproject.model.entity.DogEntity;
import com.example.finalproject.model.entity.FindPetEntity;
import com.example.finalproject.model.entity.TravelPlanEntity;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.BoardRepository;
import com.example.finalproject.model.repository.DogRepository;
import com.example.finalproject.model.repository.FindPetRepository;
import com.example.finalproject.model.repository.TravelPlanRepository;
import com.example.finalproject.model.repository.UserRepository;
import com.example.finalproject.service.DogService;
import com.example.finalproject.service.S3Uploader;
import com.example.finalproject.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class MypageController {


    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DogService dogService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;

    @Autowired
    private FindPetRepository findPetRepository;

    @Autowired
    private S3Uploader S3Uploader;


    @GetMapping("/myPage")
    public String myPage(Authentication authentication, Model model) {
        
        AuthUserDto userDetails = (AuthUserDto)authentication.getPrincipal();
        model.addAttribute("userDto", userService.findByUserId(userDetails.getUsername()));
        model.addAttribute("dogDto", dogService.findByDogid(userDetails.getUsername()));
        
        UserEntity user = userRepository.findByUserid(userDetails.getUsername()).get();
        List<BoardEntity> boards = boardRepository.findByUserid(user);
        model.addAttribute("boardList", boards);
        List<FindPetEntity> findpet = findPetRepository.findByUserid(user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
        List<Map<String, Object>> findPetList = findpet.stream()
            .map(pet -> {
                Map<String, Object> petMap = new HashMap<>();
                petMap.put("boardId", pet.getBoardId());
                petMap.put("title", pet.getTitle());
                petMap.put("petName", pet.getPetName());
                petMap.put("formattedTime", pet.getTime().format(formatter));
                return petMap;
            })
            .collect(Collectors.toList());
        model.addAttribute("findPetList", findPetList);
        List<TravelPlanEntity> travelPlans = travelPlanRepository.findTravelPlanByUserid(user);
        model.addAttribute("travelPlanDto", travelPlans);
        return "myPage";
    }

    @PostMapping("/updateNickname")
    public String updateNickname(@RequestParam("nickname") String nickname, RedirectAttributes redirectAttributes) {

        userService.updateUserNickname(nickname);

        return "redirect:/myPage";
    }


    @PostMapping("/registerDogProfile")
    public String registerDogProfile(
        @RequestPart("image") MultipartFile multipartFile,
        @RequestParam("InputName") String dogname,
        @RequestParam("InputBirthday") String inputBirthday,
        @RequestParam("InputWeight") float dogweight,
        RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        Optional<UserEntity> userOptional = userRepository.findByUserid(userid);
        UserEntity user = userOptional.get();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate dogbirth = LocalDate.parse(inputBirthday, formatter);

        DogEntity dogEntity = new DogEntity();
        dogEntity.setDogname(dogname);
        dogEntity.setDogbirth(dogbirth);
        dogEntity.setDogweight(dogweight);
        dogEntity.setUser(user);

        try {
            String uploadImageUrl = S3Uploader.upload(multipartFile,"profile-image");
            dogEntity.setDogphotoUrl(uploadImageUrl);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "파일 업로드 실패");
            return "redirect:/myPage";
        }

        dogRepository.save(dogEntity);

        return "redirect:/myPage";
    }

    @PostMapping("/updateDogProfile")
    public String updateDogProfile(
                                @RequestParam("dogname") String dogname,
                                @RequestParam("dogbirth") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dogbirth, 
                                @RequestParam("dogweight") float dogweight, 
                                @RequestPart("dogphotourl") MultipartFile dogphotourl,
                                RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();


        DogEntity existingDogEntity = dogService.findDogByUserId(userid);
        String existingUrl = existingDogEntity.getDogphotoUrl();

        String uploadedUrl = null;
        try {
            if (!dogphotourl.isEmpty()) {
                uploadedUrl = S3Uploader.upload(dogphotourl,"profile-image");
            } else {
                uploadedUrl = existingUrl;
            }
            dogService.updateDogProfile(userid, dogname, dogbirth, dogweight, uploadedUrl);
        } catch (IOException e) {
            log.error("Dog profile photo upload failed.", e);
            redirectAttributes.addFlashAttribute("message", "프로필 사진 업로드에 실패했습니다.");
            return "redirect:/myPage";
        }

        return "redirect:/myPage";
    }
}