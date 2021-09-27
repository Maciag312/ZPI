package com.zpi.api.token;

import com.zpi.domain.token.RefreshRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshRequestDTO {
    private String client_id;
    private String grant_type;
    private String refresh_token;
    private String scope;

    public RefreshRequest toDomain() {
        return new RefreshRequest(client_id, grant_type, refresh_token, scope);
    }
}
