package com.zpi.domain.organization;

import com.zpi.domain.organization.manager.Manager;
import lombok.Getter;


@Getter
public class Organization {
    String name;
    public Organization(String name) {
        this.name = name;
    }
}
