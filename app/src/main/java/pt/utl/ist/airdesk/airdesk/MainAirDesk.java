package pt.utl.ist.airdesk.airdesk;

import pt.inesc.termite.wifidirect.SimWifiP2pBroadcast;
import pt.inesc.termite.wifidirect.SimWifiP2pDevice;
import pt.inesc.termite.wifidirect.SimWifiP2pDeviceList;
import pt.inesc.termite.wifidirect.SimWifiP2pInfo;
import pt.inesc.termite.wifidirect.SimWifiP2pManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
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
import java.io.FileReader;
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
import pt.utl.ist.airdesk.airdesk.Sqlite.WSPermissionSource;
import pt.utl.ist.airdesk.airdesk.Sqlite.WorkspaceRepresentation;
import pt.inesc.termite.wifidirect.SimWifiP2pManager.PeerListListener;
import pt.utl.ist.airdesk.airdesk.datastructures.*;

public class MainAirDesk extends ActionBarActivity implements SimWifiP2pManager.PeerListListener, SimWifiP2pManager.GroupInfoListener  {

    public static final String TAG = "airdesk";
    private Button button;
    private ListView listView;
    private ListView listView2;
    private ArrayList<String> listaWorkplacesPrivados;
    private ArrayList<DeviceInformation> listaDeDevices;
    private ArrayAdapter<String> listAdapter;
    private ArrayAdapter<String> listAdapter2;
    private WSDataSource datasource;
    private WSPermissionSource datasourcePermissions;
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
    List<WorkspaceRepToBeSent> foreignWS;
    String myname;
    List<DataLockStructure> listOfLocks;
    boolean variavel;

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

        variavel = true;
        guiSetButtonListeners();
        guiUpdateInitState();

        listaWorkplacesPrivados = new ArrayList<String>();
        listaDeDevices = new ArrayList<DeviceInformation>();
        listOfLocks = new ArrayList<DataLockStructure>();

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

        foreignWS = new ArrayList<WorkspaceRepToBeSent>();

        datasource = new WSDataSource(this);
        datasource.open();
        datasourcePermissions = new WSPermissionSource(this);
        datasourcePermissions.open();


        final Intent intent = getIntent();

        login = intent.getStringExtra("login");

        values2 = new ArrayList<String>();
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
                String owner = null;
                Intent intent = new Intent(MainAirDesk.this, ViewWorkspace.class);
                //owner = datasource.getOwner(values2.get(position));
                intent.putExtra("wsName",values2.get(position));
               // intent.putExtra("login",owner);
                intent.putExtra("ambiente","publico");
                intent.putExtra("login",login);
                WorkspaceRepToBeSent wsToSend = null;
                DeviceInformation deviceInformationToSend = null;

                for (WorkspaceRepToBeSent ws : foreignWS){
                    if(ws.get_name().equals(values2.get(position))){
                        owner = ws.get_sentFrom();
                        wsToSend = ws;
                    }
                }

                for(DeviceInformation dvInformation : listaDeDevices){
                    if(dvInformation.getUserLogin().equals(owner)){
                        deviceInformationToSend = dvInformation;
                    }
                }

                intent.putExtra("deviceInformation",deviceInformationToSend);

                intent.putExtra("from", owner);

                ArrayList<String> listOfStrings = new ArrayList<String>();
                listOfStrings.addAll(wsToSend.get_files());

                intent.putStringArrayListExtra("list", listOfStrings);
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
         /*   new IncommingCommTask().executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
            findViewById(R.id.ConnectButton).setEnabled(true);*/

            ///tentativa multithread


                    Thread t = new Thread() {
                        public void run() {

                            try{
                                mSrvSocket = new SimWifiP2pSocketServer(
                                        Integer.parseInt(getString(R.string.port)));
                                while(true) {

                                    final SimWifiP2pSocket s = mSrvSocket.accept();

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            new ReceiveCommTask().executeOnExecutor(
                                                    AsyncTask.THREAD_POOL_EXECUTOR, s);
                                        }
                                    });


                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    t.start();


                findViewById(R.id.WifiOnButton).setEnabled(false);
                findViewById(R.id.ConnectButton).setEnabled(true);
            }
            ///////////////////////////////

    };


    private View.OnClickListener listenerInRangeButton = new View.OnClickListener() {
        public void onClick(View v){
            if (mBound) {

                mManager.requestGroupInfo(mChannel, (SimWifiP2pManager.GroupInfoListener) MainAirDesk.this);

                /*mManager.requestPeers(mChannel, (PeerListListener) MainAirDesk.this);
                // display list of devices in range
                new AlertDialog.Builder(MainAirDesk.this)
                        .setTitle("Devices in WiFi Range")
                        .setMessage(peersStrGlobal.toString())
                        .setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();*/
            } else {
                Toast.makeText(v.getContext(), "Service not bound",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void inRange() {




        if(variavel == false){
            final Handler handler = new Handler();
            Runnable runable = new Runnable() {

                @Override
                public void run() {
                    try{
                        mManager.requestGroupInfo(mChannel, (SimWifiP2pManager.GroupInfoListener) MainAirDesk.this);
                        updateForeignWsList();
                        handler.postDelayed(this, 10000);
                    }
                    catch (Exception e) {
                        // TODO: handle exception
                    }
                    finally{
                        //also call the same runnable
                        handler.postDelayed(this, 10000);
                    }
                }
            };
            variavel = true;
            handler.postDelayed(runable, 1000);
        }
        else{
            mManager.requestGroupInfo(mChannel, (SimWifiP2pManager.GroupInfoListener) MainAirDesk.this);
        }


    }

    public void updateForeignWsList(){
        if (mBound) {

            Thread t = new Thread() {
                public void run() {

                    try {

                        Log.v("conadamae","antes");

                        toBePassed objectToBePassed = new toBePassed(login);

                        for (SimWifiP2pDevice device : lastPeers.getDeviceList()) {
                            if(!myname.equals(device.deviceName)) {

                                String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
                                Log.v("conadamae", "sending->virtIp: " + device.getVirtIp() + " Port: " + device.getVirtPort());

                                final SimWifiP2pSocket cli = new SimWifiP2pSocket(device.getVirtIp(), device.getVirtPort());

                                ObjectOutputStream oos = new ObjectOutputStream(cli.getOutputStream());
                                oos.writeObject(objectToBePassed);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        new ReceiveCommTask().executeOnExecutor(
                                                AsyncTask.THREAD_POOL_EXECUTOR, cli);
                                    }
                                });



                                //mCliSocket.getOutputStream().write( ("hello world" + "\n").getBytes());

                                Log.v("conadamae", "depois");


                            }


                        }

                        //mCliSocket.getOutputStream().write( ("hello world" + "\n").getBytes());

                        Log.v("conadamae", "depois");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            };
            t.start();
        } else {
            Toast.makeText(getApplicationContext(), "Service not bound",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private View.OnClickListener listenerConnectButton = new View.OnClickListener() {
        public void onClick(View v){

            if(mBound){
                updateForeignWsList();
            }
            /*findViewById(R.id.ConnectButton).setEnabled(false);
            findViewById(R.id.SendButton).setEnabled(true);
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
            }*/
        }
    };




    private View.OnClickListener listenerSendButton = new View.OnClickListener() {
        public void onClick(View v){
            if (mBound) {
                values2.clear();
                listAdapter2.notifyDataSetChanged();
                Thread t = new Thread() {
                    public void run() {

                try {

                    Log.v("conadamae","antes");

                    toBePassed objectToBePassed = new toBePassed(login);

                    for (SimWifiP2pDevice device : lastPeers.getDeviceList()) {
                        if(!myname.equals(device.deviceName)) {

                            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
                            Log.v("conadamae", "sending->virtIp: " + device.getVirtIp() + " Port: " + device.getVirtPort());

                            final SimWifiP2pSocket cli = new SimWifiP2pSocket(device.getVirtIp(), device.getVirtPort());

                            ObjectOutputStream oos = new ObjectOutputStream(cli.getOutputStream());
                            oos.writeObject(objectToBePassed);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    new ReceiveCommTask().executeOnExecutor(
                                            AsyncTask.THREAD_POOL_EXECUTOR, cli);
                                }
                            });



                            //mCliSocket.getOutputStream().write( ("hello world" + "\n").getBytes());

                            Log.v("conadamae", "depois");


                        }


                    }

                    //mCliSocket.getOutputStream().write( ("hello world" + "\n").getBytes());

                    Log.v("conadamae", "depois");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                    }
                };
                t.start();
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
                    //refreshForeignList();

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

        File userPath = new File(Environment.getExternalStorageDirectory().toString()+"/"+login);
        Log.v("conadamae","absolute path:" + userPath.getPath());
        deleteFolder(userPath);
        values.clear();
        listAdapter.notifyDataSetChanged();
        values2.clear();
        listAdapter2.notifyDataSetChanged();
    }

    /*public void refreshForeignList(){
        values2.clear();
        values2 = datasource.GetAllValues(login);
        listAdapter2 = new ArrayAdapter<String>(this,  R.layout.mylistfolder ,R.id.ItemnameFolder, values2);
        listView2.setAdapter(listAdapter2);
    }*/

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
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

        //refreshForeignList();


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

    @Override
    public void onGroupInfoAvailable(SimWifiP2pDeviceList simWifiP2pDeviceList, SimWifiP2pInfo simWifiP2pInfo) {

        lastPeers = simWifiP2pDeviceList;

        myname = simWifiP2pInfo.getDeviceName();



        Log.v("conadamae","No groupavailablePeers");
        for (SimWifiP2pDevice device : simWifiP2pDeviceList.getDeviceList()) {
            String devstr = "" + device.deviceName + " (" + device.getVirtIp() + ")\n";
            Log.v("conadamae","No groupavailablePeers->virtIp: "+device.getVirtIp()+" Port: "+device.getVirtPort());

        }
    }




    /***********************************************************************************************/
    /***********************************************************************************************/
    /****************************    OutgoingCommTask    *******************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/

    //solução :criamos uma thread diferente, passamos-lhe a socket server e ai sim fazemos accept()...assim
    //    bloqueia dentro das varias threads(n ha problema varias ligações num porto)

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
                    while(true){
                    // sockIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    Log.v("conadamae", "receivecomm");

                    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                    ArrayList<WorkspaceRepToBeSent> lwrtbs = new ArrayList<WorkspaceRepToBeSent>();
                    WorkspacesShared foreignWsReceived;
                    Object o = ois.readObject();


                    if (o instanceof toBePassed) {
                        toBePassed ds = (toBePassed) o;
                        // do something with ds
                        //publishProgress(ds.getId());

                        //publishProgress(ds.getId());

                        //lastPeers

                        List<String> listaDeWSApassar = datasourcePermissions.GetAllWSByUser(ds.getId());

                        for (String ws : listaDeWSApassar) {

                            Log.v("conadamae", "wsapassar");

                            final String path = Environment.getExternalStorageDirectory().toString() + "/" + login + "/" + ws;
                            File f = new File(path);
                            File file[] = f.listFiles();
                            ArrayList<String> wsFileNames = new ArrayList<String>();

                            if (file != null) {
                                for (int i = 0; i < file.length; i++) {
                                    wsFileNames.add(file[i].getName());
                                }
                            }

                            WorkspaceRepToBeSent reptobe =new WorkspaceRepToBeSent(ws,login);
                            reptobe.set_files(wsFileNames);
                            lwrtbs.add(reptobe);
                        }
                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                        oos.writeObject(new WorkspacesShared(lwrtbs, myname, login));

                        break;
                    }

                    if (o instanceof WorkspacesShared) {
                        foreignWsReceived = (WorkspacesShared) o;
                        //WorkspacesShared received = (WorkspacesShared) o;
                        String owner;
                        //received.getFrom();
                        String networkName = foreignWsReceived.get_networkName();
                        SimWifiP2pDevice receivedDevice = lastPeers.getByName(networkName);
                        String ip = receivedDevice.getVirtIp();
                        int port = receivedDevice.getVirtPort();
                        String receivedLogin = foreignWsReceived.get_login();

                        listaDeDevices.add(new DeviceInformation(port, networkName, receivedLogin, ip));
                        Log.v("conadamae", port + "|" + networkName + "|" + receivedLogin + "|" + ip);


                        for (WorkspaceRepToBeSent wsRec : foreignWsReceived.getWs()) {
                            foreignWS.add(wsRec);
                            publishProgress(wsRec.get_name());

                        }

                        break;
                    }
                    if (o instanceof  FileRequest){
                        FileRequest fileRequest;
                        fileRequest = (FileRequest) o;
                        String text = new String();

                        String fileName =fileRequest.getFileName();
                        String fileWorkspace = fileRequest.getWorkspace();

                        String pathForFile = Environment.getExternalStorageDirectory().toString()+"/"+login+"/"+fileWorkspace;
                        File file  = new File(pathForFile,fileName);

                        try {
                            BufferedReader br = new BufferedReader(new FileReader(file));
                            String line;

                            while ((line = br.readLine()) != null) {
                                text = text+line;
                                text = text+'\n';
                            }
                            br.close();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                        oos.writeObject(new FileResponse(text));
                    break;

                    }

                    if (o instanceof FileResponse){
                        FileResponse fileResponse;
                        fileResponse = (FileResponse) o;
                        publishProgress(fileResponse.getFile());
                        break;
                    }

                    if (o instanceof FileRequestAlteration){
                        FileRequestAlteration fileRequestAlteration;
                        fileRequestAlteration = (FileRequestAlteration) o;
                        String text = fileRequestAlteration.get_text();
                        String pathOfFile = Environment.getExternalStorageDirectory().toString()+"/"+login+"/"+fileRequestAlteration.get_workspaceName()+"/"+fileRequestAlteration.get_fileName();
                        File f = new File(pathOfFile);
                        FileWriter writer = new FileWriter(f);
                        writer.append(text);
                        writer.flush();
                        writer.close();

                        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                        oos.writeObject(new FileResponseAlteration("saved"));
                        break;

                    }

                        if (o instanceof FileLockRequest){
                            FileLockRequest fileLockRequest;
                            fileLockRequest = (FileLockRequest) o;
                            String fileToLock = fileLockRequest.getFile();
                            String userLock = fileLockRequest.getLogin();
                            String wsTolock = fileLockRequest.getWorkpace();
                            String permission = datasourcePermissions.getPermission(wsTolock, userLock);

                            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

                            Boolean alreadyLocked = false;

                            for(DataLockStructure entry : listOfLocks){
                                if(entry.getFileName().equals(fileToLock)){
                                    alreadyLocked = true;
                                }
                            }


                            Log.v("conadamae", permission);
                            Log.v("conadamae", alreadyLocked.toString());
                            if((permission.contains("rw")) && (alreadyLocked==false)){

                                listOfLocks.add(new DataLockStructure(userLock,filename));
                                oos.writeObject(new FileLockResponse("lock_Acquired",alreadyLocked));

                            }
                            else{

                                oos.writeObject(new FileLockResponse("lock_Not_Acquired",alreadyLocked));
                            }

                            //publishProgress(fileLockRequest.getFile(), "FileResponse");
                            break;
                        }

                        if(o instanceof FileDeleteRequest){
                            FileDeleteRequest fileDeleteRequest;
                            fileDeleteRequest = (FileDeleteRequest) o;

                            File fileToDelete = fileDeleteRequest.getFile();

                            String userName = fileDeleteRequest.getLogin();
                            String workspace = fileDeleteRequest.getWorkspace();

                            File path = new File(Environment.getExternalStorageDirectory().toString()+"/"+login+"/"+workspace+"/"+fileToDelete);
                            Log.v("conadamae","caminho do ficheiro: "+path);

                            String permission = datasourcePermissions.getPermission(workspace, userName);

                            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                            if(permission.contains("d")) {
                                oos.writeObject(new FileDeleteResponse("Deleted",fileToDelete.getPath()));
                                path.delete();
                            }
                            else{
                                oos.writeObject(new FileDeleteResponse("Not_Deleted",fileToDelete.getPath()));
                            }
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
            findViewById(R.id.ConnectButton).setEnabled(false);
            findViewById(R.id.SendButton).setEnabled(true);

        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.v("conadamae", values[0]);
            Toast.makeText(getApplicationContext(), "recebi: " + values[0], Toast.LENGTH_LONG).show();

            values2.add(values[0]);
            listAdapter2.notifyDataSetChanged();

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


        findViewById(R.id.ConnectButton).setEnabled(true);
        findViewById(R.id.SendButton).setEnabled(true);
        findViewById(R.id.WifiOnButton).setEnabled(true);
        findViewById(R.id.InRangeButton).setEnabled(true);
    }

}
