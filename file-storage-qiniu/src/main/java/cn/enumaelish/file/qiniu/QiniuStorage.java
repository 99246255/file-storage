package cn.enumaelish.file.qiniu;

import cn.enumaelish.file.domain.gateway.FileStorage;
import cn.enumaelish.file.domain.model.FileInfo;
import cn.enumaelish.file.domain.model.StoragePlatform;
import cn.enumaelish.file.domain.model.gateway.PutObjectBytesCmd;
import cn.enumaelish.file.domain.spi.Extension;
import cn.enumaelish.file.util.Base64Utils;
import cn.enumaelish.file.util.JsonUtil;
import cn.enumaelish.file.util.MD5Utils;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.dto.data.MultipartRequestParam;
import cn.enumaelish.file.dto.data.MultipartUploadResult;
import cn.enumaelish.file.enums.FileConstants;
import com.alibaba.cola.exception.BizException;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import okhttp3.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: EnumaElish
 * @Date: 2022/2/14 16:35
 * @Description: TODO 七牛一定是外网，没有内网，所以没有实现内网逻辑，如果有这样需求，请调整实现
 */
@Extension("1")
public class QiniuStorage implements FileStorage<QiniuConfigStorage> {

	private StoragePlatform storagePlatform;

	private QiniuConfigStorage qiniuConfig;

	private BucketManager bucketManager;

	private UploadManager uploadManager;

	private OkHttpClient client;
	@Override
	public StoragePlatform getPlatformStore() {
		return storagePlatform;
	}

	@Override
	public void init(StoragePlatform<QiniuConfigStorage> storagePlatform) {
		this.storagePlatform = storagePlatform;
		qiniuConfig = storagePlatform.getConfig();
		if(qiniuConfig == null){
			throw new BizException(String.format("七牛配置错误：%s", JsonUtil.toJSONString(storagePlatform)));
		}
		bucketManager = new BucketManager(Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getAccessSecret()), new Configuration(Region.autoRegion()));
		uploadManager = new UploadManager(new Configuration(Region.autoRegion()));

		client = new OkHttpClient().newBuilder().build();
	}

	@Override
	public String save(PutObjectBytesCmd putObjectBytesCmd) {
		String key = putObjectBytesCmd.getKey();
		StringMap putPolicy = new StringMap();
		// 七牛不返回md5并且有时候会报错，所以只能用这种方式，这种不能传大文件
		byte[] bytes = MD5Utils.toByteArray(putObjectBytesCmd.getInputStream());
		String md5 = MD5Utils.stringToMd5(bytes);
		try {
			Response put = uploadManager.put(bytes, key, getUploadToken(key, putPolicy), putPolicy, putObjectBytesCmd.getContentType(), false);
			if(put.isOK()){
				return md5;
			}
			throw new BizException(String.format("上传七牛云失败%s,失败原因%s",key ,put.error));
		} catch (QiniuException e) {
			throw new BizException(String.format("上传七牛云失败%s",key),e);
		}

	}

	@Override
	public boolean delete(String key) {
		try {
			Response response = bucketManager.delete(qiniuConfig.getBucketName(), key);
			if(response.isOK()){
				return true;
			}
			throw new BizException(String.format("删除七牛云文件出错: %s", response.toString()));
		} catch (QiniuException e) {
			throw new BizException(String.format("删除七牛云文件出错: %s", e.getMessage()));
		}
	}

	@Override
	public boolean check(FileInfo fileInfo) {
		try {
			com.qiniu.storage.model.FileInfo stat = bucketManager.stat(qiniuConfig.getBucketName(), fileInfo.getPath());
			if(fileInfo.isCheckMd5()){
				if(!stat.md5.equals(fileInfo.getMd5())){
					return false;
				}
			}
			return stat.fsize == fileInfo.getSize();
		} catch (QiniuException e) {
			return false;
		}
	}

	private String getUploadToken(String key,StringMap putPolicy){
		Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getAccessSecret());
		return auth.uploadToken(qiniuConfig.getBucketName(), key, 3600, putPolicy);
	}
	
	@Override
	public String getDownloadFileUrl(String key,String fileName, long expireTime, boolean extranet) {
		String encodedFileName = null;
		try {
			encodedFileName = URLEncoder.encode(key, "utf-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new BizException("文件名称编码错误");
		}
		String publicUrl = String.format("%s/%s", qiniuConfig.getDomain(), encodedFileName);
		Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getAccessSecret());
		return auth.privateDownloadUrl(publicUrl, expireTime/1000L);
	}

	@Override
	public GeneratePutUrlResult generatePutUrl(FileInfo fileInfo, boolean extranet) {
		// TODO 后续回调认证
		GeneratePutUrlResult generatePutUrlResult = new GeneratePutUrlResult();
		Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getAccessSecret());
		StringMap putPolicy = new StringMap();
		// 限制文件上传大小
		putPolicy.put("fsizeLimit", fileInfo.getSize());
		// 返回格式
		putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
		// 限制上传文件大小
		putPolicy.put("fsizeLimit",fileInfo.getSize());
		// 限制文件类型
		putPolicy.put("mimeLimit", fileInfo.getFileType());
		long expireSeconds = 3600;
		String token = auth.uploadToken(qiniuConfig.getBucketName(), fileInfo.getPath(), expireSeconds, putPolicy);
		//非华东空间需要根据注意事项 1 修改上传域名
		generatePutUrlResult.setUrl(qiniuConfig.getUploadUrl());
		generatePutUrlResult.setHttpMethod(FileConstants.HTTP_METHOD_POST);
		generatePutUrlResult.setFileId(fileInfo.getFileId());
		generatePutUrlResult.setHeaders(new HashMap<>(4));
		Map<String, Object> body = new HashMap<>(8);
		body.put("token",token);
		body.put(FileConstants.FILE_KEY, FileConstants.FILE_VALUE);
		body.put("key", fileInfo.getPath());
		generatePutUrlResult.setFormBody(body);
		return generatePutUrlResult;
	}

	@Override
	public String initiateMultipartUpload(FileInfo fileInfo, boolean extranet) {
		Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getAccessSecret());
		String upToken = auth.uploadToken(qiniuConfig.getBucketName(), fileInfo.getPath());
		RequestBody body = new FormBody.Builder().build();
		String url = getUrl(fileInfo.getPath());
		Request request = new Request.Builder()
				.url(url)
				.method("POST", body)
				.header("Authorization", "UpToken " + upToken)
				.build();
		okhttp3.Response response = null;
		try {
			response = client.newCall(request).execute();
			Map<String,String> map = JsonUtil.toBean(new String(response.body().bytes()), Map.class);
			return map.get("uploadId");
		} catch (IOException e) {
			throw new BizException("初始化分片失败", e);
		}
	}

	@Override
	public MultipartUploadResult generateMultipartUrl(FileInfo fileInfo, boolean extranet) {
		MultipartUploadResult multipartUploadResult = new MultipartUploadResult();
		multipartUploadResult.setFileId(fileInfo.getFileId());
		Long fileLength = fileInfo.getSize();
		long pageSize = fileInfo.getFileMulPartPageSize();
		int partCount = fileInfo.getPageCount();
		Auth auth = Auth.create(qiniuConfig.getAccessKey(), qiniuConfig.getAccessSecret());
		String upToken = auth.uploadToken(qiniuConfig.getBucketName(), fileInfo.getPath());
		List<MultipartRequestParam> list = new ArrayList<>();
		String url = getUrl(fileInfo.getPath());
		for (int i = 0; i < partCount ; i++) {
			MultipartRequestParam multipartRequestParam = new MultipartRequestParam();
			int partNumber = i + 1;
			multipartRequestParam.setPartNumber(partNumber);
			long startPos = i * pageSize;
			long curPartSize = (partNumber == partCount) ? (fileLength - startPos) : pageSize;
			multipartRequestParam.setUrl(new StringBuilder().append(url)
					.append(FileConstants.SEPARATOR)
					.append(fileInfo.getUploadId())
					.append(FileConstants.SEPARATOR)
					.append(partNumber).toString());
			multipartRequestParam.setPartSize(curPartSize);
			multipartRequestParam.setPartNumber(partNumber);
			HashMap<String, Object> headMap = new HashMap<>();
			headMap.put(FileConstants.HEADER_CONTENT_TYPE, "application/json");
			headMap.put("Authorization", "UpToken " + upToken);
			multipartRequestParam.setHeaders(headMap);
			multipartRequestParam.setHttpMethod(FileConstants.HTTP_METHOD_PUT);
			multipartRequestParam.setFormBody(null);
			multipartRequestParam.setStartPos(startPos);
			list.add(multipartRequestParam);
		}
		multipartUploadResult.setList(list);
		return multipartUploadResult;
	}

	private String getUrl(String path) {
		return new StringBuilder().append(qiniuConfig.getUploadUrl())
				.append("/buckets/").append(qiniuConfig.getBucketName())
				.append("/objects/").append(Base64Utils.encode(path.getBytes(StandardCharsets.UTF_8))).append("/uploads").toString();
	}

	@Override
	public boolean completeMultipartUpload(FileInfo fileInfo) {
		QiniuPartDto qiniuPartDto = listUploadPart(fileInfo.getPath(), fileInfo.getUploadId());
		if(qiniuPartDto.getParts() == null || qiniuPartDto.getParts().isEmpty()){
			throw new BizException("请上传分片后再合并");
		}
		if(fileInfo.getPageCount() != qiniuPartDto.getParts().size()){
			throw new BizException("请上传全部分片后再合并");
		}
		String content = JsonUtil.toJSONString(qiniuPartDto);
		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
		Request request = new Request.Builder()
				.url(getUrl(fileInfo.getPath()) + "/" + fileInfo.getUploadId())
				.method("POST", body)
				.header(FileConstants.HEADER_CONTENT_TYPE, "application/json")
				.header("Authorization", "UpToken " + getUploadToken(fileInfo.getPath(), null))
				.build();
		try {
			okhttp3.Response response = client.newCall(request).execute();
			if(response.isSuccessful()){
				return true;
			}
			throw new BizException(new String(response.body().bytes()));
		} catch (IOException e) {
			throw new BizException("合并失败", e);
		}
	}

	private QiniuPartDto listUploadPart(String key, String uploadId){
		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");
		Request request = new Request.Builder()
				.url(getUrl(key) + "/" + uploadId)
				.method("GET", null)
				.header(FileConstants.HEADER_CONTENT_TYPE, "application/json")
				.header("Authorization", "UpToken " + getUploadToken(key, null))
				.build();
		try {
			okhttp3.Response response = client.newCall(request).execute();
			if(response.isSuccessful()){
				return JsonUtil.toBean(new String(response.body().bytes()), QiniuPartDto.class);
			}
			throw new BizException(new String(response.body().bytes()));
		} catch (IOException e) {
			throw new BizException("合并失败", e);
		}
	}
}
