package company.friendsdog.dogcommunity.controller;

import company.friendsdog.dogcommunity.dto.PetProfileModifyRequestDTO;
import company.friendsdog.dogcommunity.dto.request.PetProfileRequestDTO;
import company.friendsdog.dogcommunity.entity.Pet;
import company.friendsdog.dogcommunity.dto.page.Page;
import company.friendsdog.dogcommunity.dto.page.PageMaker;
import company.friendsdog.dogcommunity.repository.PetMapper;
import company.friendsdog.dogcommunity.service.BoardService;
import company.friendsdog.dogcommunity.service.PetService;
import company.friendsdog.dogcommunity.util.LoginUtil;
import company.friendsdog.dogcommunity.util.upload.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

import static company.friendsdog.dogcommunity.util.LoginUtil.getCurrentLoginUser;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pet")
@Slf4j
public class PetController {
  private final PetService petService;
  private final PetMapper petMapper;
  private final BoardService boardService;

  // 펫 프로필 카드 만들기 페이지 요청
  @GetMapping("/profile")
  public String petCardMake(
      HttpSession session
  ) {
    log.info("펫 카드 만들기 GET");

    Long userNo = getCurrentLoginUser(session).getUserNo();
    Pet pet = petMapper.userFindPet(userNo);

    if (pet != null) {
      session.setAttribute("p",pet);
      return "pet/profileMod";
    }

    return "pet/profile";
  }


//  @PostMapping("/upload-file")
//
//  public String uploadForm(@RequestParam("thumbnail") MultipartFile file) {
//    log.info("file-name: {}", file.getOriginalFilename());
//    log.info("file-size: {}KB", (double) file.getSize() / 1024);
//    log.info("file-type: {}", file.getContentType());
//
////    String filePath = FileUtil.uploadFile(file, rootPath);
//
//    return "redirect:/upload-form";
//  }


  @Value("${file-upload.root-path}")
  private String rootPath;

  // 펫 카드 정보 저장
  @PostMapping("/profile")
  public String petCardMake(PetProfileRequestDTO dto, HttpSession session) {
    log.info("펫 입력 정보 : {}", dto);
//    log.info("세션 : {}", session);

    // 파일 업로드
    String savePath = null;
    MultipartFile petPhoto = dto.getPetPhoto();
    log.info("프로필사진 이름: {}", petPhoto.getOriginalFilename());

    if (!petPhoto.isEmpty()) {
      // 실제 로컬 스토리지에 파일을 업로드하는 로직
      savePath = FileUtil.uploadFile(petPhoto, rootPath);
    }

    boolean petSave = petService.petCardMake(dto, session, savePath);
//    log.info("펫 저장 : {}", petSave);
    Pet loginPet = petMapper.userFindPet(LoginUtil.getCurrentLoginUser(session).getUserNo());
    session.setAttribute("loginPet",loginPet);

    return "redirect:/pet/modify";
  }

  // 펫 카드 수정창 요청
  @GetMapping("/modify")
  public String modifyData(HttpSession session, Model model) {

    log.info("펫 수정창 들어옴 - 세션 : {}", session);

    Pet foundPet = petService.userFindPet(session);

    log.info("펫 수정 - 기존 펫 정보 : {}", foundPet);
    model.addAttribute("p", foundPet);

    return "pet/profileMod";
  }

  @PostMapping("/modify") //수정 폼안에 있는 데이터를 보내주는애, 수정하기 버튼 눌렀을 때
  public String modifyData(HttpSession session, PetProfileModifyRequestDTO dto, Model model) {

    log.info("mod POST");


    // 로그인한 유저 넘버
    Long userNo = getCurrentLoginUser(session).getUserNo();
    log.info("유저 넘버 : {}", userNo);
//        currUser.getPwd();

    // 유저넘버로 찾은 펫 넘머
    Long petNo = petService.findOne(userNo).getPetNo();
    log.info("펫 넘버 : {}", petNo);

    log.info(dto.getHashtag());

//        String hashTag = petService.getDetail((long) petNo).getHashtag();
    // 세션에서 유저 정보 가져오기

    //Long userNo = currUser.getUserNo();
    // true / false 여부

    // 서비스에 dto(클라이언트에서 수정된값) + 세션에서 받아온 유저넘버 같이 넘겨주기 <=해결

    boolean flag = petService.modify(dto, petNo, rootPath);

    //어디로 이동할지 정하기
    return "redirect:/pet/modify";
  }


  // 우리 동네 강아지 보기
  @GetMapping("/neighbor")
  public String findNeighbor(
      HttpSession session,
      Model model,
      Page page
  ) {
    System.out.println("page = " + page);

    model.addAttribute("noneSidebar", true);

    String addr = LoginUtil.getCurrentLoginUser(session).getAddr();
    List<Pet> foundPet = petService.findNeighbor(addr, page);
    PageMaker maker = new PageMaker(page, petService.petCount(addr));

    model.addAttribute("addr", addr);
    model.addAttribute("petList", foundPet);
    model.addAttribute("p", page);
    model.addAttribute("maker", maker);

    return "map/neighbor";
  }


  @PostMapping("/delete")
  public String delete(Long petNo) {
    log.info("/pet/delete : POST");
    petService.delete(petNo);
    return "redirect:/pet/list";

  }

  //프로필 조회 요청

  @GetMapping("/detail")
  public Pet detail(Long petNo) {

    Pet pet = petService.getDetail(petNo);
    //pet
    return pet;
  }


}

