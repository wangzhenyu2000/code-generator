package ${packageName}.mapper;

import ${packageName}.entity.${tableInfo.entityName};
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ${tableInfo.entityName}Mapper extends BaseMapper<${tableInfo.entityName}> {
}
