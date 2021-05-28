package com.hrbeu.conf;

import com.hrbeu.pojo.User;
import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping(value = "/error")
public class MyErrorController extends BasicErrorController {
    Logger logger =Logger.getLogger(MyErrorController.class);

    public MyErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties) {
        super(errorAttributes, errorProperties);
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        HttpStatus status = getStatus(request);
        Map<String, Object> model = Collections.unmodifiableMap(getErrorAttributes(
                request, isIncludeStackTrace(request, MediaType.TEXT_HTML)));
        response.setStatus(status.value());
        ModelAndView modelAndView = resolveErrorView(request, response, status, model);
        if(modelAndView!=null){
            return modelAndView;
        }else {
            String path=model.get("path").toString();
            logger.debug("该地址无法访问:"+path);
            if(path.startsWith("/admin/")){
                return new ModelAndView("error/admin/error", model);
            }else {
                return new ModelAndView("error/front/error", model);
            }

        }
    }
}
