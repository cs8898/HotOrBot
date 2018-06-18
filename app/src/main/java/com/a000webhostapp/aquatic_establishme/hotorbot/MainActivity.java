package com.a000webhostapp.aquatic_establishme.hotorbot;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    Sensor gravSensor;
    SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView t = findViewById(R.id.text);
        final boolean b = isEmulator();
        t.setText(b ? "bot" : "hot");
        t.setTextColor(b ? Color.RED : Color.GREEN);
        /*Thread d = new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request r = new Request.Builder()
                        .url("http://aquatic-establishme.000webhostapp.com/hotorbot.php?q="+(b?"bot":"hot"))
                        .get()
                        .build();
                try {
                    client.newCall(r).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        //d.start();*/
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this,gravSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    private boolean isEmulator() {
        //Build.SERIAL is deprecated
        //READ_PHONE_STATE is required
        if (Build.SERIAL.equals(Build.UNKNOWN))
            return true;
        if (!Build.FINGERPRINT.contains("release-key")
                || Build.FINGERPRINT.contains("generic"))
            return true;
        if (Build.DEVICE.equals("generic")
                || Build.DEVICE.equals("vmi"))
            return true;
        if (Build.MANUFACTURER.equals(Build.UNKNOWN))
            return true;
        if (Build.HARDWARE.equals("goldfish")
                || Build.HARDWARE.equals("vmi"))
            return true;
        String cIMEI = checkImei();
        //THE IMEI lookup has to be
        //implemented somehow
        //we will accept these for now...
        /*
        if(cIMEI.equals("true"))
            lookupIMEI(Build.MANUFACTURER);*/
        if (cIMEI.equals("false"))
            return true;
        return false;
    }

    private String checkImei() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "restricted";
        }
        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (tMgr == null)
            return "restricted";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return String.valueOf(checkImei(tMgr.getImei()));
        } else {
            return String.valueOf(checkImei(tMgr.getDeviceId()));
        }
    }

    private boolean checkImei(String imei) {
        if (imei.length() != 15)
            return false;
        int sum = 0;
        for (int i = 0; i < imei.length(); i++) {
            sum += sumDig((imei.charAt(i) - '0') * (i % 2 == 0 ? 1 : 2));
        }
        return sum % 10 == 0;
    }

    public static int sumDig(int n) {
        int a = 0;
        while (n > 0) {
            a = a + n % 10;
            n = n / 10;
        }
        return a;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < event.values.length; i++){
            s.append(event.values[i]).append("\n");
        }
        ((TextView) findViewById(R.id.text)).setText(s.toString());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
