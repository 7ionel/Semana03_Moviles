package com.example.semana02;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.semana02.adapter.UserAdapter;
import com.example.semana02.entity.Address;
import com.example.semana02.entity.User;
import com.example.semana02.service.ServiceUser;
import com.example.semana02.util.ConnectionRest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // ListView y Adapter
    ListView lstUser;
    ArrayList<User> listaUser = new ArrayList<User>();
    UserAdapter userAdapter;

    // Conecta al servicio REST
    ServiceUser serviceUser;

    private List<User> listaTotalUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lstUser = findViewById(R.id.lstUsuarios);
        userAdapter = new UserAdapter(this, R.layout.user_item, listaUser);
        lstUser.setAdapter(userAdapter);

        // Conecta al servicio REST
        serviceUser = ConnectionRest.getConnecion().create(ServiceUser.class);

        // Llama al método para cargar usuarios cuando se hace clic en el botón
        findViewById(R.id.btnFiltrar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargaUsuarios();
            }
        });

        // Establece clics de escucha para los elementos de la lista
        lstUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Obtén el usuario seleccionado
                User selectedUser = listaUser.get(position);
                // Obtén la dirección del usuario seleccionado
                Address address = selectedUser.getAddress();
                // Muestra un mensaje con la información de la dirección
                showMessageDialog("Dirección", getAddressInfo(address));
            }
        });
    }

    // Método para cargar usuarios desde el servicio REST
    void cargaUsuarios() {
        Call<List<User>> call = serviceUser.listausuarios();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()) {
                    listaTotalUsuarios = response.body();
                    listaUser.clear();
                    listaUser.addAll(listaTotalUsuarios);
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                // Manejar el error
            }
        });
    }

    // Método para obtener la información de la dirección
    private String getAddressInfo(Address address) {
        return "Calle: " + address.getStreet() + "\n" +
                "Suite: " + address.getSuite() + "\n" +
                "Ciudad: " + address.getCity() + "\n" +
                "Código Postal: " + address.getZipcode();
    }

    // Método para mostrar un diálogo con el título y el mensaje dados
    private void showMessageDialog(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
}
