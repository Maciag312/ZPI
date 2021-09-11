package com.zpi.domain.organization.client;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.List;

@Getter
@Builder
@EqualsAndHashCode
public class Client {
    private HashSet<String> availableRedirectUri;
    private final List<String> hardcodedDefaultScope = List.of("openid profile".split(" "));
    private final String id;
    String organizationName;


    public boolean containsRedirectUri(String redirectUri) {
        if (availableRedirectUri == null) {
            availableRedirectUri = new HashSet<>();
        }

        return availableRedirectUri.contains(redirectUri);
    }

    public void addRedirectUri(String uri) {
        if (availableRedirectUri == null) {
            availableRedirectUri = new HashSet<>();
        }

        availableRedirectUri.add(uri);
    }
}
