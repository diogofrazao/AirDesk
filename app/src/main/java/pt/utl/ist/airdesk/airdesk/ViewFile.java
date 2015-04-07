package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ViewFile extends ActionBarActivity {

    EditText fileTextView;
    String fileName;
    String path;
    String ambiente;
    String permission;
    Button editFile;
    Button saveFile;
    File file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_file);

        TextView fileNameView = (TextView) findViewById(R.id.editText);
        fileTextView = (EditText) findViewById(R.id.editText2);
        saveFile = (Button) findViewById(R.id.button5);

        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
        path = intent.getStringExtra("path");
        ambiente = intent.getStringExtra("ambiente");
        permission = intent.getStringExtra("permission");

        fileNameView.setText(fileName, TextView.BufferType.EDITABLE);

        File f = new File(path);
        file = new File(f,fileName);
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
        }

        fileTextView.setText(text, EditText.BufferType.EDITABLE);
        fileTextView.setEnabled(false);
        fileTextView.setClickable(false);
        saveFile.setEnabled(false);

    }


    public void onClickEdit(View view){

        if(ambiente.equals("local") || permission.equals("rw")) {
            fileTextView.setEnabled(true);
            fileTextView.setClickable(true);
            saveFile.setEnabled(true);
        }
        else{
            Toast.makeText(getApplicationContext(), "Nao tem permissao!",
                    Toast.LENGTH_LONG).show();
        }
    }


    public void onClickSave(View view){
        try
        {

            FileWriter writer = new FileWriter(file);
            writer.append(fileTextView.getText().toString());
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            //importError = e.getMessage();
            // iError();
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_file, menu);
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
