package com.zpi.api.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UIRedirect {

    @RequestMapping(value = "/auth")
    public String auth() {
        return "index.html";
    }

    @RequestMapping(value = "/signin")
    public String signin() {
        return "index.html";
    }

    @RequestMapping(value = "/signup")
    public String signup() {
        return "index.html";
    }
}