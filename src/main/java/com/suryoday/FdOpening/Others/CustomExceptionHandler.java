package com.suryoday.FdOpening.Others;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
 
@SuppressWarnings({"unchecked","rawtypes"})
@ControllerAdvice
public class CustomExceptionHandler {
	 @ExceptionHandler(ServletRequestBindingException.class)
	    public final ResponseEntity<Object> handleHeaderException(Exception ex, WebRequest request) 
	    {
	        List<String> details = new ArrayList<>();
	        details.add(ex.getLocalizedMessage());
	        ExceptionResponse error = new ExceptionResponse("400","Bad Request", details);
	        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
	    }
	     
	    @ExceptionHandler(Exception.class)
	    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) 
	    {
	        List<String> details = new ArrayList<>();
	        details.add(ex.getLocalizedMessage());
	        ExceptionResponse error = new ExceptionResponse("500","Server Error", details);
	        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}