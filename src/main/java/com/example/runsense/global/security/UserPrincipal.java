package com.example.runsense.global.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@RequiredArgsConstructor
public class UserPrincipal implements Serializable {
    private final Long userId;
    private final String email;
}
