package co.mx.davincho20.dockerspringlayering;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class DockerApplication {

    @Value("${message}")
    String message;

    @RequestMapping("/")
    public String home() {
        return message;
    }

    public static void main(String... args) {
        SpringApplication.run(DockerApplication.class);
    }
}
