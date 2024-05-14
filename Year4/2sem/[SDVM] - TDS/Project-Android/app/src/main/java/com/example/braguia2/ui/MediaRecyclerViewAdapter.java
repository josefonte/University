package com.example.braguia2.ui;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.braguia2.R;
import com.example.braguia2.model.Trails.Conteudo;
import com.example.braguia2.model.User.User;
import com.example.braguia2.viewmodel.UserViewModel;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MediaRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<Conteudo> mValues;
    private FragmentManager mFragmentManager;
    private LifecycleOwner lifecycle;
    private Context mContext;
    private OnDownloadClickListener mDownloadListener;

    private static final int VIEW_TYPE_AUDIO = 0;
    private static final int VIEW_TYPE_VIDEO = 1;
    private static final int VIEW_TYPE_IMAGE = 2;

    public MediaRecyclerViewAdapter(LifecycleOwner x, List<Conteudo> items, FragmentManager fragmentManager, Context context) {
        lifecycle = x;
        mValues = items;
        mFragmentManager = fragmentManager;
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        Conteudo conteudo = mValues.get(position);
        switch (conteudo.getMediaType()) {
            case "R":
                return VIEW_TYPE_AUDIO;
            case "V":
                return VIEW_TYPE_VIDEO;
            case "I":
            default:
                return VIEW_TYPE_IMAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;
        switch (viewType) {
            case VIEW_TYPE_AUDIO:
                itemView = inflater.inflate(R.layout.fragment_media_audio, parent, false);
                return new AudioViewHolder(itemView);
            case VIEW_TYPE_VIDEO:
                itemView = inflater.inflate(R.layout.fragment_media_video, parent, false);
                return new VideoViewHolder(itemView);
            case VIEW_TYPE_IMAGE:
                itemView = inflater.inflate(R.layout.fragment_media_imagem, parent, false);
                return new ImageViewHolder(itemView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Conteudo conteudo = mValues.get(position);
        UserViewModel userViewModel = new ViewModelProvider((ViewModelStoreOwner) mContext).get(UserViewModel.class);

        try {
            userViewModel.getUser().observe(this.lifecycle, user -> {
                switch (holder.getItemViewType()) {
                    case VIEW_TYPE_AUDIO:
                        ((AudioViewHolder) holder).bindAudio(conteudo,user);
                        break;
                    case VIEW_TYPE_VIDEO:
                        ((VideoViewHolder) holder).bindVideo(conteudo,user);
                        break;
                    case VIEW_TYPE_IMAGE:
                        ((ImageViewHolder) holder).bindImage(conteudo,user);
                        break;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface OnDownloadClickListener {
        void onDownloadClick(String mediaUrl);
    }

    public void setOnDownloadClickListener(OnDownloadClickListener listener) {
        mDownloadListener = listener;
    }

    private class AudioViewHolder extends RecyclerView.ViewHolder {
        private final Button audioButton;
        private MediaPlayer mediaPlayer;
        private boolean isPlaying = false;

        private final ImageView btnTransferencias;

        AudioViewHolder(View itemView) {
            super(itemView);
            audioButton = itemView.findViewById(R.id.cardaudio);
            btnTransferencias = itemView.findViewById(R.id.botaoTransferencias);
        }


        void bindAudio(Conteudo conteudo, User user) {
            if (user.getUsername().equals("premium_user")) {
                String audioUrl = conteudo.getMediaFile().replace("http","https");
                File audioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), audioUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", ""));
                audioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isPlaying) {
                            if (audioFile.exists()){
                                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED){
                                    Uri uri = Uri.fromFile(audioFile);
                                    startAudio(uri.toString());
                                    audioButton.setText("Pause");
                                }
                                else{
                                    ActivityCompat.requestPermissions((Activity) mContext,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 303);
                                    startAudio(conteudo.getMediaFile());
                                    audioButton.setText("Pause");
                                }
                            }
                            else{
                                startAudio(conteudo.getMediaFile());
                                audioButton.setText("Pause");
                            }
                        } else {
                            pauseAudio();
                            audioButton.setText("Play");
                        }
                    }
                });
                btnTransferencias.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String audioUrl = conteudo.getMediaFile().replace("http","https");
                        downloadAudio(audioUrl);
                    }
                });
            }
        }

        public void downloadAudio(String mediaUrl) {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // request it
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 133);

            }
            initiateDownload(mediaUrl);
            //} else {
            //    initiateDownload(mediaUrl);
            //}
        }

        private void initiateDownload(String mediaUrl) {
            try {

                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mediaUrl));
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mediaUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", ""));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(mediaUrl.replace("https://764f-193-137-92-72.ngrok-free.app/", ""));

                downloadManager.enqueue(request);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void startAudio(String mediaFile) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mediaFile);
                mediaPlayer.prepareAsync();

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        releaseMediaPlayer();
                        audioButton.setText("Play");
                        isPlaying = false;
                    }
                });

                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        isPlaying = true;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void pauseAudio() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
            }
        }

        private void releaseMediaPlayer() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                isPlaying = false;
            }
        }

    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {
        private final StyledPlayerView videoView;
        private SimpleExoPlayer player;
        private final ImageView downloadbtn;

        VideoViewHolder(View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.cardvideo);
            player = new SimpleExoPlayer.Builder(mContext).build();
            downloadbtn = itemView.findViewById(R.id.botaoTransferencias);
        }

        void bindVideo(Conteudo conteudo, User user) {
            if (user.getUsername().equals("premium_user")){
                videoView.setPlayer(player);
                String videoUrl = conteudo.getMediaFile().replace("http","https");




                File videoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), videoUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", ""));
                if (videoFile.exists()) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        Log.d("CONECTIVIDADE REDUZIDA", "SAQUEI A IMAGEM LOCALMENTE");
                        Uri uri = Uri.fromFile(videoFile); // Convert File to Uri
                        MediaItem mediaItem = MediaItem.fromUri(uri);
                        player.setMediaItem(mediaItem);
                    }
                    else{
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 302);
                        MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                        player.setMediaItem(mediaItem);
                    }
                } else {
                    MediaItem mediaItem = MediaItem.fromUri(videoUrl);
                    player.setMediaItem(mediaItem);
                }

                player.prepare();
                player.setPlayWhenReady(true);

                downloadbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String vidUrl = conteudo.getMediaFile().replace("http","https");
                        downloadVideo(vidUrl);
                    }
                });
            }
        }
        public void downloadVideo(String mediaUrl) {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // request it
                ActivityCompat.requestPermissions((Activity) mContext,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);

            }
            initiateDownload(mediaUrl);
            //} else {
            //    initiateDownload(mediaUrl);
            //}
        }

        private void initiateDownload(String mediaUrl) {
            try {

                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mediaUrl));
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mediaUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", ""));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(mediaUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", ""));

                downloadManager.enqueue(request);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    }

    private class ImageViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final ImageView downloadBtn;

        ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cardimage);
            downloadBtn = itemView.findViewById(R.id.botaoTransferencias);
        }

        void bindImage(Conteudo conteudo, User user) {
            if (user.getUsername().equals("premium_user")) {
                String imgUrl = conteudo.getMediaFile().replace("http","https");
                File imageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imgUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", ""));
                Log.d("IMAGEFILE", imageFile.toString());
                Log.d("IMAGEFILE", Boolean.toString(imageFile.exists()));
                if (imageFile.exists()) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        Log.d("CONECTIVIDADE REDUZIDA", "SAQUEI A IMAGEM LOCALMENTE");
                        Uri uri = Uri.fromFile(imageFile); // Convert File to Uri
                        Log.d("URI", uri.toString());
                        Picasso.get().load(uri.toString()).into(imageView);
                    }
                    else{
                        ActivityCompat.requestPermissions((Activity) mContext,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 301);

                        Picasso.get().load(imgUrl).into(imageView);
                    }
                } else {
                    Picasso.get().load(imgUrl).into(imageView);
                }
                downloadBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadImg(imgUrl);
                    }
                });
            }
        }

        public void downloadImg(String mediaUrl) {
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // request it
                    ActivityCompat.requestPermissions((Activity) mContext,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

                }
            initiateDownload(mediaUrl);
            //} else {
            //    initiateDownload(mediaUrl);
            //}
        }

        private void initiateDownload(String mediaUrl) {
            try {

                DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mediaUrl));
                Log.d("REQUEST", request.toString());
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, mediaUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", ""));
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(mediaUrl.replace("https://764f-193-137-92-72.ngrok-free.app/media/", "")); // Probably useless

                downloadManager.enqueue(request);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
