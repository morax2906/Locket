package com.tandev.locket.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import com.tandev.locket.sharedfreferences.SharedPreferencesUser;
import com.tandev.locket.test.MomentEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ItemAdapter.java
public class ViewMomentAdapter extends RecyclerView.Adapter<ViewMomentAdapter.ItemViewHolder> {

    private List<MomentEntity> itemList; // Đổi từ Moment sang MomentEntity

    private final Context context;
    private final LoginResponse loginResponse;
    private final FriendApiService friendApiService;

    public ViewMomentAdapter(Context context, List<MomentEntity> itemList) {
        this.context = context;
        this.itemList = itemList;
        loginResponse = SharedPreferencesUser.getLoginResponse(context);
        friendApiService = LoginApiClient.getCheckEmailClient().create(FriendApiService.class);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setFilterList(List<MomentEntity> filterList) {
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

        public void bind(MomentEntity moment) {
            Glide.with(context)
                    .load(moment.getThumbnailUrl())
                    .into(shapeable_imageview);
            if (moment.getOverlays() != null && !moment.getOverlays().isEmpty()) {
                txt_content.setText(checkOverlayId(moment.getOverlays().get(0).getOverlay_id(), moment.getOverlays().get(0).getAlt_text(), txt_content));
            } else {
                txt_content.setVisibility(View.GONE);
            }


            getFetchUserV2(moment.getUser(), new FetchUserCallback() {
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
            txt_time.setText(formatDate(moment.getDateSeconds()));
        }
    }

    @SuppressLint("ResourceAsColor")
    private String checkOverlayId(String overlay_id, String alt_text, TextView txt_content) {
        if (overlay_id.equals("caption:time")) {
            alt_text = "🕘 " + alt_text;
        } else if (overlay_id.equals("caption:party_time")) {
            Drawable backgroundDrawable = txt_content.getBackground();
            if (backgroundDrawable instanceof GradientDrawable) {
                txt_content.setBackgroundResource(R.drawable.gradient_party_time);
            }
            txt_content.setTextColor(ContextCompat.getColor(context, R.color.black));
            alt_text = "\uD83E\uDEA9 " + alt_text;
        } else if (overlay_id.equals("caption:goodnight")) {
            Drawable backgroundDrawable = txt_content.getBackground();
            if (backgroundDrawable instanceof GradientDrawable) {
                txt_content.setBackgroundResource(R.drawable.gradient_good_night);
            }
            txt_content.setTextColor(ContextCompat.getColor(context, R.color.white));
            alt_text = "\uD83C\uDF19 " + alt_text;
        } else if (overlay_id.equals("caption:miss_you")) {
            Drawable backgroundDrawable = txt_content.getBackground();
            if (backgroundDrawable instanceof GradientDrawable) {
                txt_content.setBackgroundResource(R.drawable.gradient_miss_you);
            }
            txt_content.setTextColor(ContextCompat.getColor(context, R.color.white));
            alt_text = "\ud83e\udd70 " + alt_text;
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
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
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
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                callback.onFailure("Unsuccessful response: " + throwable.getMessage());
            }
        });
    }
}
