package com.velocitypowered.proxy.protocol.packet;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.player.TabListEntry;
import com.velocitypowered.api.util.GameProfile;
import com.velocitypowered.proxy.connection.MinecraftSessionHandler;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.ProtocolUtils;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.kyori.text.Component;
import net.kyori.text.serializer.ComponentSerializers;
import org.checkerframework.checker.nullness.qual.Nullable;

public class PlayerListItem implements MinecraftPacket {

  public static final int ADD_PLAYER = 0;
  public static final int UPDATE_GAMEMODE = 1;
  public static final int UPDATE_LATENCY = 2;
  public static final int UPDATE_DISPLAY_NAME = 3;
  public static final int REMOVE_PLAYER = 4;
  private int action;
  private final List<Item> items = new ArrayList<>();

  public PlayerListItem(int action, List<Item> items) {
    this.action = action;
    this.items.addAll(items);
  }

  public PlayerListItem() {
  }

  public int getAction() {
    return action;
  }

  public List<Item> getItems() {
    return items;
  }

  @Override
  public void decode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    action = ProtocolUtils.readVarInt(buf);
    int length = ProtocolUtils.readVarInt(buf);

    for (int i = 0; i < length; i++) {
      Item item = new Item(ProtocolUtils.readUuid(buf));
      items.add(item);
      switch (action) {
        case ADD_PLAYER:
          item.setName(ProtocolUtils.readString(buf));
          item.setProperties(ProtocolUtils.readProperties(buf));
          item.setGameMode(ProtocolUtils.readVarInt(buf));
          item.setLatency(ProtocolUtils.readVarInt(buf));
          item.setDisplayName(readOptionalComponent(buf));
          break;
        case UPDATE_GAMEMODE:
          item.setGameMode(ProtocolUtils.readVarInt(buf));
          break;
        case UPDATE_LATENCY:
          item.setLatency(ProtocolUtils.readVarInt(buf));
          break;
        case UPDATE_DISPLAY_NAME:
          item.setDisplayName(readOptionalComponent(buf));
          break;
        case REMOVE_PLAYER:
          //Do nothing, all that is needed is the uuid
          break;
        default:
          throw new UnsupportedOperationException("Unknown action " + action);
      }
    }
  }

  @Nullable
  private static Component readOptionalComponent(ByteBuf buf) {
    if (buf.readBoolean()) {
      return ComponentSerializers.JSON.deserialize(ProtocolUtils.readString(buf));
    }
    return null;
  }

  @Override
  public void encode(ByteBuf buf, ProtocolUtils.Direction direction, ProtocolVersion version) {
    ProtocolUtils.writeVarInt(buf, action);
    ProtocolUtils.writeVarInt(buf, items.size());
    for (Item item : items) {
      ProtocolUtils.writeUuid(buf, item.getUuid());
      switch (action) {
        case ADD_PLAYER:
          ProtocolUtils.writeString(buf, item.getName());
          ProtocolUtils.writeProperties(buf, item.getProperties());
          ProtocolUtils.writeVarInt(buf, item.getGameMode());
          ProtocolUtils.writeVarInt(buf, item.getLatency());

          writeDisplayName(buf, item.getDisplayName());
          break;
        case UPDATE_GAMEMODE:
          ProtocolUtils.writeVarInt(buf, item.getGameMode());
          break;
        case UPDATE_LATENCY:
          ProtocolUtils.writeVarInt(buf, item.getLatency());
          break;
        case UPDATE_DISPLAY_NAME:
          writeDisplayName(buf, item.getDisplayName());
          break;
        case REMOVE_PLAYER:
          //Do nothing, all that is needed is the uuid
          break;
        default:
          throw new UnsupportedOperationException("Unknown action " + action);
      }
    }
  }

  @Override
  public boolean handle(MinecraftSessionHandler handler) {
    return handler.handle(this);
  }

  private void writeDisplayName(ByteBuf buf, @Nullable Component displayName) {
    buf.writeBoolean(displayName != null);
    if (displayName != null) {
      ProtocolUtils.writeString(buf, ComponentSerializers.JSON.serialize(displayName));
    }
  }

  public static class Item {

    private final UUID uuid;
    private String name = "";
    private List<GameProfile.Property> properties = ImmutableList.of();
    private int gameMode;
    private int latency;
    private @Nullable Component displayName;

    public Item(UUID uuid) {
      this.uuid = uuid;
    }

    public static Item from(TabListEntry entry) {
      return new Item(entry.getProfile().getId())
          .setName(entry.getProfile().getName())
          .setProperties(entry.getProfile().getProperties())
          .setLatency(entry.getLatency())
          .setGameMode(entry.getGameMode())
          .setDisplayName(entry.getDisplayName().orElse(null));
    }

    public UUID getUuid() {
      return uuid;
    }

    public String getName() {
      return name;
    }

    public Item setName(String name) {
      this.name = name;
      return this;
    }

    public List<GameProfile.Property> getProperties() {
      return properties;
    }

    public Item setProperties(List<GameProfile.Property> properties) {
      this.properties = properties;
      return this;
    }

    public int getGameMode() {
      return gameMode;
    }

    public Item setGameMode(int gamemode) {
      this.gameMode = gamemode;
      return this;
    }

    public int getLatency() {
      return latency;
    }

    public Item setLatency(int latency) {
      this.latency = latency;
      return this;
    }

    public @Nullable Component getDisplayName() {
      return displayName;
    }

    public Item setDisplayName(@Nullable Component displayName) {
      this.displayName = displayName;
      return this;
    }
  }
}
