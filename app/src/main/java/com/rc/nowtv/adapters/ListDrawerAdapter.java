package com.rc.nowtv.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rc.nowtv.R;
import com.rc.nowtv.models.Member;

import java.util.ArrayList;

/**
 * Created by berg on 27/05/17.
 */

public class ListDrawerAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private ArrayList<Member> itens;

    public ListDrawerAdapter(Context context, ArrayList<Member> itens) {
        this.itens = itens;
        mInflater = LayoutInflater.from(context);
    }

    /**
     * Retorna a quantidade de itens
     *
     * @return
     */
    public int getCount()
    {
        return itens.size();
    }

    /**
     * Retorna o item de acordo com a posicao dele na tela.
     *
     * @param position
     * @return
     */
    public Member getItem(int position) {
        return itens.get(position);
    }

    /**
     * Sem implementação
     *
     * @param position
     * @return
     */
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {
        Member item = itens.get(position);
        view = mInflater.inflate(R.layout.item_member, null);

        ((ImageView) view.findViewById(R.id.ic_user_list_members)).setImageResource(item.getIconeRId());
        ((TextView) view.findViewById(R.id.tv_username_member)).setText(item.getTexto());

        return view;
    }

}
