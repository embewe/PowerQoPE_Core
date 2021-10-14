package za.ac.uct.cs.powerqope.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.VPNLaunchHelper;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;
import za.ac.uct.cs.powerqope.Config;
import za.ac.uct.cs.powerqope.SubscriptionCallbackInterface;
import za.ac.uct.cs.powerqope.dns.ConfigurationAccess;
import za.ac.uct.cs.powerqope.dns.DNSFilterService;

public class WebSocketConnector {

    private static final String TAG = "WebSocketConnector";
    private static Context context;
    private StompClient mStompClient;
    private CompositeDisposable compositeDisposable;
    private static WebSocketConnector instance;
    private ConfigurationAccess CONFIG = ConfigurationAccess.getLocal();

    private WebSocketConnector() {
    }

    public static WebSocketConnector getInstance() {
        if (instance == null) {
            instance = new WebSocketConnector();
        }
        return instance;
    }

    public static synchronized void setContext(Context newContext) {
        assert newContext != null;
        context = newContext;
    }

    private List<Disposable> getSubscriptions() {
        return new ArrayList<Disposable>() {{
            add(subscribeToSecurityConfig());
        }};
    }

    private void modifyConfig(JSONObject filter, JSONObject cipher) {
        try {
            boolean changed = false;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            String ln;
            String filterValue;
            String secLevel;
            String secLevelBefore = CONFIG.getConfig().getProperty("secLevel", "default");
            switch (filter.getString("dnsType")) {
                case "dot":
                    filterValue = filter.getString("ipAddress") + "::853::DoT";
                    secLevel = "medium";
                    break;
                case "doh":
                    String url = filter.getString("url");
                    filterValue = filter.getString("ipAddress") + "::443::DoH::" + url;
                    secLevel = "high";
                    break;
                default:
                    filterValue = filter.getString("ipAddress");
                    secLevel = "low";
                    break;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(CONFIG.readConfig())));
            while ((ln = reader.readLine()) != null) {
                String old = ln;
                if (ln.trim().startsWith("detectDNS"))
                    ln = "detectDNS = " + false;

                else if (ln.trim().startsWith("fallbackDNS"))
                    ln = "fallbackDNS = " + filterValue;

                else if (ln.trim().startsWith("cipher"))
                    ln = "cipher = " + cipher.getString("tlsVersion") + ":" + cipher.getString("name");

                else if (ln.trim().startsWith("secLevel"))
                    ln = "secLevel = " + secLevel;

                out.write((ln + "\r\n").getBytes());

                changed = changed || !old.equals(ln);
            }

            reader.close();
            out.flush();
            out.close();

            if (changed) {
                CONFIG.updateConfig(out.toByteArray());
            }
        } catch (Exception e) {
            Log.e(TAG, "persistConfig: " + e.getMessage());
        }
    }

    private boolean loadVpnProfile(JSONObject vpnServer, String dns) {
        VpnServer currentServer;
        try {
            currentServer = new VpnServer(
                    vpnServer.getString("hostName"),
                    vpnServer.getString("ip"),
                    String.valueOf(vpnServer.getLong("score")),
                    String.valueOf(vpnServer.getInt("ping")),
                    String.valueOf(vpnServer.getLong("speed")),
                    vpnServer.getString("country"),
                    vpnServer.getString("countryCode"),
                    String.valueOf(vpnServer.getInt("numVpnSessions")),
                    String.valueOf(vpnServer.getLong("uptime")),
                    String.valueOf(vpnServer.getLong("totalUsers")),
                    String.valueOf(vpnServer.getLong("totalTraffic")),
                    vpnServer.getString("logType"),
                    vpnServer.getString("operator"),
                    vpnServer.getString("message"),
                    vpnServer.getString("configData"),
                    ConnectionQuality.getConnectionQuality(
                            String.valueOf(vpnServer.getLong("speed")),
                            String.valueOf(vpnServer.getInt("numVpnSessions")),
                            String.valueOf(vpnServer.getInt("ping"))
                    ),
                    null,
                    0,
                    null,
                    0,
                    0
            );
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        byte[] data;
        try {
            data = Base64.decode(currentServer.getConfigData(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        ConfigParser cp = new ConfigParser();
        InputStreamReader isr = new InputStreamReader(new ByteArrayInputStream(data));
        try {
            cp.parseConfig(isr);
            VpnProfile vpnProfile = cp.convertProfile();
            vpnProfile.mName = currentServer.getCountryLong();
            vpnProfile.mOverrideDNS = true;
            vpnProfile.mDNS1 = dns;
            vpnProfile.mDNS2 = dns;
            ProfileManager.getInstance(context).addProfile(vpnProfile);
            VPNLaunchHelper.startOpenVpn(vpnProfile, context);
        } catch (IOException | ConfigParser.ConfigParseError e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Disposable subscribeToSecurityConfig() {
        String deviceId = getDeviceId();
        return subscribeToTopic(String.format(Config.STOMP_SERVER_CONFIG_RESPONSE_ENDPOINT, deviceId), result -> {
            try {
                JSONObject config = new JSONObject(result.getPayload());
                JSONObject filter = config.getJSONObject("filter");
                JSONObject cipher = config.getJSONObject("cipher");
                // Write filter to file
                modifyConfig(filter, cipher);
                if (filter.getString("dnsType").equalsIgnoreCase("doh")) {
                    loadVpnProfile(config.getJSONObject("vpn"), filter.getString("ipAddress"));
                }
            } catch (JSONException e) {
                Log.e(TAG, "subscribeToSecurityConfig: Error parsing JSON from server");
                Log.e(TAG, "subscribeToSecurityConfig: " + e.getMessage());
            }

        });
    }

    public void connectWebSocket(String target) {
        if (target == null)
            return;
        String deviceId = getDeviceId();
        OkHttpClient client = new OkHttpClient.Builder()
                .hostnameVerifier(new HostnameVerifier() {
                    @SuppressLint("BadHostnameVerifier")
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, target, null, client);
        List<StompHeader> headers = new ArrayList<StompHeader>() {{
            add(new StompHeader("deviceId", deviceId));
        }};
        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.d(TAG, "Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            Log.d(TAG, "Stomp connection error");
                            break;
                        case CLOSED:
                            Log.d(TAG, "Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            Log.d(TAG, "Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);
        List<Disposable> subscriptions = getSubscriptions();
        for (Disposable subscription : subscriptions)
            compositeDisposable.add(subscription);
        mStompClient.connect(headers);
    }

    public String getDeviceId() {
        String uuid;
        SharedPreferences uniqueIdPref = context.getSharedPreferences(Config.PREF_KEY_UNIQUE_ID, Context.MODE_PRIVATE);
        uuid = uniqueIdPref.getString(Config.PREF_KEY_UNIQUE_ID, null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString() + "_" + Util.hashTimeStamp();
            SharedPreferences.Editor edit = uniqueIdPref.edit();
            edit.putString(Config.PREF_KEY_UNIQUE_ID, uuid);
            edit.apply();
        }
        return uuid;
    }

    public void sendMessage(String endpoint, String content) {
        compositeDisposable.add(mStompClient.send(endpoint, content)
                .compose(applySchedulers())
                .subscribe(
                        () -> Log.d(TAG, String.format("Message sent successfully to %s", endpoint)),
                        (throwable) -> Log.d(TAG, String.format("Error sending message to %s", endpoint, throwable))
                ));
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    public void disconnect() {
        mStompClient.disconnect();
    }

    public boolean isConnected() {
        if (mStompClient == null) return false;
        return mStompClient.isConnected();
    }

    private Disposable subscribeToTopic(String endpoint, SubscriptionCallbackInterface callback) {
        return mStompClient.topic(endpoint)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callback::onSubscriptionResult, throwable -> {
                    Log.e(TAG, "Error on subscribe topic", throwable);
                });
    }
}
