package com.tripography.web.controller;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;

/**
 * @author gscott
 */
@Controller
@RequestMapping("/vehicles/")
public class VehiclesController {

    private static final Logger logger = LoggerFactory.getLogger(VehiclesController.class);

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "/vehicles/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String submitRegister(Principal user, @ModelAttribute("inviteRequest") @Valid MyTeslaCredentialsForm credentialsForm,
                                 BindingResult bindingResult) {

        logger.info("Submit called for register " + credentialsForm);

        if (bindingResult.hasErrors()) {

            // Return the form again
            return "/vehicles/register";
        }


        return "/vehicles/register";
    }

    public static class MyTeslaCredentialsForm {

        @NotEmpty
        private String username;


        @NotEmpty
        private String password;


        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public String toString() {
            return "MyTeslaCredentialsForm{" +
                    "username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    '}';
        }
    }
}
