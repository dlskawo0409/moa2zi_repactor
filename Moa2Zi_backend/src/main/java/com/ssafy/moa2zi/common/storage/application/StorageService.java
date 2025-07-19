package com.ssafy.moa2zi.common.storage.application;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {

    String store(MultipartFile file, String s3FileName, String s3FolderName) throws IOException;
    String getPreSignedUrl(String filename);

    //    void deleteAll();
    void deleteOne(String fileName);
    String makeUUIDName(MultipartFile multipartFile);
    String makeUUIDName(String fileName);
}