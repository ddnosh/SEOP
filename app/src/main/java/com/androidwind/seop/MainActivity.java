package com.androidwind.seop;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.unnamed.b.atv.holder.IconTreeItemHolder;
import com.unnamed.b.atv.holder.SelectableHeaderHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

public class MainActivity extends AppCompatActivity implements TreeNode.TreeNodeClickListener{

    private AndroidTreeView tView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewGroup containerView = (ViewGroup) findViewById(R.id.container);
        TreeNode root = TreeNode.root();
        TreeNode ui = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "UI")).setViewHolder(
                new SelectableHeaderHolder(this));
        TreeNode dialog = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Dialog")).setViewHolder(new SelectableHeaderHolder(this));
        TreeNode sheet = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "Sheet")).setViewHolder(new SelectableHeaderHolder(this));

        ui.addChildren(dialog, sheet);

        TreeNode architecture = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ic_folder, "ARCHITECTURE")).setViewHolder(
                new SelectableHeaderHolder(this));
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
        Toast toast = Toast.makeText(this, ((IconTreeItemHolder.IconTreeItem)value).text, Toast.LENGTH_SHORT);
        toast.show();
    }
}
