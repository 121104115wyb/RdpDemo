package com.renogy.device.rdpdemo.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author Create by 17474 on 2023/6/20.
 * Email： lishuwentimor1994@163.com
 * Describe：蓝牙广播接收器
 */
public class BlueToothReceiver extends BroadcastReceiver {

    private final static class LazyHolder {
        public final static BlueToothReceiver INSTANCE = new BlueToothReceiver();
    }

    public static BlueToothReceiver getInstance(){
        return LazyHolder.INSTANCE;
    }

    private ReceiverListener listener;

    public void setListener(ReceiverListener listener) {
        this.listener = listener;
    }

    public void registerBleReceiver(Context context,ReceiverListener listener) {
        if (context == null) return;
        this.listener = listener;
        context.registerReceiver(this, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    public void unregisterBleReceiver(Context context) {
        if (context == null) return;
        context.unregisterReceiver(this);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            listener.onReceive(context, intent);
        }
    }


    public interface ReceiverListener {

        void onReceive(Context context, Intent intent);

    }
}
