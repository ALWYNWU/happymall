package com.happymall.product.exception;

import com.common.exception.BizCodeEnume;
import com.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

/**
 * @author Yilong
 * @date 2022-10-01 6:12 p.m.
 * @description
 */

/**
 * Handle all exception
 */
@Slf4j

//@ResponseBody
//@ControllerAdvice(basePackages = "com.happymall.product.controller")

/**
 * Convert java object to JSON and write it into HttpResponse
 * base package means it will handle the exception under the package
 * @RestControllerAdvice (@ResponseBody + @ControllerAdvice)
 */
@RestControllerAdvice(basePackages = "com.happymall.product.controller")
public class HappymallExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("Data valid error {} {}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        HashMap<String, String> errorMap = new HashMap<>();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnume.VALID_EXCEPTION.getCode(), BizCodeEnume.VALID_EXCEPTION.getMsg()).put("data", errorMap);
    }

    @ExceptionHandler(value = Exception.class)
    public R handleException(Throwable e){
        return R.error(BizCodeEnume.UNKNOWN_EXCEPTION.getCode(),BizCodeEnume.UNKNOWN_EXCEPTION.getMsg());
    }

}
