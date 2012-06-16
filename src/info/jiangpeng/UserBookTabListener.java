package info.jiangpeng;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;

public class UserBookTabListener<T extends Fragment> implements ActionBar.TabListener {

    private final Activity activity;
    private BookListFragment bookListFragment;

    public UserBookTabListener(Activity activity, String tag, BookListFragment bookListFragment) {
        this.activity = activity;
        this.bookListFragment = bookListFragment;


        //if remove this method call, when device orientation change, it will attach again.
        detachFragmentIfAttached(activity, tag);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (bookListFragment != null) {
            fragmentTransaction.attach(bookListFragment);
            bookListFragment.executeSearchByReadingStatus();
            System.out.println("------------attach "+ tab.getTag().toString() + " to fragment");
        }
        else{
            System.out.println("------------Add " + tab.getTag().toString() + " to fragment");
            bookListFragment = (BookListFragment) Fragment.instantiate(activity, BookListFragment.class.getName());
            bookListFragment.executeSearchByReadingStatus();
            fragmentTransaction.add(android.R.id.content, bookListFragment, tab.getTag().toString());
        }

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (bookListFragment != null) {
            fragmentTransaction.detach(bookListFragment);
        }
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        if (bookListFragment != null) {
            fragmentTransaction.attach(bookListFragment);
        }
    }

    private void detachFragmentIfAttached(Activity activity, String tag) {
        bookListFragment = (BookListFragment) activity.getFragmentManager().findFragmentByTag(tag);
        if (bookListFragment != null && !bookListFragment.isDetached()) {
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            ft.detach(bookListFragment);
            ft.commit();
        }
    }
}
