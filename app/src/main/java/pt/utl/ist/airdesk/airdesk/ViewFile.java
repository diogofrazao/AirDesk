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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ViewFile extends ActionBarActivity {

    EditText fileTextView;
    String fileName;
    String path;
    Button editFile;
    Button saveFile;

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

        fileNameView.setText(fileName, TextView.BufferType.EDITABLE);

        File f = new File(path);
        File file = new File(f,fileName);
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


    public void onClick(View view){
        fileTextView.setEnabled(true);
        fileTextView.setClickable(true);
        saveFile.setEnabled(true);
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
