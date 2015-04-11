package pt.utl.ist.airdesk.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private Button button;
    private ListView listView;
    private ListView listView2;
    private ArrayList<String> listaWorkplacesPrivados;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> listAdapter2;
    private WSDataSource datasource;
    private ArrayList<String> values;
    private ArrayList<String> values2;
    private String filename;
    private String maxSize;
    private String login;



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

        final String path = Environment.getExternalStorageDirectory().toString()+"/"+login;
        File f = new File(path);
        File file[] = f.listFiles();

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



        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(MainAirDesk.this);

                alert.setTitle("Delete file");
                alert.setMessage("Do you want to delete file?");

// Set an EditText view to get user input


                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        String pathDir = path+"/"+values.get(position).toString();

                        File dir = new File(pathDir);
                        if (dir.isDirectory()) {
                            String[] children = dir.list();
                            for (int i = 0; i < children.length; i++) {
                                new File(dir, children[i]).delete();
                            }
                            dir.delete();
                        }

                        //checks if it´s a shared workspace and deletes it from database
                        String ifExistsShared = datasource.workSpaceOnTable(values.get(position).toString());

                        if(!(ifExistsShared==null )){
                            datasource.deleteWorkspaceEntry(values.get(position).toString());
                            values2.remove(values.get(position).toString());
                            //values2 = datasource.GetAllValues(login);
                            listAdapter2.notifyDataSetChanged();
                        }

                        values.remove(position);
                        listAdapter.notifyDataSetChanged();

                        dialog.dismiss();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

                alert.show();
                return true;
            }
        });




        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {
                //WorkspaceRepresentation workspaceRepresentation = null;
                String comments = "teste2";

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
                    refreshForeignList();

                }
            }
        }

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


    public void refreshForeignList(){
        values2.clear();
        values2 = datasource.GetAllValues(login);
        listAdapter2 = new ArrayAdapter<String>(this,  R.layout.mylistfolder ,R.id.ItemnameFolder, values2);
        listView2.setAdapter(listAdapter2);
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        refreshForeignList();


    }


}
