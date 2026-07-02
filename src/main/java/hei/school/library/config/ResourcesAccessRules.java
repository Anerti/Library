package hei.school.library.config;

import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import hei.school.library.exception.NotFoundException;
import hei.school.library.repository.dao.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResourcesAccessRules {
    private final UserRepository userRepository;

    public boolean GrantAccessFor(UUID requestedResourceId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserId = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

        User user = userRepository.findById(requestedResourceId).orElseThrow(() -> new NotFoundException(String.format("User %s not found", requestedResourceId)));

        return authenticatedUserId.equals(requestedResourceId.toString()) || (role.equals("ADMIN") && user.getRole().equals(Role.CUSTOMER));
    }
}
