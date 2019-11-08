package cn.jim.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;

public class Servlet1 extends HttpServlet {
    // 用此方法测试是否调用多次，以及获取配置参数信息
    public void init() throws ServletException {
        super.init();// empty
        // 此方法只在第一次调用时触发
        System.out.println("===Servlet1 init() =========");
        // 从servletConfig读取<servlet> <init-param>下参数
        Enumeration<String> initParameterNames = getServletConfig().getInitParameterNames();
        while(initParameterNames.hasMoreElements()){
            String paramName = initParameterNames.nextElement();
            System.out.println(paramName+" "+ getServletConfig().getInitParameter(paramName));

        }
        System.out.println("======================");
        // 从servletContext读取<context-param>下参数
        Enumeration<String> contextParams = getServletContext().getInitParameterNames();
        while(contextParams.hasMoreElements()){
            String paramName = contextParams.nextElement();
            System.out.println(paramName+" "+ getServletContext().getInitParameter(paramName));
        }

    }

    public void destroy(){
        super.destroy();
        System.out.println("====Servlet1 destroy() =====");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        super.doPost(req,resp);

    }
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        System.out.println("====Servlet1====doGet()=====");
        super.doGet(req,resp);
    }
}
