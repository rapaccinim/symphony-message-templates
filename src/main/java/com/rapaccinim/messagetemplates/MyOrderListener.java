package com.rapaccinim.messagetemplates;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import java.util.Map;

class MyOrderListener implements RealTimeEventListener {

    private final SymphonyBdk bdk;

    public MyOrderListener(SymphonyBdk bdk) {
        this.bdk = bdk;
    }

    // override for the message sending
    @Override
    public void onMessageSent(V4Initiator initiator, V4MessageSent event) throws EventException {
        // first of all let's take the message
        V4Message message = event.getMessage();
        // get rid of all the HTML and other tags
        String messageText = message.getMessage().replaceAll("<[^>]*>", "");

        // if the user has prompted an /order command then the bot will reply with a form
        if (messageText.startsWith("/order")) {
            // here we use the appropriate form template
            String form = bdk.messages().templates()
                    .newTemplateFromClasspath("/templates/order-form.ftl")
                    .process(Map.of()); // here we use an empty Map for now

            // and send back the form in the stream
            bdk.messages().send(message.getStream(), form);
        }
    }

    // override for the Elements Form submission (so we can handle the form submission)
    @Override
    public void onSymphonyElementsAction(
            V4Initiator initiator,
            V4SymphonyElementsAction event) throws EventException {

        // the handler will activate only if the form submission is regarding the "order" form
        if(event.getFormId().equals("order")){
            System.out.println("I am here!");
            // we assume that all the values coming from the form are String
            @SuppressWarnings("unchecked")
            Map<String, String> values = (Map<String, String>) event.getFormValues();

            // then we get the actual individual values
            String ticker = values.get("ticker").replace("$", "");
            int quantity = Integer.parseInt(values.get("quantity"));
            int price = Integer.parseInt(values.get("price"));

            // here we create a data object (in this case a simple Map)
            Map<String, Object> data = Map.of(
                    "ticker", ticker,
                    "quantity", quantity,
                    "price", price
            );

            // and the bot will send back a template message with cash tag
            String replyMessageTemplate = bdk.messages().templates()
                    .newTemplateFromClasspath("/templates/order-confirm.ftl")
                    .process(data); // here we pass the data object

            bdk.messages().send(event.getStream(), String.format(replyMessageTemplate, quantity, ticker, price));
        }

    }
}
