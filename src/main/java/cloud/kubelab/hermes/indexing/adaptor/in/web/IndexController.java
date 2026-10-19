package cloud.kubelab.hermes.indexing.adaptor.in.web;


import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("writer")
@RestController
public class IndexController {

    @GetMapping("/ping")
    public String ping(){
        return "PONG Writer";
    }
}
