package org.yunshanmc.lmc.core.gui;

public interface InvProvider<TInv> {

    /**
     * 构建适用于服务端的箱子gui界面
     *
     * @param gui 用于构建的gui
     * @return 构建好的箱子gui
     */
    TInv buildInv(Gui gui);
}
