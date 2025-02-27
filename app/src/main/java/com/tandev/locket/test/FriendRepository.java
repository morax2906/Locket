package com.tandev.locket.test;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.google.gson.Gson;
import com.tandev.locket.api.FriendApiService;
import com.tandev.locket.api.MomentApiService;
import com.tandev.locket.api.client.LoginApiClient;
import com.tandev.locket.model.firend.Friend;
import com.tandev.locket.model.firend.UserData;
import com.tandev.locket.model.login.response.LoginResponse;
import com.tandev.locket.test.FriendEntity;
import com.tandev.locket.sharedfreferences.SharedPreferencesUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendRepository {
    private final LiveData<List<FriendEntity>> allFriends;
    private final MomentApiService momentApiService;
    private final Context context;
    private final LoginResponse loginResponse; // Giả sử đây là model chứa token (idToken, vv)

    public FriendRepository(Application application) {
        this.context = application;
        AppDatabase db = Room.databaseBuilder(application, AppDatabase.class, "friend_database")
                .fallbackToDestructiveMigration()
                .build();
        FriendDao friendDao = db.friendDao();
        allFriends = friendDao.getAllFriends();
        momentApiService = LoginApiClient.getCheckEmailClient().create(MomentApiService.class);
        loginResponse = SharedPreferencesUser.getLoginResponse(application);
    }

    // --- Phương thức tạo JSON cho API ---
    @SuppressLint("DefaultLocale")
    private String createGetMomentV2ExcludedUsersJson(List<String> excludedUsers) {
        String excludedUsersJson = (excludedUsers == null || excludedUsers.isEmpty()) ? "[]" : new Gson().toJson(excludedUsers);

        return String.format(
                "{\"data\":{" +
                        "\"excluded_users\":%s," +
                        "\"last_fetch\":%d," +
                        "\"should_count_missed_moments\":%b" +
                        "}}",
                excludedUsersJson,
                1,
                true
        );
    }

    public void refreshDataFromServer(List<String> excludedUsers) {
        if (excludedUsers == null) {
            excludedUsers = new ArrayList<>();
        }

        String token = "Bearer " + loginResponse.getIdToken();
        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=UTF-8"),
                createGetMomentV2ExcludedUsersJson(excludedUsers)
        );

        Call<ResponseBody> responseBodyCall = momentApiService.GET_MOMENT_V2(token, requestBody);
        // Lưu ý: Nếu bạn không cần gọi đệ quy (pagination) thì có thể bỏ logic cập nhật excludedUsers và gọi lại refreshDataFromServer
        List<String> finalExcludedUsers = excludedUsers;

        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        // Chuyển đổi JSON thành đối tượng Friend
                        Friend friendResponse = gson.fromJson(responseBody, Friend.class);

                        if (friendResponse.getResult().getData() != null) {
                            // Lấy danh sách dữ liệu (mảng data) từ server
                            List<UserData> dataList = (List<UserData>) friendResponse.getResult().getData();
                            List<FriendEntity> entityList = new ArrayList<>();

                            // Map từng UserData sang FriendEntity
                            for (UserData userData : dataList) {
                                FriendEntity entity = new FriendEntity(
                                        userData.getUid(),
                                        userData.getFirst_name(),
                                        userData.getLast_name(),
                                        userData.getBadge(),
                                        userData.getProfile_picture_url(),
                                        userData.isTemp(),
                                        userData.getUsername()
                                );
                                entityList.add(entity);
                            }

                            // Lưu dữ liệu vào Room trên background thread
                            new Thread(() -> {
                                // Lấy instance của AppDatabase; nếu có singleton, dùng nó thay vì tạo mới
                                AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "friend_database")
                                        .fallbackToDestructiveMigration()
                                        .build();
                                db.friendDao().insertAll(entityList);
                            }).start();

                            // Cập nhật danh sách excludedUsers cho lần gọi tiếp theo (nếu dùng phân trang)
                            finalExcludedUsers.add(dataList.get(0).getUsername());
                            Log.d(">>>>>>>>>>", "onResponse: "+dataList.get(0).getUsername());
                            // Gọi lại API đệ quy để load thêm dữ liệu nếu cần
                            refreshDataFromServer(finalExcludedUsers);
                        }
                    } catch (IOException e) {
                        Log.e("FriendRepository", "Error reading response body", e);
                    }
                } else {
                    Log.e("FriendRepository", "Response unsuccessful: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("FriendRepository", "Error fetching data", t);
            }
        });
    }

    // Phương thức overload nếu không cần truyền excludedUsers (bắt đầu mới)
    public void refreshDataFromServer() {
        refreshDataFromServer(new ArrayList<>());
    }

    public LiveData<List<FriendEntity>> getAllFriends() {
        return allFriends;
    }
}
