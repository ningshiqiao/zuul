package com.ly.config;

import com.ly.helper.AppException;
import com.ly.helper.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public Result defaultErrorHandler(HttpServletRequest req, Exception exception) throws Exception {
        logger.info("=========Exception[begin]==========");
        StringBuilder sb = new StringBuilder(req.getRequestURL().toString());
        if (exception instanceof AppException) {
            AppException e = (AppException) exception;
            sb.append("  CODE:");
            sb.append(e.getErrorCode().getCode().toString());
            sb.append("  MESSAGE:");
            sb.append(e.getErrorCode().getMessage().toString());
            logger.error(sb.toString());
            return new Result(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } else {
            sb.append("  CODE:");
            sb.append("");
            sb.append("  MESSAGE:");
            sb.append(exception.getMessage());
            logger.error(sb.toString());
            return new Result(Result.Status.ERROR,"",exception.getMessage());
        }
    }

}

