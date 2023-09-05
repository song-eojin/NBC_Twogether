package com.example.twogether.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/views/signup")
    public String signup() {
        return "signup";
    }

    @GetMapping("/views/login")
    public String login() {
        return "login";
    }

    @GetMapping("/views/workspace")
    public String workspace() {
        return "workspace";
    }

    @GetMapping("/views/board")
    public String board() {
        return "board";
    }
}
