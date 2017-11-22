package hu.ait.demos.shoppinglist.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import hu.ait.demos.shoppinglist.CreateItemActivity;
import hu.ait.demos.shoppinglist.MainActivity;
import hu.ait.demos.shoppinglist.R;
import hu.ait.demos.shoppinglist.data.Item;
import io.realm.Realm;

import static hu.ait.demos.shoppinglist.R.string.dollar;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivIcon;
        public TextView tvItem;
        public TextView tvPrice;
        public Button btnDelete;
        public Button btnEdit;
        public CheckBox checkBox;
        public int total;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvItem = (TextView) itemView.findViewById(R.id.tvItem);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            btnDelete = (Button) itemView.findViewById(R.id.btnDelete);
            btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
            checkBox = (CheckBox) itemView.findViewById(R.id.cbPurchased);

        }
    }

    private List<Item> itemsList;
    private Context context;
    private int lastPosition = -1;

    public ItemsAdapter(List<Item> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_item, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

        viewHolder.tvItem.setText(itemsList.get(position).getItemName());
        viewHolder.tvPrice.setText(itemsList.get(position).getPrice());
        viewHolder.checkBox.setChecked(itemsList.get(position).getFlag());
        viewHolder.ivIcon.setImageResource(
                itemsList.get(position).getItemType().getIconId());
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemsList.get(position).getFlag()){
                    viewHolder.checkBox.setChecked(false);
                    ((MainActivity) context).getRealm().beginTransaction();
                    itemsList.get(position).setFlag(false);
                    ((MainActivity) context).getRealm().commitTransaction();
                }
                else {
                    viewHolder.checkBox.setChecked(true);
                    ((MainActivity) context).getRealm().beginTransaction();
                    itemsList.get(position).setFlag(true);
                    ((MainActivity) context).getRealm().commitTransaction();
                }
            }
        });
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(viewHolder.getAdapterPosition());
            }
        });
        viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) context).showEditItemActivity(
                        itemsList.get(viewHolder.getAdapterPosition()).getItemID(),
                        viewHolder.getAdapterPosition());
            }
        });

        setAnimation(viewHolder.itemView, position);
    }


    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public int calculate(){
        int length = itemsList.size();
        int total = 0;
        for (int i = 0; i < length; i++) {
            String add = itemsList.get(i).getPrice();
            int combine = Integer.parseInt(add);
            total = total + combine;
        }
        return total;
    }

    public void addItem(Item item) {
        itemsList.add(item);
        notifyDataSetChanged();
    }

    public void updateItem(int index, Item item) {
        itemsList.set(index, item);
        notifyItemChanged(index);

    }

    public void removeItem(int index) {
        ((MainActivity)context).deleteItem(itemsList.get(index));
        itemsList.remove(index);
        notifyItemRemoved(index);
    }

    public void removeAllItem() {
        ((MainActivity)context).deleteAllItem();
        notifyItemRangeRemoved(0, itemsList.size());
        itemsList.clear();
    }


    public void swapItems(int oldPosition, int newPosition) {
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(itemsList, i, i + 1);
            }
        } else {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(itemsList, i, i - 1);
            }
        }
        notifyItemMoved(oldPosition, newPosition);
    }

    public Item getItem(int i) {
        return itemsList.get(i);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
