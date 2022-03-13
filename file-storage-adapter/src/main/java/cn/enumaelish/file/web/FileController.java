package cn.enumaelish.file.web;

import cn.enumaelish.file.command.*;
import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.util.JsonUtil;
import cn.enumaelish.file.dto.data.FileUrlDto;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.dto.data.MultipartUploadResult;
import cn.enumaelish.file.dto.data.PutObjectResult;
import cn.enumaelish.file.dto.file.*;
import cn.enumaelish.file.enums.FileConstants;
import cn.enumaelish.file.query.GetFileUrlQueryExc;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.BizException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author: EnumaElish
 * @Date: 2022/2/28 20:08
 * @Description: 文件服务Api
 */
@RequestMapping(FileConstants.PATH + "/file")
@Controller
@Api(value = "文件服务",tags = "文件上传下载相关接口")
public class FileController {


    @Autowired
    HydraFileStorageFactory hydraFileStorageFactory;

    @Autowired
    PutObjectCmdExc putObjectCmdExc;

    @Autowired
    GeneratePutUrlCmdExc generatePutUrlCmdExc;

    @Autowired
    GetFileUrlQueryExc getFileUrlQueryExc;

    @Autowired
    DeleteFileCmdExc deleteFileCmdExc;

    @Autowired
    PermanentFileCmdExc permanentFileCmdExc;

    @Autowired
    MultipartUploadExc multipartUploadExc;

    @Autowired
    CompleteMultipartUploadExc completeMultipartUploadExc;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ApiOperation(value = "直接上传文件，不支持大文件，会内存溢出")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessType",paramType = "query",defaultValue = "1",value = "文件的业务标识",required = false),
            @ApiImplicitParam(name = "extranet",paramType = "query",defaultValue = "true",value = "使用内网还是外网，默认外网",required = false)
    })
    @ResponseBody
    public SingleResponse<PutObjectResult> uploadFile(MultipartFile file,
          @RequestParam(value = "businessType", defaultValue = "1", required = false) Integer businessType,
          @RequestParam(value = "extranet", defaultValue = "true", required = false) boolean extranet) throws IOException {
        if(file == null){
            throw new BizException("上传的文件不可为空");
        }
        PutObjectCmd putObjectCmd = new PutObjectCmd();
        putObjectCmd.setFileName(file.getName());
        putObjectCmd.setFileType(file.getContentType());
        putObjectCmd.setBusinessType(businessType);
        putObjectCmd.setExtranet(extranet);
        putObjectCmd.setInputStream(file.getInputStream());
        putObjectCmd.setSize(file.getSize());
        return SingleResponse.of(putObjectCmdExc.execute(putObjectCmd));
    }


    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    @ApiOperation(value = "获取直接上传文件url")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessType",paramType = "query",defaultValue = "1",value = "文件的业务标识",required = false),
            @ApiImplicitParam(name = "extranet",paramType = "query",defaultValue = "true",value = "使用内网还是外网，默认外网",required = false),
            @ApiImplicitParam(name = "check",paramType = "query",defaultValue = "true",value = "true校验md5,false不校验，前端个性化项目md5加密实现有问题",required = false)
    })
    @ResponseBody
    public SingleResponse<GeneratePutUrlResult> generatePutFileUrl(@Valid GeneratePutUrlCmd generatePutUrlCmd) {
        return SingleResponse.of(generatePutUrlCmdExc.execute(generatePutUrlCmd));
    }



    @RequestMapping(value = "/getDownloadFileUrl", method = RequestMethod.GET)
    @ApiOperation(value = "获取文件url及文件信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId",paramType = "query",defaultValue = "1",value = "文件标识",required = true),
            @ApiImplicitParam(name = "expireTime",paramType = "query",defaultValue = "3600000",value = "过期时间(毫秒），默认3600000L，一小时",required = false),
            @ApiImplicitParam(name = "extranet",paramType = "query",defaultValue = "true",value = "使用内网还是外网，默认外网",required = false)
    })
    @ResponseBody
    public SingleResponse<FileUrlDto> getDownloadFileUrl(@Valid GetFileUrlQuery getFileUrlQuery){
        return SingleResponse.of(getFileUrlQueryExc.execute(getFileUrlQuery));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId",paramType = "query",defaultValue = "1",value = "文件标识",required = true)
    })
    @ResponseBody
    public SingleResponse<Boolean> delete(@Valid @RequestBody DeleteFileCmd deleteFileCmd){
        return SingleResponse.of(deleteFileCmdExc.execute(deleteFileCmd));
    }


    @RequestMapping(value = "/redirectDownloadFileUrl", method = RequestMethod.GET)
    @ApiOperation(value = "跳转至文件下载地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fileId",paramType = "query",defaultValue = "1",value = "文件标识",required = true),
            @ApiImplicitParam(name = "expireTime",paramType = "query",defaultValue = "3600000",value = "过期时间(毫秒），默认3600000L，一小时",required = false),
            @ApiImplicitParam(name = "extranet",paramType = "query",defaultValue = "true",value = "使用内网还是外网，默认外网",required = false)
    })
    public void redirectDownloadFileUrl(GetFileUrlQuery getFileUrlQuery, HttpServletResponse response){
        try {
            response.sendRedirect(getFileUrlQueryExc.execute(getFileUrlQuery).getUrl());
        } catch (Exception e) {
            response.setStatus(404);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            try {
                response.getWriter().write(JsonUtil.toJSONString(SingleResponse.buildFailure("404",e.getMessage())));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    @RequestMapping(value = "/permanent", method = RequestMethod.POST)
    @ApiOperation(value = "标记为永久文件，会校验文件是否存在与一致性")
    @ResponseBody
    public SingleResponse<Boolean> permanent(@RequestBody PermanentFileCmd permanentFileCmd){
        permanentFileCmdExc.execute(permanentFileCmd);
        return SingleResponse.of(true);
    }

    @RequestMapping(value = "/multipartUpload", method = RequestMethod.GET)
    @ApiOperation(value = "获取分片上传文件url")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessType",paramType = "query",defaultValue = "1",value = "文件的业务标识",required = false),
            @ApiImplicitParam(name = "extranet",paramType = "query",defaultValue = "true",value = "使用内网还是外网，默认外网",required = false),
            @ApiImplicitParam(name = "check",paramType = "query",defaultValue = "true",value = "true校验md5,false不校验，前端个性化项目md5加密实现有问题",required = false)
    })
    @ResponseBody
    public SingleResponse<MultipartUploadResult> multipartUpload(GeneratePutUrlCmd generatePutUrlCmd) {
        return SingleResponse.of(multipartUploadExc.execute(generatePutUrlCmd));
    }

    @RequestMapping(value = "/completeMultipartUpload", method = RequestMethod.POST)
    @ApiOperation(value = "完成分片上传")
    @ResponseBody
    public SingleResponse<Boolean> multipartUpload(@RequestBody CompleteMultipartUploadCmd cmd) {
        return SingleResponse.of(completeMultipartUploadExc.execute(cmd));
    }


}
