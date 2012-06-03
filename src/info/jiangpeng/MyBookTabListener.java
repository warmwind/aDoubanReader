package info.jiangpeng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class MyBookTabListener<T extends Fragment> implements ActionBar.TabListener {

    private final Activity activity;
    private final Class<T> clazz;
    private Fragment fragment;

    public MyBookTabListener(Activity activity, Class clazz) {
        this.activity = activity;

        this.clazz = clazz;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // Check if the fragment is already initialized
        System.out.println("-------selected");
        if (fragment == null) {
            // If not, instantiate and add it to the activity
            fragment = Fragment.instantiate(activity, clazz.getName());
            fragmentTransaction.add(android.R.id.content, fragment, tab.getTag().toString());
        } else {
            // If it exists, simply attach it in order to show it
            fragmentTransaction.attach(fragment);
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragment != null) {
            fragmentTransaction.detach(fragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragment != null) {
            fragmentTransaction.attach(fragment);
        }
    }
}
