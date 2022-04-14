package com.natiqhaciyef.programmingbook;

import static com.natiqhaciyef.programmingbook.MainActivity.programsArrayList;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.natiqhaciyef.programmingbook.databinding.ActivityProgrammingLanguagesBinding;

import java.io.ByteArrayOutputStream;

public class ProgrammingLanguages extends AppCompatActivity {

    private ActivityProgrammingLanguagesBinding binding ;
    Bitmap selectedImage ;
    Programs program ;
    ActivityResultLauncher<Intent> activityResultLauncher ;
    ActivityResultLauncher<String> permissionLauncher ;
    SQLiteDatabase database ;
    Integer programId ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProgrammingLanguagesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLaunch();
        database = this.openOrCreateDatabase("Programs" , MODE_PRIVATE ,null);

        Intent intent = getIntent() ;
        String choice = intent.getStringExtra("info");

        if(choice.equals("new")){
            binding.langName.setText("");
            binding.langUsage.setText("");
            binding.save.setVisibility(View.VISIBLE);
            binding.delete.setVisibility(View.INVISIBLE);
            binding.imageView.setImageResource(R.drawable.selectimage);

        }else {
            programId = intent.getIntExtra("programId" , 0);
            binding.save.setVisibility(View.INVISIBLE);
            binding.delete.setVisibility(View.VISIBLE);

            try{
                Cursor cursor = database.rawQuery("SELECT * FROM programs WHERE id = ?",new String[] {String.valueOf(programId)});
                int nameIx = cursor.getColumnIndex("name");
                int workspaceIx = cursor.getColumnIndex("workspace");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext()){
                    binding.langName.setText(cursor.getString(nameIx));
                    binding.langUsage.setText(cursor.getString(workspaceIx));

                    byte [] imageByteArray = cursor.getBlob(imageIx);
                    Bitmap bitmapImage = BitmapFactory.decodeByteArray(imageByteArray , 0 , imageByteArray.length);

                    binding.imageView.setImageBitmap(bitmapImage);

                }


            }catch(Exception e){
                e.printStackTrace();

            }

        }


    }


    public void delete (View view){

        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS programs (id INTEGER PRIMARY KEY , name VARCHAR , workspace VARCHAR , image BLOB)");

            String deleteQuery = "DELETE FROM programs WHERE id = ?";
            SQLiteStatement sqLiteStatement = database.compileStatement(deleteQuery);
            sqLiteStatement.bindLong(1 , programId);


            for (int i = 0 ; i < programsArrayList.size() ; i++){
                if (programId == programsArrayList.get(i).id)
                    programsArrayList.remove(i);
            }

            sqLiteStatement.execute();
            Intent intent = new Intent(ProgrammingLanguages.this , MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);        // arxa fondaki activitini silmek ucun
            startActivity(intent);

        }catch(Exception e){
            e.printStackTrace();

        }



    }




    public void save(View view){
        String name = binding.langName.getText().toString() ;
        String workspace = binding.langUsage.getText().toString() ;

        Bitmap imageSmall = imageZoomOut(selectedImage , 300) ;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        imageSmall.compress(Bitmap.CompressFormat.PNG , 50 , outputStream) ;
        byte [] imageByte = outputStream.toByteArray();

        try{
            database.execSQL("CREATE TABLE IF NOT EXISTS programs (id INTEGER PRIMARY KEY , name VARCHAR , workspace VARCHAR , image BLOB)");

            String insertQuery = "INSERT INTO programs (name , workspace , image) VALUES ( ? , ? , ?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(insertQuery);
            sqLiteStatement.bindString(1 , name);
            sqLiteStatement.bindString(2 , workspace);
            sqLiteStatement.bindBlob(3 , imageByte);
            sqLiteStatement.execute();

        }catch(Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(ProgrammingLanguages.this , MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    public void chooseImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                Snackbar.make(view,"If you want to show photo , you should give permission",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }else{
                //permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }


        }else{
            //go to gallery (permission granted)
            Intent intentToGallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
            activityResultLauncher.launch(intentToGallery);
        }

    }


    public Bitmap imageZoomOut(Bitmap image , int maxSize){
        int width = image.getWidth() ;
        int height = image.getHeight() ;

        float bitmapRatio = (float) width / (float) height ;
        if (bitmapRatio > 1){
            // Landscape image (vertikal)
            width = maxSize ;
            height = (int) (width / bitmapRatio) ;

        }else if (bitmapRatio < 1) {
            // Portrait image (horizontal)
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }


        return image.createScaledBitmap(image , width , height , true) ;
    }




    private void registerLaunch(){

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode() == RESULT_OK){
                    Intent intentFromResult = result.getData() ;
                    if(intentFromResult != null){
                        Uri imageData = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);

                        try{
                            //Bitmapa cevirilir
                            ImageDecoder.Source sourceImage = ImageDecoder.createSource(getContentResolver(),imageData);
                            selectedImage = ImageDecoder.decodeBitmap(sourceImage);
                            binding.imageView.setImageBitmap(selectedImage);

                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }

                }else{

                }

            }
        });


        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
                    activityResultLauncher.launch(intentToGallery);
                } else {
                    // permission denied
                    Toast.makeText(ProgrammingLanguages.this ,"Permission denied",Toast.LENGTH_LONG).show();
                }


            }
        });

    }





}