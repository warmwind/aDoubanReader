package info.jiangpeng.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import info.jiangpeng.R;
import info.jiangpeng.activity.MainActivity;
import info.jiangpeng.adapter.ContactsAdapter;
import info.jiangpeng.helper.RequestParams;
import info.jiangpeng.sign.CustomOAuthConsumer;
import info.jiangpeng.sign.OAuthFactory;
import info.jiangpeng.task.UserParseTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContactsFragment extends Fragment {

    private GridView contactsGridView;
    private RequestParams requestParams;
    private ContactsAdapter contactsAdapter;

    private View contactView;
    private UserBooksFragment userBooksFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.contacts_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactView = getView().findViewById(R.id.contact_view);

        contactsGridView = (GridView) getView().findViewById(R.id.contact_grid);
        contactsAdapter = new ContactsAdapter(getActivity());
        contactsGridView.setAdapter(contactsAdapter);


        contactsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                requestParams.setUserId(String.valueOf(id));
                requestParams.setUserName(contactsAdapter.getItem(position).getName());
                requestParams.setAccessToken(requestParams.getAccessToken());
                requestParams.setAccessTokenSecret(requestParams.getAccessTokenSecret());
                requestParams.setUserChanged(true);
                contactView.setVisibility(View.GONE);
//                userBooksFragment = (UserBooksFragment) Fragment.instantiate(getActivity(), UserBooksFragment.class.getName());
                MainActivity activity = (MainActivity) getActivity();
                System.out.println("------------requestParams.getUserId() = " + requestParams.getUserId());
                System.out.println("------------requestParams.getUserName() = " + requestParams.getUserName());
                activity.setRequestParams(requestParams);
                activity.showMyBooksTab();

            }
        });


    }

    public void searchContacts() {
        contactView.setVisibility(View.VISIBLE);

        System.out.println("-----------------on contact fragment resume: requestParams is " + requestParams);
        if (requestParams == null){
            return;
        }
        new SearchContactsTask().execute();
    }

    public void setRequestParams(RequestParams requestParams) {
        System.out.println("------------set requestParams for contact = " + requestParams);
        this.requestParams = requestParams;
    }

    private class SearchContactsTask extends AsyncTask<Void, Integer, String>{

        @Override
        protected String doInBackground(Void... voids) {
            CustomOAuthConsumer consumerSignedIn = OAuthFactory.createConsumer(requestParams.getAccessToken(), requestParams.getAccessTokenSecret());

            String rawJsonString = "";
            try {
                rawJsonString = consumerSignedIn.executeAfterSignIn("http://api.douban.com/people/" + requestParams.getUserId() + "/contacts?alt=json");
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("------------contacts rawJsonString = " + rawJsonString);
            return rawJsonString;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray entry = jsonObject.getJSONArray("entry");
                int length = entry.length();
                for (int i = 0; i < length; i++) {
                    new UserParseTask(contactsAdapter).execute(entry.get(i).toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
