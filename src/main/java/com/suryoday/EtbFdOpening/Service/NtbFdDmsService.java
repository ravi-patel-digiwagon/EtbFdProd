package com.suryoday.EtbFdOpening.Service;

import org.springframework.stereotype.Component;

import com.suryoday.EtbFdOpening.Pojo.FdOpeningNTB;

@Component
public interface NtbFdDmsService {

	void FdDmsUpload(FdOpeningNTB fd);
}
