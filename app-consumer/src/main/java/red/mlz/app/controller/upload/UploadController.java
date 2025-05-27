package red.mlz.app.controller.upload;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import red.mlz.app.api.upload.UploadFeignClient;
import red.mlz.common.utils.Response;

import javax.annotation.Resource;

@RestController
public class UploadController {

    @Resource
    private UploadFeignClient uploadFeignClient;

    // 上传图片接口
    @RequestMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {

        return uploadFeignClient.uploadImage(file);
    }


    @RequestMapping("/uploadImage")
    public Response uploadImages(@RequestParam("file") MultipartFile file) {

       return uploadFeignClient.uploadImages(file);
    }

    @RequestMapping("/uploadVideo")
    public Response uploadVideo(@RequestParam("file") MultipartFile file) {

        return uploadFeignClient.uploadVideo(file);
    }

    @RequestMapping("/uploadFile")
    public Response uploadFile(@RequestParam("file") MultipartFile file) {

        return uploadFeignClient.uploadFile(file);
    }
}
