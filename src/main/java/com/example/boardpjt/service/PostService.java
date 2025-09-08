package com.example.boardpjt.service;

import com.example.boardpjt.model.dto.PostDTO;
import com.example.boardpjt.model.entity.Post;
import com.example.boardpjt.model.entity.UserAccount;
import com.example.boardpjt.model.repository.PostRepository;
import com.example.boardpjt.model.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserAccountRepository userAccountRepository;

    // 1. create
    @Transactional
    public Post createPost(PostDTO.Request dto) {
        UserAccount userAccount = userAccountRepository
                .findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        Post post = new Post();
        post.setAuthor(userAccount);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        return postRepository.save(post);
    }
    // 2-1. findAll
    @Transactional(readOnly = true)
    public List<Post> findAll() {
        return postRepository.findAll();
    }
    // 2-2. findOne (byId...)
    @Transactional(readOnly = true)
    public Post findById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시물 없음"));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!postRepository.existsById(id)) {
            throw new IllegalArgumentException("게시물 없음");
        }
        postRepository.deleteById(id);
    }

    // -------------------------
    // 3. update
    // 4. delete v

    // 페이징, 검색 쿼리 -> 내일 오전
    // 내일 오후 -> 댓글. (추천/좋아요). 팔로우.
    // 남은 시간. 질답.
}
