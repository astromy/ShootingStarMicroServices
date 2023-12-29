package com.astromyllc.astropreorder.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping({"/","index"})
    public String getIndex(Model model) {
        return "orbsPreOrder";

    }

}
