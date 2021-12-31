package za.ac.uct.cs.powerqope.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Connection;
import okhttp3.EventListener;
import okhttp3.Handshake;
import okhttp3.Protocol;
import okhttp3.Response;

public class CustomHTTPEventListener extends EventListener {

    private Map<String, Double> eventDurationMap;
    private double callStartSec;

    public CustomHTTPEventListener(Map<String, Double> eventDurationMap) {
        this.eventDurationMap = eventDurationMap;
    }

    private void registerEvent(String name) {
        double nowSec = (double) System.currentTimeMillis() / 1000;
        if (name.equals("callStart")) {
            callStartSec = nowSec;
        }
        double elapsed = (nowSec - callStartSec);
        eventDurationMap.put(name, elapsed);
    }

    @Override
    public void callEnd(@NonNull Call call) {
        super.callEnd(call);
        registerEvent("callEnd");
    }

    @Override
    public void callStart(@NonNull Call call) {
        super.callStart(call);
        registerEvent("callStart");
    }

    @Override
    public void connectEnd(@NonNull Call call, @NonNull InetSocketAddress inetSocketAddress, @NonNull Proxy proxy, @Nullable Protocol protocol) {
        super.connectEnd(call, inetSocketAddress, proxy, protocol);
        registerEvent("connectEnd");
    }

    @Override
    public void connectStart(@NonNull Call call, @NonNull InetSocketAddress inetSocketAddress, @NonNull Proxy proxy) {
        super.connectStart(call, inetSocketAddress, proxy);
        registerEvent("connectStart");
    }

    @Override
    public void connectionAcquired(@NonNull Call call, @NonNull Connection connection) {
        super.connectionAcquired(call, connection);
        registerEvent("connectionAcquired");
    }

    @Override
    public void connectionReleased(@NonNull Call call, @NonNull Connection connection) {
        super.connectionReleased(call, connection);
        registerEvent("connectionReleased");
    }

    @Override
    public void dnsEnd(@NonNull Call call, @NonNull String domainName, @NonNull List<InetAddress> inetAddressList) {
        super.dnsEnd(call, domainName, inetAddressList);
        registerEvent("dnsEnd");
    }

    @Override
    public void dnsStart(@NonNull Call call, @NonNull String domainName) {
        super.dnsStart(call, domainName);
        registerEvent("dnsStart");
    }

    @Override
    public void responseBodyEnd(@NonNull Call call, long byteCount) {
        super.responseBodyEnd(call, byteCount);
        registerEvent("responseBodyEnd");
    }

    @Override
    public void responseBodyStart(@NonNull Call call) {
        super.responseBodyStart(call);
        registerEvent("responseBodyStart");
    }

    @Override
    public void responseHeadersEnd(@NonNull Call call, @NonNull Response response) {
        super.responseHeadersEnd(call, response);
        registerEvent("responseHeadersEnd");
    }

    @Override
    public void responseHeadersStart(@NonNull Call call) {
        super.responseHeadersStart(call);
        registerEvent("responseHeadersStart");
    }

    @Override
    public void secureConnectEnd(@NonNull Call call, @Nullable Handshake handshake) {
        super.secureConnectEnd(call, handshake);
        registerEvent("secureConnectEnd");
    }

    @Override
    public void secureConnectStart(@NonNull Call call) {
        super.secureConnectStart(call);
        registerEvent("secureConnectStart");
    }
}
