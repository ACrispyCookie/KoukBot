package me.acrispycookie.managers.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {

    AudioPlayer player;
    ByteBuffer byteBuffer;
    MutableAudioFrame frame;

    public AudioPlayerSendHandler(AudioPlayer player){
        this.player = player;
        this.byteBuffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        frame.setBuffer(byteBuffer);
    }

    @Override
    public boolean canProvide() {
        return player.provide(this.frame);
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return (ByteBuffer) (this.byteBuffer.flip());
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}
