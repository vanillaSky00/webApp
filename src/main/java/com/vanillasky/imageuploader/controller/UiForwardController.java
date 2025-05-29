package com.vanillasky.imageuploader.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UiForwardController {

    // Anything *not* starting with /api and not a static asset (*.js, *.css, *.png …)
    @RequestMapping("/{path:^(?!api$).*$}/**")
    public String forward(){
        return "forward:/";
    }
}
