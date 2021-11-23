package com.zpi.infrastructure.rest.ams;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDTO {
    private String permission;
    private boolean removable;
}
