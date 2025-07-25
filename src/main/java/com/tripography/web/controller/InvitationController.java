package com.tripography.web.controller;

import com.rumbleware.invites.InviteRequest;
import com.rumbleware.invites.InviteService;
import com.rumbleware.web.forms.FormErrors;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequestMapping(AppPaths.INVITE)
public class InvitationController extends WebApplicationObjectSupport {

    private static final Logger logger = LoggerFactory.getLogger(InvitationController.class);

    @Autowired
    InviteService inviteService;

    @RequestMapping(value = "/request", method = RequestMethod.POST)
    public String createInviteRequest(Principal user, Model model, @ModelAttribute("inviteRequest") @Valid InviteRequestForm inviteRequestForm,
                                      BindingResult bindingResult) {

        if (user != null) {
            return "redirect:/";
        }
        if (bindingResult.hasErrors()) {
            // Return the form again
            //logger.info("woops we got some erros " + bindingResult.getAllErrors());
            model.addAttribute("form", inviteRequestForm);
            model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));

            return "/invite/request";
        }
        else {

            InviteRequest request = inviteService.newObject();
            request.setEmail(inviteRequestForm.getEmail());

            try {
                inviteService.create(request);
            }
            catch (DuplicateKeyException e) {
                // ignore for now, already recorded it.
                logger.debug("got a dup", e);
            }

            return "redirect:/invite/success";
        }


    }

    @RequestMapping(value = "/request", method = RequestMethod.GET)
    public String inviteRequest(Principal user) {
        return "invite/request";
    }


    @RequestMapping(value = "/success", method = RequestMethod.GET)
    public String inviteSuccess() {
        return "invite/success";
    }

    public static class InviteRequestForm {
        @NotEmpty
        @Email
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}