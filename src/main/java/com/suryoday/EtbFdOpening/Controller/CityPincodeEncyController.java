package com.suryoday.EtbFdOpening.Controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.EtbFdOpening.Others.Crypt;
import com.suryoday.EtbFdOpening.Service.CityPincodeService;
import com.suryoday.EtbFdOpening.Service.SendOtpService;

@RestController
@RequestMapping("/fdOpening")
public class CityPincodeEncyController {

	@Autowired
	private CityPincodeService cityPincodeService;

	@Autowired
	SendOtpService otpservice;

	private static Logger logger = LoggerFactory.getLogger(CityPincodeEncyController.class);

	@RequestMapping(value = "/cityPincodeEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> cityPinCode(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
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
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
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
		String pinCode = jsonObject.getJSONObject("Data").getString("PinCode");
		JSONObject cityPinCode = cityPincodeService.getCityPincode(pinCode, Header);
		logger.debug("Response from API", cityPinCode);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (cityPinCode != null) {
			String Data2 = cityPinCode.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);
			logger.debug("JSON Response From API", Data2);

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("Main Response From API", Data1.toString());
			data = Data1.toString();
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", encryptString2);
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), h);


		} else {
			logger.debug("GATEWAY_TIMEOUT");
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
	
	@RequestMapping(value = "/createWorkitemEncy", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createWorkitemEncy(@RequestBody String bm,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Content-Type-Options", required = true) String X_Content_Type_Options,
			@RequestHeader(name = "X-Frame-Options", required = true) String X_Frame_Options,
			@RequestHeader(name = "Content-Security-Policy", required = true) String Content_Security_Policy,
			@RequestHeader(name = "X-XSS-Protection", required = true) String X_XSS_Protection,
			@RequestHeader(name = "Strict-Transport-Security", required = true) String Strict_Transport_Security,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Encode-ID", required = true) String X_encode_ID,
			@RequestHeader(name = "Authorization", required = true) String Authorization,HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("Authorization", Authorization);
		boolean sessionId = otpservice.validateSessionId(X_Session_ID, mobileNo);
		if (sessionId == true) {
			org.json.JSONObject encryptJSONObject = new org.json.JSONObject(bm);
			String encryptString = encryptJSONObject.getJSONObject("Data").getString("value");

			// logger.debug("start request" + bm.toString());

			String key = X_Session_ID;

			String decryptContainerString = Crypt.decrypt(encryptString, X_encode_ID);

			String data = "";
			JSONObject jsonObject = new JSONObject(decryptContainerString);
		JSONObject createWorkitem = cityPincodeService.createWorkitem(jsonObject, Header);
		logger.debug("Response from API", createWorkitem);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createWorkitem != null) {
			String Data2 = createWorkitem.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);
			logger.debug("JSON Response From API", Data2);
			String statusCode = Data1.getString("statusCode");
			if (statusCode.equals("201")||statusCode.equals("200")) {
				h = HttpStatus.OK;

			} else if (statusCode.equals("400")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("Main Response From API", Data1.toString());
			data = Data1.toString();
			String encryptString2 = Crypt.encrypt(data, X_encode_ID);
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", encryptString2);
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Data", data2);
			logger.debug("response : " + data3.toString());
			return new ResponseEntity<Object>(data3.toString(), h);

		} else {
			logger.debug("GATEWAY_TIMEOUT");
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
