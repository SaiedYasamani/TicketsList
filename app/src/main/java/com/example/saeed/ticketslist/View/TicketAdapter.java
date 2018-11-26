package com.example.saeed.ticketslist.View;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.saeed.ticketslist.Model.Ticket;
import com.example.saeed.ticketslist.R;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.ViewHolder> {

    private Context context;
    private List<Ticket> contactList;
    private TicketAdapterListener ticketAdapterListener;

    public TicketAdapter(Context context, List<Ticket> contactList, TicketAdapterListener ticketAdapterListener) {
        this.context = context;
        this.contactList = contactList;
        this.ticketAdapterListener = ticketAdapterListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_row,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Ticket ticket = contactList.get(position);

        Glide.with(context)
                .load(ticket.getAirline().getLogo())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.logo);

        holder.airlineName.setText(ticket.getAirline().getName());

        holder.departure.setText(ticket.getDeparture() + " Dep");
        holder.arrival.setText(ticket.getArrival() + " Dest");

        holder.duration.setText(ticket.getFlightNumber());
        holder.duration.append(", " + ticket.getDuration());
        holder.stops.setText(ticket.getNumberOfStops() + " Stops");

        if (!TextUtils.isEmpty(ticket.getInstructions())) {
            holder.duration.append(", " + ticket.getInstructions());
        }

        if (ticket.getPrice() != null) {
            holder.price.setText("₹" + String.format("%.0f", ticket.getPrice().getPrice()));
            holder.seats.setText(ticket.getPrice().getSeats() + " Seats");
            holder.loader.setVisibility(View.INVISIBLE);
        } else {
            holder.loader.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.airline_name)
        TextView airlineName;


        @BindView(R.id.logo)
        ImageView logo;

        @BindView(R.id.number_of_stops)
        TextView stops;

        @BindView(R.id.number_of_seats)
        TextView seats;

        @BindView(R.id.departure)
        TextView departure;

        @BindView(R.id.arrival)
        TextView arrival;

        @BindView(R.id.duration)
        TextView duration;

        @BindView(R.id.price)
        TextView price;

        @BindView(R.id.loader)
        SpinKitView loader;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ticketAdapterListener.onTicketSelected(contactList.get(getAdapterPosition()));
                }
            });
        }
    }

    public interface TicketAdapterListener{
        void onTicketSelected(Ticket contact);
    }
}
