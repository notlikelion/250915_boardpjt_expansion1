package com.example.boardpjt.controller;

import com.example.boardpjt.model.entity.RefreshToken;
import com.example.boardpjt.model.repository.RefreshTokenRepository;
import com.example.boardpjt.service.UserAccountService;
import com.example.boardpjt.util.CookieUtil;
import com.example.boardpjt.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    // 사용자 계정 관련 비즈니스 로직을 처리하는 서비스
    private final UserAccountService userAccountService;

    // 회원 목록 페이지
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("users", userAccountService.findAllUsers());
        return "admin"; // templates/admin.html
    }

    // 회원 강제 탈퇴
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userAccountService.deleteUser(id);
        return "redirect:/admin";
    }
}