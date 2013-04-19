package com.tripography.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author gscott
 */
@Controller
public class ErrorsController {

    private static final Logger logger = LoggerFactory.getLogger(ErrorsController.class);

    @RequestMapping("/errors/404")
    public String get404() {
        return "errors/404";
    }

    @RequestMapping("/errors/500")
    public String get500(HttpServletRequest request) {
        logger.warn("Error while processing url " + request.getAttribute("javax.servlet.error.request_uri"), (Throwable)request.getAttribute("javax.servlet.error.exception"));
        return "errors/500";
    }

    @RequestMapping("/errors/403")
    public String get403(HttpServletRequest request) {
        return "errors/403";
    }

}
