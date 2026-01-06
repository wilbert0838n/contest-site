package contest_site.contest_site.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class UserController {
    @GetMapping("/api/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(Map.of(
                "name", principal.getAttribute("name"),
                "email", principal.getAttribute("email")
        ));
    }
}
