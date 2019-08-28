package org.yunshanmc.lmc.core.bukkit.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.yunshanmc.lmc.core.bukkit.LMCCoreBukkitPlugin;
import org.yunshanmc.lmc.core.gui.ClickInfo;
import org.yunshanmc.lmc.core.gui.Gui;
import org.yunshanmc.lmc.core.gui.components.Container;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntFunction;

public class InvHolder implements InventoryHolder {

    private final Gui gui;
    private final Inventory inventory;

    public InvHolder(Gui gui) {
        this.gui = gui;
        this.inventory = Bukkit.createInventory(this, gui.getRow() * 9, gui.getTitle());
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void handleDrag(InventoryDragEvent event) {
        ItemStack cursor = event.getCursor();
        // bukkit的实现无法直接更改event.getNewItems()的返回值来改变添加的内容，所以这里在事件结束之后删除多出来的物品
        List<Map.Entry<Integer, ItemStack>> toRemove = new ArrayList<>();

        for (Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
            int slot = entry.getKey();
            if (slot < this.gui.getSize() && !this.gui.canInput(slot, entry.getValue())) {
                if (this.isNullItem(cursor)) {
                    cursor = entry.getValue().clone();
                    cursor.setAmount(0);
                }
                ItemStack item = this.inventory.getItem(slot);
                if (!this.isNullItem(item)) {
                    entry.getValue().setAmount(entry.getValue().getAmount() - item.getAmount());
                }
                cursor.setAmount(cursor.getAmount() + entry.getValue().getAmount());
                toRemove.add(entry);
            }
        }

        event.setCursor(cursor);
        Bukkit.getScheduler().runTask(JavaPlugin.getPlugin(LMCCoreBukkitPlugin.class), () -> toRemove.forEach(entry -> {
            int slot = entry.getKey();
            if (!this.gui.canInput(slot, entry.getValue())) {
                ItemStack raw = this.inventory.getItem(slot);
                raw.setAmount(raw.getAmount() - entry.getValue().getAmount());
                this.inventory.setItem(slot, raw);
            }
        }));
    }

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getClickedInventory() == null) {
            // 没有点击格子
            return;
        }

        ClickInfo.ClickType type = transformClickType(event);

        if (event.getClickedInventory().getHolder() == this) {
            this.gui.onClick(event.getRawSlot(), new ClickInfo(type), event.getWhoClicked());
        }

        this.handleAction(event);
    }

    /**
     * 根据action判断是否需要取消事件
     *
     * @param event 点击事件
     */
    @SuppressWarnings("deprecation")
    private void handleAction(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        ItemStack currentItem = event.getCurrentItem();
        switch (event.getAction()) {
            case PICKUP_ALL:
            case PICKUP_SOME:
            case PICKUP_HALF:
            case PICKUP_ONE: {
                if (event.getClickedInventory().getHolder() == this) {
                    event.setCancelled(!this.gui.canOutput(event.getSlot()));
                } else {
                    event.setCancelled(false);
                }
                break;
            }
            case PLACE_ALL:
            case PLACE_SOME:
            case PLACE_ONE:
            case SWAP_WITH_CURSOR: {
                if (event.getClickedInventory().getHolder() == this) {
                    event.setCancelled(!this.gui.canInput(event.getSlot(), cursor));
                } else {
                    event.setCancelled(false);
                }
                break;
            }
            case DROP_ALL_CURSOR:
            case DROP_ONE_CURSOR:
            case DROP_ALL_SLOT:
            case DROP_ONE_SLOT: {
                if (event.getClickedInventory().getHolder() == event.getWhoClicked()) {
                    event.setCancelled(false);
                }
                break;
            }
            case MOVE_TO_OTHER_INVENTORY: {
                if (event.getClickedInventory().getHolder() == this) {
                    event.setCancelled(!this.gui.canOutput(event.getSlot()));
                } else {
                    this.addItem(currentItem);
                    event.setCurrentItem(currentItem);
                }
                break;
            }
            case HOTBAR_SWAP:
            case HOTBAR_MOVE_AND_READD: {
                if (event.getClickedInventory().getHolder() == this) {
                    ItemStack item = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
                    if (isNullItem(item)) {
                        event.setCancelled(!this.gui.canOutput(event.getSlot()));
                    } else {
                        event.setCancelled(!this.gui.canInput(event.getSlot(), item));
                    }
                } else {
                    event.setCancelled(false);
                }
                break;
            }
            case COLLECT_TO_CURSOR: {
                if (!isNullItem(cursor)) {
                    this.collectItem(event.getWhoClicked().getInventory(), cursor);
                    // 根据文档说明，这里直接设置可能出现gui中物品错乱(刷物品/遗失物品)，暂时未发现问题，但是如果出现bug可以检查是否是这个地方的问题
                    event.setCursor(cursor);
                }
                break;
            }
            case CLONE_STACK: {
                // 仅限op复制(即便禁止op复制也可以使用give指令获取，故没必要。但是禁止普通玩家创造模式复制是有意义的)
                if (event.getWhoClicked().isOp()) {
                    event.setCancelled(false);
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * 转换Bukkit的{@link ClickType}到LMC-Core的{@link org.yunshanmc.lmc.core.gui.ClickInfo.ClickType}
     *
     * @param event 点击事件
     * @return 转换后的click type
     */
    private static ClickInfo.ClickType transformClickType(InventoryClickEvent event) {
        ClickType click = event.getClick();
        ClickInfo.ClickType type;
        // double click必须在isLeftClick前面，因为double click也是left click
        if (click == ClickType.DOUBLE_CLICK) {
            type = ClickInfo.ClickType.DOUBLE_CLICK;
        } else if (click.isLeftClick()) {
            if (click.isShiftClick()) {
                type = ClickInfo.ClickType.SHIFT_LEFT;
            } else {
                type = ClickInfo.ClickType.LEFT;
            }
        } else if (click.isRightClick()) {
            if (click.isShiftClick()) {
                type = ClickInfo.ClickType.SHIFT_RIGHT;
            } else {
                type = ClickInfo.ClickType.RIGHT;
            }
        } else if (click == ClickType.NUMBER_KEY) {
            type = ClickInfo.ClickType.NUMBER_KEY;
        } else if (click == ClickType.MIDDLE) {
            type = ClickInfo.ClickType.MIDDLE;
        } else if (click == ClickType.DROP) {
            type = ClickInfo.ClickType.DROP;
        } else if (click == ClickType.CONTROL_DROP) {
            type = ClickInfo.ClickType.CONTROL_DROP;
        } else {
            type = ClickInfo.ClickType.UNKNOWN;
        }
        return type;
    }

    private boolean isNullItem(ItemStack item) {
        return item == null || item.getType() == Material.AIR;
    }

    private void collectItem(PlayerInventory playerInventory, ItemStack toCollect) {
        BiFunction<Inventory, Integer, Boolean> collect = (inv, slot) -> {
            ItemStack item = inv.getItem(slot);
            if (!this.isNullItem(item) && item.isSimilar(toCollect)) {
                int add = Math.min(toCollect.getMaxStackSize() - toCollect.getAmount(), item.getAmount());
                if (add > 0) {
                    toCollect.setAmount(toCollect.getAmount() + add);
                    item.setAmount(item.getAmount() - add);
                    if (item.getAmount() == 0) {
                        item.setType(Material.AIR);
                    }
                    inv.setItem(slot, item);
                }
            }
            return toCollect.getAmount() >= toCollect.getMaxStackSize();
        };
        // 这个变量保存原本是刚好满stack size的，优先收集不满的
        List<Map.Entry<Inventory, Integer>> recheck = new ArrayList<>();

        for (Container container : this.gui.getContainers()) {
            if (container.canOutput()) {
                for (int slot : container.getSlots()) {
                    ItemStack item = this.inventory.getItem(slot);
                    if (!this.isNullItem(item) && item.getAmount() >= item.getMaxStackSize()) {
                        recheck.add(new AbstractMap.SimpleEntry<>(this.inventory, slot));
                    } else if (collect.apply(this.inventory, slot)) {
                        return;
                    }
                }
            }
        }

        if (toCollect.getAmount() < toCollect.getMaxStackSize()) {
            IntFunction<Boolean> checkPlayerInv = slot -> {
                ItemStack item = playerInventory.getItem(slot);
                if (!this.isNullItem(item) && item.getAmount() >= item.getMaxStackSize()) {
                    recheck.add(new AbstractMap.SimpleEntry<>(playerInventory, slot));
                } else if (collect.apply(playerInventory, slot)) {
                    return true;
                }
                return false;
            };

            for (int slot = 9; slot < 36; slot++) {
                if (checkPlayerInv.apply(slot)) {
                    return;
                }
            }
            for (int slot = 0; slot < 9; slot++) {
                if (checkPlayerInv.apply(slot)) {
                    return;
                }
            }
        }

        if (toCollect.getAmount() < toCollect.getMaxStackSize()) {
            for (Map.Entry<Inventory, Integer> entry : recheck) {
                if (collect.apply(entry.getKey(), entry.getValue())) {
                    return;
                }
            }
        }
    }

    private void addItem(ItemStack toAdd) {
        for (Container container : this.gui.getContainers()) {
            if (container.canInput() && container.accept(toAdd)) {
                for (int slot : container.getSlots()) {
                    ItemStack item = this.inventory.getItem(slot);
                    if (this.isNullItem(item)) {
                        this.inventory.setItem(slot, toAdd.clone());
                        toAdd.setType(Material.AIR);
                        return;
                    } else if (item.isSimilar(toAdd)) {
                        int add = Math.min(toAdd.getAmount(), item.getMaxStackSize() - item.getAmount());
                        if (add > 0) {
                            item.setAmount(item.getAmount() + add);
                            toAdd.setAmount(toAdd.getAmount() - add);
                        }
                    }
                    if (toAdd.getAmount() <= 0) {
                        toAdd.setType(Material.AIR);
                        return;
                    }
                }
            }
        }
    }
}
