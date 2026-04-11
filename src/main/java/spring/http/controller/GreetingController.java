package spring.http.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import spring.database.entity.Role;
import spring.database.repository.CompanyRepository;
import spring.dto.UserReadDto;

import java.util.Arrays;
import java.util.List;

@Controller
@SessionAttributes({"user"})
@RequestMapping("/api/v1")
public class GreetingController {

    @ModelAttribute("roles")
    public List<Role> getRoles() {
        return Arrays.asList(Role.values());
    }

    @GetMapping("/hello")
    public String hello(Model model,
                              UserReadDto userReadDto
//                        ModelAndView mv, CompanyRepository companyRepository,
//                        HttpServletRequest request,
//                              @RequestParam("age") Integer age,
//                              @RequestHeader("accept") String accept,
//                              @CookieValue("JSESSIONID") String jsessionId,
//                              @PathVariable("id") Integer id
    ) {
        model.addAttribute("user",
                userReadDto);
        return "greeting/hello";
    }

    @GetMapping("/bye")
    public ModelAndView bye(ModelAndView mv,
                            @SessionAttribute("user") UserReadDto user) {
        mv.setViewName("greeting/bye");
        return mv;
    }
}
