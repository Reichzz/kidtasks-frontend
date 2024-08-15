package com.puce.kidtasks_ar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildrenAdapter extends ArrayAdapter<Child> {
    List<Child> ChildrenList;
    AppCompatActivity appCompatActivity;
    Context context;
    APIChild api;

    ChildrenAdapter(AppCompatActivity context, List<Child> ChildrenList) {
        super(context, R.layout.children_template_list, ChildrenList);
        this.appCompatActivity = context;
        this.context = context.getApplicationContext();
        this.ChildrenList = ChildrenList;
        AdaptadorRetrofit retrofitAdapter = new AdaptadorRetrofit();
        this.api = retrofitAdapter.getAdaptador().create(APIChild.class);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = appCompatActivity.getLayoutInflater();
        View item = inflater.inflate(R.layout.children_template_list, parent, false);

        TextView textView1 = item.findViewById(R.id.tvName);
        TextView tvStreak = item.findViewById(R.id.tvStreak);
        Button editname = item.findViewById(R.id.editname);
        Button deletename = item.findViewById(R.id.deletename);

        Child child = ChildrenList.get(position);
        textView1.setText(child.getNameChild());
        tvStreak.setText("Racha: " + child.getStreakChild());

        item.setOnClickListener(v -> {
            Intent intent = new Intent(appCompatActivity, TaskActivity.class);
            intent.putExtra("child_id", child.getIdChild());
            appCompatActivity.startActivity(intent);
        });

        deletename.setOnClickListener(v -> {
            showDeleteNameDialog(position);
        });

        editname.setOnClickListener(v -> {
            showEditNameDialog(position);
        });

        return item;
    }

    private void deleteChild(String id, int position) {
        Call<Void> call = api.deleteChildI(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    ChildrenList.remove(position);
                    notifyDataSetChanged();
                    Toast.makeText(context, "Registro eliminado con éxito!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Ha ocurrido un error. Inténtelo de nuevo", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Fallo al conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void editChild(String id, String name, int position) {
        Child child = new Child();
        child.setNameChild(name);

        Call<Void> call = api.updateChildI(id, child);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    ChildrenList.get(position).setNameChild(name);
                    Toast.makeText(context, "Nombre editado correctamente", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                } else {
                    Toast.makeText(context, "Ha ocurrido un error. Vuelva a intentarlo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Fallo al conectar con el servidor", Toast.LENGTH_LONG).show();
            }
        });


    }


    private void showDeleteNameDialog(int position) {
        Dialog dialog = new Dialog(appCompatActivity);
        Child child = ChildrenList.get(position);
        dialog.setContentView(R.layout.dialog_deletechild);
        Button btncanceldeletechild = dialog.findViewById(R.id.btncanceldeletechild);
        Button btnconfirmdelete = dialog.findViewById(R.id.btnconfirmdelete);

        btncanceldeletechild.setOnClickListener(v -> {
            dialog.dismiss();

        });

        btnconfirmdelete.setOnClickListener(v -> {
            deleteChild(child.getIdChild(), position);
            dialog.dismiss();

        });

        dialog.show();

    }

    private void showEditNameDialog(int position) {
        Dialog dialog = new Dialog(appCompatActivity);
        dialog.setContentView(R.layout.dialog_editchild);
        Child child = ChildrenList.get(position);

        EditText inputeditchild = dialog.findViewById(R.id.inputeditchild);
        Button btneditchild = dialog.findViewById(R.id.btnconfirmdelete);
        Button btncancelarchild = dialog.findViewById(R.id.btncanceldeletechild);

        inputeditchild.setText(child.getNameChild());
        btncancelarchild.setOnClickListener(v -> {
            dialog.dismiss();
        });

        btneditchild.setOnClickListener(v -> {
            String inputE = inputeditchild.getText().toString();
            if (!inputE.isEmpty()) {
                editChild(child.getIdChild(), inputE, position);
                dialog.dismiss();
            } else {
                Toast.makeText(context, "Campo vacío. Por favor ingrese un nombre.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }



}
