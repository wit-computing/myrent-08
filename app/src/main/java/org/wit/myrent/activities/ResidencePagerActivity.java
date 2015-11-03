package org.wit.myrent.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import org.wit.myrent.R;

import org.wit.myrent.app.MyRentApp;
import org.wit.myrent.models.Portfolio;
import org.wit.myrent.models.Residence;

import java.util.ArrayList;
import java.util.UUID;

import static org.wit.android.helpers.LogHelpers.info;

public class ResidencePagerActivity extends FragmentActivity implements ViewPager.OnPageChangeListener{
  private ViewPager viewPager;
  private ArrayList<Residence> residences;
  private Portfolio portfolio;
  private PagerAdapter pagerAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);

    viewPager = new ViewPager(this);
    viewPager.setId(R.id.viewPager);
    setContentView(viewPager);
    setResidenceList();

    pagerAdapter = new PagerAdapter(getSupportFragmentManager(), residences);
    viewPager.setAdapter(pagerAdapter);
    viewPager.addOnPageChangeListener(this);

    setCurrentItem();
  }

  private void setResidenceList()
  {
    MyRentApp app = (MyRentApp) getApplication();
    portfolio = app.portfolio;
    residences = portfolio.residences;
  }

  @Override
  public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
  {
    info(this, "onPageScrolled: position "+position+" positionOffset "+positionOffset+" positionOffsetPixels "+positionOffsetPixels);
    Residence residence = residences.get(position);
    if (residence.geolocation != null)
    {
      setTitle(residence.geolocation);
    }
  }

  @Override
  public void onPageSelected(int position)
  {

  }

  @Override
  public void onPageScrollStateChanged(int state)
  {

  }

  /*
 * Ensure selected residence is shown in details view
 */
  private void setCurrentItem()
  {
    UUID res = (UUID) getIntent().getSerializableExtra(ResidenceFragment.EXTRA_RESIDENCE_ID);
    for (int i = 0; i < residences.size(); i++)
    {
      if (residences.get(i).id.toString().equals(res.toString()))
      {
        viewPager.setCurrentItem(i);
        break;
      }
    }
  }

  class PagerAdapter extends FragmentStatePagerAdapter
  {
    private ArrayList<Residence>  residences;

    public PagerAdapter(FragmentManager fm, ArrayList<Residence> residences)
    {
      super(fm);
      this.residences = residences;
    }

    @Override
    public int getCount()
    {
      return residences.size();
    }

    @Override
    public Fragment getItem(int pos)
    {
      Residence residence = residences.get(pos);
      Bundle args = new Bundle();
      args.putSerializable(ResidenceFragment.EXTRA_RESIDENCE_ID, residence.id);
      ResidenceFragment fragment = new ResidenceFragment();
      fragment.setArguments(args);
      return fragment;
    }
  }

}

