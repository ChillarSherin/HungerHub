package com.hungerhub.NewPreOrder;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chillarcards.cookery.R;

import java.util.List;

import com.hungerhub.networkmodels.RefreshOrderData.Item;


public class CartOrderDetailsAdapter extends RecyclerView.Adapter<CartOrderDetailsAdapter.MyviewHolder> {
    List<Item> myDataset;
    Context context;

    public CartOrderDetailsAdapter(List<Item> myDataset, Context context) {
        this.myDataset = myDataset;
        this.context = context;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_my_order_details, viewGroup, false);
        return new CartOrderDetailsAdapter.MyviewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, final int i) {
        String orderstatus = "";
        if (myDataset.get(i).getItemPurchaseStatus().equalsIgnoreCase("0")) {
            orderstatus="Order Placed";
        }
        else if (myDataset.get(i).getItemPurchaseStatus().equalsIgnoreCase("1")) {
            orderstatus="Delivered";
        }
        else if (myDataset.get(i).getItemPurchaseStatus().equalsIgnoreCase("2")){
            orderstatus="Refund";
        }
        else if (myDataset.get(i).getItemPurchaseStatus().equalsIgnoreCase("4")){
            orderstatus="Ready to deliver";
        }

        myviewHolder.OrderStatusTV.setText(orderstatus);
        myviewHolder.DetailItemPriceTV.setText("Price : "+context.getResources().getString(R.string.indian_rupee_symbol)+myDataset.get(i).getItemPrice());
        myviewHolder.DetailItemOutletNameTV.setText(myDataset.get(i).getOutletName());
        myviewHolder.DetailItemNameTV.setText(myDataset.get(i).getItemName());
        myviewHolder.DetailItemQtyTV.setText("Qty : "+myDataset.get(i).getItemQuantity());


    }

    @Override
    public int getItemCount() {
        return myDataset.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView DetailItemNameTV, DetailItemQtyTV, DetailItemOutletNameTV, DetailItemPriceTV, OrderStatusTV;
        ImageView OrderStatusIV;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            DetailItemQtyTV = itemView.findViewById(R.id.DetailItemQtyTV);
            DetailItemNameTV = itemView.findViewById(R.id.DetailItemNameTV);
            DetailItemOutletNameTV = itemView.findViewById(R.id.DetailItemOutletNameTV);
            DetailItemPriceTV = itemView.findViewById(R.id.DetailItemPriceTV);
            OrderStatusTV = itemView.findViewById(R.id.OrderStatusTV);
            OrderStatusIV = itemView.findViewById(R.id.OrderStatusIV);
        }
    }
}
