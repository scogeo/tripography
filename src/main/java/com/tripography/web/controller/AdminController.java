package com.tripography.web.controller;

import com.rumbleware.invites.InviteRequest;
import com.rumbleware.invites.InviteService;
import com.tripography.accounts.AccountService;
import com.tripography.vehicles.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
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
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private InviteService inviteService;

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

    @RequestMapping(value = "invites/update", method = RequestMethod.POST)
    public String updateInvites(Principal user, @ModelAttribute("invites") InviteForm inviteForm, HttpServletRequest request, Model model) {


        logger.info("get form " + inviteForm.getSelected());

        logger.info("Updates are " + request.getParameterMap());

        return "redirect:/olympus/invites";

    }

    public static class InviteForm {

        private List<String> selected;

        public List<String> getSelected() {
            return selected;
        }
    }

}