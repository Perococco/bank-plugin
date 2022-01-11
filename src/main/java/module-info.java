import jplugman.api.Plugin;
import perobobbot.plugin.bank.JPlugin;

module perobobbot.plugin.bank {
    requires static lombok;
    requires java.desktop;

    requires org.apache.logging.log4j;
    requires jplugman.api;
    requires com.google.common;
    requires reactor.core;

    requires perobobbot.plugin;
    requires perobobbot.extension;
    requires perobobbot.access;
    requires perobobbot.command;
    requires perobobbot.lang;
    requires perobobbot.http;
    requires perobobbot.chat.core;
    requires perobobbot.messaging;
    requires perobobbot.data.service;

    requires perobobbot.twitch.client.api;
    requires perobobbot.twitch.event.sub.api;
    requires perobobbot.eventsub;


    provides Plugin with JPlugin;
}
