package com.suryoday.FdOpening.Controller;

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

import com.suryoday.FdOpening.Pojo.FdOpeningNTB;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.RazorPayService;
import com.suryoday.FdOpening.Service.SendOtpService;

//@Component
//@RequestMapping(value = "/fdOpening")
//public class RazorPayController {
//	@Autowired
//	RazorPayService razorPayService;
//	@Autowired
//	FdOpeningService fdOpeningService;
//	@Autowired
//	SendOtpService sendOtpService;
//	private static Logger logger = LoggerFactory.getLogger(RazorPayController.class);
//
//	@RequestMapping(value = "/sendPaymentLink", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> disbursement(@RequestBody String bm,
//			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
//			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
//			throws Exception {
//
//		JSONObject Header = new JSONObject();
//		Header.put("X-Request-ID", X_Request_ID);
//
//		JSONObject jsonObject = new JSONObject(bm);
//		System.out.println("Req" + jsonObject);
//		JSONObject sendPaymentLink = razorPayService.sendPaymentLink(jsonObject, Header);
//
//		HttpStatus h = HttpStatus.BAD_GATEWAY;
//		if (sendPaymentLink != null) {
//			String Data2 = sendPaymentLink.getString("data");
//			logger.debug("data2");
//			logger.debug(Data2);
//			JSONObject Data1 = new JSONObject(Data2);
//			if (Data1.has("Data")) {
//				h = HttpStatus.OK;
//
//				return new ResponseEntity<Object>(Data1.toString(), h);
//			} else if (Data1.has("Errors")) {
//				h = HttpStatus.BAD_REQUEST;
//
//			}
//
//			return new ResponseEntity<Object>(Data1.toString(), h);
//
//		} else {
//
//			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
//		}
//	}
//
//	@RequestMapping(value = "/fetchPaymentLink", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> fetchPaymentLink(@RequestBody String bm,
//			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID,
//			@RequestHeader(name = "X-Correlation-ID", required = true) String X_Correlation_ID, HttpServletRequest req)
//			throws Exception {
//
//		JSONObject Header = new JSONObject();
//		Header.put("X-Request-ID", X_Request_ID);
//		Header.put("X-Correlation-ID", X_Correlation_ID);
//		Header.put("X-User-ID", "S7013");
//		JSONObject jsonObject = new JSONObject(bm);
//		String orderId = jsonObject.getJSONObject("Data").getString("OrderId");
//		JSONObject fetchPaymentLink = razorPayService.fetchPaymentLink(orderId, Header);
//
//		HttpStatus h = HttpStatus.BAD_GATEWAY;
//		if (fetchPaymentLink != null) {
//			String Data2 = fetchPaymentLink.getString("data");
//			logger.debug("data2");
//			logger.debug(Data2);
//			JSONObject Data1 = new JSONObject(Data2);
//			if (Data1.has("Data")) {
//				h = HttpStatus.OK;
//
//				return new ResponseEntity<Object>(Data1.toString(), h);
//			} else if (Data1.has("Error")) {
//				h = HttpStatus.BAD_REQUEST;
//			}
//			return new ResponseEntity<Object>(Data1.toString(), h);
//		} else {
//			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
//		}
//	}
//
//	@RequestMapping(value = "/payuDetails", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> payuDetails(@RequestBody String bm,
//			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
//			throws Exception {
//
//		JSONObject jsonObject = new JSONObject(bm);
//		System.out.println("Req" + jsonObject);
//		JSONObject payuDetails = razorPayService.payuDetails(jsonObject);
//		FdOpeningNTB fdOpening= fdOpeningService.fetchByApplicationNo(Long.parseLong(jsonObject.getJSONObject("Data").getString("ApplicationNo")));
//		HttpStatus h = HttpStatus.BAD_GATEWAY;
//		if (payuDetails != null) {
//			String Data2 = payuDetails.getString("data");
//			logger.debug("data2");
//			logger.debug(Data2);
//			JSONObject Data1 = new JSONObject(Data2);
//			if (Data1.has("Data")) {
//				h = HttpStatus.OK;
//				String status = Data1.getJSONObject("Data").getJSONObject("TransactionDetails").getString("Status");
//				if(status.equals("success"))
//				{
//					if(fdOpening.getIsPaymentDone()==null || fdOpening.getIsPaymentDone().equals("N"))
//					{
//					fdOpening.setPaymentDate(LocalDateTime.now());
//					}
//					fdOpening.setIsPaymentDone("Y");
//				}
//				else
//				{
//					fdOpening.setIsPaymentDone("N");
//				}
//				sendOtpService.save(fdOpening);
//				return new ResponseEntity<Object>(Data1.toString(), h);
//			} else if (Data1.has("Error")) {
//				h = HttpStatus.BAD_REQUEST;
//				fdOpening.setIsPaymentDone("N");
//				sendOtpService.save(fdOpening);
//			}
//
//			return new ResponseEntity<Object>(Data1.toString(), h);
//
//		} else {
//
//			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
//		}
//	}
//
//	@RequestMapping(value = "/savePayuDetails", method = RequestMethod.POST, produces = "application/json")
//	public ResponseEntity<Object> savePayuDetails(@RequestBody String bm,
//			@RequestHeader(name = "Content-Type", required = true) String ContentType, HttpServletRequest req)
//			throws Exception {
//
//		JSONObject jsonObject = new JSONObject(bm);
//		System.out.println("Req" + jsonObject);
//		JSONObject response = razorPayService.savePayuDetails(jsonObject);
//
//		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
//
//	}
//}
