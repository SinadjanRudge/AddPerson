package com.example.addperson;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {


    Button btnPickImage, Add, Delete, Update, Search;
    ImageView imageView;

    DatabaseReference rootDatabaseref, rootDatabaseremove, databaseReference;

    FirebaseDatabase database;
    EditText StudentID, FullName, Gender, Course, Year, Section, RoomNum;
    ActivityResultLauncher<Intent> resultLauncher;
    Toolbar toolbar;

    String imageURL;
    String imageUrl = " ";

    String oldImageURL;
    Uri uri, imageUri;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        rootDatabaseref = database.getReference();
        //rootDatabaseremove = database.getReference();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        StudentID = findViewById(R.id.textInputEditText);
        FullName  = findViewById(R.id.textInputEditText2);
        Gender    = findViewById(R.id.textInputEditText3);
        Course   = findViewById(R.id.textInputEditText4);
        Year    = findViewById(R.id.textInputEditText5);
        Section = findViewById(R.id.textInputEditText6);
        RoomNum = findViewById(R.id.textInputEditText7);

        btnPickImage = findViewById(R.id.buttonLoadPicture);
        Add = findViewById(R.id.buttonAdd);
        Delete = findViewById(R.id.buttonDelete);
        Update = findViewById(R.id.buttonUpdate);
        imageView = findViewById(R.id.loadimageView);

        Search = findViewById(R.id.buttonSearch);
        registerResult();

        btnPickImage.setOnClickListener(view -> pickImage());


        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, SearchUser.class);
                startActivity(intent);
            }
        });

        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

            readData(StudentID.getText().toString());
              //rootDatabaseref.child("Students").child(StudentID.getText().toString()).removeValue();
            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataUpdate(StudentID.getText().toString());
            }
        });

    }

    private void readData(String ID){

         rootDatabaseremove = FirebaseDatabase.getInstance().getReference("Students");
         rootDatabaseremove.child(ID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DataSnapshot> task) {

                 if (task.isSuccessful() && ID.trim().length() > 0){
                        DataSnapshot dataSnapshot = task.getResult();
                        String name = String.valueOf(dataSnapshot.child("dataName").getValue());
                        String image = String.valueOf(dataSnapshot.child("dataImage").getValue());
                        if (task.getResult().exists()){
                            android.app.AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);


                            alertDialog.setTitle("Deleting Student Info");
                            alertDialog.setMessage("ID: " + ID + "\n" + "Name: " + name);

                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference storageReference = storage.getReferenceFromUrl(image);
                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //reference.child(key).removeValue();
                                            Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                          //  finish();
                                        }
                                    });
                                    rootDatabaseref.child("Students").child(ID).removeValue();
                                    Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            });
                            alertDialog.show();
                        }else{
                            Toast.makeText(MainActivity.this, "Does not Exists", Toast.LENGTH_SHORT).show();
                        }
                 }

             }
         });
    }
    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult(){
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            imageUri = result.getData().getData();
                            imageView.setImageURI(imageUri);

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

        );
    }

    public void saveData(){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(imageUri.getLastPathSegment());
       // AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
       // builder.setCancelable(false);
       // builder.setView(R.layout.progress_layout);
       // AlertDialog dialog = builder.create();
       // dialog.show();
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageURL = urlImage.toString();
                uploadData();
              //  dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            //    dialog.dismiss();
            }
        });
    }

    public void uploadData(){
        String studentId = StudentID.getText().toString();
        String name = FullName.getText().toString();
        String gender = Gender.getText().toString();
        String course = Course.getText().toString();
        String year = Year.getText().toString();
        String section = Section.getText().toString();
        String room = RoomNum.getText().toString();
        DataClass dataClass = new DataClass(studentId, name, gender, course, year, section, room, imageURL);
        //We are changing the child from title to currentDate,
        // because we will be updating title as well and it may affect child value.
        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        FirebaseDatabase.getInstance().getReference("Students").child(studentId)
                .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                           // finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveDataUpdate(String ID){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Android Images")
                .child(imageUri.getLastPathSegment());

        rootDatabaseremove = FirebaseDatabase.getInstance().getReference("Students");
        rootDatabaseremove.child(ID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if (task.isSuccessful() && ID.trim().length() > 0){
                    DataSnapshot dataSnapshot = task.getResult();
                    String name = String.valueOf(dataSnapshot.child("dataName").getValue());
                    String image = String.valueOf(dataSnapshot.child("dataImage").getValue());
                    if (task.getResult().exists()){


                    }else{
                        Toast.makeText(MainActivity.this, "Does not Exists", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
        storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                updateData();
                //  dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //    dialog.dismiss();
            }
        });
    }
    public void updateData(){
        String studentId = StudentID.getText().toString();
        String name = FullName.getText().toString();
        String gender = Gender.getText().toString();
        String course = Course.getText().toString();
        String year = Year.getText().toString();
        String section = Section.getText().toString();
        String room = RoomNum.getText().toString();

        DataClass dataClass = new DataClass(studentId, name, gender, course, year, section, room, imageUrl);
        FirebaseDatabase.getInstance().getReference("Students").child(studentId)
        .setValue(dataClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
