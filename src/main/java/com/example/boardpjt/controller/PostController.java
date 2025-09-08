package com.example.boardpjt.controller;

import com.example.boardpjt.model.dto.PostDTO;
import com.example.boardpjt.model.entity.Post;
import com.example.boardpjt.service.PostService;
import com.example.boardpjt.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller // 스캔
@RequiredArgsConstructor // 의존성
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    // 게시물 목록
    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts",
                postService.findAll()
                        .stream().map(p -> new PostDTO.Response(
                                p.getId(),
                                p.getTitle(),
                                p.getContent(),
                                p.getAuthor().getUsername(),
                                p.getCreatedAt().toString()
                        )));
        return "post/list"; // templates/post/list.html
    }
    // 개별 게시물
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Long id,
            Model model) {
        // 각각 개별이니까... 1개.
        model.addAttribute("post",postService.findById(id));
        // 구현하는 방법은 여러가지 -> 데이터를 불러오는 건 상관X.
        // '내 게시물'임을 어떻게 보여줄 것이냐
        // 1. controller에서 처리를 해서 authentication 등 비교 -> isMyPost...
        // 2. #authencation.name -> entity, dto -> username.
        return "post/detail"; // templates/post/list.html
    }
    // 게시물 작성
    @GetMapping("/new")
    public String createForm(
            Model model, Authentication authentication) {
        PostDTO.Request dto = new PostDTO.Request();
        dto.setUsername(authentication.getName());
        model.addAttribute("post", dto);
        return "post/form"; // templates/post/list.html
    }
    // POST 처리...
    @PostMapping("/new")
    public String create(@ModelAttribute PostDTO.Request dto, Authentication authentication) {
        // dto? -> username
        // 불일치할 때 에러를?
        dto.setUsername(authentication.getName());
        postService.createPost(dto);
        return "redirect:/posts";
    }

    // 삭제
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        // 나 자신만 삭제가 가능
        try {
            // 1번 : 삭제하려고 하는 사람과 주인이 다를 때
            Post post = postService.findById(id);
            String postUsername = post.getAuthor().getUsername();
            if (!postUsername.equals(authentication.getName())) {
                // 지금 자격증명의 유저(이름)와 게시물의 유저가 다르다.
                throw new SecurityException("삭제 권한 없음");
            }
            // 2번 : 없는 걸 삭제하려고 할 때 (2번은 Service에서 throw를 하게...)
            postService.deleteById(id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return "redirect:/posts"; // 문제 발생 시 목록으로 보냄
    }
}