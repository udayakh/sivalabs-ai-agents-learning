package dev.udaylabs.reviewbuddy.config;

import dev.udaylabs.reviewbuddy.shared.BadRequestException;
import dev.udaylabs.reviewbuddy.shared.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/** Renders friendly error pages instead of stack traces. */
@ControllerAdvice
class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    ModelAndView handleNotFound(ResourceNotFoundException e) {
        log.debug("Resource not found: {}", e.getMessage());
        return errorView("error/404", HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    ModelAndView handleBadRequest(BadRequestException e) {
        log.debug("Bad request: {}", e.getMessage());
        return errorView("error/400", HttpStatus.BAD_REQUEST, e.getMessage());
    }

    private static ModelAndView errorView(String view, HttpStatus status, String message) {
        ModelAndView mav = new ModelAndView(view);
        mav.setStatus(status);
        mav.addObject("message", message);
        return mav;
    }
}
