package com.zpi.domain.organization.manager;

import java.util.NoSuchElementException;

public interface ManagerAccountManager {
    String signIn(String organizationName, String username, String password) throws IllegalArgumentException, NoSuchElementException;
}
