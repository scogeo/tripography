package com.tripography.web.controller;

import com.mongodb.DBObject;
import com.tripography.telemetry.VehicleAnalyticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author gscott
 */
@Controller
@RequestMapping(AppPaths.CHARTS)
public class ChartDataController {

    private static final Logger logger = LoggerFactory.getLogger(ChartDataController.class);

    @Autowired
    private VehicleAnalyticsService analyticsService;


    @RequestMapping(value = "/{vehicleId}/daily.json", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody String vehicleDailyOdometerData(@PathVariable("vehicleId") String vehicleId) {
        DBObject result = analyticsService.dailyDistanceForVehicle(vehicleId, "2013");
        if (result != null) {
            return result.toString();
        }
        else {
            return "{}";
        }
    }

    @RequestMapping(value = "/{vehicleId}/daily-histogram.json", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody String vehicleDailyHistogramData(@PathVariable("vehicleId") String vehicleId) {
        DBObject result = analyticsService.dailyHistogramForVehicle(vehicleId, "2013");
        if (result != null) {
            return result.toString();
        }
        else {
            return "{}";
        }
    }
}
