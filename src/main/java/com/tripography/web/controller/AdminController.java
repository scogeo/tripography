package com.tripography.web.controller;

import com.rumbleware.invites.InviteCode;
import com.rumbleware.invites.InviteRequest;
import com.rumbleware.invites.InviteService;
import com.rumbleware.web.forms.FormErrors;
import com.tripography.accounts.AccountService;
import com.tripography.telemetry.DailyVehicleReading;
import com.tripography.telemetry.DailyVehicleReadingRepository;
import com.tripography.telemetry.VehicleTelemetryService;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleService;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(method = RequestMethod.GET)
    public String mainPage(Principal user) {
        return "redirect:/olympus/stats";
    }

    @RequestMapping(value = "system", method = RequestMethod.GET)
    public String systemPage(Principal user) {
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

    @RequestMapping(value = "dailyReadings", method = RequestMethod.GET)
    public String getDailyReadings(Principal user, Model model) {
        List<DailyVehicleReading> readings = dailyVehicleReadingRepository.findAll();

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

    @RequestMapping(value = "vehicles/{vehicleId}", method = RequestMethod.GET)
    public String getTelemetry(@PathVariable("vehicleId") String vehicleId, Principal user, Model model) {

        Vehicle vehicle = vehicleService.findById(vehicleId);

        model.addAttribute("vehicle", vehicle);

        return "admin/vehicle";
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