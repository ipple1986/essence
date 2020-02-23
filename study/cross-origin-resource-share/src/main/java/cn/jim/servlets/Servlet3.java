package cn.jim.servlets;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Servlet3 extends HttpServlet {
    // 测试 load-on-startup 配置为1 时，tomcat启动时，也实例化此Servlet
    public void init() throws ServletException {
        System.out.println("===Servlet3 init() method=========");
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        super.doPost(req,resp);

    }
    //  用此方法测试
    //  1.RequestDispatcher的跳转,path相对于src/main/webapp目录，必须以 / 开头，可以是静态资源
    //  2.ServletContext()单例容器，而即使是相同路径path的RequestDispatcher都是多实例
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        System.out.println("====Servlet3======doGet()=====");
        //super.doGet(req,resp);
        //RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
        RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/html/index.html");
        System.out.println("测试servletContext,dispatcher 's hashcode   " + getServletContext().hashCode() + "======"  +dispatcher.hashCode());
        dispatcher.forward(req,resp);
    }
}
