package pt.utl.ist.airdesk.airdesk;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.Messenger;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import pt.inesc.termite.wifidirect.service.SimWifiP2pService;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocket;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketManager;
import pt.inesc.termite.wifidirect.sockets.SimWifiP2pSocketServer;
import pt.utl.ist.airdesk.airdesk.Sqlite.WSDataSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WorkspaceRepresentation;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;


public class MainAirDesk extends ActionBarActivity implements SimWifiP2pManager.PeerListListener {

    public static final String TAG = "airdesk";
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
    private SimWifiP2pManager mManager = null;
    private SimWifiP2pManager.Channel mChannel = null;
    private Messenger mService = null;
    private boolean mBound = false;
    private SimWifiP2pSocketServer mSrvSocket = null;
    private ReceiveCommTask mComm = null;
    private SimWifiP2pSocket mCliSocket = null;
    private SimWifiP2pDeviceList lastPeers;
    StringBuilder peersStrGlobal;

    public SimWifiP2pManager getManager() {
        return mManager;
    }

    public SimWifiP2pManager.Channel getChannel() {
        return mChannel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_air_desk);


        guiSetButtonListeners();
        guiUpdateInitState();

        listaWorkplacesPrivados = new ArrayList<String>();


        // initialize the WDSim API
        SimWifiP2pSocketManager.Init(getApplicationContext());

        // register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_STATE_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_PEERS_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_NETWORK_MEMBERSHIP_CHANGED_ACTION);
        filter.addAction(SimWifiP2pBroadcast.WIFI_P2P_GROUP_OWNERSHIP_CHANGED_ACTION);
        SimWifiP2pBroadcastReceiver receiver = new SimWifiP2pBroadcastReceiver(this);
        registerReceiver(receiver, filter);

        peersStrGlobal = new StringBuilder();

        datasource = new WSDataSource(this);
        datasource.open();

        final Intent intent = getIntent();

        login = intent.getStringExtra("login");

        values2 = datasource.GetAllValues(login);
        values = new ArrayList<String>();

        final String path = Environment.getExternalStorageDirectory().toString()+"/"+login;
        File f = new File(path);
        File file[] = f.listFiles();

        if(file!=null) {

            for (int i = 0; i < file.length; i++) {
                values.add(file[i].getName());
            }
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

    /***********************************************************************************************/
    /****************************       Listenners       *******************************************/
    /***********************************************************************************************/

    private View.OnClickListener listenerWifiOnButton = new View.OnClickListener() {
        public void onClick(View v){

            Intent intent = new Intent(v.getContext(), SimWifiP2pService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            mBound = true;

            // spawn the chat server background task
            new IncommingCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
            findViewById(R.id.ConnectButton).setEnabled(true);

        }
    };


    private View.OnClickListener listenerInRangeButton = new View.OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                mManager.requestPeers(mChannel, (PeerListListener) MainAirDesk.this);
                // display list of devices in range
                new AlertDialog.Builder(MainAirDesk.this)
                        .setTitle("Devices in WiFi Range")
                        .setMessage(peersStrGlobal.toString())
                        .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
            } else {
                Toast.makeText(v.getContext(), "Service not bound",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private View.OnClickListener listenerConnectButton = new View.OnClickListener() {
        public void onClick(View v){
            findViewById(R.id.ConnectButton).setEnabled(false);
            StringBuilder peersStr = new StringBuilder();
            mManager.requestPeers(mChannel, (PeerListListener) MainAirDesk.this);
            if (mBound) {
                for (SimWifiP2pDevice device : lastPeers.getDeviceList()) {
                    String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
                    peersStr.append(devstr);
                    String virtIp = device.getVirtIp();
                    int virtPort = device.getVirtPort();
                    Log.v("conadamae","virtIp: "+virtIp+" Port: "+virtPort);
                    Toast.makeText(getApplicationContext(),"virtIp: "+virtIp+" Port: "+virtPort, Toast.LENGTH_LONG).show();
                    new OutgoingCommTask().executeOnExecutor(
                            AsyncTask.THREAD_POOL_EXECUTOR,
                            virtIp);
                }

            } else {
                Toast.makeText(getApplicationContext(),"bound = false", Toast.LENGTH_LONG).show();
            }
        }
    };

    private View.OnClickListener listenerSendButton = new View.OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                try {
                    Log.v("conadamae","antes");

                    toBePassed objectToBePassed = new toBePassed();

                    ObjectOutputStream oos = new ObjectOutputStream(mCliSocket.getOutputStream());
                    oos.writeObject(objectToBePassed);

                    //mCliSocket.getOutputStream().write( ("hello world" + "\n").getBytes());

                    Log.v("conadamae", "depois");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(v.getContext(), "Service not bound",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };


    private View.OnClickListener listenerInGroupButton = new View.OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                mManager.requestGroupInfo(mChannel, (SimWifiP2pManager.GroupInfoListener) MainAirDesk.this);
            } else {
                Toast.makeText(v.getContext(), "Service not bound",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };


    /***********************************************************************************************/
    /****************************    onActivityResult    *******************************************/
    /***********************************************************************************************/

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

    /***********************************************************************************************/
    /****************************    resetDatabase   ***********************************************/
    /****************************         and        ***********************************************/
    /**************************  refreshForeignList  ***********************************************/
    /***********************************************************************************************/

    public void resetDatabase(View view){
        datasource.resetDatabase();
        values2.clear();
        listAdapter2.notifyDataSetChanged();
    }

    public void refreshForeignList(){
        values2.clear();
        values2 = datasource.GetAllValues(login);
        listAdapter2 = new ArrayAdapter<String>(this,  R.layout.mylistfolder ,R.id.ItemnameFolder, values2);
        listView2.setAdapter(listAdapter2);
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



    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        refreshForeignList();


    }

    /***********************************************************************************************/
    /****************************    ServiceConnection   *******************************************/
    /***********************************************************************************************/

    private ServiceConnection mConnection = new ServiceConnection() {
        // callbacks for service binding, passed to bindService()

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            mManager = new SimWifiP2pManager(mService);
            mChannel = mManager.initialize(getApplication(), getMainLooper(), null);
            mBound = true;
            mManager.requestPeers(mChannel,(PeerListListener) MainAirDesk.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mManager = null;
            mChannel = null;
            mBound = false;
        }
    };

    /***********************************************************************************************/
    /****************************    onPeersAvailable    *******************************************/
    /***********************************************************************************************/


    @Override
    public void onPeersAvailable(SimWifiP2pDeviceList peers) {
        StringBuilder peersStr = new StringBuilder();

        // compile list of devices in range
        for (SimWifiP2pDevice device : peers.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            Log.v("conadamae","No onPeers->virtIp: "+device.getVirtIp()+" Port: "+device.getVirtPort());
            peersStr.append(devstr);
        }

        peersStrGlobal = peersStr;
        lastPeers = peers;

    }

    /***********************************************************************************************/
    /***********************************************************************************************/
    /****************************    OutgoingCommTask    *******************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/

    public class OutgoingCommTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            Log.v("conadamae","onpreExecute");
            Toast.makeText(getApplicationContext(),"Connecting...",Toast.LENGTH_LONG).show();
           // mTextOutput.setText("Connecting...");
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                mCliSocket = new SimWifiP2pSocket(params[0],
                        Integer.parseInt(getString(R.string.port)));
            } catch (UnknownHostException e) {
                return "Unknown Host:" + e.getMessage();
            } catch (IOException e) {
                return "IO error:" + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                Log.v("conadamae","onpostExecute");
                Toast.makeText(getApplicationContext(),"connected", Toast.LENGTH_LONG).show();
                //mTextOutput.setText(result);
                //findViewById(R.id.idConnectButton).setEnabled(true);
            }
            else {
                mComm = new ReceiveCommTask();
                mComm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,mCliSocket);
            }
        }
    }


    /***********************************************************************************************/
    /***********************************************************************************************/
    /****************************    IncommingCommTask    ******************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/

    public class IncommingCommTask extends AsyncTask<Void, SimWifiP2pSocket, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Log.d(TAG, "IncommingCommTask started (" + this.hashCode() + ").");

            try {
                mSrvSocket = new SimWifiP2pSocketServer(
                        Integer.parseInt(getString(R.string.port)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    SimWifiP2pSocket sock = mSrvSocket.accept();
                    Log.v("conadamae","passou accpeted");
                    if (mCliSocket != null && mCliSocket.isClosed()) {
                        mCliSocket = null;
                    }
                    if (mCliSocket != null) {
                        Log.d(TAG, "Closing accepted socket because mCliSocket still active.");
                        sock.close();
                    } else {
                        publishProgress(sock);
                    }
                } catch (IOException e) {
                    Log.d("Error accepting socket:", e.getMessage());
                    break;
                    //e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(SimWifiP2pSocket... values) {
            mCliSocket = values[0];
            mComm = new ReceiveCommTask();

            mComm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mCliSocket);
        }
    }


    /***********************************************************************************************/
    /***********************************************************************************************/
    /****************************    ReceiveCommTask      ******************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/

    public class ReceiveCommTask extends AsyncTask<SimWifiP2pSocket, String, Void> {
        SimWifiP2pSocket s;

        @Override
        protected Void doInBackground(SimWifiP2pSocket... params) {
            BufferedReader sockIn;
            String st;

            s = params[0];
            try {
               // sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                Log.v("conadamae","receivecomm");

                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                Object o = ois.readObject();
                if(o instanceof toBePassed) {
                    toBePassed ds = (toBePassed)o;
                    // do something with ds
                    publishProgress(ds.getId());
                }

c



             //   while ((st = sockIn.readLine()) != null) {
             //       publishProgress(st);
                    Log.v("conadamae","recebi");
                   // Toast.makeText(getApplicationContext(),"recebi: " + st, Toast.LENGTH_LONG).show();

            } catch (IOException e) {
                Log.d("Error reading socket:", e.getMessage());
            }catch(ClassNotFoundException e){e.getMessage();}
            return null;
        }

        @Override
        protected void onPreExecute() {
            findViewById(R.id.ConnectButton).setEnabled(false);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.v("conadamae",values[0]);
            Toast.makeText(getApplicationContext(),"recebi: " + values[0], Toast.LENGTH_LONG).show();
            listAdapter2.add(values[0]);
            listAdapter.notifyDataSetChanged();

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
            if (mBound) {

            } else {

            }
        }
    }

    private void guiSetButtonListeners() {

        button = (Button) findViewById(R.id.button);
        listView = (ListView) findViewById(R.id.listView);
        listView2 = (ListView) findViewById(R.id.listView2);
        findViewById(R.id.WifiOnButton).setOnClickListener(listenerWifiOnButton);
        findViewById(R.id.InRangeButton).setOnClickListener(listenerInRangeButton);
        findViewById(R.id.ConnectButton).setOnClickListener(listenerConnectButton);
        findViewById(R.id.SendButton).setOnClickListener(listenerSendButton);
    }

    private void guiUpdateInitState() {


        findViewById(R.id.ConnectButton).setEnabled(false);
        findViewById(R.id.SendButton).setEnabled(false);
        findViewById(R.id.WifiOnButton).setEnabled(true);
        findViewById(R.id.InRangeButton).setEnabled(false);
    }

}
