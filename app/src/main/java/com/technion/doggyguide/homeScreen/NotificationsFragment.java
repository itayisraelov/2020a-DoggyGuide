package com.technion.doggyguide.homeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.technion.doggyguide.ExampleAdapter;
import com.technion.doggyguide.ExampleItem;
import com.technion.doggyguide.R;
import com.technion.doggyguide.homeScreen.alarm.walkAlarmActivity;
import com.technion.doggyguide.homeScreen.alarm.showerAlarmActivity;
import com.technion.doggyguide.homeScreen.alarm.eatAlarmActivity;
import java.util.ArrayList;


public class NotificationsFragment extends Fragment {
    private ArrayList<ExampleItem> mExampleList;
    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);
        mView = view;
        createExampleList();
        buildRecyclerView();

        return view;
    }

    private void changeItem(int position, String text) {
        mExampleList.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    private void createExampleList() {
        mExampleList = new ArrayList<>();
        mExampleList.add(new ExampleItem(R.mipmap.dog_walk, "Take your dog for a walk"));
        mExampleList.add(new ExampleItem(R.mipmap.dog_shower, "Give your dog shower"));
        mExampleList.add(new ExampleItem(R.mipmap.dog_eating, "Feed your dog"));
    }

    private void buildRecyclerView() {

        mRecyclerView = mView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ExampleAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeItem(position, "Clicked");
            }

            @Override
            public void onWalkAlarmClick(int position) { beginWalkAlarmActivity(); }

            @Override
            public void onShowerAlarmClick(int position) {
                beginShowerAlarmActivity();
            }

            @Override
            public void onFeedAlarmClick(int position) {
                beginFeedAlarmActivity();
            }
        });
    }
    private void beginWalkAlarmActivity(){
        Intent intent = new Intent(getActivity(), walkAlarmActivity.class);
        startActivity(intent);
    }
    private void beginShowerAlarmActivity(){
        Intent intent = new Intent(getActivity(), showerAlarmActivity.class);
        startActivity(intent);
    }
    private void beginFeedAlarmActivity(){
        Intent intent = new Intent(getActivity(), eatAlarmActivity.class);
        startActivity(intent);
    }

}
