package com.suryoday.EtbFdOpening.Controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.suryoday.EtbFdOpening.Pojo.CraftMerchantDetails;
import com.suryoday.EtbFdOpening.Service.LeegalityService;


@Component
@RestController
@RequestMapping(value = "/fdOpening")
@CrossOrigin(origins = { "http://localhost:3000", "http://127.0.0.1:3000", "https://sarathi.suryodaybank.co.in/" })
public class LeegalityController {
	@Autowired
	LeegalityService leegalityService;
	@PostMapping(value = "/sendLeegality", produces = "application/json")
	public ResponseEntity<Object> createMandate(@RequestBody String bm,
			@RequestHeader(value = "Type",required = false) String type) throws Exception {
		JSONObject jsonObject=new JSONObject(bm);
		JSONObject response = leegalityService.sendLeegality(jsonObject,type);
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/fetchLeegality", produces = "application/json")
	public ResponseEntity<Object> fetchLeegality(@RequestBody String bm) throws Exception {
		JSONObject jsonObject=new JSONObject(bm);
		String type="";
		JSONObject response = leegalityService.fetchLeegality(jsonObject,type);
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/saveData", produces = "application/json")
	public ResponseEntity<Object> saveData(@RequestBody CraftMerchantDetails craftMerchantDetails) throws Exception {
		JSONObject response = leegalityService.saveData(craftMerchantDetails);
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/fetchQr", produces = "application/json")
	public ResponseEntity<Object> fetchQr(@RequestBody String bm) throws Exception {
		JSONObject response = leegalityService.fetchQr(new JSONObject(bm));
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}
	
	@PostMapping(value = "/dashboard", produces = "application/json")
	public ResponseEntity<Object> dashboard(@RequestBody String bm) throws Exception {
		JSONObject response = leegalityService.dashboard(new JSONObject(bm));
		return new ResponseEntity<Object>(response.toString(), HttpStatus.OK);
	}
	
	
	@PostMapping(value = "/fetchByMobileNo", produces = "application/json")
    public ResponseEntity<Object> fetchByMobileNo(@RequestBody String bm) throws Exception {
        CraftMerchantDetails craftMerchantDetails = leegalityService.fetchByMobileNo(new JSONObject(bm));
        return new ResponseEntity<Object>(craftMerchantDetails, HttpStatus.OK);
    }
	
	@PostMapping(value = "/fetchAllDataWeb", produces = "application/json")
	public ResponseEntity<Object> fetchAllDataWeb(@RequestBody String bm) throws Exception {
		JSONObject craftMerchantDetails = leegalityService.fetchAllDataWeb(new JSONObject(bm));
		return new ResponseEntity<Object>(craftMerchantDetails.toString(), HttpStatus.OK);
	}
	
}
