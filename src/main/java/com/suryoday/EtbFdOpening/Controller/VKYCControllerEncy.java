package com.suryoday.EtbFdOpening.Controller;

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

import com.suryoday.EtbFdOpening.Others.Crypt;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;
import com.suryoday.EtbFdOpening.Service.VKYCService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class VKYCControllerEncy {
	Logger logger = LoggerFactory.getLogger(VKYCControllerEncy.class);
	@Autowired
	VKYCService vkycservice;
	@Autowired
	SendOtpService otpservice;
	@Autowired
	FdOpeningService fdservice;

	@RequestMapping(value = "/getVkycDetailsEncyNew", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getHyperVergeVkycDetails(@RequestBody String bm,
			@RequestHeader(name = "client-id", required = true) String clientId,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "timestamp", required = true) String timestamp, HttpServletRequest req)
			throws Exception {
		logger.debug("getHyperVergeVkycDetails start");
		logger.debug("getHyperVergeVkycDetails request" + bm);
		JSONObject Header = new JSONObject();

		Header.put("timestamp", timestamp);
		Header.put("client-id", clientId);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");

			JSONObject getVkycDetails = vkycservice.getHyperVergeVkycDetails(trackingId, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (getVkycDetails != null) {
				String Data2 = getVkycDetails.getString("data");
				logger.debug("data2");
				System.out.println("Data2" + Data2);
				if (Data2.startsWith("{")) {
					JSONObject Data1 = new JSONObject(Data2);
					h = HttpStatus.OK;
					logger.debug(Data1.toString());
					data = Data1.toString();
					String encryptString2 = Crypt.encrypt(data, X_encode_ID);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);
				}
				data = Data2;
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("getHyperVergeVkycDetails response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {
				logger.debug("timeout");
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

	@RequestMapping(value = "/createVkycEncyNew", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createHyperVergeVkyc(@RequestBody String bm,
			@RequestHeader(name = "client-id", required = true) String clientId,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "ApplicationNo", required = true) String ApplicationNo,
			@RequestHeader(name = "timestamp", required = true) String timestamp, HttpServletRequest req)
			throws Exception {
		logger.debug("createVkyc start");
		logger.debug("createVkyc request" + bm);
		JSONObject Header = new JSONObject();

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
			logger.debug("createHyperVergeVkyc NEW request :: " + decryptContainerString);
			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);

			FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(ApplicationNo));
			fdOpening.setVkycReq(jsonObject.toString());

			logger.debug("createHyperVergeVkyc NEW request start");
			JSONObject createVkyc = vkycservice.createHyperVergeVkyc(jsonObject, Header);
			logger.debug("createHyperVergeVkyc RES :: {}", createVkyc.toString());
			logger.debug("createHyperVergeVkyc NEW request end");

			HttpStatus h = HttpStatus.BAD_REQUEST;
			if (createVkyc != null) {
				String Data2 = createVkyc.getString("data");
				logger.debug("createHyperVergeVkyc Data2 " + Data2);
				if (Data2.startsWith("{")) {
					JSONObject Data1 = new JSONObject(Data2);
					h = HttpStatus.OK;
					logger.debug("createHyperVergeVkyc Data 1 :: {}", Data1.toString());
//					String trackingId = Data1.getString("trackingId");

					String trackingId = jsonObject.getString("userId");
					logger.debug("createVkycEncy trackingId :: {}", trackingId);
					fdOpening.setVkycResp(Data1.toString());
					fdOpening.setVkycTrackingId(trackingId);
					fdOpening.setVkycStatus("Initiated");
					fdOpening.setIsVkycDone("N");
					otpservice.save(fdOpening);
					data = Data1.toString();
					String encryptString2 = Crypt.encrypt(data, X_encode_ID);
					JSONObject data2 = new JSONObject();
					data2.put("value", encryptString2);
					JSONObject data3 = new JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);
				}
				fdOpening.setVkycResp(Data2);
				fdOpening.setIsVkycDone("N");
				otpservice.save(fdOpening);
				data = Data2;
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				JSONObject data2 = new JSONObject();
				data2.put("value", encryptString2);
				JSONObject data3 = new JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			JSONObject data2 = new JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			JSONObject data3 = new JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}

	@RequestMapping(value = "/getVkycDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> getVkycDetails(@RequestBody String bm,
			@RequestHeader(name = "client-id", required = true) String clientId,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "timestamp", required = true) String timestamp, HttpServletRequest req)
			throws Exception {
		logger.debug("getCustomerDetails start");
		logger.debug("getCustomerDetails request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("timestamp", timestamp);
		Header.put("client-id", clientId);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");
			JSONObject getVkycDetails = vkycservice.getVkycDetails(trackingId, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (getVkycDetails != null) {
				String Data2 = getVkycDetails.getString("data");
				logger.debug("data2");
				System.out.println("Data2" + Data2);
				if (Data2.startsWith("{")) {
					JSONObject Data1 = new JSONObject(Data2);
					h = HttpStatus.OK;
					logger.debug(Data1.toString());
					data = Data1.toString();
					String encryptString2 = Crypt.encrypt(data, X_encode_ID);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);
				}
				data = Data2;
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				org.json.JSONObject data2 = new org.json.JSONObject();
				data2.put("value", encryptString2);
				org.json.JSONObject data3 = new org.json.JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {
				logger.debug("timeout");
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

	@RequestMapping(value = "/createVkycEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createVkyc(@RequestBody String bm,
			@RequestHeader(name = "client-id", required = true) String clientId,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "ApplicationNo", required = true) String ApplicationNo,
			@RequestHeader(name = "timestamp", required = true) String timestamp, HttpServletRequest req)
			throws Exception {
		logger.debug("createVkyc start");
		logger.debug("createVkyc request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("timestamp", timestamp);
		Header.put("client-id", clientId);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
			logger.debug("createVkycEncy request :: " + decryptContainerString);
			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
//		String trackingId = jsonObject.getJSONObject("Data").getString("TrackingId");
			FdOpeningNTB fdOpening = fdservice.fetchByApplicationNo(Long.parseLong(ApplicationNo));
			fdOpening.setVkycReq(jsonObject.toString());
			JSONObject createVkyc = vkycservice.createVkyc(jsonObject, Header);

			HttpStatus h = HttpStatus.BAD_REQUEST;
			if (createVkyc != null) {
				String Data2 = createVkyc.getString("data");
				logger.debug("createVkycEncy Data2 " + Data2);
				if (Data2.startsWith("{")) {
					JSONObject Data1 = new JSONObject(Data2);
					h = HttpStatus.OK;
					logger.debug(Data1.toString());
					String trackingId = Data1.getString("trackingId");
					fdOpening.setVkycResp(Data1.toString());
					fdOpening.setVkycTrackingId(trackingId);
					fdOpening.setVkycStatus("Initiated");
					fdOpening.setIsVkycDone("N");
					otpservice.save(fdOpening);
					data = Data1.toString();
					String encryptString2 = Crypt.encrypt(data, X_encode_ID);
					JSONObject data2 = new JSONObject();
					data2.put("value", encryptString2);
					JSONObject data3 = new JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);
				}
				fdOpening.setVkycResp(Data2);
				fdOpening.setIsVkycDone("N");
				otpservice.save(fdOpening);
				data = Data2;
				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
				JSONObject data2 = new JSONObject();
				data2.put("value", encryptString2);
				JSONObject data3 = new JSONObject();
				data3.put("Data", data2);
				logger.debug("response : " + data3.toString());
				return new ResponseEntity<Object>(data3.toString(), h);

			} else {
				logger.debug("timeout");
				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
			}
		} else {
			JSONObject data2 = new JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			JSONObject data3 = new JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}
	}
}
