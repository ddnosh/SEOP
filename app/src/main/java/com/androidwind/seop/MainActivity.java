package com.androidwind.seop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.androidwind.seop.ui.refresh.SmartRefreshLayoutActivity;
import com.unnamed.b.atv.holder.IconTreeItemHolder;
import com.unnamed.b.atv.holder.SelectableHeaderHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

public class MainActivity extends AppCompatActivity implements TreeNode.TreeNodeClickListener {

    private AndroidTreeView tView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup containerView = (ViewGroup) findViewById(R.id.container);
        TreeNode root = TreeNode.root();
        //ui
        TreeNode ui = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "UI")).setViewHolder(
                new SelectableHeaderHolder(this));
        //ui -> refresh
        TreeNode refresh = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Refresh")).setViewHolder(new SelectableHeaderHolder(this));
        //ui -> refresh -> smartrefreshlayout
        TreeNode smartRefreshLayout = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_place, "SmartRefreshLayout")).setViewHolder(new SelectableHeaderHolder(this));
        // add smartRefreshLayout children
        refresh.addChild(smartRefreshLayout);
        //add ui children
        ui.addChildren(refresh);

        TreeNode architecture = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "ARCHITECTURE")).setViewHolder(
                new SelectableHeaderHolder(this));

        //add root children
        root.addChildren(ui, architecture);

        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setUse2dScroll(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultNodeClickListener(this);
        containerView.addView(tView.getView());

//        tView.expandAll();
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        String name = ((IconTreeItemHolder.IconTreeItem) value).text;
        if (!TextUtils.isEmpty(name)) {
            if (name.equals("SmartRefreshLayout"))
                startActivity(new Intent(this, SmartRefreshLayoutActivity.class));
        }
    }
}
