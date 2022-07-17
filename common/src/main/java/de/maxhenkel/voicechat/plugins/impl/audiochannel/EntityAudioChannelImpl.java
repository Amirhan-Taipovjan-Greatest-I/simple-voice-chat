package de.maxhenkel.voicechat.plugins.impl.audiochannel;

import de.maxhenkel.voicechat.api.Entity;
import de.maxhenkel.voicechat.api.audiochannel.EntityAudioChannel;
import de.maxhenkel.voicechat.api.events.SoundPacketEvent;
import de.maxhenkel.voicechat.api.packets.MicrophonePacket;
import de.maxhenkel.voicechat.plugins.impl.ServerPlayerImpl;
import de.maxhenkel.voicechat.voice.common.PlayerSoundPacket;
import de.maxhenkel.voicechat.voice.common.Utils;
import de.maxhenkel.voicechat.voice.server.Server;
import de.maxhenkel.voicechat.voice.server.ServerWorldUtils;
import net.minecraft.world.server.ServerWorld;

import java.util.UUID;

public class EntityAudioChannelImpl extends AudioChannelImpl implements EntityAudioChannel {

    protected Entity entity;
    protected boolean whispering;
    protected float distance;

    public EntityAudioChannelImpl(UUID channelId, Server server, Entity entity) {
        super(channelId, server);
        this.entity = entity;
        this.whispering = false;
        this.distance = Utils.getDefaultDistance();
    }

    @Override
    public void setWhispering(boolean whispering) {
        this.whispering = whispering;
    }

    @Override
    public boolean isWhispering() {
        return whispering;
    }

    @Override
    public void updateEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public float getDistance() {
        return distance;
    }

    @Override
    public void setDistance(float distance) {
        this.distance = distance;
    }

    @Override
    public void send(byte[] opusData) {
        broadcast(new PlayerSoundPacket(channelId, opusData, sequenceNumber.getAndIncrement(), whispering, distance));
    }

    @Override
    public void send(MicrophonePacket microphonePacket) {
        broadcast(new PlayerSoundPacket(channelId, microphonePacket.getOpusEncodedData(), sequenceNumber.getAndIncrement(), whispering, distance));
    }

    @Override
    public void flush() {
        broadcast(new PlayerSoundPacket(channelId, new byte[0], sequenceNumber.getAndIncrement(), whispering, distance));
    }

    private void broadcast(PlayerSoundPacket packet) {
        if (entity.getEntity() instanceof net.minecraft.entity.Entity) {
            net.minecraft.entity.Entity entity = ((net.minecraft.entity.Entity) this.entity.getEntity()).getEntity();
            server.broadcast(ServerWorldUtils.getPlayersInRange((ServerWorld) entity.level, entity.getEyePosition(1F), server.getBroadcastRange(distance), filter == null ? player -> true : player -> filter.test(new ServerPlayerImpl(player))), packet, null, null, null, SoundPacketEvent.SOURCE_PLUGIN);
        }
    }

}
