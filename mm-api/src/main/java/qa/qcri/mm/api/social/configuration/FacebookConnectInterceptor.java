package qa.qcri.mm.api.social.configuration;

import org.socialsignin.springsocial.security.signin.SpringSocialSecurityConnectInterceptor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Component;

@Component
public class FacebookConnectInterceptor extends SpringSocialSecurityConnectInterceptor<Facebook> {

}
