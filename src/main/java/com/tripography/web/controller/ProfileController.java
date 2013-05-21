package com.tripography.web.controller;

import com.rumbleware.accounts.UserAccount;
import com.rumbleware.accounts.UserAccountService;

import com.rumbleware.asset.controller.ResourceNotFoundException;
import com.tripography.vehicles.Vehicle;
import com.tripography.vehicles.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @author gscott
 */
@Controller
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private UserAccountService accountService;

    @Autowired
    private VehicleService vehicleService;

    @RequestMapping(value = AppPaths.PROFILE_PATTERN, method = RequestMethod.GET)
    public String getProfile(@PathVariable("username") String username, Model model) {

        UserAccount account = accountService.findByUsername(username);
        if (account != null) {
            model.addAttribute("account", account);

            logger.info("requesting vehicles for " + account.getId());

            List<Vehicle> vehicles = vehicleService.getVehiclesByAccount(account.getId());

            if (vehicles.size() > 0) {
                Vehicle vehicle = vehicles.get(0);
                logger.info("Got vehicle " + vehicle.getId());
                model.addAttribute("vehicle", vehicle);

            }

            return "profile/user";
        }
        else {
            throw new ResourceNotFoundException();
        }
    }

    @RequestMapping(value = AppPaths.PROFILE_VEHICLE_PATTERN, method = RequestMethod.GET)
    public String getProfileVehicle(@PathVariable("username") String username,
                                    @PathVariable("vehicle") String vehicleId,
                                    Model model) {

        if (username == null || vehicleId == null) {
            throw new ResourceNotFoundException();
        }

        UserAccount account = accountService.findByUsername(username);

        Vehicle vehicle = null;

        if (vehicleId.length() == 24) {
            vehicle = vehicleService.findById(vehicleId);
        }
        else {
            // vehicle is a nick name, lookup nickname
        }

        if (account == null || vehicle == null) {
            throw new ResourceNotFoundException();
        }

        model.addAttribute("account", account);
        model.addAttribute("vehicle", vehicle);

        return "profile/vehicle";
    }

}