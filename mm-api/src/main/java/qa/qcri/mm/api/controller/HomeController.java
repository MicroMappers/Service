package qa.qcri.mm.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    
    @ResponseBody 
    @RequestMapping(value = "/home")
    public ModelAndView index(){
    	ModelAndView view = new ModelAndView();  
        view.setViewName("home");   
        return view;  
    }
    
    @ResponseBody 
    @RequestMapping(value = "/index")
    public ModelAndView admin(){
    	ModelAndView view = new ModelAndView();  
        view.setViewName("app/index");   
        return view;  
    }
    
}
