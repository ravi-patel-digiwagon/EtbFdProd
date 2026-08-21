package com.suryoday.FdOpening.Service;

import org.springframework.stereotype.Component;

import com.suryoday.FdOpening.Pojo.FdOpeningNTB;

@Component
public interface NtbFdDmsService {

	void FdDmsUpload(FdOpeningNTB fd);
}
