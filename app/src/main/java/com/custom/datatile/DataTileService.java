package com.custom.datatile;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.text.format.Formatter;
import java.util.Calendar;

public class DataTileService extends TileService {

    @Override
    public void onStartListening() {
        refresh();
    }

    @Override
    public void onClick() {
        refresh();
    }

    private long usage(int type, long start, long end) throws Exception {
        NetworkStatsManager m =
            (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStats.Bucket b = m.querySummaryForDevice(type, null, start, end);
        return b.getRxBytes() + b.getTxBytes();
    }

    private void refresh() {
        Tile t = getQsTile();
        if (t == null) return;
        try {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            long start = c.getTimeInMillis();
            long now = System.currentTimeMillis();
            long mobile = usage(ConnectivityManager.TYPE_MOBILE, start, now);
            long wifi = usage(ConnectivityManager.TYPE_WIFI, start, now);
            t.setLabel("Data " + Formatter.formatShortFileSize(this, mobile));
            t.setSubtitle("Wi-Fi " + Formatter.formatShortFileSize(this, wifi));
            t.setState(Tile.STATE_ACTIVE);
        } catch (Exception e) {
            t.setLabel("Data usage");
            t.setSubtitle("No permission");
            t.setState(Tile.STATE_INACTIVE);
        }
        t.updateTile();
    }
}
