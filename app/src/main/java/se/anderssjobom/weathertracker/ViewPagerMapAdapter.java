package se.anderssjobom.weathertracker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v13.app.FragmentStatePagerAdapter;

//import android.support.v4.app.FragmentStatePagerAdapter;
//import com.google.android.gms.maps.MapFragment;
//import android.app.Fragment;
//import android.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by Rami on 2016-04-28.
 */
public class ViewPagerMapAdapter extends FragmentStatePagerAdapter {

    //Två arrayer som håller fragmenten och titeln som ska placeras i tab-listan
    ArrayList<Fragment> fragments = new ArrayList<>();
    ArrayList<String> tabTitles = new ArrayList<>();

    public ViewPagerMapAdapter(FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
    }

    //Lägger till och kopplar ihop fragment och titlar till vår ViewPagerAdapter
    public void addFragments(Fragment fragments, String titles) {
        this.fragments.add(fragments);
        this.tabTitles.add(titles);
    }

   /* //Skapar en fragmentmanager som hjälper oss hantera de olika fragmenten i ViewPagerAdapter
    public ViewPagerMapAdapter(android.app.FragmentManager fm) {
        super(fm);
    }*/

    //Hämta fragment från önskad position
    @Override
    public Fragment getItem(int position) {

        return fragments.get(position);
    }

    //Antal fragment - hjälper oss ställa in antalet tabbar och antalet fragment
    @Override
    public int getCount() {
        return fragments.size();
    }

    //För varje fragment i ViewPager hämtar vi motsvarande titel i tabTitles-arrayen
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position);
    }


}
