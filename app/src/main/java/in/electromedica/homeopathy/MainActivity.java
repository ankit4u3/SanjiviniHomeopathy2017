package in.electromedica.homeopathy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import net.mskurt.neveremptylistviewlibrary.NeverEmptyListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    WebView web;
    String url = "http://www.google.com";
    ArrayAdapter<String> adapter;
    NeverEmptyListView neverEmptyListView;
    ArrayList<String> values=new ArrayList<String>();
    EditText searchTxt;
    Button searchBtb;
    private static long back_pressed;
    InterstitialAd mInterstitialAd;
    PublisherInterstitialAd mPublisherInterstitialAd;
    private static final String[] keys = { "line1", "line2" };
    private static final int[] controlIds = { android.R.id.text1,
            android.R.id.text2 };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        searchBtb=(Button)findViewById(R.id.button2);
        searchTxt=(EditText)findViewById(R.id.editText);
        web = (WebView) findViewById(R.id.webBrowser);
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        //web.loadUrl("http://122.176.71.171/");
        web.setVisibility(View.INVISIBLE);

        //Create an empty adapter

        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, values);
//Set NeverEmptyListView's adapter
        neverEmptyListView=(NeverEmptyListView)findViewById(R.id.listview);
        neverEmptyListView.setAdapter(adapter);


        neverEmptyListView.setHolderClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do anything here


            }
        });

        neverEmptyListView.getListview().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                searchTxt.setText(String.valueOf(parent.getItemAtPosition(position)));
                Toast.makeText(getApplicationContext(),String.valueOf(parent.getItemAtPosition(position)),Toast.LENGTH_SHORT).show();

                new DownloadFilteredTextByID(getApplicationContext()).execute();

            }
        });

        searchBtb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DownloadFilteredText(getApplication()).execute();

            }
        });
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-8586622059217734~2869315405");

       // AdRequest request = new AdRequest.Builder()
          //      .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
        //        .addTestDevice("420E45ED402D5230C1C1BEC991F4D51C")  // An example device ID
       //         .build();

     // AdView mAdView = (AdView) findViewById(R.id.adView);
      //  AdRequest adRequest = new AdRequest.Builder().build();
    // mAdView.loadAd(adRequest);
      //  mAdView.loadAd(request);
     //   AdRequest.Builder.addTestDevice("420E45ED402D5230C1C1BEC991F4D51C")



        /////////////////////////////


        mPublisherInterstitialAd = new PublisherInterstitialAd(this);
        mPublisherInterstitialAd.setAdUnitId("ca-app-pub-8586622059217734~2869315405");

        mPublisherInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();

            }
        });
    }

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
// TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
// TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           // super.onBackPressed();
        }

        if (!drawer.isDrawerOpen(GravityCompat.START)) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
            } else {
                Toast.makeText(getBaseContext(), "Press once again to exit", Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_camera) {
            web.setVisibility(View.INVISIBLE);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            web.setVisibility(View.VISIBLE);
            new DownloadWebPageTask(this).execute();

        } else if (id == R.id.nav_slideshow) {
            web.setVisibility(View.VISIBLE);
            web.loadUrl("http://122.176.71.171/");

        } else if (id == R.id.nav_manage) {
            web.setVisibility(View.VISIBLE);
            web.loadUrl("http://122.176.71.171/");

        } else if (id == R.id.nav_share) {
            web.setVisibility(View.VISIBLE);
            web.loadUrl("http://122.176.71.171/");

        } else if (id == R.id.nav_send) {


            requestNewInterstitial();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void requestNewInterstitial() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice("420E45ED402D5230C1C1BEC991F4D51C")
                .build();

        mPublisherInterstitialAd.loadAd(adRequest);
    }
    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        private final Context context;

        List<Map> listtemp = new ArrayList<Map>(); //
        Map<String, String> map = new HashMap<String, String>();
        List<Map<String, String>> items = new ArrayList<Map<String, String>>();

        public String sTemp="";

        public DownloadWebPageTask(Context c) {

            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context.getApplicationContext(),
                            "Started", Toast.LENGTH_LONG).show();
                    values.add("");
                    values.add("");
                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context.getApplicationContext(),
                            "ENDED"   , Toast.LENGTH_LONG).show();



                    neverEmptyListView.notifyDataSetChanged(adapter);
                    web.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        protected String doInBackground(String... urls) {
            try {


                Log.d("OP", "Attempting File Read from Assets ");
                InputStream is = context.getAssets().open(
                        "Medica/stringtoken.txt");
                Log.d("OP", context.getAssets().open(
                        "Medica/stringtoken.txt").toString());

                // InputStream instream = resEntityGet.getContent();
                BufferedReader str = new BufferedReader(new InputStreamReader(
                        is, "ISO-8859-1"));

                String build;
                String ans = new String("");
                build = new String("");
                while ((ans = str.readLine()) != null) {
                    build = build + ans;
                    Log.d("OP", build);

                }

                JSONObject jobj = new JSONObject(build);
                JSONArray arr = jobj.getJSONArray("questions");
                String arrlen = Integer.toString(arr.length());
                // Log.d(
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject qs = arr.getJSONObject(i);

                    String qNum;
                    qNum = qs.getString("joke").replace("\n", "");// nick
                    Log.d("OP", qNum);
                    sTemp+=qNum;
                    values.add(qNum);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put(!qNum.isEmpty() ? qNum : "Unknown",qs.getString("md5val").replace("\n", ""));
                    items.add(map);
                    listtemp.add(map);
                }

                return "EXECUTED OK ";
            } catch (Exception e) {
                // TODO: handle exception
                //
            }
            return "ERROR";

        }
    }


    private class DownloadFilteredText extends AsyncTask<String, Void, String> {
        private final Context context;

        public String sTemp="";
public String searchText;

        public DownloadFilteredText(Context c) {

            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    adapter.clear();
                    values.clear();
                    searchText=searchTxt.getText().toString();
                    Toast.makeText(context.getApplicationContext(),
                            "Copied Text "+searchText, Toast.LENGTH_LONG).show();
                    values.add("");
                    values.add("");

                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context.getApplicationContext(),
                            "ENDED"   , Toast.LENGTH_LONG).show();


                    neverEmptyListView.notifyDataSetChanged(adapter);
                    web.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        protected String doInBackground(String... urls) {
            try {


                Log.d("OP", "Attempting File Read from Assets ");
                InputStream is = context.getAssets().open(
                        "Medica/stringtoken.txt");
                Log.d("OP", context.getAssets().open(
                        "Medica/stringtoken.txt").toString());

                // InputStream instream = resEntityGet.getContent();
                BufferedReader str = new BufferedReader(new InputStreamReader(
                        is, "ISO-8859-1"));

                String build;
                String ans = new String("");
                build = new String("");
                while ((ans = str.readLine()) != null) {
                    build = build + ans;
                    Log.d("OP", build);

                }



                JSONObject jobj = new JSONObject(build);
                JSONArray arr = jobj.getJSONArray("questions");
                String arrlen = Integer.toString(arr.length());
                // Log.d(
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject qs = arr.getJSONObject(i);

                    String qNum;
                    qNum = qs.getString("joke").replace("\n", "");// nick
                    Log.d("OP", qNum);
                    sTemp+=qNum;
                    if(qNum.toLowerCase().contains(searchText.toLowerCase())) {
                        values.add(qNum);
                        //md5val
                       // values.add(qs.getString("md5val").replace("\n", ""));
                    }

                }

                return "EXECUTED OK ";
            } catch (Exception e) {
                // TODO: handle exception
                //
            }
            return "ERROR";

        }
    }

    private class DownloadFilteredTextByID extends AsyncTask<String, Void, String> {
        private final Context context;

        public String sTemp="";
        public String searchText;
        public String webData;

        public DownloadFilteredTextByID(Context c) {

            this.context = c;
        }

        @Override
        protected void onPreExecute() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    searchText=searchTxt.getText().toString();
                    values.add("");
                    values.add("");


                }
            });
        }

        @Override
        protected void onPostExecute(String s) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context.getApplicationContext(),
                            "ENDED"   , Toast.LENGTH_LONG).show();


                    neverEmptyListView.notifyDataSetChanged(adapter);
                    web.setVisibility(View.VISIBLE);
                    String html = webData;
                    String mime = "text/html";
                    String encoding = "UTF-8";

                    web.loadDataWithBaseURL(null, html, mime, encoding, null);


                }
            });
        }

        @Override
        protected String doInBackground(String... urls) {
            try {


                Log.d("OP", "Attempting File Read from Assets ");
                InputStream is = context.getAssets().open(
                        "Medica/stringtoken.txt");
                Log.d("OP", context.getAssets().open(
                        "Medica/stringtoken.txt").toString());

                // InputStream instream = resEntityGet.getContent();
                BufferedReader str = new BufferedReader(new InputStreamReader(
                        is, "ISO-8859-1"));

                String build;
                String ans = new String("");
                build = new String("");
                while ((ans = str.readLine()) != null) {
                    build = build + ans;
                    Log.d("OP", build);

                }



                JSONObject jobj = new JSONObject(build);
                JSONArray arr = jobj.getJSONArray("questions");
                String arrlen = Integer.toString(arr.length());
                // Log.d(
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject qs = arr.getJSONObject(i);

                    String qNum;
                    qNum = qs.getString("joke").replace("\n", "");// nick
                    Log.d("OP", qNum);
                    sTemp+=qNum;
                    if(qNum.toLowerCase().equalsIgnoreCase(searchText.toLowerCase())) {
                        values.add(qNum);
                        String sData=qs.getString("md5val").replace("\n", "");

                        if(sData.contains("SYMPTOMS"))
                        {
                            int findSympPosition=sData.indexOf("SYMPTOMS");
                            int totalLength=sData.length();
                            String subString=sData.substring(findSympPosition,totalLength);

                            webData="<html> <head> </head> <body>  <br>  <br> "+qNum+" <br> " +subString+"</body></html>";
                        }
                        else
                        {


                            webData="<html> <head> </head> <body>  <br> " + qNum + "  <br> SYMPTOMS NOT FOUND IN THIS  <br> " +sData+"</body></html>";
                        }


                        //md5val
                        // values.add(qs.getString("md5val").replace("\n", ""));
                    }

                }

                return "EXECUTED OK ";
            } catch (Exception e) {
                // TODO: handle exception
                //
            }
            return "ERROR";

        }
    }

}

//https://android-arsenal.com/details/1/2734
//librarry for lisst sused


