package me.giverplay.agaragun;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class Utils
{
  public static void sendAction(Player player, String msg)
  {
    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
  }

  public static String createTimerLabel(int segundos)
  {
    String timeLabel = "";
    int min, sec;

    min = segundos / 60;
    sec = segundos % 60;

    timeLabel += min + ":";
    timeLabel += (sec < 10 ? "0" + sec : sec);
    return timeLabel;
  }

  public static void sendTitle(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {
    try {
      Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
              .invoke(null, "{\"text\": \"" + title + "\"}");
      Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
              getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
              int.class, int.class, int.class);
      Object packet = titleConstructor.newInstance(
              getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
              fadeInTime, showTime, fadeOutTime);

      Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
              .invoke(null, "{\"text\": \"" + subtitle + "\"}");
      Constructor<?> timingTitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
              getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
              int.class, int.class, int.class);
      Object timingPacket = timingTitleConstructor.newInstance(
              getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null), chatsTitle,
              fadeInTime, showTime, fadeOutTime);

      sendPacket(player, packet);
      sendPacket(player, timingPacket);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void sendPacket(Player player, Object packet) {
    try {
      Object handle = player.getClass().getMethod("getHandle").invoke(player);
      Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
      playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Class<?> getNMSClass(String name) {
    try {
      return Class.forName("net.minecraft.server."
              + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
