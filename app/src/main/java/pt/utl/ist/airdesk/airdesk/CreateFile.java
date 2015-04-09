package pt.utl.ist.airdesk.airdesk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pt.utl.ist.airdesk.airdesk.Sqlite.WSDataSource;


public class CreateFile extends ActionBarActivity {

    String path;
    EditText entryText;
    private WSDataSource datasource;
    final Context context = this;
    String workspace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_file);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        workspace = intent.getStringExtra("wsName");
        entryText = (EditText) findViewById(R.id.editTextCreateFile);
        Log.d("Files", "Path: " + path);

        //java.io.File file = new java.io.File("/storage/emulated/0/g");

        //long length = folderSize(file);

        //Log.d("TAMANHOOO", "TAMANHO: " + length);

        datasource = new WSDataSource(this);
        datasource.open();

    }



    public static long folderSize(File directory) {
        long length = 0;
        for (File file : directory.listFiles()) {
            if (file.isFile())
                length += file.length();
            else
                length += folderSize(file);
        }
        return length;
    }

    public void onClickSaveFile(View view){
    if(entryText.getText().toString().isEmpty()){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("No text entered");
        alert.setMessage("save anyways?");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                saveFile(dialog, input);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
        else {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save file");
        alert.setMessage("Insert file name");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                saveFile(dialog,input);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });

        alert.show();

    }
    }

    public void saveFile(DialogInterface dialog, EditText input){

        String value = input.getText().toString();
        if(value.isEmpty()){
            Toast.makeText(CreateFile.this, "Cannot save file without name", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }
        else {
            try
            {
                File f = new File(path);
                File file = new File(f,value);
                FileWriter writer = new FileWriter(file);
                writer.append(entryText.getText().toString());
                writer.flush();
                writer.close();

                long dirSize = folderSize(f);

                //long fileSize =file.length();

                Log.d("filesize",dirSize+"");

                Intent intent2 = new Intent(CreateFile.this, ViewWorkspace.class);

//                if(dirSize> 0){
                        if(dirSize/(1024*1024)> datasource.getWorkspaceStorage(workspace)){
                    Toast.makeText(CreateFile.this, "File not saved - quota exceeded", Toast.LENGTH_SHORT).show();
                    file.delete();
                    setResult(RESULT_CANCELED,intent2);

                }else{
                    Toast.makeText(CreateFile.this, "Saved", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK,intent2);

                }

                finish();
            }
            catch(IOException e)
            {
                e.printStackTrace();
                //importError = e.getMessage();
                // iError();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_file, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
