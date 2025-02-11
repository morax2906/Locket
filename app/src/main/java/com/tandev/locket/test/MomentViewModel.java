package com.tandev.locket.test;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import java.util.List;

public class MomentViewModel extends AndroidViewModel {
    private final LiveData<List<MomentEntity>> allMoments;

    // Constructor với loginResponse được truyền vào
    public MomentViewModel(@NonNull Application application) {
        super(application);
        MomentRepository repository = new MomentRepository(application);
        allMoments = repository.getAllMoments();
        // Đồng bộ dữ liệu khi ViewModel khởi tạo (nếu có kết nối)
        repository.refreshDataFromServer();
    }

    public LiveData<List<MomentEntity>> getAllMoments() {
        return allMoments;
    }
}
