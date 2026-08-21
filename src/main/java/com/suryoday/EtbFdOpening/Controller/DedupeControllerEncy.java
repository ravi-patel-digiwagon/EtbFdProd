package com.suryoday.EtbFdOpening.Controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
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
import com.suryoday.EtbFdOpening.Service.DedupeService;
import com.suryoday.EtbFdOpening.Service.FdOpeningService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class DedupeControllerEncy {
	@Autowired
	DedupeService dedupeservice;
	@Autowired
	SendOtpService otpservice;
	@Autowired
	FdOpeningService fdopeningservice;
	Logger logger = LoggerFactory.getLogger(DedupeControllerEncy.class);

	@RequestMapping(value = "/checkDedupeEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> checkDedupeEncy(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_Correlation_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("checkDedupe start");
		logger.debug("checkDedupe request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "14508");
		Header.put("X-Request-ID", X_Request_ID);

		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
			String panNo = jsonObject.getJSONObject("Data").getString("PanNo");
			JSONObject customerDetails = dedupeservice.checkDedupe(panNo, Header);

			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (customerDetails != null) {
				String Data2 = customerDetails.getString("data");
				logger.debug("data2");
				JSONObject Data1 = new JSONObject(Data2);

				logger.debug(Data1.toString());

				if (Data1.has("Data")) {
					h = HttpStatus.OK;

				} else if (Data1.has("Error")) {
					JSONObject errorJson = Data1.getJSONObject("Error");
					if (errorJson.isEmpty()) {
						h = HttpStatus.OK;
					} else {
						h = HttpStatus.BAD_REQUEST;
					}
					h = HttpStatus.BAD_REQUEST;

				}
				logger.debug("response" + Data1);
				data = Data1.toString();
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

	@RequestMapping(value = "/panCardValidateEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> panCardValidationEncy(@RequestBody String bm,
			@RequestHeader(name = "LgCode") String lgcode,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "S5050");
		Header.put("X-Request-ID", X_Request_ID);
		System.out.println(Header);
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			if (X_Request_ID.equals("COR")) {
				JSONObject jsonObject = new JSONObject(decryptContainerString);
				JSONObject panCardValidate = dedupeservice.panValidation(jsonObject, Header);
				logger.debug("Response from the API" + panCardValidate);
				HttpStatus h = HttpStatus.BAD_GATEWAY;
				if (panCardValidate != null) {
					String Data2 = panCardValidate.getString("data");
					JSONObject Data1 = new JSONObject(Data2);
					// JSONArray jsonArray = Data1.getJSONArray("PANDetails");

					logger.debug("JSON Object from Response" + Data2);

					if (Data1.has("Data")) {
						h = HttpStatus.OK;
						String panNo = Data1.getJSONArray("Data").getJSONObject(0).getJSONArray("OutputData").getJSONObject(0).getString("Pan");
						FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
						fdOpening.setPanNo(panNo);
						logger.debug("lgcode :: "+ lgcode);
						fdOpening.setLgCode(lgcode);
						otpservice.save(fdOpening);
					} else if (Data1.has("Error")) {
						h = HttpStatus.BAD_REQUEST;
					}
					logger.debug("Main Response from API" + Data1.toString());
//					Thread.sleep(20000);
					data = Data1.toString();
					String encryptString2 = Crypt.encrypt(data, X_encode_ID);
					org.json.JSONObject data2 = new org.json.JSONObject();
					data2.put("value", encryptString2);
					org.json.JSONObject data3 = new org.json.JSONObject();
					data3.put("Data", data2);
					logger.debug("response : " + data3.toString());
					return new ResponseEntity<Object>(data3.toString(), h);
				}
			}
			logger.debug("INVALID REQUEST");
			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);
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
