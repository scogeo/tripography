package com.tripography.web.controller;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.rumbleware.email.EmailService;
import com.rumbleware.invites.InviteCode;
import com.rumbleware.invites.InviteRequest;
import com.rumbleware.invites.InviteService;
import com.rumbleware.web.forms.FormErrors;
import com.tripography.accounts.Account;
import com.tripography.accounts.AccountService;
import com.tripography.telemetry.DailyVehicleReading;
import com.tripography.telemetry.DailyVehicleReadingRepository;
import com.tripography.telemetry.VehicleTelemetryService;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleService;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gscott
 */
@Controller
@RequestMapping("/olympus/")
@Secured("ROLE_ADMIN")
public class AdminController extends WebApplicationObjectSupport {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private InviteService inviteService;

    @Autowired
    private DailyVehicleReadingRepository dailyVehicleReadingRepository;

    @Autowired
    private VehicleTelemetryService telemetryService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AWSCredentialsProvider awsCredentialsProvider;

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(Principal user) {
        return "redirect:/olympus/stats";
    }

    @RequestMapping(value = "system", method = RequestMethod.GET)
    public String systemPage(Principal user, Model model) {
        model.addAttribute("aws", awsCredentialsProvider.getCredentials());
        return "admin/system";
    }

    @RequestMapping(value = "stats", method = RequestMethod.GET)
    public String statsPage(Principal user, Model model) {

        Map<String, Object> stats = new HashMap<String, Object>();

        stats.put("accounts", accountService.numberOfAccounts());
        stats.put("vehicles", vehicleService.count());
        stats.put("invite requests", inviteService.count());

        model.addAttribute("stats", stats);
        return "admin/stats";
    }

    @RequestMapping(value = "invites", method = RequestMethod.GET)
    public String invitesPage(Principal user, Model model) {

        List <InviteRequest> requests = inviteService.findAll(new Sort(Sort.Direction.DESC, "c"));

        model.addAttribute("requests", requests);
        return "admin/invites";

    }

    @RequestMapping(value = "inviteCodes", method = RequestMethod.GET)
    public String getInviteCodes(Principal user, Model model) {

        List<InviteCode> inviteCodes = inviteService.findAllInviteCodes();

        model.addAttribute("inviteCodes", inviteCodes);
        return "admin/inviteCodes";

    }

    @RequestMapping(value = "inviteCodes/create", method = RequestMethod.GET)
    public String getInviteCodeCreate(Principal user, Model model) {

        List<InviteCode> inviteCodes = inviteService.findAllInviteCodes();

        model.addAttribute("inviteCodes", inviteCodes);
        return "admin/inviteCodeCreate";

    }

    @RequestMapping(value = "inviteCodes/create", method = RequestMethod.POST)
    public String postInviteCodeCreate(Principal user, @ModelAttribute("inviteCode") @Valid InviteCodeForm form,
                                       BindingResult bindingResult, Model model, RedirectAttributes redirectAttrs) {

        logger.info("invite create called" + form);

        model.addAttribute("form", form);

        if (bindingResult.hasErrors()) {
            model.addAttribute("form", form);
            model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));
            return "admin/inviteCodeCreate";
        }

        InviteCode inviteCode = new InviteCode(form.getInviteCode());
        inviteCode.setCount(form.getCount());

        inviteService.addInviteCode(inviteCode);

        redirectAttrs.addFlashAttribute("message", "Invite code created");
        return "redirect:/olympus/inviteCodes/create";

    }

    @RequestMapping(value = "invites/update", method = RequestMethod.POST)
    public String updateInvites(Principal user, @ModelAttribute("invites") InviteForm inviteForm, HttpServletRequest request, Model model) {

        logger.info("get form " + inviteForm.getSelected());

        logger.info("Updates are " + request.getParameterMap());

        return "redirect:/olympus/invites";

    }

    @RequestMapping(value = "invites/{inviteId}/invite", method = RequestMethod.POST)
    public String updateInvites(@PathVariable("inviteId") String inviteId) {
        logger.info("Invite requested for invite id " + inviteId);

        inviteService.createInviteCode(inviteId);

        return "redirect:/olympus/invites";
    }

    @RequestMapping(value = "dailyReadings", method = RequestMethod.GET)
    public String getDailyReadings(Principal user, Model model) {
        List<DailyVehicleReading> readings = dailyVehicleReadingRepository.findAll(new Sort(Sort.Direction.ASC, "n"));

        model.addAttribute("readings", readings);

        return "admin/dailyReadings";
    }


    @RequestMapping(value = "telemetry", method = RequestMethod.GET)
    public String getTelemetry(Principal user, Model model) {

        model.addAttribute("running", telemetryService.isRunning());

        return "admin/telemetry";
    }

    @RequestMapping(value = "telemetry/update", method = RequestMethod.POST)
    public String updateTelemetryStatus(Principal user, @ModelAttribute("action") String action, Model model) {

        logger.info("got a call, woots " + action);

        if ("enable".equals(action)) {
            logger.info("Starting telemetry service");
            telemetryService.startService();
        }
        else if ("disable".equals(action)) {
            logger.info("Stopping telemetry service...");
            telemetryService.stopService();
        }

        return "redirect:/olympus/telemetry";
    }

    @RequestMapping(value = "accounts", method = RequestMethod.GET)
    public String getAccounts(Principal user, Model model) {

        List<Account> accounts = accountService.findAll(new Sort(Sort.Direction.DESC, "c"));

        model.addAttribute("accounts", accounts);



        return "admin/accounts";
    }

    @RequestMapping(value = "accounts/{accountId}", method = RequestMethod.GET)
    public String getAccounts(@PathVariable("accountId") String accountId, Principal user, Model model) {

        Account account = accountService.findById(accountId);

        List<Vehicle> vehicles = vehicleService.getVehiclesByAccount(accountId);
        // Don't set the "account" attribute as we use that for logged-in user in some cases, need to check this.
        model.addAttribute("userAccount", account);
        model.addAttribute("vehicles", vehicles);
        return "admin/account";
    }

    @RequestMapping(value = "accounts/{accountId}/password", method = RequestMethod.POST)
    public String getAccounts(@PathVariable("accountId") String accountId, @RequestParam("newPassword") String password,
                              @RequestParam("confirmPassword") String verifyPassword, Principal user, Model model,
                              RedirectAttributes redirectAttrs) {

        Account account = accountService.findById(accountId);

        logger.info("UPdate passwrod called with " + password + " and " + verifyPassword);

        String message = null;

        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(verifyPassword)) {
            message = "Password is empty";
        }
        else if (password.equals(verifyPassword)) {
            accountService.updatePassword(accountId, password);
            message = "Password updated.";
        }
        else {
            message = "Passwords do not match.";
        }

        redirectAttrs.addFlashAttribute("message", message);

        return "redirect:/olympus/accounts/" + accountId;

    }



    @RequestMapping(value = "vehicles", method = RequestMethod.GET)
    public String getVehicles(Principal user, Model model) {

        List<Vehicle> vehicles = vehicleService.findAll();

        model.addAttribute("vehicles", vehicles);

        return "admin/vehicles";
    }

    @RequestMapping(value = "vehicles/{vehicleId}", method = RequestMethod.GET)
    public String getVehicle(@PathVariable("vehicleId") String vehicleId, Principal user, Model model) {

        Vehicle vehicle = vehicleService.findById(vehicleId);

        model.addAttribute("vehicle", vehicle);

        model.addAttribute("owner", accountService.findById(vehicle.getAccountId()));

        DailyVehicleReading reading = dailyVehicleReadingRepository.findOne(new ObjectId(vehicleId));

        model.addAttribute("reading", reading);

        return "admin/vehicle";
    }

    @RequestMapping(value = "email/test", method = RequestMethod.GET)
    public String getTestEmailPage() {
        return "admin/email/test";
    }

    @RequestMapping(value = "email/test", method = RequestMethod.POST)
    public String sendTestEmail(@ModelAttribute("email") @Valid TestEmailForm form,
                                BindingResult bindingResult, Model model, RedirectAttributes redirectAttrs) {

        logger.info("Test email called" + form);

        model.addAttribute("form", form);

        if (bindingResult.hasErrors()) {
            model.addAttribute("form", form);
            model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));
            return "admin/email/test";
        }

        emailService.sendMessageToEmail("", form.getEmail(), form.getSubject(), form.getBody());

        //redirectAttrs.addFlashAttribute("message", "Invite code created");

        return "redirect:/olympus/email/test";
    }

    public static class TestEmailForm {

        @NotEmpty
        @Email
        private String email;

        @NotEmpty
        private String subject;

        @NotEmpty
        private String body;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        @Override
        public String toString() {
            return "TestEmailForm{" +
                    "email='" + email + '\'' +
                    ", subject='" + subject + '\'' +
                    ", body='" + body + '\'' +
                    '}';
        }
    }

    public static class InviteCodeForm {

        @NotEmpty
        private String inviteCode;

        @Min(1) @NotNull
        private Integer count;

        public void setInviteCode(String inviteCode) {
            this.inviteCode = inviteCode;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public String getInviteCode() {
            return inviteCode;
        }

        public Integer getCount() {
            return count;
        }

        @Override
        public String toString() {
            return "InviteCodeForm{" +
                    "inviteCode='" + inviteCode + '\'' +
                    ", count=" + count +
                    '}';
        }
    }
    public static class InviteForm {

        private List<String> selected;

        public List<String> getSelected() {
            return selected;
        }
    }

}