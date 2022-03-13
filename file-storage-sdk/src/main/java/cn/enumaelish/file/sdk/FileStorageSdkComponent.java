package cn.enumaelish.file.sdk;

import cn.enumaelish.file.dto.data.FileUrlDto;
import cn.enumaelish.file.dto.data.GeneratePutUrlResult;
import cn.enumaelish.file.dto.data.MultipartRequestParam;
import cn.enumaelish.file.dto.data.MultipartUploadResult;
import cn.enumaelish.file.dto.file.CompleteMultipartUploadCmd;
import cn.enumaelish.file.dto.file.PermanentFileCmd;
import cn.enumaelish.file.enums.FileConstants;
import cn.enumaelish.file.util.MD5Utils;
import com.alibaba.cola.dto.SingleResponse;
import com.alibaba.cola.exception.BizException;
import okhttp3.*;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @author: EnumaElish
 * @Date: 2022/3/9 14:38
 * @Description: 
 */
@Configuration
@EnableFeignClients(basePackages = {"cn.enumaelish.file.sdk"})
public class FileStorageSdkComponent {


	@Autowired
	FileStorageFeignClient hydraFileFeignThirdClient;

	OkHttpClient client = new OkHttpClient().newBuilder()
			.build();

	/**
	 * 上传文件，小于100M直接上传，否则分片上传
	 * @param file 文件
	 * @param extranet  true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整
	 * @param businessType 业务类型
	 * @return
	 * @throws FileNotFoundException
	 */
	public String uploadFile(File file, boolean extranet, int businessType) {
		String md5 = null;
		try {
			md5 = MD5Utils.getMd5(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException("文件不存在", e);
		}
		long length = file.length();
		if(length > FileConstants.ONE_HUNDRED_M_SIZE){
			return mulUpload(file, md5, extranet, businessType);
		}else{
			return directUpload(file, md5, extranet, businessType);
		}
	}


	/**
	 * 直接上传文件
	 * @param file 文件
	 * @param md5 文件的md5
	 * @param extranet  true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整
	 * @param businessType 业务类型
	 * @return
	 */
	private String directUpload(File file, String md5, boolean extranet, int businessType){
		SingleResponse<GeneratePutUrlResult> generatePutUrlResultSingleResponse = hydraFileFeignThirdClient.generatePutFileUrl(businessType, extranet, file.length(), file.getName(), md5, true);
		if(!generatePutUrlResultSingleResponse.isSuccess()){
			throw new BizException(generatePutUrlResultSingleResponse.getErrMessage());
		}
		GeneratePutUrlResult results = generatePutUrlResultSingleResponse.getData();
		if(!results.isNeedUpload()){
			return results.getFileId();
		}
		Request request;
		if(FileConstants.HTTP_METHOD_PUT.equals(results.getHttpMethod())){
			RequestBody body = RequestBody.create(null, file);
			Request.Builder builder = new Request.Builder()
					.url(results.getUrl())
					.method(results.getHttpMethod().toUpperCase(), body);
			for (Map.Entry<String, Object> entry: results.getHeaders().entrySet()){
				builder.header(entry.getKey(), String.valueOf(entry.getValue()));
			}
			request = builder.build();
		}else if(FileConstants.HTTP_METHOD_POST.equals(results.getHttpMethod())){
			MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
			for (Map.Entry<String,Object> entry: results.getFormBody().entrySet()){

				String value = String.valueOf(entry.getValue());
				if(FileConstants.FILE_VALUE.equals(value)){
					builder.addFormDataPart(entry.getKey(),file.getName(),
                        RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"),file));
				}else{
					builder.addFormDataPart(entry.getKey(),value);
				}
			}
			Request.Builder requestBuilder = new Request.Builder()
					.url(results.getUrl())
					.method("POST", builder.build());
			for (Map.Entry<String, Object> entry: results.getHeaders().entrySet()){
				requestBuilder.header(entry.getKey(), String.valueOf(entry.getValue()));
			}
			request = requestBuilder.build();
		}else{
			throw new BizException("暂不支持此实现");
		}
		try {
			Response response = client.newCall(request).execute();
			if(response.isSuccessful()){
				return results.getFileId();
			}
			throw new BizException("上传文件失败");
		} catch (IOException e) {
			throw new BizException("上传文件失败",e);
		}

	}


	/**
	 * 分片上传
	 * @param file 文件
	 * @param md5 文件的md5
	 * @param extranet  true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整
	 * @param businessType 业务类型
	 * @return
	 */
	private String mulUpload(File file, String md5, boolean extranet, int businessType){
		SingleResponse<MultipartUploadResult> multipartUpload = hydraFileFeignThirdClient.multipartUpload(businessType, extranet, file.length(), file.getName(), md5, true);
		if(!multipartUpload.isSuccess()){
			throw new BizException(multipartUpload.getErrMessage());
		}
		MultipartUploadResult results = multipartUpload.getData();
		if(!results.isNeedUpload()){
			return results.getFileId();
		}
		List<MultipartRequestParam> list = results.getList();
		for(MultipartRequestParam param: list) {
			if (FileConstants.HTTP_METHOD_PUT.equals(param.getHttpMethod())) {
				try {
					RequestBody body = new RequestBody() {
						@Override
						public MediaType contentType() {
							return MediaType.parse(file.getName());
						}

						@Override
						public void writeTo(BufferedSink sink) throws IOException {
							Source source = null;
							try {
								InputStream instream = new FileInputStream(file);
								// 跳过已经上传的分片。
								instream.skip(param.getStartPos());
								source = Okio.source(instream);
								sink.write(source, param.getPartSize());
							} finally {
								Util.closeQuietly(source);
							}
						}
					};
					Request.Builder builder = new Request.Builder()
							.url(param.getUrl())
							.method(param.getHttpMethod().toUpperCase(), body);
					for (Map.Entry<String, Object> entry : param.getHeaders().entrySet()) {
						builder.header(entry.getKey(), String.valueOf(entry.getValue()));
					}
					Response response = client.newCall(builder.build()).execute();
					if (!response.isSuccessful()) {
						throw new BizException(new String(response.body().bytes()));
					}
				} catch (IOException e) {
					throw new BizException("上传失败", e);
				}
			} else{
				throw new BizException("暂不支持此分配上传方式");
			}
		}
		SingleResponse<Boolean> booleanSingleResponse = hydraFileFeignThirdClient.completeMultipartUpload(new CompleteMultipartUploadCmd(results.getFileId()));
		if(!booleanSingleResponse.isSuccess()){
			throw new BizException(booleanSingleResponse.getErrMessage());
		}
		return results.getFileId();
	}


	/**
	 * 获取文件流
	 * @param fileId 文件标识
	 * @param extranet  true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整
	 * @return
	 */
	public InputStream downloadFile(String fileId, boolean extranet) {
		feign.Response response = hydraFileFeignThirdClient.downFile(fileId, extranet);
		try {
			if(response.status() == 200) {
				return response.body().asInputStream();
			}else{
				throw new BizException("找不到文件" +fileId);
			}
		} catch (IOException e) {
			throw new BizException("找不到文件" + fileId, e);
		}

	}

	/**
	 * 获取文件下载url
	 * @param fileId 文件标识
	 * @param extranet  true 外网， false:内网,默认外网，一些特殊情况可通过此参数调整
	 * @param expireTime 过期时间(毫秒），默认3600000L，一小时
	 * @return
	 */
	public FileUrlDto getDownloadFileUrl(String fileId, boolean extranet, Long expireTime){
		SingleResponse<FileUrlDto> downloadFileUrl = hydraFileFeignThirdClient.getDownloadFileUrl(fileId, extranet, expireTime);
		if(!downloadFileUrl.isSuccess()){
			throw new BizException(downloadFileUrl.getErrMessage());
		}
		return downloadFileUrl.getData();
	}

	/**
	 * 永久文件标记
	 * @param fileId 文件标识
	 * @return
	 */
	public Boolean permanent(String fileId) {
		SingleResponse<Boolean> permanent = hydraFileFeignThirdClient.permanent(new PermanentFileCmd(fileId));
		if(!permanent.isSuccess() ){
			throw new BizException(permanent.getErrMessage());
		}
		return permanent.getData();
	}
}
