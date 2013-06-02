package com.tripography.web.controller;

import com.rumbleware.accounts.UserAccount;
import com.rumbleware.accounts.UserAccountService;
import com.rumbleware.dao.UniqueKeyException;
import com.rumbleware.web.forms.FormErrors;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gscott
 */
@Controller
@RequestMapping(AppPaths.SIGNUP)
public class SignupController extends WebApplicationObjectSupport {

    private Logger logger = LoggerFactory.getLogger(SignupController.class);

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserAccountService accountService;

    private Set<String> inviteCodes = new HashSet<String>();

    public SignupController() {
        inviteCodes.add("tripit");
        inviteCodes.add("rumble");
        inviteCodes.add("teslive2013");
        inviteCodes.add("teslaroadtrip");
        inviteCodes.add("tmc2013");
    }

    @RequestMapping(method = {RequestMethod.GET })
    public String displaySingnupForm(Principal user, Model model) {
        logger.info("signup called with get");

        model.addAttribute("account", new AccountForm());

        // if authenticated redirect to home page
        if (user != null) {
            return "redirect:/";
        }
        return "signup";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSignupForm(Principal user,
                                    @ModelAttribute("account") @Valid AccountForm accountForm, BindingResult bindingResult,
                                    Model model) {
        logger.info("signup called with post");

        logger.info("binding result is " + bindingResult);

        logger.info("keys are " + model.asMap().keySet());

        if (user != null) {
            return "redirect:/";
        }

        if (accountForm.invite != null && !inviteCodes.contains(accountForm.invite.toLowerCase())) {
            bindingResult.addError(new FieldError("account", "invite", "Unknown invite code."));
        }

        model.addAttribute("form", accountForm);
        model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));

        if (bindingResult.hasErrors()) {
            // Return the form again
            //model.addAttribute("form", accountForm);
            //model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));
            return "signup";
        }
        else {
            // Create an account
            UserAccount account = accountService.newAccount();

            account.setUsername(accountForm.getUsername());
            account.setFullname(accountForm.getFullname());
            account.setEmail(accountForm.getEmail());
            account.setClearTextPassword(accountForm.getPassword());

            logger.info("Username is " + account.getUsername());
            try {
                accountService.update(account);
                logger.info("Account successfully saved");

            }
            catch(UniqueKeyException e) {
                logger.warn("hit a duplicate key " + e.getKey());
                bindingResult.addError(new FieldError("account", e.getKey(), "already in use."));

                return "signup";
            }
            catch(ConstraintViolationException e) {
                logger.warn("Constraint violation", e);
                logger.warn("Violations " + e.getConstraintViolations());
            }
            catch(DuplicateKeyException e) {
                // TODO process exceptions
                logger.error(e.getMessage());
            }

            authenticateSession(account);

            return "redirect:/";
        }

    }

    /**
     * This will authenticate the current session with the specified account, if the hashed password
     * matches the username's account in the database.
     */
    private void authenticateSession(UserAccount account) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(account.getUsername());

        // The hashed password provided must match the value in the database before we can
        // authorize.  This prevents accidental session authorization by just passing in an account
        // object with a valid username.
        if (userDetails.getPassword().equals(account.getHashedPassword())) {
            // It is important that we use the account parameter's hashed password, and not
            // the userDetails pulled from the database to prevent accidental authorization from a random account being
            // passed to this service.
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails,
                            account.getHashedPassword(),
                            userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        else {
            logger.error("Passwords do not match!!!!");
        }
    }

    @RequestMapping(value = "/check/username", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody
    CheckResult checkUsername(@RequestParam(value = "username", required = false) String username,
                              @RequestParam(value = "fullname", required = false) String fullname,
                              @RequestParam(value = "email", required = false) String email) {
        if (accountService.isUsernameAvailable(username)) {
            return new CheckResult(true);
        }
        else {
            return new CheckResult(false);
        }

    }

    @RequestMapping(value = "/check/email", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody CheckResult checkEmail(@RequestParam(value = "email", required = false) String email) {
        return new CheckResult(accountService.isEmailAvailable(email));
    }

    public static class CheckResult {

        private final boolean available;

        public CheckResult(boolean available) {
            this.available = available;
        }

        public boolean isAvailable() {
            return available;
        }

    }

    public static class AccountForm {
        @NotEmpty
        @Size(max = 30)
        private String fullname;

        @NotEmpty
        @Email
        private String email;


        @NotEmpty
        private String password;

        @NotEmpty
        @Size(max = 30)
        private String username;


        @NotEmpty
        private String invite;


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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getInvite() {
            return invite;
        }

        public void setInvite(String invite) {
            this.invite = invite;
        }

    }
}
