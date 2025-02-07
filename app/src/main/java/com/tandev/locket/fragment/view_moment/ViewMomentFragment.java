package com.tandev.locket.fragment.view_moment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tandev.locket.R;
import com.tandev.locket.adapter.ViewMomentAdapter;
import com.tandev.locket.adapter.ViewAllMomentAdapter;
import com.tandev.locket.api.MomentApiService;
import com.tandev.locket.api.client.LoginApiClient;
import com.tandev.locket.model.login.response.LoginResponse;
import com.tandev.locket.model.moment.Moment;
import com.tandev.locket.sharedfreferences.SharedPreferencesUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewMomentFragment extends Fragment {
    private RelativeLayout relative_view_all_moment;
    private RelativeLayout relative_view_moment;
    private RecyclerView rv_view_moment;
    private RecyclerView rv_view_all_moment;

    private RoundedImageView img_capture;
    private ImageView img_all_moment;
    private ViewMomentAdapter viewMomentAdapter;
    private ViewAllMomentAdapter viewAllMomentAdapter;
    private final ArrayList<Moment> itemList = new ArrayList<>();
    private LoginResponse loginResponse;
    private MomentApiService momentApiService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_moment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginResponse = SharedPreferencesUser.getLoginResponse(requireContext());
        momentApiService = LoginApiClient.getCheckEmailClient().create(MomentApiService.class);

         rv_view_moment = view.findViewById(R.id.rv_view_moment);
         rv_view_all_moment = view.findViewById(R.id.rv_view_all_moment);
        img_capture = view.findViewById(R.id.img_capture);
        img_all_moment = view.findViewById(R.id.img_all_moment);
        relative_view_all_moment = view.findViewById(R.id.relative_view_all_moment);
        relative_view_moment = view.findViewById(R.id.relative_view_moment);

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        rv_view_moment.setLayoutManager(layoutManager);

        // Thiết lập Adapter cho RecyclerView
        viewMomentAdapter = new ViewMomentAdapter(requireContext(), itemList);
        rv_view_moment.setAdapter(viewMomentAdapter);

        // Sử dụng PagerSnapHelper để tạo hiệu ứng cuộn giống ViewPager2
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv_view_moment);



        // Thiết lập LayoutManager với 3 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3); // 3 là số item mỗi hàng
        rv_view_all_moment.setLayoutManager(gridLayoutManager);

        // Thiết lập Adapter
        viewAllMomentAdapter = new ViewAllMomentAdapter(itemList, requireContext());
        rv_view_all_moment.setAdapter(viewAllMomentAdapter);

        img_capture.setOnClickListener(view1 -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
            if (viewPager != null) {
                viewPager.setCurrentItem(0, true); // Chuyển về LiveCameraFragment
            } else {
                Log.e("ViewMomentFragment", "ViewPager2 not found!");
            }
        });
        img_all_moment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relative_view_all_moment.setVisibility(View.VISIBLE);
                relative_view_moment.setVisibility(View.GONE);

                viewAllMomentAdapter.setFilterList(itemList);
            }
        });

        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        for (int i = firstVisibleItemPosition; i <= lastVisibleItemPosition; i++) {
           sendPositionSwipeViewpage2(i);
            Log.d(">>>>>>>>>>>>>>>>>>>>>>>>>", "onViewCreated: "+i);
        }

        // Thêm dữ liệu mẫu vào danh sách
        getMomentV2(null);
    }

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

    private void getMomentV2(List<String> excludedUsers) {
        if (excludedUsers == null) {
            excludedUsers = new ArrayList<>();
        }

        String token = "Bearer " + loginResponse.getIdToken();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), createGetMomentV2ExcludedUsersJson(excludedUsers));
        Call<ResponseBody> ResponseBodyCall = momentApiService.GET_MOMENT_V2(token, requestBody);
        List<String> finalExcludedUsers = excludedUsers;
        ResponseBodyCall.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseBody = response.body().string();
                        Gson gson = new Gson();
                        Moment moment = gson.fromJson(responseBody, Moment.class);
                        if (!moment.getResult().getData().isEmpty()) {
                            finalExcludedUsers.add(moment.getResult().getData().get(0).getUser());
                            getMomentV2(finalExcludedUsers);
                            itemList.add(moment);
                        } else {
                            viewMomentAdapter.setFilterList(itemList);
                        }
                    } catch (IOException e) {
                        Log.e("Response Error", "Error reading response body", e);
                    }
                } else {
                    viewMomentAdapter.setFilterList(itemList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                viewMomentAdapter.setFilterList(itemList);
            }
        });
    }

    private void sendPositionSwipeViewpage2(int position) {
        Intent intent = new Intent("send_position_swipe_viewpage2");
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        relative_view_all_moment.setVisibility(View.GONE);
        relative_view_moment.setVisibility(View.VISIBLE);
        rv_view_moment.scrollToPosition(0);
    }
}
