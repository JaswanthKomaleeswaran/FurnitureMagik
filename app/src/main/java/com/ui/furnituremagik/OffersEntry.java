package com.ui.furnituremagik;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class OffersEntry extends AppCompatActivity {
    private ImageButton offerImage;
    private EditText productNameText,priceText,discountPriceText;
    private Button backButton, saveButton;
    private Spinner spinnerItem;
    private Intent intent;
    private SQLiteDatabase sqLiteDatabase;
    private String sql;
    private String[] offerItem;
    private ArrayAdapter arrayAdapter;
    private Uri uri;
    private Bitmap bitmap;
    private File directory, path;
    static final int PICK_IMAGE_REQUEST = 1;
    ValidationClass validationClass;
    int imageStatus;

    String offer_id, product_name, item, price, discount_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers_entry);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.app_name));
            actionBar.setSubtitle(getResources().getString(R.string.offers_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        offerItem = new String[]{"Chair", "Table", "Sofa", "WFH", "Other"};
        spinnerItem = findViewById(R.id.spinner_item);
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,offerItem);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerItem.setAdapter(arrayAdapter);
        spinnerItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                item=offerItem[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        imageStatus=0;
        validationClass=new ValidationClass();
        sqLiteDatabase=openOrCreateDatabase(getResources().getString(R.string.db_name), Context.MODE_PRIVATE,null);
        try
        {

            sql="CREATE TABLE IF NOT EXISTS offers_table " +
                    "(offerId TEXT PRIMARY KEY,productName TEXT," +
                    "productItem TEXT,price NUMERIC,discountPrice NUMERIC,imagePath TEXT)";
//            sql="DROP TABLE offers_table";

            sqLiteDatabase.execSQL(sql);

        }catch (Exception e){}



        offerImage=findViewById(R.id.offer_image);
        productNameText=findViewById(R.id.product_name);
        priceText=findViewById(R.id.price);
        discountPriceText=findViewById(R.id.discount_price);
        backButton=findViewById(R.id.back);
        saveButton=findViewById(R.id.save);
        

        offerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                offer_id = UUID.randomUUID().toString();
                offer_id=offer_id.replaceAll("-","");
                product_name=productNameText.getText().toString();
                price=priceText.getText().toString();
                discount_price=discountPriceText.getText().toString();
                if(imageStatus==0)
                {
                    Snackbar.make(view, getResources().getString(R.string.image_error), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                else if(product_name.trim().length()==0)
                {
                    productNameText.setError(getResources().getString(R.string.productname_error));
                    productNameText.requestFocus();
                }
                else if(!validationClass.priceValidation(price))
                {
                    priceText.setError(getResources().getString(R.string.price_error));
                    priceText.requestFocus();
                }
                else if(!validationClass.priceValidation(discount_price))
                {
                    discountPriceText.setError(getResources().getString(R.string.price_error));
                    discountPriceText.requestFocus();
                }
                else {
                    final Thread tableInsert = new Thread() {
                        public void run() {
                            try {
                                sql = "INSERT INTO offers_table values('" + offer_id + "','" + product_name + "','" + item + "'," + price + "," + discount_price + ",'" + path.toString() + "')";
                                sqLiteDatabase.execSQL(sql);
                            } catch (Exception e) {
                            }
                        }
                    };
                    Thread saveImage = new Thread() {
                        public void run() {
                            saveImageStorage(bitmap);
                            tableInsert.start();
                        }
                    };
                    saveImage.start();
                    uri = null;
                    imageStatus=0;
                    offerImage.setImageResource(R.drawable.ic_baseline_add_a_photo_24);
                    productNameText.setText("");
                    spinnerItem.setSelection(0);
                    priceText.setText("");
                    discountPriceText.setText("");
                    Snackbar.make(view, "Item created", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    productNameText.requestFocus();
                }

            }
        });



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    public void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                offerImage.setImageBitmap(bitmap);
                imageStatus=1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveImageStorage(Bitmap bitmapImage){
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        directory = contextWrapper.getDir("imageStorage", Context.MODE_PRIVATE);
        path=new File(directory,offer_id+".png");

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(path);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        intent=new Intent(OffersEntry.this,MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}