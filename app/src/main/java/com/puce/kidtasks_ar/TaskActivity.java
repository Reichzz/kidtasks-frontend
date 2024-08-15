package com.puce.kidtasks_ar;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TaskActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TasksAdapter adapter;
    List<Tasks> tasksList = new ArrayList<>();
    Retrofit retrofit;
    APITasks api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task);

        recyclerView = findViewById(R.id.recyclerViewTasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        retrofit = new AdaptadorRetrofit().getAdaptador();
        api = retrofit.create(APITasks.class);

        adapter = new TasksAdapter(this, tasksList);
        recyclerView.setAdapter(adapter);

        String child = getIntent().getStringExtra("child_id");
        getTasks(api, child);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tasks), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAddTaskDialog(child);
            };
        });


    }


    private void getTasks(APITasks api, String child) {
        tasksList.clear();
        Call<List<Tasks>> call = api.getTasks(child);

        call.enqueue(new Callback<List<Tasks>>() {
            @Override
            public void onResponse(Call<List<Tasks>> call, Response<List<Tasks>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tasksList.clear();
                    tasksList.addAll(response.body());
                    adapter = new TasksAdapter(TaskActivity.this, tasksList);
                    recyclerView.setAdapter(adapter);
                } else {
                    // Manejar el caso en que la respuesta no contiene datos
                    Log.e("TAG", "Received empty response or error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Tasks>> call, Throwable t) {
                Log.e("TAG", "Error fetching tasks", t);
            }
        });
    }

    public void addActivity(final APITasks api,String childId, String taskD) {
        Tasks tasks = new Tasks();

        tasks.setDescriptionTasks(taskD);
        tasks.setChildTasks(childId);

        Call<Void> call = api.addTasksI(tasks);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.d("TAG", "Codigo "+response.code());
                Log.d("TAG", "Body "+response.body());
                Log.d("TAG", "Error Body "+response.errorBody());
                Log.d("TAG", "Mensaje "+response.message());
                Log.d("TAG", "RAW "+response.raw());
                Log.d("TAG", "Headers "+response.headers());

                if (response.isSuccessful()) {
                    Toast.makeText(TaskActivity.this, "Tarea añadida con éxito!", Toast.LENGTH_SHORT).show();
                    getTasks(api, childId);
                } else {
                    Toast.makeText(TaskActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TAG", "Error al agregar "+t);
            }
        });
    }

    private void showAddTaskDialog(String child_id) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_addtask);

        EditText inputask = dialog.findViewById(R.id.inputeditchild);
        Button btnGuadarTask = dialog.findViewById(R.id.btnconfirmdelete);
        Button btnCancelarTask = dialog.findViewById(R.id.btncanceldeletechild);

        btnCancelarTask.setOnClickListener(v -> {
            inputask.setText("");
            dialog.dismiss();
        });

        btnGuadarTask.setOnClickListener(v -> {
            String input = inputask.getText().toString();
            if (!input.isEmpty()) {
                addActivity(api, child_id, input);
                dialog.dismiss();
            } else {
                Toast.makeText(TaskActivity.this, "Campo vacío. Por favor ingrese un nombre para la tarea.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }


}