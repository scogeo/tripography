package com.rumbleware.tesla.api;

import com.sun.corba.se.impl.orb.ParserTable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpCookie;
import java.net.URLDecoder;

import static org.junit.Assert.assertEquals;

/**
 * @author gscott
 */

@Ignore
public class TeslaPortalTest {

    private static final Logger logger = LoggerFactory.getLogger(TeslaPortalTest.class);

    private TeslaPortal portal;
    private UsernamePasswordCredentials credentials;
    private VehicleDescriptor vehicle;


    @Before
    public void login() {
        credentials = new UsernamePasswordCredentials("george.scott@gmail.com", "kyrdn3yt3sla");

        portal = new TeslaPortal();

        VehicleDescriptor[] vehicles = portal.vehicles(credentials);

        assertEquals(1, vehicles.length);

        vehicle = vehicles[0];

        /**
         * Vehicle descriptor 4.4:
         * [{"color":null,"display_name":null,"id":137,"option_codes":"MS01,RENA,TM00,DRLH,PF01,BT85,PMMB,RFBC,WT21,IDOG,IZMT,TR00,SU01,SC01,TP01,AU01,CH00,HP00,PA01,PS01,AD02,X024,X001,X003,X007,X011,X013,X021","user_id":152,"vehicle_id":1551198381,"vin":"5YJSA1CP4CFP02817","tokens":["dfb6bc06f1baea0e","fe9b85628978d7f4"],"state":"online"}]
         *
         * Vehicle descriptor 4.5:
         * [{"color":null,"display_name":null,"id":137,"option_codes":"MS01,RENA,TM00,DRLH,PF01,BT85,PMMB,RFBC,WT21,IDOG,IZMT,TR00,SU01,SC01,TP01,AU01,CH00,HP00,PA01,PS01,AD02,X024,X001,X003,X007,X011,X013,X021","user_id":152,"vehicle_id":1551198381,"vin":"5YJSA1CP4CFP02817","tokens":[],"state":"online"}]

         */
    }

    @Ignore
    public void testIllegalAccess() throws Exception {

        // a quick hack to check response codes for illegal access.
        /*
        HttpCookie cookie = credentials.checkLogin();

        String value = cookie.getValue();

        value = value.replace('4', '6');
        CookieCredentials cookieCredentials = new CookieCredentials("george.scott@gmail.com", value);

        DriveState driveState = portal.driveState(cookieCredentials, "137");

*/

    }


    @Test
    public void testDriveState() throws Exception {
        DriveState driveState = portal.driveState(credentials, vehicle.getId());

        /**
         * Drive state 4.4:
         * {"shift_state":null,"speed":null,"latitude":37.391193,"longitude":-122.017005,"heading":14,"gps_as_of":1370020284}
         *
         * drive state 4.5:
         * {"shift_state":null,"speed":null,"latitude":37.391157,"longitude":-122.016988,"heading":198,"gps_as_of":1370023504}
         *
         * if disabled:
         * {"result":false,"reason":"mobile_access_disabled"}
         */
        System.out.println("Drive state is " + driveState);
    }

    @Test
    public void testChargeState() throws Exception {
        ChargeState chargeState = portal.chargeState(credentials, vehicle.getId());

        /**
         * Charge state 4.4:
         * {"charging_state":"Disconnected","charge_to_max_range":false,"max_range_charge_counter":0,"fast_charger_present":false,"battery_range":145.25,"est_battery_range":139.27,"ideal_battery_range":167.17,"battery_level":62,"battery_current":-0.1,"charge_starting_range":null,"charge_starting_soc":null,"charger_voltage":0,"charger_pilot_current":0,"charger_actual_current":0,"charger_power":0,"time_to_full_charge":null,"charge_rate":-1.0,"charge_port_door_open":false,"scheduled_charging_start_time":null,"scheduled_charging_pending":false,"user_charge_enable_request":null,"charge_enable_request":false}
         *
         * Charge state 4.5:
         * {"charging_state":"Disconnected","charge_limit_soc":81,"charge_limit_soc_std":90,"charge_limit_soc_min":50,"charge_limit_soc_max":100,"charge_to_max_range":false,"battery_heater_on":false,"not_enough_power_to_heat":false,"max_range_charge_counter":0,"fast_charger_present":false,"battery_range":142.62,"est_battery_range":135.53,"ideal_battery_range":163.4,"battery_level":62,"battery_current":-0.6,"charge_starting_range":null,"charge_starting_soc":null,"charger_voltage":0,"charger_pilot_current":0,"charger_actual_current":0,"charger_power":0,"time_to_full_charge":null,"charge_rate":-1.0,"charge_port_door_open":false,"scheduled_charging_start_time":null,"scheduled_charging_pending":false,"user_charge_enable_request":null,"charge_enable_request":false}
         */
        System.out.println("Charge state is " + chargeState);
    }

    @Test
    public void testClimateState() throws Exception {
        ClimateState climateState = portal.climateState(credentials, vehicle.getId());
        System.out.println("Climate state is " + climateState);
    }

    @Test
    public void testMobileEnabled() throws Exception {
        CommandResponse response = portal.mobileEnabled(credentials, vehicle.getId());

        /**
         * mobile enabled 4.4:
         *
         * {"reason":"","result":true}
         *
         * mobile enabled 4.5:
         * {"reason":"","result":true}
         *
         * // Mobile disabled in 4.5:
         * {"reason":"customer_setting","result":false}
         */
        System.out.println("Response is " + response);
    }

    @Test
    public void testGetVehicle() throws Exception {
        VehicleDescriptor vd = portal.vehicle(credentials, vehicle.getId());

        System.out.println("vehicle state is " + vd);
    }

    @Test
    public void testVehicleState() throws Exception {
        VehicleState vehicleState = portal.vehicleState(credentials, vehicle.getId());

        /**
         * vehicle state 4.4
         * {"df":false,"dr":false,"pf":false,"pr":false,"ft":false,"rt":false,"car_version":"1.31.11","locked":true,"sun_roof_installed":false,"sun_roof_state":"unknown","sun_roof_percent_open":null,"dark_rims":false,"wheel_type":"Silver21","has_spoiler":false,"roof_color":"Colored","perf_config":"Sport"}
         *
         * vehicle state 4.5
         * {"df":0,"dr":0,"pf":0,"pr":0,"ft":0,"rt":0,"car_version":"1.33.44","locked":true,"sun_roof_installed":false,"sun_roof_state":"unknown","sun_roof_percent_open":null,"dark_rims":false,"wheel_type":"Silver21","has_spoiler":false,"roof_color":"Colored","perf_config":"Sport"}
         */
        System.out.println("State is " + vehicleState);
    }

    @Test
    public void testGuiSettings() throws Exception {
        GuiSettings guiSettings = portal.guiSettings(credentials, vehicle.getId());

        /**
         * gui state 4.4
         *{"gui_distance_units":"mi/hr","gui_temperature_units":"F","gui_charge_rate_units":"mi/hr","gui_24_hour_time":false,"gui_range_display":"Rated"}
         *
         * gui state 4.5
         *
         * {"gui_distance_units":"mi/hr","gui_temperature_units":"F","gui_charge_rate_units":"mi/hr","gui_24_hour_time":false,"gui_range_display":"Rated"}
         *
         * // For metric:
         * {"gui_distance_units":"km/hr","gui_temperature_units":"F","gui_charge_rate_units":"km/hr","gui_24_hour_time":false,"gui_range_display":"Rated"}
         */
        System.out.println("State is " + guiSettings);
    }


}
