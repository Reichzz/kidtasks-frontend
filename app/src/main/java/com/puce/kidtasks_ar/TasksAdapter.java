package com.puce.kidtasks_ar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    List<Tasks> tasksList;
    Context context;
    AppCompatActivity appCompatActivity;
    APITasks api;

    public TasksAdapter(Context context,List<Tasks> tasksList) {
        this.context = context.getApplicationContext();
        this.appCompatActivity = (AppCompatActivity) context;
        this.tasksList = tasksList;
        AdaptadorRetrofit retrofitAdapter = new AdaptadorRetrofit();
        this.api = retrofitAdapter.getAdaptador().create(APITasks.class);
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_template, null, false);
        return new TasksAdapter.TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Tasks task = tasksList.get(position);
        holder.textViewDescription.setText(task.getDescriptionTasks());
        holder.checkBoxCompleted.setChecked(task.getCompletedTasks());


        holder.checkBoxCompleted.setOnCheckedChangeListener(null);

        holder.checkBoxCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
            updateTaskOnServer(task.getIdTasks(), isChecked, task.getDescriptionTasks(), task.getChildTasks());
        });


        // Configura el estilo inicialmente al recargar la vista
        if (task.getCompletedTasks()) {
            holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewDescription.setPaintFlags(holder.textViewDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.btndeletetask.setOnClickListener(v ->{
            showDeleteTaskDialog(position);
        });

        holder.btneditask.setOnClickListener(v ->{
            showEditTaskDialog(position);
        });


    }
    private void updateTaskOnServer(String taskId, boolean completed, String description, String childId) {
        Tasks taskToUpdate = new Tasks();
        taskToUpdate.setIdTasks(taskId);
        taskToUpdate.setCompletedTasks(completed);
        taskToUpdate.setDescriptionTasks(description); // Asegúrate de incluir todos los campos necesarios según tu modelo en el servidor
        taskToUpdate.setChildTasks(childId);

        Call<Void> call = api.updateTasksI(taskId, taskToUpdate);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Tarea completada!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al actualizar tarea: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Fallo al conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }





    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
         TextView textViewDescription;
         CheckBox checkBoxCompleted;
         Button btndeletetask, btneditask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDescription = itemView.findViewById(R.id.tvTaskName);
            checkBoxCompleted = itemView.findViewById(R.id.checkBox);
            btndeletetask = itemView.findViewById(R.id.btndeletetask);
            btneditask = itemView.findViewById(R.id.btneditask);
        }
    }

    private void removeTasks(String id, int position) {
        Call<Void> call = api.deleteTasksI(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    tasksList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Tarea eliminada con éxito!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error al eliminar tarea.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Fallo al conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void editTasks(String id, String taskD, int position) {
        Tasks tasks =  tasksList.get(position);
        tasks.setDescriptionTasks(taskD);

        Call<Void> call = api.updateTasksI(id, tasks);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    tasksList.get(position).setDescriptionTasks(taskD);
                    Toast.makeText(context, "Tarea editada con éxito!", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "La tarea no logró editar.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
            }
        });


    }

    private void showDeleteTaskDialog(int position) {
        Dialog dialog = new Dialog(appCompatActivity);
        Tasks task = tasksList.get(position);
        dialog.setContentView(R.layout.dialog_deletetask);
        Button cancelbtntask = dialog.findViewById(R.id.cancelbtntask);
        Button confirmbtntask = dialog.findViewById(R.id.confirmbtntask);

        cancelbtntask.setOnClickListener(v -> {
            dialog.dismiss();

        });

        confirmbtntask.setOnClickListener(v -> {
            removeTasks(task.getIdTasks(), position);
            dialog.dismiss();
        });

        dialog.show();

    }

    private void showEditTaskDialog(int position) {
        Dialog dialog = new Dialog(appCompatActivity);
        dialog.setContentView(R.layout.dialog_editask);
        Tasks tasks = tasksList.get(position);

        EditText inputEditask = dialog.findViewById(R.id.inputEditask);
        Button canceltaskedit = dialog.findViewById(R.id.canceltaskedit);
        Button taskeditbtn = dialog.findViewById(R.id.taskeditbtn);

        inputEditask.setText(tasks.getDescriptionTasks());

        canceltaskedit.setOnClickListener(v -> {
            dialog.dismiss();
        });

        taskeditbtn.setOnClickListener(v -> {
            String inputEA = inputEditask.getText().toString();
            if (!inputEA.isEmpty()) {
                editTasks(tasks.getIdTasks(), inputEA, position);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Campo vacío. Por favor completarlo", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }



}
