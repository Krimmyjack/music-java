package com.example.yin.controller;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

@Service
public class MinioUploadController {

    // MinioClient 是 MinIO 客户端，用于连接 MinIO 服务器
    private static MinioClient minioClient;

    // 存储桶名称，通过 application 配置获取
    private static String bucketName;

    // 初始化 MinIO 客户端，读取配置文件
    public static void init() {
        Properties properties = new Properties();
        try {
            // 从类路径加载配置文件，配置文件中包含 MinIO 服务器的相关信息
            InputStream inputStream = MinioUploadController.class.getClassLoader().getResourceAsStream("application-dev.properties");
            if (inputStream != null) {
                // 加载配置文件
                properties.load(inputStream);

                // 从配置文件中读取 MinIO 连接信息
                String minioEndpoint = properties.getProperty("minio.endpoint");
                String minioAccessKey = properties.getProperty("minio.access-key");
                String minioSecretKey = properties.getProperty("minio.secret-key");
                String minioBucketName = properties.getProperty("minio.bucket-name");

                // 将存储桶名称赋值给 bucketName
                bucketName = minioBucketName;

                // 使用 MinIO 客户端构建连接
                minioClient = MinioClient.builder()
                        .endpoint(minioEndpoint)
                        .credentials(minioAccessKey, minioSecretKey)
                        .build();
            }
        } catch (Exception e) {
            System.out.println(e); // 输出异常信息
        }
    }

    // 上传普通文件
    public static String uploadFile(MultipartFile file) {
        try {
            init(); // 初始化 MinIO 客户端

            // 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 将文件上传到 MinIO 存储桶
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName) // 指定存储桶名称
                            .object(file.getOriginalFilename()) // 文件名
                            .stream(inputStream, inputStream.available(), -1) // 上传文件流
                            .contentType(file.getContentType()) // 文件类型
                            .build()
            );

            // 文件上传成功返回信息
            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage(); // 捕获上传文件时的异常并返回错误信息
        } catch (Exception e) {
            throw new RuntimeException(e); // 其他异常抛出运行时异常
        }
    }

    // 上传歌手图片文件
    public static String uploadImgFile(MultipartFile file) {
        try {
            init();
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/singer/img/" + file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 上传歌单图片文件
    public static String uploadSonglistImgFile(MultipartFile file) {
        try {
            init();
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/songlist/" + file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 上传歌曲图片文件
    public static String uploadSongImgFile(MultipartFile file) {
        try {
            init();
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/singer/song/" + file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return "File uploaded successfully!";
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 上传用户头像图片文件
    public static String uploadAtorImgFile(MultipartFile file) {
        try {
            init();
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object("/img/avatorImages/" + file.getOriginalFilename())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return "File uploaded successfully!"; // 上传成功
        } catch (MinioException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            return "Error uploading file to MinIO: " + e.getMessage();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
