package cn.enumaelish.file.sdk;

import cn.enumaelish.file.dto.data.FileUrlDto;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.dto.data.MultipartUploadResult;
import cn.enumaelish.file.dto.data.PutObjectResult;
import cn.enumaelish.file.dto.file.CompleteMultipartUploadCmd;
import cn.enumaelish.file.dto.file.PermanentFileCmd;
import cn.enumaelish.file.enums.FileConstants;
import com.alibaba.cola.dto.SingleResponse;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: EnumaElish
 * @Date: 2022/3/9 14:38
 * @Description: 此为调用第三方服务的client
 */
@FeignClient(value = "${file.storage.url}}")
public interface FileStorageFeignClient {

	/**
	 * 直接上传文件
	 * @param file
	 * @param businessType
	 * @param extranet
	 * @return
	 */
	@PostMapping(value = FileConstants.PATH + "/file/upload", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ApiOperation(value = "直接上传文件", notes = "文件唯一id")
	SingleResponse<PutObjectResult> uploadFile(
			@RequestPart("file") MultipartFile file,
			@RequestParam(value = "businessType", defaultValue = "1", required = false) Integer businessType,
			@RequestParam(value = "extranet", defaultValue = "true", required = false) boolean extranet) ;

	/**
	 * 获取上传文件url
	 * @param businessType
	 * @param extranet
	 * @param size
	 * @param fileName
	 * @param md5
	 * @param check
	 * @return
	 */
	@GetMapping(FileConstants.PATH + "/file/upload")
	@ApiOperation(value = "获取直接上传文件url(调用方直接上传)")
	SingleResponse<GeneratePutUrlResult> generatePutFileUrl(
			@RequestParam(value = "businessType", defaultValue = "1", required = false) Integer businessType,
			@RequestParam(value = "extranet", defaultValue = "true", required = false) boolean extranet,
			@RequestParam(value = "size") Long size,
			@RequestParam(value = "fileName") String fileName,
			@RequestParam(value = "md5") String md5,
			@RequestParam(value = "check", defaultValue = "true", required = false) boolean check);

	/**
	 * 删除文件
	 * @param fileId
	 * @return
	 */
	@DeleteMapping(FileConstants.PATH + "/file/delete")
	@ApiOperation(value = "删除文件", notes = "文件是否删除成功")
	SingleResponse<Boolean> deleteFile(
			@RequestParam("fileId") String fileId) ;

	@GetMapping(FileConstants.PATH + "/file/getDownloadFileUrl")
	@ApiOperation(value = "获取文件url及文件信息", notes = "获取文件url及文件信息")
	SingleResponse<FileUrlDto> getDownloadFileUrl(
			@RequestParam("fileId") String fileId,
			@RequestParam(value = "extranet", defaultValue = "true", required = false) boolean extranet,
			@RequestParam(value = "expireTime", defaultValue = "3600000", required = false) Long expireTime);

	@GetMapping(value = FileConstants.PATH + "/file/redirectDownloadFileUrl")
	@ApiOperation(value = "获取文件流", notes = "获取文件流")
	feign.Response downFile(@RequestParam("fileId") String fileId,
	                        @RequestParam(value = "extranet", defaultValue = "true", required = false) boolean extranet);

	/**
	 * 获取分片上传文件url
	 * @param businessType
	 * @param extranet
	 * @param size
	 * @param fileName
	 * @param md5
	 * @param check
	 * @return
	 */
	@GetMapping(FileConstants.PATH + "/file/multipartUpload")
	@ApiOperation(value = "获取分片上传文件url")
	SingleResponse<MultipartUploadResult> multipartUpload(
			@RequestParam(value = "businessType", defaultValue = "1", required = false) Integer businessType,
			@RequestParam(value = "extranet", defaultValue = "true", required = false) boolean extranet,
			@RequestParam(value = "size") Long size,
			@RequestParam(value = "fileName") String fileName,
			@RequestParam(value = "md5") String md5,
			@RequestParam(value = "check", defaultValue = "true", required = false) boolean check);

	/**
	 * 合并分片
	 * @param completeMultipartUploadCmd
	 * @return
	 */
	@PostMapping(FileConstants.PATH + "/file/completeMultipartUpload")
	@ApiOperation(value = "合并分片")
	SingleResponse<Boolean> completeMultipartUpload(
			@RequestBody CompleteMultipartUploadCmd completeMultipartUploadCmd);

	/**
	 * 标记为永久文件，会校验文件是否存在与一致性
	 * @param permanentFileCmd
	 * @return
	 */
	@PostMapping(FileConstants.PATH + "/file/permanent")
	@ApiOperation(value = "标记为永久文件，会校验文件是否存在与一致性")
	SingleResponse<Boolean> permanent(@RequestBody PermanentFileCmd permanentFileCmd);
}
