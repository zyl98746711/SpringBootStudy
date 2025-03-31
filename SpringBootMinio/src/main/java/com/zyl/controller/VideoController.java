package com.zyl.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("video")
@RestController
public class VideoController {

    @Autowired
    private MinioClient minioClient;

    @GetMapping("/stream/{bucket}/{object}")
    public void streamVideo(
            @PathVariable(name = "bucket") String bucket,
            @PathVariable(name = "object") String object,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(bucket).object(object).build();
        // 获取文件信息
        long fileSize = minioClient.statObject(statObjectArgs).size();

        // 解析范围请求头
        String rangeHeader = request.getHeader(HttpHeaders.RANGE);
        GetObjectArgs objectArgs = GetObjectArgs.builder().bucket(bucket).object(object).build();
        if (rangeHeader == null) {
            // 如果没有范围请求头，返回整个文件
            response.setStatus(HttpStatus.OK.value());
            response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
            response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
            try (InputStream inputStream = minioClient.getObject(objectArgs);
                 OutputStream outputStream = response.getOutputStream()) {
                IOUtils.copy(inputStream, outputStream);
            }
            return;
        }

        // 解析范围
        String[] ranges = rangeHeader.replace("bytes=", "").split("-");
        long start = Long.parseLong(ranges[0]);
        long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;
        long contextLength = end - start + 1;
        long rangeLength = (contextLength - start) / 10 + start;

        // 设置响应头
        response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");
        response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + fileSize);
        response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(contextLength));
        InputStream inputStream = minioClient.getObject(objectArgs);
        // 分段传输
        try (OutputStream outputStream = response.getOutputStream()) {
            long skip = inputStream.skip(start);// 跳过起始字节
            byte[] buffer = new byte[1024 * 1024 * 4]; // 1MB 缓冲区
            int bytesRead;
            long remainingBytes = rangeLength;
            while ((bytesRead = inputStream.read(buffer, 0, (int) Math.min(buffer.length, remainingBytes))) > 0) {
                outputStream.write(buffer, 0, bytesRead);
                remainingBytes -= bytesRead;
            }
        }

    }

    @GetMapping("/url/{bucket}/{object}")
    public String getUrl(@PathVariable(name = "bucket") String bucket,
                         @PathVariable(name = "object") String object) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetPresignedObjectUrlArgs objectUrlArgs = GetPresignedObjectUrlArgs.builder().bucket(bucket)
                .object(object)
                .expiry(1, TimeUnit.HOURS)
                .method(Method.GET)
                .build();
        return minioClient.getPresignedObjectUrl(objectUrlArgs);
    }
}