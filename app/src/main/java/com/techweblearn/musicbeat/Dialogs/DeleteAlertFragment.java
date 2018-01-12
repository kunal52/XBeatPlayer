package com.techweblearn.musicbeat.Dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;
import com.techweblearn.musicbeat.Utils.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunal on 27-12-2017.
 */



public class DeleteAlertFragment extends DialogFragment {


    public static DeleteAlertFragment create(Song song)
    {
        DeleteAlertFragment deleteAlertFragment=new DeleteAlertFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelable("song",song);
        deleteAlertFragment.setArguments(bundle);
        return deleteAlertFragment;
    }





    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Song song=getArguments().getParcelable("song");
        final List<Song>songs=new ArrayList<>();
        songs.add(song);
        return new AlertDialog.Builder(getActivity())
                .setMessage("Delete "+song.title)
                .setIcon(R.drawable.ic_delete_black_24dp)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Delete ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Util.deleteTracks(getActivity(),songs);
                    }
                })
                .create();
    }
}
