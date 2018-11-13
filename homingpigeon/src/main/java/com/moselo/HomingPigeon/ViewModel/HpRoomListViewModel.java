package com.moselo.HomingPigeon.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.moselo.HomingPigeon.Manager.TAPDataManager;
import com.moselo.HomingPigeon.Model.TAPRoomListModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HpRoomListViewModel extends AndroidViewModel {
    private List<TAPRoomListModel> roomList;
    private Map<String, TAPRoomListModel> roomPointer;
    private Map<String, TAPRoomListModel> selectedRooms;
    private boolean isSelecting;
    private static boolean isShouldNotLoadFromAPI = false;
    private boolean isDoneFirstSetup = false;
    private String myUserID;

    public HpRoomListViewModel(@NonNull Application application) {
        super(application);
        myUserID = TAPDataManager.getInstance().getActiveUser().getUserID();
    }

    public List<TAPRoomListModel> getRoomList() {
        return roomList == null ? roomList = new ArrayList<>() : roomList;
    }

    public void setRoomList(List<TAPRoomListModel> roomList) {
        this.roomList = roomList;
    }

    public void addRoomList(List<TAPRoomListModel> roomList) {
        getRoomList().addAll(roomList);
    }

    public void clearRoomList() {
        getRoomList().clear();
    }

    public Map<String, TAPRoomListModel> getRoomPointer() {
        return roomPointer == null ? roomPointer = new LinkedHashMap<>() : roomPointer;
    }

    public void setRoomPointer(Map<String, TAPRoomListModel> roomPointer) {
        this.roomPointer = roomPointer;
    }

    public void addRoomPointer(TAPRoomListModel roomModel){
        getRoomPointer().put(roomModel.getLastMessage().getRoom().getRoomID(), roomModel);
    }

    public Map<String, TAPRoomListModel> getSelectedRooms() {
        return selectedRooms == null ? selectedRooms = new LinkedHashMap<>() : selectedRooms;
    }

    public void setSelectedRooms(Map<String, TAPRoomListModel> selectedRooms) {
        this.selectedRooms = selectedRooms;
    }

    public int getSelectedCount() {
        return getSelectedRooms().size();
    }

    public boolean isSelecting() {
        return isSelecting;
    }

    public void setSelecting(boolean selecting) {
        isSelecting = selecting;
    }

    public String getMyUserID() {
        return myUserID;
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    public static boolean isShouldNotLoadFromAPI() {
        return isShouldNotLoadFromAPI;
    }

    public static void setShouldNotLoadFromAPI(boolean shouldNotLoadFromAPI) {
        isShouldNotLoadFromAPI = shouldNotLoadFromAPI;
    }

    public boolean isDoneFirstSetup() {
        return isDoneFirstSetup;
    }

    public void setDoneFirstSetup(boolean doneFirstSetup) {
        isDoneFirstSetup = doneFirstSetup;
    }
}
