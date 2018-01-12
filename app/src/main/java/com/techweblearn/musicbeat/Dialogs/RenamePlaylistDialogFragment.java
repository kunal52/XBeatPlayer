package com.techweblearn.musicbeat.Dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.PlaylistsUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Kunal on 02-01-2018.
 */

public class RenamePlaylistDialogFragment extends DialogFragment implements View.OnClickListener {

    @BindView(R.id.playlist_name)EditText playlist_name;
    @BindView(R.id.cancel_button)Button cancel_button;
    @BindView(R.id.add_button)Button add_button;
    @BindView(R.id.textView)TextView title;

    @NonNull
    public static RenamePlaylistDialogFragment create(int playlist_id) {
        RenamePlaylistDialogFragment dialog = new RenamePlaylistDialogFragment();
        Bundle args = new Bundle();
        args.putInt("playlist_id", playlist_id);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    public static RenamePlaylistDialogFragment create() {
        RenamePlaylistDialogFragment dialog = new RenamePlaylistDialogFragment();
        return dialog;
    }


    Unbinder unbinder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_playlist_dialog_fragment,container,false);
        unbinder= ButterKnife.bind(this,view);
        getDialog().setTitle("Rename PlayList");
        title.setText("Rename Playlist");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        add_button.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.add_button:
                PlaylistsUtil.renamePlaylist(getActivity(),getArguments().getInt("playlist_id"),playlist_name.getText().toString());
                renamePlaylistCallback.onPlaylistRename();
                this.dismiss();
                break;
            case R.id.cancel_button:
                this.dismiss();
                break;
        }
    }

    RenamePlaylistCallback renamePlaylistCallback;
    public void setCallback(RenamePlaylistCallback renamePlaylistCallback)
    {
        this.renamePlaylistCallback=renamePlaylistCallback;
    }
    public interface RenamePlaylistCallback
    {
        void onPlaylistRename();
    }
}
