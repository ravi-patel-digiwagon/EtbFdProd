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

import com.suryoday.EtbFdOpening.Service.CkycService;
@Component
@RestController
@RequestMapping("/fdOpening")
public class CkycController {
private static Logger logger = LoggerFactory.getLogger(CkycController.class);
	@Autowired
	CkycService ckycservice;
	@RequestMapping(value="/ckycSearch", method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<Object> ckycSearch(@RequestBody String bm,
			 @RequestHeader(name = "Content-Type", required = true) String Content_Type,
			 @RequestHeader(name = "Accept", required = true) String accept)  throws Exception{
	
		JSONObject Header= new JSONObject();
		 		JSONObject jsonObject=new JSONObject(bm);
		        JSONObject ckycSearch = ckycservice.ckycSearch(jsonObject, Header);
		        logger.debug("Response from API",ckycSearch);
			
				HttpStatus  h=HttpStatus.BAD_GATEWAY;
				 if(ckycSearch!=null)
				 {
					 String Data2= ckycSearch.getString("data");
					 logger.debug("data2");
					 JSONObject Data1= new JSONObject(Data2);
					logger.debug("JSON Response From API",Data2);
					 
					 if(Data1.has("Data"))
					 {
						   h= HttpStatus.OK;
						   
					 }
					 else if(Data1.has("Error"))
					 {
						 h= HttpStatus.BAD_REQUEST;
						 
					 }
					 logger.debug("Main Response From API",Data1.toString());
					 return new ResponseEntity<Object>(Data1.toString(), h);
					 
				 }
				 else
				 {
					 logger.debug("GATEWAY_TIMEOUT");
					 return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
				 }	
			}
	
	@RequestMapping(value="/ckycDownload", method = RequestMethod.POST,produces = "application/json")
	public ResponseEntity<Object> ckycDownload(@RequestBody String bm,
			 @RequestHeader(name = "Content-Type", required = true) String Content_Type,
			 @RequestHeader(name = "Accept", required = true) String accept)  throws Exception{
	
		JSONObject Header= new JSONObject();
		 		JSONObject jsonObject=new JSONObject(bm);
		        JSONObject ckycDownload = ckycservice.ckycDownload(jsonObject, Header);
		        logger.debug("Response from API",ckycDownload);
			
				HttpStatus  h=HttpStatus.BAD_GATEWAY;
				 if(ckycDownload!=null)
				 {
					 String Data2= ckycDownload.getString("data");
					 logger.debug("data2");
					 JSONObject Data1= new JSONObject(Data2);
					logger.debug("JSON Response From API",Data2);
					 
					 if(Data1.has("Data"))
					 {
						   h= HttpStatus.OK;
						   
					 }
					 else if(Data1.has("Error"))
					 {
						 h= HttpStatus.BAD_REQUEST;
						 
					 }
					 logger.debug("Main Response From API",Data1.toString());
					 return new ResponseEntity<Object>(Data1.toString(), h);
					 
				 }
				 else
				 {
					 logger.debug("GATEWAY_TIMEOUT");
					 return new ResponseEntity<Object>("timeout", HttpStatus.GATEWAY_TIMEOUT);
				 }	
			}
}
