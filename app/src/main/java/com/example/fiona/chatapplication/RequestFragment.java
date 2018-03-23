package com.example.fiona.chatapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RequestFragment extends Fragment {

    private RecyclerView myRequestList;
    private View myMainView;
    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myMainView = inflater.inflate(R.layout.fragment_request, container, false);
        myRequestList = (RecyclerView)myMainView.findViewById(R.id.request_list);
        myRequestList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myRequestList.setLayoutManager(linearLayoutManager);

        return myMainView;
    }

}
