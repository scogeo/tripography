package com.tripography.web.controller;

import com.rumbleware.tesla.TeslaVehicle;
import com.rumbleware.tesla.VehicleFactory;
import com.rumbleware.tesla.api.*;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

/**
 * @author gscott
 */
@Controller
@RequestMapping("/vehicles/")
public class VehiclesController {

    private static final Logger logger = LoggerFactory.getLogger(VehiclesController.class);

    @Autowired
    AccountService accountService;

    @Autowired
    VehicleService vehicleService;

    @Autowired
    VehicleProviderService vehicleProviderService;

    @Autowired
    VehicleTelemetryService telemetryService;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String register() {
        return "/vehicles/register";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String list(Principal principal, Model model) {
        String accountId = SaltedUser.userIdFromPrincipal(principal);

        logger.info("Listing vehicles for "+ accountId);
        List<? extends Vehicle> vehicles = vehicleService.getVehiclesByAccount(accountId);

        logger.info("got vehicles " + vehicles);

        model.addAttribute("vehicles", vehicles);


        return "/vehicles/list";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String submitRegister(Principal principal, @ModelAttribute("inviteRequest") @Valid MyTeslaCredentialsForm credentialsForm,
                                 BindingResult bindingResult) {

        String accountId = SaltedUser.userIdFromPrincipal(principal);

        logger.info("Submit called for register " + credentialsForm);

        if (bindingResult.hasErrors()) {

            // Return the form again
            return "/vehicles/register";
        }
        else {
            logger.info("Registering user " + credentialsForm.getUsername());

            TeslaVehicleProvider provider = new TeslaVehicleProvider(credentialsForm.getUsername(), credentialsForm.getPassword());

            provider.validate();

            provider.setAccountId(new ObjectId(accountId));

            vehicleProviderService.create(provider);


            vehicleService.addVehiclesFromProvider(accountId, provider);

        }

        return "/vehicles/register";
    }

    @RequestMapping(value = "/odometer", method = RequestMethod.GET)
    public String getOdometer(Principal principal, Model model) {
        String accountId = SaltedUser.userIdFromPrincipal(principal);

        Account account = accountService.findById(accountId);

        logger.info("Account name " + account.getUsername());
        //logger.info("Accont is " + account.getVehicleProviders());

        //TeslaVehicleProvider provider = account.getVehicleProviders().get(0);

        PortalCredentials credentials = null; //provider.getCredentials();

        Portal portal = new Portal();

        List<TeslaVehicle> vehicles = VehicleFactory.getVehicles(portal, credentials);

        logger.info("Vehicles are " + vehicles);


        TeslaVehicle vehicle = vehicles.get(0);

        model.addAttribute("odometer", vehicle.getOdometer());

        logger.info("Drive state " + vehicles.get(0).driveState());




        return "/vehicles/odometer";
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
