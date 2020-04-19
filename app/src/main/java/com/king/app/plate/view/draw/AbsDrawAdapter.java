package com.king.app.plate.view.draw;

import org.jetbrains.annotations.NotNull;

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 16:45
 */
public abstract class AbsDrawAdapter {

    private NotifyObserver notifyObserver;

    public abstract String getText(int x, int y);

    public abstract int getTextColor(int x, int y);

    public void notifyDataSetChanged() {
        notifyObserver.notifyDataSetChanged();
    }

    public void setNotifyObserver(@NotNull NotifyObserver observer) {
        this.notifyObserver = observer;
    }
}
