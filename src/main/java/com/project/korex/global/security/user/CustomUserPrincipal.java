package com.project.korex.global.security.user;

import com.project.korex.user.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomUserPrincipal implements UserDetails{

    private final Users user;
    private final Map<String, Object> attributes;
    private final String attributeKey;
    private final String registrationId;

    //일반 Login 생성
    public CustomUserPrincipal(Users user) {
        this.user = user;
        this.attributes = null;
        this.attributeKey = null;
        this.registrationId = null;
    }

    //공통
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user == null) return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleName()));
    }

    //loginId 반환
    public String getName() {
        if (registrationId == null) {
            return user.getLoginId();
        }
        switch (registrationId) {
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get(attributeKey);
                return (String) response.get("id");
            case "google":
                return (String) attributes.get(attributeKey);
            case "kakao":
                return attributes.get(attributeKey).toString();
            default:
                return null;
        }
    }

    //name 반환
    @Override
    public String getUsername() {
        if (registrationId == null) {
            return user.getName();
        }
        switch (registrationId) {
            case "google":
                return (String) attributes.get("name");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>)  attributes.get("properties"); //todo 비즈니스 정보 통과가 된다면 kakao_account로 변경
                return (String) kakaoAccount.get("nickname"); // 추후 name변경
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get(attributeKey);
                return (String) response.get("name");
            default:
                return null;
        }
    }

    public String getEmail() {
        switch (registrationId) {
            case "google":
                return (String) attributes.get("email");
            case "kakao":
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                return (String) kakaoAccount.get("email");
            case "naver":
                Map<String, Object> response = (Map<String, Object>) attributes.get(attributeKey);
                return (String) response.get("email");
            default:
                return null;
        }
    }

    //UserDetails
    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
