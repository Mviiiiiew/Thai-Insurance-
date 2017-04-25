package com.example.thaiinsurancesoftware.thaiinsurance.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.example.thaiinsurancesoftware.thaiinsurance.R;
import com.example.thaiinsurancesoftware.thaiinsurance.adapter.ImageListAdapter;
import com.example.thaiinsurancesoftware.thaiinsurance.model.ImageList;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by nuuneoi on 11/16/2014.
 */
@SuppressWarnings("unused")
public class SecondFragment extends Fragment {

    private DatabaseReference mReference;
    private ArrayList<ImageList> imageLists;
    private ListView listViewImage;
    private ImageListAdapter adapter;
    private ProgressDialog dialog;
    SwipeRefreshLayout swipeRefreshLayout;

    public SecondFragment() {
        super();
    }

    @SuppressWarnings("unused")
    public static SecondFragment newInstance() {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second, container, false);
        initInstances(rootView, savedInstanceState);

        return rootView;
    }


    private void reloadData() {

        imageLists = new ArrayList<>();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please loading list image....");
        dialog.show();

        mReference = FirebaseDatabase.getInstance().getReference(FirstFragment.FB_DATABASE_PATH);

        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dialog.dismiss();
                swipeRefreshLayout.setRefreshing(false);
                imageLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageList img = snapshot.getValue(ImageList.class);
                    imageLists.add(img);

                }

                adapter = new ImageListAdapter(getActivity(), imageLists);
                listViewImage.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();
            }
        });
    }

    private void init(Bundle savedInstanceState) {
        // Init Fragment level's variable(s) here
    }

    @SuppressWarnings("UnusedParameters")
    private void initInstances(View rootView, Bundle savedInstanceState) {
        // Init 'View' instance(s) with rootView.findViewById here
        listViewImage = (ListView) rootView.findViewById(R.id.listViewImage);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadData();

            }
        });
        listViewImage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                swipeRefreshLayout.setEnabled(i == 0);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Save Instance State Here
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    @SuppressWarnings("UnusedParameters")
    private void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore Instance State here
    }

}
