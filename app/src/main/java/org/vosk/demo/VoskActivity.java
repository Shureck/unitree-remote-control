// Copyright 2019 Alpha Cephei Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.vosk.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioDeviceCallback;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.media.MicrophoneInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.RecognitionListener;
import org.vosk.android.SpeechService;
import org.vosk.android.SpeechStreamService;
import org.vosk.android.StorageService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class VoskActivity implements
        RecognitionListener {

    static private final int STATE_START = 0;
    static private final int STATE_READY = 1;
    static private final int STATE_DONE = 2;
    static private final int STATE_FILE = 3;
    static private final int STATE_MIC = 4;

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private Context context;
    private Model model;
    private SpeechService speechService;
    private SpeechStreamService speechStreamService;
    private TextView resultView;
    private TcpClient mTcpClient;
    private UdpClient udpClient = new UdpClient();

    @RequiresApi(api = Build.VERSION_CODES.S)
    public VoskActivity(Context context) {
//        super.onCreate(state);
        this.context = context;
//        new ConnectTask().execute("");

        // Setup layout
//        setUiState(STATE_START);
//        findViewById(R.id.recognize_file).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AudioManager audioManager = getApplicationContext().getSystemService(AudioManager.class);
//                AudioDeviceInfo speakerDevice = null;
//                List<AudioDeviceInfo> devices = null;
//
//                devices = audioManager.getAvailableCommunicationDevices();
//                for (AudioDeviceInfo device : devices) {
//                    System.out.println("AAAAAAAAAAAA "+device.getType());
//                    resultView.append(String.valueOf(device.getType()) + "\n");
//                    if (device.getType() == AudioDeviceInfo.TYPE_BLUETOOTH_SCO) {
//                        speakerDevice = device;
//                        break;
//                    }
//                }
//                if (speakerDevice != null) {
//                    // Turn speakerphone ON.
//                    boolean result = audioManager.setCommunicationDevice(speakerDevice);
//                    if (!result) {
//                        // Handle error.
//                    }
//                }
//            }
//        });
//        findViewById(R.id.recognize_mic).setOnClickListener(view -> recognizeMicrophone());
//        ((ToggleButton) findViewById(R.id.pause)).setOnCheckedChangeListener((view, isChecked) -> pause(isChecked));
//
//        LibVosk.setLogLevel(LogLevel.INFO);
//
//        // Check if user has given permission to record audio, init the model after permission is granted
//        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
//        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSIONS_REQUEST_RECORD_AUDIO);
//        } else {
//            initModel();
//        }
    }

    public void initModel() {
        StorageService.unpack(context, "model-en-us", "model",
                (model) -> {
                    this.model = model;
//                    setUiState(STATE_READY);
                },
                (exception) -> setErrorState("Failed to unpack the model" + exception.getMessage()));
    }

//    public class ConnectTask extends AsyncTask<String, String, TcpClient> {
//
//        @Override
//        protected TcpClient doInBackground(String... message) {
//
//            //we create a TCPClient object
//            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
//                @Override
//                //here the messageReceived method is implemented
//                public void messageReceived(String message) {
//                    //this method calls the onProgressUpdate
//                    publishProgress(message);
//                }
//            });
//            mTcpClient.run();
//
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(String... values) {
//            super.onProgressUpdate(values);
//            //response received from server
//            Log.d("test", "response " + values[0]);
//            //process server response here....
//
//        }
//    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        if (speechService != null) {
//            speechService.stop();
//            speechService.shutdown();
//        }
//
//        if (speechStreamService != null) {
//            speechStreamService.stop();
//        }
//    }

    @Override
    public void onResult(String hypothesis) {
        try {
            JSONObject jObject = new JSONObject(hypothesis);
            String res = jObject.getString("text");
            System.out.println(res);
//            resultView.append(hypothesis + " " + FuzzySearch.ratio(res,"собака лежать") + "\n");

//            if (mTcpClient != null) {
//                mTcpClient.sendMessage(res);
//            }
            udpClient.sendMessage(res);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinalResult(String hypothesis) {
//        resultView.append(hypothesis + "\n");
        if (speechStreamService != null) {
            speechStreamService = null;
        }
    }

    @Override
    public void onPartialResult(String hypothesis) {
//        resultView.append(hypothesis + "\n");
//        try {
//            JSONObject jObject = new JSONObject(hypothesis);
//            jObject = new JSONObject(hypothesis);
//            String res = jObject.getString("partial");
//            udpClient.sendMessage(res);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onError(Exception e) {
        setErrorState(e.getMessage());
    }

    @Override
    public void onTimeout() {

    }

//    @Override
//    public void onTimeout() {
//        setUiState(STATE_DONE);
//    }

//    private void setUiState(int state) {
//        switch (state) {
//            case STATE_START:
//                resultView.setText(R.string.preparing);
//                resultView.setMovementMethod(new ScrollingMovementMethod());
//                findViewById(R.id.recognize_file).setEnabled(false);
//                findViewById(R.id.recognize_mic).setEnabled(false);
//                findViewById(R.id.pause).setEnabled((false));
//                break;
//            case STATE_READY:
//                resultView.setText(R.string.ready);
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//                findViewById(R.id.recognize_file).setEnabled(true);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
//                break;
//            case STATE_DONE:
//                ((Button) findViewById(R.id.recognize_file)).setText(R.string.recognize_file);
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//                findViewById(R.id.recognize_file).setEnabled(true);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
//                break;
//            case STATE_FILE:
//                ((Button) findViewById(R.id.recognize_file)).setText(R.string.stop_file);
//                resultView.setText(getString(R.string.starting));
//                findViewById(R.id.recognize_mic).setEnabled(false);
//                findViewById(R.id.recognize_file).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((false));
//                break;
//            case STATE_MIC:
//                ((Button) findViewById(R.id.recognize_mic)).setText(R.string.stop_microphone);
//                resultView.setText(getString(R.string.say_something));
//                findViewById(R.id.recognize_file).setEnabled(false);
//                findViewById(R.id.recognize_mic).setEnabled(true);
//                findViewById(R.id.pause).setEnabled((true));
//                break;
//            default:
//                throw new IllegalStateException("Unexpected value: " + state);
//        }
//    }

    private void setErrorState(String message) {
//        resultView.setText(message);
//        ((Button) findViewById(R.id.recognize_mic)).setText(R.string.recognize_microphone);
//        findViewById(R.id.recognize_file).setEnabled(false);
//        findViewById(R.id.recognize_mic).setEnabled(false);
    }

    public void recognizeMicrophone() {
        if (speechService != null) {
//            setUiState(STATE_DONE);
            speechService.stop();
            speechService = null;
        } else {
//            setUiState(STATE_MIC);
            try {
                Recognizer rec = new Recognizer(model, 16000.0f);
                speechService = new SpeechService(rec, 16000.0f);
                speechService.startListening(this);
            } catch (IOException e) {
                setErrorState(e.getMessage());
            }
        }
    }


    public void pause(boolean checked) {
        if (speechService != null) {
            speechService.setPause(checked);
        }
    }

}
