package edu.feup.stockportfolio.ui;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import edu.feup.stockportfolio.R;

public class PortfolioDetailsActivity extends FragmentActivity {
    private final static String TAG = "PortfolioDetailsActivity";

    private PagerSlidingTabStrip tabs_;
    private ViewPager pager_;
    private PortfolioPagerAdapter adapter_;

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.portfolio_details);

        ActionBar action_bar = getActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        tabs_ = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        pager_ = (ViewPager) findViewById(R.id.pager);
        adapter_ = new PortfolioPagerAdapter(getSupportFragmentManager());

        pager_.setAdapter(adapter_);
        tabs_.setViewPager(pager_);
    }

    public class PortfolioPagerAdapter extends FragmentPagerAdapter {
        private final int[] TITLES = {
                R.string.history,
                R.string.shares,
                R.string.values
        };

        public PortfolioPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(TITLES[position]);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return PortfolioHistoryFragment.newInstance();
                case 1:
                    return PortfolioSharesFragment.newInstance();
                case 2:
                    return PortfolioValuesFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
