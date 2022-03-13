package cn.enumaelish.file.alioss;

import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.domain.model.FileInfo;
import cn.enumaelish.file.domain.model.StoragePlatform;
import cn.enumaelish.file.domain.model.gateway.PutObjectBytesCmd;
import cn.enumaelish.file.domain.spi.Extension;
import cn.enumaelish.file.util.JsonUtil;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.dto.data.MultipartRequestParam;
import cn.enumaelish.file.dto.data.MultipartUploadResult;
import cn.enumaelish.file.enums.FileConstants;
import com.alibaba.cola.exception.BizException;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;

import java.net.URL;
import java.util.*;

/**
 * @author: EnumaElish
 * @Date: 2022/2/20 20:35
 * @Description: 这个是阿里云的共有云的实现，如果私有云，请使用file-storage-alioss-proxy
 */
@Extension("3")
public class AliOssStorage implements FileStorage<AliOssConfigStorage> {


	public final String PART_NUNMBER = "partNumber";

	public final String UPLOAD_ID = "uploadId";

	private StoragePlatform storagePlatform;

	private AliOssConfigStorage config;

	private OSS intranetClient;

	/**
	 * 外网client，处理外网相关
	 */
	private OSS extranetClient;

	@Override
	public StoragePlatform getPlatformStore() {
		return storagePlatform;
	}

	@Override
	public void init(StoragePlatform<AliOssConfigStorage> storagePlatform) {
		this.storagePlatform = storagePlatform;
		config = storagePlatform.getConfig();
		if (config == null) {
			throw new BizException(String.format("七牛配置错误：%s", JsonUtil.toJSONString(storagePlatform)));
		}
		// 此为公有云的实现，如果要特殊的请额外添加实现
		intranetClient = new OSSClientBuilder().build(config.getIntranetDomain(), config.getAccessKey(), config.getAccessSecret());
		extranetClient = new OSSClientBuilder().build(config.getExtranetDomain(), config.getAccessKey(), config.getAccessSecret());
	}

	private OSS getClient(boolean extranet) {
		return extranet ? extranetClient : intranetClient;
	}

	@Override
	public String save(PutObjectBytesCmd putObjectBytesCmd) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(putObjectBytesCmd.getContentType());
		PutObjectResult putObjectResult = getClient(putObjectBytesCmd.isExtranet()).putObject(config.getBucketName(), putObjectBytesCmd.getKey(), putObjectBytesCmd.getInputStream(), metadata);
		return putObjectResult.getETag().toLowerCase();
	}

	@Override
	public boolean delete(String key) {
		getClient(true).deleteObject(config.getBucketName(), key);
		return true;
	}

	@Override
	public boolean check(FileInfo fileInfo) {
		ObjectMetadata objectMetadata = getClient(true).getObjectMetadata(config.getBucketName(), fileInfo.getPath());
		if (fileInfo.isCheckMd5()) {
			if (!objectMetadata.getETag().toLowerCase().equals(fileInfo.getMd5())) {
				return false;
			}
		}
		return objectMetadata.getContentLength() == fileInfo.getSize();
	}

	@Override
	public String getDownloadFileUrl(String key, String fileName, long expireTime, boolean extranet) {
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(config.getBucketName(), key);
		generatePresignedUrlRequest.setMethod(HttpMethod.GET);
		generatePresignedUrlRequest.setExpiration(new Date(System.currentTimeMillis() + expireTime));
		URL url = getClient(extranet).generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();
	}

	@Override
	public GeneratePutUrlResult generatePutUrl(FileInfo fileInfo, boolean extranet) {
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);
		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(config.getBucketName(), fileInfo.getPath());
		generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
		generatePresignedUrlRequest.setExpiration(expiration);
		generatePresignedUrlRequest.setContentType(fileInfo.getFileType());
		URL url = getClient(extranet).generatePresignedUrl(generatePresignedUrlRequest);
		GeneratePutUrlResult generatePutUrlResult = new GeneratePutUrlResult();
		generatePutUrlResult.setUrl(url.toString());
		generatePutUrlResult.setHttpMethod(FileConstants.HTTP_METHOD_PUT);
		generatePutUrlResult.setFileId(fileInfo.getFileId());
		Map<String, Object> headers = new HashMap<>(4);
		headers.put(FileConstants.HEADER_CONTENT_TYPE, fileInfo.getFileType());
		generatePutUrlResult.setHeaders(headers);
		// 表示使用binary方式传文件
		generatePutUrlResult.setFormBody(null);
		return generatePutUrlResult;
	}

	@Override
	public String initiateMultipartUpload(FileInfo fileInfo, boolean extranet) {
		try {
			InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(config.getBucketName(), fileInfo.getPath());
			InitiateMultipartUploadResult upresult = getClient(extranet).initiateMultipartUpload(request);
			return upresult.getUploadId();
		} catch (OSSException oe) {
			throw new BizException("初始化分片失败", oe);
		}
	}

	@Override
	public MultipartUploadResult generateMultipartUrl(FileInfo fileInfo, boolean extranet) {
		MultipartUploadResult multipartUploadResult = new MultipartUploadResult();
		multipartUploadResult.setFileId(fileInfo.getFileId());
		Long fileLength = fileInfo.getSize();
		long pageSize = fileInfo.getFileMulPartPageSize();
		int partCount = fileInfo.getPageCount();
		//封裝url信息
		Date expiration = new Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 24 * 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);
		List<MultipartRequestParam> list = new ArrayList<>();
//		String url = getUrl(fileInfo.getPath());
		for (int i = 0; i < partCount; i++) {
			MultipartRequestParam multipartRequestParam = new MultipartRequestParam();
			int partNumber = i + 1;
			multipartRequestParam.setPartNumber(partNumber);
			long startPos = i * pageSize;
			long curPartSize = (partNumber == partCount) ? (fileLength - startPos) : pageSize;
			//给路径封装partNumber 和 uploadId 参数
			GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(config.getBucketName(), fileInfo.getPath());
			HashMap<String, String> stringStringHashMap = new HashMap<>();
			stringStringHashMap.put(PART_NUNMBER, String.valueOf(partNumber));
			stringStringHashMap.put(UPLOAD_ID, fileInfo.getUploadId());
			generatePresignedUrlRequest.setQueryParameter(stringStringHashMap);
			generatePresignedUrlRequest.setMethod(HttpMethod.PUT);
			generatePresignedUrlRequest.setExpiration(expiration);
			generatePresignedUrlRequest.setContentType(fileInfo.getFileType());
			URL url = getClient(extranet).generatePresignedUrl(generatePresignedUrlRequest);
			multipartRequestParam.setPartSize(curPartSize);
			multipartRequestParam.setPartNumber(partNumber);
			HashMap<String, Object> headMap = new HashMap<>();
			headMap.put(FileConstants.HEADER_CONTENT_TYPE, fileInfo.getFileType());
			multipartRequestParam.setUrl(url.toString());
			multipartRequestParam.setHeaders(headMap);
			multipartRequestParam.setHttpMethod(FileConstants.HTTP_METHOD_PUT);
			multipartRequestParam.setFormBody(null);
			multipartRequestParam.setStartPos(startPos);
			list.add(multipartRequestParam);
		}
		multipartUploadResult.setList(list);
		return multipartUploadResult;
	}


	@Override
	public boolean completeMultipartUpload(FileInfo fileInfo) {
		try {
			List<PartETag> partETags = listUploadPart(fileInfo.getPath(), fileInfo.getUploadId());
			if(partETags == null || partETags.isEmpty()){
				throw new BizException("请上传分片后再合并");
			}
			if(fileInfo.getPageCount() != partETags.size()){
				throw new BizException("请上传全部分片后再合并");
			}
			CompleteMultipartUploadRequest completeMultipartUploadRequest =
					new CompleteMultipartUploadRequest(config.getBucketName(), fileInfo.getPath(), fileInfo.getUploadId(), partETags);
			CompleteMultipartUploadResult completeMultipartUploadResult = getClient(true).completeMultipartUpload(completeMultipartUploadRequest);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new BizException("合并失败", e);
		}
	}

	private List<PartETag> listUploadPart(String key, String uploadId){
		// 分页列举已上传的分片。
		PartListing partListing;
		ArrayList<PartETag> list = new ArrayList<>();
		ListPartsRequest listPartsRequest = new ListPartsRequest(config.getBucketName(), key, uploadId);
		listPartsRequest.setMaxParts(1000);
		do {
			partListing = getClient(true).listParts(listPartsRequest);
			for (PartSummary part : partListing.getParts()) {
				PartETag partETag = new PartETag(part.getPartNumber(), part.getETag());
				list.add(partETag);
			}
			listPartsRequest.setPartNumberMarker(partListing.getNextPartNumberMarker());
		} while (partListing.isTruncated());
		return list;
	}

}
