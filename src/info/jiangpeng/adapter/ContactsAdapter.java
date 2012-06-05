package info.jiangpeng.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import info.jiangpeng.R;
import info.jiangpeng.model.User;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends BaseAdapter {

    private Activity activity;
    private List<User> userList;

    public ContactsAdapter(Activity activity) {

        this.activity = activity;
        userList = new ArrayList<User>();
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

        LinearLayout layout;
        if (convertView == null) {
            layout = new LinearLayout(activity);
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) activity.getSystemService(inflater);
            vi.inflate(R.layout.contact_item, layout, true);
        } else {
            layout = (LinearLayout) convertView;
        }

        fillContentToView(getItem(position), layout);

        return layout;
    }

    private void fillContentToView(User user, LinearLayout layout) {
        ((ImageView) layout.findViewById(R.id.contact_image)).setImageDrawable(user.getImageDrawable());
        ((TextView) layout.findViewById(R.id.contact_name)).setText(user.getName());
        ((TextView) layout.findViewById(R.id.contact_signature)).setText(user.getSignature());
    }

    public void add(User user) {
        userList.add(user);
        notifyDataSetChanged();
    }
}
