package kr.ac.kaist.mr.biolab_myco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;
    public static ArrayList<RecyclerViewAdapter> mAdapters = new ArrayList<>();
    public Context mContext;
    public Snackbar sn;
    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();
    final int TIME_TO_SCAN = BleObserver.TIME_TO_SCAN;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        startService(new Intent(mContext, BleObserver.class));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mAdapters.add(null);
        mAdapters.add(null);
        mAdapters.add(null);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sn = Snackbar.make(view, "Fetching data from device...", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null);
                sn.show();

                Intent i = new Intent(mContext, BleObserver.class);
                i.putExtra("force", true);
                startService(i);
                //new bgRefresh().execute();
                Runnable task = new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable(){public void run(){updatedata();sn.dismiss();}});
                    }
                };
                worker.schedule(task, TIME_TO_SCAN+100, TimeUnit.MILLISECONDS);

            }
        });
        sn = Snackbar.make(fab, "Fetching data from device...", Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null);
        sn.show();
        Runnable task2 = new Runnable() {
            public void run() {
                runOnUiThread(new Runnable(){public void run(){updatedata();sn.dismiss();}});
            }
        };
        worker.schedule(task2, TIME_TO_SCAN + 100, TimeUnit.MILLISECONDS);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int sect = this.getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recyclerView);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity().getApplicationContext());
            mRecyclerView.setLayoutManager(llm);
//            if(BleObserver.scanResult!= null)
//                mAdapters.set(sect-1, new RecyclerViewAdapter(formatDisplay(BleObserver.scanResult), getContext()));
//            else
            mAdapters.set(sect - 1, new RecyclerViewAdapter(new ArrayList<DisplayItem>(), getContext()));
            mRecyclerView.setAdapter(mAdapters.get(sect-1));
            ((MainActivity)getContext()).updatedata();
            return rootView;
        }
    }

    public static ArrayList<DisplayItem> formatDisplay(ArrayList<BleData> lst){
        ArrayList<DisplayItem> result = new ArrayList<>();
        if(lst==null) return result;
        for(BleData item : lst){
            DisplayItem d = new DisplayItem();
            d.add1 = "Color\nCount\nMotor Done\nBattery";
            d.add2 = item.getR() + "\n"+
                    item.getCount() + "\n" + item.isMotorDone() + "\n" + item.getBatteryVoltage()+"V";
            d.name = item.getDeviceName();
            d.mtr = item.isMotorDone();
            d.isGreen = (item.getR() >= 10);
            long t = TimeUnit.SECONDS.convert((new java.util.Date()).getTime()-BleObserver.LastScanDate, TimeUnit.MILLISECONDS);

            d.sub = "Updated " + t + " seconds ago";
            result.add(d);
        }
        return result;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }



        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Total";
                case 1:
                    return "Finished";
                case 2:
                    return "Error";
            }
            return null;
        }
    }

    public void updatedata(){
        // TODO Extend implementation toward other tabs
        if(BleObserver.scanResult==null){
            Log.e(getClass().getName(), "ScanResult is null.");
            return;
        }

        ArrayList<DisplayItem> data = formatDisplay(BleObserver.scanResult);
        for(int i=0;i<data.size();i++){
            DisplayItem current = data.get(i);
            List<DisplayItem> prev = mAdapters.get(0).dp_items;
            boolean done = false;
            for(int j=0;j<prev.size();j++){
                if(prev.get(j).name!=null && prev.get(j).name.equals(current.name)){
                    //must update
                    mAdapters.get(0).update(current, j);
                    done = true;
                    break;
                }
            }
            if(!done){
                //must add
                mAdapters.get(0).add(current, mAdapters.get(0).getItemCount());
            }
        }

        for(int i=0;i<data.size();i++){
            DisplayItem current = data.get(i);
            List<DisplayItem> prev = mAdapters.get(1).dp_items;
            boolean done = false;
            for(int j=0;j<prev.size();j++){
                if(prev.get(j).name!=null && prev.get(j).name.equals(current.name) && current.mtr){
                    //must update
                    mAdapters.get(1).update(current, j);
                    done = true;
                    break;
                }
            }
            if(!done && current.mtr){
                //must add
                mAdapters.get(1).add(current, mAdapters.get(1).getItemCount());
            }
        }

    }

//    public class ScanReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            Log.e(getClass().getName(), "Received Broadcast... Checking");
//            int num = intent.getIntExtra("btScanResult", 0);
//            Log.e(getClass().getName(), "Received Broadcast : " + num);
//        }
//    }
}
