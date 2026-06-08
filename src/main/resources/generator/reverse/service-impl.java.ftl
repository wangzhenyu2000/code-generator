package ${packageName}.service.impl;

import ${packageName}.entity.${tableInfo.entityName};
import ${packageName}.mapper.${tableInfo.entityName}Mapper;
import ${packageName}.service.${tableInfo.entityName}Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ${tableInfo.entityName}ServiceImpl
        extends ServiceImpl<${tableInfo.entityName}Mapper, ${tableInfo.entityName}>
        implements ${tableInfo.entityName}Service {
}
