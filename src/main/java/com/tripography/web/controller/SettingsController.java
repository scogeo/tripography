package com.tripography.web.controller;

import com.rumbleware.accounts.UserAccount;
import com.rumbleware.accounts.UserAccountService;
import com.rumbleware.web.security.PasswordUtil;
import com.rumbleware.web.security.SaltedUser;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.ScriptAssert;
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
import java.util.logging.Logger;

/**
 * @author gscott
 */
@Controller
@RequestMapping(AppPaths.SETTINGS)
public class SettingsController {

    private static Logger logger = Logger.getLogger(SettingsController.class.getName());

    @Autowired
    private UserAccountService _accountService;

    private UserAccount lookupAccount(Principal principal) {
        String id = SaltedUser.userIdFromPrincipal(principal);
        return id != null ? _accountService.findById(id) : null;
    }

    @RequestMapping(value = "/account", method = RequestMethod.GET)
    public String displayAccountSettings(Principal principal, Model model) {

        UserAccount account = lookupAccount(principal);

        logger.info("found account " + account );
        model.addAttribute("settings", account);

        return "settings/account";
    }

    @RequestMapping(value = "/account/update", method = RequestMethod.POST)
    public String updateAccountSettings(Principal user,
                                        @ModelAttribute("settings") @Valid AccountForm accountForm, BindingResult bindingResult,
                                        Model model) {
        logger.info("Received a post for updating account");
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
    public String displayPasswordSettings(Model model) {
        model.addAttribute("settings", new PasswordSettingsForm());
        return "settings/password";
    }

    @RequestMapping(value = "/password/update", method = RequestMethod.POST)
    public String udpatePasswordSettings(Principal user,
                                         @ModelAttribute("settings") @Valid PasswordSettingsForm form,
                                         BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            // verify password

            logger.info("woops we have errors " + bindingResult.getErrorCount());
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

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String displayProfileSettings(Model model) {
        model.addAttribute("settings", new ProfileSettingsForm());
        return "settings/profile";
    }

    @RequestMapping(value = "/profile/update", method = RequestMethod.POST)
    public String udpateProfileSettings(Principal user,
                                        @ModelAttribute("settings") @Valid ProfileSettingsForm profile,
                                        BindingResult bindingResult,
                                        @RequestParam(value = "image", required = false) MultipartFile file) {

        if (file != null) {
            logger.info("got an image " + file.getOriginalFilename() + " of type " + file.getContentType());

            InputStream input = null;
            try {
                input = file.getInputStream();
                //BufferedImage image = ImageIO.read(file.getInputStream());
                //if (image != null) {
                //    logger.info("image size is " + image.getWidth() + " x " + image.getHeight());
                //}


                UserAccount account = lookupAccount(user);
                //_accountService.setProfilePhoto(account.getId().toString(), file.getBytes(), file.getContentType());
            }
            catch (IOException e) {
                logger.warning("trouble reading stream ");
            }
            finally {
                if (input != null) {
                    try {
                        input.close();
                    }
                    catch (IOException e) {
                        // do nothing
                    }
                }
            }

        }
        return "redirect:/settings/profile";
    }

    public static class ProfileSettingsForm {

        private String bio;

        private String location;

        public String getBio() {
            return bio;
        }

        public void setBio(String bio) {
            this.bio = bio;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    @RequestMapping(value = "/personal", method = RequestMethod.GET)
    public String displayPersonalSettings(Model model) {
        model.addAttribute("settings", new PersonalSettingsForm());
        return "settings/personal";
    }

    @RequestMapping(value = "/personal/update", method = RequestMethod.POST)
    public String udpatePersonalSettings(Principal user,
                                         @ModelAttribute("settings") @Valid PersonalSettingsForm profile,
                                         BindingResult bindingResult) {


        return "redirect:/settings/personal";
    }

    public static class PersonalSettingsForm {

        private String sex;

        private String birthDate;

        private String height;

        private String weight;


        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }
    }
}
