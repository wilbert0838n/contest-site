package contest_site.contest_site.security;

import contest_site.contest_site.model.User;
import contest_site.contest_site.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");

        userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(name.replaceAll(" ", "_").toLowerCase());
            newUser.setPassword(null); //no pass management required
            return userRepository.save(newUser);
        });

        return oauthUser;
    }
}