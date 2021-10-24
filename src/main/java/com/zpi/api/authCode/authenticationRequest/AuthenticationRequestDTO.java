package com.zpi.api.authCode.authenticationRequest;

import com.zpi.api.authCode.authenticationRequest.audit.AuditMetadataDTO;
import com.zpi.api.common.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationRequestDTO {
    private final UserDTO user;
    private final AuditMetadataDTO audit;
}
