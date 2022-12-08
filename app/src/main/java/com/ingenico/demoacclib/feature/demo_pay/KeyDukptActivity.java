package com.ingenico.demoacclib.feature.demo_pay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ingenico.demoacclib.R;
import com.usdk.apiservice.aidl.pinpad.DeviceName;
import com.usdk.apiservice.aidl.pinpad.KAPId;
import com.usdk.apiservice.aidl.pinpad.KeySystem;
import com.usdk.apiservice.aidl.pinpad.UPinpad;
import com.usdk.apiservice.limited.pinpad.PinpadLimited;

public class KeyDukptActivity extends AppCompatActivity {

    private UPinpad pinpad;
    private PinpadLimited pinpadLimited;

    Button btnRegister;
    Button btnExist;
    Button btnDelete;
    Button btnDukptData;
    Button btnPrintData;
    Button btnPrintPin;
    Button btnClear;
    Button btnDukptPin;
    Button btnPrefix;

    EditText etExist;
    EditText etDukptData;
    EditText etDukptPin;
    EditText etDelete;
    EditText etLog;
    EditText etPrefix;

    private StringBuilder builder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_dukpt);

        DeviceHelper.me().init(this);
        DeviceHelper.me().bindService();

        builder = new StringBuilder();

        btnRegister = findViewById(R.id.btnRegister);
        btnExist = findViewById(R.id.btnExist);
        btnDelete = findViewById(R.id.btnDelete);
        btnDukptData = findViewById(R.id.btnDukptData);
        btnDukptPin = findViewById(R.id.btnDukptPin);
        btnPrintData = findViewById(R.id.btnPrintData);
        btnPrintPin = findViewById(R.id.btnPrintPin);
        btnClear = findViewById(R.id.btnClear);
        btnPrefix = findViewById(R.id.btnPrefix);

        etExist = findViewById(R.id.et_exist);
        etDukptData = findViewById(R.id.etDukptData);
        etDukptPin = findViewById(R.id.etDukptPin);
        etDelete = findViewById(R.id.etDelete);
        etLog = findViewById(R.id.et_log);
        etPrefix = findViewById(R.id.etPrefix);

        btnRegister.setOnClickListener(view -> {
            try {
                register(true);

            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnExist.setOnClickListener(view -> {

            pinpad = createPinpadUSDK(new KAPId(0, 0));

            try {
                openPinpadUSDK();
                String index = etExist.getText().toString();
                boolean isExist = pinpad.isKeyExist(Integer.parseInt(index));
                if (isExist) {
                    outputBlueText(String.format("The key(keyId = %s) is exist", index));
                } else {
                    outputBlueText(String.format("The key(keyId = %s) is non-existent", index));
                }
                closePinpadUSDK();

            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnDelete.setOnClickListener(view -> {
            try {
                openPinpadUSDK();
                outputBlueText(etExist.getText().toString());
                String index = etExist.getText().toString();
                boolean isSucc = pinpadLimited.deleteKey(Integer.parseInt(index));
                if (isSucc) {
                    outputBlueText(String.format("Delete key(keyId = %s) success", index));
                } else {
                    outputBlueText(String.format("Delete key(keyId = %s) fail", index));
                }
                closePinpadUSDK();

            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnDukptData.setOnClickListener(view -> {
            try {
                String index = etDukptData.getText().toString();
                if (index.isEmpty()) {
                    outputBlueText("empty");
                    return;
                }
                Constant.setKeyDukptData(String.format("%02d", Integer.parseInt(index)));
                outputBlueText("SET DUKPT DATA >>> " + String.format("%02d", Integer.parseInt(index)));
            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnDukptPin.setOnClickListener(view -> {
            try {
                String index = etDukptPin.getText().toString();
                if (index.isEmpty()) {
                    outputBlueText("empty");
                    return;
                }
                Constant.setKeyDukptPin(String.format("%02d", Integer.parseInt(index)));
                outputBlueText("SET DUKPT PIN >>> " + String.format("%02d", Integer.parseInt(index)));
            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnPrefix.setOnClickListener(view -> {
            try {
                String index = etPrefix.getText().toString();
                if (index.length() != 4) {
                    outputBlueText("ERROR : empty or size lenght");
                    return;
                }
                Constant.setKeyPrefix(index);
                outputBlueText("SET PREFIX >>> " + index);
            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnPrintData.setOnClickListener(view -> {
            try {
                outputBlueText("DUKTP DATA >>> " + Constant.getKeyPrefix() + " " + Constant.getKeyDukptData());
            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnPrintPin.setOnClickListener(view -> {
            try {
                outputBlueText("DUKTP PIN >>> " + Constant.getKeyPrefix() + " " + Constant.getKeyDukptPin());
            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });

        btnClear.setOnClickListener(view -> {
            try {
                etLog.setText("");
            } catch (Exception e) {
                outputBlueText(e.getMessage());
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void register(boolean useEpayModule) {
        try {
            DeviceHelper.me().register(useEpayModule);
            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            Toast.makeText(this, "Error Register", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        closePinpadUSDK();
        super.onDestroy();
    }

    private void openPinpadUSDK() {
        try {
            if (!this.pinpad.open()) {
                outputBlueText("<<< open PINPAD USDK fail >>>");
            }
        } catch (Exception e) {
            outputBlueText(e.getMessage());
        }
    }

    private void closePinpadUSDK() {
        try {
            if (!this.pinpad.close()) {
                outputBlueText("<<< close PINPAD USDK fail >>>");
            }
        } catch (Exception e) {
            outputBlueText(e.getMessage());
        }
    }

    private UPinpad createPinpadUSDK(KAPId kapId) {
        try {
            pinpadLimited = new PinpadLimited(getApplicationContext(), kapId, KeySystem.KS_DUKPT, DeviceName.IPP);
            return DeviceHelper.me().getPinpad(kapId, KeySystem.KS_DUKPT, DeviceName.IPP);
        } catch (Exception e) {
            return null;
        }
    }

    private void existKey(int keyId) {
        outputBlueText(">>> isKeyExist");
        try {
            boolean isExist = pinpad.isKeyExist(keyId);
            if (isExist) {
                outputBlueText(String.format("The key(keyId = %s) is exist", keyId));
            } else {
                outputBlueText(String.format("The key(keyId = %s) is non-existent", keyId));
            }

        } catch (RemoteException e) {

        }
    }

    private void initUSDK() {
        try {




        } catch (Exception e) {

        }
    }

    private void outputBlueText(String msg) {
        builder.append(msg).append("\n");
        etLog.setText(etLog.getText().toString() + msg + "\n");
    }
}