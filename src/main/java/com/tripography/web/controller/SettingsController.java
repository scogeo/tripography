package com.tripography.web.controller;

import com.rumbleware.accounts.UserAccount;
import com.rumbleware.accounts.UserAccountService;
import com.rumbleware.web.security.PasswordUtil;
import com.rumbleware.web.security.SaltedUser;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;

/**
 * @author gscott
 */
@Controller
@RequestMapping(AppPaths.SETTINGS)
public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @Autowired
    private UserAccountService _accountService;

    private UserAccount lookupAccount(Principal principal) {
        String id = SaltedUser.userIdFromPrincipal(principal);
        return id != null ? _accountService.findById(id) : null;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String getSettings() {
        return "redirect:/settings/vehicles";
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String displayAccountSettings(Principal principal, Model model) {

        UserAccount account = lookupAccount(principal);
        model.addAttribute("account", account);

        return "settings/account";
    }

    @RequestMapping(value = "/account/update", method = RequestMethod.POST)
    public String updateAccountSettings(Principal user,
                                        @ModelAttribute("settings") @Valid AccountForm accountForm, BindingResult bindingResult,
                                        Model model) {
        //logger.info("Received a post for updating account");
        return "redirect:/settings/account";
    }

    public static class AccountForm {
        @NotEmpty
        @Size(max = 30)
        private String fullname;

        @NotEmpty
        @Email
        private String email;

        @NotEmpty
        @Size(max = 30)
        private String username;

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

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    @RequestMapping(value = "/password", method = RequestMethod.GET)
    public String displayPasswordSettings(Principal principal, Model model) {
        UserAccount account = lookupAccount(principal);
        model.addAttribute("account", account);
        model.addAttribute("settings", new PasswordSettingsForm());
        return "settings/password";
    }

    @RequestMapping(value = "/password/update", method = RequestMethod.POST)
    public String udpatePasswordSettings(Principal user,
                                         @ModelAttribute("settings") @Valid PasswordSettingsForm form,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // verify password

            logger.debug("woops we have errors " + bindingResult.getErrorCount());
        }
        else {
            UserAccount account = lookupAccount(user);
            if (account != null) {
                // TODO This check is no longer useufl with BCrypt, check logic here
                if (account.getHashedPassword().equals(PasswordUtil.hashPassword(account.getId(), form.currentPassword))) {
                    // update the password
                    logger.info("updating password");
                    _accountService.updatePassword(account.getId(), form.newPassword);
                }
                else {
                    logger.info("passwords do not match");
                }
            }
            else {
                // todo - shouldn't happen
            }
        }
        return "redirect:/settings/password";
    }



    @ScriptAssert(lang = "javascript", script = "_this.newPassword.equals(_this.confirmPassword)")
    public static class PasswordSettingsForm {

        @NotEmpty
        private String currentPassword;

        @NotEmpty
        private String newPassword;

        @NotEmpty
        private String confirmPassword;

        public String getCurrentPassword() {
            return currentPassword;
        }

        public void setCurrentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }


}
