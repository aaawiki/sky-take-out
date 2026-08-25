package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/common")
public class CommonController {
    @Autowired
    AliOssUtil aliOssUtil;
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("上传文件：{}", file.getOriginalFilename());
        //TODO 应该把处理逻辑放到AliOssUtil类里

        // 原始文件名
        String originalFileName = file.getOriginalFilename();

        // 上传并返回访问路径
        String filePath = aliOssUtil.upload(file.getBytes(), originalFileName);
        return Result.success(filePath);
    }
}
