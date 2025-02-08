package com.tandev.locket.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tandev.locket.R;
import com.tandev.locket.api.FriendApiService;
import com.tandev.locket.api.client.LoginApiClient;
import com.tandev.locket.model.firend.Friend;
import com.tandev.locket.model.login.response.LoginResponse;
import com.tandev.locket.model.moment.Moment;
import com.tandev.locket.sharedfreferences.SharedPreferencesUser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ItemAdapter.java
public class ViewMomentAdapter extends RecyclerView.Adapter<ViewMomentAdapter.ItemViewHolder> {

    private ArrayList<Moment> itemList; // Danh sách URL hình ảnh hoặc dữ liệu
    private Context context;
    private LoginResponse loginResponse;
    private FriendApiService friendApiService;

    public ViewMomentAdapter(Context context, ArrayList<Moment> itemList) {
        this.context = context;
        this.itemList = itemList;
        loginResponse = SharedPreferencesUser.getLoginResponse(context);
        friendApiService = LoginApiClient.getCheckEmailClient().create(FriendApiService.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(ArrayList<Moment> filterList) {
        this.itemList = filterList;
        notifyDataSetChanged();
    }

    public interface FetchUserCallback {
        void onSuccess(Friend friend);

        void onFailure(String errorMessage);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_moment, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.bind(itemList.get(position));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    // ViewHolder cho mỗi item
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView shapeable_imageview;
        TextView txt_content;
        RoundedImageView rounded_imageview;
        TextView txt_name;
        TextView txt_time;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            shapeable_imageview = itemView.findViewById(R.id.shapeable_imageview);
            txt_content = itemView.findViewById(R.id.txt_content);
            rounded_imageview = itemView.findViewById(R.id.rounded_imageview);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_time = itemView.findViewById(R.id.txt_time);
        }

        public void bind(Moment moment) {
            Glide.with(context)
                    .load(moment.getResult().getData().get(0).getThumbnail_url())
                    .into(shapeable_imageview);
            txt_content.setText(checkOverlayId(moment.getResult().getData().get(0).getOverlays().get(0).getOverlay_id(),
                    moment.getResult().getData().get(0).getOverlays().get(0).getAlt_text()));

            getFetchUserV2(moment.getResult().getData().get(0).getUser(), new FetchUserCallback() {
                @Override
                public void onSuccess(Friend friend) {
                    Glide.with(context)
                            .load(friend.getResult().getData().getProfile_picture_url())
                            .placeholder(R.drawable.background_btn)
                            .into(rounded_imageview);
                    txt_name.setText(friend.getResult().getData().getFirst_name());
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFailure(String errorMessage) {
                    Glide.with(context)
                            .load("")
                            .placeholder(R.drawable.background_btn)
                            .into(rounded_imageview);
                    txt_name.setText("null");
                }
            });
            txt_time.setText(formatDate(moment.getResult().getData().get(0).getDate().get_seconds()));
        }
    }

    private String checkOverlayId(String overlay_id, String alt_text) {
        if (overlay_id.equals("caption:time")) {
            alt_text = "🕘 " + alt_text;
        }
        return alt_text;
    }

    private String formatDate(long seconds) {
        long currentTimeMillis = System.currentTimeMillis();
        long timeInMillis = seconds * 1000;
        long diff = currentTimeMillis - timeInMillis;

        if (diff < TimeUnit.HOURS.toMillis(1)) {
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            return minutes + "ph";
        }

        if (diff < TimeUnit.DAYS.toMillis(1)) {
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            return hours + "g";
        }

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        if (days < 7) { // Nếu dưới 7 ngày thì hiển thị số ngày
            return days + "d";
        }

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        return dateFormat.format(new Date(timeInMillis));
    }


    @SuppressLint("DefaultLocale")
    private String createGetFriendsJson(String user_id) {
        return String.format(
                "{\"data\":{\"user_uid\":\"%s\"}}",
                user_id
        );
    }

    // Phương thức gọi API getMomentV2
    private void getFetchUserV2(String user_id, FetchUserCallback callback) {
        String token = "Bearer " + loginResponse.getIdToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), createGetFriendsJson(user_id));
        Call<ResponseBody> responseBodyCall = friendApiService.FETCH_USER_RESPONSE_CALL(token, requestBody);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        Friend friend = gson.fromJson(responseBody, Friend.class);
                        // Gọi callback khi thành công
                        callback.onSuccess(friend);
                    } catch (IOException e) {
                        callback.onFailure("Error reading response body: " + e.getMessage());
                    }
                } else {
                    callback.onFailure("Failed response from getFetchUserV2");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                callback.onFailure("Unsuccessful response: " + throwable.getMessage());
            }
        });
    }
}
