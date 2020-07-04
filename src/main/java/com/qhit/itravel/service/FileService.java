package com.qhit.itravel.service;

import java.io.IOException;

import com.qhit.itravel.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;



public interface FileService {

	FileInfo save(MultipartFile file) throws IOException;

	void delete(String id);

}
