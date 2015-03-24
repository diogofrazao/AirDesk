package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pt.utl.ist.airdesk.airdesk.Sqlite.WSDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WorkspaceRepresentation;


public class MainAirDesk extends ActionBarActivity {

    Button button;
    ListView listView;
    ListView listView2;
    ArrayList<String> listaWorkplacesPrivados;
    ArrayAdapter<WorkspaceRepresentation> listAdapter;
    private WSDataSource datasource;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_air_desk);

        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById( R.id.listView);
        listView2 = (ListView) findViewById( R.id.listView2);
        listaWorkplacesPrivados = new ArrayList<String>();

        datasource = new WSDataSource(this);
        datasource.open();

        List<WorkspaceRepresentation> values = datasource.getAllComments();
        listAdapter = new ArrayAdapter<WorkspaceRepresentation>(this, android.R.layout.simple_list_item_1, values);

        listView.setAdapter(listAdapter);


        button.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View arg0) {
                WorkspaceRepresentation workspaceRepresentation = null;
                String comments = "lol";
                Log.v("conadamae", "passou");

                workspaceRepresentation = datasource.createWorkspaceRepresentation(comments, "lol", "lol");

                //Intent intent = new Intent(MainAirDesk.this, CreateWorkSpace.class)
                Log.v("conadamae", "passou");
//                startActivityForResult(intent,1);

            }

        });


        /*String sFileName="teste3.txt";
        String sBody="lolol";

        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
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
*/

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_air_desk, menu);
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
