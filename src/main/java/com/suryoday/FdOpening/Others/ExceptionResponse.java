package com.suryoday.FdOpening.Others;

import java.util.Date;
import java.util.List;

public class ExceptionResponse {

	private String errorMessage;
	private String statusCode;
	//private Date timestamp;
	private List details;

	
	
	public ExceptionResponse(String errorMessage, String statusCode, List details) {
		super();
		this.errorMessage = errorMessage;
		this.statusCode = statusCode;
		this.details = details;
	}


	public ExceptionResponse()
	{
		
	}
	

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

//	public Date getTimestamp() {
//		return timestamp;
//	}
//
//	public void setTimestamp(Date timestamp) {
//		this.timestamp = timestamp;
//	}

	public List getDetails() {
		return details;
	}

	public void setDetails(List details) {
		this.details = details;
	}
	
	
   
	
	
}
