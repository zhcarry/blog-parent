package com.mszlu.blog.controller;

import com.mszlu.blog.service.PhotoDealService;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class FileDealController {

    @Autowired
    private PhotoDealService photoDealService;
    /**
     * 图片上传
     */
    @PostMapping("addPhoto")
    public Result addPhoto(@RequestParam("image")MultipartFile fileUpload) {
        if (null == fileUpload) {
            return Result.fail(ErrorCode.PHOTO_ADD_ERROR.getCode(),ErrorCode.PHOTO_ADD_ERROR.getMsg());
        }
        String url = photoDealService.upload(fileUpload);
        return Result.success(url);
    }
}
