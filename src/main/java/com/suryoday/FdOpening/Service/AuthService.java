package com.suryoday.FdOpening.Service;

public interface AuthService {
	
	public String sendAuth(String request);
	
	public String getJsonRequest(String parent , String uid) throws Exception;

}
