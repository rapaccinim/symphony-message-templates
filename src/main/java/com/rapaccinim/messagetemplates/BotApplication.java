package com.rapaccinim.messagetemplates;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.command.SlashCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromFile;

/**
 * Simple Bot Application.
 */
public class BotApplication {

  private static final Logger LOGGER = LoggerFactory.getLogger(BotApplication.class);

  public static void main(String[] args) throws Exception {

    // Initialize BDK entry point
    // final SymphonyBdk bdk = new SymphonyBdk(loadFromClasspath("/config.yaml"));
    final SymphonyBdk bdk = new SymphonyBdk(loadFromFile("config.yaml"));

    // here you subscribe the event listener to the datafeed
    bdk.datafeed().subscribe(new MyOrderListener(bdk));

    // let's add an activity for the slash command /price
    bdk.activities().register(SlashCommand.slash(
            "/price",
            false,
            context -> {
              String form = bdk.messages().templates()
                      .newTemplateFromClasspath("/templates/price-form.ftl")
                      .process(Map.of());

              bdk.messages().send(context.getStreamId(), form);
            }
    ));

    // remember to register the custom activity to the Activity Register
    bdk.activities().register(new PriceFormReplyActivity(bdk.messages()));

    // always remember to start the datafeed service at the end
    bdk.datafeed().start();
  }

}
