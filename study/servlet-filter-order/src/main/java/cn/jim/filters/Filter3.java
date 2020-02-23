package cn.jim.filters;

import javax.servlet.*;
import java.io.IOException;

public class Filter3 implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("==Filter3==init()============");
    }

    // 不调用chain.doFilter(request,response);request将不经过servlet
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            System.out.println("====Filter3===doFilter()======");
            chain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        System.out.println("==Filter3==destroy()============");
    }
}
