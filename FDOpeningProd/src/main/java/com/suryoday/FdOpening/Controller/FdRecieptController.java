package com.suryoday.FdOpening.Controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.html2pdf.HtmlConverter;
import com.lowagie.text.BadElementException;
import com.suryoday.FdOpening.Others.GenerateProperty;
import com.suryoday.FdOpening.Pojo.ErrorResponse;
import com.suryoday.FdOpening.Pojo.FdOpening;
import com.suryoday.FdOpening.Service.FdOpeningService;
import com.suryoday.FdOpening.Service.FdRecieptService;
@Component
@RestController
@RequestMapping(value = "/fdOpening")

public class FdRecieptController extends OncePerRequestFilter{
	@Autowired
	FdRecieptService fdrecieptservice;
	Logger logger = LoggerFactory.getLogger(FdRecieptController.class);
	@RequestMapping(value = "/downloadReciept", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> downloadRecietPdf(@RequestBody String jsonRequest,
			@RequestHeader(name = "X-Correlation-ID", required = true) String headerPersist,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-To-ID", required = true) String X_To_ID,
			@RequestHeader(name = "X-Transaction-ID", required = true) String X_Transaction_ID,
			@RequestHeader(name = "MobileNo", required = true) String mobileNo,
			@RequestHeader(name = "X-Session-ID", required = true) String X_Session_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID)
			throws IOException, BadElementException {

		JSONObject jsonObject = new JSONObject(jsonRequest);
		
		
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		String OutputFileName = "";
		String pdfresponse = "";
		GenerateProperty x = GenerateProperty.getInstance();
		x.getappprop();

		OutputFileName = x.temp+mobileNo+"Reciept.pdf";
		StringBuilder htmlString = new StringBuilder();
		FdOpening fdopening=fdrecieptservice.fetchByMobNoAndSessionId(mobileNo,X_Session_ID);
		pdfresponse = fdrecieptservice.downloadRecietPdf(htmlString,mobileNo,fdopening);

		HtmlConverter.convertToPdf(pdfresponse, new FileOutputStream(OutputFileName));
		document.close();
		byte[] inFileBytes = Files.readAllBytes(Paths.get(OutputFileName));
		
		org.json.JSONObject reciept = new org.json.JSONObject();
		reciept.put("image", "photo.jpg");
		reciept.put("Lat", "00");
		reciept.put("Long", "00");
		reciept.put("Address", "");
		LocalDateTime localDateTime = LocalDateTime.now();
		reciept.put("timestamp", localDateTime);
		org.json.JSONObject reciept1 = new org.json.JSONObject();
		reciept1.put("Reciept", reciept);
		
//		twowheelerImageService.savepdf(inFileBytes,sanctionLetter1,applicationNo);
		
		String base64 = Base64.getEncoder().encodeToString(inFileBytes);
		logger.debug(base64);
		JSONObject response = new JSONObject();
		response.put("Success", "PDF Successfully Downloaded");
		response.put("pdf", base64);
		return new ResponseEntity<>(response.toString(), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/createLead", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Object> createLead(@RequestBody String bm,
			@RequestHeader(name = "X-From-ID", required = true) String X_From_ID,
			@RequestHeader(name = "X-Request-ID", required = true) String X_Request_ID, HttpServletRequest req)
			throws Exception {
		logger.debug("createLead start");
		logger.debug("createLead request" + bm);
		JSONObject Header = new JSONObject();
//		 Header.put("X-Correlation-ID",X_CORRELATION_ID );
		Header.put("X-From-ID", X_From_ID);
		Header.put("X-Request-ID", X_Request_ID);

		JSONObject jsonObject = new JSONObject(bm);
		JSONObject createLead = fdrecieptservice.createLead(jsonObject, Header);

		HttpStatus h = HttpStatus.BAD_GATEWAY;
		if (createLead != null) {
			String Data2 = createLead.getString("data");
			logger.debug("data2");
			JSONObject Data1 = new JSONObject(Data2);

			logger.debug(Data1.toString());

			if (Data1.has("Data")) {
				h = HttpStatus.OK;

			} else if (Data1.has("Error")) {
				h = HttpStatus.BAD_REQUEST;

			}
			logger.debug("response" + Data1);
			return new ResponseEntity<Object>(Data1.toString(), h);

		} else {
			logger.debug("timeout");
			return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
		}
	}
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getMethod().equals("OPTIONS")) {
			// response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			// throw new NoSuchElementException("You are Not authorized");
			org.json.JSONObject data2 = new org.json.JSONObject();
			data2.put("value", "unatharised Access");
			org.json.JSONObject data3 = new org.json.JSONObject();
			data3.put("Error", data2);

			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setCode(401);
			errorResponse.setMessage("Unauthorized Access");

			byte[] responseToSend = restResponseBytes(errorResponse);
			((HttpServletResponse) response).setHeader("Content-Type", "application/json");
			((HttpServletResponse) response).setStatus(401);
			response.getOutputStream().write(responseToSend);
			return;
		} else {
			filterChain.doFilter(request, response);
		}
	}

	private byte[] restResponseBytes(ErrorResponse eErrorResponse) throws IOException {
		String serialized = new ObjectMapper().writeValueAsString(eErrorResponse);
		return serialized.getBytes();
	}
}
