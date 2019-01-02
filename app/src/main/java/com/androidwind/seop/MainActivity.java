package com.androidwind.seop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.androidwind.seop.bean.MenuBean;
import com.androidwind.seop.ui.BaseActivity;
import com.androidwind.seop.ui.refresh.SmartRefreshLayoutActivity;
import com.androidwind.seop.ui.tab.SmartTabLayoutFragment;
import com.androidwind.seop.util.MenuUtil;
import com.unnamed.b.atv.holder.IconTreeItemHolder;
import com.unnamed.b.atv.holder.SelectableHeaderHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.util.List;

public class MainActivity extends BaseActivity implements TreeNode.TreeNodeClickListener {

    private TreeNode root;
    private AndroidTreeView tView;
    private List<MenuBean> menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup containerView = findViewById(R.id.container);

        root = TreeNode.root();
        menuList = MenuUtil.getPositions(this, "menu.txt");
        initMenus();

        //init AndroidTreeView
        tView = new AndroidTreeView(this, root);
        tView.setDefaultAnimation(true);
        tView.setUse2dScroll(true);
        tView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom);
        tView.setDefaultNodeClickListener(this);
        containerView.addView(tView.getView());
    }

    private void initMenus() {
        for (MenuBean bean : menuList) {
            if (bean.treeNode == null) {
                getTreeNode(bean);
            }
        }
    }

    private TreeNode getTreeNode(MenuBean menuBean) {
        //root
        if (menuBean.upperId == 0) {
            if (menuBean.treeNode == null) {
                return menuBean.treeNode = addTreeNode(root, menuBean.name);
            }
        } else {
            for (MenuBean bean : menuList) {
                if (bean.currentId == menuBean.upperId) {
                    if (bean.treeNode == null) {
                        return menuBean.treeNode = getTreeNode(menuBean);
                    } else {
                        return menuBean.treeNode = addTreeNode(bean.treeNode, menuBean.name);
                    }
                }
            }
        }
        return null;
    }


    private TreeNode addTreeNode(TreeNode treeNode, String nodeName) {
        if (treeNode == null) return null;
        TreeNode node = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, nodeName)).setViewHolder(
                new SelectableHeaderHolder(this));
        treeNode.addChild(node);
        return node;
    }

    @Override
    public void onClick(TreeNode node, Object value) {
        String name = ((IconTreeItemHolder.IconTreeItem) value).text;
        if (!TextUtils.isEmpty(name)) {
            if (name.equals("SmartRefreshLayout")) {
                startActivity(new Intent(this, SmartRefreshLayoutActivity.class));
            } else if (name.equals("SmartTabLayout")) {
                readyGo(SmartTabLayoutFragment.class);
            }
        }
    }
}
