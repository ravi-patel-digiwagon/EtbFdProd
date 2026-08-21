package com.suryoday.FdOpening.Others;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;


@ControllerAdvice
public class ExceptionHandlerControllerAdvice {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody ExceptionResponse handleResourceNotFound(final ResourceNotFoundException ex,
			final HttpServletRequest request) {
		String errorMessageDescription =   ex.getLocalizedMessage();
		if(errorMessageDescription==null) errorMessageDescription = ex.toString();
		
		ExceptionResponse error = new ExceptionResponse();
		//error.setTimestamp(new Date());
		error.setStatusCode(""+HttpStatus.BAD_REQUEST);
		error.setErrorMessage(errorMessageDescription);

		return error;
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody ExceptionResponse handleException(final Exception exception,
			final HttpServletRequest request) {

		ExceptionResponse error = new ExceptionResponse();
	//	error.setTimestamp(new Date());
		
		error.setStatusCode(""+HttpStatus.INTERNAL_SERVER_ERROR);
		error.setErrorMessage("INTERNAL_SERVER_ERROR");

		return error;
	}
	
	
	@ExceptionHandler(value = {NullPointerException.class})
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleAnyException(NullPointerException ex ,WebRequest wb)
	{
		
		String errorMessageDescription =   ex.getLocalizedMessage();
		if(errorMessageDescription==null) errorMessageDescription = ex.toString();
		
		
		ExceptionResponse error = new ExceptionResponse();
	//	error.setTimestamp(new Date());
		error.setStatusCode(""+HttpStatus.INTERNAL_SERVER_ERROR);
		error.setErrorMessage(errorMessageDescription);
		
	   return new ResponseEntity<>(error,new HttpHeaders(),HttpStatus.INTERNAL_SERVER_ERROR);	
	}
	

}
