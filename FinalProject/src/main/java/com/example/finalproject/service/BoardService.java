package com.example.finalproject.service;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.finalproject.model.dto.BoardDto;
import com.example.finalproject.model.entity.BoardEntity;
import com.example.finalproject.model.entity.UserEntity;
import com.example.finalproject.model.repository.BoardRepository;
import com.example.finalproject.model.repository.UserRepository;



@Service
public class BoardService {
    
    @Autowired
    private final S3Uploader s3Uploader;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    public BoardService(BoardRepository boardRepository){
        this.s3Uploader = null;
        this.boardRepository = boardRepository;
    }

    public void insertBoard(BoardDto boardDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userid = authentication.getName();

        Optional<UserEntity> userOptional = userRepository.findByUserid(userid);
        UserEntity user = userOptional.get();

        LocalDateTime currentTime = LocalDateTime.now();
        
        BoardEntity boardEntity = new BoardEntity();
        boardEntity.setBoardid(boardDto.getBoardid());
        boardEntity.setTitle(boardDto.getTitle());
        boardEntity.setText(boardDto.getText());
        boardEntity.setUserid(user);
        boardEntity.setNickname(user.getNickname());
        boardEntity.setDogname(boardDto.getDogname());
        boardEntity.setBreed(boardDto.getBreed());
        boardEntity.setDogbirth(boardDto.getDogbirth());
        boardEntity.setDogweight(boardDto.getDogweight());;
        boardEntity.setDogphotoUrl(boardDto.getDogphotoUrl());
        boardEntity.setDogphotoUrl2(boardDto.getDogphotoUrl2());
        boardEntity.setDogphotoUrl3(boardDto.getDogphotoUrl3());
        boardEntity.setDogphotoUrl4(boardDto.getDogphotoUrl4());
        boardEntity.setTime(currentTime);
        boardRepository.save(boardEntity); 

    }

    // public Page<BoardDto> getAllBoards(int page, int size, String sortType) {
    //     Pageable pageable;
    
    //     switch (sortType) {
    //         case "likes":
    //             pageable = PageRequest.of(page, size, Sort.by("likes").descending());
    //             break;
    //         case "views":
    //             pageable = PageRequest.of(page, size, Sort.by("views").descending());
    //             break;
    //         case "latest":
    //         default:
    //             pageable = PageRequest.of(page, size, Sort.by("boardid").descending());
    //             break;
    //     }
    
    //     Page<BoardEntity> boardEntities = boardRepository.findAll(pageable);
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    
    //     return boardEntities.map(entity -> {
    //         BoardDto dto = new BoardDto();
    //         dto.setBoardid(entity.getBoardid());
    //         dto.setTitle(entity.getTitle());
    //         dto.setText(entity.getText());
    //         dto.setUserid(entity.getUserid().getUserid());
    //         dto.setNickname(entity.getNickname());
    //         dto.setDogname(entity.getDogname());
    //         dto.setBreed(entity.getBreed());
    //         dto.setDogbirth(entity.getDogbirth());
    //         dto.setDogweight(entity.getDogweight());
    //         dto.setDogphotoUrl(entity.getDogphotoUrl());
    //         String formattedTime = entity.getTime().format(formatter).toString();
    //         dto.setTime(formattedTime);
    //         dto.setLikes(entity.getLikes());
    //         dto.setViews(entity.getViews());
    //         return dto;
    //     });
    // }

    public List<BoardDto> getAllBoards(String sortType) {
        Sort sort;
        switch (sortType) {
            case "likes":
                sort = Sort.by(Sort.Direction.DESC, "likes");
                break;
            case "views":
                sort = Sort.by(Sort.Direction.DESC, "views");
                break;
            case "latest":
            default:
                sort = Sort.by(Sort.Direction.DESC, "boardid");
                break;
        }

        List<BoardEntity> boardEntities = boardRepository.findAll(sort);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        return boardEntities.stream().map(entity -> {
            BoardDto dto = new BoardDto();
            dto.setBoardid(entity.getBoardid());
            dto.setTitle(entity.getTitle());
            dto.setText(entity.getText());
            dto.setUserid(entity.getUserid().getUserid());
            dto.setNickname(entity.getNickname());
            dto.setDogname(entity.getDogname());
            dto.setBreed(entity.getBreed());
            dto.setDogbirth(entity.getDogbirth());
            dto.setDogweight(entity.getDogweight());
            dto.setDogphotoUrl(entity.getDogphotoUrl());
            dto.setDogphotoUrl2(entity.getDogphotoUrl2());
            dto.setDogphotoUrl3(entity.getDogphotoUrl3());
            dto.setDogphotoUrl4(entity.getDogphotoUrl4());
            String formattedTime = entity.getTime().format(formatter).toString();
            dto.setTime(formattedTime);
            dto.setLikes(entity.getLikes());
            dto.setViews(entity.getViews());
            return dto;
        }).collect(Collectors.toList());
    }
    
    public List<BoardDto> searchBoard(String searchType, String keyword, String sortType) {
        Specification<BoardEntity> spec = Specification.where(null);
    
        if (StringUtils.hasText(keyword)) {
            switch (searchType) {
                case "title":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("title"), "%" + keyword + "%"));
                    break;
                case "userid":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("userid").get("userid"), "%" + keyword + "%"));
                    break;
                case "dogname":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("dogname"), "%" + keyword + "%"));
                    break;
                case "breed":
                    spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("breed"), "%" + keyword + "%"));
                    break;
            }
        }
    
        Sort sort = Sort.by(Sort.Direction.DESC, "boardid");
        if ("latest".equals(sortType)) {
            sort = Sort.by(Sort.Direction.DESC, "boardid");
        } else if ("likes".equals(sortType)) {
            sort = Sort.by(Sort.Direction.DESC, "likes");
        } else if ("views".equals(sortType)) {
            sort = Sort.by(Sort.Direction.DESC, "views");
        }
    
        List<BoardEntity> boardEntities = boardRepository.findAll(spec, sort);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    
        return boardEntities.stream().map(entity -> {
            BoardDto dto = new BoardDto();
            dto.setBoardid(entity.getBoardid());
            dto.setTitle(entity.getTitle());
            dto.setText(entity.getText());
            dto.setUserid(entity.getUserid().getUserid());
            dto.setNickname(entity.getNickname());
            dto.setDogname(entity.getDogname());
            dto.setBreed(entity.getBreed());
            dto.setDogbirth(entity.getDogbirth());
            dto.setDogweight(entity.getDogweight());
            dto.setDogphotoUrl(entity.getDogphotoUrl());
            dto.setDogphotoUrl2(entity.getDogphotoUrl2());
            dto.setDogphotoUrl3(entity.getDogphotoUrl3());
            dto.setDogphotoUrl4(entity.getDogphotoUrl4());
            String formattedTime = entity.getTime().format(formatter).toString();
            dto.setTime(formattedTime);
            dto.setLikes(entity.getLikes());
            dto.setViews(entity.getViews());
            return dto;
        }).collect(Collectors.toList());
    }
    

    public Optional<BoardDto> getBoardById(Long boardid) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(boardid);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");

        if (boardEntityOptional.isPresent()) {
            BoardEntity entity = boardEntityOptional.get();
            BoardDto dto = new BoardDto();
            dto.setBoardid(entity.getBoardid());
            dto.setTitle(entity.getTitle());
            dto.setText(entity.getText());
            dto.setUserid(entity.getUserid().getUserid());
            dto.setNickname(entity.getUserid().getNickname());
            dto.setDogname(entity.getDogname());
            dto.setBreed(entity.getBreed());
            dto.setDogbirth(entity.getDogbirth());
            dto.setDogweight(entity.getDogweight());
            dto.setDogphotoUrl(entity.getDogphotoUrl());
            dto.setDogphotoUrl2(entity.getDogphotoUrl2());
            dto.setDogphotoUrl3(entity.getDogphotoUrl3());
            dto.setDogphotoUrl4(entity.getDogphotoUrl4());
            String formattedTime = entity.getTime().format(formatter).toString();
            dto.setTime(formattedTime);
            dto.setLikes(entity.getLikes());
            dto.setViews(entity.getViews());
            
            return Optional.of(dto);
        } else {
            return Optional.empty();
        }
    }

    public boolean likeBoard(Long boardId, String userId) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(boardId);
    
        if (boardEntityOptional.isPresent()) {
            BoardEntity boardEntity = boardEntityOptional.get();
            if (boardEntity.getLikedUsers() == null) {
                boardEntity.setLikedUsers(new HashSet<>());
            }
            if (!boardEntity.getLikedUsers().contains(userId)) { // 사용자가 게시판에 이미 좋아요를 눌렀는지 확인합니다.
                boardEntity.getLikedUsers().add(userId);
                boardEntity.setLikes(boardEntity.getLikes() + 1);
                boardRepository.save(boardEntity);
                return true; // 좋아요 작업이 성공적으로 수행되었습니다.
            } else {
                System.out.println("이미 좋아요를 누른 사용자입니다.");
                return false; // 이미 좋아요를 누른 사용자입니다.
            }
        }
        return false; // 게시글을 찾을 수 없습니다.
    }
    

    public void increaseViewCount(Long boardId) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(boardId);
        if (boardEntityOptional.isPresent()) {
            BoardEntity boardEntity = boardEntityOptional.get();
            boardEntity.setViews(boardEntity.getViews() + 1); // 조회수 증가
            boardRepository.save(boardEntity);
        }
    }

    public void delete(Long boardid) throws IOException {
        Optional<BoardEntity> entityOptional = boardRepository.findById(boardid);
        if (entityOptional.isPresent()) {
            BoardEntity boardEntity = entityOptional.get();
            deleteImagesFromS3(boardEntity); // Delete associated S3 files
            boardRepository.delete(boardEntity);
        } else {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }

    private void deleteImagesFromS3(BoardEntity boardEntity) throws IOException {
        if (boardEntity.getDogphotoUrl() != null) {
            s3Uploader.deleteFile(getFileNameFromUrl(boardEntity.getDogphotoUrl()),"profile-image");
        }
        if (boardEntity.getDogphotoUrl2() != null) {
            s3Uploader.deleteFile(getFileNameFromUrl(boardEntity.getDogphotoUrl2()),"profile-image");
        }
        if (boardEntity.getDogphotoUrl3() != null) {
            s3Uploader.deleteFile(getFileNameFromUrl(boardEntity.getDogphotoUrl3()),"profile-image");
        }
        if (boardEntity.getDogphotoUrl4() != null) {
            s3Uploader.deleteFile(getFileNameFromUrl(boardEntity.getDogphotoUrl4()),"profile-image");
        }
    }

    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public void updateBoard(Long boardid, BoardDto boardDto) {
        Optional<BoardEntity> boardEntityOptional = boardRepository.findById(boardid);
        if (boardEntityOptional.isPresent()) {
            BoardEntity boardEntity = boardEntityOptional.get();
    
            boardEntity.setTitle(boardDto.getTitle());
            boardEntity.setText(boardDto.getText());
            boardEntity.setDogname(boardDto.getDogname());
            boardEntity.setBreed(boardDto.getBreed());
            boardEntity.setDogbirth(boardDto.getDogbirth());
            boardEntity.setDogweight(boardDto.getDogweight());
    
            if (boardDto.getDogphotoUrl() != null) {
                boardEntity.setDogphotoUrl(boardDto.getDogphotoUrl());
            }
            if (boardDto.getDogphotoUrl2() != null) {
                boardEntity.setDogphotoUrl2(boardDto.getDogphotoUrl2());
            } else {
                boardEntity.setDogphotoUrl2(null);
            }
            if (boardDto.getDogphotoUrl3() != null) {
                boardEntity.setDogphotoUrl3(boardDto.getDogphotoUrl3());
            } else {
                boardEntity.setDogphotoUrl3(null);
            }
            if (boardDto.getDogphotoUrl4() != null) {
                boardEntity.setDogphotoUrl4(boardDto.getDogphotoUrl4());
            } else {
                boardEntity.setDogphotoUrl4(null);
            }
    
            boardRepository.save(boardEntity);
        } else {
            throw new IllegalArgumentException("게시글을 찾을 수 없습니다.");
        }
    }
    
    

}
