package by.tux.weatherbot.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class DashboardController {

    @GetMapping()
    public String controlPanel(Model model, HttpServletRequest request){
        model.addAttribute("servletPath", request.getServletPath());
        return "dashboard";
    }

}
