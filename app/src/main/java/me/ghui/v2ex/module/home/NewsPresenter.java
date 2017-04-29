package me.ghui.v2ex.module.home;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import me.ghui.v2ex.network.APIService;
import me.ghui.v2ex.network.bean.NewsInfo;

/**
 * Created by ghui on 03/04/2017.
 */

public class NewsPresenter implements NewsContract.IPresenter {

    private NewsContract.IView mView;

    public NewsPresenter(NewsContract.IView view) {
        mView = view;
    }


    @Override
    public void start() {
        APIService.get()
                .homeNews("all")
                .compose(mView.<NewsInfo>rx())
                .subscribe(new Consumer<NewsInfo>() {
                    @Override
                    public void accept(@NonNull NewsInfo newsInfo) throws Exception {
                        mView.fillView(newsInfo, false);
                    }
                });

    }

    @Override
    public void loadMore() {
        APIService.get()
                .recentNews()
                .compose(mView.<NewsInfo>rx())
                .subscribe(new Consumer<NewsInfo>() {
                    @Override
                    public void accept(@NonNull NewsInfo newsInfo) throws Exception {
                        mView.fillView(newsInfo, true);
                    }
                });
    }


}

