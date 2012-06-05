package info.jiangpeng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class UserBookTabListener<T extends Fragment> implements ActionBar.TabListener {

    private final Activity activity;
    private final Class<T> clazz;
    private Fragment fragment;

    public UserBookTabListener(Activity activity, String tag, Class clazz) {
        this.activity = activity;

        this.clazz = clazz;

        //if remove this method call, when device orientation change, it will attach again.
        detachFragmentIfAttached(activity, tag);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        System.out.println("-------selected");
        if (fragment == null) {
            fragment = Fragment.instantiate(activity, clazz.getName());
            fragmentTransaction.add(android.R.id.content, fragment, tab.getTag().toString());
        } else {
            fragmentTransaction.attach(fragment);
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragment != null) {
            System.out.println("----------unselect");
            fragmentTransaction.detach(fragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (fragment != null) {
            fragmentTransaction.attach(fragment);
        }
    }

    private void detachFragmentIfAttached(Activity activity, String tag) {
        fragment = activity.getFragmentManager().findFragmentByTag(tag);
        if (fragment != null && !fragment.isDetached()) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.detach(fragment);
            ft.commit();
        }
    }
}
