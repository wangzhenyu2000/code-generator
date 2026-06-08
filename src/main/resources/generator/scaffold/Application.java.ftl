package ${packageName};

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("${packageName}.mapper")
public class ${projectName?cap_first}Application {
    public static void main(String[] args) {
        SpringApplication.run(${projectName?cap_first}Application.class, args);
    }
}
