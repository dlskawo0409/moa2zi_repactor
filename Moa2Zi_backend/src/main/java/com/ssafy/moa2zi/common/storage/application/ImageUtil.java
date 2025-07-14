package com.ssafy.moa2zi.common.storage.application;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.nio.AnimatedGif;
import com.sksamuel.scrimage.nio.AnimatedGifReader;
import com.sksamuel.scrimage.nio.ImageSource;
import com.sksamuel.scrimage.webp.Gif2WebpWriter;
import com.sksamuel.scrimage.webp.WebpWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageUtil{
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    private final S3Service s3Service;

    public static String isImage(MultipartFile multipartFile) throws BadRequestException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadRequestException("이미지가 존재하지않습니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BadRequestException("파일 명이 유효하지않습니다.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if(!IMAGE_EXTENSIONS.contains(extension)){
            throw new BadRequestException("지원하지 않는 파일확장자 입니다.");
        }

        if(!isValidImage(multipartFile)){
           throw new BadRequestException("지원하지 않는 파일입니다.");
        }

        return extension;
    }

    private static boolean isValidImage(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            byte[] headerBytes = new byte[4]; // Magic Number는 대부분 2~4바이트
            inputStream.read(headerBytes);

            String hex = bytesToHex(headerBytes);
            return isValidImageSignature(hex);
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean isValidImageSignature(String hex) {
        List<String> imageSignatures = Arrays.asList(
            "ffd8ffe0", "ffd8ffe1", "ffd8ffe2", "ffd8ffe3", // JPEG
            "89504e47", // PNG
            "47494638", // GIF
            "424d",     // BMP
            "49492a00", "4d4d002a" // TIFF
        );
        return imageSignatures.stream().anyMatch(hex::startsWith);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public String store(MultipartFile multipartFile, String s3FileName, String s3FolderName) throws IOException {
        isImage(multipartFile);

        return s3Service.store(multipartFile, s3FileName, s3FolderName);
    }

    public String store(MultipartFile multipartFile) throws IOException {
        isImage(multipartFile);

        return s3Service.store(multipartFile);
    }

    public String CovertToWebpAndStore(
            MultipartFile multipartFile,
            int width,
            int height,
            String s3FolderName
    ) throws IOException {
        isImage(multipartFile);
        String s3FileName = makeUUIDName(getFileNameWithOut(multipartFile)) + ".webp";
        InputStream inputStream = convertToWebp(multipartFile,width,height);
        return s3Service.store(inputStream, s3FileName, s3FolderName);
    }


    public String CovertToWebpAndStore(
            MultipartFile multipartFile,
            String s3FolderName
    ) throws IOException {
        String extension = isImage(multipartFile);
        String s3FileName = makeUUIDName(getFileNameWithOut(multipartFile)) + ".webp";
        if(extension.equals("gif")){
            return s3Service.store(multipartFile, s3FileName, s3FolderName);
        }

        return s3Service.store(convertToWebp(multipartFile), s3FileName, s3FolderName);
    }

    public static String getFileNameWithOut(MultipartFile multipartFile){
        StringTokenizer st = new StringTokenizer(Objects.requireNonNull(multipartFile.getOriginalFilename()), ".");
        return st.nextToken();
    }

    public static InputStream convertToWebp(
            MultipartFile multipartFile,
            int width,
            int height
    ) throws BadRequestException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadRequestException("파일이 존재하지 않습니다.");
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] fileBytes = multipartFile.getBytes();

            ImmutableImage image = ImmutableImage.loader().fromBytes(fileBytes);

//            return new ByteArrayInputStream(image.bound(width,height).bytes(WebpWriter.DEFAULT));
            return new ByteArrayInputStream(image.scaleToWidth(width).bytes(WebpWriter.DEFAULT.withQ(80)));
        } catch (IOException e) {
            throw new RuntimeException("WebP 변환 중 오류가 발생했습니다.", e);
        }
    }


    public static InputStream convertToWebp(
            MultipartFile multipartFile
    ) throws BadRequestException {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new BadRequestException("파일이 존재하지 않습니다.");
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] fileBytes = multipartFile.getBytes();

            ImmutableImage image = ImmutableImage.loader().fromBytes(fileBytes);

            return new ByteArrayInputStream(image.bytes(WebpWriter.DEFAULT.withQ(80)));

        } catch (IOException e) {
            throw new RuntimeException("WebP 변환 중 오류가 발생했습니다.", e);
        }
    }


    public String makeUUIDName(MultipartFile multipartFile){
        return UUID.randomUUID() +multipartFile.getOriginalFilename();
    }

    public String makeUUIDName(String string){
        return UUID.randomUUID() +string;
    }

    public String getPreSignedUrl(String keyName){ return s3Service.getPreSignedUrl(keyName); }
}
