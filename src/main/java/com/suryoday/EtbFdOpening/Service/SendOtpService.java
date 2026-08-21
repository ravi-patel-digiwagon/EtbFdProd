package com.suryoday.EtbFdOpening.Service;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.FdOpening;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Pojo.MerchantTCDetails;
import com.suryoday.EtbFdOpening.Pojo.OtpValidation;

@Component
public interface SendOtpService {

	JSONObject sendOtp(String mobileNo, JSONObject header);

	JSONObject validateOTP(String oTP, JSONObject header);

	String saveData(FdOpening fdopening);

	String getSessionId(String mobileNo);

	String getRequestSession(HttpServletRequest req);

	boolean validateSessionId(String x_Session_ID, String mobileNo );

	void deleteAllSessions(String mobileNo, String sessionid);

	FdOpening fetchData(String mobileNo, String type, String status);

	void save(FdOpening fdOpening);

	String saveNewJourney(String mobileNo);

	JSONObject saveNtbFd(String mobileNo, String type, String string);

	void save(FdOpeningNTB fdOpening);

	Optional<OtpValidation> fetchOtpData(String mobileNo);

	void saveValidateData(OtpValidation otpValidation);

	JSONObject sendSms(String mobileNo, JSONObject header);

	JSONObject sendsms(JSONObject jsonObject);

	JSONObject saveConsentDetails(MerchantTCDetails merchantTCDetails) throws Exception;

	JSONObject sendOtpNew(String mobileNo, JSONObject header);

	JSONObject emailOtp(JSONObject jsonObject) throws Exception;


}
