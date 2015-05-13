package pt.utl.ist.airdesk.airdesk;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.utl.ist.airdesk.airdesk.datastructures.DeviceInformation;
import pt.utl.ist.airdesk.airdesk.datastructures.FileRequest;
import pt.utl.ist.airdesk.airdesk.datastructures.FileResponse;
import pt.utl.ist.airdesk.airdesk.datastructures.WorkspaceRepToBeSent;
import pt.utl.ist.airdesk.airdesk.datastructures.WorkspacesShared;
import pt.utl.ist.airdesk.airdesk.datastructures.toBePassed;


public class ViewForeignFile extends ActionBarActivity {

    private String fileName;
    private String workspace;
    TextView fileTextView;
    DeviceInformation deviceInformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_foreign_file);

        TextView fileNameView = (TextView) findViewById(R.id.editText);
        fileTextView = (EditText) findViewById(R.id.editText2);


        Intent intent = getIntent();

        fileName = intent.getStringExtra("fileName");
        workspace = intent.getStringExtra("wsName");
        deviceInformation = (DeviceInformation) intent.getSerializableExtra("deviceInformation");

        fileNameView.setText(fileName);


        Thread t = new Thread() {
            public void run() {

                try {

                    Log.v("conadamae", "antes");

                    FileRequest fileRequest = new FileRequest(workspace,fileName);


                            final SimWifiP2pSocket s = new SimWifiP2pSocket(deviceInformation.getIp(),deviceInformation.getPort());

                            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                            oos.writeObject(fileRequest);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_foreign_file, menu);
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


                    if (o instanceof FileResponse){
                        FileResponse fileResponse;
                        fileResponse = (FileResponse) o;
                        publishProgress(fileResponse.getFile());
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
            findViewById(R.id.ConnectButton).setEnabled(false);
            findViewById(R.id.SendButton).setEnabled(true);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            fileTextView.setText(values[0]);

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
