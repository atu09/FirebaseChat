package com.atirek.alm.firebasechat;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Button btn_chatRoom;
    EditText et_chatRoom;
    ListView lv_chatRooms;

    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> roomList = new ArrayList<>();

    String userName;
    DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_chatRoom = (Button) findViewById(R.id.btn_chatRoom);
        et_chatRoom = (EditText) findViewById(R.id.et_chatRoom);
        lv_chatRooms = (ListView) findViewById(R.id.lv_chatRooms);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, roomList);
        lv_chatRooms.setAdapter(arrayAdapter);

        request_user_name();

        btn_chatRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(et_chatRoom.getText().toString(), "");
                root.updateChildren(map);

                et_chatRoom.setText("");
            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    Set<String> set = new HashSet<String>();
                    Iterator i = dataSnapshot.getChildren().iterator();

                    while (i.hasNext()) {
                        set.add(((DataSnapshot) i.next()).getKey());
                    }

                    roomList.clear();
                    roomList.addAll(set);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Connectivity Failure", Toast.LENGTH_SHORT).show();
                }

                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        lv_chatRooms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(MainActivity.this, ChatRoom.class);
                intent.putExtra("user_name", userName);
                intent.putExtra("room_name", ((TextView) view).getText().toString());
                startActivity(intent);

            }
        });

    }

    public void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Name: ");

        final EditText inputField = new EditText(this);
        builder.setView(inputField);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                userName = inputField.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });
        builder.show();
    }
}
