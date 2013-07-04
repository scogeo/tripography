package com.tripography.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * This Controller handles all of the static content on the site, it should really be implemented and driven
 * from a configuration file and directly load markdown templates, but works for now.
 *
 * @author gscott
 */
@Controller
public class StaticContentController {

    @RequestMapping(value = {"/legal", "/legal/"}, method = RequestMethod.GET)
    public String getLegal() {
        return "redirect:/legal/terms";
    }

    @RequestMapping(value = {"/legal/terms"}, method = RequestMethod.GET)
    public String getLegalTerms() {
        return "legal/terms";
    }

    @RequestMapping(value = "/legal/privacy", method = RequestMethod.GET)
    public String getLegalPrivacy() {
        return "legal/privacy";
    }

    @RequestMapping(value = {"/about", "/about/"}, method = RequestMethod.GET)
    public String getAbout() {
        return "redirect:/about/contact";
    }

    @RequestMapping(value = "/about/contact", method = RequestMethod.GET)
    public String getContactInfo() {
        return "about/contact";
    }

}
