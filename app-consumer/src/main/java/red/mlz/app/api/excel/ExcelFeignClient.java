package red.mlz.app.api.excel;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import red.mlz.common.utils.Response;


/**
 * 导出类目表
 */
@FeignClient(name="app-provider")
public interface ExcelFeignClient {

    @RequestMapping("/export/file")
    String export();

    /**
     * 导入
     */
    @RequestMapping("/import/file")
    public Response importFile(@RequestParam(name = "file") MultipartFile file);

}
