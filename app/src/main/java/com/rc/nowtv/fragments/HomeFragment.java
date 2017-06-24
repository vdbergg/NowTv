package com.rc.nowtv.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rc.nowtv.R;
import com.rc.nowtv.activities.PlayerActivity;
import com.rc.nowtv.adapters.VideoListAdapter;
import com.rc.nowtv.models.Video;
import com.rc.nowtv.utils.C;
import com.rc.nowtv.utils.LocalStorage;
import com.rc.nowtv.utils.UtilDesign;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private ListView lvVideos;
    private VideoListAdapter videoListAdapter;

    private ArrayList<Video> listVideos;

    private View rootView;


    public HomeFragment() {
        // Required empty public constructor
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        initView();
        initValue();

        return rootView;
    }

    private void initView() {
        lvVideos = (ListView) rootView.findViewById(R.id.lv_videos);
        lvVideos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(rootView.getContext(), "Video posição: " + position + " clicado.", Toast.LENGTH_SHORT).show();
                LocalStorage.getInstance(rootView.getContext()).addToStorage(LocalStorage.OBJ_VIDEO, listVideos.get(position));
                startActivity(new Intent(rootView.getContext(), PlayerActivity.class));
            }
        });
    }

    private void initValue() {
        listVideos = new ArrayList<>();
        listVideos.add(new Video("Amazon Sat", C.URL_LIVE_VIDEO_AMAZONSAT, R.mipmap.ic_amazon_sat));
        listVideos.add(new Video("TV Escola", C.URL_LIVE_VIDEO_TV_ESCOLA, R.mipmap.ic_tv_escola));
        listVideos.add(new Video("TV Brasil", C.URL_LIVE_VIDEO_TV_BRASIL, R.mipmap.ic_tv_brasil));
        listVideos.add(new Video("TV Diário", C.URL_LIVE_VIDEO_TV_DIARIO, R.mipmap.ic_tv_diario));
        listVideos.add(new Video("NBR", C.URL_LIVE_VIDEO_NBR, R.mipmap.ic_nbr));
        listVideos.add(new Video("PUC TV - Aparecida", C.URL_LIVE_VIDEO_APARECIDA, R.mipmap.ic_tv_aparecida));
        listVideos.add(new Video("Rede Notícias", C.URL_LIVE_VIDEO_REDE_NOTICIAS, R.mipmap.ic_sbt));
        listVideos.add(new Video("SBT", C.URL_LIVE_VIDEO_SBT, R.mipmap.tigre));

        videoListAdapter = new VideoListAdapter(rootView.getContext(), listVideos);
        lvVideos.setAdapter(videoListAdapter);
        UtilDesign.setListViewHeightBasedOnChildren(lvVideos);

    }

}
