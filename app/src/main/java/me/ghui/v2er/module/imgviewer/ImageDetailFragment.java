package me.ghui.v2er.module.imgviewer;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.orhanobut.logger.Logger;

import me.ghui.v2er.R;
import me.ghui.v2er.general.GlideApp;
import me.ghui.v2er.network.Constants;
import me.ghui.v2er.util.UriUtils;
import me.ghui.v2er.util.Utils;
import me.ghui.v2er.util.Voast;

import static com.bumptech.glide.request.target.Target.SIZE_ORIGINAL;


/**
 * Created by ghui on 5/18/16.
 */
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private SubsamplingScaleImageView mPhotoView;
    private ProgressBar progressBar;
    private View mImgRootView;
    private static String IMG_URL = Constants.PACKAGE_NAME + "_img_url";

    public static ImageDetailFragment newInstance(String url) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString(IMG_URL, url);
        Log.e("img", "url: " + url);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString(IMG_URL) : null;
        mImageUrl = UriUtils.checkSchema(mImageUrl);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_detail_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImgRootView = view.findViewById(R.id.img_viewer_root);
        progressBar = (ProgressBar) view.findViewById(R.id.loading);
        mPhotoView = (SubsamplingScaleImageView) view.findViewById(R.id.imageview);
        mPhotoView.setOnClickListener(v -> getActivity().finish());
        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });
        loadImage();
    }

    private void loadImage() {
        progressBar.setVisibility(View.VISIBLE);
        int maxSize = Utils.getMaxTextureSize();
        Logger.d("maxSize: " + maxSize);
        GlideApp.with(getContext())
                .load(mImageUrl)
                .fitCenter()
                .into(new SimpleTarget<Drawable>(maxSize, SIZE_ORIGINAL) {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        progressBar.setVisibility(View.GONE);
                        Voast.show("图片加载出错");
                    }

                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        progressBar.setVisibility(View.GONE);
//                        mPhotoView.animateScale();
                        if (resource instanceof BitmapDrawable) {
                            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
                            mPhotoView.setImage(ImageSource.bitmap(bitmap));
                            paletteBg(bitmap);
                        }
                    }
                });

    }

    private void paletteBg(Bitmap bitmap) {
        Palette.from(bitmap)
                .generate(palette -> {
                    //柔和色中的暗色
                    Palette.Swatch textSwatch = palette.getDarkMutedSwatch();
                    if (textSwatch == null) {
                        Logger.e("textSwatch is null");
                        return;
                    }
                    mImgRootView.setBackgroundColor(textSwatch.getRgb());
//                    titleColorText.setTextColor(textSwatch.getTitleTextColor());
                    if (getActivity() instanceof IndicatorTextDelegator) {
                        ((IndicatorTextDelegator) getActivity()).changeIndicatorColor(textSwatch.getBodyTextColor());
                    }
                });
    }


}
