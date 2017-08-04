package trueffect.truconnect.api.oauth.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Rambert Rioja
 */
@Controller("defaultStartPage")
public class DefaultStartPage {
    @RequestMapping("/")
    public String  index() throws Exception {
        return "redirect:/index.jsp";
    }
}
