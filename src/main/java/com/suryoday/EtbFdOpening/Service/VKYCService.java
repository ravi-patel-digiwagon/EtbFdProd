package com.suryoday.EtbFdOpening.Service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;

@Component
public interface VKYCService {

	JSONObject getVkycDetails(String trackingId, JSONObject header);

	JSONObject createVkyc(JSONObject jsonObject, JSONObject header);

//	List<String> getAllTrackingIds();

	List<String> getAllTrackingIds(String isActive);

	List<FdOpeningNTB> findByIsPartialVkycAndIsFdCreated(String isActive, String isPartialVkyc, String isFdCreated);

	List<FdOpeningNTB> getAllDmsUploadList(String isDmsUpload);

	FdOpeningNTB fetchByCifCustomerId(String customerId);

	JSONObject createHyperVergeVkyc(JSONObject jsonObject, JSONObject header);

	JSONObject getHyperVergeVkycDetails(String trackingId, JSONObject header);

}
