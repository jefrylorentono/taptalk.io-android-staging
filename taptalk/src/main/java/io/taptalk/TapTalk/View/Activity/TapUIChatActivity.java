package io.taptalk.TapTalk.View.Activity;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.taptalk.TapTalk.API.View.TAPDefaultDataView;
import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.CircleImageView;
import io.taptalk.TapTalk.Helper.MaxHeightRecyclerView;
import io.taptalk.TapTalk.Helper.TAPBroadcastManager;
import io.taptalk.TapTalk.Helper.TAPChatRecyclerView;
import io.taptalk.TapTalk.Helper.TAPEndlessScrollListener;
import io.taptalk.TapTalk.Helper.TAPFileUtils;
import io.taptalk.TapTalk.Helper.TAPRoundedCornerImageView;
import io.taptalk.TapTalk.Helper.TAPTimeFormatter;
import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Helper.TAPVerticalDecoration;
import io.taptalk.TapTalk.Helper.TapTalk;
import io.taptalk.TapTalk.Helper.TapTalkDialog;
import io.taptalk.TapTalk.Interface.TapTalkActionInterface;
import io.taptalk.TapTalk.Listener.TAPAttachmentListener;
import io.taptalk.TapTalk.Listener.TAPChatListener;
import io.taptalk.TapTalk.Listener.TAPDatabaseListener;
import io.taptalk.TapTalk.Listener.TAPSocketListener;
import io.taptalk.TapTalk.Listener.TapListener;
import io.taptalk.TapTalk.Manager.TAPCacheManager;
import io.taptalk.TapTalk.Manager.TAPChatManager;
import io.taptalk.TapTalk.Manager.TAPConnectionManager;
import io.taptalk.TapTalk.Manager.TAPContactManager;
import io.taptalk.TapTalk.Manager.TAPDataManager;
import io.taptalk.TapTalk.Manager.TAPEncryptorManager;
import io.taptalk.TapTalk.Manager.TAPFileDownloadManager;
import io.taptalk.TapTalk.Manager.TAPFileUploadManager;
import io.taptalk.TapTalk.Manager.TAPGroupManager;
import io.taptalk.TapTalk.Manager.TAPMessageStatusManager;
import io.taptalk.TapTalk.Manager.TAPNetworkStateManager;
import io.taptalk.TapTalk.Manager.TAPNotificationManager;
import io.taptalk.TapTalk.Manager.TAPOldDataManager;
import io.taptalk.TapTalk.Manager.TapUI;
import io.taptalk.TapTalk.Model.ResponseModel.TAPAddContactResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPCreateRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetMessageListByRoomResponse;
import io.taptalk.TapTalk.Model.ResponseModel.TAPGetUserResponse;
import io.taptalk.TapTalk.Model.TAPErrorModel;
import io.taptalk.TapTalk.Model.TAPImageURL;
import io.taptalk.TapTalk.Model.TAPMediaPreviewModel;
import io.taptalk.TapTalk.Model.TAPMessageModel;
import io.taptalk.TapTalk.Model.TAPOnlineStatusModel;
import io.taptalk.TapTalk.Model.TAPRoomModel;
import io.taptalk.TapTalk.Model.TAPTypingModel;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.TapTalk.View.Adapter.TAPCustomKeyboardAdapter;
import io.taptalk.TapTalk.View.Adapter.TAPMessageAdapter;
import io.taptalk.TapTalk.View.Adapter.TapUserMentionListAdapter;
import io.taptalk.TapTalk.View.BottomSheet.TAPAttachmentBottomSheet;
import io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet;
import io.taptalk.TapTalk.View.Fragment.TAPConnectionStatusFragment;
import io.taptalk.TapTalk.ViewModel.TAPChatViewModel;
import io.taptalk.TapTalk.R;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.ApiErrorCode.USER_NOT_FOUND;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.CLEAR_ROOM_LIST_BADGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.CancelDownload;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.DownloadProgressLoading;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.DownloadBroadcastEvent.OpenFile;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.CLOSE_ACTIVITY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.COPY_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.GROUP_TYPING_MAP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.INSTANCE_KEY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.JUMP_TO_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MEDIA_PREVIEWS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.ROOM_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Extras.URL_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LOADING_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LATITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LOCATION_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Location.LONGITUDE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressChatBubble;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressEmail;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressLink;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressMention;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.LongPressBroadcastEvent.LongPressPhone;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.ADDRESS;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.CAPTION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URI;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.FILE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.IMAGE_URL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.MEDIA_TYPE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageData.THUMBNAIL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_DATE_SEPARATOR;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOADING_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_SYSTEM_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_TEXT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_UNREAD_MESSAGE_IDENTIFIER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_CAMERA_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_READ_EXTERNAL_STORAGE_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.PermissionRequest.PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.FORWARD;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.QuoteAction.REPLY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RELOAD_ROOM_LIST;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.FORWARD_MESSAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_GROUP_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.OPEN_MEMBER_PROFILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.PICK_LOCATION;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_IMAGE_FROM_CAMERA;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_GALLERY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RequestCode.SEND_MEDIA_FROM_PREVIEW;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_GROUP;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.ASCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.Sorting.DESCENDING;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.DELETE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.LEAVE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_ADD_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.ROOM_REMOVE_PARTICIPANT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_ROOM;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.SystemMessageAction.UPDATE_USER;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_EMIT_DELAY;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.TYPING_INDICATOR_TIMEOUT;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UNREAD_INDICATOR_LOCAL_ID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadCancelled;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFailed;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadFileData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadImageData;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadLocalID;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressFinish;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.UploadBroadcastEvent.UploadProgressLoading;
import static io.taptalk.TapTalk.Helper.CustomMaterialFilePicker.ui.FilePickerActivity.RESULT_FILE_PATH;
import static io.taptalk.TapTalk.Manager.TAPConnectionManager.ConnectionStatus.CONNECTED;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.CHAT_BUBBLE_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.EMAIL_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.LINK_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.MENTION_TYPE;
import static io.taptalk.TapTalk.View.BottomSheet.TAPLongPressActionBottomSheet.LongPressType.PHONE_TYPE;

public class TapUIChatActivity extends TAPBaseActivity {

    private String TAG = TapUIChatActivity.class.getSimpleName();

    // View
//    private SwipeBackLayout sblChat;
    private TAPChatRecyclerView rvMessageList;
    private RecyclerView rvCustomKeyboard;
    private MaxHeightRecyclerView rvUserMentionList;
    private FrameLayout flMessageList;
    private FrameLayout flRoomUnavailable;
    private FrameLayout flLoading;
    private LinearLayout llButtonDeleteChat;
    private ConstraintLayout clContainer;
    private ConstraintLayout clContactAction;
    private ConstraintLayout clUnreadButton;
    private ConstraintLayout clChatComposerAndHistory;
    private ConstraintLayout clEmptyChat;
    private ConstraintLayout clQuote;
    private ConstraintLayout clChatComposer;
    private ConstraintLayout clUserMentionList;
    private ConstraintLayout clRoomOnlineStatus;
    private ConstraintLayout clRoomTypingStatus;
    private ConstraintLayout clChatHistory;
    private EditText etChat;
    private ImageView ivButtonBack;
    private ImageView ivRoomIcon;
    private ImageView ivButtonDismissContactAction;
    private ImageView ivUnreadButtonImage;
    private ImageView ivButtonCancelReply;
    private ImageView ivChatMenu;
    private ImageView ivButtonChatMenu;
    private ImageView ivButtonAttach;
    private ImageView ivSend;
    private ImageView ivButtonSend;
    private ImageView ivToBottom;
    private ImageView ivMentionAnchor;
    private ImageView ivRoomTypingIndicator;
    private ImageView ivLoadingPopup;
    private CircleImageView civRoomImage;
    private CircleImageView civMyAvatarEmpty;
    private CircleImageView civRoomAvatarEmpty;
    private TAPRoundedCornerImageView rcivQuoteImage;
    private TextView tvRoomName;
    private TextView tvRoomStatus;
    private TextView tvRoomImageLabel;
    private TextView tvDateIndicator;
    private TextView tvUnreadButtonCount;
    private TextView tvChatEmptyGuide;
    private TextView tvMyAvatarLabelEmpty;
    private TextView tvRoomAvatarLabelEmpty;
    private TextView tvProfileDescription;
    private TextView tvQuoteTitle;
    private TextView tvQuoteContent;
    private TextView tvBadgeUnread;
    private TextView tvBadgeMentionCount;
    private TextView tvButtonBlockContact;
    private TextView tvButtonAddToContacts;
    private TextView tvRoomTypingStatus;
    private TextView tvChatHistoryContent;
    private TextView tvMessage;
    private TextView tvLoadingText;
    private View vRoomImage;
    private View vStatusBadge;
    private View vQuoteDecoration;
    private TAPConnectionStatusFragment fConnectionStatus;

    // RecyclerView
    private TAPMessageAdapter messageAdapter;
    private TAPCustomKeyboardAdapter customKeyboardAdapter;
    private TapUserMentionListAdapter userMentionListAdapter;
    private LinearLayoutManager messageLayoutManager;
    private SimpleItemAnimator messageAnimator;
    private TAPEndlessScrollListener endlessScrollListener;

    private TAPChatViewModel vm;
    private RequestManager glide;
    private TAPSocketListener socketListener;

    // Scroll state
    private enum STATE {WORKING, LOADED, DONE}

    private STATE state = STATE.WORKING;

    /**
     * =========================================================================================== *
     * START ACTIVITY
     * =========================================================================================== *
     */

    public static void start(Context context, String instanceKey, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor) {
        start(context, instanceKey, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), null, null);
    }

    public static void start(Context context, String instanceKey, String roomID, String roomName, TAPImageURL roomImage, int roomType, String roomColor, String jumpToMessageLocalID) {
        start(context, instanceKey, TAPRoomModel.Builder(roomID, roomName, roomType, roomImage, roomColor), null, jumpToMessageLocalID);
    }

    // Open chat room from notification
    public static void start(Context context, String instanceKey, TAPRoomModel roomModel) {
        start(context, instanceKey, roomModel, null, null);
    }

    // Open chat room from notification
    public static void start(Context context, String instanceKey, TAPRoomModel roomModel, LinkedHashMap<String, TAPUserModel> typingUser) {
        start(context, instanceKey, roomModel, typingUser, null);
    }

    public static void start(Context context, String instanceKey, TAPRoomModel roomModel, LinkedHashMap<String, TAPUserModel> typingUser, @Nullable String jumpToMessageLocalID) {
        if (TYPE_PERSONAL == roomModel.getRoomType() &&
                TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(
                        TAPChatManager.getInstance(instanceKey).getOtherUserIdFromRoom(roomModel.getRoomID()))) {
            // Disable opening active user's own room
            return;
        }

        TAPChatManager.getInstance(instanceKey).saveUnsentMessage();
        Intent intent = new Intent(context, TapUIChatActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(ROOM, roomModel);

        if (null != typingUser) {
            Gson gson = new Gson();
            String list = gson.toJson(typingUser);
            intent.putExtra(GROUP_TYPING_MAP, list);
        }
        if (null != jumpToMessageLocalID) {
            intent.putExtra(JUMP_TO_MESSAGE, jumpToMessageLocalID);
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            activity.runOnUiThread(() -> TAPUtils.dismissKeyboard(activity));
            activity.overridePendingTransition(R.anim.tap_slide_left, R.anim.tap_stay);
        }
    }

    public static PendingIntent generatePendingIntent(Context context, String instanceKey, TAPRoomModel roomModel) {
        Intent intent = new Intent(context, TapUIChatActivity.class);
        intent.putExtra(INSTANCE_KEY, instanceKey);
        intent.putExtra(ROOM, roomModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     * =========================================================================================== *
     * OVERRIDE METHODS
     * =========================================================================================== *
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_activity_chat);

        glide = Glide.with(this);
        bindViews();
        initRoom();
        registerBroadcastManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TAPChatManager.getInstance(instanceKey).setActiveRoom(vm.getRoom());
        etChat.setText(TAPChatManager.getInstance(instanceKey).getMessageFromDraft());
        showQuoteLayout(vm.getQuotedMessage(), vm.getQuoteAction(), false);

        if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getRoomType()) {
            callApiGetUserByUserID();
        } else {
            getRoomDataFromApi();
        }

        if (vm.isInitialAPICallFinished() && vm.getMessageModels().size() == 0 && TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
            fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveDraftToManager();
        sendTypingEmit(false);
        TAPChatManager.getInstance(instanceKey).deleteActiveRoom();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sendTypingEmitDelayTimer.cancel();
        typingIndicatorTimeoutTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Reload UI in room list
        Intent intent = new Intent(RELOAD_ROOM_LIST);
        intent.putExtra(ROOM_ID, vm.getRoom().getRoomID());
        LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent);

        TAPBroadcastManager.unregister(this, broadcastReceiver);
        TAPChatManager.getInstance(instanceKey).updateUnreadCountInRoomList(TAPChatManager.getInstance(instanceKey).getOpenRoom());
        TAPChatManager.getInstance(instanceKey).setOpenRoom(null); // Reset open room
        TAPChatManager.getInstance(instanceKey).removeChatListener(chatListener);
        TAPConnectionManager.getInstance(instanceKey).removeSocketListener(socketListener);
        vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable); // Stop offline timer
        TAPChatManager.getInstance(instanceKey).setNeedToCalledUpdateRoomStatusAPI(true);
        TAPFileDownloadManager.getInstance(instanceKey).clearFailedDownloads(); // Remove failed download list from active room
    }

    @Override
    public void onBackPressed() {
        if (vm.isDeleteGroup() && !TAPGroupManager.Companion.getInstance(instanceKey).getRefreshRoomList()) {
            TAPGroupManager.Companion.getInstance(instanceKey).setRefreshRoomList(true);
        }
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            hideKeyboards();
        } else {
            //TAPNotificationManager.getInstance(instanceKey).updateUnreadCount();
            new Thread(() -> TAPChatManager.getInstance(instanceKey).putUnsentMessageToList()).start();
            if (isTaskRoot()) {
                // Trigger listener callback if no other activity is open
                for (TapListener listener : TapTalk.getTapTalkListeners(instanceKey)) {
                    listener.onTaskRootChatRoomClosed(this);
                }
            }
            setResult(RESULT_OK);
            finish();
            overridePendingTransition(R.anim.tap_stay, R.anim.tap_slide_right);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            // Set active room to prevent null pointer when returning to chat
            TAPChatManager.getInstance(instanceKey).setActiveRoom(vm.getRoom());
            switch (requestCode) {
                case SEND_IMAGE_FROM_CAMERA:
                    if (null != intent && null != intent.getData()) {
                        vm.setCameraImageUri(intent.getData());
                    }
                    if (null == vm.getCameraImageUri()) {
                        return;
                    }
                    ArrayList<TAPMediaPreviewModel> imageCameraUris = new ArrayList<>();
                    imageCameraUris.add(TAPMediaPreviewModel.Builder(vm.getCameraImageUri(), TYPE_IMAGE, true));
                    openMediaPreviewPage(imageCameraUris);
                    break;
                case SEND_MEDIA_FROM_GALLERY:
                    if (null == intent) {
                        return;
                    }
                    ArrayList<TAPMediaPreviewModel> galleryMediaPreviews = new ArrayList<>();
                    ClipData clipData = intent.getClipData();
                    if (null != clipData) {
                        // Multiple media selection
                        galleryMediaPreviews = TAPUtils.getPreviewsFromClipData(TapUIChatActivity.this, clipData, true);
                    } else {
                        // Single media selection
                        Uri uri = intent.getData();
                        galleryMediaPreviews.add(TAPMediaPreviewModel.Builder(uri, TAPUtils.getMessageTypeFromFileUri(TapUIChatActivity.this, uri), true));
                    }
                    openMediaPreviewPage(galleryMediaPreviews);
                    break;
                case SEND_MEDIA_FROM_PREVIEW:
                    ArrayList<TAPMediaPreviewModel> medias = intent.getParcelableArrayListExtra(MEDIA_PREVIEWS);
                    if (null != medias && 0 < medias.size()) {
                        TAPChatManager.getInstance(instanceKey).sendImageOrVideoMessage(TapTalk.appContext, vm.getRoom(), medias);
                    }
                    break;
                case FORWARD_MESSAGE:
                    TAPRoomModel room = intent.getParcelableExtra(ROOM);
                    if (room.getRoomID().equals(vm.getRoom().getRoomID())) {
                        // Show message in composer
                        showQuoteLayout(intent.getParcelableExtra(MESSAGE), FORWARD, false);
                    } else {
                        // Open selected chat room
                        TAPChatManager.getInstance(instanceKey).setQuotedMessage(room.getRoomID(), intent.getParcelableExtra(MESSAGE), FORWARD);
                        start(TapUIChatActivity.this, instanceKey, room);
                        finish();
                    }
                    break;
                case PICK_LOCATION:
                    String address = intent.getStringExtra(LOCATION_NAME) == null ? "" : intent.getStringExtra(LOCATION_NAME);
                    Double latitude = intent.getDoubleExtra(LATITUDE, 0.0);
                    Double longitude = intent.getDoubleExtra(LONGITUDE, 0.0);
                    TAPChatManager.getInstance(instanceKey).sendLocationMessage(vm.getRoom(), address, latitude, longitude);
                    break;
                case SEND_FILE:
                    File tempFile = new File(intent.getStringExtra(RESULT_FILE_PATH));
                    if (null != tempFile) {
                        if (TAPFileUploadManager.getInstance(instanceKey).isSizeAllowedForUpload(tempFile.length()))
                            TAPChatManager.getInstance(instanceKey).sendFileMessage(TapUIChatActivity.this, vm.getRoom(), tempFile);
                        else {
                            new TapTalkDialog.Builder(TapUIChatActivity.this)
                                    .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                                    .setTitle(getString(R.string.tap_sorry))
                                    .setMessage(String.format(getString(R.string.tap_format_s_maximum_file_size), TAPUtils.getStringSizeLengthFile(TAPFileUploadManager.getInstance(instanceKey).getMaxFileUploadSize())))
                                    .setPrimaryButtonTitle(getString(R.string.tap_ok))
                                    .show();
                        }
                    }
                    break;

                case OPEN_GROUP_PROFILE:
                    vm.setDeleteGroup(true);
                    rvCustomKeyboard.setVisibility(View.GONE);
                    onBackPressed();
                    break;
                case OPEN_MEMBER_PROFILE:
                    if (intent.getBooleanExtra(CLOSE_ACTIVITY, false)) {
                        rvCustomKeyboard.setVisibility(View.GONE);
                        onBackPressed();
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_CAMERA_CAMERA:
                case PERMISSION_WRITE_EXTERNAL_STORAGE_CAMERA:
                    vm.setCameraImageUri(TAPUtils.takePicture(instanceKey, TapUIChatActivity.this, SEND_IMAGE_FROM_CAMERA));
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_GALLERY:
                    TAPUtils.pickMediaFromGallery(TapUIChatActivity.this, SEND_MEDIA_FROM_GALLERY, true);
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE:
                    if (null != messageAdapter) {
                        messageAdapter.notifyDataSetChanged();
                    }
                    break;
                case PERMISSION_READ_EXTERNAL_STORAGE_FILE:
                    TAPUtils.openDocumentPicker(TapUIChatActivity.this);
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO:
                    if (null != attachmentListener) {
                        attachmentListener.onSaveVideoToGallery(vm.getPendingDownloadMessage());
                    }
                    break;
                case PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE:
                    startFileDownload(vm.getPendingDownloadMessage());
                    break;
                case PERMISSION_LOCATION:
                    TAPUtils.openLocationPicker(TapUIChatActivity.this, instanceKey);
                    break;
            }
        }
    }

    /**
     * =========================================================================================== *
     * INIT CHAT ROOM
     * =========================================================================================== *
     */

    private void initRoom() {
        if (initViewModel()) {
            initView();
            initHelper();
            initListener();
            cancelNotificationWhenEnterRoom();
            //registerBroadcastManager();

//            if (null != clChatHistory) {
//                clChatHistory.setVisibility(View.GONE);
//            }
//            if (null != clChatComposer) {
//                clChatComposer.setVisibility(View.VISIBLE);
//            }

        } else if (vm.getMessageModels().size() == 0) {
            initView();
        }
    }

    private void bindViews() {
//        sblChat = getSwipeBackLayout();
        flMessageList = (FrameLayout) findViewById(R.id.fl_message_list);
        flRoomUnavailable = (FrameLayout) findViewById(R.id.fl_room_unavailable);
        flLoading = (FrameLayout) findViewById(R.id.fl_loading);
        llButtonDeleteChat = (LinearLayout) findViewById(R.id.ll_button_delete_chat);
        clContainer = (ConstraintLayout) findViewById(R.id.cl_container);
        clContactAction = (ConstraintLayout) findViewById(R.id.cl_contact_action);
        clUnreadButton = (ConstraintLayout) findViewById(R.id.cl_unread_button);
        clEmptyChat = (ConstraintLayout) findViewById(R.id.cl_empty_chat);
        clChatComposerAndHistory = (ConstraintLayout) findViewById(R.id.cl_chat_composer_and_history);
        clChatHistory = (ConstraintLayout) findViewById(R.id.cl_chat_history);
        clQuote = (ConstraintLayout) findViewById(R.id.cl_quote);
        clChatComposer = (ConstraintLayout) findViewById(R.id.cl_chat_composer);
        clUserMentionList = (ConstraintLayout) findViewById(R.id.cl_user_mention_list);
        clRoomOnlineStatus = (ConstraintLayout) findViewById(R.id.cl_room_online_status);
        clRoomTypingStatus = (ConstraintLayout) findViewById(R.id.cl_room_typing_status);
        ivButtonBack = (ImageView) findViewById(R.id.iv_button_back);
        ivRoomIcon = (ImageView) findViewById(R.id.iv_room_icon);
        ivButtonDismissContactAction = (ImageView) findViewById(R.id.iv_button_dismiss_contact_action);
        ivUnreadButtonImage = (ImageView) findViewById(R.id.iv_unread_button_image);
        ivButtonCancelReply = (ImageView) findViewById(R.id.iv_cancel_reply);
        ivChatMenu = (ImageView) findViewById(R.id.iv_chat_menu);
        ivButtonChatMenu = (ImageView) findViewById(R.id.iv_chat_menu_area);
        ivButtonAttach = (ImageView) findViewById(R.id.iv_attach);
        ivSend = (ImageView) findViewById(R.id.iv_send);
        ivButtonSend = (ImageView) findViewById(R.id.iv_send_area);
        ivToBottom = (ImageView) findViewById(R.id.iv_to_bottom);
        ivMentionAnchor = (ImageView) findViewById(R.id.iv_mention_anchor);
        ivRoomTypingIndicator = (ImageView) findViewById(R.id.iv_room_typing_indicator);
        ivLoadingPopup = findViewById(R.id.iv_loading_image);
        civRoomImage = (CircleImageView) findViewById(R.id.civ_room_image);
        civMyAvatarEmpty = (CircleImageView) findViewById(R.id.civ_my_avatar_empty);
        civRoomAvatarEmpty = (CircleImageView) findViewById(R.id.civ_room_avatar_empty);
        rcivQuoteImage = (TAPRoundedCornerImageView) findViewById(R.id.rciv_quote_image);
        tvRoomName = (TextView) findViewById(R.id.tv_room_name);
        tvRoomStatus = (TextView) findViewById(R.id.tv_room_status);
        tvRoomImageLabel = (TextView) findViewById(R.id.tv_room_image_label);
        tvRoomTypingStatus = (TextView) findViewById(R.id.tv_room_typing_status);
        tvButtonBlockContact = (TextView) findViewById(R.id.tv_button_block_contact);
        tvButtonAddToContacts = (TextView) findViewById(R.id.tv_button_add_to_contacts);
        tvDateIndicator = (TextView) findViewById(R.id.tv_date_indicator);
        tvUnreadButtonCount = (TextView) findViewById(R.id.tv_unread_button_count);
        tvChatEmptyGuide = (TextView) findViewById(R.id.tv_chat_empty_guide);
        tvMyAvatarLabelEmpty = (TextView) findViewById(R.id.tv_my_avatar_label_empty);
        tvRoomAvatarLabelEmpty = (TextView) findViewById(R.id.tv_room_avatar_label_empty);
        tvProfileDescription = (TextView) findViewById(R.id.tv_profile_description);
        tvQuoteTitle = (TextView) findViewById(R.id.tv_quote_title);
        tvQuoteContent = (TextView) findViewById(R.id.tv_quote_content);
        tvBadgeUnread = (TextView) findViewById(R.id.tv_badge_unread);
        tvBadgeMentionCount = (TextView) findViewById(R.id.tv_badge_mention_count);
        tvChatHistoryContent = (TextView) findViewById(R.id.tv_chat_history_content);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        tvLoadingText = findViewById(R.id.tv_loading_text);
        rvMessageList = (TAPChatRecyclerView) findViewById(R.id.rv_message_list);
        rvCustomKeyboard = (RecyclerView) findViewById(R.id.rv_custom_keyboard);
        rvUserMentionList = (MaxHeightRecyclerView) findViewById(R.id.rv_user_mention_list);
        etChat = (EditText) findViewById(R.id.et_chat);
        vRoomImage = findViewById(R.id.v_room_image);
        vStatusBadge = findViewById(R.id.v_room_status_badge);
        vQuoteDecoration = findViewById(R.id.v_quote_decoration);
        fConnectionStatus = (TAPConnectionStatusFragment) getSupportFragmentManager().findFragmentById(R.id.f_connection_status);
    }

    private boolean initViewModel() {
        vm = new ViewModelProvider(this,
                new TAPChatViewModel.TAPChatViewModelFactory(
                        getApplication(), instanceKey))
                .get(TAPChatViewModel.class);
        if (null == vm.getRoom()) {
            vm.setRoom(getIntent().getParcelableExtra(ROOM));
        }
        if (null == vm.getMyUserModel()) {
            vm.setMyUserModel(TAPChatManager.getInstance(instanceKey).getActiveUser());
        }

        if (null == vm.getRoom()) {
            Toast.makeText(TapTalk.appContext, getString(R.string.tap_error_room_not_found), Toast.LENGTH_SHORT).show();
            finish();
            return false;
        }
        if (null == vm.getOtherUserModel() && TYPE_PERSONAL == vm.getRoom().getRoomType()) {
            vm.setOtherUserModel(TAPContactManager.getInstance(instanceKey).getUserData(vm.getOtherUserID()));
        }

        // Updated 2020/02/10
        if (TYPE_PERSONAL != vm.getRoom().getRoomType() &&
                TAPGroupManager.Companion.getInstance(instanceKey).checkIsRoomDataAvailable(vm.getRoom().getRoomID())) {
            TAPRoomModel room = TAPGroupManager.Companion.getInstance(instanceKey).getGroupData(vm.getRoom().getRoomID());
            if (null != room && null != room.getRoomName() && !room.getRoomName().isEmpty()) {
                vm.setRoom(room);
            }
        }

        if (null != getIntent().getStringExtra(JUMP_TO_MESSAGE) && !vm.isInitialAPICallFinished()) {
            vm.setTappedMessageLocalID(getIntent().getStringExtra(JUMP_TO_MESSAGE));
        }

        getInitialUnreadCount();

        return null != vm.getMyUserModel() && (null != vm.getOtherUserModel() || (TYPE_PERSONAL != vm.getRoom().getRoomType()));
    }

    private void initView() {
        getWindow().setBackgroundDrawable(null);

        // Set room name
        if (vm.getRoom().getRoomType() == TYPE_PERSONAL && null != vm.getOtherUserModel() &&
                (null == vm.getOtherUserModel().getDeleted() || vm.getOtherUserModel().getDeleted() <= 0L) &&
                !vm.getOtherUserModel().getName().isEmpty()) {
            tvRoomName.setText(vm.getOtherUserModel().getName());
        } else {
            tvRoomName.setText(vm.getRoom().getRoomName());
        }

        if (!TapUI.getInstance(instanceKey).isProfileButtonVisible()) {
            civRoomImage.setVisibility(View.GONE);
            vRoomImage.setVisibility(View.GONE);
            tvRoomImageLabel.setVisibility(View.GONE);
        } else if (null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getRoomType() && null != vm.getOtherUserModel() &&
                (null == vm.getOtherUserModel().getDeleted() || vm.getOtherUserModel().getDeleted() <= 0L) &&
                null != vm.getOtherUserModel().getAvatarURL().getThumbnail() &&
                !vm.getOtherUserModel().getAvatarURL().getThumbnail().isEmpty()) {
            // Load user avatar URL
            loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civRoomImage, tvRoomImageLabel);
            vm.getRoom().setRoomImage(vm.getOtherUserModel().getAvatarURL());
        } else if (null != vm.getRoom() && !vm.getRoom().isRoomDeleted() && null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
            // Load room image
            loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomImage, tvRoomImageLabel);
        } else {
            loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
        }

        // TODO: 1 February 2019 SET ROOM ICON FROM ROOM MODEL
        if (null != vm.getOtherUserModel() && null != vm.getOtherUserModel().getUserRole() &&
                null != vm.getOtherUserModel().getUserRole().getRoleIconURL() && null != vm.getRoom() &&
                TYPE_PERSONAL == vm.getRoom().getRoomType() &&
                !vm.getOtherUserModel().getUserRole().getRoleIconURL().isEmpty()) {
            glide.load(vm.getOtherUserModel().getUserRole().getRoleIconURL()).into(ivRoomIcon);
            ivRoomIcon.setVisibility(View.VISIBLE);
        } else {
            ivRoomIcon.setVisibility(View.GONE);
        }

//        // Set typing status
//        if (getIntent().getBooleanExtra(IS_TYPING, false)) {
//            vm.setOtherUserTyping(true);
//            showTypingIndicator();
//        }

        if (null != getIntent().getStringExtra(GROUP_TYPING_MAP)) {
            try {
                String tempGroupTyping = getIntent().getStringExtra(GROUP_TYPING_MAP);
                Gson gson = new Gson();
                Type typingType = new TypeToken<LinkedHashMap<String, TAPUserModel>>() {
                }.getType();
                vm.setGroupTyping(gson.fromJson(tempGroupTyping, typingType));
                if (0 < vm.getGroupTypingSize()) showTypingIndicator();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Initialize chat message RecyclerView
        messageAdapter = new TAPMessageAdapter(instanceKey, glide, chatListener, vm.getMessageMentionIndexes());
        messageAdapter.setMessages(vm.getMessageModels());
        messageLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true) {
            @Override
            public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        messageLayoutManager.setStackFromEnd(true);
        rvMessageList.setAdapter(messageAdapter);
        rvMessageList.setLayoutManager(messageLayoutManager);
        rvMessageList.setHasFixedSize(false);
        rvMessageList.setupSwipeHelper(this, position -> {
            showQuoteLayout(messageAdapter.getItemAt(position), REPLY, true);
        });
        // FIXME: 9 November 2018 IMAGES/VIDEOS CURRENTLY NOT RECYCLED TO PREVENT INCONSISTENT DIMENSIONS
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_LEFT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_IMAGE_RIGHT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_LEFT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_VIDEO_RIGHT, 0);
        rvMessageList.getRecycledViewPool().setMaxRecycledViews(TAPDefaultConstant.BubbleType.TYPE_BUBBLE_PRODUCT_LIST, 0);
        messageAnimator = (SimpleItemAnimator) rvMessageList.getItemAnimator();
        if (null != messageAnimator) {
            messageAnimator.setSupportsChangeAnimations(false);
        }
        rvMessageList.setItemAnimator(null);
        rvMessageList.addOnScrollListener(messageListScrollListener);
//        OverScrollDecoratorHelper.setUpOverScroll(rvMessageList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL); FIXME: 8 Apr 2020 DISABLED OVERSCROLL DECORATOR

        // Listener for scroll pagination
        endlessScrollListener = new TAPEndlessScrollListener(messageLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!vm.isOnBottom()) {
                    loadMoreMessagesFromDatabase();
                }
            }
        };

        // Initialize custom keyboard
        vm.setCustomKeyboardItems(TAPChatManager.getInstance(instanceKey).getCustomKeyboardItems(vm.getRoom(), vm.getMyUserModel(), vm.getOtherUserModel()));
        if (null != vm.getCustomKeyboardItems() && vm.getCustomKeyboardItems().size() > 0) {
            // Enable custom keyboard
            vm.setCustomKeyboardEnabled(true);
            customKeyboardAdapter = new TAPCustomKeyboardAdapter(vm.getCustomKeyboardItems(), customKeyboardItemModel -> {
                TAPChatManager.getInstance(instanceKey).triggerCustomKeyboardItemTapped(TapUIChatActivity.this, customKeyboardItemModel, vm.getRoom(), vm.getMyUserModel(), vm.getOtherUserModel());
                hideUnreadButton();
            });
            rvCustomKeyboard.setAdapter(customKeyboardAdapter);
            rvCustomKeyboard.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            ivButtonChatMenu.setOnClickListener(v -> toggleCustomKeyboard());
        } else {
            // Disable custom keyboard
            vm.setCustomKeyboardEnabled(false);
            ivChatMenu.setVisibility(View.GONE);
            ivButtonChatMenu.setVisibility(View.GONE);
        }

        // Show / hide attachment button
        if (TapUI.getInstance(instanceKey).isDocumentAttachmentDisabled() &&
            TapUI.getInstance(instanceKey).isCameraAttachmentDisabled() &&
            TapUI.getInstance(instanceKey).isGalleryAttachmentDisabled() &&
            TapUI.getInstance(instanceKey).isLocationAttachmentDisabled()
        ) {
            ivButtonAttach.setVisibility(View.GONE);
            etChat.setPadding(
                    TAPUtils.dpToPx(12),
                    TAPUtils.dpToPx(6),
                    TAPUtils.dpToPx(12),
                    TAPUtils.dpToPx(6)
            );
        } else {
            ivButtonAttach.setVisibility(View.VISIBLE);
            etChat.setPadding(
                    TAPUtils.dpToPx(12),
                    TAPUtils.dpToPx(6),
                    TAPUtils.dpToPx(44),
                    TAPUtils.dpToPx(6)
            );
        }

        if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getRoomType()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_format_s_personal_chat_room_empty_guide_title), vm.getRoom().getRoomName())));
            tvProfileDescription.setText(String.format(getString(R.string.tap_format_s_personal_chat_room_empty_guide_content), vm.getRoom().getRoomName()));
        } else if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType() && null != vm.getRoom().getGroupParticipants()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_format_s_group_chat_room_empty_guide_title), vm.getRoom().getRoomName())));
            tvProfileDescription.setText(getString(R.string.tap_group_chat_room_empty_guide_content));
            tvRoomStatus.setText(String.format("%d Members", vm.getRoom().getGroupParticipants().size()));
            new Thread(() -> {
                for (TAPUserModel user : vm.getRoom().getGroupParticipants()) {
                    vm.addRoomParticipantByUsername(user);
                }
            }).start();
        } else if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType()) {
            tvChatEmptyGuide.setText(Html.fromHtml(String.format(getString(R.string.tap_format_s_group_chat_room_empty_guide_title), vm.getRoom().getRoomName())));
            tvProfileDescription.setText(getString(R.string.tap_group_chat_room_empty_guide_content));
        }

        // Load items from database for the first time
        if (vm.getRoom().isRoomDeleted()) {
            //showRoomIsUnavailableState();
            showChatAsHistory(getString(R.string.tap_group_unavailable));
        } else if (null != vm.getOtherUserModel() && null != vm.getOtherUserModel().getDeleted()) {
            showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
        }
//        else if (vm.getMessageModels().size() == 0 && !vm.getRoom().isRoomDeleted()) {
//            //vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
//            getAllUnreadMessage();
//        }

        if (vm.getRoom().isLocked()) {
            // Hide chat composer if room is locked
            lockChatRoom();
        }

        if (vm.getMessageModels().size() == 0) {
            getAllUnreadMessage();
        }

        LayoutTransition containerTransition = clContainer.getLayoutTransition();
        containerTransition.addTransitionListener(containerTransitionListener);

        etChat.addTextChangedListener(chatWatcher);
        etChat.setOnFocusChangeListener(chatFocusChangeListener);

//        sblChat.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
//        sblChat.setSwipeInterface(swipeInterface);

        vRoomImage.setOnClickListener(v -> openRoomProfile());
        ivButtonBack.setOnClickListener(v -> closeActivity());
        tvButtonBlockContact.setOnClickListener(v -> blockContact());
        tvButtonAddToContacts.setOnClickListener(v -> addUserToContacts());
        ivButtonDismissContactAction.setOnClickListener(v -> dismissContactAction());
        ivButtonCancelReply.setOnClickListener(v -> hideQuoteLayout());
        ivButtonAttach.setOnClickListener(v -> openAttachMenu());
        ivButtonSend.setOnClickListener(v -> buildAndSendTextMessage());
        ivToBottom.setOnClickListener(v -> scrollToBottom());
        ivMentionAnchor.setOnClickListener(v -> scrollToMessage(vm.getUnreadMentions().entrySet().iterator().next().getValue().getLocalID()));
        flMessageList.setOnClickListener(v -> chatListener.onOutsideClicked());
        flLoading.setOnClickListener(v -> {});

//        // TODO: 19 July 2019 SHOW CHAT AS HISTORY IF ACTIVE USER IS NOT IN PARTICIPANT LIST
//        if (null == vm.getRoom().getGroupParticipants()) {
//            showChatAsHistory(getString(R.string.tap_not_a_participant));
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            llButtonDeleteChat.setBackground(getDrawable(R.drawable.tap_bg_button_destructive_ripple));
            ivButtonAttach.setBackground(getDrawable(R.drawable.tap_bg_chat_composer_attachment_ripple));
            ivToBottom.setBackground(getDrawable(R.drawable.tap_bg_scroll_to_bottom_ripple));
            ivMentionAnchor.setBackground(getDrawable(R.drawable.tap_bg_scroll_to_bottom_ripple));
            clUnreadButton.setBackground(getDrawable(R.drawable.tap_bg_white_rounded_8dp_ripple));
        }
    }

    private void initHelper() {
        TAPChatManager.getInstance(instanceKey).addChatListener(chatListener);
    }

    private void initListener() {
        socketListener = new TAPSocketListener() {
            @Override
            public void onSocketConnected() {
                if (!vm.isInitialAPICallFinished()) {
                    // Call Message List API
                    if (null != vm.getRoom() && TYPE_GROUP == vm.getRoom().getRoomType()) {
                        getRoomDataFromApi();
                    } else if (null != vm.getRoom() && TYPE_PERSONAL == vm.getRoom().getRoomType())
                        callApiGetUserByUserID();
                }
                // Updated 2020-05-15
                if (vm.getMessageModels().size() > 0) {
                    callApiAfter();
                } else {
                    fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                }
                restartFailedDownloads();
            }
        };
        TAPConnectionManager.getInstance(instanceKey).addSocketListener(socketListener);
    }

    private void registerBroadcastManager() {
        TAPBroadcastManager.register(this, broadcastReceiver,
                UploadProgressLoading,
                UploadProgressFinish,
                UploadFailed,
                UploadCancelled,
                DownloadProgressLoading,
                DownloadFinish,
                DownloadFailed,
                DownloadFile,
                OpenFile,
                CancelDownload,
                LongPressChatBubble,
                LongPressEmail,
                LongPressLink,
                LongPressPhone,
                LongPressMention);
    }

    private void cancelNotificationWhenEnterRoom() {
        TAPNotificationManager.getInstance(instanceKey).cancelNotificationWhenEnterRoom(this, vm.getRoom().getRoomID());
        TAPNotificationManager.getInstance(instanceKey).clearNotificationMessagesMap(vm.getRoom().getRoomID());
    }

    /**
     * =========================================================================================== *
     * UI ACTIONS
     * =========================================================================================== *
     */

    private TAPChatListener chatListener = new TAPChatListener() {
        @Override
        public void onReceiveMessageInActiveRoom(TAPMessageModel message) {
            checkChatRoomLocked(message);
            handleSystemMessageAction(message);
            updateMessage(message);
        }

        @Override
        public void onUpdateMessageInActiveRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onDeleteMessageInActiveRoom(TAPMessageModel message) {
            updateMessageFromSocket(message);
        }

        @Override
        public void onReceiveMessageInOtherRoom(TAPMessageModel message) {
            super.onReceiveMessageInOtherRoom(message);

            if (null != TAPChatManager.getInstance(instanceKey).getOpenRoom() &&
                    TAPChatManager.getInstance(instanceKey).getOpenRoom().equals(message.getRoom().getRoomID()))
                updateMessage(message);
        }

        @Override
        public void onUpdateMessageInOtherRoom(TAPMessageModel message) {
            super.onUpdateMessageInOtherRoom(message);
        }

        @Override
        public void onDeleteMessageInOtherRoom(TAPMessageModel message) {
            super.onDeleteMessageInOtherRoom(message);
        }

        @Override
        public void onSendMessage(TAPMessageModel message) {
            addNewMessage(message);
            hideQuoteLayout();
            hideUnreadButton();
        }

        @Override
        public void onReplyMessage(TAPMessageModel message) {
            if (null != vm.getRoom()) {
                showQuoteLayout(message, REPLY, true);
                TAPChatManager.getInstance(instanceKey).removeUserInfo(vm.getRoom().getRoomID());
            }
        }

        @Override
        public void onRetrySendMessage(TAPMessageModel message) {
            vm.delete(message.getLocalID());
            if ((message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO || message.getType() == TYPE_FILE) && null != message.getData() &&
                    (null == message.getData().get(FILE_ID) || ((String) message.getData().get(FILE_ID)).isEmpty())) {
                // Re-upload image/video
                TAPChatManager.getInstance(instanceKey).retryUpload(TapUIChatActivity.this, message);
            } else {
                // Resend message
                TAPChatManager.getInstance(instanceKey).resendMessage(message);
            }
        }

        @Override
        public void onSendFailed(TAPMessageModel message) {
            vm.updateMessagePointer(message);
            vm.removeMessagePointer(message.getLocalID());
            runOnUiThread(() -> messageAdapter.notifyItemRangeChanged(0, messageAdapter.getItemCount()));
        }

        @Override
        public void onMessageRead(TAPMessageModel message) {
            if (vm.getUnreadCount() != 0) {
                //message.setIsRead(true);
                vm.removeUnreadMessage(message.getLocalID());
                updateUnreadCount();
            }
            vm.removeUnreadMention(message.getLocalID());
            updateMentionCount();
        }

        @Override
        public void onMentionClicked(TAPMessageModel message, String username) {
            TAPUserModel participant = vm.getRoomParticipantsByUsername().get(username);
            if (null != participant) {
                TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, participant, true);
            } else {
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username);
                if (null != user) {
                    TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, user, false);
                } else {
                    callApiGetUserByUsername(username, message);
                }
            }
        }

        @Override
        public void onMessageQuoteClicked(TAPMessageModel message) {
            TAPChatManager.getInstance(instanceKey).triggerMessageQuoteTapped(TapUIChatActivity.this, message);
            if (null != message.getReplyTo() &&
                    null != message.getReplyTo().getLocalID() &&
                    !message.getReplyTo().getLocalID().isEmpty() &&
                    message.getReplyTo().getMessageType() != -1 && // FIXME: 25 October 2019 MESSAGE TYPE -1 IS USED FOR DUMMY MESSAGE IN CHAT MANAGER setQuotedMessage
                    (null == message.getForwardFrom() ||
                            null == message.getForwardFrom().getFullname() ||
                            message.getForwardFrom().getFullname().isEmpty())) {
                scrollToMessage(message.getReplyTo().getLocalID());
            }
        }

        @Override
        public void onGroupMemberAvatarClicked(TAPMessageModel message) {
            openGroupMemberProfile(message.getUser());
        }

        @Override
        public void onOutsideClicked() {
            hideKeyboards();
        }

        @Override
        public void onBubbleExpanded() {
            if (messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                rvMessageList.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onLayoutLoaded(TAPMessageModel message) {
            if (/*message.getUser().getUserID().equals(vm.getMyUserModel().getUserID()) ||*/
                    messageLayoutManager.findFirstVisibleItemPosition() == 0) {
                // Scroll recycler to bottom when image finished loading if message is sent by user or recycler is on bottom
                rvMessageList.smoothScrollToPosition(0);
            }
        }

        @Override
        public void onUserOnlineStatusUpdate(TAPOnlineStatusModel onlineStatus) {
            setChatRoomStatus(onlineStatus);
        }

        @Override
        public void onReceiveStartTyping(TAPTypingModel typingModel) {
            if (typingModel.getRoomID().equals(vm.getRoom().getRoomID())) {
                vm.addGroupTyping(typingModel.getUser());
                showTypingIndicator();
            }
        }

        @Override
        public void onReceiveStopTyping(TAPTypingModel typingModel) {
            if (typingModel.getRoomID().equals(vm.getRoom().getRoomID())) {
                vm.removeGroupTyping(typingModel.getUser().getUserID());
                if (0 < vm.getGroupTypingSize()) {
                    showTypingIndicator();
                } else {
                    hideTypingIndicator();
                }
            }
        }
    };

    private void closeActivity() {
        rvCustomKeyboard.setVisibility(View.GONE);
        onBackPressed();
    }

    private void blockContact() {
        // TODO: 19 November 2019
        clContactAction.setVisibility(View.GONE);
    }

    private void addUserToContacts() {
        clContactAction.setVisibility(View.GONE);
        TAPDataManager.getInstance(instanceKey).addContactApi(vm.getOtherUserID(), addContactView);
    }

    private void dismissContactAction() {
        clContactAction.setVisibility(View.GONE);
        TAPDataManager.getInstance(instanceKey).saveChatRoomContactActionDismissed(vm.getRoom().getRoomID());
    }

    private void openRoomProfile() {
        TAPChatManager.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(TapUIChatActivity.this, vm.getRoom(), vm.getOtherUserModel());
        hideUnreadButton();
    }

    private void openGroupMemberProfile(TAPUserModel groupMember) {
        TAPChatManager.getInstance(instanceKey).triggerChatRoomProfileButtonTapped(TapUIChatActivity.this, vm.getRoom(), groupMember);
        hideUnreadButton();
    }

    private void loadProfilePicture(String image, ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE) {
            return;
        }
        glide.load(image).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                runOnUiThread(() -> loadInitialsToProfilePicture(imageView, tvAvatarLabel));
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ImageViewCompat.setImageTintList(imageView, null);
                tvAvatarLabel.setVisibility(View.GONE);
                return false;
            }
        }).into(imageView);
    }

    private void loadInitialsToProfilePicture(ImageView imageView, TextView tvAvatarLabel) {
        if (imageView.getVisibility() == View.GONE) {
            return;
        }
        if (tvAvatarLabel == tvMyAvatarLabelEmpty) {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getRandomColor(this, TAPChatManager.getInstance(instanceKey).getActiveUser().getName())));
            tvAvatarLabel.setText(TAPUtils.getInitials(TAPChatManager.getInstance(instanceKey).getActiveUser().getName(), 2));
        } else {
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(TAPUtils.getRandomColor(this, vm.getRoom().getRoomName())));
            tvAvatarLabel.setText(TAPUtils.getInitials(vm.getRoom().getRoomName(), vm.getRoom().getRoomType() == TYPE_PERSONAL ? 2 : 1));
        }
        imageView.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_circle_9b9b9b));
        tvAvatarLabel.setVisibility(View.VISIBLE);
    }

    private void showMessageList() {
        flMessageList.setVisibility(View.VISIBLE);
//        flMessageList.post(() -> {
//            TAPMessageModel message = messageAdapter.getItemAt(messageLayoutManager.findLastVisibleItemPosition());
//            if (null != message) {
//                tvDateIndicator.setVisibility(View.VISIBLE);
//                tvDateIndicator.setText(TAPTimeFormatter.getInstance().dateStampString(this, message.getCreated()));
//            }
//        });
    }

    private void updateUnreadCount() {
        runOnUiThread(() -> {
            if (vm.isOnBottom() || vm.getUnreadCount() == 0) {
                tvBadgeUnread.setVisibility(View.GONE);
                if (View.GONE != ivToBottom.getVisibility()) {
                    ivToBottom.setVisibility(View.GONE);
                }
            } else if (vm.getUnreadCount() > 0) {
                tvBadgeUnread.setText(String.valueOf(vm.getUnreadCount()));
                tvBadgeUnread.setVisibility(View.VISIBLE);
                if (View.VISIBLE != ivToBottom.getVisibility()) {
                    ivToBottom.setVisibility(View.VISIBLE);
                }
            } else if (View.VISIBLE == ivToBottom.getVisibility()) {
                ivToBottom.setVisibility(View.GONE);
            }
        });
    }

    private void updateMentionCount() {
        runOnUiThread(() -> {
            if (vm.getUnreadMentionCount() > 0) {
                tvBadgeMentionCount.setText(String.valueOf(vm.getUnreadMentionCount()));
                tvBadgeMentionCount.setVisibility(View.VISIBLE);
                if (View.VISIBLE != ivMentionAnchor.getVisibility()) {
                    ivMentionAnchor.setVisibility(View.VISIBLE);
                }
            } else {
                ivMentionAnchor.setVisibility(View.GONE);
                tvBadgeMentionCount.setVisibility(View.GONE);
            }
        });
    }

    private void updateMessageDecoration() {
        // Update decoration for the top item in recycler view
        runOnUiThread(() -> {
            if (rvMessageList.getItemDecorationCount() > 0) {
                rvMessageList.removeItemDecorationAt(0);
            }
            rvMessageList.addItemDecoration(new TAPVerticalDecoration(TAPUtils.dpToPx(10), 0, messageAdapter.getItemCount() - 1));
        });
    }

    private void showQuoteLayout(@Nullable TAPMessageModel message, int quoteAction, boolean showKeyboard) {
        if (null == message) {
            return;
        }
        vm.setQuotedMessage(message, quoteAction);
        boolean quotedOwnMessage = null != TAPChatManager.getInstance(instanceKey).getActiveUser() &&
                TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(message.getUser().getUserID());
        runOnUiThread(() -> {
            clQuote.setVisibility(View.VISIBLE);
            // Add other quotable message type here
            if ((message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) && null != message.getData()) {
                // Show image quote
                vQuoteDecoration.setVisibility(View.GONE);
                // TODO: 29 January 2019 IMAGE MIGHT NOT EXIST IN CACHE
                Drawable drawable = TAPCacheManager.getInstance(this).getBitmapDrawable(TAPUtils.getUriKeyFromMessage(message));
                if (null != drawable) {
                    rcivQuoteImage.setImageDrawable(drawable);
                } else {
                    // Show small thumbnail
                    Drawable thumbnail = new BitmapDrawable(
                            getResources(),
                            TAPFileUtils.getInstance().decodeBase64(
                                    (String) (null == message.getData().get(THUMBNAIL) ? "" :
                                            message.getData().get(THUMBNAIL))));
                    rcivQuoteImage.setImageDrawable(thumbnail);
                }
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else if (message.getType() == TYPE_FILE && null != message.getData()) {
                // Show file quote
                vQuoteDecoration.setVisibility(View.GONE);
                rcivQuoteImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_documents_white));
                rcivQuoteImage.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconFileLeft));
                rcivQuoteImage.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_quote_layout_file));
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                tvQuoteTitle.setText(TAPUtils.getFileDisplayName(message));
                tvQuoteContent.setText(TAPUtils.getFileDisplayInfo(message));
                tvQuoteContent.setMaxLines(1);
            } else if (null != message.getData() && null != message.getData().get(FILE_URL)) {
                // Show image quote from file URL
                glide.load((String) message.getData().get(FILE_URL)).into(rcivQuoteImage);
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                vQuoteDecoration.setVisibility(View.GONE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else if (null != message.getData() && null != message.getData().get(IMAGE_URL)) {
                // Show image quote from image URL
                glide.load((String) message.getData().get(IMAGE_URL)).into(rcivQuoteImage);
                rcivQuoteImage.setColorFilter(null);
                rcivQuoteImage.setBackground(null);
                rcivQuoteImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                rcivQuoteImage.setVisibility(View.VISIBLE);
                vQuoteDecoration.setVisibility(View.GONE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(1);
            } else {
                // Show text quote
                vQuoteDecoration.setVisibility(View.VISIBLE);
                rcivQuoteImage.setVisibility(View.GONE);

                if (quotedOwnMessage) {
                    tvQuoteTitle.setText(getResources().getText(R.string.tap_you));
                } else {
                    tvQuoteTitle.setText(message.getUser().getName());
                }
                tvQuoteContent.setText(message.getBody());
                tvQuoteContent.setMaxLines(2);
            }
            boolean hadFocus = etChat.hasFocus();
            if (/*hadFocus && */showKeyboard) {
                TAPUtils.showKeyboard(this, etChat);
                //clContainer.post(() -> etChat.requestFocus());
                // FIXME: 17 Apr 2020
                new Handler().postDelayed(() -> etChat.requestFocus(), 300L);
            }
            if (!hadFocus && etChat.getSelectionEnd() == 0) {
                etChat.setSelection(etChat.getText().length());
            }
        });
    }

    private void hideQuoteLayout() {
        vm.setQuotedMessage(null, 0);
        boolean hasFocus = etChat.hasFocus();
        if (clQuote.getVisibility() == View.VISIBLE) {
            runOnUiThread(() -> {
                clQuote.setVisibility(View.GONE);
                if (hasFocus) {
                    clQuote.post(() -> etChat.requestFocus());
                }
            });
        }
    }

    private void scrollToBottom() {
        ivToBottom.setVisibility(View.GONE);
        rvMessageList.scrollToPosition(0);
        tvDateIndicator.setVisibility(View.GONE);
        vm.setOnBottom(true);
        vm.clearUnreadMessages();
        vm.clearUnreadMentions();
        updateUnreadCount();
        updateMentionCount();
    }

    private void toggleCustomKeyboard() {
        if (rvCustomKeyboard.getVisibility() == View.VISIBLE) {
            showNormalKeyboard();
        } else {
            showCustomKeyboard();
        }
    }

    private void showNormalKeyboard() {
        rvCustomKeyboard.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple));
        } else {
            ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu));
        }
        ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
        ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
        //etChat.requestFocus();
        TAPUtils.showKeyboard(this, etChat);
    }

    private void showCustomKeyboard() {
        TAPUtils.dismissKeyboard(this);
        etChat.clearFocus();
        new Handler().postDelayed(() -> {
            rvCustomKeyboard.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_show_keyboard_ripple));
            } else {
                ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_show_keyboard));
            }
            ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_keyboard_white));
            ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerShowKeyboard));
        }, 150L);
    }

    private void hideKeyboards() {
        rvCustomKeyboard.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ivButtonChatMenu.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_burger_menu_ripple));
        } else {
            ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_bg_chat_composer_burger_menu));
        }
        ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
        ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
        TAPUtils.dismissKeyboard(this);
    }

    private void openAttachMenu() {
        //if (!etChat.hasFocus()) {
        //    etChat.requestFocus();
        //}
        TAPUtils.dismissKeyboard(this);
        TAPAttachmentBottomSheet attachBottomSheet = new TAPAttachmentBottomSheet(instanceKey, attachmentListener);
        attachBottomSheet.show(getSupportFragmentManager(), "");
    }

    private TAPAttachmentListener attachmentListener = new TAPAttachmentListener(instanceKey) {
        @Override
        public void onCameraSelected() {
            if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == CONNECTED)
                fConnectionStatus.hideUntilNextConnect(true);
            vm.setCameraImageUri(TAPUtils.takePicture(instanceKey, TapUIChatActivity.this, SEND_IMAGE_FROM_CAMERA));
        }

        @Override
        public void onGallerySelected() {
            if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() == CONNECTED)
                fConnectionStatus.hideUntilNextConnect(true);
            TAPUtils.pickMediaFromGallery(TapUIChatActivity.this, SEND_MEDIA_FROM_GALLERY, true);
        }

        @Override
        public void onLocationSelected() {
            TAPUtils.openLocationPicker(TapUIChatActivity.this, instanceKey);
        }

        @Override
        public void onDocumentSelected() {
            TAPUtils.openDocumentPicker(TapUIChatActivity.this);
        }

        @Override
        public void onCopySelected(String text) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(text, text);
            clipboard.setPrimaryClip(clip);
        }

        @Override
        public void onReplySelected(TAPMessageModel message) {
            chatListener.onReplyMessage(message);
        }

        @Override
        public void onForwardSelected(TAPMessageModel message) {
            TAPForwardPickerActivity.start(TapUIChatActivity.this, instanceKey, message);
        }

        @Override
        public void onOpenLinkSelected(String url) {
            TAPUtils.openCustomTabLayout(TapUIChatActivity.this, url);
        }

        @Override
        public void onComposeSelected(String emailRecipient) {
            TAPUtils.composeEmail(TapUIChatActivity.this, emailRecipient);
        }

        @Override
        public void onPhoneCallSelected(String phoneNumber) {
            TAPUtils.openDialNumber(TapUIChatActivity.this, phoneNumber);
        }

        @Override
        public void onPhoneSmsSelected(String phoneNumber) {
            TAPUtils.composeSMS(TapUIChatActivity.this, phoneNumber);
        }

        @Override
        public void onSaveImageToGallery(TAPMessageModel message) {
            if (!TAPUtils.hasPermissions(TapUIChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Request storage permission
                vm.setPendingDownloadMessage(message);
                ActivityCompat.requestPermissions(TapUIChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_IMAGE);
            } else if (null != message.getData() && null != message.getData().get(MEDIA_TYPE)) {
                new Thread(() -> {
                    vm.setPendingDownloadMessage(null);
                    Bitmap bitmap = null;
                    if (null != message.getData().get(FILE_ID)) {
                        // Get bitmap from cache
                        bitmap = TAPCacheManager.getInstance(TapTalk.appContext).getBitmapDrawable((String) message.getData().get(FILE_ID)).getBitmap();
                    } else if (null != message.getData().get(FILE_URL)) {
                        // Get bitmap from URL
                        try {
                            URL imageUrl = new URL((String) message.getData().get(FILE_URL));
                            bitmap = BitmapFactory.decodeStream(imageUrl.openConnection().getInputStream());
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if (null != bitmap) {
                        TAPFileDownloadManager.getInstance(instanceKey).writeImageFileToDisk(TapUIChatActivity.this,
                                System.currentTimeMillis(), bitmap,
                                (String) message.getData().get(MEDIA_TYPE), new TapTalkActionInterface() {
                                    @Override
                                    public void onSuccess(String message) {
                                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                                    }
                                });
                    }
                }).start();
            }
        }

        @Override
        public void onSaveVideoToGallery(TAPMessageModel message) {
            if (!TAPUtils.hasPermissions(TapUIChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Request storage permission
                vm.setPendingDownloadMessage(message);
                ActivityCompat.requestPermissions(TapUIChatActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_VIDEO);
            } else if (null != message.getData() &&
                    null != message.getData().get(FILE_ID) &&
                    null != message.getData().get(MEDIA_TYPE) &&
                    null != message.getData().get(FILE_ID)) {
                vm.setPendingDownloadMessage(null);
                TAPFileDownloadManager.getInstance(instanceKey).writeFileToDisk(TapUIChatActivity.this, message, new TapTalkActionInterface() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });
            }
            // TODO: 6 Dec 2019 HANDLE FILE URL
        }

        @Override
        public void onSaveToDownloads(TAPMessageModel message) {
            if (null != message.getData() &&
                    null != message.getData().get(FILE_ID) &&
                    null != message.getData().get(MEDIA_TYPE) &&
                    null != message.getData().get(FILE_ID)) {
                TAPFileDownloadManager.getInstance(instanceKey).writeFileToDisk(TapUIChatActivity.this, message, new TapTalkActionInterface() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, message, Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> Toast.makeText(TapUIChatActivity.this, errorMessage, Toast.LENGTH_SHORT).show());
                    }
                });
            }
            // TODO: 6 Dec 2019 HANDLE FILE URL
        }

        @Override
        public void onViewProfileSelected(String username, TAPMessageModel message) {
            TAPUserModel participant = vm.getRoomParticipantsByUsername().get(username);
            if (null != participant) {
                TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, participant, true);
            } else {
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username);
                if (null != user) {
                    TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, user, false);
                } else {
                    callApiGetUserByUsername(username, message);
                }
            }
        }

        @Override
        public void onSendMessageSelected(String username) {
            TAPUserModel participant = vm.getRoomParticipantsByUsername().get(username);
            if (null != participant) {
                TapUI.getInstance().openChatRoomWithOtherUser(TapUIChatActivity.this, participant);
                rvCustomKeyboard.setVisibility(View.GONE);
                onBackPressed();
            } else {
                TAPUserModel user = TAPContactManager.getInstance(instanceKey).getUserDataByUsername(username);
                if (null != user) {
                    TapUI.getInstance().openChatRoomWithOtherUser(TapUIChatActivity.this, user);
                    rvCustomKeyboard.setVisibility(View.GONE);
                    onBackPressed();
                } else {
                    callApiGetUserByUsername(username, null);
                }
            }
        }
    };

    private void setChatRoomStatus(TAPOnlineStatusModel onlineStatus) {
        vm.setOnlineStatus(onlineStatus);
        if (onlineStatus.getUser().getUserID().equals(vm.getOtherUserID()) && onlineStatus.getOnline()) {
            // User is online
            showUserOnline();
        } else if (onlineStatus.getUser().getUserID().equals(vm.getOtherUserID()) && !onlineStatus.getOnline()) {
            // User is offline
            showUserOffline();
        }
    }

    private void showUserOnline() {
        runOnUiThread(() -> {
            if (0 >= vm.getGroupTypingSize()) {
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            vStatusBadge.setVisibility(View.VISIBLE);
            vStatusBadge.setBackground(ContextCompat.getDrawable(this, R.drawable.tap_bg_circle_active));
            tvRoomStatus.setText(getString(R.string.tap_active_now));
            vm.getLastActivityHandler().removeCallbacks(lastActivityRunnable);
        });
    }

    private void showUserOffline() {
        runOnUiThread(() -> {
            if (0 >= vm.getGroupTypingSize()) {
                clRoomTypingStatus.setVisibility(View.GONE);
                clRoomOnlineStatus.setVisibility(View.VISIBLE);
            }
            lastActivityRunnable.run();
        });
    }

    private Runnable lastActivityRunnable = new Runnable() {
        final int INTERVAL = 1000 * 60;

        @Override
        public void run() {
            Long lastActive = vm.getOnlineStatus().getLastActive();
            if (lastActive == 0) {
                runOnUiThread(() -> {
                    vStatusBadge.setVisibility(View.VISIBLE);
                    vStatusBadge.setBackground(null);
                    tvRoomStatus.setText("");
                });
            } else {
                runOnUiThread(() -> {
                    //vStatusBadge.setBackground(getDrawable(R.drawable.tap_bg_circle_butterscotch));
                    vStatusBadge.setVisibility(View.GONE);
                    tvRoomStatus.setText(TAPTimeFormatter.getInstance().getLastActivityString(TapUIChatActivity.this, lastActive));
                });
            }
            vm.getLastActivityHandler().postDelayed(this, INTERVAL);
        }
    };

    private void sendTypingEmit(boolean isTyping) {
        if (TAPConnectionManager.getInstance(instanceKey).getConnectionStatus() != CONNECTED) {
            return;
        }
        String currentRoomID = vm.getRoom().getRoomID();
        if (isTyping && !vm.isActiveUserTyping()) {
            TAPChatManager.getInstance(instanceKey).sendStartTypingEmit(currentRoomID);
            vm.setActiveUserTyping(true);
            sendTypingEmitDelayTimer.cancel();
            sendTypingEmitDelayTimer.start();
        } else if (!isTyping && vm.isActiveUserTyping()) {
            TAPChatManager.getInstance(instanceKey).sendStopTypingEmit(currentRoomID);
            vm.setActiveUserTyping(false);
            sendTypingEmitDelayTimer.cancel();
        }
    }

    private void showTypingIndicator() {
        typingIndicatorTimeoutTimer.cancel();
        typingIndicatorTimeoutTimer.start();
        runOnUiThread(() -> {
            clRoomTypingStatus.setVisibility(View.VISIBLE);
            clRoomOnlineStatus.setVisibility(View.GONE);

            if (TYPE_PERSONAL == vm.getRoom().getRoomType()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(getString(R.string.tap_typing));
            } else if (1 < vm.getGroupTypingSize()) {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_format_d_people_typing), vm.getGroupTypingSize()));
            } else {
                glide.load(R.raw.gif_typing_indicator).into(ivRoomTypingIndicator);
                //tvRoomTypingStatus.setText(getString(R.string.tap_typing));
                tvRoomTypingStatus.setText(String.format(getString(R.string.tap_format_s_typing_single), vm.getFirstTypingUserName()));
            }
        });
    }

    private void hideTypingIndicator() {
        typingIndicatorTimeoutTimer.cancel();
        runOnUiThread(() -> {
            vm.getGroupTyping().clear();
            clRoomTypingStatus.setVisibility(View.GONE);
            clRoomOnlineStatus.setVisibility(View.VISIBLE);
        });
    }

    private CountDownTimer sendTypingEmitDelayTimer = new CountDownTimer(TYPING_EMIT_DELAY, 1000L) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            vm.setActiveUserTyping(false);
        }
    };

    private CountDownTimer typingIndicatorTimeoutTimer = new CountDownTimer(TYPING_INDICATOR_TIMEOUT, 1000L) {
        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            hideTypingIndicator();
        }
    };

    private void saveDraftToManager() {
        String draft = etChat.getText().toString();
        if (!draft.isEmpty()) {
            TAPChatManager.getInstance(instanceKey).saveMessageToDraft(draft);
        } else {
            TAPChatManager.getInstance(instanceKey).removeDraft();
        }
    }

    private void openMediaPreviewPage(ArrayList<TAPMediaPreviewModel> mediaPreviews) {
        TAPMediaPreviewActivity.start(TapUIChatActivity.this, instanceKey, mediaPreviews, new ArrayList<>(vm.getRoomParticipantsByUsername().values()));
    }

    // Previously callApiGetGroupData
    private void getRoomDataFromApi() {
        new Thread(() -> TAPDataManager.getInstance(instanceKey).getChatRoomData(vm.getRoom().getRoomID(), new TAPDefaultDataView<TAPCreateRoomResponse>() {
            @Override
            public void onSuccess(TAPCreateRoomResponse response) {
                vm.setRoom(response.getRoom());
                vm.getRoom().setAdmins(response.getAdmins());
                vm.getRoom().setGroupParticipants(response.getParticipants());
                TAPGroupManager.Companion.getInstance(instanceKey).addGroupData(vm.getRoom());

                if (null != vm.getRoom() && null != vm.getRoom().getRoomImage() && !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
                    // Load room image
                    loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomImage, tvRoomImageLabel);
                } else {
                    // Room image is empty
                    loadInitialsToProfilePicture(civRoomImage, tvRoomImageLabel);
                }

                tvRoomName.setText(vm.getRoom().getRoomName());

                if (vm.getRoom().getRoomType() == TYPE_GROUP && null != vm.getRoom().getGroupParticipants()) {
                    // Show number of participants for group room
                    tvRoomStatus.setText(String.format(getString(R.string.tap_format_d_group_member_count), vm.getRoom().getGroupParticipants().size()));
                }
                if (null != vm.getRoom().getGroupParticipants()) {
                    new Thread(() -> {
                        vm.getRoomParticipantsByUsername().clear();
                        for (TAPUserModel user : vm.getRoom().getGroupParticipants()) {
                            vm.addRoomParticipantByUsername(user);
                        }
                    }).start();
                }
            }
        })).start();
    }

    private void callApiGetUserByUserID() {
        new Thread(() -> {
            if (TAPChatManager.getInstance(instanceKey).isNeedToCalledUpdateRoomStatusAPI() &&
                    TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this))
                TAPDataManager.getInstance(instanceKey).getUserByIdFromApi(vm.getOtherUserID(), new TAPDefaultDataView<TAPGetUserResponse>() {
                    @Override
                    public void onSuccess(TAPGetUserResponse response) {
                        TAPUserModel userResponse = response.getUser();
                        TAPContactManager.getInstance(instanceKey).updateUserData(userResponse);
                        TAPOnlineStatusModel onlineStatus = TAPOnlineStatusModel.Builder(userResponse);
                        setChatRoomStatus(onlineStatus);
                        TAPChatManager.getInstance(instanceKey).setNeedToCalledUpdateRoomStatusAPI(false);

                        if (null == vm.getOtherUserModel()) {
                            vm.setOtherUserModel(response.getUser());
                            initRoom();
                        }

                        if (!TAPDataManager.getInstance(instanceKey).isChatRoomContactActionDismissed(vm.getRoom().getRoomID()) &&
                                (null == vm.getOtherUserModel().getIsContact() || vm.getOtherUserModel().getIsContact() == 0)) {
                            clContactAction.setVisibility(View.VISIBLE);
                        } else {
                            clContactAction.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(TAPErrorModel error) {
                        if (null != error.getCode() && error.getCode().equals(String.valueOf(USER_NOT_FOUND))) {
                            showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
                        }
                    }
                });
            else if (null == vm.getOtherUserModel()) {
                showChatAsHistory(getString(R.string.tap_this_user_is_no_longer_available));
            }
        }).start();
    }

    // For mentioned user data
    private void callApiGetUserByUsername(String username, @Nullable TAPMessageModel message) {
        TAPDataManager.getInstance(instanceKey).getUserByUsernameFromApi(username, true, new TAPDefaultDataView<TAPGetUserResponse>() {
            private boolean isCanceled = false;

            @Override
            public void startLoading() {
                showLoadingPopup();
                tvLoadingText.setOnClickListener(v -> {
                    hideLoadingPopup();
                    isCanceled = true;
                });
            }

            @Override
            public void onSuccess(TAPGetUserResponse response) {
                TAPUserModel userResponse = response.getUser();
                TAPContactManager.getInstance(instanceKey).updateUserData(userResponse);
                if (!isCanceled) {
                    hideLoadingPopup();
                    if (null == message) {
                        // Open chat room if message is null (from send message menu)
                        TapUI.getInstance().openChatRoomWithOtherUser(TapUIChatActivity.this, userResponse);
                        rvCustomKeyboard.setVisibility(View.GONE);
                        onBackPressed();
                    } else {
                        // Open profile if message is not null (from mention tap/view profile menu)
                        TAPChatManager.getInstance(instanceKey).triggerUserMentionTapped(TapUIChatActivity.this, message, userResponse, false);
                    }
                }
            }

            @Override
            public void onError(TAPErrorModel error) {
                if (!isCanceled) {
                    hideLoadingPopup();
                    showErrorDialog(getString(R.string.tap_error), error.getMessage());
                }
            }

            @Override
            public void onError(String errorMessage) {
                if (!isCanceled) {
                    hideLoadingPopup();
                    showErrorDialog(getString(R.string.tap_error), getString(R.string.tap_error_message_general));
                }
            }
        });
    }

    private void showChatAsHistory(String message) {
        if (null != clChatHistory) {
            runOnUiThread(() -> clChatHistory.setVisibility(View.VISIBLE));
        }
        if (null != tvChatHistoryContent) {
            runOnUiThread(() -> tvChatHistoryContent.setText(message));
        }
        if (null != clChatComposer) {
            runOnUiThread(() -> {
                TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                rvCustomKeyboard.setVisibility(View.GONE);
                clChatComposer.setVisibility(View.INVISIBLE);
                etChat.clearFocus();
            });
        }

        if (null != vRoomImage) {
            vRoomImage.setClickable(false);
        }

        if (null != llButtonDeleteChat) {
            llButtonDeleteChat.setOnClickListener(llDeleteGroupClickListener);
        }
    }

    private void showDefaultChatEditText() {
        if (null != clChatHistory) {
            runOnUiThread(() -> clChatHistory.setVisibility(View.GONE));
        }

        if (null != clChatComposer) {
            clChatComposer.setVisibility(View.VISIBLE);
        }

        if (null != civRoomImage) {
            vRoomImage.setClickable(true);
        }
        hideKeyboards();
    }

    private void checkChatRoomLocked(TAPMessageModel message) {
        if (null != message && message.getRoom().isLocked()) {
            lockChatRoom();
        } else {
            clChatComposer.setVisibility(View.VISIBLE);
        }
    }

    private void lockChatRoom() {
        runOnUiThread(() -> {
            etChat.setText("");
            hideQuoteLayout();
            hideKeyboards();
            clChatComposer.setVisibility(View.GONE);
        });
    }

    private void handleSystemMessageAction(TAPMessageModel message) {
        if (message.getType() != TYPE_SYSTEM_MESSAGE) {
            return;
        }
        if (null != vm.getRoom() && TYPE_PERSONAL != vm.getRoom().getRoomType() &&
                (ROOM_ADD_PARTICIPANT.equals(message.getAction()) ||
                        ROOM_REMOVE_PARTICIPANT.equals(message.getAction())) &&
                !TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(message.getTarget().getTargetID())) {
            // Another member removed from group
            getRoomDataFromApi();
        } else if ((ROOM_REMOVE_PARTICIPANT.equals(message.getAction()) &&
                null != message.getTarget() &&
                vm.getMyUserModel().getUserID().equals(message.getTarget().getTargetID())) ||
                (LEAVE_ROOM.equals(message.getAction()) &&
                        vm.getMyUserModel().getUserID().equals(message.getUser().getUserID()))) {
            // Active user removed from group
            showChatAsHistory(getString(R.string.tap_not_a_participant));
        } else if (ROOM_ADD_PARTICIPANT.equals(message.getAction())) { // TODO: 27 Jan 2020 CHECK TARGET ID?
            // New group participant added
            showDefaultChatEditText();
        } else if (DELETE_ROOM.equals(message.getAction())) {
            // Room deleted
            //TAPChatManager.getInstance(instanceKey).deleteMessageFromIncomingMessages(message.getLocalID());
            //showRoomIsUnavailableState();
            showChatAsHistory(getString(R.string.tap_group_unavailable));
        } else {
            updateRoomDetailFromSystemMessage(message);
        }
    }

    private void updateRoomDetailFromSystemMessage(TAPMessageModel message) {
        if (message.getType() != TYPE_SYSTEM_MESSAGE) {
            return;
        }
        if (UPDATE_ROOM.equals(message.getAction())) {
            // Update room details
            vm.getRoom().setRoomName(message.getRoom().getRoomName());
            vm.getRoom().setRoomImage(message.getRoom().getRoomImage());
            TAPGroupManager.Companion.getInstance(instanceKey).addGroupData(vm.getRoom());
            runOnUiThread(() -> {
                tvRoomName.setText(vm.getRoom().getRoomName());
                if (null != vm.getRoom().getRoomImage()) {
                    civRoomImage.post(() -> loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomImage, tvRoomImageLabel));
                }
            });
        } else if (vm.getRoom().getRoomType() == TYPE_PERSONAL &&
                UPDATE_USER.equals(message.getAction()) &&
                message.getUser().getUserID().equals(vm.getOtherUserID())) {
            // Update user details
            vm.getRoom().setRoomName(message.getUser().getName());
            vm.getRoom().setRoomImage(message.getUser().getAvatarURL());
            vm.setOtherUserModel(message.getUser());
            runOnUiThread(() -> {
                tvRoomName.setText(vm.getOtherUserModel().getName());
                if (null != vm.getRoom().getRoomImage()) {
                    civRoomImage.post(() -> loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civRoomImage, tvRoomImageLabel));
                }
            });
        }
    }

    private void buildAndSendTextMessage() {
        String message = etChat.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            etChat.setText("");
            messageAdapter.shrinkExpandedBubble();
            TAPChatManager.getInstance(instanceKey).sendTextMessage(message);
            // Updated 2020/04/23
            //rvMessageList.scrollToPosition(0);
            rvMessageList.post(this::scrollToBottom);
        } else {
            TAPChatManager.getInstance(instanceKey).checkAndSendForwardedMessage(vm.getRoom());
            ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
            ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
        }
    }

    private void updateMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                // Remove empty chat layout if still shown
                if (clEmptyChat.getVisibility() == View.VISIBLE && (null == newMessage.getHidden() || !newMessage.getHidden())) {
                    clEmptyChat.setVisibility(View.GONE);
                    showMessageList();
                }
            });
            // Replace pending message with new message
            String newID = newMessage.getLocalID();
            updateMessageMentionIndexes(newMessage);
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());
            runOnUiThread(() -> {
                if (vm.getMessagePointer().containsKey(newID)) {
                    // Update message instead of adding when message pointer already contains the same local ID
                    int index = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID));
                    vm.updateMessagePointer(newMessage);
                    messageAdapter.notifyItemChanged(index);
                    if (TYPE_IMAGE == newMessage.getType() && ownMessage) {
                        TAPFileUploadManager.getInstance(instanceKey).removeUploadProgressMap(newMessage.getLocalID());
                    }
                } else {
                    // Check previous message date and add new message
                    TAPMessageModel previousMessage = messageAdapter.getItemAt(0);
                    String currentDate = TAPTimeFormatter.getInstance().formatDate(newMessage.getCreated());
                    if ((null == newMessage.getHidden() || !newMessage.getHidden()) &&
                            newMessage.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                            newMessage.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                            newMessage.getType() != TYPE_DATE_SEPARATOR &&
                            (null == previousMessage || !currentDate.equals(TAPTimeFormatter.getInstance()
                                    .formatDate(previousMessage.getCreated())))
                    ) {
                        // Generate date separator if first message or date is different
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, newMessage);
                        vm.getDateSeparators().put(dateSeparator.getLocalID(), dateSeparator);
                        vm.getDateSeparatorIndexes().put(dateSeparator.getLocalID(), 0);
                        runOnUiThread(() -> messageAdapter.addMessage(dateSeparator));
                    }

                    // Add new message
                    runOnUiThread(() -> messageAdapter.addMessage(newMessage));
                    vm.addMessagePointer(newMessage);
                    if (vm.isOnBottom() && !ownMessage) {
                        // Scroll recycler to bottom if recycler is already on bottom
                        vm.setScrollFromKeyboard(true);
                        scrollToBottom();
                    } else if (ownMessage) {
                        // Scroll recycler to bottom if own message
                        scrollToBottom();
                    } else {
                        // Message from other people is received when recycler is scrolled up
                        vm.addUnreadMessage(newMessage);
                        updateUnreadCount();
                        updateMentionCount();
                    }
                }
                updateMessageDecoration();
            });
            updateFirstVisibleMessageIndex();

            if (null != vm.getPendingAfterResponse() &&
                    !TAPChatManager.getInstance(instanceKey).hasPendingMessages()) {
                messageAfterView.onSuccess(vm.getPendingAfterResponse());
            }
        }
    }

    private void addNewMessage(final TAPMessageModel newMessage) {
        if (vm.getContainerAnimationState() == vm.ANIMATING) {
            // Hold message if layout is animating
            // Message is added after transition finishes in containerTransitionListener
            vm.addPendingRecyclerMessage(newMessage);
        } else {
            // Message is added after transition finishes in containerTransitionListener
            runOnUiThread(() -> {
                // Remove empty chat layout if still shown
                if (clEmptyChat.getVisibility() == View.VISIBLE && (null == newMessage.getHidden() || !newMessage.getHidden())) {
                    clEmptyChat.setVisibility(View.GONE);
                    showMessageList();
                }
            });
            updateMessageMentionIndexes(newMessage);
            boolean ownMessage = newMessage.getUser().getUserID().equals(TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID());

            TAPMessageModel previousMessage = messageAdapter.getItemAt(0);
            String currentDate = TAPTimeFormatter.getInstance().formatDate(newMessage.getCreated());
            if ((null == newMessage.getHidden() || !newMessage.getHidden()) &&
                    newMessage.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                    newMessage.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                    newMessage.getType() != TYPE_DATE_SEPARATOR &&
                    (null == previousMessage || !currentDate.equals(TAPTimeFormatter.getInstance()
                            .formatDate(previousMessage.getCreated())))
            ) {
                // Generate date separator if first message or date is different
                TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, newMessage);
                vm.getDateSeparators().put(dateSeparator.getLocalID(), dateSeparator);
                vm.getDateSeparatorIndexes().put(dateSeparator.getLocalID(), 0);
                runOnUiThread(() -> messageAdapter.addMessage(dateSeparator));
            }

            runOnUiThread(() -> messageAdapter.addMessage(newMessage));
            vm.addMessagePointer(newMessage);

            runOnUiThread(() -> {
                if (vm.isOnBottom() || ownMessage) {
                    // Scroll recycler to bottom if own message or recycler is already on bottom
                    ivToBottom.setVisibility(View.GONE);
                    rvMessageList.scrollToPosition(0);
                } else {
                    // Message from other people is received when recycler is scrolled up
                    vm.addUnreadMessage(newMessage);
                    updateUnreadCount();
                    updateMentionCount();
                }
                updateMessageDecoration();
            });
            updateFirstVisibleMessageIndex();
        }
    }

    private void updateMessageFromSocket(TAPMessageModel message) {
        runOnUiThread(() -> {
            int position = messageAdapter.getItems().indexOf(vm.getMessagePointer().get(message.getLocalID()));
            if (-1 != position) {
                // Update message in pointer and adapter
                vm.updateMessagePointer(message);
                TAPMessageModel existingMessage = messageAdapter.getItemAt(position);
                if (null != existingMessage) {
                    existingMessage.updateValue(message);
                    messageAdapter.notifyItemChanged(position);
                }
            }
//            else {
//                new Thread(() -> updateMessage(message)).start();
//            }
            if (0 == position) {
                updateFirstVisibleMessageIndex();
            }
        });
    }

    private List<TAPMessageModel> addBeforeTextMessage(final TAPMessageModel newMessage) {
        List<TAPMessageModel> tempBeforeMessages = new ArrayList<>();
        String newID = newMessage.getLocalID();

        if (vm.getMessagePointer().containsKey(newID)) {
            // Update existing message
            vm.updateMessagePointer(newMessage);
            runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID))));
        } else {
            // Add new message to pointer
            tempBeforeMessages.add(newMessage);
            vm.addMessagePointer(newMessage);
        }
        //updateMessageDecoration();
        return tempBeforeMessages;
    }

    private void updateFirstVisibleMessageIndex() {
        new Thread(() -> {
            vm.setFirstVisibleItemIndex(0);
            TAPMessageModel message = messageAdapter.getItemAt(vm.getFirstVisibleItemIndex());
            while (null != message && null != message.getHidden() && message.getHidden()) {
                vm.setFirstVisibleItemIndex(vm.getFirstVisibleItemIndex() + 1);
                message = messageAdapter.getItemAt(vm.getFirstVisibleItemIndex());
            }
        }).start();
    }

    private void scrollToMessage(String localID) {
        TAPMessageModel message = vm.getMessagePointer().get(localID);
        if (null != message) {
            vm.setTappedMessageLocalID(null);
            if ((null != message.getIsDeleted() && message.getIsDeleted()) ||
                    (null != message.getHidden() && message.getHidden())) {
                // Message does not exist
                runOnUiThread(() -> {
                    Toast.makeText(this, getResources().getString(R.string.tap_error_could_not_find_message), Toast.LENGTH_SHORT).show();
                    hideUnreadButtonLoading();
                });
            } else {
                // Scroll to message
                runOnUiThread(() -> {
                    messageLayoutManager.scrollToPositionWithOffset(messageAdapter.getItems().indexOf(message), TAPUtils.dpToPx(128));
                    rvMessageList.post(() -> {
                        if (messageLayoutManager.findFirstVisibleItemPosition() > 0) {
                            vm.setOnBottom(false);
                            ivToBottom.setVisibility(View.VISIBLE);
                            hideUnreadButton();
                            hideUnreadButtonLoading();
                        }
                        messageAdapter.highlightMessage(message);
                    });
                });
            }
        } else if (state != STATE.DONE) {
            // Find message in database/API
            vm.setTappedMessageLocalID(localID);
            showUnreadButtonLoading();
            loadMoreMessagesFromDatabase();
        } else {
            // Message not found
            runOnUiThread(() -> {
                Toast.makeText(this, getResources().getString(R.string.tap_error_could_not_find_message), Toast.LENGTH_SHORT).show();
                hideUnreadButtonLoading();
            });
        }
    }

    private TAPMessageModel insertUnreadMessageIdentifier(long created, TAPUserModel user) {
        TAPMessageModel unreadIndicator = new TAPMessageModel();
        unreadIndicator.setType(TYPE_UNREAD_MESSAGE_IDENTIFIER);
        unreadIndicator.setLocalID(UNREAD_INDICATOR_LOCAL_ID);
        unreadIndicator.setCreated(created - 1);
        unreadIndicator.setUser(user);

        vm.addMessagePointer(unreadIndicator);
        vm.setUnreadIndicator(unreadIndicator);

        return unreadIndicator;
    }

    private void getInitialUnreadCount() {
        // Get room's unread count
        vm.setInitialUnreadCount(vm.getRoom().getUnreadCount());
        if (0 == vm.getInitialUnreadCount()) {
            // Query unread count from database
            TAPDataManager.getInstance(instanceKey).getUnreadCountPerRoom(vm.getRoom().getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
                @Override
                public void onCountedUnreadCount(String roomID, int unreadCount, int mentionCount) {
                    if (!roomID.equals(vm.getRoom().getRoomID())) {
                        vm.setInitialUnreadCount(0);
                        hideUnreadButton();
                        return;
                    }
                    vm.setInitialUnreadCount(unreadCount);
                    if (vm.isUnreadButtonShown() && clUnreadButton.getVisibility() == View.GONE) {
                        vm.setUnreadButtonShown(false);
                        showUnreadButton(vm.getUnreadIndicator());
                    }
                }
            });
        }

        // Get unread mentions
        TAPDataManager.getInstance(instanceKey).getAllUnreadMentionsFromRoom(vm.getRoom().getRoomID(), new TAPDatabaseListener<TAPMessageEntity>() {
            @Override
            public void onSelectFinished(List<TAPMessageEntity> entities) {
                if (!entities.isEmpty()) {
                    for (TAPMessageEntity entity : entities) {
                        TAPMessageModel model = TAPChatManager.getInstance(instanceKey).convertToModel(entity);
                        vm.addUnreadMention(model);
                    }
                    updateMentionCount();
                }
            }
        });
    }

    private void showUnreadButton(@Nullable TAPMessageModel unreadIndicator) {
        if (0 >= vm.getInitialUnreadCount() || vm.isUnreadButtonShown()) {
            return;
        }
        vm.setUnreadButtonShown(true);
        rvMessageList.post(() -> runOnUiThread(() -> {
            if (vm.isAllUnreadMessagesHidden()) {
                // All unread messages are hidden
                return;
            }
            if (null != unreadIndicator) {
                View view = messageLayoutManager.findViewByPosition(messageAdapter.getItems().indexOf(unreadIndicator));
                if (null != view) {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    if (location[1] < TAPUtils.getScreenHeight()) {
                        // Do not show button if unread indicator is visible on screen
                        return;
                    }
                }
            }
            tvUnreadButtonCount.setText(String.format(getString(R.string.tap_format_s_unread_messages),
                    vm.getInitialUnreadCount() > 99 ? getString(R.string.tap_over_99) : vm.getInitialUnreadCount()));
            ivUnreadButtonImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_chevron_up_circle_orange));
            ivUnreadButtonImage.clearAnimation();
            clUnreadButton.setVisibility(View.VISIBLE);
            clUnreadButton.setOnClickListener(v -> scrollToMessage(UNREAD_INDICATOR_LOCAL_ID));
        }));
    }

    private void hideUnreadButton() {
        if (null != ivUnreadButtonImage.getAnimation()) {
            return;
        }
        runOnUiThread(() -> {
            clUnreadButton.setVisibility(View.GONE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void showUnreadButtonLoading() {
        runOnUiThread(() -> {
            tvUnreadButtonCount.setText(getString(R.string.tap_loading));
            ivUnreadButtonImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivUnreadButtonImage.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(this, ivUnreadButtonImage);
            }
            clUnreadButton.setVisibility(View.VISIBLE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void hideUnreadButtonLoading() {
        runOnUiThread(() -> {
            ivUnreadButtonImage.clearAnimation();
            clUnreadButton.setVisibility(View.GONE);
            clUnreadButton.setOnClickListener(null);
        });
    }

    private void showLoadingOlderMessagesIndicator() {
        hideLoadingOlderMessagesIndicator();
        rvMessageList.post(() -> runOnUiThread(() -> {
            vm.addMessagePointer(vm.getLoadingIndicator(true));
            messageAdapter.addItem(vm.getLoadingIndicator(false)); // Add loading indicator to last index
            messageAdapter.notifyItemInserted(messageAdapter.getItemCount() - 1);
        }));
    }

    private void hideLoadingOlderMessagesIndicator() {
        rvMessageList.post(() -> runOnUiThread(() -> {
            if (!messageAdapter.getItems().contains(vm.getLoadingIndicator(false))) {
                return;
            }
            int index = messageAdapter.getItems().indexOf(vm.getLoadingIndicator(false));
            vm.removeMessagePointer(LOADING_INDICATOR_LOCAL_ID);
            if (index >= 0) {
                messageAdapter.removeMessage(vm.getLoadingIndicator(false));
                if (null != messageAdapter.getItemAt(index)) {
                    messageAdapter.notifyItemChanged(index);
                } else {
                    messageAdapter.notifyItemRemoved(index);
                }
                updateMessageDecoration();
            }
        }));
    }

    private RecyclerView.OnScrollListener messageListScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            // Show/hide ivToBottom
            if (messageLayoutManager.findFirstVisibleItemPosition() <= vm.getFirstVisibleItemIndex()) {
                vm.setOnBottom(true);
                ivToBottom.setVisibility(View.GONE);
                tvBadgeUnread.setVisibility(View.GONE);
                vm.clearUnreadMessages();
            } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.getFirstVisibleItemIndex() && !vm.isScrollFromKeyboard()) {
                vm.setOnBottom(false);
                ivToBottom.setVisibility(View.VISIBLE);
                hideUnreadButton();
            } else if (messageLayoutManager.findFirstVisibleItemPosition() > vm.getFirstVisibleItemIndex()) {
                vm.setOnBottom(false);
                ivToBottom.setVisibility(View.VISIBLE);
                vm.setScrollFromKeyboard(false);
            }

            if (newState == SCROLL_STATE_IDLE) {
                // Start hide date indicator timer if state is idle
                hideDateIndicatorTimer.start();
            } else if (newState == SCROLL_STATE_DRAGGING) {
                // Show date indicator
                hideDateIndicatorTimer.cancel();
                tvDateIndicator.setText(TAPTimeFormatter.getInstance().dateStampString(TapUIChatActivity.this,
                        messageAdapter.getItemAt(messageLayoutManager.findLastVisibleItemPosition()).getCreated()));
                tvDateIndicator.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            // Update date indicator text
            if (tvDateIndicator.getVisibility() == View.VISIBLE) {
                tvDateIndicator.setText(TAPTimeFormatter.getInstance().dateStampString(TapUIChatActivity.this,
                        messageAdapter.getItemAt(messageLayoutManager.findLastVisibleItemPosition()).getCreated()));
                tvDateIndicator.setVisibility(View.VISIBLE);
            }
        }

        private CountDownTimer hideDateIndicatorTimer = new CountDownTimer(1000L, 100L) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                runOnUiThread(() -> tvDateIndicator.setVisibility(View.GONE));
            }
        };
    };

    private TextWatcher chatWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (null != TapUIChatActivity.this.getCurrentFocus() && TapUIChatActivity.this.getCurrentFocus().getId() == etChat.getId()
                    && s.length() > 0 && s.toString().trim().length() > 0) {
                // Hide chat menu and enable send button when EditText is filled
                ivChatMenu.setVisibility(View.GONE);
                ivButtonChatMenu.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_ripple));
                } else {
                    ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send));
                }
                ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
                checkAndSearchUserMentionList();
                //checkAndHighlightTypedText();
            } else if (null != TapUIChatActivity.this.getCurrentFocus() && TapUIChatActivity.this.getCurrentFocus().getId() == etChat.getId()
                    && s.length() > 0) {
                // Hide chat menu but keep send button disabled if trimmed text is empty
                ivChatMenu.setVisibility(View.GONE);
                ivButtonChatMenu.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_inactive_ripple));
                } else {
                    ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
                }
                ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
                hideUserMentionList();
                //} else if (s.length() > 0 && s.toString().trim().length() > 0) {
                //    if (vm.isCustomKeyboardEnabled()) {
                //        ivChatMenu.setVisibility(View.VISIBLE);
                //        ivButtonChatMenu.setVisibility(View.VISIBLE);
                //    }
                //    ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, );(R.drawable.tap_bg_chat_composer_send_ripple));
                //    ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
            } else {
                if (vm.isCustomKeyboardEnabled() && s.length() == 0) {
                    // Show chat menu if text is empty
                    ivChatMenu.setVisibility(View.VISIBLE);
                    ivButtonChatMenu.setVisibility(View.VISIBLE);
                } else {
                    ivChatMenu.setVisibility(View.GONE);
                    ivButtonChatMenu.setVisibility(View.GONE);
                }
                if (vm.getQuoteAction() == FORWARD) {
                    // Enable send button if message to forward exists
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_ripple));
                    } else {
                        ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send));
                    }
                    ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSend));
                } else {
                    // Disable send button
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        ivButtonSend.setImageDrawable(getDrawable(R.drawable.tap_bg_chat_composer_send_inactive_ripple));
                    } else {
                        ivButtonSend.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_send_inactive));
                    }
                    ivSend.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerSendInactive));
                }
                hideUserMentionList();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            sendTypingEmit(s.length() > 0);
        }
    };

    private void checkAndSearchUserMentionList() {
        if (vm.getRoomParticipantsByUsername().isEmpty()) {
            hideUserMentionList();
            return;
        }
        String s = etChat.getText().toString();
        if (!s.contains("@")) {
            // Return if text does not contain @
            hideUserMentionList();
            return;
        }
        int cursorIndex = etChat.getSelectionStart();
        int loopIndex = etChat.getSelectionStart();
        while (loopIndex > 0) {
            // Loop text from cursor index to the left
            loopIndex--;
            char c = s.charAt(loopIndex);
            if (c == ' ' || c == '\n') {
                // Found space before @, return
                hideUserMentionList();
                return;
            }
            if (c == '@') {
                // Found @, start searching user
                String keyword = s.substring(loopIndex + 1, cursorIndex).toLowerCase();
                if (keyword.isEmpty()) {
                    // Show all participants
                    List<TAPUserModel> searchResult = new ArrayList<>(vm.getRoomParticipantsByUsername().values());
                    searchResult.remove(vm.getMyUserModel());
                    showUserMentionList(searchResult, loopIndex, cursorIndex);
                } else {
                    // Search participants from keyword
                    int finalLoopIndex = loopIndex;
                    new Thread(() -> {
                        List<TAPUserModel> searchResult = new ArrayList<>();
                        for (Map.Entry<String, TAPUserModel> entry : vm.getRoomParticipantsByUsername().entrySet()) {
                            if (null != entry.getValue().getUsername() &&
                                    !entry.getValue().getUsername().equals(vm.getMyUserModel().getUsername()) &&
                                    (entry.getValue().getName().toLowerCase().contains(keyword) ||
                                            entry.getValue().getUsername().toLowerCase().contains(keyword))) {
                                // Add result if name/username matches and not self
                                searchResult.add(entry.getValue());
                            }
                        }
                        runOnUiThread(() -> showUserMentionList(searchResult, finalLoopIndex, cursorIndex));
                    }).start();
                }
                return;
            }
        }
        hideUserMentionList();
    }

    private void updateMessageMentionIndexes(TAPMessageModel message) {
        vm.getMessageMentionIndexes().remove(message.getLocalID());
        if (vm.getRoom().getRoomType() == TYPE_PERSONAL) {
            return;
        }
        String originalText;
        if (message.getType() == TYPE_TEXT) {
            originalText = message.getBody();
        } else if ((message.getType() == TYPE_IMAGE || message.getType() == TYPE_VIDEO) && null != message.getData()) {
            originalText = (String) message.getData().get(CAPTION);
        } else if (message.getType() == TYPE_LOCATION && null != message.getData()) {
            originalText = (String) message.getData().get(ADDRESS);
        } else {
            return;
        }
        if (null == originalText) {
            return;
        }
        List<Integer> mentionIndexes = new ArrayList<>();
        if (originalText.contains("@")) {
            int length = originalText.length();
            int startIndex = -1;
            for (int i = 0; i < length; i++) {
                if (originalText.charAt(i) == '@' && startIndex == -1) {
                    // Set index of @ (mention start index)
                    startIndex = i;
                } else {
                    boolean endOfMention = originalText.charAt(i) == ' ' ||
                            originalText.charAt(i) == '\n';
                    if (i == (length - 1) && startIndex != -1) {
                        // End of string (mention end index)
                        int endIndex = endOfMention ? i : (i + 1);
                        //String username = originalText.substring(startIndex + 1, endIndex);
                        //if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        if (endIndex > (startIndex + 1)) {
                            mentionIndexes.add(startIndex);
                            mentionIndexes.add(endIndex);
                        }
                        //}
                        startIndex = -1;
                    } else if (endOfMention && startIndex != -1) {
                        // End index for mentioned username
                        //String username = originalText.substring(startIndex + 1, i);
                        //if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        if (i > (startIndex + 1)) {
                            mentionIndexes.add(startIndex);
                            mentionIndexes.add(i);
                        }
                        //}
                        startIndex = -1;
                    }
                }
            }
            if (!mentionIndexes.isEmpty()) {
                vm.getMessageMentionIndexes().put(message.getLocalID(), mentionIndexes);
            }
        }
    }

    private void showUserMentionList(List<TAPUserModel> searchResult, int loopIndex, int cursorIndex) {
        if (!searchResult.isEmpty()) {
            // Show search result in list
            userMentionListAdapter = new TapUserMentionListAdapter(searchResult, user -> {
                // Append username to typed text
                if (etChat.getText().length() >= cursorIndex) {
                    etChat.getText().replace(loopIndex + 1, cursorIndex, user.getUsername() + " ");
                }
            });
            rvUserMentionList.setMaxHeight(TAPUtils.dpToPx(160));
            rvUserMentionList.setAdapter(userMentionListAdapter);
            if (null == rvUserMentionList.getLayoutManager()) {
                rvUserMentionList.setLayoutManager(new LinearLayoutManager(
                        TapUIChatActivity.this, LinearLayoutManager.VERTICAL, false) {
                    @Override
                    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
                        try {
                            super.onLayoutChildren(recycler, state);
                        } catch (IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            clUserMentionList.setVisibility(View.VISIBLE);
        } else {
            // Result is empty
            hideUserMentionList();
        }
    }

    private void hideUserMentionList() {
        boolean hasFocus = etChat.hasFocus();
        clUserMentionList.setVisibility(View.GONE);
        if (hasFocus) {
            clUserMentionList.post(() -> {
                rvUserMentionList.setAdapter(null);
                rvUserMentionList.post(() -> etChat.requestFocus());
            });
        }
    }

    private void checkAndHighlightTypedText() {
        if (vm.getRoomParticipantsByUsername().isEmpty()) {
            hideUserMentionList();
            return;
        }
        String s = etChat.getText().toString();
        // Check for mentions
        if (vm.getRoom().getRoomType() == TYPE_PERSONAL || !s.contains("@")) {
            return;
        }
        SpannableString span = new SpannableString(s);
        int cursorIndex = etChat.getSelectionStart();
        int mentionStartIndex = -1;
        int length = s.length();
        boolean isSpanSet = false;
        for (int i = 0; i < length; i++) {
            if (mentionStartIndex == -1 && s.charAt(i) == '@') {
                // Set index of @
                mentionStartIndex = i;
            } else {
                boolean endOfMention = s.charAt(i) == ' ' || s.charAt(i) == '\n';
                if (mentionStartIndex != -1 && i == (length - 1)) {
                    // End of string
                    int mentionEndIndex = endOfMention ? i : (i + 1);
                    String username = s.substring(mentionStartIndex + 1, mentionEndIndex);
                    if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        span.setSpan(new ForegroundColorSpan(
                                        ContextCompat.getColor(TapTalk.appContext,
                                                R.color.tapLeftBubbleMessageBodyURLColor)),
                                mentionStartIndex, mentionEndIndex, 0);
                        isSpanSet = true;
                    }
                    mentionStartIndex = -1;
                } else if (mentionStartIndex != -1 && endOfMention) {
                    // End index for mentioned username
                    String username = s.substring(mentionStartIndex + 1, i);
                    if (vm.getRoomParticipantsByUsername().containsKey(username)) {
                        span.setSpan(new ForegroundColorSpan(
                                        ContextCompat.getColor(TapTalk.appContext,
                                                R.color.tapLeftBubbleMessageBodyURLColor)),
                                mentionStartIndex, i, 0);
                        isSpanSet = true;
                    }
                    mentionStartIndex = -1;
                }
            }
        }
        if (isSpanSet) {
            etChat.removeTextChangedListener(chatWatcher);
            etChat.setText(span);
            etChat.setSelection(cursorIndex); // FIXME: 24 Apr 2020 TAP AND HOLD BACKSPACE NOT WORKING IF TEXT CONTAINS @
            etChat.addTextChangedListener(chatWatcher);
        }
    }

    private View.OnFocusChangeListener chatFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus && vm.isCustomKeyboardEnabled()) {
                vm.setScrollFromKeyboard(true);
                rvCustomKeyboard.setVisibility(View.GONE);
                ivButtonChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_bg_chat_composer_burger_menu_ripple));
                ivChatMenu.setImageDrawable(ContextCompat.getDrawable(TapUIChatActivity.this, R.drawable.tap_ic_burger_white));
                ivChatMenu.setColorFilter(ContextCompat.getColor(TapTalk.appContext, R.color.tapIconChatComposerBurgerMenu));
                TAPUtils.showKeyboard(TapUIChatActivity.this, etChat);

                if (0 < etChat.getText().toString().length()) {
                    ivChatMenu.setVisibility(View.GONE);
                    ivButtonChatMenu.setVisibility(View.GONE);
                }
            } else if (hasFocus) {
                vm.setScrollFromKeyboard(true);
                TAPUtils.showKeyboard(TapUIChatActivity.this, etChat);
            }
//            else {
//                etChat.requestFocus();
//            }
        }
    };

    private LayoutTransition.TransitionListener containerTransitionListener = new LayoutTransition.TransitionListener() {
        @Override
        public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            // Change animation state
            if (vm.getContainerAnimationState() != vm.PROCESSING) {
                vm.setContainerAnimationState(vm.ANIMATING);
            }
        }

        @Override
        public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
            if (vm.getContainerAnimationState() == vm.ANIMATING) {
                processPendingMessages();
            }
        }

        private void processPendingMessages() {
            vm.setContainerAnimationState(vm.PROCESSING);
            if (vm.getPendingRecyclerMessages().size() > 0) {
                // Copy list to prevent concurrent exception
                List<TAPMessageModel> pendingMessages = new ArrayList<>(vm.getPendingRecyclerMessages());
                for (TAPMessageModel pendingMessage : pendingMessages) {
                    // Loop the copied list to add messages
                    if (vm.getContainerAnimationState() != vm.PROCESSING) {
                        return;
                    }
                    updateMessage(pendingMessage);
                }
                // Remove added messages from pending message list
                vm.getPendingRecyclerMessages().removeAll(pendingMessages);
                if (vm.getPendingRecyclerMessages().size() > 0) {
                    // Redo process if pending message is not empty
                    processPendingMessages();
                    return;
                }
            }
            // Change state to idle when processing finished
            vm.setContainerAnimationState(vm.IDLE);
        }
    };

    private void mergeSort(List<TAPMessageModel> messages, int sortDirection) {
        int messageListSize = messages.size();

        if (messageListSize < 2) {
            return;
        }

        int leftListSize = messageListSize / 2;
        int rightListSize = messageListSize - leftListSize;
        List<TAPMessageModel> leftList = new ArrayList<>(leftListSize);
        List<TAPMessageModel> rightList = new ArrayList<>(rightListSize);

        for (int index = 0; index < leftListSize; index++)
            leftList.add(index, messages.get(index));

        for (int index = leftListSize; index < messageListSize; index++)
            rightList.add((index - leftListSize), messages.get(index));

        mergeSort(leftList, sortDirection);
        mergeSort(rightList, sortDirection);

        merge(messages, leftList, rightList, leftListSize, rightListSize, sortDirection);
    }

    private void merge(List<TAPMessageModel> messagesAll, List<TAPMessageModel> leftList, List<TAPMessageModel> rightList, int leftSize, int rightSize, int sortDirection) {
        int indexLeft = 0, indexRight = 0, indexCombine = 0;

        while (indexLeft < leftSize && indexRight < rightSize) {
            if (DESCENDING == sortDirection && leftList.get(indexLeft).getCreated() < rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, leftList.get(indexLeft));
                indexLeft += 1;
                indexCombine += 1;
            } else if (DESCENDING == sortDirection && leftList.get(indexLeft).getCreated() >= rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, rightList.get(indexRight));
                indexRight += 1;
                indexCombine += 1;
            } else if (ASCENDING == sortDirection && leftList.get(indexLeft).getCreated() > rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, leftList.get(indexLeft));
                indexLeft += 1;
                indexCombine += 1;
            } else if (ASCENDING == sortDirection && leftList.get(indexLeft).getCreated() <= rightList.get(indexRight).getCreated()) {
                messagesAll.set(indexCombine, rightList.get(indexRight));
                indexRight += 1;
                indexCombine += 1;
            }
        }

        while (indexLeft < leftSize) {
            messagesAll.set(indexCombine, leftList.get(indexLeft));
            indexLeft += 1;
            indexCombine += 1;
        }

        while (indexRight < rightSize) {
            messagesAll.set(indexCombine, rightList.get(indexRight));
            indexRight += 1;
            indexCombine += 1;
        }
    }

    private void showLoadingPopup() {
        runOnUiThread(() -> {
            ivLoadingPopup.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tap_ic_loading_progress_circle_white));
            if (null == ivLoadingPopup.getAnimation()) {
                TAPUtils.rotateAnimateInfinitely(this, ivLoadingPopup);
            }
            tvLoadingText.setVisibility(View.INVISIBLE);
            flLoading.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> {
                if (flLoading.getVisibility() == View.VISIBLE) {
                    tvLoadingText.setText(getString(R.string.tap_cancel));
                    tvLoadingText.setVisibility(View.VISIBLE);
                }
            }, 1000L);
        });
    }

    private void hideLoadingPopup() {
        runOnUiThread(() -> flLoading.setVisibility(View.GONE));
    }

    private void showErrorDialog(String title, String message) {
        runOnUiThread(() -> new TapTalkDialog.Builder(this)
                .setDialogType(TapTalkDialog.DialogType.ERROR_DIALOG)
                .setTitle(title)
                .setCancelable(true)
                .setMessage(message)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .show());
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (null == action) {
                return;
            }
            String localID;
            Uri fileUri;
            switch (action) {
                case UploadProgressLoading:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case UploadProgressFinish:
                    localID = intent.getStringExtra(UploadLocalID);
                    TAPMessageModel messageModel = vm.getMessagePointer().get(localID);
                    if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(UploadImageData) &&
                            intent.getSerializableExtra(UploadImageData) instanceof HashMap) {
                        // Set image data
                        messageModel.setData((HashMap<String, Object>) intent.getSerializableExtra(UploadImageData));
                    } else if (vm.getMessagePointer().containsKey(localID) && intent.hasExtra(UploadFileData) &&
                            intent.getSerializableExtra(UploadFileData) instanceof HashMap) {
                        // Put file data
                        messageModel.putData((HashMap<String, Object>) intent.getSerializableExtra(UploadFileData));
                    }
                    messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(messageModel));
                    break;
                case UploadFailed:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        TAPMessageModel failedMessageModel = vm.getMessagePointer().get(localID);
                        failedMessageModel.setFailedSend(true);
                        failedMessageModel.setSending(false);
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(failedMessageModel));
                    }
                    break;
                case UploadCancelled:
                    localID = intent.getStringExtra(UploadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        TAPMessageModel cancelledMessageModel = vm.getMessagePointer().get(localID);
                        vm.delete(localID);
                        int itemPos = messageAdapter.getItems().indexOf(cancelledMessageModel);

                        TAPFileUploadManager.getInstance(instanceKey).cancelUpload(TapUIChatActivity.this, cancelledMessageModel,
                                vm.getRoom().getRoomID());

                        vm.removeFromUploadingList(localID);
                        vm.removeMessagePointer(localID);
                        messageAdapter.removeMessageAt(itemPos);
                    }
                    break;
                case DownloadProgressLoading:
                case DownloadFinish:
                    localID = intent.getStringExtra(DownloadLocalID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case DownloadFailed:
                    localID = intent.getStringExtra(DownloadLocalID);
                    TAPFileDownloadManager.getInstance(instanceKey).addFailedDownload(localID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case DownloadFile:
                    startFileDownload(intent.getParcelableExtra(MESSAGE));
                    break;
                case CancelDownload:
                    localID = intent.getStringExtra(DownloadLocalID);
                    TAPFileDownloadManager.getInstance(instanceKey).cancelFileDownload(localID);
                    if (vm.getMessagePointer().containsKey(localID)) {
                        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID)));
                    }
                    break;
                case OpenFile:
                    TAPMessageModel message = intent.getParcelableExtra(MESSAGE);
                    fileUri = intent.getParcelableExtra(FILE_URI);
                    vm.setOpenedFileMessage(message);
                    if (null != fileUri && null != message.getData() && null != message.getData().get(MEDIA_TYPE)) {
                        if (!TAPUtils.openFile(instanceKey, TapUIChatActivity.this, fileUri, (String) message.getData().get(MEDIA_TYPE))) {
                            showDownloadFileDialog();
                        }
                    } else {
                        showDownloadFileDialog();
                    }
                    break;
                case LongPressChatBubble:
                    if (null != intent.getParcelableExtra(MESSAGE) && intent.getParcelableExtra(MESSAGE) instanceof TAPMessageModel) {
                        TAPLongPressActionBottomSheet chatBubbleBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(CHAT_BUBBLE_TYPE, intent.getParcelableExtra(MESSAGE), attachmentListener);
                        chatBubbleBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressLink:
                    if (null != intent.getStringExtra(URL_MESSAGE) && null != intent.getStringExtra(COPY_MESSAGE)) {
                        TAPLongPressActionBottomSheet linkBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(LINK_TYPE, intent.getStringExtra(COPY_MESSAGE), intent.getStringExtra(URL_MESSAGE), attachmentListener);
                        linkBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressEmail:
                    if (null != intent.getStringExtra(URL_MESSAGE) && null != intent.getStringExtra(COPY_MESSAGE)) {
                        TAPLongPressActionBottomSheet emailBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(EMAIL_TYPE, intent.getStringExtra(COPY_MESSAGE), intent.getStringExtra(URL_MESSAGE), attachmentListener);
                        emailBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressPhone:
                    if (null != intent.getStringExtra(URL_MESSAGE) && null != intent.getStringExtra(COPY_MESSAGE)) {
                        TAPLongPressActionBottomSheet phoneBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(PHONE_TYPE, intent.getStringExtra(COPY_MESSAGE), intent.getStringExtra(URL_MESSAGE), attachmentListener);
                        phoneBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
                case LongPressMention:
                    if (null != intent.getStringExtra(URL_MESSAGE) && null != intent.getStringExtra(COPY_MESSAGE)) {
                        TAPLongPressActionBottomSheet mentionBottomSheet = TAPLongPressActionBottomSheet.Companion.newInstance(MENTION_TYPE, intent.getStringExtra(COPY_MESSAGE), intent.getStringExtra(URL_MESSAGE), attachmentListener);
                        mentionBottomSheet.show(getSupportFragmentManager(), "");
                        TAPUtils.dismissKeyboard(TapUIChatActivity.this);
                    }
                    break;
            }
        }
    };

    /**
     * =========================================================================================== *
     * LOAD MESSAGES
     * =========================================================================================== *
     */

    private void getAllUnreadMessage() {
        TAPDataManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(TAPChatManager.getInstance(instanceKey).getOpenRoom(),
                new TAPDatabaseListener<TAPMessageEntity>() {
                    @Override
                    public void onSelectFinished(List<TAPMessageEntity> entities) {
                        if (0 < entities.size()) {
                            vm.setLastUnreadMessageLocalID(entities.get(0).getLocalID());
//                            new Thread(() -> {
                            boolean allUnreadHidden = true; // Flag to check hidden unread when looping
                            for (TAPMessageEntity entity : entities) {
                                if (null == entity.getHidden() || !entity.getHidden()) {
                                    allUnreadHidden = false;
                                    break;
                                }
                            }
                            if (allUnreadHidden) {
                                vm.setAllUnreadMessagesHidden(true);
                            }
//                            }).start();
                        }
                        vm.getMessageEntities(vm.getRoom().getRoomID(), dbListener);
                    }
                });
    }

    private TAPDatabaseListener<TAPMessageEntity> dbListener = new TAPDatabaseListener<TAPMessageEntity>() {

        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            final List<TAPMessageModel> models = new ArrayList<>();
            boolean allMessagesHidden = true;
            String previousDate = "";
            TAPMessageModel previousMessage = null;
            LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();
            for (TAPMessageEntity entity : entities) {
                TAPMessageModel model = TAPChatManager.getInstance(instanceKey).convertToModel(entity);
                models.add(model);
                vm.addMessagePointer(model);

                if (allMessagesHidden && (null == model.getHidden() || !model.getHidden())) {
                    allMessagesHidden = false;
                }

                updateMessageMentionIndexes(model);

                if ((null == model.getIsRead() || !model.getIsRead()) &&
                        TAPUtils.isActiveUserMentioned(model, vm.getMyUserModel())) {
                    // Add unread mention
                    vm.addUnreadMention(model);
                }

                if ((null == entity.getHidden() || !entity.getHidden()) &&
                        entity.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                        entity.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                        entity.getType() != TYPE_DATE_SEPARATOR
                ) {
                    String currentDate = TAPTimeFormatter.getInstance().formatDate(model.getCreated());
                    if (null != previousMessage && !currentDate.equals(previousDate)) {
                        // Generate date separator if date is different
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                        dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                        dateSeparatorIndex.put(dateSeparator.getLocalID(), models.indexOf(previousMessage) + 1);
                    }
                    previousDate = currentDate;
                    previousMessage = model;
                }
            }

            if (0 < models.size()) {
                vm.setLastTimestamp(models.get(models.size() - 1).getCreated());
//                vm.setLastTimestamp(models.get(0).getCreated());
            }

            insertDateSeparators(dateSeparators, dateSeparatorIndex, models);

            TAPMessageModel lastUnreadMessage = vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID());
            if (null != lastUnreadMessage && !vm.isAllUnreadMessagesHidden()) {
                TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(
                        lastUnreadMessage.getCreated(),
                        lastUnreadMessage.getUser());
                models.add(models.indexOf(lastUnreadMessage) + 1, unreadIndicator);
            }

            boolean finalAllMessagesHidden = allMessagesHidden;
            if (null != messageAdapter && 0 == messageAdapter.getItems().size()) {
                runOnUiThread(() -> {
                    // First load
                    messageAdapter.setMessages(models);
                    if (models.size() == 0 || finalAllMessagesHidden) {
                        // Chat is empty
                        // TODO: 24 September 2018 CHECK ROOM TYPE
                        clEmptyChat.setVisibility(View.VISIBLE);

                        // Load my avatar
                        if (null != vm.getMyUserModel().getAvatarURL() && !vm.getMyUserModel().getAvatarURL().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getMyUserModel().getAvatarURL().getThumbnail(), civMyAvatarEmpty, tvMyAvatarLabelEmpty);
                        } else {
                            loadInitialsToProfilePicture(civMyAvatarEmpty, tvMyAvatarLabelEmpty);
                        }

                        // Load room avatar
                        if (null != vm.getRoom() &&
                                TYPE_PERSONAL == vm.getRoom().getRoomType() &&
                                null != vm.getOtherUserModel() &&
                                null != vm.getOtherUserModel().getAvatarURL().getThumbnail() &&
                                !vm.getOtherUserModel().getAvatarURL().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getOtherUserModel().getAvatarURL().getThumbnail(), civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                        } else if (null != vm.getRoom() &&
                                null != vm.getRoom().getRoomImage() &&
                                !vm.getRoom().getRoomImage().getThumbnail().isEmpty()) {
                            loadProfilePicture(vm.getRoom().getRoomImage().getThumbnail(), civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                        } else {
                            loadInitialsToProfilePicture(civRoomAvatarEmpty, tvRoomAvatarLabelEmpty);
                        }
                        if (vm.isCustomKeyboardEnabled() && 0 == etChat.getText().toString().trim().length()) {
                            showCustomKeyboard();
                        }
                    } else {
                        // Message exists
                        vm.setMessageModels(models);
                        state = STATE.LOADED;
                        if (clEmptyChat.getVisibility() == View.VISIBLE && !finalAllMessagesHidden) {
                            clEmptyChat.setVisibility(View.GONE);
                        }
                        showMessageList();
                        showUnreadButton(vm.getUnreadIndicator());
                        updateMentionCount();
                        checkChatRoomLocked(models.get(0));
                    }
                    rvMessageList.scrollToPosition(0);
                    updateMessageDecoration();

                    if (0 < vm.getMessageModels().size() && MAX_ITEMS_PER_PAGE > vm.getMessageModels().size()) {
                        // Only fetch newer messages from API if message is below 50
                        callApiAfter();
                        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapUIChatActivity.this)) {
                            insertDateSeparatorToLastIndex();
                        }
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    } else if (MAX_ITEMS_PER_PAGE <= vm.getMessageModels().size()) {
                        // Fetch newer messages from API and add pagination listener if message is over 50
                        rvMessageList.addOnScrollListener(endlessScrollListener);
                        callApiAfter();
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    } else {
                        // Fetch older messages from API if room has no message
                        fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                    }
                });
            } else if (null != messageAdapter) {
                runOnUiThread(() -> {
                    if (clEmptyChat.getVisibility() == View.VISIBLE && !finalAllMessagesHidden) {
                        clEmptyChat.setVisibility(View.GONE);
                    }
                    showMessageList();
                    messageAdapter.setMessages(models);
                    new Thread(() -> {
                        vm.setMessageModels(messageAdapter.getItems());
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    }).start();
                    if (rvMessageList.getVisibility() != View.VISIBLE) {
                        rvMessageList.setVisibility(View.VISIBLE);
                    }
                    if (state == STATE.DONE) {
                        insertDateSeparatorToLastIndex();
                        updateMessageDecoration();
                    }
                    if (!models.isEmpty()) {
                        checkChatRoomLocked(models.get(0));
                    }
                });
                if (MAX_ITEMS_PER_PAGE > entities.size() && 1 < entities.size()) {
                    state = STATE.DONE;
                } else {
                    rvMessageList.addOnScrollListener(endlessScrollListener);
                    state = STATE.LOADED;
                }
            }
        }
    };

    private void loadMoreMessagesFromDatabase() {
        if (state == STATE.LOADED && 0 < messageAdapter.getItems().size()) {
            new Thread(() -> {
                vm.getMessageByTimestamp(vm.getRoom().getRoomID(), dbListenerPaging, vm.getLastTimestamp());
                state = STATE.WORKING;
            }).start();
        }
    }

    private TAPDatabaseListener<TAPMessageEntity> dbListenerPaging = new TAPDatabaseListener<TAPMessageEntity>() {
        @Override
        public void onSelectFinished(List<TAPMessageEntity> entities) {
            final List<TAPMessageModel> models = new ArrayList<>();
            String previousDate = "";
            TAPMessageModel previousMessage = null;
            if (null != messageAdapter && !messageAdapter.getItems().isEmpty()) {
                int offset = 1;
                while (null == previousMessage) {
                    if (messageAdapter.getItems().size() < offset) {
                        break;
                    }
                    previousMessage = messageAdapter.getItemAt(messageAdapter.getItems().size() - offset);
                    if ((null != previousMessage.getHidden() && previousMessage.getHidden()) ||
                            previousMessage.getType() == TYPE_UNREAD_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_LOADING_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_DATE_SEPARATOR
                    ) {
                        previousMessage = null;
                        offset++;
                    }
                }
                if (null != previousMessage) {
                    previousDate = TAPTimeFormatter.getInstance().formatDate(previousMessage.getCreated());
                }
            }
            LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();

            for (TAPMessageEntity entity : entities) {
                if (!vm.getMessagePointer().containsKey(entity.getLocalID())) {
                    TAPMessageModel model = TAPChatManager.getInstance(instanceKey).convertToModel(entity);
                    models.add(model);
                    vm.addMessagePointer(model);
                    updateMessageMentionIndexes(model);
                    if ((null == model.getIsRead() || !model.getIsRead()) &&
                            TAPUtils.isActiveUserMentioned(model, vm.getMyUserModel())) {
                        // Add unread mention
                        vm.addUnreadMention(model);
                    }

                    if ((null == entity.getHidden() || !entity.getHidden()) &&
                            entity.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                            entity.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                            entity.getType() != TYPE_DATE_SEPARATOR
                    ) {
                        String currentDate = TAPTimeFormatter.getInstance().formatDate(model.getCreated());
                        if (null != previousMessage && !currentDate.equals(previousDate)) {
                            // Generate date separator if date is different
                            int index = models.contains(previousMessage) ?
                                    models.indexOf(previousMessage) + 1 : 0;
                            TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                            dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                            dateSeparatorIndex.put(dateSeparator.getLocalID(), index);
                        }
                        previousDate = currentDate;
                        previousMessage = model;
                    }
                }
            }

            if (0 < models.size()) {
                vm.setLastTimestamp(models.get(models.size() - 1).getCreated());
            }

            if (null != messageAdapter) {
                insertDateSeparators(dateSeparators, dateSeparatorIndex, models);

                if (MAX_ITEMS_PER_PAGE > entities.size() && STATE.DONE != state) {
                    if (0 == entities.size()) {
                        showLoadingOlderMessagesIndicator();
                    } else {
                        vm.setNeedToShowLoading(true);
                    }
                    fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeViewPaging);
                } else if (STATE.WORKING == state) {
                    state = STATE.LOADED;
                }

                runOnUiThread(() -> {
                    //flMessageList.setVisibility(View.VISIBLE);
                    messageAdapter.addMessage(models);

                    if (vm.isNeedToShowLoading()) {
                        // Show loading if Before API is called
                        vm.setNeedToShowLoading(false);
                        showLoadingOlderMessagesIndicator();
                    }

                    // Insert unread indicator
                    TAPMessageModel lastUnreadMessage = vm.getMessagePointer().get(vm.getLastUnreadMessageLocalID());
                    if (null != lastUnreadMessage && null == vm.getUnreadIndicator() && !vm.isAllUnreadMessagesHidden()) {
                        TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(
                                lastUnreadMessage.getCreated(),
                                lastUnreadMessage.getUser());
                        messageAdapter.getItems().add(messageAdapter.getItems().indexOf(lastUnreadMessage) + 1, unreadIndicator);
                    }

                    new Thread(() -> {
                        vm.setMessageModels(messageAdapter.getItems());
                        showUnreadButton(vm.getUnreadIndicator());
                        updateMentionCount();
                        if (null != vm.getTappedMessageLocalID()) {
                            scrollToMessage(vm.getTappedMessageLocalID());
                        }
                    }).start();

                    if (rvMessageList.getVisibility() != View.VISIBLE) {
                        rvMessageList.setVisibility(View.VISIBLE);
                    }
                    if (state == STATE.DONE) {
                        updateMessageDecoration();
                    }
                });
            }
        }
    };

    private void callApiAfter() {
        if (!TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(this)) {
            return;
        }
        if (!vm.isInitialAPICallFinished()) {
            showLoadingOlderMessagesIndicator();
        }
        new Thread(() -> {
            if (vm.getMessageModels().size() > 0 && !TAPDataManager.getInstance(instanceKey).checkKeyInLastMessageTimestamp(vm.getRoom().getRoomID())) {
                // Set oldest message's create time as minCreated and lastUpdated if last updated timestamp does not exist in preference
                TAPDataManager.getInstance(instanceKey).getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        messageAfterView);
            } else if (vm.getMessageModels().size() > 0) {
                // Set oldest message's create time as minCreated, last updated timestamp is obtained from preference
                TAPDataManager.getInstance(instanceKey).getMessageListByRoomAfter(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(),
                        TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()),
                        messageAfterView);
            }
        }).start();
    }

    private TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageAfterView = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            if (TAPChatManager.getInstance(instanceKey).hasPendingMessages()) {
                vm.setPendingAfterResponse(response);
                return;
            }
            vm.setPendingAfterResponse(null);
            List<TAPMessageEntity> responseMessages = new ArrayList<>(); // Entities to be saved to database
            List<TAPMessageModel> messageAfterModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            List<String> unreadMessageIds = new ArrayList<>(); // Results to be marked as read
            LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();
            TAPMessageModel updateRoomDetailSystemMessage = null;

            int unreadMessageIndex = -1; // Index for unread message identifier
            long smallestUnreadCreated = 0L;

            vm.setAllUnreadMessagesHidden(false); // Set initial value for unread identifier/button flag
            int allUnreadHidden = 0; // Flag to check hidden unread when looping
            boolean allMessagesHidden = true; // Flag to check whether empty chat layout should be removed

            messageAdapter.getItems().removeAll(vm.getDateSeparators().values());
            vm.getDateSeparators().clear();

            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    String newID = message.getLocalID();
                    if (vm.getMessagePointer().containsKey(newID)) {
                        // Update existing message
                        vm.updateMessagePointer(message);
                        runOnUiThread(() -> {
                            messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(newID)));
                        });
                    } else if (!vm.getMessagePointer().containsKey(newID)) {
                        // Insert new message to list and HashMap
                        messageAfterModels.add(message);
                        TAPMessageModel messageFromPointer = vm.getMessagePointer().get(message.getLocalID());
                        if (null != messageFromPointer) {
                            messageFromPointer = messageFromPointer.copyMessageModel();
                        }
                        vm.addMessagePointer(message);
                        if ((null == message.getIsRead() || !message.getIsRead()) &&
                                (null == messageFromPointer || null == messageFromPointer.getIsRead() || !messageFromPointer.getIsRead()) &&
                                // Updated 2020/02/10
                                (null == message.getHidden() || !message.getHidden()) &&
                                (null == messageFromPointer || null == messageFromPointer.getHidden() || !messageFromPointer.getHidden()) &&
                                //(null == message.getIsDeleted() || !message.getIsDeleted()) &&
                                //(null == messageFromPointer || null == messageFromPointer.getIsDeleted() || !messageFromPointer.getIsDeleted()) &&
                                !TAPMessageStatusManager.getInstance(instanceKey).getReadMessageQueue().contains(message.getMessageID()) &&
                                !TAPMessageStatusManager.getInstance(instanceKey).getMessagesMarkedAsRead().contains(message.getMessageID())) {
                            // Add message ID to pending list if new message has not been read or not in mark read queue
                            unreadMessageIds.add(message.getMessageID());
                        }

                        if ("".equals(vm.getLastUnreadMessageLocalID())
                                && (smallestUnreadCreated > message.getCreated() || 0L == smallestUnreadCreated)
                                && (null != message.getIsRead() && !message.getIsRead())
                                && null == vm.getUnreadIndicator()) {
                            // Update first unread message index
                            unreadMessageIndex = messageAfterModels.indexOf(message);
                            smallestUnreadCreated = message.getCreated();
                        }

                        if (allMessagesHidden && (null == message.getHidden() || !message.getHidden())) {
                            allMessagesHidden = false;
                        }

                        updateMessageMentionIndexes(message);

                        if ((null == message.getIsRead() || !message.getIsRead()) &&
                                TAPUtils.isActiveUserMentioned(message, vm.getMyUserModel())) {
                            // Add unread mention
                            vm.addUnreadMention(message);
                        }

                        if (message.getType() == TYPE_SYSTEM_MESSAGE &&
                                null != message.getAction() &&
                                (message.getAction().equals(UPDATE_ROOM) ||
                                        message.getAction().equals(UPDATE_USER)) &&
                                (null == updateRoomDetailSystemMessage ||
                                        updateRoomDetailSystemMessage.getCreated() < message.getCreated())) {
                            // Store update room system message
                            updateRoomDetailSystemMessage = message;
                        }
                    }

                    if (null == message.getIsRead() || !message.getIsRead()) {
                        if (allUnreadHidden != -1 && null != message.getHidden() && message.getHidden()) {
                            allUnreadHidden = 1;
                        } else {
                            // Set allUnreadHidden to false
                            allUnreadHidden = -1;
                        }
                    }

                    responseMessages.add(TAPChatManager.getInstance(instanceKey).convertToEntity(message));
                    new Thread(() -> {
                        // Update last updated timestamp in preference (new thread to prevent stutter when scrolling)
                        if (null != message.getUpdated() &&
                                TAPDataManager.getInstance(instanceKey).getLastUpdatedMessageTimestamp(vm.getRoom().getRoomID()) < message.getUpdated()) {
                            TAPDataManager.getInstance(instanceKey).saveLastUpdatedMessageTimestamp(vm.getRoom().getRoomID(), message.getUpdated());
                        }
                    }).start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (null != updateRoomDetailSystemMessage) {
                // Update room detail if update room system message exists in API result
                updateRoomDetailFromSystemMessage(updateRoomDetailSystemMessage);
            }

            if (!vm.isInitialAPICallFinished()) {
                // Add unread messages to count
                vm.setInitialUnreadCount(vm.getInitialUnreadCount() + unreadMessageIds.size());
            }

            if (allUnreadHidden == 1) {
                // All unread messages are hidden
                vm.setAllUnreadMessagesHidden(true);
            }
            if (-1 != unreadMessageIndex && 0L != smallestUnreadCreated && !vm.isAllUnreadMessagesHidden()) {
                // Insert unread indicator
                TAPMessageModel unreadIndicator = insertUnreadMessageIdentifier(
                        messageAfterModels.get(unreadMessageIndex).getCreated(),
                        messageAfterModels.get(unreadMessageIndex).getUser());
                messageAfterModels.add(unreadMessageIndex, unreadIndicator);
            }

            if (0 < messageAfterModels.size()) {
                // Update new message status to delivered
                TAPMessageStatusManager.getInstance(instanceKey).updateMessageStatusToDelivered(messageAfterModels);
            }

            // Insert new messages to first index
            messageAdapter.addMessage(0, messageAfterModels, false);
            // Sort adapter items according to timestamp
            mergeSort(messageAdapter.getItems(), ASCENDING);

            String previousDate = "";
            TAPMessageModel previousMessage = null;
            for (TAPMessageModel message : messageAdapter.getItems()) {
                if ((null == message.getHidden() || !message.getHidden()) &&
                        message.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_DATE_SEPARATOR
                ) {
                    String currentDate = TAPTimeFormatter.getInstance().formatDate(message.getCreated());
                    if (null != previousMessage && !currentDate.equals(previousDate)) {
                        // Generate date separator if date is different
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                        dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                        dateSeparatorIndex.put(dateSeparator.getLocalID(), messageAdapter.getItems().indexOf(previousMessage) + 1);
                    }
                    previousDate = currentDate;
                    previousMessage = message;
                }
            }
            insertDateSeparators(dateSeparators, dateSeparatorIndex, messageAdapter.getItems());

            boolean finalAllMessagesHidden = allMessagesHidden;
            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE && !finalAllMessagesHidden) {
                    clEmptyChat.setVisibility(View.GONE);
                }
                showMessageList();
                updateMessageDecoration();
                // Moved outside UI thread 30/04/2020
//                // Insert new messages to first index
//                messageAdapter.addMessage(0, messageAfterModels, false);
//                // Sort adapter items according to timestamp
//                mergeSort(messageAdapter.getItems(), ASCENDING);
                showUnreadButton(vm.getUnreadIndicator());
                updateMentionCount();

                if (vm.isOnBottom() && 0 < messageAfterModels.size()) {
                    // Scroll recycler to bottom
                    rvMessageList.scrollToPosition(0);
                }
                if (rvMessageList.getVisibility() != View.VISIBLE) {
                    rvMessageList.setVisibility(View.VISIBLE);
                }
                if (state == STATE.DONE) {
                    insertDateSeparatorToLastIndex();
                    updateMessageDecoration();
                }
            });

            if (0 < responseMessages.size()) {
                // Save entities to database
                TAPDataManager.getInstance(instanceKey).insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
                });
            }

            if (0 < vm.getMessageModels().size() && MAX_ITEMS_PER_PAGE > vm.getMessageModels().size() && !vm.isInitialAPICallFinished()) {
                // Fetch older messages on first call
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
            } else {
                hideLoadingOlderMessagesIndicator();
            }

            if (!vm.isInitialAPICallFinished()) {
                vm.setInitialAPICallFinished(true);
                setAllUnreadMessageToRead(unreadMessageIds);
            }
            checkIfChatIsAvailableAndUpdateUI();
            updateFirstVisibleMessageIndex();
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(String errorMessage) {
            checkIfChatIsAvailableAndUpdateUI();
            if (0 < vm.getMessageModels().size()) {
                fetchBeforeMessageFromAPIAndUpdateUI(messageBeforeView);
                hideLoadingOlderMessagesIndicator();
            }
        }

        private void checkIfChatIsAvailableAndUpdateUI() {
            if (messageAdapter.getItems().isEmpty()) {
                return;
            }
            TAPMessageModel lastMessage = messageAdapter.getItems().get(0);
            if ((ROOM_REMOVE_PARTICIPANT.equals(lastMessage.getAction()) &&
                    null != lastMessage.getTarget() &&
                    vm.getMyUserModel().getUserID().equals(lastMessage.getTarget().getTargetID())) ||
                    (LEAVE_ROOM.equals(lastMessage.getAction()) &&
                            vm.getMyUserModel().getUserID().equals(lastMessage.getUser().getUserID()))) {
                // User has been removed / left from group
                showChatAsHistory(getString(R.string.tap_not_a_participant));
            } else if (DELETE_ROOM.equals(lastMessage.getAction())) {
                // Room was deleted
                //showRoomIsUnavailableState();
                showChatAsHistory(getString(R.string.tap_group_unavailable));
            } else {
                checkChatRoomLocked(lastMessage);
            }
        }
    };

    private void setAllUnreadMessageToRead(List<String> unreadMessageIds) {
        new Thread(() -> {
            // Clear unread badge from room list
            Intent intent = new Intent(CLEAR_ROOM_LIST_BADGE);
            intent.putExtra(ROOM_ID, vm.getRoom().getRoomID());
            LocalBroadcastManager.getInstance(TapTalk.appContext).sendBroadcast(intent);

            // Mark filtered API result messages as read
            markMessageAsRead(unreadMessageIds);

            // Mark messages from database as read
            TAPDataManager.getInstance(instanceKey).getAllUnreadMessagesFromRoom(TAPChatManager.getInstance(instanceKey).getOpenRoom(),
                    new TAPDatabaseListener<TAPMessageEntity>() {
                        @Override
                        public void onSelectFinished(List<TAPMessageEntity> entities) {
                            List<String> pendingReadList = new ArrayList<>();
                            for (TAPMessageEntity entity : entities) {
                                TAPMessageModel messageFromPointer = vm.getMessagePointer().get(entity.getLocalID());
                                if (null != messageFromPointer) {
                                    messageFromPointer = messageFromPointer.copyMessageModel();
                                }
                                if (!unreadMessageIds.contains(entity.getMessageID()) &&
                                        (null == entity.getIsRead() || !entity.getIsRead()) &&
                                        (null == messageFromPointer || null == messageFromPointer.getIsRead() || !messageFromPointer.getIsRead()) &&
                                        !TAPMessageStatusManager.getInstance(instanceKey).getReadMessageQueue().contains(entity.getMessageID()) &&
                                        !TAPMessageStatusManager.getInstance(instanceKey).getMessagesMarkedAsRead().contains(entity.getMessageID())) {
                                    // Add message ID to pending list if new message has not been read or not in mark read queue
                                    pendingReadList.add(entity.getMessageID());
                                }
                            }
                            markMessageAsRead(pendingReadList);
                        }
                    });
        }).start();
    }

    private void fetchBeforeMessageFromAPIAndUpdateUI(TAPDefaultDataView<TAPGetMessageListByRoomResponse> beforeView) {
        new Thread(() -> {
            if (0 < vm.getMessageModels().size()) {
                // Use oldest message's create time as parameter
                TAPDataManager.getInstance(instanceKey).getMessageListByRoomBefore(vm.getRoom().getRoomID(),
                        vm.getMessageModels().get(vm.getMessageModels().size() - 1).getCreated(), MAX_ITEMS_PER_PAGE,
                        beforeView);
            } else {
                // Use current timestamp as parameter if message list is empty
                TAPDataManager.getInstance(instanceKey).getMessageListByRoomBefore(vm.getRoom().getRoomID(),
                        System.currentTimeMillis(), MAX_ITEMS_PER_PAGE,
                        beforeView);
            }
        }).start();
    }

    private TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageBeforeView = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            List<TAPMessageEntity> responseMessages = new ArrayList<>();  // Entities to be saved to database
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();
            boolean allMessagesHidden = true; // Flag to check whether empty chat layout should be removed

            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    messageBeforeModels.addAll(addBeforeTextMessage(message));
                    responseMessages.add(TAPChatManager.getInstance(instanceKey).convertToEntity(message));
                    if (allMessagesHidden && (null == message.getHidden() || !message.getHidden())) {
                        allMessagesHidden = false;
                    }
                    updateMessageMentionIndexes(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Check if room has more messages
            state = response.getHasMore() ? STATE.LOADED : STATE.DONE;

            // Sort adapter items according to timestamp
            mergeSort(messageBeforeModels, ASCENDING);

            String previousDate = "";
            TAPMessageModel previousMessage = null;
            if (null != messageAdapter && !messageAdapter.getItems().isEmpty()) {
                int offset = 1;
                while (null == previousMessage) {
                    if (messageAdapter.getItems().size() < offset) {
                        break;
                    }
                    previousMessage = messageAdapter.getItemAt(messageAdapter.getItems().size() - offset);
                    if ((null != previousMessage.getHidden() && previousMessage.getHidden()) ||
                            previousMessage.getType() == TYPE_UNREAD_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_LOADING_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_DATE_SEPARATOR
                    ) {
                        previousMessage = null;
                        offset++;
                    }
                }
                if (null != previousMessage) {
                    previousDate = TAPTimeFormatter.getInstance().formatDate(previousMessage.getCreated());
                }
            }
            for (TAPMessageModel message : messageBeforeModels) {
                if ((null == message.getHidden() || !message.getHidden()) &&
                        message.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_DATE_SEPARATOR
                ) {
                    String currentDate = TAPTimeFormatter.getInstance().formatDate(message.getCreated());
                    if (null != previousMessage && !currentDate.equals(previousDate)) {
                        // Generate date separator if date is different
                        int index = messageBeforeModels.contains(previousMessage) ?
                                messageBeforeModels.indexOf(previousMessage) + 1 : 0;
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                        dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                        dateSeparatorIndex.put(dateSeparator.getLocalID(), index);
                    }
                    previousDate = currentDate;
                    previousMessage = message;
                }
            }
            insertDateSeparators(dateSeparators, dateSeparatorIndex, messageBeforeModels);

            List<TAPMessageModel> finalMessageBeforeModels = messageBeforeModels;
            boolean finalAllMessagesHidden = allMessagesHidden;
            runOnUiThread(() -> {
                if (clEmptyChat.getVisibility() == View.VISIBLE &&
                        0 < finalMessageBeforeModels.size() &&
                        !finalAllMessagesHidden) {
                    clEmptyChat.setVisibility(View.GONE);
                }

                hideLoadingOlderMessagesIndicator();

                if (!(0 < messageAdapter.getItems().size() && (ROOM_REMOVE_PARTICIPANT.equals(messageAdapter.getItems().get(0).getAction())
                        && TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(messageAdapter.getItems().get(0).getTarget().getTargetID()))
                        || (0 < messageAdapter.getItems().size() && DELETE_ROOM.equals(messageAdapter.getItems().get(0).getAction()))
                        || (0 < messageAdapter.getItems().size() && LEAVE_ROOM.equals(messageAdapter.getItems().get(0).getAction()) &&
                        TAPChatManager.getInstance(instanceKey).getActiveUser().getUserID().equals(messageAdapter.getItems().get(0).getUser().getUserID())))) {
                    showMessageList();
                }

                // Add messages to last index
                messageAdapter.addOlderMessagesFromApi(finalMessageBeforeModels);

                if (0 < finalMessageBeforeModels.size())
                    vm.setLastTimestamp(finalMessageBeforeModels.get(finalMessageBeforeModels.size() - 1).getCreated());

                updateMessageDecoration();
                new Thread(() -> {
                    vm.setMessageModels(messageAdapter.getItems());
                    if (null != vm.getTappedMessageLocalID()) {
                        scrollToMessage(vm.getTappedMessageLocalID());
                    }
                }).start();

                if (rvMessageList.getVisibility() != View.VISIBLE) {
                    rvMessageList.setVisibility(View.VISIBLE);
                }

                setRecyclerViewAnimator();

                if (state == STATE.DONE) {
                    insertDateSeparatorToLastIndex();
                    updateMessageDecoration();
                } else if (state == STATE.LOADED) {
                    rvMessageList.addOnScrollListener(endlessScrollListener);
                }
            });

            TAPDataManager.getInstance(instanceKey).insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
            });
            // Moved to UI thread 2020-05-15
//            if (MAX_ITEMS_PER_PAGE > response.getMessages().size() && 1 < response.getMessages().size()) {
//                state = STATE.DONE;
//            } else {
//                rvMessageList.addOnScrollListener(endlessScrollListener);
//                state = STATE.LOADED;
//            }
        }

        @Override
        public void onError(TAPErrorModel error) {
            setRecyclerViewAnimator();
            hideLoadingOlderMessagesIndicator();
        }

        @Override
        public void onError(Throwable throwable) {
            setRecyclerViewAnimator();
            hideLoadingOlderMessagesIndicator();
            insertDateSeparatorToLastIndex();
        }

        private void setRecyclerViewAnimator() {
            if (null == rvMessageList.getItemAnimator()) {
                // Set default item animator for recycler view
                new Handler().postDelayed(() ->
                        rvMessageList.post(() ->
                                rvMessageList.setItemAnimator(messageAnimator)), 200L);
            }
        }
    };

    private TAPDefaultDataView<TAPGetMessageListByRoomResponse> messageBeforeViewPaging = new TAPDefaultDataView<TAPGetMessageListByRoomResponse>() {
        @Override
        public void onSuccess(TAPGetMessageListByRoomResponse response) {
            hideLoadingOlderMessagesIndicator();
            List<TAPMessageEntity> responseMessages = new ArrayList<>(); // Entities to be saved to database
            List<TAPMessageModel> messageBeforeModels = new ArrayList<>(); // Results from Api that are not present in recyclerView
            LinkedHashMap<String, TAPMessageModel> dateSeparators = new LinkedHashMap<>();
            LinkedHashMap<String, Integer> dateSeparatorIndex = new LinkedHashMap<>();

            for (HashMap<String, Object> messageMap : response.getMessages()) {
                try {
                    TAPMessageModel message = TAPEncryptorManager.getInstance().decryptMessage(messageMap);
                    messageBeforeModels.addAll(addBeforeTextMessage(message));
                    responseMessages.add(TAPChatManager.getInstance(instanceKey).convertToEntity(message));
                    updateMessageMentionIndexes(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Check if room has more messages
            state = response.getHasMore() ? STATE.LOADED : STATE.DONE;

            // Sort adapter items according to timestamp
            mergeSort(messageBeforeModels, ASCENDING);

            String previousDate = "";
            TAPMessageModel previousMessage = null;
            if (null != messageAdapter && !messageAdapter.getItems().isEmpty()) {
                int offset = 1;
                while (null == previousMessage) {
                    if (messageAdapter.getItems().size() < offset) {
                        break;
                    }
                    previousMessage = messageAdapter.getItemAt(messageAdapter.getItems().size() - offset);
                    if ((null != previousMessage.getHidden() && previousMessage.getHidden()) ||
                            previousMessage.getType() == TYPE_UNREAD_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_LOADING_MESSAGE_IDENTIFIER ||
                            previousMessage.getType() == TYPE_DATE_SEPARATOR
                    ) {
                        previousMessage = null;
                        offset++;
                    }
                }
                if (null != previousMessage) {
                    previousDate = TAPTimeFormatter.getInstance().formatDate(previousMessage.getCreated());
                }
            }
            for (TAPMessageModel message : messageBeforeModels) {
                if ((null == message.getHidden() || !message.getHidden()) &&
                        message.getType() != TYPE_UNREAD_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_LOADING_MESSAGE_IDENTIFIER &&
                        message.getType() != TYPE_DATE_SEPARATOR
                ) {
                    String currentDate = TAPTimeFormatter.getInstance().formatDate(message.getCreated());
                    if (null != previousMessage && !currentDate.equals(previousDate)) {
                        // Generate date separator if date is different
                        int index = messageBeforeModels.contains(previousMessage) ?
                                messageBeforeModels.indexOf(previousMessage) + 1 : 0;
                        TAPMessageModel dateSeparator = vm.generateDateSeparator(TapUIChatActivity.this, previousMessage);
                        dateSeparators.put(dateSeparator.getLocalID(), dateSeparator);
                        dateSeparatorIndex.put(dateSeparator.getLocalID(), index);
                    }
                    previousDate = currentDate;
                    previousMessage = message;
                }
            }
            insertDateSeparators(dateSeparators, dateSeparatorIndex, messageBeforeModels);

            runOnUiThread(() -> {
                // Add messages to last index
                messageAdapter.addMessage(messageBeforeModels);

                if (0 < messageBeforeModels.size()) {
                    vm.setLastTimestamp(messageBeforeModels.get(messageBeforeModels.size() - 1).getCreated());
                }

                new Thread(() -> {
                    vm.setMessageModels(messageAdapter.getItems());
                    if (null != vm.getTappedMessageLocalID()) {
                        scrollToMessage(vm.getTappedMessageLocalID());
                    }
                }).start();

                if (rvMessageList.getVisibility() != View.VISIBLE) {
                    rvMessageList.setVisibility(View.VISIBLE);
                }
                if (state == STATE.DONE) {
                    insertDateSeparatorToLastIndex();
                }
                updateMessageDecoration();
            });

            TAPDataManager.getInstance(instanceKey).insertToDatabase(responseMessages, false, new TAPDatabaseListener() {
            });
        }

        @Override
        public void onError(TAPErrorModel error) {
            onError(error.getMessage());
        }

        @Override
        public void onError(Throwable throwable) {
            hideLoadingOlderMessagesIndicator();
            insertDateSeparatorToLastIndex();
        }
    };

    private void markMessageAsRead(List<String> readMessageIds) {
        if (null == readMessageIds || readMessageIds.isEmpty()) {
            return;
        }
        new Thread(() -> {
            //TAPMessageStatusManager.getInstance(instanceKey).addUnreadList(vm.getRoom().getRoomID(), readMessageIds.size());
            TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(readMessageIds);
        }).start();
    }

    private void insertDateSeparators(LinkedHashMap<String, TAPMessageModel> dateSeparators,
                                      LinkedHashMap<String, Integer> dateSeparatorIndex,
                                      List<TAPMessageModel> insertToList) {
        if (dateSeparators.isEmpty()) {
            return;
        }
        int separatorCount = 0;
        for (Map.Entry<String, TAPMessageModel> entry : dateSeparators.entrySet()) {
            Integer baseIndex = dateSeparatorIndex.get(entry.getKey());
            if (null != baseIndex) {
                int index = baseIndex + separatorCount;
                if (index <= insertToList.size()) {
                    insertToList.add(index, entry.getValue());
                    separatorCount++;
                }
            }
        }
        vm.getDateSeparators().putAll(dateSeparators);
    }

    private void insertDateSeparatorToLastIndex() {
        if (null == messageAdapter || messageAdapter.getItems().isEmpty()) {
            return;
        }
        TAPMessageModel firstMessage = null;
        int offset = 1;
        while (null == firstMessage) {
            if (messageAdapter.getItems().size() < offset) {
                return;
            }
            firstMessage = messageAdapter.getItemAt(messageAdapter.getItems().size() - offset);
            if ((null != firstMessage.getHidden() && firstMessage.getHidden()) ||
                    firstMessage.getType() == TYPE_UNREAD_MESSAGE_IDENTIFIER ||
                    firstMessage.getType() == TYPE_LOADING_MESSAGE_IDENTIFIER ||
                    firstMessage.getType() == TYPE_DATE_SEPARATOR
            ) {
                firstMessage = null;
                offset++;
            }
        }
        TAPMessageModel dateSeparator = vm.generateDateSeparator(this, firstMessage);
        vm.getDateSeparators().put(dateSeparator.getLocalID(), dateSeparator);
        vm.getDateSeparatorIndexes().put(dateSeparator.getLocalID(), messageAdapter.getItems().size());
        runOnUiThread(() -> {
            messageAdapter.addItem(messageAdapter.getItems().size(), dateSeparator);
            messageAdapter.notifyItemInserted(messageAdapter.getItems().indexOf(dateSeparator));
        });
    }

    /**
     * =========================================================================================== *
     * FILE TRANSFER
     * =========================================================================================== *
     */

    private void startFileDownload(TAPMessageModel message) {
        if (!TAPUtils.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Request storage permission
            vm.setPendingDownloadMessage(message);
            ActivityCompat.requestPermissions(
                    TapUIChatActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_WRITE_EXTERNAL_STORAGE_SAVE_FILE);
        } else {
            // Download file
            vm.setPendingDownloadMessage(null);
            TAPFileDownloadManager.getInstance(instanceKey).downloadMessageFile(message);
        }
    }

    private void showDownloadFileDialog() {
        // Prompt download if file does not exist
        if (null == vm.getOpenedFileMessage()) {
            return;
        }
        if (null != vm.getOpenedFileMessage().getData()) {
            String fileId = (String) vm.getOpenedFileMessage().getData().get(FILE_ID);
            String fileUrl = (String) vm.getOpenedFileMessage().getData().get(FILE_URL);
            if (null != fileUrl) {
                fileUrl = TAPUtils.removeNonAlphaNumeric(fileUrl).toLowerCase();
            }
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.getRoom().getRoomID(), fileId);
            TAPFileDownloadManager.getInstance(instanceKey).removeFileMessageUri(vm.getRoom().getRoomID(), fileUrl);
        }
        messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getOpenedFileMessage()));
        new TapTalkDialog.Builder(TapUIChatActivity.this)
                .setTitle(getString(R.string.tap_error_could_not_find_file))
                .setMessage(getString(R.string.tap_error_redownload_file))
                .setCancelable(true)
                .setPrimaryButtonTitle(getString(R.string.tap_ok))
                .setSecondaryButtonTitle(getString(R.string.tap_cancel))
                .setPrimaryButtonListener(v -> startFileDownload(vm.getOpenedFileMessage()))
                .show();
    }

    private void restartFailedDownloads() {
        if (TAPFileDownloadManager.getInstance(instanceKey).hasFailedDownloads() &&
                TAPNetworkStateManager.getInstance(instanceKey).hasNetworkConnection(TapTalk.appContext)) {
            // Notify chat bubbles with failed download
            for (String localID : TAPFileDownloadManager.getInstance(instanceKey).getFailedDownloads()) {
                if (vm.getMessagePointer().containsKey(localID)) {
                    runOnUiThread(() -> messageAdapter.notifyItemChanged(messageAdapter.getItems().indexOf(vm.getMessagePointer().get(localID))));
                }
            }
            //TAPFileDownloadManager.getInstance(instanceKey).clearFailedDownloads();
        }
    }

    /**
     * =========================================================================================== *
     * OTHERS
     * =========================================================================================== *
     */

//    private void showRoomIsUnavailableState() {
//        new DeleteRoomAsync().execute(vm.getRoom().getRoomID());
//        runOnUiThread(() -> {
//            tvMessage.setText(getResources().getString(R.string.tap_group_unavailable));
//            flRoomUnavailable.setVisibility(View.VISIBLE);
//            flMessageList.setVisibility(View.GONE);
//            clEmptyChat.setVisibility(View.GONE);
//            clChatComposerAndHistory.setVisibility(View.GONE);
//            if (null != vRoomImage) {
//                vRoomImage.setClickable(false);
//            }
//        });
//    }

//    private void markMessageAsRead(TAPMessageModel readMessage) {
//        new Thread(() -> {
//            if (null != readMessage.getIsRead() && !readMessage.getIsRead()) {
//                TAPMessageStatusManager.getInstance(instanceKey).addUnreadListByOne(readMessage.getRoom().getRoomID());
//                TAPMessageStatusManager.getInstance(instanceKey).addReadMessageQueue(readMessage);
//            }
//        }).start();
//    }

    private TAPDefaultDataView<TAPAddContactResponse> addContactView = new TAPDefaultDataView<TAPAddContactResponse>() {
        @Override
        public void onSuccess(TAPAddContactResponse response) {
            TAPUserModel newContact = response.getUser().setUserAsContact();
            TAPContactManager.getInstance(instanceKey).updateUserData(newContact);
        }
    };

    private View.OnClickListener llDeleteGroupClickListener = v -> TAPOldDataManager.getInstance(instanceKey).cleanRoomPhysicalData(vm.getRoom().getRoomID(), new TAPDatabaseListener() {
        @Override
        public void onDeleteFinished() {
            super.onDeleteFinished();
            TAPDataManager.getInstance(instanceKey).deleteMessageByRoomId(vm.getRoom().getRoomID(), new TAPDatabaseListener() {
                @Override
                public void onDeleteFinished() {
                    super.onDeleteFinished();
                    vm.setDeleteGroup(true);
                    rvCustomKeyboard.setVisibility(View.GONE);
                    onBackPressed();
                }
            });
        }
    });

    private class DeleteRoomAsync extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... roomIDs) {
            TAPOldDataManager.getInstance(instanceKey).cleanRoomPhysicalData(roomIDs[0], new TAPDatabaseListener() {
                @Override
                public void onDeleteFinished() {
                    super.onDeleteFinished();
                    TAPDataManager.getInstance(instanceKey).deleteMessageByRoomId(roomIDs[0], new TAPDatabaseListener() {
                        @Override
                        public void onDeleteFinished() {
                            super.onDeleteFinished();
                            if (!TAPGroupManager.Companion.getInstance(instanceKey).getRefreshRoomList())
                                TAPGroupManager.Companion.getInstance(instanceKey).setRefreshRoomList(true);
                        }
                    });
                }
            });
            return null;
        }
    }

//    private SwipeBackLayout.SwipeBackInterface swipeInterface = new SwipeBackLayout.SwipeBackInterface() {
//        @Override
//        public void onSwipeBack() {
//            TAPUtils.dismissKeyboard(TapUIChatActivity.this);
//        }
//
//        @Override
//        public void onSwipeToFinishActivity() {
//            if (isTaskRoot()) {
//                // Trigger listener callback if no other activity is open
//                for (TapListener listener : TapTalk.getTapTalkListeners(instanceKey)) {
//                    listener.onTaskRootChatRoomClosed(TapUIChatActivity.this);
//                }
//            }
//        }
//    };
}
