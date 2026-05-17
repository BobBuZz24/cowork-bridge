package com.caoboost.coworkbridge;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.caoboost.coworkbridge.databinding.ActivityMainBinding;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Handler refreshHandler;
    private final Runnable refreshRunnable = this::refreshStatus;
    private static final int REFRESH_INTERVAL_MS = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupButtons();
        refreshStatus();

        refreshHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
        refreshStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        refreshHandler.removeCallbacks(refreshRunnable);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }
    }

    private void setupButtons() {
        // Copy IP
        binding.btnCopyIp.setOnClickListener(v -> {
            String ip = binding.tvIpAddress.getText().toString();
            if (!ip.isEmpty() && !ip.equals(getString(R.string.not_connected))) {
                copyToClipboard("device_ip", ip);
                showToast(getString(R.string.copied_to_clipboard));
            }
        });

        // Copy ADB command
        binding.btnCopyAdb.setOnClickListener(v -> {
            String ip = binding.tvIpAddress.getText().toString();
            if (!ip.isEmpty() && !ip.equals(getString(R.string.not_connected))) {
                String cmd = "adb connect " + ip + ":5555";
                copyToClipboard("adb_command", cmd);
                showToast(getString(R.string.adb_command_copied));
            }
        });

        // Open Developer Options
        binding.btnDevOptions.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
            } catch (Exception e) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
                showToast(getString(R.string.nav_to_dev_options));
            }
        });

        // Open WiFi Settings
        binding.btnWifiSettings.setOnClickListener(v ->
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS))
        );

        // Toggle Keep Screen On
        binding.switchKeepAwake.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                showToast(getString(R.string.screen_keep_on));
            } else {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                showToast(getString(R.string.screen_normal));
            }
        });

        // Toggle background service
        binding.switchBgService.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                startBridgeService();
            } else {
                stopBridgeService();
            }
        });

        // Refresh button
        binding.btnRefresh.setOnClickListener(v -> refreshStatus());
    }

    private void refreshStatus() {
        String ip = getDeviceIpAddress();
        String wifi = getWifiSsid();
        String device = Build.MODEL + " (Android " + Build.VERSION.RELEASE + ")";

        // IP
        if (ip != null && !ip.isEmpty()) {
            binding.tvIpAddress.setText(ip);
            binding.tvAdbCommand.setText("adb connect " + ip + ":5555");
            binding.cardAdb.setCardBackgroundColor(getColor(R.color.card_connected));
            binding.tvConnectionStatus.setText(R.string.status_wifi_ok);
            binding.tvConnectionStatus.setTextColor(getColor(R.color.green_500));
        } else {
            binding.tvIpAddress.setText(R.string.not_connected);
            binding.tvAdbCommand.setText(R.string.connect_wifi_first);
            binding.cardAdb.setCardBackgroundColor(getColor(R.color.card_disconnected));
            binding.tvConnectionStatus.setText(R.string.status_no_wifi);
            binding.tvConnectionStatus.setTextColor(getColor(R.color.red_500));
        }

        // WiFi name
        binding.tvWifiName.setText(wifi != null ? wifi : getString(R.string.unknown));

        // Device info
        binding.tvDeviceInfo.setText(device);

        // ADB port hint
        binding.tvAdbTcpip.setText(getString(R.string.adb_tcpip_hint, ip != null ? ip : "?"));

        // Schedule next refresh
        if (refreshHandler != null) {
            refreshHandler.removeCallbacks(refreshRunnable);
            refreshHandler.postDelayed(refreshRunnable, REFRESH_INTERVAL_MS);
        }
    }

    /**
     * Get device IP on the local network (prefers WiFi, falls back to any non-loopback IPv4).
     */
    private String getDeviceIpAddress() {
        // Try WifiManager first (most reliable for WiFi IP)
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null && wm.isWifiEnabled()) {
            int ip = wm.getConnectionInfo().getIpAddress();
            if (ip != 0) {
                return Formatter.formatIpAddress(ip);
            }
        }

        // Fallback: iterate network interfaces
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface iface : Collections.list(ifaces)) {
                if (!iface.isUp() || iface.isLoopback()) continue;
                for (InetAddress addr : Collections.list(iface.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr.getAddress().length == 4) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    @SuppressWarnings("deprecation")
    private String getWifiSsid() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm != null && wm.isWifiEnabled()) {
            String ssid = wm.getConnectionInfo().getSSID();
            if (ssid != null && !ssid.equals("<unknown ssid>")) {
                return ssid.replace("\"", "");
            }
        }
        return null;
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(label, text));
        }
    }

    private void startBridgeService() {
        Intent intent = new Intent(this, BridgeService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void stopBridgeService() {
        stopService(new Intent(this, BridgeService.class));
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
