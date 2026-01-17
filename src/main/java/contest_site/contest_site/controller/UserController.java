package contest_site.contest_site.controller;

import contest_site.contest_site.model.User;
import contest_site.contest_site.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    @GetMapping("/api/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null) return ResponseEntity.status(401).build();

        String email = principal.getAttribute("email");
        User dbUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));
        return ResponseEntity.ok(dbUser);
    }
}
