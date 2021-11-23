package com.zpi.infrastructure.rest.ams;

import com.zpi.domain.rest.ams.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
    private String username;
    private List<PermissionDTO> permissions;
    private List<String> roles;

    public UserInfo toDomain() {
        var perm = permissions.stream().map(PermissionDTO::getPermission).collect(Collectors.toList());
        return new UserInfo(username, perm, roles);
    }
}
