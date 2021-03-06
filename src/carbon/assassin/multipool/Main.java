package carbon.assassin.multipool;

import java.io.IOException;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	public static DataPuller dataPuller;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		dataPuller = new DataPuller((ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE));
		try {
			dataPuller.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			switch(position)
			{
			case 0:
				fragment = new CurrentlyMiningFragment();
			break;
			}
			Bundle args = new Bundle();
			args.putInt(CurrentlyMiningFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 1;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class CurrentlyMiningFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private long lastUpdateTime;
		private DataPuller d;
		CountDownTimer cdt;
		public CurrentlyMiningFragment()  {
			lastUpdateTime = System.currentTimeMillis();
			d = Main.dataPuller;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.main,
					container, false);
			((TextView) rootView.findViewById(R.id.currentMiningMain)).setText("Pool Currently Mining:" +" " +d.getCurrentlyMined());
			((TextView) rootView.findViewById(R.id.alert)).setText("*Based on reported hash data.");
			ListAdapter ad = new ListAdapter(this.getActivity().getApplicationContext(),d.pullListofCurrentHashrates());
			ListView list = (ListView)rootView.findViewById(R.id.listView1);
			list.setAdapter(ad);
			cdt = new CountDownTimer(Settings.updateInterval, Settings.updateInterval) {

			    public void onTick(long millisUntilFinished) {
			        update();
			    }

			    public void onFinish() {
			        cdt.start(); // Call Again After 30 seconds
			    }
			}.start();
			return rootView;
		}
		public void update()
		{
			new UpdateTask(this.getView())
			{
				@Override public void onPostExecute(String result)
			    {
					if(result.equals("updated"))
					{
						((TextView) main.findViewById(R.id.currentMiningMain)).setText("Pool Currently Mining:" +" " +d.getCurrentlyMined());
						ListAdapter ad = new ListAdapter(this.main.getContext(),d.pullListofCurrentHashrates());
						ListView list = (ListView)main.findViewById(R.id.listView1);
						list.setAdapter(ad);
					}
			    }
			}.execute();
		}
	}

}
