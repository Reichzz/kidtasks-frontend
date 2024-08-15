package com.puce.kidtasks_ar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChildrenActivity extends AppCompatActivity {
    Button btnAddChild;
    TextView tvchildren;
    List<Child> ChildrenList = new ArrayList<>();
    ListView childLV;
    Retrofit retrofit;
    APIChild api;

    @Override
    protected void onResume() {
        super.onResume();
        checkStreaksForAllChildren();
    }


    private void checkStreaksForAllChildren() {
        for (Child child : ChildrenList) {
            showStreakDialog(child);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_children);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.children), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnAddChild = findViewById(R.id.btnAddChild);
        tvchildren = findViewById(R.id.tvchildren);


        childLV = (ListView) findViewById(R.id.childLV);
        ChildrenAdapter adapter = new ChildrenAdapter(this, ChildrenList);

        childLV.setAdapter(adapter);
        retrofit = new AdaptadorRetrofit().getAdaptador();
        api = retrofit.create(APIChild.class);
        getChild(api);


        btnAddChild.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showAddDialog();
            };
        });

//
    }


    private void getChild(APIChild api) {
        ChildrenList.clear();
        Call<List<Child>> call = api.getChildrenI();

        call.enqueue(new Callback<List<Child>>() {
            @Override
            public void onResponse(Call<List<Child>> call, Response<List<Child>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ChildrenList = new ArrayList<>(response.body());
                    ChildrenAdapter adapter = new ChildrenAdapter(ChildrenActivity.this, ChildrenList);
                    childLV.setAdapter(adapter);
                    checkStreaksForAllChildren();  // Verificar las rachas inmediatamente después de cargar los niños
                } else {
                    Log.e("API Error", "Response not successful or is empty");
                }
            }

            @Override
            public void onFailure(Call<List<Child>> call, Throwable t) {
                Log.e("TAG", "Error fetching children", t);
            }
        });
    }

    public void addChild(final APIChild api, String name) {
        Child child = new Child();
        child.setNameChild(name);

        Call<Void> call = api.addChildI(child);
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
                    Toast.makeText(ChildrenActivity.this, "Registro exitoso!", Toast.LENGTH_SHORT).show();
                    getChild(api);
                } else {
                    Toast.makeText(ChildrenActivity.this, "Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TAG", "Error al agregar "+t);
            }
        });
    }
    private void showAddDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_addchild);

        EditText inputname = dialog.findViewById(R.id.inputeditchild);
        Button btnCancelar = dialog.findViewById(R.id.btncanceldeletechild);
        Button btnguadar = dialog.findViewById(R.id.btnconfirmdelete);

        btnCancelar.setOnClickListener(v -> {
            inputname.setText("");
            dialog.dismiss();
        });

        btnguadar.setOnClickListener(v -> {
            String input = inputname.getText().toString();
            if (!input.isEmpty()) {
                addChild(api, input);
                dialog.dismiss();
            } else {
                Toast.makeText(ChildrenActivity.this, "Campo vacío. Por favor completarlo.", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
    private void showStreakDialog(Child child) {
        SharedPreferences prefs = getSharedPreferences("StreakPrefs", MODE_PRIVATE);
        int lastShownStreak = prefs.getInt("LastShownStreak_" + child.getIdChild(), 0);

        if (child.getStreakChild() >= 7 && lastShownStreak != child.getStreakChild()) {
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_reward, null);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView);

            // Configuración del mensaje en el TextView
            TextView rewardMessage = dialogView.findViewById(R.id.rewardmessage);
            String message = child.getNameChild() + " ha completado 7 días de rachas. ¡Es hora de una recompensa!";
            rewardMessage.setText(message);

            // Creación del diálogo
            final AlertDialog dialog = builder.create();

            // Configuración de los botones
            Button btnOk = dialogView.findViewById(R.id.btnok);
            btnOk.setOnClickListener(v -> {
                resetStreak(child.getIdChild());
                dialog.dismiss();
                prefs.edit().putInt("LastShownStreak_" + child.getIdChild(), child.getStreakChild()).apply();
            });

            dialog.show();
        }
    }


    private void resetStreak(String childId) {
        Call<Void> call = api.resetStreak(childId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Reinicia el valor en SharedPreferences
                    SharedPreferences prefs = getSharedPreferences("StreakPrefs", MODE_PRIVATE);
                    prefs.edit().putInt("LastShownStreak_" + childId, 0).apply();
                    getChild(api);  // Recargar los datos de los niños
                } else {
                    Toast.makeText(ChildrenActivity.this, "Error al reiniciar la racha.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(ChildrenActivity.this, "Fallo al conectar con el servidor.", Toast.LENGTH_LONG).show();
            }
        });
    }




}