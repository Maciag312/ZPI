package com.zpi.domain.rest.ams;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class Client {
    private final List<String> availableRedirectUri;
    private final String id;

    private final List<String> hardcodedDefaultScope = List.of("profile".split(" "));
    private final Set<String> availableGrantTypes = Set.of("authorization_code");
    private final String organizationName = "asdf";
}
