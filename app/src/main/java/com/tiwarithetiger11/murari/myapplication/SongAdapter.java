package com.tiwarithetiger11.murari.myapplication;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    ArrayList<SongInfo> _song;
    Context context;

   OnitemClickListener onitemClickListener;


   public interface OnitemClickListener
    {

        void onItemClick(SongHolder b, View v, SongInfo obj, int position);
    }


    public void setOnitemClickListener(OnitemClickListener onitemClickListener)
    {
        this.onitemClickListener=onitemClickListener;
    }


    public SongAdapter(ArrayList<SongInfo> _song, Context context) {
        this._song = _song;
        this.context = context;
    }

    @NonNull
    @Override
    public SongHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(context).inflate(R.layout.list,viewGroup,false);
        return new SongHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SongHolder songHolder, final int i) {

       final SongInfo c=_song.get(i);
       songHolder.songName.setText(c.songname);
        songHolder.artistName.setText(c.artist);
        try {
            Log.e("path", "onBindViewHolder: " + Uri.parse(c.path));
            if(c.path!="null")
                songHolder.imageView.setImageURI(Uri.parse(c.path));
        }catch (NullPointerException n)
        {
            n.getCause();
        }
        songHolder.btn.setTag(i);

    }

    @Override
    public int getItemCount() {
        return _song.size();
    }

    public static class SongHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView songName,artistName;
        ImageButton btn;
        ImageView imageView;
        public SongHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            songName=itemView.findViewById(R.id.textView);
            artistName=itemView.findViewById(R.id.textView2);
            btn=itemView.findViewById(R.id.btn);
            imageView=itemView.findViewById(R.id.iv);
        }

        @Override
        public void onClick(View view) {
            MainActivity.sogN((Integer) btn.getTag());

        }
    }


}
