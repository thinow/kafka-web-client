package kafkawebclient;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/http/hello")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}