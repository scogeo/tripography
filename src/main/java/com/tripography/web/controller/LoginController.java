package com.tripography.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class LoginController {

    @RequestMapping(value = {"/login", "/login/"}, method = RequestMethod.GET)
    public String getLoginPage(HttpServletRequest request, HttpSession session, Model model) {

        if (request.getParameter("login_error") != null) {
            model.addAttribute("error", true);
        }
        else {
            model.addAttribute("error", false);
        }
        return "login";
    }
}