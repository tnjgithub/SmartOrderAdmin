package com.hanibey.smartorder.administration.admin;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hanibey.smartorder.administration.R;
import com.hanibey.smartorderbusiness.LogService;
import com.hanibey.smartorderhelper.Constant;
import com.hanibey.smartordermodel.Product;
import com.hanibey.smartorderrepository.FirebaseDb;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductRegisterActivity extends AppCompatActivity {

    private DatabaseReference productRef;
    private StorageReference storageRef;
    private LogService logService;

    ImageButton btnUploadImage;
    EditText edtTitle, edtPrice, edtDescription, edtFileName;
    Spinner spCategory;
    Uri selectedImageUri;

    public List<String> categoryKeyList;
    public String selectedCategoryKey = "";
    public String selectedProductKey = "";
    public static final ProductRegisterActivity instance = new ProductRegisterActivity();
    public static ProductRegisterActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_register);

        productRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Product);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        logService = new LogService();

        edtTitle = (EditText) findViewById(R.id.edt_title);
        edtPrice = (EditText) findViewById(R.id.edt_price);
        edtDescription = (EditText) findViewById(R.id.edt_description);
        edtFileName = (EditText) findViewById(R.id.edt_file_name);
        spCategory = (Spinner)findViewById(R.id.category_Spinner);

        setCategoryToSpinner();

        btnUploadImage = (ImageButton)findViewById(R.id.btn_upload_image);
        btnUploadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, Constant.IMAGE_PICK);
            }
        });

        Button btnSaveProduct = (Button)findViewById(R.id.btnSaveProduct);
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkFields();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if(requestCode == Constant.IMAGE_PICK && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 300);
                edtFileName.setText(selectedImageUri.toString());
                //btnUploadImage.setImageBitmap(selectedImage);
            }
        } catch (Exception ex) {

            DateFormat df = new android.text.format.DateFormat();
            String date = df.format("dd.MM.yyyy hh:mm", new Date()).toString();
            String className = "ProductRegisterActivity";
            String metodName = "onActivityResult";
            logService.saveLog(date, className, metodName, ex.toString());

            ex.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private  void setCategoryToSpinner(){

        try{
            DatabaseReference categoryRef = FirebaseDb.getDatabase().getReference(Constant.Nodes.Category);
            categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    categoryKeyList = new ArrayList<String>();
                    List<String> categoryNameList = new ArrayList<String>();

                    for(DataSnapshot pos : dataSnapshot.getChildren()){
                        String name = pos.child("Name").getValue(String.class);
                        categoryNameList.add(name);
                        categoryKeyList.add(pos.getKey());
                    }

                    ArrayAdapter dataAdapterTypes = new ArrayAdapter<>(ProductRegisterActivity.this, android.R.layout.simple_spinner_item, categoryNameList);
                    dataAdapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategory.setAdapter(dataAdapterTypes);

                    if(!ProductRegisterActivity.getInstance().selectedProductKey.equals("")){
                        String cKey = ProductRegisterActivity.getInstance().selectedCategoryKey;
                        String pKey = ProductRegisterActivity.getInstance().selectedProductKey;
                        SetDataToEditText(cKey, pKey);
                    }

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    showAlert(getString(R.string.unexpected_error));
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });
        }
        catch (Exception ex){
            saveLog("onActivityResult(catch)", ex.toString());
            showAlert(getString(R.string.unexpected_error));
        }

    }

    private  void checkFields(){
        try {
            if(TextUtils.isEmpty(edtTitle.getText()) || TextUtils.isEmpty(edtPrice.getText()))
            {
                showAlert("Zorunlu alanlar: başlık, fiyat");
            }
            else {
                String productKey ="";
                if(ProductRegisterActivity.getInstance().selectedProductKey.equals("")){
                    if(selectedImageUri == null){
                        showAlert("Lütfen ürün fotoğrafı yükleyin!");
                    }
                    else {
                        productKey = productRef.push().getKey();
                        uploadImageToStorage(productKey, "");
                    }
                }
                else {

                    final String pKey = ProductRegisterActivity.getInstance().selectedProductKey;
                    final String cKey = ProductRegisterActivity.getInstance().selectedCategoryKey;

                    productRef.child(cKey).child(pKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String imageName = dataSnapshot.child("ImageName").getValue(String.class);
                            String imageUrl = dataSnapshot.child("ImageUrl").getValue(String.class);

                            if(selectedImageUri == null){
                               saveProduct(pKey, imageName, imageUrl, cKey);
                            }
                            else {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageRef = storage.getReference();
                                StorageReference imgRef = storageRef.child(Constant.IMAGE_FOLDER + imageName);
                                imgRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("Success","image deleted");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Log.e("Error","image not delete");
                                    }
                                });

                                uploadImageToStorage(pKey, cKey);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            showAlert(getString(R.string.unexpected_error));
                            Log.w("Error", "Failed to read value.", error.toException());
                        }
                    });
                }

            }
        }
        catch (Exception ex){
            saveLog("uploadProduct", ex.toString());
            showAlert(getString(R.string.unexpected_error));
            Log.w("Hata", ex);
        }
    }

    @SuppressWarnings("VisibleForTests")
    private void uploadImageToStorage(String pKey, String cKey){

        final String productKey = pKey;
        final String categoryKey = cKey;
        final ProgressDialog progressDialog = new ProgressDialog(this);

        try {
            final String imageName = productKey + selectedImageUri.getLastPathSegment();
            StorageReference imageRef = storageRef.child(Constant.IMAGE_FOLDER + imageName);

            progressDialog.setMax(100);
            progressDialog.setMessage("Ürün yükleniyor, lütfen bekleyin...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
            progressDialog.setCancelable(false);
            //starting upload
            UploadTask uploadTask = imageRef.putFile(selectedImageUri);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    //sets and increments value of progressbar
                    progressDialog.incrementProgressBy((int) progress);
                }
            });
            // Register observers to listen for when the download is done or if it fails
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    showAlert(exception.toString());
                    progressDialog.dismiss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    saveProduct(productKey, imageName, downloadUrl.toString(), categoryKey);
                    progressDialog.dismiss();
                }
            });
        }
        catch (Exception ex){
            saveLog("uploadImageToStorage", ex.toString());
            showAlert(getString(R.string.unexpected_error));
            progressDialog.dismiss();
        }
    }

    private void saveProduct(String productKey, String imageName, String imageUrl, String oldCategoryKey){

        try{

            String title = edtTitle.getText().toString();
            double price = Double.valueOf(edtPrice.getText().toString());
            String description = edtDescription.getText().toString();
            String categoryKey = categoryKeyList.get(spCategory.getSelectedItemPosition());

            Product product = new Product(productKey, categoryKey, title, price, description, imageName, imageUrl);

            if(ProductRegisterActivity.getInstance().selectedProductKey.equals("")){//new record
                productRef.child(categoryKey).child(productKey).setValue(product);
                Log.w("Mesaj", "Yeni kayıt eklendi");
            }
            else  if(!oldCategoryKey.equals("") && !oldCategoryKey.equals(categoryKey)){ //if categori changed
                productRef.child(oldCategoryKey).child(productKey).removeValue();
                productRef.child(categoryKey).child(productKey).setValue(product);
                Log.w("Mesaj", "kategori değişti");
            }
            else {
                Map<String, Object> postValues = product.toMap();

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(productKey, postValues);
                productRef.child(product.CategoryKey).updateChildren(childUpdates);
                Log.w("Mesaj", "kayıt güncellendi");
                ProductRegisterActivity.getInstance().selectedProductKey = "";
                ProductRegisterActivity.getInstance().selectedCategoryKey = "";
            }

            openFragment(Constant.Fragments.Product);
        }
        catch (Exception ex){
            saveLog("saveProduct", ex.toString());
            showAlert(getString(R.string.unexpected_error));
        }


    }

    public void openFragment(int index){
        AdminMainActivity.getInstance().selectedFragmentIndex = index;
        Intent intent = new Intent(this, AdminMainActivity.class);
        startActivity(intent);
    }

    private  void SetDataToEditText(String categoryKey, String productKey){

        try{
            productRef.child(categoryKey).child(productKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Product product = dataSnapshot.getValue(Product.class);
                    edtTitle.setText(product.Title);
                    edtPrice.setText(String.valueOf(product.Price));
                    edtDescription.setText(product.Description);
                    spCategory.setSelection(categoryKeyList.indexOf(product.CategoryKey));
                    edtFileName.setText(product.ImageUrl);
                    //Picasso.with(ProductRegisterActivity.this).load(product.ImageUrl).into(btnUploadImage);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    showAlert(getString(R.string.unexpected_error));
                    Log.w("Error", "Failed to read value.", error.toException());
                }
            });
        }
        catch (Exception ex){
            saveLog("saveProduct", ex.toString());
            showAlert(getString(R.string.unexpected_error));
        }

    }

    private  void showAlert(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void saveLog(String methodName, String message){
        DateFormat df = new android.text.format.DateFormat();
        String date = df.format("dd.MM.yyyy hh:mm", new Date()).toString();
        String className = "ProductRegisterActivity";

        logService.saveLog(date, className, methodName, message);
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}
