package com.rumbleware.tesla.api;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import com.sun.xml.internal.ws.server.StatefulInstanceResolver;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import java.util.List;

/**
 * @author gscott
 */
public class Portal {

    private final Client restClient;

    private final PortalCredentials credentials;

    public static final String DEFAULT_HOST = "portal.vn.teslamotors.com";
    public static final String DEFAULT_SCHEME = "https";

    public static final String USER_CREDENTIAL_COOKIE = "user_credentials";

    private String host;
    private String protocol;

    private List<NewCookie> loginCookies;

    private final VehicleRequest<VehicleDescriptor> vehicleById = new VehicleRequest<VehicleDescriptor>(VehicleDescriptor.class);

    private final VehiclePathRequest<CommandResponse> mobileEnabled = new VehiclePathRequest<CommandResponse>("mobile_enabled", CommandResponse.class);

    private final VehicleCommand<GuiSettings> guiSettingsCommand = new VehicleCommand<GuiSettings>("gui_settings", GuiSettings.class);
    private final VehicleCommand<DriveState> driveStateCommand = new VehicleCommand<DriveState>("drive_state", DriveState.class);
    private final VehicleCommand<VehicleState> vehicleStateCommand = new VehicleCommand<VehicleState>("vehicle_state", VehicleState.class);
    private final VehicleCommand<ChargeState> chargeStateCommand = new VehicleCommand<ChargeState>("charge_state", ChargeState.class);
    private final VehicleCommand<ClimateState> climateStateCommand = new VehicleCommand<ClimateState>("climate_state", ClimateState.class);


    private final VehicleSimpleCommand honkHornCommand = new VehicleSimpleCommand("honk_horn");
    private final VehicleSimpleCommand wakeupCommand = new VehicleSimpleCommand("wake_up");

    public Portal(PortalCredentials credentials) {
        this(credentials, DEFAULT_HOST, DEFAULT_SCHEME);
    }

    public Portal(PortalCredentials credentials, String host, String protocol) {
        this.credentials = credentials;
        this.host = host;
        this.protocol = protocol;

        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(JacksonJsonProvider.class);

        restClient = Client.create(cc);
        restClient.addFilter(new LoggingFilter(System.out));
    }

    public VehicleDescriptor[] vehicles() {
        WebResource.Builder resource = restClient.resource(protocol + "://" + host + "/vehicles").getRequestBuilder();

        credentials.sign(resource);

        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);


        return response.getEntity(VehicleDescriptor[].class);
    }


    public VehicleDescriptor vehicle(String id) {
        return vehicleById.execute(id);
    }

    public CommandResponse mobileEnabled(String id) {
        return mobileEnabled.execute(id);
    }

    public CommandResponse honkHorn(String id) {
        return honkHornCommand.execute(id);
    }

    public ClimateState climateState(String id) {
        return climateStateCommand.execute(id);
    }

    public ChargeState chargeState(String id) {
        return chargeStateCommand.execute(id);
    }

    public DriveState driveState(final String id) {
        return driveStateCommand.execute(id);
    }

    public VehicleState vehicleState(final String id) {
        return vehicleStateCommand.execute(id);
    }

    public CommandResponse wakeUp(final String id) {
        return wakeupCommand.execute(id);
    }

    public GuiSettings guiSettings(final String id) {
        return guiSettingsCommand.execute(id);
    }

    public class VehicleRequest<Response> {

        protected final Class<Response> responseClass;

        public VehicleRequest(Class<Response> responseClass) {
            this.responseClass = responseClass;
        }

        protected String getUriTemplate(String ...args) {
            return protocol + "://" + host + "/vehicles/" + args[0];
        }

        public Response execute(String id) {
            WebResource.Builder resource = restClient.resource(getUriTemplate(id)).getRequestBuilder();

            credentials.sign(resource);

            ClientResponse response = resource.accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

            System.out.println("response " + response.toString());

            return response.getEntity(responseClass);
        }

    }

    public class VehiclePathRequest<Response> extends VehicleRequest<Response> {

        private String pathName;

        public VehiclePathRequest(String pathName, Class<Response> responseClass) {
            super(responseClass);
            this.pathName = pathName;
        }

        protected String getUriTemplate(String ...args) {
            return super.getUriTemplate(args[0]) + "/" + pathName;
        }

    }

    public class VehicleCommand<Response> extends VehicleRequest<Response> {

        private final String commandName;

        public VehicleCommand(String commandName, Class<Response> responseClass) {
            super(responseClass);
            this.commandName = commandName;
        }

        protected String getUriTemplate(String ...args) {
            return super.getUriTemplate(args[0]) + "/command/" + commandName;
        }

    }

    public class VehicleSimpleCommand extends VehicleCommand<CommandResponse> {

        public VehicleSimpleCommand(String commandName) {
            super(commandName, CommandResponse.class);
        }
    }



}
