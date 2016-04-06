package com.example.java.opencamera;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;

    Button b1, b2;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b1=(Button)findViewById(R.id.button); //button for open camera
        b2=(Button)findViewById(R.id.button2); //button for open gallery
        iv=(ImageView)findViewById(R.id.imageView);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //intent to open the camera
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //intent to open the gallery
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); //take the image from the external store
                startActivityForResult(galleryIntent , RESULT_LOAD_IMG );

            }

    });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){

            case 0:
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                iv.setImageBitmap(bp);

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/OpenCamera");
                if (myDir.mkdirs()) {
                    Toast.makeText(getApplicationContext(), "no good", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "good", Toast.LENGTH_LONG).show();
                }
                String FileNameOut = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss").format(new Date()); //take the local date, hour, minute, second

                String fname = "image-"+FileNameOut+".jpg"; //create the name
                File file = new File(myDir, fname); //assign the name
                try {
                    FileOutputStream out = new FileOutputStream(file);
                    bp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    // When an Image is picked
                    if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                            && null != data) {

                        // Get the Image from data
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = { MediaStore.Images.Media.DATA };

                        // Get the cursor
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        // Move to first row
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        imgDecodableString = cursor.getString(columnIndex);
                        cursor.close();
                        ImageView imgView = (ImageView) findViewById(R.id.imgView);
                        // Set the Image in ImageView after decoding the String
                        imgView.setImageBitmap(BitmapFactory
                                .decodeFile(imgDecodableString));

                    } else {
                        Toast.makeText(this, "You haven't picked Image",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                            .show();
                }
                break;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
