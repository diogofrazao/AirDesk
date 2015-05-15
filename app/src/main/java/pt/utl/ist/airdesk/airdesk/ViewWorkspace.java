package pt.utl.ist.airdesk.airdesk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.airdesk.airdesk.Sqlite.UsersDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WSDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WSPermissionSource;
import pt.utl.ist.airdesk.airdesk.datastructures.DeviceInformation;
import pt.utl.ist.airdesk.airdesk.datastructures.FileDeleteRequest;
import pt.utl.ist.airdesk.airdesk.datastructures.FileDeleteResponse;
import pt.utl.ist.airdesk.airdesk.datastructures.FileLockRequest;
import pt.utl.ist.airdesk.airdesk.datastructures.FileLockResponse;
import pt.utl.ist.airdesk.airdesk.datastructures.FileResponse;
import pt.utl.ist.airdesk.airdesk.datastructures.FileResponseAlteration;
import pt.utl.ist.airdesk.airdesk.datastructures.WorkspaceRepToBeSent;
import pt.utl.ist.airdesk.airdesk.datastructures.WorkspacesShared;


public class ViewWorkspace extends ActionBarActivity {


    private GridView listView;
    private ArrayList<String> filesList;
    private ArrayAdapter<String> listAdapter;
    String path;
    File f;
    private WSDataSource datasourceWorkspace;
    private WSPermissionSource datasourcePermissions;
    private String permission;
    private String workspace;
    private String loginWorkspace;
    private String ambiente;
    Intent intent;
    DeviceInformation deviceInformation;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workspace);
        listView = (GridView) findViewById(R.id.listView3);
        TextView editText = (TextView) findViewById(R.id.editText);
        filesList = new ArrayList<String>();
        intent = getIntent();
        final String name = intent.getStringExtra("wsName");
        final String name2 = intent.getStringExtra("ambiente");
        ambiente = name2;
        workspace = name;
        login = intent.getStringExtra("login");



        //listAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, filesList);
       // listView.setAdapter(listAdapter);

        datasourceWorkspace = new WSDataSource(this);
        datasourceWorkspace.open();
        datasourcePermissions = new WSPermissionSource(this);
        datasourcePermissions.open();

        if(ambiente.equals("local")) {
            path = Environment.getExternalStorageDirectory().toString() + "/" + login + "/" + name;
            f = new File(path);
            File file[] = f.listFiles();

            if (!(file == null)) {
                for (int i = 0; i < file.length; i++) {
                    filesList.add(file[i].getName());
                }
            }
        }else{
            deviceInformation = (DeviceInformation) intent.getSerializableExtra("deviceInformation");
            Intent viewFile = new Intent(ViewWorkspace.this,ViewForeignFile.class);

            filesList = intent.getStringArrayListExtra("list");


        }
        listAdapter = new ArrayAdapter<String>(this, R.layout.mylistfile ,R.id.Itemname, filesList);
        listView.setAdapter(listAdapter);
        editText.setText(name, TextView.BufferType.EDITABLE);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(ambiente.equals("local")) {
                    permission = datasourcePermissions.getPermission(workspace, loginWorkspace);
                    Intent viewFile = new Intent(ViewWorkspace.this, ViewFile.class);
                    viewFile.putExtra("fileName", filesList.get(position));
                    viewFile.putExtra("path", path);
                    viewFile.putExtra("ambiente", ambiente);
                    viewFile.putExtra("permission", permission);
                    viewFile.putExtra("wsName", workspace);
                    startActivity(viewFile);
                }
                else{
                    Intent viewFile = new Intent(ViewWorkspace.this, ViewForeignFile.class);
                    viewFile.putExtra("deviceInformation",deviceInformation);
                    viewFile.putExtra("fileName", filesList.get(position));
                    viewFile.putExtra("wsName", workspace);
                    viewFile.putExtra("login",login);
                    startActivity(viewFile);

                }
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(ViewWorkspace.this);

                alert.setTitle("Delete file");
                alert.setMessage("Do you want to delete file?");

// Set an EditText view to get user input


                alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (ambiente.equals("local")) {
                            File fileToDelete = new File(path, filesList.get(position).toString());
                            fileToDelete.delete();
                            filesList.clear();
                            File file[] = f.listFiles();
                            if (!(file == null)) {
                                for (int i = 0; i < file.length; i++) {
                                    filesList.add(file[i].getName());
                                }
                            }
                            listAdapter.notifyDataSetChanged();
                            dialog.dismiss();

                        } else {

                            final File fileToDelete = new File(path, filesList.get(position).toString());
                            Thread t = new Thread() {
                                public void run() {

                                    try {

                                        Log.v("conadamae", "antes");

                                        FileDeleteRequest fileDeleteRequest = new FileDeleteRequest(fileToDelete,login,workspace);


                                        final SimWifiP2pSocket s = new SimWifiP2pSocket(deviceInformation.getIp(), deviceInformation.getPort());

                                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                                        oos.writeObject(fileDeleteRequest);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                new ReceiveFileCommTask().executeOnExecutor(
                                                        AsyncTask.THREAD_POOL_EXECUTOR, s);
                                            }
                                        });



                                        //mCliSocket.getOutputStream().write( ("hello world" + "\n").getBytes());

                                        Log.v("conadamae", "depois");

                                        //mCliSocket.getOutputStream().write( ("hello world" + "\n").getBytes());

                                        Log.v("conadamae", "depois");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            };
                            t.start();
                        }
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


    }

    public void onClickCreateFile(View view){

        permission = datasourcePermissions.getPermission(workspace, loginWorkspace);

        if(ambiente.equals("local")) {
            Intent createFile = new Intent(ViewWorkspace.this,CreateFile.class);
            createFile.putExtra("wsName", workspace);
            createFile.putExtra("path", path);
            startActivityForResult(createFile,1);
        }
        else if(ambiente.equals("publico") && permission.equals("rw")) {
            Intent createFile = new Intent(ViewWorkspace.this,CreateFile.class);
            createFile.putExtra("wsName", workspace);
            createFile.putExtra("path", path);
            startActivityForResult(createFile,1);

        }

        else{
            Toast.makeText(getApplicationContext(), "You dont have permission!",
                    Toast.LENGTH_LONG).show();
        }





    }

    public void onClickInviteUser(View view) {
        if(ambiente.equals("publico")){
            Toast.makeText(getApplicationContext(), "You dont have permission!",
                    Toast.LENGTH_LONG).show();
        }
else {

            final CharSequence[] items = {"Modify","Create","Delete"};
            final ArrayList selectedItems = new ArrayList();

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Invite user");


            alert.setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    if (isChecked) {
                        selectedItems.add(which);


                    } else if (selectedItems.contains(which)) {
                        selectedItems.remove(Integer.valueOf(which));
                    }
                }
            });

            //alert.setMessage("Name of the user");


            final EditText input = new EditText(this);
            input.setHint("Insert username to share here");
            alert.setView(input);

            alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    StringBuilder stringBuilder = new StringBuilder();


                    if (selectedItems.contains(0)) {
                        //permission = "rw_";
                        stringBuilder.append("rw_");
                    }
                    if (selectedItems.contains(1)) {
                        stringBuilder.append("c_");
                    }
                    if (selectedItems.contains(2)) {
                        stringBuilder.append("d_");
                    }


                    permission = stringBuilder.toString();

                    Log.v("conadamae", permission);

                    if(datasourcePermissions.getPermission(workspace,input.getText().toString()).equals("Not_Found")) {

                        datasourcePermissions.createPermissionsRepresentation(workspace, input.getText().toString(), permission);
                        Toast.makeText(getApplicationContext(), "workspace shared with " + input.getText().toString(),
                                Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Permission " + permission,
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        datasourcePermissions.updatePermission(input.getText().toString(),workspace,permission);
                    }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_workspace, menu);
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
               filesList.clear();
                File file[] = f.listFiles();
                if(!(file == null)) {
                    for (int i = 0; i < file.length; i++) {
                        filesList.add(file[i].getName());
                    }
                }
                listAdapter.notifyDataSetChanged();
            }
        }
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

    public class ReceiveFileCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
        SimWifiP2pSocket s;

        @Override
        protected Void doInBackground(SimWifiP2pSocket... params) {
            BufferedReader sockIn;
            String st;

            s = params[0];
            try {
                while(true){
                    // sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    Log.v("conadamae", "receivecomm");

                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                    ArrayList<WorkspaceRepToBeSent> lwrtbs = new ArrayList<WorkspaceRepToBeSent>();
                    WorkspacesShared foreignWsReceived;
                    Object o = ois.readObject();


                    if (o instanceof FileDeleteResponse){
                        FileDeleteResponse fileDeleteResponse;
                        fileDeleteResponse = (FileDeleteResponse) o;
                        publishProgress(fileDeleteResponse.getStatus(),"FileDelete",fileDeleteResponse.getFileName());
                        break;
                    }


                    Log.v("conadamae", "recebi");
                }
                //   while ((st = sockIn.readLine()) != null) {
                //       publishProgress(st);

                // Toast.makeText(getApplicationContext(),"recebi: " + st, Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Log.d("Error reading socket:", e.getMessage());
            }catch(ClassNotFoundException e){e.getMessage();}
            return null;
        }

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected void onProgressUpdate(String... values) {
        if(values[1].equals("FileDelete")){
            if(values[0].equals("Deleted")){
                Toast.makeText(getApplicationContext(), "File was deleted!", Toast.LENGTH_LONG).show();
                filesList.remove(values[2]);
                listAdapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(getApplicationContext(), "You don't have permission to delete file", Toast.LENGTH_LONG).show();
            }
        }
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!s.isClosed()) {
                try {
                    s.close();
                }
                catch (Exception e) {
                    Log.d("Error closing socket:", e.getMessage());
                }
            }
            s = null;

        }
    }
}
