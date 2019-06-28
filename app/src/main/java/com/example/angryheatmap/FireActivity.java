package com.example.angryheatmap;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class FireActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast toast = Toast.makeText(getApplicationContext(),"Давайте определим объём сгоревшего леса", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, -250);
        toast.show();
    }

    public void goToDialog(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        dialog.setTitle("Вы готовы смириться с потерями?");
        dialog.setMessage("Сделайте ваш выбор!");
        dialog.setPositiveButton("Готов", null);
        dialog.setNegativeButton("Не смирюсь", null);
        dialog.show();
    }
}
