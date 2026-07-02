package hei.school.library.config;

import hei.school.library.entity.User;
import hei.school.library.entity.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourcesAccessRules {

  public boolean grantAccessFor(User user) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String authenticatedUserId = auth.getName();
    String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");

    return authenticatedUserId.equals(user.getId().toString())
        || (role.equals("ADMIN") && user.getRole().equals(Role.CUSTOMER));
  }
}
