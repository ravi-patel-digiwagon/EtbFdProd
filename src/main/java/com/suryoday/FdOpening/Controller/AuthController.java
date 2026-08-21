package com.suryoday.FdOpening.Controller;

import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.FdOpening.Service.AuthService;



@RestController
@RequestMapping("/uidauth")
//@CrossOrigin(origins = {"http://localhost:3000","http://127.0.0.1:3000","https://corporate.suryodaybank.com/suryoday-ntb-fd/","https://corporate.suryodaybank.com/FDOpeningService/fdOpening/ekycSendOtp","https://corporate.suryodaybank.com/suryoday-ntb-fd"})
@CrossOrigin(origins = "*")
public class AuthController {

	@Autowired
	AuthService authService;

	private static Logger logger = LoggerFactory.getLogger(AuthController.class);

	@RequestMapping(value = "/auth", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> authRequest(@RequestBody String parent) throws Exception {

		parent = parent.replaceAll("\t", "");

		logger.debug(parent.toString());
		
		logger.debug("POST Request : " + parent);

		JSONObject request = new JSONObject(parent);

		String uid = request.getJSONObject("kycRequest").getString("uid");
		logger.debug(uid);

		String udc = request.getJSONObject("kycRequest").getString("udc");

		String otp = request.getJSONObject("kycRequest").getString("otp");

		String serviceType = request.getJSONObject("kycRequest").getString("serviceType");

		String isKyc = request.getJSONObject("kycRequest").getString("isKyc");

		String pidxml = request.getJSONObject("kycRequest").getString("pidxml");

		logger.debug(pidxml);
		String authRequest = authService.getJsonRequest(pidxml, uid);
		logger.debug("Main Auth Request : " + authRequest);
		
		logger.debug("Main Auth Request : " + authRequest);

		String sendAuth = authService.sendAuth(authRequest);
		logger.debug("Response From the API : " + sendAuth);

		org.json.JSONObject jsonConverted1 = XML.toJSONObject(sendAuth);
		String response = jsonConverted1.toString();
		logger.debug("XML Data Converted To JSON : " + jsonConverted1);
		logger.debug("JSONString : " + response);

		JSONObject jsonResponse = new JSONObject(response);
		logger.debug(jsonResponse.toString());
		if (!jsonResponse.equals(null)) {
			HttpStatus h = HttpStatus.BAD_GATEWAY;
			if (jsonResponse.has("Response")) {
				h = HttpStatus.OK;
			} else if (jsonResponse.has("Description")) {
				h = HttpStatus.BAD_REQUEST;
			}
			logger.debug("Main Response : " + jsonResponse.toString());
			return new ResponseEntity<>(jsonResponse.toString(), h);
		} else {
			logger.debug("GATEWAY_TIMEOUT");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}

	}

}
