package com.techweblearn.musicbeat.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.techweblearn.musicbeat.Models.Song;
import com.techweblearn.musicbeat.R;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.techweblearn.musicbeat.Utils.Util.getReadableDurationString;

/**
 * Created by Kunal on 27-12-2017.
 */

public class SongDetailDialog extends DialogFragment implements View.OnClickListener {

    String TAG="SongDetails";

    @NonNull
    public static SongDetailDialog create(Song song) {
        SongDetailDialog dialog = new SongDetailDialog();
        Bundle args = new Bundle();
        args.putParcelable("song", song);
        dialog.setArguments(args);
        return dialog;
    }

    private static Spanned makeTextWithTitle(@NonNull Context context, int titleResId, String text) {
        return Html.fromHtml("<b>" + context.getResources().getString(titleResId) + ": " + "</b>" + text);
    }

    private static String getFileSizeString(long sizeInBytes) {
        long fileSizeInKB = sizeInBytes / 1024;
        long fileSizeInMB = fileSizeInKB / 1024;
        return fileSizeInMB + " MB";
    }

    @BindView(R.id.file_name)TextView fileName;
    @BindView(R.id.file_path)TextView filePath;
    @BindView(R.id.file_size)TextView fileSize;
    @BindView(R.id.file_format)TextView fileFormat;
    @BindView(R.id.track_length)TextView trackLength;
    @BindView(R.id.bitrate)TextView bitRate;
    @BindView(R.id.sampling_rate)TextView samplingRate;
    @BindView(R.id.dismiss)Button dismiss;

    Unbinder unbinder;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.dialog_file_details,container,false);
        unbinder= ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle=getArguments();
        Song song=bundle.getParcelable("song");
        setDetail(song);
        getDialog().setTitle("Details");

        dismiss.setOnClickListener(this);

    }


    private void setDetail(Song song)
    {
        fileName.setText(makeTextWithTitle(getContext(), R.string.label_file_name, "-"));
        filePath.setText(makeTextWithTitle(getContext(), R.string.label_file_path, "-"));
        fileSize.setText(makeTextWithTitle(getContext(), R.string.label_file_size, "-"));
        fileFormat.setText(makeTextWithTitle(getContext(), R.string.label_file_format, "-"));
        trackLength.setText(makeTextWithTitle(getContext(), R.string.label_track_length, "-"));
        bitRate.setText(makeTextWithTitle(getContext(), R.string.label_bit_rate, "-"));
        samplingRate.setText(makeTextWithTitle(getContext(), R.string.label_sampling_rate, "-"));

        try {
            if (song != null) {
                final File songFile = new File(song.data);
                if (songFile.exists()) {
                    AudioFile audioFile = AudioFileIO.read(songFile);
                    AudioHeader audioHeader = audioFile.getAudioHeader();

                    fileName.setText(makeTextWithTitle(getContext(), R.string.label_file_name, songFile.getName()));
                    filePath.setText(makeTextWithTitle(getContext(), R.string.label_file_path, songFile.getAbsolutePath()));
                    fileSize.setText(makeTextWithTitle(getContext(), R.string.label_file_size, getFileSizeString(songFile.length())));
                    fileFormat.setText(makeTextWithTitle(getContext(), R.string.label_file_format, audioHeader.getFormat()));
                    trackLength.setText(makeTextWithTitle(getContext(), R.string.label_track_length, getReadableDurationString(audioHeader.getTrackLength() * 1000)));
                    bitRate.setText(makeTextWithTitle(getContext(), R.string.label_bit_rate, audioHeader.getBitRate() + " kb/s"));
                    samplingRate.setText(makeTextWithTitle(getContext(), R.string.label_sampling_rate, audioHeader.getSampleRate() + " Hz"));
                }
            }
        } catch (@NonNull CannotReadException | IOException | TagException | ReadOnlyFileException | InvalidAudioFrameException e) {
            Log.e(TAG, "error while reading the song file", e);
        }

    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }
}
