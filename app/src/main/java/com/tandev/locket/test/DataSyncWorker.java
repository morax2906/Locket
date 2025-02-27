package com.tandev.locket.test;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import okhttp3.ResponseBody;
import com.google.gson.Gson;

import java.util.ArrayList;

public class DataSyncWorker extends Worker {
    private MomentRepository momentRepository;

    public DataSyncWorker(Context context, WorkerParameters params) {
        super(context, params);
        momentRepository = new MomentRepository((Application) context.getApplicationContext());
    }

    @Override
    public Result doWork() {
        // Gọi API để tải dữ liệu
        momentRepository.refreshDataFromServer(new ArrayList<>()); // Hoặc truyền danh sách excludedUsers

        // Nếu công việc thành công
        return Result.success();
    }
}
