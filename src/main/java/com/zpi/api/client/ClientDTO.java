package com.zpi.api.client;

import com.zpi.domain.client.Client;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    @NotNull
    @NotEmpty
    private String id;

    @NotNull
    private List<String> availableRedirectUri;

    public Client toDomain() {
        return Client.builder()
                .id(id)
                .availableRedirectUri(new HashSet<>(availableRedirectUri))
                .build();
    }
}
