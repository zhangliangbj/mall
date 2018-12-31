package com.mmall.controller.commom.interceptor;

import com.github.pagehelper.StringUtil;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
       log.info("preHandle");
       //请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) o;

        //解析HandlerHandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数，具体的参数key以及value是什么，我们打印日志
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator iterator = paramMap.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String mapKey = (String) entry.getKey();
            String mapValue = StringUtils.EMPTY;

            //request这个参数的map，里面的value返回的是一个String[]
            Object obj = entry.getValue();
            if (obj instanceof String[]) {
                String[] str = (String[]) obj;
                mapValue = Arrays.toString(str);
            }

            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        if (StringUtils.equals(className,"UserManageController")&&StringUtils.equals(methodName,"login")){
            log.info("权限拦截器拦截到请求，className：{}，methodName:{}",className,methodName);
            //登录请求不打印参数，有密码
            return  true;
        }



            User user = null;
            String loginToken = CookieUtil.readLoginToken(httpServletRequest);
            if(StringUtils.isNotEmpty(loginToken)){
                String userJsonStr = RedisShardedPoolUtil.get(loginToken);
                user = JsonUtil.string2Obj(userJsonStr,User.class);
                }
            if (user == null || (user.getRole().intValue()!= Const.Role.ROLE_ADMIN)){
            //返回false，即不会调用controller的方法
                httpServletResponse.reset();;//note:这里要添加reset，否则报异常getWriter has already been called for this response
                httpServletResponse.setCharacterEncoding("UTF-8");//设置编码，否则乱码
                httpServletResponse.setContentType("application/json;charset=UTF-8");

                PrintWriter out = httpServletResponse.getWriter();

                //上传由于富文本的控件要求，要特殊处理返回值，这里区分是否登录以及是否有权限
                if (user == null){
                    if (StringUtils.equals(className,"UserManageController")&&StringUtils.equals(methodName,"richtext_img_upload")){
                        log.info("权限拦截器拦截到请求，className：{}，methodName:{}",className,methodName);
                        Map resultMap = Maps.newHashMap();
                        resultMap.put("success",false);
                        resultMap.put("msg","请登录管理员");
                        out.print(JsonUtil.obj2String(resultMap));
                    }else{
                        out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户未登录")));
                    }
                }else {
                    if (StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "richtext_img_upload")) {
                        log.info("权限拦截器拦截到请求，className：{}，methodName:{}", className, methodName);
                        Map resultMap = Maps.newHashMap();
                        resultMap.put("success", false);
                        resultMap.put("msg", "无权限操作");
                        out.print(JsonUtil.obj2String(resultMap));
                    } else {
                        out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截，用户无权限操作")));
                    }
                }
                out.flush();
                out.close();//关闭流

                return false;
            }
                 return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
