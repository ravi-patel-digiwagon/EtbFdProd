package com.suryoday.EtbFdOpening.Controller;

import java.time.LocalDateTime;

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

import com.suryoday.EtbFdOpening.Others.Crypt;
import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.RazorPayService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@Component
@RequestMapping(value = "/fdOpening")
public class RazorPayControllerEncy {
	@Autowired
	RazorPayService razorPayService;
	@Autowired
	SendOtpService otpservice;
	@Autowired
	FdOpeningService fdOpeningService;
	private static Logger logger = LoggerFactory.getLogger(RazorPayControllerEncy.class);
//
//	@RequestMapping(value = "/sendPaymentLinkEncy", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> disbursement(@RequestBody String bm,
//			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
//			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
//			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
//			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
//			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
//			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
//			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
//			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
//			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
//			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
//			throws Exception {
//
//		JSONObject Header = new JSONObject();
//		Header.put("X-Request-ID", X_Request_ID);
//
//		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
//		if (sessionId == true) {
//			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
//			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");
//
//			// logger.debug("start request" + bm.toString());
//
//			String key = X_Session_ID;
//
//			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
//
//			String data = "";
//			JSONObject jsonObject = new JSONObject(decryptContainerString);
//			System.out.println("Req" + jsonObject);
//			JSONObject sendPaymentLink = razorPayService.sendPaymentLink(jsonObject, Header);
//
//			HttpStatus h = HttpStatus.BAD_GATEWAY;
//			if (sendPaymentLink != null) {
//				String Data2 = sendPaymentLink.getString("data");
//				logger.debug("data2");
//				logger.debug(Data2);
//				JSONObject Data1 = new JSONObject(Data2);
//				if (Data1.has("Data")) {
//					h = HttpStatus.OK;
//
//					return new ResponseEntity<Object>(Data1.toString(), h);
//				} else if (Data1.has("Errors")) {
//					h = HttpStatus.BAD_REQUEST;
//
//				}
//
//				data = Data1.toString();
//				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
//				org.json.JSONObject data2 = new org.json.JSONObject();
//				data2.put("value", encryptString2);
//				org.json.JSONObject data3 = new org.json.JSONObject();
//				data3.put("Data", data2);
//				logger.debug("response : " + data3.toString());
//				return new ResponseEntity<Object>(data3.toString(), h);
//
//			} else {
//
//				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
//			}
//		} else {
//			org.json.JSONObject data2 = new org.json.JSONObject();
//			data2.put("value", "SessionId is expired or Invalid sessionId");
//			org.json.JSONObject data3 = new org.json.JSONObject();
//			data3.put("Error", data2);
//			logger.debug("SessionId is expired or Invalid sessionId");
//			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
//		}
//	}
//
//	@RequestMapping(value = "/fetchPaymentLinkEncy", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> fetchPaymentLink(@RequestBody String bm,
//			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
//			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
//			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
//			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
//			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
//			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
//			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
//			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
//			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_Correlation_ID, HttpServletRequest req)
//			throws Exception {
//
//		JSONObject Header = new JSONObject();
//		Header.put("X-Request-ID", X_Request_ID);
//		Header.put("X-Correlation-ID", X_Correlation_ID);
//		Header.put("X-User-ID", "S7013");
//		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
//		if (sessionId == true) {
//			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
//			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");
//
//			// logger.debug("start request" + bm.toString());
//
//			String key = X_Session_ID;
//
//			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);
//
//			String data = "";
//			JSONObject jsonObject = new JSONObject(decryptContainerString);
//			String orderId = jsonObject.getJSONObject("Data").getString("OrderId");
//			JSONObject fetchPaymentLink = razorPayService.fetchPaymentLink(orderId, Header);
//
//			HttpStatus h = HttpStatus.BAD_GATEWAY;
//			if (fetchPaymentLink != null) {
//				String Data2 = fetchPaymentLink.getString("data");
//				logger.debug("data2");
//				logger.debug(Data2);
//				JSONObject Data1 = new JSONObject(Data2);
//				if (Data1.has("Data")) {
//					h = HttpStatus.OK;
//
//					return new ResponseEntity<Object>(Data1.toString(), h);
//				} else if (Data1.has("Error")) {
//					h = HttpStatus.BAD_REQUEST;
//				}
//				data = Data1.toString();
//				String encryptString2 = Crypt.encrypt(data, X_encode_ID);
//				org.json.JSONObject data2 = new org.json.JSONObject();
//				data2.put("value", encryptString2);
//				org.json.JSONObject data3 = new org.json.JSONObject();
//				data3.put("Data", data2);
//				logger.debug("response : " + data3.toString());
//				return new ResponseEntity<Object>(data3.toString(), h);
//			} else {
//				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
//			}
//		} else {
//			org.json.JSONObject data2 = new org.json.JSONObject();
//			data2.put("value", "SessionId is expired or Invalid sessionId");
//			org.json.JSONObject data3 = new org.json.JSONObject();
//			data3.put("Error", data2);
//			logger.debug("SessionId is expired or Invalid sessionId");
//			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
//		}
//	}
//
	@RequestMapping(value = "/payuDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> payuDetails(@RequestBody String bm,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "Content-Type", required = true) String ContentType,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID, HttpServletRequest req)
			throws Exception {
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {

			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			logger.debug("payuDetailsEncy Req" + jsonObject);
			JSONObject payuDetails = razorPayService.payuDetails(jsonObject);
			logger.debug("payuDetailsEncy Resp" + payuDetails);
			FdOpeningNTB fdOpening= fdOpeningService.fetchByApplicationNo(Long.parseLong(jsonObject.getJSONObject("Data").getString("ApplicationNo")));
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (payuDetails != null) {
				String Data2 = payuDetails.getString("data");
				logger.debug("data2");
				logger.debug(Data2);
				JSONObject Data1 = new JSONObject(Data2);
				if (Data1.has("Data")) {
					h = HttpStatus.OK;
					String status = Data1.getJSONObject("Data").getJSONObject("TransactionDetails").getString("Status");
					//RAVI PAY
					if(status.equals("success"))
					{
						String mihPayid = Data1.getJSONObject("Data").getJSONObject("TransactionDetails").getString("MihPayid");
						if(fdOpening.getIsPaymentDone()==null || fdOpening.getIsPaymentDone().equals("N"))
						{
						fdOpening.setPaymentDate(LocalDateTime.now());
						}
						fdOpening.setIsPaymentDone("Y");//payuDetailsEncy NTB
						fdOpening.setMihPayid(mihPayid);
					}
					else
					{
						fdOpening.setIsPaymentDone("N");//payuDetailsEncy
					}
					otpservice.save(fdOpening);

				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
					fdOpening.setIsPaymentDone("N");//payuDetailsEncy
					otpservice.save(fdOpening);
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

	@RequestMapping(value = "/savePayuDetailsEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> savePayuDetailsEncy(@RequestBody String bm,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "Content-Type", required = true) String ContentType,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID, HttpServletRequest req)
			throws Exception {

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {

			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			logger.debug("savePayuDetailsEncy Req :: " + jsonObject);
			JSONObject response = razorPayService.savePayuDetails(jsonObject);
			data = response.toString();
			logger.debug("savePayuDetailsEncy response :: " + data);
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", encryptString2);
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
		} else {
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "SessionId is expired or Invalid sessionId");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);
			logger.debug("SessionId is expired or Invalid sessionId");
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.UNAUTHORIZED);
		}

	}

	@RequestMapping(value = "/savePayuDetailsEtbEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> savePayuDetailsEtbEncy(@RequestBody String bm,
	                                                     @RequestHeader(name = "MobileNo", required = true) String mobileNo,
	                                                     @RequestHeader(name = "Content-Type", required = true) String ContentType,
	                                                     @RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
	                                                     @RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID, HttpServletRequest req)
			throws Exception {

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {

			JSONObject encryptJSONObject = new JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			logger.debug("savePayuDetailsEtbEncy Req :: " + jsonObject);
			JSONObject response = razorPayService.savePayuDetailsEtb(jsonObject,mobileNo,X_Session_ID);
			data = response.toString();
			logger.debug("savePayuDetailsEtbEncy response :: " + data);
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			JSONObject data2 = new JSONObject();
			data2.put("value", encryptString2);
			JSONObject data3 = new JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), HttpStatus.OK);
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
