package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    ArrayAdapter<String> listAdapter;
    ArrayAdapter<String> listAdapter2;
    private WSDataSource datasource;
    private ArrayList<String> values;
    private ArrayList<String> values2;
    String filename;
    String maxSize;
    String login;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_air_desk);

        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listView);
        listView2 = (ListView) findViewById(R.id.listView2);
        listaWorkplacesPrivados = new ArrayList<String>();



        datasource = new WSDataSource(this);
        datasource.open();


        final Intent intent = getIntent();

        login = intent.getStringExtra("login");

        values2 = datasource.GetAllValues(login);


        values = new ArrayList<String>();



        String path = Environment.getExternalStorageDirectory().toString()+"/"+login;
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        Log.d("Files", "Size: "+ file.length);
        for (int i=0; i < file.length; i++)
        {
            values.add(file[i].getName());
        }


        listAdapter = new ArrayAdapter<String>(this,  R.layout.mylistfolder ,R.id.ItemnameFolder, values);
        listAdapter2 = new ArrayAdapter<String>(this,  R.layout.mylistfolder ,R.id.ItemnameFolder, values2);

        //values = datasource.getAllComments();
        //listAdapter = new ArrayAdapter<WorkspaceRepresentation>(this, android.R.layout.simple_expandable_list_item_1, values);


        listView.setAdapter(listAdapter);
        listView2.setAdapter(listAdapter2);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainAirDesk.this, ViewWorkspace.class);
                intent.putExtra("wsName",values.get(position));
                intent.putExtra("login",login);
                intent.putExtra("ambiente","local");
                startActivity(intent);
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String owner;
                Intent intent = new Intent(MainAirDesk.this, ViewWorkspace.class);
                owner = datasource.getOwner(values2.get(position));
                intent.putExtra("wsName",values2.get(position));
                intent.putExtra("login",owner);
                intent.putExtra("ambiente","publico");

                startActivity(intent);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {
                //WorkspaceRepresentation workspaceRepresentation = null;
                String comments = "teste2";
                Log.v("teste", "passou");


                //workspaceRepresentation = datasource.createWorkspaceRepresentation(comments, "lol", "lol");
                //listAdapter.add(workspaceRepresentation);

                Intent intent = new Intent(MainAirDesk.this, CreateWorkSpace.class);
                intent.putExtra("login",login);

                startActivityForResult(intent, 1);

            }

        });
    }


        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {
                    Intent intent = getIntent();
                    //WorkspaceRepresentation workspaceRepresentation = null;
                    String comments = "teste2";


                    filename = data.getStringExtra("titles");
                    maxSize = data.getStringExtra("contents");

                    //workspaceRepresentation = datasource.createWorkspaceRepresentation(filename, "lol", "lol");
                    listAdapter.add(filename);

                    //values.add(filename);
                    //contents.add(conteudo);
                    listAdapter.notifyDataSetChanged();
                    values2.clear();
                    values2 = datasource.GetAllValues(login);
                    listAdapter2 = new ArrayAdapter<String>(this,  R.layout.mylistfolder ,R.id.ItemnameFolder, values2);
                    listView2.setAdapter(listAdapter2);

                }
            }
        }


     /*   String sFileName="teste3.txt";
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

    /*
    public static long getFolderSize(File dir) {
    long size = 0;
    for (File file : dir.listFiles()) {
        if (file.isFile()) {
            System.out.println(file.getName() + " " + file.length());
            size += file.length();
        }
        else
            size += getFolderSize(file);
    }
    return size;
}
     */



    public void resetDatabase(View view){
    datasource.resetDatabase();
    values2.clear();
    listAdapter2.notifyDataSetChanged();
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
