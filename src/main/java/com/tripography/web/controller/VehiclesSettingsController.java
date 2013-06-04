package com.tripography.web.controller;

import com.rumbleware.accounts.UserAccount;
import com.rumbleware.tesla.TeslaVehicle;
import com.rumbleware.tesla.VehicleFactory;
import com.rumbleware.tesla.api.*;
import com.rumbleware.web.forms.FormErrors;
import com.rumbleware.web.security.SaltedUser;
import com.tripography.accounts.Account;
import com.tripography.accounts.AccountService;
import com.tripography.providers.tesla.TeslaVehicleProvider;
import com.tripography.providers.VehicleProviderService;
import com.tripography.telemetry.VehicleTelemetryService;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleService;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationObjectSupport;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * @author gscott
 */
@Controller
@RequestMapping(AppPaths.SETTINGS + "/vehicles")
public class VehiclesSettingsController extends WebApplicationObjectSupport {

    private static final Logger logger = LoggerFactory.getLogger(VehiclesSettingsController.class);

    @Autowired
    AccountService accountService;

    @Autowired
    VehicleService vehicleService;

    @Autowired
    VehicleProviderService vehicleProviderService;

    @Autowired
    VehicleTelemetryService telemetryService;

    @Autowired
    TeslaPortal teslaPortal;

    private UserAccount lookupAccount(Principal principal) {
        String id = SaltedUser.userIdFromPrincipal(principal);
        return id != null ? accountService.findById(id) : null;
    }

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String list(Principal principal, Model model) {
        String accountId = SaltedUser.userIdFromPrincipal(principal);

        UserAccount account = lookupAccount(principal);
        model.addAttribute("account", account);

        // See if the user has any vehicle providers setup
        TeslaVehicleProvider provider = vehicleProviderService.findByAccountId(new ObjectId(accountId));

        if (provider == null) {
            return "settings/vehicle/setup";
        }

        model.addAttribute("provider", provider);


        logger.info("Listing vehicles for "+ accountId);
        List<Vehicle> vehicles = vehicleService.getVehiclesByAccount(accountId);

        logger.info("got vehicles " + vehicles);

        model.addAttribute("vehicles", vehicles);


        return "/settings/vehicle/view";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String submitRegister(Principal principal, @ModelAttribute("register") @Valid MyTeslaCredentialsForm credentialsForm,
                                 BindingResult bindingResult, Model model) {

        String accountId = SaltedUser.userIdFromPrincipal(principal);
        UserAccount account = lookupAccount(principal);
        model.addAttribute("account", account);

        if (bindingResult.hasErrors()) {
            credentialsForm.setPassword("");
            model.addAttribute("form", credentialsForm);
            model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));
            // Return the form again
            return "settings/vehicle/setup";
        }
        else {
            TeslaVehicleProvider provider = new TeslaVehicleProvider(credentialsForm.getUsername(), credentialsForm.getPassword());
            provider.setAccountId(new ObjectId(accountId));

            try {
                provider.validate(teslaPortal);
                vehicleProviderService.create(provider);
            }
            catch (Exception e) {
                // catch all for now
                model.addAttribute("form", credentialsForm);
                model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));
                bindingResult.addError(new ObjectError("register", "Username/Password invalid"));
                return "settings/vehicle/setup";
            }

            List<Vehicle> vehicles = vehicleService.addVehiclesFromProvider(accountId, provider);
            for (Vehicle vehicle : vehicles) {
                telemetryService.startTrackingVehicle(vehicle);
            }

            return "redirect:/settings/vehicles";
        }

    }

    @RequestMapping(value = "/renew", method = RequestMethod.GET)
    public String getRenewPage(Principal principal, Model model) {
        String accountId = SaltedUser.userIdFromPrincipal(principal);
        UserAccount account = lookupAccount(principal);
        model.addAttribute("account", account);

        TeslaVehicleProvider provider = vehicleProviderService.findByAccountId(new ObjectId(accountId));

        if (provider == null) {
            return "redirect:/settings/vehicle";
        }

        model.addAttribute("provider", provider);

        return "settings/vehicle/renew";
    }

    @RequestMapping(value = "/renew", method = RequestMethod.POST)
    public String submitRenew(Principal principal, Model model,
                              @ModelAttribute("register") @Valid MyTeslaCredentialsForm credentialsForm,
                              BindingResult bindingResult) {

        String accountId = SaltedUser.userIdFromPrincipal(principal);
        UserAccount account = lookupAccount(principal);
        model.addAttribute("account", account);

        TeslaVehicleProvider provider = vehicleProviderService.findByAccountId(new ObjectId(accountId));

        if (provider == null) {
            return "redirect:/settings/vehicle";
        }

        model.addAttribute("provider", provider);

        if (bindingResult.hasErrors()) {
            credentialsForm.setPassword("");
            model.addAttribute("form", credentialsForm);
            model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));

            logger.info("form contains errors");

            return "settings/vehicle/renew";
        }

        logger.info("Called with a form " + credentialsForm);


        try {
            // update password
            provider.setPassword(credentialsForm.getPassword());
            provider.validate(teslaPortal);
            vehicleProviderService.update(provider);

            // TODO refresh vehicles.
            return "redirect:/settings/vehicles";
        }
        catch (Exception e) {
            // catch all for now
            model.addAttribute("form", credentialsForm);
            model.addAttribute("formErrors", new FormErrors(bindingResult, getWebApplicationContext()));
            bindingResult.addError(new ObjectError("register", "Username/Password invalid"));
        }


        return "settings/vehicle/renew";
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
