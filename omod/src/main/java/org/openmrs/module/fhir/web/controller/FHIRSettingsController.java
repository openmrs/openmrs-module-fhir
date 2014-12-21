package org.openmrs.module.fhir.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class FHIRSettingsController {

    @RequestMapping(value="/module/fhir/settings.form")
    public void showSettings(ModelMap model) {
        //Display settings
    }

}
