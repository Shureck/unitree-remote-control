package org.vosk.demo;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.vosk.android.RecognitionListener;
import org.vosk.android.StorageService;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class MainActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    private TextView angleTextView1, angleTextView2;
    private TextView powerTextView1, powerTextView2;
    private Button button_up, button_right, button_left, button_down, button_y1, button_y2,
            button_x1, button_x2, select;
    FloatingActionButton settigs;
    private Spinner spinner;
    // Importing also other views
    private org.vosk.demo.JoystickView joystick, joystick2;
    private double LOW = -100;
    private double HIGHT = 100;
    private double LOW_2 = 0;
    private double HIGHT_2 = 255;
    private VoskActivity voskActivity;
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private byte[] send = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
    private boolean sendUdp;

    // TODO ПОМЕНЯТЬ!!
    private String outputIP = "localhost";
    private Integer broadcastPort = 5060;

    public int result_devise = 0;

    /**
     * Called when the activity is first created.
     */
    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        voskActivity = new VoskActivity(this);
        //Referencing also other views
        joystick = (org.vosk.demo.JoystickView) findViewById(R.id.joystickView);
        joystick2 = (org.vosk.demo.JoystickView) findViewById(R.id.joystickView2);

        button_up = (Button) findViewById(R.id.button);
        button_right = (Button) findViewById(R.id.button_right);
        button_left = (Button) findViewById(R.id.button_left);
        button_down = (Button) findViewById(R.id.button_down);
        button_y1 = (Button) findViewById(R.id.button_y1);
        button_y2 = (Button) findViewById(R.id.button_y2);
        button_x1 = (Button) findViewById(R.id.button_x1);
        button_x2 = (Button) findViewById(R.id.button_x2);
        select = (Button) findViewById(R.id.select);
        settigs = (FloatingActionButton) findViewById(R.id.settings);


        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
        } else {
            voskActivity.initModel();
        }

        WebView webView = findViewById(R.id.puge);
//        webView.loadUrl("file:///android_asset/mypage.html");
        webView.loadUrl("https://thiscatdoesnotexist.com/");
        webView.getSettings().setJavaScriptEnabled(true);
        //-----UDP send thread
        Thread udpSendThread = new Thread(new Runnable() {

            @Override
            public void run() {


                while (true) {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    if (sendUdp == true) {

                        try {

                            // get server name

                            InetAddress serverAddr = InetAddress.getByName(outputIP);
                            Log.d("UDP", "C: Connecting...");

                            // create new UDP socket
                            DatagramSocket socket = new DatagramSocket();

                            // prepare data to be sent
                            // byte[] buf = udpOutputData.getBytes();

                            // create a UDP packet with data and its destination ip & port
                            DatagramPacket packet = new DatagramPacket(send, send.length, serverAddr, broadcastPort);
                            Log.d("UDP", "C: Sending: '" + new String(send) + "'");

                            // send the UDP packet
                            socket.send(packet);

                            socket.close();

                            Log.d("UDP", "C: Sent.");
                            Log.d("UDP", "C: Done.");

                            for (int i = 0; i < 8; i++) {
                                send[i] = (byte) 0;
                            }
                        } catch (Exception e) {

                            Log.e("UDP", "C: Error", e);

                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }


                        sendUdp = false;
                    }

                }
            }

        });



//        Thread ttt = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e1) {
//                        // TODO Auto-generated catch block
//                        e1.printStackTrace();
//                    }
//                    if (sendUdp == true) {
//                        try {
//                            System.out.println("ОТПРАВКА!!!!!!" + getSend());
//                            Log.d("UDP", "C: Sent.");
//                            Log.d("UDP", "C: Done.");
//
//                        } catch (Exception e) {
//
//                            Log.e("UDP", "C: Error", e);
//
//                        }
//
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//                            // TODO Auto-generated catch block
//                            e.printStackTrace();
//                        }
//
//
//                        sendUdp = false;
//                    }
//
//                }
//            }
//        });
//
//        ttt.start();

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick.setOnJoystickMoveListener(new org.vosk.demo.JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
                // TODO Auto-generated method stub
                double Y = Math.cos(Math.toRadians((double) angle)) * power;
                double X = Math.sin(Math.toRadians((double) angle)) * power;
                System.out.println(result_devise);
                send[0] = (byte) map(X);
                send[1] = (byte) map(Y);
                sendUdp = true;
            }
        }, org.vosk.demo.JoystickView.DEFAULT_LOOP_INTERVAL);

        //Event listener that always returns the variation of the angle in degrees, motion power in percentage and direction of movement
        joystick2.setOnJoystickMoveListener(new org.vosk.demo.JoystickView.OnJoystickMoveListener() {

            @Override
            public void onValueChanged(int angle, int power, int direction) {
                // TODO Auto-generated method stub
                double Y = Math.cos(Math.toRadians((double) angle)) * power;
                double X = Math.sin(Math.toRadians((double) angle)) * power;
                send[2] = (byte) map(X);
                send[3] = (byte) map(Y);
                sendUdp = true;

            }
        }, org.vosk.demo.JoystickView.DEFAULT_LOOP_INTERVAL);

        // устанавливаем один обработчик для всех кнопок
        // Left joystick

        button_up.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[4] |= 16;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[4] ^= 16;
                        break;
                }
                return false;
            }
        });
        button_down.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[4] |= 1;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[4] ^= 1;
                        break;
                }
                return false;
            }
        });
        button_left.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[5] |= 16;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[5] ^= 16;
                        break;
                }
                return false;
            }
        });
        button_right.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[5] |= 1;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[5] ^= 1;
                        break;
                }
                return false;
            }
        });

        button_x1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[6] |= 16;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[6] ^= 16;
                        break;
                }
                return false;
            }
        });
        ;
        button_x2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[6] |= 1;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[6] ^= 1;
                        break;
                }
                return false;
            }
        });
        ;
        button_y1.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[7] |= 16;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[7] ^= 16;
                        break;
                }
                return false;
            }
        });
        ;
        button_y2.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // нажатие
                        send[7] |= 1;
                        sendUdp = true;
                        break;
                    case MotionEvent.ACTION_UP: // отпускание
                        send[7] ^= 1;
                        break;
                }
                return false;
            }
        });
        ;

        Switch switch_micro = findViewById(R.id.micro);
        Switch switch_camera = findViewById(R.id.camera);
        if (switch_micro != null) {
            switch_micro.setOnCheckedChangeListener(this);
            switch_micro.setTransitionName("micro");
        }

        if (switch_camera != null) {
            switch_camera.setOnCheckedChangeListener(this);
            switch_camera.setTransitionName("camera");
        }

        Dialog dialog = new Dialog(MainActivity.this);

//        AudioManager audioManager = this.getApplicationContext().getSystemService(AudioManager.class);
//        AudioDeviceInfo speakerDevice = null;
//        List<AudioDeviceInfo> devices = null;
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            devices = audioManager.getAvailableCommunicationDevices();
//            ArrayList<String> devs = new ArrayList<>();
//            ArrayList<AudioDeviceInfo> devis = new ArrayList<>();
//            for (AudioDeviceInfo device : devices) {
//                System.out.println("AAAAAAAAAAAA " + device.getType());
//                devis.add(device);
//                devs.add(String.valueOf(device.getType()));
//            }
//            System.out.println(devs + "\n---\t----\t---\n" + devis);
//        }

        settigs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                // Установите заголовок
//                dialog.setTitle("Настройки");
//                // Передайте ссылку на разметку
//                dialog.setContentView(R.layout.settings);
//                dialog.show()
//                FragmentManager manager = getSupportFragmentManager();
                final String[] catNamesArray = {"0", "1", "2"};

                SettingsDialog myDialogFragment = new SettingsDialog(catNamesArray, result_devise, MainActivity.this);
                myDialogFragment.show(getSupportFragmentManager(), "myDialog");

            }
        });


        spinner = findViewById(R.id.spinner);

//        select.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View view) {
//
////                System.out.println(String.valueOf(spinner.getSelectedItem()));
//                dialog.dismiss();
//            }
//        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        Toast.makeText(this, "Отслеживание переключения: " + findViewById(buttonView.getId()).getTransitionName() + "\n"+ (isChecked ? "on" : "off"),
//                Toast.LENGTH_SHORT).show();
        if (buttonView.getTransitionName().compareTo("micro") == 0){
            if (isChecked) {
                voskActivity.recognizeMicrophone();
            }
            else{
                voskActivity.pause(true);
            }
        }
        if (buttonView.getTransitionName().compareTo("camera") == 0){
            if (isChecked) {

            }
            else{

            }
        }

    }



    public String getSend() {
        return byteArrayToHex(send);
    }

    private static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public int getResult_devise() {
        return result_devise;
    }

    public void setResult_devise(int item){
        result_devise = item;
    }

    private int map(double value) {

        // Положение числа в исходном отрезке, от -100 до 100
        double relative_value = (value - LOW) / (HIGHT - LOW);

        // Накладываем его на конечный отрезок
        return (int) Math.ceil(LOW_2 + (HIGHT_2 - LOW_2) * relative_value);
    }

}