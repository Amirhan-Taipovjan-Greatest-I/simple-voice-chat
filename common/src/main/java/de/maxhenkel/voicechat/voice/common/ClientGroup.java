package de.maxhenkel.voicechat.voice.common;

import net.minecraft.network.PacketBuffer;

import java.util.UUID;

public class ClientGroup {

    private final UUID id;
    private final String name;
    private final boolean hasPassword;
    private final boolean persistent;

    public ClientGroup(UUID id, String name, boolean hasPassword, boolean persistent) {
        this.id = id;
        this.name = name;
        this.hasPassword = hasPassword;
        this.persistent = persistent;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean hasPassword() {
        return hasPassword;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public static ClientGroup fromBytes(PacketBuffer buf) {
        return new ClientGroup(buf.readUUID(), buf.readUtf(512), buf.readBoolean(), buf.readBoolean());
    }

    public void toBytes(PacketBuffer buf) {
        buf.writeUUID(id);
        buf.writeUtf(name, 512);
        buf.writeBoolean(hasPassword);
        buf.writeBoolean(persistent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientGroup group = (ClientGroup) o;

        return id != null ? id.equals(group.id) : group.id == null;
    }
}
