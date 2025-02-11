package com.tandev.locket.fragment.view_moment;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.tandev.locket.R;
import com.tandev.locket.adapter.ViewAllMomentAdapter;
import com.tandev.locket.adapter.ViewMomentAdapter;
import com.tandev.locket.test.MomentViewModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ViewMomentFragment extends Fragment {
    private RelativeLayout relative_view_all_moment;
    private RelativeLayout relative_view_moment;
    private RecyclerView rv_view_moment;
    private ViewMomentAdapter viewMomentAdapter;
    private ViewAllMomentAdapter viewAllMomentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_moment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        rv_view_moment = view.findViewById(R.id.rv_view_moment);
        RecyclerView rv_view_all_moment = view.findViewById(R.id.rv_view_all_moment);
        RoundedImageView img_capture = view.findViewById(R.id.img_capture);
        ImageView img_all_moment = view.findViewById(R.id.img_all_moment);
        relative_view_all_moment = view.findViewById(R.id.relative_view_all_moment);
        relative_view_moment = view.findViewById(R.id.relative_view_moment);

        // Thiết lập RecyclerView cho chế độ xem từng moment (vertical, như ViewPager)
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false);
        rv_view_moment.setLayoutManager(layoutManager);
        // Khởi tạo adapter với danh sách ban đầu là rỗng
        viewMomentAdapter = new ViewMomentAdapter(requireContext(), new ArrayList<>());
        rv_view_moment.setAdapter(viewMomentAdapter);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rv_view_moment);

        // Thiết lập RecyclerView cho chế độ xem tất cả các moment (grid 3 cột)
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 3);
        rv_view_all_moment.setLayoutManager(gridLayoutManager);
        viewAllMomentAdapter = new ViewAllMomentAdapter(new ArrayList<>(), requireContext());
        rv_view_all_moment.setAdapter(viewAllMomentAdapter);

        // Sự kiện khi nhấn nút capture: chuyển về trang LiveCameraFragment
        img_capture.setOnClickListener(v -> {
            ViewPager2 viewPager = requireActivity().findViewById(R.id.viewPager);
            if (viewPager != null) {
                viewPager.setCurrentItem(0, true);
                sendPositionSwipeViewpage2(0);
            } else {
                Log.e("ViewMomentFragment", "ViewPager2 not found!");
            }
        });

        // Sự kiện khi nhấn nút xem all moment: hiển thị grid view
        img_all_moment.setOnClickListener(v -> {
            relative_view_all_moment.setVisibility(View.VISIBLE);
            relative_view_moment.setVisibility(View.GONE);
            // Nếu cần, cập nhật lại dữ liệu cho grid adapter
            sendPositionSwipeViewpage2(0);
        });

        // Lắng nghe sự kiện cuộn của RecyclerView view moment để gửi vị trí hiện tại
        rv_view_moment.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { // Khi cuộn dừng
                    View centerView = snapHelper.findSnapView(layoutManager);
                    if (centerView != null) {
                        int position = layoutManager.getPosition(centerView);
                        Log.d("RecyclerView", "Item hiện tại: " + position);
                        sendPositionSwipeViewpage2(position);
                    }
                }
            }
        });
    }

    // Phương thức gửi vị trí cuộn qua LocalBroadcast (sử dụng cho các thành phần khác nếu cần)
    private void sendPositionSwipeViewpage2(int position) {
        Intent intent = new Intent("send_position_swipe_viewpage2");
        intent.putExtra("position", position);
        LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        relative_view_all_moment.setVisibility(View.GONE);
        relative_view_moment.setVisibility(View.VISIBLE);
        rv_view_moment.scrollToPosition(0);

        // Quan sát LiveData từ Room để cập nhật UI
        MomentViewModel viewModel = new ViewModelProvider(this).get(MomentViewModel.class);
        viewModel.getAllMoments().observe(getViewLifecycleOwner(), momentEntities -> {
            // Cập nhật dữ liệu cho cả hai adapter
            viewMomentAdapter.setFilterList(momentEntities);
            viewAllMomentAdapter.setFilterList(momentEntities);
        });
    }
}
