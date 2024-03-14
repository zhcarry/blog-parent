package com.mszlu.blog.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoDealService {
    // 下载图片到oss
    String upload(MultipartFile fileUpload);
}
