package com.zpi.domain.client;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;

@Getter
@Builder
@EqualsAndHashCode
public class Client {
    private HashSet<String> availableRedirectUri;
    private final String id;

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
