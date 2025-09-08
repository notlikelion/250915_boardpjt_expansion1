package com.example.boardpjt.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

public class CookieUtil {
    public static void createCookie(HttpServletResponse response, String key, String value, int maxAge) {
//        ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
        ResponseCookie cookie = ResponseCookie.from(key, value)
                .httpOnly(true) // XSS 공격 방지 (JavaScript에서 접근 불가)
                .path("/") // 쿠키가 유효한 경로 (전체 도메인)
//                .maxAge(3600) // 쿠키 유효기간 (3600초 = 1시간) 주의: 초 단위임
                .maxAge(maxAge)
                .build();

        // HTTP 응답 헤더에 "Set-Cookie" 추가하여 클라이언트에 쿠키 전송
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // findCookie

    // TODO: deleteCookie
}
