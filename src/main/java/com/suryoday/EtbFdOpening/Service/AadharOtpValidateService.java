package com.suryoday.EtbFdOpening.Service;

import org.springframework.stereotype.Component;

@Component
public interface AadharOtpValidateService {

	String getXmlRequest(String otp, String aadharNo, String uKC) throws Exception;

	String decryptString(String hsmData);

	String sendEkyc(String xmlRequest);

}
