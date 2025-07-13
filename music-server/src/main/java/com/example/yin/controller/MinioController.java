package com.example.yin.controller;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.InputStream;

@Controller
public class MinioController {

    // 从配置文件中获取桶名称
    @Value("${minio.bucket-name}")
    private String bucketName;

    // 自动注入 MinioClient，用于与 Minio 服务交互
    @Autowired
    private MinioClient minioClient;


     // 获取歌曲文件
    @GetMapping("/user01/{fileName:.+}")
    public ResponseEntity<byte[]> getMusic(@PathVariable String fileName) {
        try {
            // 构建获取文件的请求参数
            GetObjectArgs args = GetObjectArgs.builder()
                    .bucket(bucketName)  // 指定桶名称
                    .object(fileName)    // 指定文件名
                    .build();

            // 从 Minio 获取文件输入流
            InputStream inputStream = minioClient.getObject(args);

            // 将输入流转换为字节数组
            byte[] bytes = IOUtils.toByteArray(inputStream);

            // 设置响应头，指示浏览器如何处理响应内容
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // 设置文件内容类型为二进制流
            headers.setContentDispositionFormData("attachment", fileName);  // 设置为附件下载

            // 返回字节数组作为响应
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);  // 返回500错误
        }
    }


     // 获取歌手图片

    @GetMapping("/user01/singer/img/{fileName:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws Exception {
        // 从 Minio 获取图片文件输入流
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)  // 指定桶名称
                        .object("singer/img/" + fileName)  // 文件路径
                        .build()
        );

        // 将输入流转换为字节数组
        byte[] bytes = IOUtils.toByteArray(stream);

        // 设置响应头，指示返回图片格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);  // 设置内容类型为JPEG图片

        // 返回图片字节数组
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }


     // 获取歌单图片
    @GetMapping("/user01/songlist/{fileName:.+}")
    public ResponseEntity<byte[]> getImage1(@PathVariable String fileName) throws Exception {
        // 从 Minio 获取歌单图片文件输入流
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)  // 指定桶名称
                        .object("songlist/" + fileName)  // 文件路径
                        .build()
        );

        // 将输入流转换为字节数组
        byte[] bytes = IOUtils.toByteArray(stream);

        // 设置响应头，指示返回图片格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);  // 设置内容类型为JPEG图片

        // 返回图片字节数组
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }


     // 获取歌曲图片

    @GetMapping("/user01/singer/song/{fileName:.+}")
    public ResponseEntity<byte[]> getImage2(@PathVariable String fileName) throws Exception {
        // 从 Minio 获取歌曲图片文件输入流
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)  // 指定桶名称
                        .object("singer/song/" + fileName)  // 文件路径
                        .build()
        );

        // 将输入流转换为字节数组
        byte[] bytes = IOUtils.toByteArray(stream);

        // 设置响应头，指示返回图片格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);  // 设置内容类型为JPEG图片

        // 返回图片字节数组
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }


     // 获取用户头像图片

    @GetMapping("/img/avatorImages/{fileName:.+}")
    public ResponseEntity<byte[]> getImage3(@PathVariable String fileName) throws Exception {
        // 从 Minio 获取头像图片文件输入流
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)  // 指定桶名称
                        .object("img/avatorImages/" + fileName)  // 文件路径
                        .build()
        );

        // 将输入流转换为字节数组
        byte[] bytes = IOUtils.toByteArray(stream);

        // 设置响应头，指示返回图片格式
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);  // 设置内容类型为JPEG图片

        // 返回图片字节数组
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
