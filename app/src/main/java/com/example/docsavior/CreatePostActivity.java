package com.example.docsavior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

public class CreatePostActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private ImageButton btnPost;
    private TextView tvUsername;
    private EditText edPostDesciption;
    private EditText edPostContent;
    private Button btnPictureVideo;
    private Button btnFile;
    private boolean isFileChosen;
    // Request code for selecting a PDF document.
    private static final int FILE_SELECT_CODE = 0;
    private File file = null;
    private String fileData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        findViewByIds();

        setOnClickListeners();

        initVariables();
    }

    private void findViewByIds() {
        btnClose = findViewById(R.id.btnClose);
        btnPost = findViewById(R.id.btnPost);
        tvUsername = findViewById(R.id.tvUsername);
        edPostDesciption = findViewById(R.id.edPostDesciption);
        edPostContent = findViewById(R.id.edPostContent);
        btnPictureVideo = findViewById(R.id.btnPictureVideo);
        btnFile = findViewById(R.id.btnFile);
    }

    private void setOnClickListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPictureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: the same as btnFile but need to let user choose only video, image file type
            }
        });

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void initVariables() {
        tvUsername.setText(ApplicationInfo.username);
        isFileChosen = false;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d("URI: ", uri.toString());
                    // Get the path
                    String path = uri.getPath();
                    Log.d("PATH: ", path);
                    // Get the file instance
                    file = new File(path);

                    // get the byteArray of that file
                    byte[] byteArray = new byte[(int) file.length()];
                    try (FileInputStream inputStream = new FileInputStream(file)) {
                        inputStream.read(byteArray);
                    } catch (Exception ex) {
                        Log.e("ERROR: ", ex.getMessage());
                    }

                    // convert byteArray to string
                    fileData = new String(byteArray, StandardCharsets.UTF_8);
                    Log.i("str: ", fileData);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}