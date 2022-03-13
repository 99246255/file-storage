package cn.enumaelish.file.web;

import cn.enumaelish.file.domain.ability.HydraFileStorageFactory;
import cn.enumaelish.file.dto.business.SaveBusinessTypeCmd;
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
 * @Description:
 */
@RequestMapping(FileConstants.PATH + "/business")
@RestController
@Api(value = "业务类型维护",tags = "业务类型维护")
public class FileBusinessController {

	@Autowired
	HydraFileStorageFactory hydraFileStorageFactory;

	@DeleteMapping()
	@ApiOperation(value = "删除业务类型")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "id",paramType = "query",value = "业务类型",required = true)
	})
	public SingleResponse<Boolean> delete(@RequestParam("id") Integer id){
		return SingleResponse.of(hydraFileStorageFactory.deleteBusinessType(id));
	}

	@PostMapping()
	@ApiOperation(value = "保存存储配置")
	public SingleResponse<Boolean> addStorageCmd(SaveBusinessTypeCmd saveBusinessTypeCmd)  {
		return SingleResponse.of(hydraFileStorageFactory.saveBusinessType(saveBusinessTypeCmd));
	}


}
