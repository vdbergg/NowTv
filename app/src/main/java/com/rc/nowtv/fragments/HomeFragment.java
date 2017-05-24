package com.rc.nowtv.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.rc.nowtv.R;
import com.rc.nowtv.adapters.VideoListAdapter;
import com.rc.nowtv.models.Video;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private ListView lvVideos;
    private VideoListAdapter videoListAdapter;
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
            }
        });
    }

    private void initValue() {
        ArrayList<Video> listVideos = new ArrayList<>();
        listVideos.add(new Video("Atiradores de elite - Afeganistão", "4:20", "1 anos atrás", R.mipmap.sniper));
        listVideos.add(new Video("Aventuras no Canadá", "6:24", "2 anos atrás", R.mipmap.alpinista));
        listVideos.add(new Video("Dubai nas nuvens", "1:40", "3 meses atrás", R.mipmap.cidade));
        listVideos.add(new Video("Jogo inesquecível", "10:00", "1 anos atrás", R.mipmap.furebol_americano));
        listVideos.add(new Video("Mini homem de ferro", "1:30", "1 mês atrás", R.mipmap.homem_ferro));
        listVideos.add(new Video("Montanhas Alemanha", "3:10", "4 meses atrás", R.mipmap.horizonte));
        listVideos.add(new Video("Animais mais fortes do mundo", "4:20", "1 anos atrás", R.mipmap.tigre));

        videoListAdapter = new VideoListAdapter(rootView.getContext(), listVideos);
        lvVideos.setAdapter(videoListAdapter);

    }

}
