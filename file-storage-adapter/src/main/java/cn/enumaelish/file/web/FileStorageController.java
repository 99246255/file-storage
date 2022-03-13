package cn.enumaelish.file.web;

import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.dto.storage.AddStorageCmd;
import cn.enumaelish.file.dto.storage.UpdateStorageCmd;
import cn.enumaelish.file.enums.FileConstants;
import com.alibaba.cola.dto.SingleResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: EnumaElish
 * @Date: 2022/2/28 20:01
 * @Description: 此操作低频且数据量极小，所以实现直接放内存，目前是单机，集群后续实现，也可手动调用指定服务的刷新接口
 */
@RequestMapping(FileConstants.PATH + "/storage")
@RestController
@Api(value = "文件存储配置服务",tags = "文件存储配置服务")
public class FileStorageController {

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;

	@DeleteMapping()
	@ApiOperation(value = "删除存储配置")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",paramType = "query",value = "存储id",required = true)
	})
	public SingleResponse<Boolean> delete(@RequestParam("id") Integer id){
		return SingleResponse.of(hydraFileStorageFactory.deleteStorageByStoreId(id));
	}

	@PostMapping()
	@ApiOperation(value = "添加存储配置")
	@ResponseBody
	public SingleResponse<Boolean> addStorageCmd(AddStorageCmd addStorageCmd)  {
		return SingleResponse.of(hydraFileStorageFactory.addStorageCmd(addStorageCmd));
	}


	@PutMapping()
	@ApiOperation(value = "修改存储配置")
	@ResponseBody
	public SingleResponse<Boolean> updateStorageCmd(UpdateStorageCmd updateStorageCmd)  {
		return SingleResponse.of(hydraFileStorageFactory.updateStorageCmd(updateStorageCmd));
	}

	@RequestMapping(value = "/refreshStorageConfig", method = RequestMethod.GET)
	@ApiOperation(value = "刷新存储配置", notes = "刷新存储配置")
	@ResponseBody
	public SingleResponse<Boolean> refreshStorageConfig(){
		hydraFileStorageFactory.refresh();
		return SingleResponse.of(true);
	}

}
