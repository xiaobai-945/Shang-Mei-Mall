package red.mlz.console.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import red.mlz.common.utils.Response;

@ControllerAdvice
@Slf4j
public class ConsoleGlobalControllerAdvice {
    @ExceptionHandler(Exception.class)
    @ResponseBody
    Response handleControllerException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return new Response(4004, exception.getStackTrace());
    }

}
