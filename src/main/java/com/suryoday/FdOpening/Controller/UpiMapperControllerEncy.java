package com.suryoday.FdOpening.Controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.FdOpening.Others.Crypt;
import com.suryoday.FdOpening.Pojo.FdOpening;
import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.FdRecieptService;
import com.suryoday.FdOpening.Service.SendOtpService;
import com.suryoday.FdOpening.Service.UpiMapperService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class UpiMapperControllerEncy {
	Logger logger = LoggerFactory.getLogger(UpiMapperControllerEncy.class);
	@Autowired
	SendOtpService otpservice;
	@Autowired
	UpiMapperService upimapperservice;
	@Autowired
	FdRecieptService fdetbservice;
	@Autowired
	FdOpeningService fdservice;
	@RequestMapping(value = "/upiMapperEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> upiMapper(@RequestBody String bm,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
			logger.debug("upiMapperEncy :: " + decryptContainerString);
			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String MobileNo = jsonObject.getJSONObject("Data").getString("MobileNo");
			JSONObject request = new JSONObject();
			JSONObject Data = new JSONObject();
			Data.put("ClientReferenceId", "2023092109062712581821");
			Data.put("MerchantId", "MER0000000009631");
			Data.put("MerchantVpa", "suryodayav@suryoday");
//			Comment on 20260223 With reference with rushali Email  
//			Data.put("MerchantId", "MER0000000000002");
//			Data.put("MerchantVpa", "bhavanimedicalstore@suryoday");
			Data.put("UPINumber", MobileNo);
			request.put("Data", Data);
			JSONObject upiMapper = upimapperservice.upiMapper(request, Header);
//		JSONObject upiMapper=null;
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (upiMapper != null) {
//			String upaName ="dfsdfs";
				String Data2 = upiMapper.getString("data");
				JSONObject Data1 = new JSONObject(Data2);
				if (Data1.has("Data")) {
					h = HttpStatus.OK;
				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
				}
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {

				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/paymentVpaEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> paymentVpa(@RequestBody String bm,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			JSONObject paymentVpa = upimapperservice.paymentVpa(jsonObject, Header);
			FdOpeningNTB fdOpening= fdservice.fetchByApplicationNo(Long.parseLong(applicationNo));
			logger.debug("paymentVpaEncy setVerifyUpiReq ::  "+ jsonObject.toString());
			fdOpening.setVerifyUpiReq(jsonObject.toString());
//		JSONObject upiMapper=null;
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (paymentVpa != null) {
//				String upaName ="dfsdfs";
				String Data2 = paymentVpa.getString("data");
				JSONObject Data1 = new JSONObject(Data2);
				System.out.println(Data1);
				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					fdOpening.setIsUpiVerify("Y");
					fdOpening.setUpiId(jsonObject.getJSONObject("Data").getString("CustomerVPA"));
					fdOpening.setAccountNo(Data1.getJSONObject("Data").getString("PayeeAccountNumber"));
					fdOpening.setIfsc(Data1.getJSONObject("Data").getString("IFSC"));
				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
					fdOpening.setIsUpiVerify("N");
				}
				fdOpening.setVerifyUpiResp(Data1.toString());
				logger.debug("paymentVpaEncy setVerifyUpiResp ::  "+ Data1.toString());
				otpservice.save(fdOpening);
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {

				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}
	
	@RequestMapping(value = "/paymentVpaETBEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> paymentVpaETBEncy(@RequestBody String bm,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			JSONObject paymentVpa = upimapperservice.paymentVpa(jsonObject, Header);
			FdOpening fdOpening=fdetbservice.fetchByMobNoAndSessionId(mobileNo,X_Session_ID);
			fdOpening.setVerifyUpiReq(jsonObject.toString());
//		JSONObject upiMapper=null;
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (paymentVpa != null) {
//				String upaName ="dfsdfs";
				String Data2 = paymentVpa.getString("data");
				JSONObject Data1 = new JSONObject(Data2);
				System.out.println(Data1);
				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					fdOpening.setIsUpiVerify("Y");
					fdOpening.setUpiId(jsonObject.getJSONObject("Data").getString("CustomerVPA"));
				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
					fdOpening.setIsUpiVerify("N");
				}
				fdOpening.setVerifyUpiResp(Data1.toString());
				otpservice.save(fdOpening);
				data = Data1.toString();
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {

				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}
}
