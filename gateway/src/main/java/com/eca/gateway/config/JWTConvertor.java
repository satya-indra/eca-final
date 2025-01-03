package com.eca.gateway.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class JWTConvertor implements Converter<Jwt, AbstractAuthenticationToken>{

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
             jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
             extractResourceRoles(jwt,resourceId).stream()
     ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(jwt,authorities,getPrincipleClaimName(jwt));
    }


    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }


    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt, String resourceId) {
        // Extract role collections from JWT claims
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> accounts = jwt.getClaim("accounts");
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        // Use a streamlined approach to extract and combine roles from various sources
        return Stream.of(
                        extractRoles(realmAccess),
                        extractRoles(accounts),
                        extractRolesFromResource(resourceAccess, resourceId)
                )
                .flatMap(Collection::stream) // Flatten the stream of role lists into a single stream of roles
                .distinct() // Ensure uniqueness of roles
                .map(roleName -> roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName) // Normalize role names
                .map(SimpleGrantedAuthority::new) // Convert role names to GrantedAuthority objects
                .collect(Collectors.toSet()); // Collect into a set to eliminate duplicates
    }

    private List<String> extractRoles(Map<String, Object> access) {
        return access == null ? Collections.emptyList() : (List<String>) access.get("roles");
    }

    private List<String> extractRolesFromResource(Map<String, Object> resourceAccess, String resourceId) {
        return Optional.ofNullable(resourceAccess)
                .map(ra -> (Map<String, Object>) ra.get(resourceId))
                .map(r -> (List<String>) r.get("roles"))
                .orElse(Collections.emptyList());
    }


}
