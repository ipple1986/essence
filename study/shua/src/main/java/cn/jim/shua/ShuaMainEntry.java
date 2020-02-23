package cn.jim.shua;

import cn.jim.shua.cmds.DefaultOpenExplorerCmd;
import cn.jim.shua.cmds.OpenExplorerCmd;
import cn.jim.shua.cmds.orders.OrderItemsSupport;

public class ShuaMainEntry {
    public static void  main(String... args){
        OpenExplorerCmd openExplorerCmd = null;
        String url = System.getProperty("url", OrderItemsSupport.REMOTE_HOST);
        System.out.println("user = "+System.getProperty("user"));
        if("zw".equals(System.getProperty("user","zw"))){
            openExplorerCmd = new DefaultOpenExplorerCmd(url,"sap5rd4l5s0l6mjgp0mrats2ag");
        }else{ // change cookies
            openExplorerCmd = new DefaultOpenExplorerCmd(url,"k3oqmevfidn2lbft7ba6ug172b");
        }
        openExplorerCmd.openExplorer();
    }
}
