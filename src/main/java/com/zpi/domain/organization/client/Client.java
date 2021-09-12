package com.zpi.domain.organization.client;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Client {
    private final Set<String> availableRedirectUri = new HashSet<>();
    private final List<String> hardcodedDefaultScope = List.of("openid profile".split(" "));
    private final String id;
    String organizationName;
}
