package com.suryoday.FdOpening.Controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.FdOpening.Service.CityPincodeService;

@RestController
@RequestMapping("/fdOpening")
public class CityPincodeController {

	@Autowired
	private CityPincodeService cityPincodeService;

	private static Logger logger = LoggerFactory.getLogger(CityPincodeController.class);

	@RequestMapping(value = "/cityPincode", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> cityPinCode(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Correlation-ID", X_CORRELATION_ID);
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-To-ID", X_To_ID);
		Header.put("X-Transaction-ID", X_Transaction_ID);
		Header.put("X-User-ID", "14508");
		Header.put("X-Request-ID", X_Request_ID);
		JSONObject jsonObject = new JSONObject(bm);
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
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("GATEWAY_TIMEOUT");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

	@RequestMapping(value = "/createWorkitem", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createWorkitem(@RequestBody String bm,
			@RequestHeader(name = "Content-Type", required = true) String Content_Type,
			@RequestHeader(name = "Authorization", required = true) String Authorization,HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("Authorization", Authorization);
		JSONObject jsonObject = new JSONObject(bm);
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
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("GATEWAY_TIMEOUT");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

}
