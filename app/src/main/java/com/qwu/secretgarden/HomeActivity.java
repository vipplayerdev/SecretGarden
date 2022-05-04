package com.qwu.secretgarden;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    ListView record_list;
    List<UserObject> users;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        users = new ArrayList<UserObject>();
        record_list = findViewById(R.id.record_list);

        ImageView addMessage = findViewById(R.id.addMessage);
        addMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(HomeActivity.this, PostActivity.class);
                startActivity(i);
            }

        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getFamilyData();
    }


    private void getFamilyData(){

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<MessageObject> list = new ArrayList<MessageObject>();

                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    String key = ds.getKey();

                    if(key.indexOf("msg_") == 0){ //find the key math message data

                        try {

                            JSONObject obj = new JSONObject(ds.getValue().toString());
                            String id = obj.getString("id");
                            String msg = obj.getString("msg");
                            String thumb = obj.getString("thumb");
                            String user_id = obj.getString("user_id");
                            String family_code = obj.getString("family_code");
                            if(!family_code.equals(SqlLiteController.FamilyCode)){
                                continue;
                            }
                            String time = "0";
                            if(obj.has("time")){
                                time = obj.getString("time");
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                            String time2 = sdf.format(new Date(Long.valueOf(time)));

                            String user_name = "text";
                            if(thumb.length() > 10){
                                user_name = "text&picture";
                            }
                            MessageObject newObj = new MessageObject(id,msg,thumb,user_id,family_code,time2,user_name);
                            list.add(0,newObj);

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }


                    }else if(key.indexOf("user_") == 0) { //find the key math message data

                        try {

                            JSONObject obj = new JSONObject(ds.getValue().toString());
                            String id = obj.getString("id");
                            String account = obj.getString("account");
                            String avatar = obj.getString("avatar");
                            String name = obj.getString("name");

                            if(name.length() == 0){
                                name = account;
                            }
                            UserObject newObj = new UserObject(id,account,avatar,name);
                            users.add(newObj);

                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }

                }


                CustomAdapter jsonCustomAdapter = new CustomAdapter(HomeActivity.this, list);
                record_list.setAdapter(jsonCustomAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabase.addListenerForSingleValueEvent(eventListener);
    }


    public class CustomAdapter extends BaseAdapter {

        private LayoutInflater lInflater;
        private List<MessageObject> listStorage;

        public CustomAdapter(Context context, List<MessageObject>  listData) {
            lInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listStorage = listData;
        }


        @Override
        public int getCount() {
            return listStorage.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            TextView msg = null;
            ImageView thumb = null;
            TextView time = null;
            TextView user_name = null;
        }

        @SuppressLint("CutPasteId")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_layout, null);
                viewHolder = new ViewHolder();
                viewHolder.msg = (TextView) convertView.findViewById(R.id.list_item_text);
                viewHolder.thumb = (ImageView) convertView.findViewById(R.id.list_item_image);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.user_name = (TextView) convertView.findViewById(R.id.user_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.msg.setText(listStorage.get(position).getMsg());
            viewHolder.time.setText(listStorage.get(position).getTime());

            String uname = "";
            for(UserObject u :users){
                if(u.getId().equals(listStorage.get(position).getUserId())){
                    uname = u.getName();
                }
            }

            viewHolder.user_name.setText(uname);

            if(listStorage.get(position).getThumb().length() > 10){
                Picasso.with(HomeActivity.this).load(listStorage.get(position).getThumb()).into(viewHolder.thumb);

            }else{
                viewHolder.thumb.setVisibility(View.GONE);
            }
            return convertView;

        }

    }

}