package com.rapaccinim.messagetemplates;

// this is the data object (a standard POJO) we use for the reply to the price form submission
public class PriceQuote {

   private String ticker;
   private int price;

   public PriceQuote(String ticker, int price){
       this.ticker = ticker;
       this.price = price;
   }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
