package com.atirek.alm.firebasechat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Alm on 8/16/2016.
 */
public class ChatRoom extends AppCompatActivity {

    Button btn_chat;
    EditText et_chat;
    ListView lv_chat;

    CustomAdapter adapter;
    ArrayList<ChatMessage> chatList = new ArrayList<>();

    String userName, roomName;

    DatabaseReference root;
    String temp_key;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chatList.clear();

        btn_chat = (Button) findViewById(R.id.btn_chat);
        et_chat = (EditText) findViewById(R.id.et_chat);
        lv_chat = (ListView) findViewById(R.id.lv_chat);

        userName = getIntent().getExtras().getString("user_name");
        roomName = getIntent().getExtras().getString("room_name");

        setTitle("Room: " + roomName);

        adapter = new CustomAdapter();
        lv_chat.setAdapter(adapter);

        root = FirebaseDatabase.getInstance().getReference().child(roomName);

        btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map1 = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map1);

                DatabaseReference messageRoot = root.child(temp_key);

                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name", userName);
                map2.put("msg", et_chat.getText().toString());

                messageRoot.updateChildren(map2);

                et_chat.setText("");

            }
        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                chatUpdate(dataSnapshot);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                chatUpdate(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                chatUpdate(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                chatUpdate(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void chatUpdate(DataSnapshot dataSnapshot) {

        try {

            Iterator i = dataSnapshot.getChildren().iterator();
            String chat_user = null, chat_msg = null;

            while (i.hasNext()) {

                chat_user = (String) ((DataSnapshot) i.next()).getValue();
                chat_msg = (String) ((DataSnapshot) i.next()).getValue();

                ChatMessage chatMessage = new ChatMessage(chat_user, chat_msg);
                chatList.add(chatMessage);

            }
        } catch (Exception e) {
            Toast.makeText(ChatRoom.this, "Connectivity Failure", Toast.LENGTH_SHORT).show();
        }

        adapter.notifyDataSetChanged();

    }

    public class CustomAdapter extends BaseAdapter {

        LayoutInflater inflater = getLayoutInflater();

        @Override
        public int getCount() {
            return chatList.size();
        }

        @Override
        public Object getItem(int i) {
            return chatList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        class ViewHolder {
            TextView user, msg;
        }

        @Override
        public int getItemViewType(int position) {
            if (chatList.get(position).getUser().equals(userName)) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                if (getItemViewType(i) == 0) {
                    convertView = inflater.inflate(R.layout.custom_chat_sender, null, false);
                } else {
                    convertView = inflater.inflate(R.layout.custom_chat_receiver, null, false);
                }

                holder.user = (TextView) convertView.findViewById(R.id.user);
                holder.msg = (TextView) convertView.findViewById(R.id.msg);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.user.setText((chatList.get(i).getUser()));
            holder.msg.setText((chatList.get(i).getMsg()));

            return convertView;
        }
    }


}
