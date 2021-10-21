package com.zpi.infrastructure.rest.ams;

import com.zpi.domain.rest.ams.Client;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ClientDTO {
    private List<String> availableRedirectUri;
    private String id;

    public Client toDomain(){
        return new Client(availableRedirectUri, id);
    }
}
