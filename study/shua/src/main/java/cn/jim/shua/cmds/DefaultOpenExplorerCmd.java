package cn.jim.shua.cmds;

import cn.jim.shua.cmds.orders.OrderItemsSupport;

public class DefaultOpenExplorerCmd extends OrderItemsSupport implements OpenExplorerCmd {

    public DefaultOpenExplorerCmd(String url, String cookies) {
        super(url, cookies);
    }
    @Override
    public void openExplorer() {
        openItems();
    }
}
