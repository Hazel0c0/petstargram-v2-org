package company.friendsdog.dogcommunity.controller;

import company.friendsdog.dogcommunity.dto.request.JoinRequestDTO;
import company.friendsdog.dogcommunity.dto.request.LoginRequestDTO;
import company.friendsdog.dogcommunity.service.LoginResult;
import company.friendsdog.dogcommunity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static company.friendsdog.dogcommunity.service.LoginResult.*;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

  private final UserService userService;

  @GetMapping("/main")
  public String Main(){
    log.info("GET");
    return "/main/main";
  }

  // 회원가입
  // 회원가입창
  @GetMapping("/join")
  public String userJoin(){
    log.info("회원가입 GET");
    return "/login/sign-up";
  }

  // 회원가입 처리 요청
  @PostMapping("/join")
  public String userJoin(JoinRequestDTO dto){
    log.info("회원가입 POST : {}",dto);
    boolean flag = userService.userJoin(dto);

    // 회원가입 끝나면 main 창으로
    return "redirect:/user/main";
  }

  // 아이디,이메일,폰번호 중복 검사
  @GetMapping("/check")
  @ResponseBody   // 비동기 요청처리
  public ResponseEntity<?> joinCheckValue(String type, String keyword) {
    log.info("중복검사 : type={}&keyword={} GET!", type, keyword);
    boolean flag = userService.joinCheckValue(type, keyword);
    return ResponseEntity.ok().body(flag); // -> 중복
  }


  // ==============로그인 화면 요청======================================
  @GetMapping("/login")
  public String login(HttpServletRequest request) {
    log.info("로그인 GET");

    return "/login/login";
  }

  // 로그인 검증 요청
  @PostMapping("/login")
  public String login(LoginRequestDTO dto
      , HttpServletRequest request
      , RedirectAttributes ra
  ){
    LoginResult loginResult = userService.loginAuthenticate(dto);

    if (loginResult== SUCCESS){
      log.info("로그인 성공 : {}",loginResult);
      // 세션에 로그인 정보 저장하기
      userService.maintainLoginState(
              request.getSession(), dto.getUserNo());
      return "redirect:/user/main";
    }

    log.info("로그인 실패");
    // 일회용 데이터
    ra.addFlashAttribute("msg", loginResult);
    return "redirect:/user/login";
  }

  // 로그아웃 요청 처리
  @GetMapping("/logout")
  public String logout(HttpSession session){

    return "redirect:/";
  }
}
