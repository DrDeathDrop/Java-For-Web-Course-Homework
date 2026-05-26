package pu.fmi.webprogramming.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping
    //@RequestMapping(method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute(
                "message",
                "Hello World"
        );
        return "index.html";
    }

}
