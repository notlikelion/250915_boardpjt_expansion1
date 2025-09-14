package com.example.boardpjt.controller;

import com.example.boardpjt.model.dto.PostDTO;
import com.example.boardpjt.model.entity.Post;
import com.example.boardpjt.model.entity.UserAccount;
import com.example.boardpjt.service.PostService;
import com.example.boardpjt.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserAccountService userAccountService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(required = false) String keyword) {
        if (!StringUtils.hasText(keyword)) {
            keyword = "";
        }
        Page<Post> postPage = postService.findWithPagingAndSearch(keyword, page - 1);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", postPage.getTotalPages());
        model.addAttribute("posts",
                postPage.getContent()
                        .stream().map(p -> new PostDTO.Response(
                                p.getId(),
                                p.getTitle(),
                                p.getContent(),
                                p.getAuthor().getUsername(),
                                p.getCreatedAt()
                        )).toList() // Stream을 List로 변환
        );
        model.addAttribute("keyword", keyword);
        return "post/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         Authentication authentication) {
        UserAccount userAccount = userAccountService.findByUsername(authentication.getName());
        Post post = postService.findById(id);
        boolean followCheck = post.getAuthor().getFollowers().contains(userAccount);

        model.addAttribute("followCheck", followCheck);
        model.addAttribute("post", post);
        return "post/detail";
    }

    @GetMapping("/new")
    public String createForm(Model model, Authentication authentication) {
        PostDTO.Request dto = new PostDTO.Request();
        dto.setUsername(authentication.getName());
        model.addAttribute("post", dto);
        return "post/form";
    }

    @PostMapping("/new")
    public String create(@Valid @ModelAttribute("post") PostDTO.Request dto,
                         BindingResult bindingResult,
                         Authentication authentication) {
        if (bindingResult.hasErrors()) {
            return "post/form";
        }
        dto.setUsername(authentication.getName());
        postService.createPost(dto);
        return "redirect:/posts";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication) {
        try {
            Post post = postService.findById(id);
            String postUsername = post.getAuthor().getUsername();
            if (!postUsername.equals(authentication.getName())) {
                throw new SecurityException("삭제 권한이 없습니다.");
            }
            postService.deleteById(id);
        } catch (Exception e) {
            System.err.println("게시물 삭제 실패: " + e.getMessage());
        }
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Authentication authentication) {
        Post post = postService.findById(id);
        if (!post.getAuthor().getUsername().equals(authentication.getName())) {
            return "redirect:/posts/" + id;
        }

        PostDTO.Request dto = new PostDTO.Request();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        model.addAttribute("post", dto);
        model.addAttribute("postId", id);
        return "post/edit";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id,
                       @Valid @ModelAttribute("post") PostDTO.Request dto,
                       BindingResult bindingResult,
                       Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id);
            return "post/edit";
        }
        dto.setUsername(authentication.getName());
        try {
            postService.updatePost(id, dto);
        } catch (Exception e) {
            return "redirect:/posts/" + id + "/edit";
        }
        return "redirect:/posts";
    }
}