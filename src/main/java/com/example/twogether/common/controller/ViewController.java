package com.example.twogether.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ViewController {

    @GetMapping("/")
    public String init() { return "login"; }

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

    @GetMapping("/views/boards/{id}")
    public String board(@PathVariable Long id, Model model) {
        model.addAttribute("id", id);
        return "board";
    }
}
