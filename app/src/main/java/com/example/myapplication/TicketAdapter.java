package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> ticketList;

    public TicketAdapter(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = ticketList.get(position);
        holder.tvTitle.setText(ticket.getMovieTitle());
        holder.tvShowtime.setText("Giờ chiếu: " + ticket.getShowtime());
        holder.tvSeat.setText("Ghế: " + ticket.getSeatNumber());

        if (ticket.getBookingDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            holder.tvDate.setText("Ngày đặt: " + sdf.format(ticket.getBookingDate()));
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvShowtime, tvSeat, tvDate;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTicketMovieTitle);
            tvShowtime = itemView.findViewById(R.id.tvTicketShowtime);
            tvSeat = itemView.findViewById(R.id.tvTicketSeat);
            tvDate = itemView.findViewById(R.id.tvTicketDate);
        }
    }
}
