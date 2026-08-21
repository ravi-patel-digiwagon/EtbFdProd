package com.suryoday.EtbFdOpening.Service;

import org.springframework.stereotype.Component;

@Component
public interface ErrorResponseService {

	String getError(String kycRes, long parseLong);

}
