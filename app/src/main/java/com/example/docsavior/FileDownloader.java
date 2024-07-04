package com.example.docsavior;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;

public class FileDownloader extends AsyncTask<String, Long, Void> {
    private Context context;
    private Newsfeed newsfeed;
    private String fileFullName;


    public FileDownloader(Context context, Newsfeed newsfeed) {
        this.context = context;
        this.newsfeed = newsfeed;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            // create jsonArray to store fileData
            JSONArray jsonArray = new JSONArray(newsfeed.getFileData());
            // convert jsonArray to byteArray
            byte[] byteArray = new byte[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                int temp = (int)jsonArray.get(i);
                byteArray[i] = (byte)temp;
            }

            // save file to Download
            writeFileToDownloads(byteArray, newsfeed.getFileName(), newsfeed.getFileExtension());
        } catch (Exception ex) {
            Log.e("ERROR009: ", ex.getMessage());
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Long ...values) {
        super.onProgressUpdate();

        // notify user that the file is successfully downloaded
        Toast.makeText(context, fileFullName + " successfully downloaded to Downloads!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    private void writeFileToDownloads(byte[] array, String fileName, String fileExtension)
    {
        try
        {
            // get the path to Downloads folder plus file's name
            String pathToDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String pathToFile = pathToDownloads + "/" + fileName + "." + fileExtension;
            File file = new File(pathToFile);

            // create the file if it is not existed
            fileFullName = fileName + "." + fileExtension;
            if (file.exists()) {
                int i = 1;
                // Loop until the file is not exist
                do {
                    fileFullName = fileName + " (" + i + ")." + fileExtension;
                    pathToFile = pathToDownloads + "/" + fileName + " (" + i + ")." + fileExtension;
                    file = new File(pathToFile);
                    i++;
                } while (file.exists());
            }
            file.createNewFile();

            // write the file from byte array
            FileOutputStream stream = new FileOutputStream(pathToFile);
            stream.write(array);
            stream.close();

            publishProgress();
        } catch (Exception ex) {
            Log.e("ERROR1: ", ex.getMessage());
        }
    }
}
