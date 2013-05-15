package com.tripography.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author gscott
 */
@Controller
@RequestMapping("/account")
public class AccountController {

    @RequestMapping("/{name}")
    public String summary() {
        return "summary";
    }

}