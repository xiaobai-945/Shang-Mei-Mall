package red.mlz.app.controller.excel;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import red.mlz.app.api.excel.ExcelFeignClient;
import red.mlz.common.utils.Response;

import javax.annotation.Resource;
import java.util.List;


/**
 * 导出类目表
 */
@RestController
public class ExcelController {

    @Resource
    private ExcelFeignClient excelFeignClient;

    @RequestMapping("/export/file")
    public String export() {
        return excelFeignClient.export();
    }

    /**
     * 导入
     */
    @RequestMapping("/import/file")
    public Response importFile(@RequestParam(name = "file") MultipartFile file) {

        return excelFeignClient.importFile(file);
    }

}
