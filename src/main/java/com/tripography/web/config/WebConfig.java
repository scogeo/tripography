package com.tripography.web.config;

import com.rumbleware.web.config.BaseWebAppConfigurationSupport;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gscott
 */
@Configuration
@ComponentScan({"com.tripography.web.controller"})
public class WebConfig extends BaseWebAppConfigurationSupport {
}
