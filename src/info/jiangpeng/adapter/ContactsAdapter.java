package info.jiangpeng.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import info.jiangpeng.R;
import info.jiangpeng.model.User;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {

    private ArrayList<User> userList;
    private Activity activity;

    public ContactsAdapter(Activity activity) {
        userList = new ArrayList<User>();
        this.activity = activity;
    }


    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return Long.parseLong(userList.get(i).getId());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("------------position = " + position);
        RelativeLayout layout;
        if (convertView == null) {
            layout = new RelativeLayout(activity);
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(inflater);
            vi.inflate(R.layout.contact_item, layout, true);
        } else {
            layout = (RelativeLayout) convertView;
        }

        fillContentToView(getItem(position), layout);

        return layout;
    }

    private void fillContentToView(User user, RelativeLayout layout) {
        ((ImageView) layout.findViewById(R.id.contact_image)).setImageDrawable(user.getImageDrawable());
        ((TextView) layout.findViewById(R.id.contact_name)).setText(user.getName());
        ((TextView) layout.findViewById(R.id.contact_signature)).setText(user.getSignature());
    }

    public void add(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }
}
