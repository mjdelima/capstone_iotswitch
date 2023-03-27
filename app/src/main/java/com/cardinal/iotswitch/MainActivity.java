package com.cardinal.iotswitch;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.cardinal.iotswitch.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;








public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sSwitch;
    ImageView img;
    EditText edtDelay,edtDevice;
    Button saveDelay, saveDevice;
    Toolbar toolbar;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference delayRef, statusRef;
    String deviceDelay, deviceStatus;
    SharedPreferences appData;
    SharedPreferences.Editor dataEditor;
    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        sSwitch = findViewById(R.id.swStatus);
        img = findViewById(R.id.imgOutlet);
        edtDelay = findViewById(R.id.etDelay);
        saveDelay = findViewById(R.id.btnDelay);
        saveDelay.setOnClickListener(this);
        edtDevice = findViewById(R.id.etDevice);
        saveDevice = findViewById(R.id.btnDevice);
        saveDevice.setOnClickListener(this);

        appData = getApplicationContext().getSharedPreferences("appdata",MODE_PRIVATE);
        deviceID = appData.getString("user1","");
        deviceDelay = deviceID + "/delay";
        deviceStatus = deviceID + "/status";
        delayRef = database.getReference(deviceDelay);
        statusRef = database.getReference(deviceStatus);

    }
    @Override
    public void onBackPressed() {

    }


    public void onSwitchClick(View view) {
        if (sSwitch.isChecked()){
            img.setBackgroundResource(R.drawable.outleton);
            statusRef.setValue(1);
        }else{

            img.setBackgroundResource(R.drawable.outletoff);
            statusRef.setValue(0);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnDelay:
                String delay = edtDelay.getText().toString();
                try{
                    int iDelay = Integer.parseInt(delay);
                    if ( iDelay > 4 && iDelay < 61){
                        delayRef.setValue(iDelay);
                        Toast.makeText(this, "New delay value saved.", LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Invalid delay value.", LENGTH_SHORT).show();
                    }
                } catch(NumberFormatException nfe) {
                    Toast.makeText(this, "Invalid delay value.", LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDevice:
                dataEditor = appData.edit();
                dataEditor.putString("user",edtDevice.getText().toString());
                deviceDelay = edtDevice.getText().toString() + "/delay";
                deviceStatus = edtDevice.getText().toString() + "/status";
                delayRef = database.getReference(deviceDelay);
                statusRef = database.getReference(deviceStatus);
                dataEditor.apply();


                statusRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot statusSnapshot) {
                        int status = statusSnapshot.getValue(int.class);
                        if (status == 1){
                            sSwitch.setChecked(true);
                            img.setBackgroundResource(R.drawable.outleton);
                        } else {
                            sSwitch.setChecked(false);
                            img.setBackgroundResource(R.drawable.outletoff);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                delayRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot delaySnapshot) {
                        int delay = delaySnapshot.getValue(int.class);
                        edtDelay.setText(String.valueOf(delay));
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        }
    }
}