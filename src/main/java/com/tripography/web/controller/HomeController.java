package com.tripography.web.controller;

import com.rumbleware.web.security.SaltedUser;
import com.tripography.accounts.Account;
import com.tripography.accounts.AccountService;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Nullable;
import javax.validation.constraints.Size;
import java.security.Principal;

/**
 * @author gscott
 */
@Controller
public class HomeController {

    private static Logger logger =  LoggerFactory.getLogger(HomeController.class);

    //@Autowired
    //InviteService _inviteService;

    @Autowired
    AccountService accountService;

    // temporarily added HEAD support to see if AWS is happy
    @RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.HEAD })
    //@RequestMapping(value = "/")
    public String home(@Nullable Principal principal, Model model) {

        logger.info("Called home controoler");
        model.addAttribute("inviteRequest", new InviteForm());

        if (principal == null) {
            return "welcome";
        }
        else {
            String userId = SaltedUser.userIdFromPrincipal(principal);
            return renderHomePage(userId, model);
        }
    }

    private String renderHomePage(String userId, Model model) {

        Account account = accountService.findById(userId);

        model.addAttribute("account", account);


        return "home";
    }

    public static class InviteForm {
        @NotEmpty
        @Size(max = 30)
        private String fullname;

        @NotEmpty
        @Email
        private String email;

        public String getFullname() {
            return fullname;
        }

        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


    }
}
