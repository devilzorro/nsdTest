package com.example.gaozhelong.nsdtest;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.net.nsd.NsdServiceInfo;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button regBtn;
    Button connectBtn;
    Button discoveryBtn;
    Button unregBtn;

    ListView listView;

    ArrayAdapter<String> adapter;

    List<String> datas;

    String[] arrayDatas;

    boolean unregStatus = false;

    NsdServiceInfo serviceInfo = new NsdServiceInfo();
//    ServerSocket sock = null;
    NsdManager nsdManager = null;
    NsdManager.RegistrationListener registrationListener = null;
    NsdManager.ResolveListener resolveListener = null;
    NsdManager.DiscoveryListener discoveryListener = null;

    NsdManager nsdDiscoverManager = null;

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        regBtn = findViewById(R.id.reg_btn);
        connectBtn = findViewById(R.id.connect_btn);
        discoveryBtn = findViewById(R.id.discovery_btn);
        unregBtn = findViewById(R.id.unreg_btn);
        listView = findViewById(R.id.device_list);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 100) {
                    adapter.notifyDataSetChanged();
                }
            }
        };



        datas = new ArrayList<>();

        adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,datas);
        listView.setAdapter(adapter);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.reg_btn:
                        nsdRegisterService();
                        break;
                    case R.id.connect_btn:
                        initResolveListener();
                        break;
                    case R.id.discovery_btn:
                        discoverService();
                        break;
                    case R.id.unreg_btn:
                        unregService();
                        break;
                    default:
                        break;
                }
            }
        };
        regBtn.setOnClickListener(onClickListener);
        connectBtn.setOnClickListener(onClickListener);
        discoveryBtn.setOnClickListener(onClickListener);
        unregBtn.setOnClickListener(onClickListener);
    }

    public void nsdRegisterService() {
//        int port = 0;
//        try {
//            sock = new ServerSocket(0);
//            port = sock.getLocalPort();
//            sock.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        InetAddress s =

        serviceInfo.setServiceName("cabbage_android_pad_test1");
        serviceInfo.setServiceType("_http._tcp.");
//        serviceInfo.setHost();
        serviceInfo.setPort(5353);
        
        registrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(getApplicationContext(),"reg failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Toast.makeText(getApplicationContext(),"unreg failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(getApplicationContext(),"service reg",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(getApplicationContext(),"unreg failed",Toast.LENGTH_SHORT).show();
            }
        };

        nsdManager = (NsdManager)getApplicationContext().getSystemService(Context.NSD_SERVICE);
        nsdManager.registerService(serviceInfo,NsdManager.PROTOCOL_DNS_SD,registrationListener);
    }

    public void discoverService() {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                Toast.makeText(getApplicationContext(),"start discovery failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                Toast.makeText(getApplicationContext(),"stop discovery failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDiscoveryStarted(String s) {
                Toast.makeText(getApplicationContext(),"discovery started",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Toast.makeText(getApplicationContext(),"discovery stopped",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(getApplicationContext(),"service found service name :" + nsdServiceInfo.getServiceName(),Toast.LENGTH_SHORT).show();
//                datas.clear();
                datas.add(nsdServiceInfo.getServiceName());
                if (nsdServiceInfo.getHost() != null) {
                    datas.add(nsdServiceInfo.getHost().toString());
                }

                mHandler.sendEmptyMessage(100);

                Toast.makeText(getApplicationContext(),"is mainThread:" + isMainThread(),Toast.LENGTH_SHORT).show();
//                adapter.notifyDataSetChanged();
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                Toast.makeText(getApplicationContext(),"service lost",Toast.LENGTH_SHORT).show();
            }
        };

//        nsdDiscoverManager = (NsdManager) getApplicationContext().getSystemService(Context.NSD_SERVICE);
        nsdManager.discoverServices("_http._tcp.",NsdManager.PROTOCOL_DNS_SD,discoveryListener);
    }

    public void initResolveListener() {
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {

            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                String serviceName = nsdServiceInfo.getServiceName();
                Toast.makeText(getApplicationContext(),serviceName,Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void unregService() {
        if (discoveryListener != null) {
             nsdManager.stopServiceDiscovery(discoveryListener);
             nsdManager.unregisterService(registrationListener);
        }


    }

    public boolean isMainThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

}
