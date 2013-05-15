package com.tripography.web.controller;

import com.rumbleware.accounts.UserAccount;
import com.rumbleware.accounts.UserAccountService;

import com.rumbleware.asset.controller.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author gscott
 */
@Controller
public class ProfileController {

    @Autowired
    private UserAccountService accountService;

    @RequestMapping(value = AppPaths.PROFILE_PATTERN, method = RequestMethod.GET)
    public String getProfile(@PathVariable("username") String username, Model model) {

        UserAccount account = accountService.findByUsername(username);
        if (account != null) {
            model.addAttribute("account", account);
            return "profile";
        }
        else {
            throw new ResourceNotFoundException();
        }
    }
}