/**
 * 
 */
package com.gcit.library.service;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gcit
 *
 */
@RestController
public class ErrorService implements ErrorController{

    private static final String PATH = "/error";

    @RequestMapping(value = PATH)
    public String error() {
        return "The Page you try to access does not exist!";
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
