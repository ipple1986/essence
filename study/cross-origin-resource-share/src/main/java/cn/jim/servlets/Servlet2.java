package cn.jim.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Servlet2 extends HttpServlet {
    public void init() throws ServletException {
        System.out.println("===Servlet2 init()=========");
    }
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        super.doPost(req,resp);

    }
    // 用此方法，测试单实例 + 多线程问题：每次线程可能一样，也可能是不同线程来处理
    //====Servlet2======doGet=====57==505fdd01
    //====Servlet2======doGet=====59==505fdd01
    //====Servlet2======doGet=====59==505fdd01
    //====Servlet2======doGet=====61==505fdd01
    // 单实例 多线程使用这个实例
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException{
        System.out.println("====Servlet2====doGet()====="+Thread.currentThread().getId()+"=="+Integer.toHexString(hashCode()));
        super.doGet(req,resp);
    }

}
