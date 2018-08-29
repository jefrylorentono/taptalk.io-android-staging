package com.moselo.HomingPigeon.View.Activity;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Manager.ChatManager;
import com.moselo.HomingPigeon.Manager.ConnectionManager;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;
import com.moselo.HomingPigeon.View.Adapter.RoomListAdapter;
import com.moselo.HomingPigeon.View.Helper.Const;

import java.util.ArrayList;
import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;

public class SampleRoomListActivity extends BaseActivity {

    private RecyclerView rvContactList;
    private RoomListAdapter adapter;
    private List<MessageEntity> roomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_room_list);

        if (ConnectionManager.ConnectionStatus.DISCONNECTED == ConnectionManager.getInstance().getConnectionStatus()){
            ConnectionManager.getInstance().connect();
        }

        initView();
    }

    private void initView() {
        roomList = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        //Dummy Rooms
        UserModel myUser = Utils.getInstance().fromJSON(new TypeReference<UserModel>() {}, prefs.getString(K_USER, ""));
        String userId = myUser.getUserID();
        MessageEntity roomDummy1 = new MessageEntity(
                "", "",
                ChatManager.getInstance().arrangeRoomId(userId, userId),
                1,
                "LastMessage",
                System.currentTimeMillis()/1000,
                prefs.getString(K_USER,"{}"));
        String dummyUser2 = Utils.getInstance().toJsonString(new UserModel("0", "BAMBANGS"));
        MessageEntity roomDummy2 = new MessageEntity(
                "", "",
                ChatManager.getInstance().arrangeRoomId(userId, "0"),
                1,
                "mas bambang's room",
                0,
                dummyUser2);
        roomList.add(roomDummy1);
        roomList.add(roomDummy2);

        adapter = new RoomListAdapter(roomList, getIntent().getStringExtra(Const.K_MY_USERNAME));
        rvContactList = findViewById(R.id.rv_contact_list);
        rvContactList.setAdapter(adapter);
        rvContactList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvContactList.setHasFixedSize(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConnectionManager.getInstance().close();
    }
}
