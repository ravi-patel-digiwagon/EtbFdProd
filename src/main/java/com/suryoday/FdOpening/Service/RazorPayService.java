package com.suryoday.FdOpening.Service;

import org.json.JSONObject;
import org.springframework.stereotype.Component;


@Component
public interface RazorPayService {

	JSONObject sendPaymentLink(JSONObject jsonObject, JSONObject header);

	JSONObject fetchPaymentLink(String orderId, JSONObject header);

	JSONObject payuDetails(JSONObject jsonObject);

	JSONObject savePayuDetails(JSONObject jsonObject);

	JSONObject savePayuDetailsEtb(JSONObject jsonObject,String mobileNo,String X_Session_ID);


}
