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

import java.util.*;

/**
 * @author gscott
 */
@Controller
@RequestMapping(AppPaths.CHARTS)
public class ChartDataController {

    private static final Logger logger = LoggerFactory.getLogger(ChartDataController.class);

    @Autowired
    private VehicleAnalyticsService analyticsService;


    @RequestMapping(value = "/vehicle/{vehicleId}/daily.json", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody String vehicleDailyOdometerData(@PathVariable("vehicleId") String vehicleId) {
        DBObject result = analyticsService.dailyDistanceForVehicle(vehicleId, "2013");
        if (result != null) {
            return result.toString();
        }
        else {
            return "{}";
        }
    }

    @RequestMapping(value = "/account/{accountId}/daily.json", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody Map<String, Object> accountDailyOdometerData(@PathVariable("accountId") String accountId) {
        DBObject result = analyticsService.dailyDistanceForAccount(accountId, "2013");
        if (result != null) {
            return computeDailyResult(result);
        }
        else {
            return Collections.EMPTY_MAP;
        }
    }

    private Map<String, Object> computeDailyResult(DBObject object) {
        Map<String, Object> result = new HashMap<>();

        Map<String, Integer> startDate = new HashMap<String, Integer>();

        List<Double> values = new ArrayList<>(400);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));

        int year = 2013;

        startDate.put("year", 2013);

        int firstMonth = -1;
        int firstDay = -1;

        for (int month = 1; month <= 12; month++) {
            DBObject monthlyValues = (DBObject) object.get(Integer.toString(month));
            if (monthlyValues == null) {
                continue;
            }
            if (firstMonth < 0) {
                firstMonth = month;
            }

            calendar.set(Calendar.MONTH, month - 1);
            for (int day = 1; day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH); day++) {
                Object dailyValue = monthlyValues.get(Integer.toString(day));
                if (dailyValue == null) {
                    if (firstDay > 0) {
                        values.add(null);
                    }
                }
                else {
                    if (firstDay < 0) {
                        firstDay = day;
                    }
                }
                if (dailyValue instanceof Double) {
                    values.add((Double)dailyValue);
                }
                else if (dailyValue instanceof DBObject) {
                    DBObject detail = (DBObject) dailyValue;
                    values.add((Double)detail.get("s"));
                }
            }
        }

        if (firstMonth > 0 && firstDay > 0) {
            for (int i = values.size() - 1; i >= 0; i--) {
                if (values.get(i) == null) {
                    values.remove(i);
                }
                else {
                    break;
                }
            }

            startDate.put("month", firstMonth);
            startDate.put("day", firstDay);

            result.put("startDate", startDate);
            result.put("values", values);
        }
        // Prune trailing nulls

        return result;
    }

    @RequestMapping(value = "/vehicle/{vehicleId}/daily-histogram.json", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody String vehicleDailyHistogramData(@PathVariable("vehicleId") String vehicleId) {
        DBObject result = analyticsService.dailyHistogramForVehicle(vehicleId);
        if (result != null) {
            return result.toString();
        }
        else {
            return "{}";
        }
    }

    @RequestMapping(value = "/account/{accountId}/daily-histogram.json", method = {RequestMethod.GET}, produces = "application/json")
    public @ResponseBody String accountDailyHistogramData(@PathVariable("accountId") String accountId) {
        DBObject result = analyticsService.dailyHistogramForAccount(accountId);
        if (result != null) {
            return result.toString();
        }
        else {
            return "{}";
        }
    }



}
