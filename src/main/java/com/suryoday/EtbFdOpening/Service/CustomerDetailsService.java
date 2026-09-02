package com.suryoday.EtbFdOpening.Service;

import java.io.IOException;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.InterestRates;

@Component
public interface CustomerDetailsService {

	JSONObject getCustomerDetails(String mobileNo, JSONObject header);

	JSONObject validateCustomerMobileNumber(String mobileNo, JSONObject header);

	JSONObject getAccountDetails(String accountNo, JSONObject header);

	JSONObject calculateDeposit(JSONObject jsonObject, JSONObject header);

	List<InterestRates> fetchInterestRates();

	JSONObject nameMatch(JSONObject jsonObject, JSONObject header);

	JSONObject cifCreation(JSONObject jsonObject, JSONObject header);

	JSONObject accountCreation(JSONObject jsonObject, JSONObject header);

	JSONObject getDetailsByCustId(String custId, JSONObject header);

	JSONObject checkAml(JSONObject jsonObject);

	JSONObject dmsUpload(JSONObject jsonObject) throws IOException;

	JSONObject downloadPdf(JSONObject jsonObject);

	JSONObject getCustomerDetailsEtbOrNtb (String aadhaarNo, String panNo, JSONObject header);

	JSONObject fetchCustomerDetailSurapiByCustomerId(String customerId, JSONObject headerJson);

}
