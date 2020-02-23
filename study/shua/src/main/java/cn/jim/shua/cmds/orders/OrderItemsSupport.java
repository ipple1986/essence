package cn.jim.shua.cmds.orders;

import cn.jim.shua.http.HttpRequestSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderItemsSupport extends HttpRequestSupport  implements  OrderItemsOpenable{

    //public static final String REMOTE_HOST = "http://47.99.117.186:17008";
    public static final String REMOTE_HOST = "http://120.41.41.104:27008";
    private static final String ORDER_LIST_URL = "/api/order/getMyOrderList?status=2&page=1";
    private static final String ORDER_DETAIL_URL_FORMAT = "/api/order/getDetail?orderid=%s";

    private static final String TAOBAO_HOST_URL = "https://www.taobao.com";
    private String url;
    private String cookies;

    public  OrderItemsSupport(String url,String cookies){
        this.url = (url==null || "".equals(url))?REMOTE_HOST:url;
        this.cookies = cookies;

    }
    private static final Pattern DETAIL_ORDER_ITEMS_PATTERM = Pattern.compile("\"tasktype\":([\\d]+),\"ordertype\":\\[[^\\]]+\\],\"orderid\":([\\d]+)");
    protected  List<String> getDetailUrls(){
        String result = getRequest(this.url.concat(ORDER_LIST_URL),this.cookies);
        System.out.println(result);
        Matcher taskIdMatcher = DETAIL_ORDER_ITEMS_PATTERM.matcher(result);
        List<String> resutList = new ArrayList<>(20);
        while(taskIdMatcher.find()){// 浏览单 100，预售单 5
           // if(taskIdMatcher.group(1).equals("100")){
                resutList.add(String.format(this.url.concat(ORDER_DETAIL_URL_FORMAT),taskIdMatcher.group(2)));
            //}
        }
        return resutList;
    }
    protected  List<String> getUrls(List<String> urls){
        List<String> resutList = new ArrayList<>(20);
        Pattern urlPatter = Pattern.compile("\"url\":\"([^\"]+)?");
        for(String url:urls){
            String result = getRequest(url,this.cookies);
            Matcher urlMatcher = urlPatter.matcher(result);
            while(urlMatcher.find()){
                resutList.add(urlMatcher.group(1));
            }
        }
        return resutList;
    }
    private static final String chromeExeRelativePath = String.format("%sGoogle%sChrome%sApplication%schrome.exe", File.separator, File.separator, File.separator, File.separator);
    private static final String chromeExecutablePath = System.getenv("LOCALAPPDATA").concat(chromeExeRelativePath);
    private static void openURLs(List<String> taskIds) throws InterruptedException {
        taskIds.add(TAOBAO_HOST_URL);
        for(String target : taskIds){
            Thread.sleep(1000);
            List<String> cmd = new ArrayList<String>();
            cmd.add(chromeExecutablePath);
            target = target.replaceAll("\\\\/","/");
            cmd.add(target);
            ProcessBuilder process = new ProcessBuilder(cmd);
            try {
                System.out.println("正在打开网页："+target);
                process.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            cmd = null;
        }
    }

    @Override
    public void openItems() {
        List<String> taskids = getDetailUrls();
        taskids = getUrls(taskids);
        try {
            openURLs(taskids);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public static void main(String ... args){
        // 方法内部类
        class A{
            private int i = 1;
            @Override
            public boolean equals(Object o){
                return Boolean.TRUE;
            }
            @Override
            public int hashCode(){
                return i++;
            }
        }
        A a = new A();
        if(a.equals("1") && a.equals("2") && a.equals("3")){
            System.out.println("1");
        }
        if(a.hashCode() == 1 && a.hashCode() ==2 && a.hashCode() ==3){
            System.out.println("2");
        }
    }
}
