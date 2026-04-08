package com.example.myapplication;

import java.util.Date;

public class Ticket {
    private String ticketId;
    private String userId;
    private String movieId;
    private String movieTitle;
    private String showtime;
    private String seatNumber;
    private double price;
    private Date bookingDate;

    public Ticket() {}

    public Ticket(String ticketId, String userId, String movieId, String movieTitle, String showtime, String seatNumber, double price, Date bookingDate) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.showtime = showtime;
        this.seatNumber = seatNumber;
        this.price = price;
        this.bookingDate = bookingDate;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getShowtime() { return showtime; }
    public void setShowtime(String showtime) { this.showtime = showtime; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Date getBookingDate() { return bookingDate; }
    public void setBookingDate(Date bookingDate) { this.bookingDate = bookingDate; }
}