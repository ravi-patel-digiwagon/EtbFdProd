package com.suryoday.FdOpening.Controller;

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

import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Service.DedupeService;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.SendOtpService;

@Component
@RestController
@RequestMapping(value = "/fdOpening")
public class DedupeController {
	@Autowired
	DedupeService dedupeservice;
	@Autowired
	FdOpeningService fdopeningservice;
	@Autowired
	SendOtpService sendotpservice;
	Logger logger = LoggerFactory.getLogger(DedupeController.class);

	@RequestMapping(value = "/checkDedupe", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> checkDedupe(@RequestBody String bm,
			@RequestHeader(name = "X-Correlation-ID", required = true) String X_Correlation_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("checkDedupe start");
		logger.debug("checkDedupe request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-User-ID", "14508");
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
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
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}

//	@RequestMapping(value = "/panCardValidate", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> panCardValidation(@RequestBody String bm,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_CORRELATION_ID,
//			@RequestHeader(name = "ApplicationNo", required = true) String applicationNo,
//			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
//			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
//			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
//			throws Exception {
//
//		JSONObject Header = new JSONObject();
//		Header.put("X-Correlation-ID", X_CORRELATION_ID);
//		Header.put("X-From-ID", X_From_ID);
//		Header.put("X-Transaction-ID", X_Transaction_ID);
//		Header.put("X-User-ID", "S5050");
//		Header.put("X-Request-ID", X_Request_ID);
//		System.out.println(Header);
//		if (X_Request_ID.equals("IEXCEED")) {
//			JSONObject jsonObject=new JSONObject(bm);
//			String panCardNo = jsonObject.getJSONObject("Data").getString("PanNo");
//			JSONObject panCardValidate = dedupeservice.panCardValidation(panCardNo, Header);
//			logger.debug("Response from the API" + panCardValidate);
//			String panStatus = null;
//			HttpStatus h = HttpStatus.BAD_GATEWAY;
//			if (panCardValidate != null) {
//				String Data2 = panCardValidate.getString("data");
//				JSONObject Data1 = new JSONObject(Data2);
//				// JSONArray jsonArray = Data1.getJSONArray("PANDetails");
//
//				logger.debug("JSON Object from Response" + Data2);
//
//				if (Data1.has("Data")) {
//					JSONObject jsonArray1=null;
//					JSONArray jsonArray = Data1.getJSONObject("Data").getJSONArray("PANDetails");
//					for (int n = 0; n < jsonArray.length(); n++) {
//						 jsonArray1 = jsonArray.getJSONObject(n);
//						panStatus = jsonArray1.getString("Pan-Status");
//						System.out.println(panStatus);
//					}
//					
//					if (panStatus.equals("Record (PAN) Not Found in ITD Database/Invalid PAN")) {
//						h = HttpStatus.BAD_REQUEST;
//						JSONObject error = new JSONObject();
//						JSONObject error1 = new JSONObject();
//						error1.put("Description", panStatus);
//						error1.put("status", h);
//						error.put("Error", error1);
//						return new ResponseEntity<Object>(error.toString(), h);
//					}
//					h = HttpStatus.OK;
//					FdOpeningNTB fdOpening= fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
//					fdOpening.setPanNo(panCardNo);
//					sendotpservice.save(fdOpening);
//				logger.debug("Main Response from API" + Data1.toString());
//				return new ResponseEntity<Object>(Data1.toString(), h);
//				}
//			} else {
//				logger.debug("GATEWAY_TIMEOUT");
//				return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
//			}
//		} 
//			logger.debug("INVALID REQUEST");
//			return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);
//
//	}

	@RequestMapping(value = "/panCardValidate", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> panValidate(@RequestBody String bm,
			@RequestHeader(name = "Content-Type", required = true) String contrnt_Type,
			@RequestHeader(name = "LgCode",required = false) String lgcode, @RequestHeader(name = "ApplicationNo",required = false) String applicationNo,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		System.out.println(Header);
		if (X_Request_ID.equals("COR")) {
			JSONObject jsonObject = new JSONObject(bm);
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
					String panNo = Data1.getJSONArray("Data").getJSONObject(0).getJSONArray("OutputData")
							.getJSONObject(0).getString("Pan");
					if (applicationNo != null && !applicationNo.isEmpty()) {
						FdOpeningNTB fdOpening = fdopeningservice.fetchByApplicationNo(Long.parseLong(applicationNo));
						fdOpening.setPanNo(panNo);
						logger.debug("lgcode :: " + lgcode);
						fdOpening.setLgCode(lgcode);
						sendotpservice.save(fdOpening);
					}
				} else if (Data1.has("Error")) {
					h = HttpStatus.BAD_REQUEST;
				}
				logger.debug("Main Response from API" + Data1.toString());
//				Thread.sleep(20000);
				return new ResponseEntity<Object>(Data1.toString(), h);
			}
		}
		logger.debug("INVALID REQUEST");
		return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

	}

	@RequestMapping(value = "/aadharPanLinkStatus", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> aadharPanLinkStatus(@RequestBody String bm,
			@RequestHeader(name = "Content-Type", required = true) String contrnt_Type,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {

		JSONObject Header = new JSONObject();
		Header.put("X-Request-ID", X_Request_ID);
		System.out.println(Header);

		JSONObject jsonObject = new JSONObject(bm);
		JSONObject panCardValidate = dedupeservice.aadharPanLinkStatus(jsonObject, Header);
		logger.debug("Response from the API" + panCardValidate);
		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (panCardValidate != null) {
			String Data2 = panCardValidate.getString("data");
			JSONObject Data1 = new JSONObject(Data2);
			// JSONArray jsonArray = Data1.getJSONArray("PANDetails");

			logger.debug("JSON Object from Response" + Data2);

			if (Data1.has("Data")) {
				h = HttpStatus.OK;
			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;
			}
			logger.debug("Main Response from API" + Data1.toString());
//				Thread.sleep(20000);
			return new ResponseEntity<Object>(Data1.toString(), h);
		}

		logger.debug("INVALID REQUEST");
		return new ResponseEntity<Object>("Invalid Request ", HttpStatus.BAD_REQUEST);

	}
}
