package com.natiqhaciyef.programmingbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.natiqhaciyef.programmingbook.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding ;
    static ArrayList<Programs> programsArrayList ;
    ProgramAdapter adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot() ;
        setContentView(view);

        programsArrayList = new ArrayList<>();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProgramAdapter(programsArrayList);
        binding.recyclerView.setAdapter(adapter);

        getData();

    }


    public void getData (){
        try{
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Programs",MODE_PRIVATE,null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM programs",null);

            int idIx = cursor.getColumnIndex("id");
            int nameIx = cursor.getColumnIndex("name");


            while (cursor.moveToNext()){
                int id = cursor.getInt(idIx);
                String name = cursor.getString(nameIx);

                Programs program = new Programs(id , name);
                programsArrayList.add(program);
            }

            adapter.notifyDataSetChanged();

            cursor.close();

        }catch(Exception e){
            e.printStackTrace();
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_lay , menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add) {
            Intent intent = new Intent(MainActivity.this, ProgrammingLanguages.class);
            intent.putExtra("info" , "new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}