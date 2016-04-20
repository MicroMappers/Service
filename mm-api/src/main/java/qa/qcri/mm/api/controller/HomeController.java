package qa.qcri.mm.api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import qa.qcri.mm.api.RoleType;
import qa.qcri.mm.api.aidr_predict_entity.UserAccount;
import qa.qcri.mm.api.service.UserService;
import qa.qcri.mm.api.util.CommonUtil;

@Controller
@PreAuthorize("hasRole('ROLE_USER_SPRINGSOCIALSECURITY')")
public class HomeController {
    
	@Autowired
	private UserService userService;
	
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
    
    @ResponseBody 
    @RequestMapping(value = "/rest/isadmin")
    public Map<String, Object> isCurrentUserIsAdmin() throws Exception{
		UserAccount userAccount = getAuthenticatedUser();
		if (userAccount != null) {
			List<RoleType> userRoles = userService.getUserRoles(userAccount
					.getId());
			if (userRoles != null) {
				for (RoleType userRole : userRoles) {
					if (userRole.equals(RoleType.ADMIN)) {
						return CommonUtil.returnSuccess("User is Admin", "ADMIN");
					}
				}
			}
			return CommonUtil.returnSuccess("User is not Admin", "NOT_ADMIN");
		} else {
			return CommonUtil.returnSuccess("User Not Logged In", "NOT_SIGNED_IN");
		}
	}
    
    public UserAccount getAuthenticatedUser() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return userService.fetchByUserName(authentication.getName());
		} else {
			throw new Exception("No user logged in ");
		}
	}
}
