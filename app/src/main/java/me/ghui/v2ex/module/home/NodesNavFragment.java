package me.ghui.v2ex.module.home;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import javax.inject.Inject;

import butterknife.BindView;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import me.ghui.v2ex.R;
import me.ghui.v2ex.adapter.base.CommonAdapter;
import me.ghui.v2ex.injector.component.DaggerNodesNavComponent;
import me.ghui.v2ex.injector.module.NodesNavModule;
import me.ghui.v2ex.module.base.BaseFragment;
import me.ghui.v2ex.network.bean.NodesNavInfo;
import me.ghui.v2ex.widget.BaseRecyclerView;

/**
 * Created by ghui on 22/03/2017.
 */

public class NodesNavFragment extends BaseFragment<NodesNavConstract.IPresenter> implements NodesNavConstract.IView {

    @Inject
    CommonAdapter<NodesNavInfo.Item> mAdapter;
    @BindView(R.id.common_recyclerview)
    BaseRecyclerView mRecyclerView;

    public static NodesNavFragment newInstance() {
        Bundle args = new Bundle();
        NodesNavFragment fragment = new NodesNavFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int attachLayoutRes() {
        return R.layout.common_recyclerview_layout;
    }

    @Override
    protected void startInject() {
        DaggerNodesNavComponent.builder()
                .appComponent(getAppComponent())
                .nodesNavModule(new NodesNavModule(this))
                .build().inject(this);
    }

    @Override
    protected void init() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected PtrHandler attachPtrHandler() {
        return new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPresenter.start();
            }
        };
    }

    @Override
    public void fillView(NodesNavInfo navInfo) {
        mAdapter.setData(navInfo);
    }
}
