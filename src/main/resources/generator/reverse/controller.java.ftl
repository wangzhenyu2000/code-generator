package ${packageName}.controller;

import ${packageName}.entity.${tableInfo.entityName};
import ${packageName}.service.${tableInfo.entityName}Service;
import ${packageName}.common.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * ${tableInfo.comment!tableInfo.entityName} Controller
<#if author?? && author != "">
 * @author ${author}
</#if>
 */
@RestController
@RequestMapping("/${tableInfo.entityName?uncap_first}")
public class ${tableInfo.entityName}Controller {

    @Autowired
    private ${tableInfo.entityName}Service ${tableInfo.entityName?uncap_first}Service;

    @PostMapping
    public Result<Boolean> save(@RequestBody ${tableInfo.entityName} entity) {
        return Result.ok(${tableInfo.entityName?uncap_first}Service.save(entity));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable ${pkType} id) {
        return Result.ok(${tableInfo.entityName?uncap_first}Service.removeById(id));
    }

    @PutMapping
    public Result<Boolean> update(@RequestBody ${tableInfo.entityName} entity) {
        return Result.ok(${tableInfo.entityName?uncap_first}Service.updateById(entity));
    }

    @GetMapping("/{id}")
    public Result<${tableInfo.entityName}> getById(@PathVariable ${pkType} id) {
        return Result.ok(${tableInfo.entityName?uncap_first}Service.getById(id));
    }

    @GetMapping("/page")
    public Result<Page<${tableInfo.entityName}>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.ok(${tableInfo.entityName?uncap_first}Service.page(new Page<>(pageNum, pageSize)));
    }
}
