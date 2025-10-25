package com.util.Sounds;

import javazoom.jl.player.Player;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.IOException;

public class SoundPlayer {

    public SoundPlayer() {
        SoundEffects.preloadSounds();

    }

    public static final int INTRO = -1, LEVEL_SELECT = 0;

    public static void loadSongs(int area) {
        try {
            switch (area) {
                case INTRO:
                    Songs.INTRO.preloadSound();
                    break;
                case LEVEL_SELECT:
                    Songs.LEVEL_SELECT.preloadSound();
                    break;
                case 1:
                    Songs.LELVEL_1_MAIN.preloadSound();
                    break;
                case 2:
                    Songs.LELVEL_2_MAIN.preloadSound();
                    break;
                case 3:
                    Songs.LELVEL_3_MAIN.preloadSound();
                    break;
                case 4:
                    Songs.LELVEL_4_MAIN.preloadSound();
                    break;
                case 5:
                    Songs.LELVEL_5_MAIN.preloadSound();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error loading song: " + area);
            // ExceptionHandler.handle(e);
        }
    }

    public static void unloadSongs(int area) {
        switch (area) {
            case -1:
                Songs.INTRO.unloadSound();
                break;
            case 1:
                Songs.LELVEL_1_MAIN.unloadSound();
                break;
            case 2:
                Songs.LELVEL_2_MAIN.unloadSound();
                break;
            case 3:
                Songs.LELVEL_3_MAIN.unloadSound();
                break;
            case 4:
                Songs.LELVEL_4_MAIN.unloadSound();
                break;
            case 5:
                Songs.LELVEL_5_MAIN.unloadSound();
                break;
        }
    }

    public static void unloadAllSongs() {

        Songs.INTRO.unloadSound();

        Songs.LELVEL_1_MAIN.unloadSound();

        Songs.LELVEL_2_MAIN.unloadSound();

        Songs.LELVEL_3_MAIN.unloadSound();

        Songs.LELVEL_4_MAIN.unloadSound();

        Songs.LELVEL_5_MAIN.unloadSound();

    }

    public static void stopAllSongs() {

        Songs.INTRO.stopSound();

        Songs.LELVEL_1_MAIN.stopSound();

        Songs.LELVEL_2_MAIN.stopSound();

        Songs.LELVEL_3_MAIN.stopSound();

        Songs.LELVEL_4_MAIN.stopSound();

        Songs.LELVEL_5_MAIN.stopSound();

    }

    public enum SoundEffects {
        SE_CLICK("/sounds/click.wav"),
        SE_SELECT("/sounds/select.wav");

        public static void preloadSounds() {
            for (SoundEffects sound : SoundEffects.values()) {
                try {
                    sound.loadSound();
                } catch (Exception e) {
                    System.out.println("Error loading sound: " + sound);
                    // ExceptionHandler.handle(e);
                }
            }
        }

        // private AudioInputStream audioStream;
        private String filePath;
        private byte[] soundData;
        private AudioFormat audioFormat;
        private Clip fastClip;
        private float volume = 6.0f; // Default volume (0.0f = full volume)

        private SoundEffects(String soundFile) {
            this.filePath = soundFile;
        }

        // Preload the audio file into memory
        public void loadSound() throws Exception {
            try (BufferedInputStream bufferedStream = new BufferedInputStream(
                    SoundPlayer.class.getResourceAsStream(filePath));) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedStream);

                // AudioFormat.Encoding encoding = audioStream.getFormat().getEncoding();
                // if (encoding != AudioFormat.Encoding.PCM_SIGNED) {
                audioStream = convertToCompatibleFormat(audioStream);
                // }

                audioFormat = audioStream.getFormat();
                soundData = audioStream.readAllBytes(); // Load the sound data into memory
                fastClip = getCompatibleClip();

                // Open the Clip with a ByteArrayInputStream of the sound data
                AudioInputStream stream = new AudioInputStream(
                        new ByteArrayInputStream(soundData),
                        audioFormat,
                        soundData.length / audioFormat.getFrameSize());
                fastClip.open(stream);
            }
        }

        // Play the sound, allowing for overlapping playback
        // Play the sound with overlapping capability
        public void playSound() {
            try {
                if (soundData == null || audioFormat == null) {
                    System.err.println("Sound not loaded. Call loadSound() first.");
                    return;
                }

                new Thread(() -> {
                    try {
                        // Create a new Clip instance for each playback
                        Clip clip = getCompatibleClip();

                        // Open the Clip with a ByteArrayInputStream of the sound data
                        AudioInputStream stream = new AudioInputStream(
                                new ByteArrayInputStream(soundData),
                                audioFormat,
                                soundData.length / audioFormat.getFrameSize());
                        clip.open(stream);
                        setVolume(clip);
                        clip.start();

                        // Cleanup after playback
                        clip.addLineListener(event -> {
                            if (event.getType() == LineEvent.Type.STOP) {
                                clip.close();
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        // ExceptionHandler.handle(e);

                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        public void stopSound() {
            try {
                fastClip.stop();
            } catch (Exception e) {

            }
        }

        public void playSoundFast() {
            // if (fastClip == null) {
            // System.err.println("Sound not loaded or fastClip not initialized.");
            // return;
            // }
            try {
                if (fastClip.isRunning()) {
                    fastClip.stop(); // Stop current playback if running
                }
                setVolume(fastClip);
                fastClip.setFramePosition(0); // Rewind to the beginning
                fastClip.start(); // Play the sound
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        public void playAndFinishSoundFast() {
            // if (fastClip == null) {
            // System.err.println("Sound not loaded or fastClip not initialized.");
            // return;
            // }
            try {
                if (fastClip.isRunning()) {
                    return;
                } else {
                    fastClip.stop(); // Stop current playback if running
                    fastClip.setFramePosition(0); // Rewind to the beginning
                    setVolume(fastClip);
                    fastClip.start(); // Play the sound

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        private void setVolume(Clip clip) {
            try {
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    float clampedVolume = Math.max(gainControl.getMinimum(),
                            Math.min(volume, gainControl.getMaximum()));
                    gainControl.setValue(clampedVolume);

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

        // public AudioInputStream convertToPcmSigned(AudioInputStream sourceStream) {
        // AudioFormat sourceFormat = sourceStream.getFormat();
        // AudioFormat targetFormat = new AudioFormat(
        // AudioFormat.Encoding.PCM_SIGNED, // Target encoding
        // sourceFormat.getSampleRate(), // Same sample rate
        // 16, // Use 16-bit samples
        // sourceFormat.getChannels(), // Same channel count
        // sourceFormat.getChannels() * 2, // Frame size: 2 bytes per channel
        // sourceFormat.getSampleRate(), // Same frame rate
        // false // Little-endian
        // );

        // return AudioSystem.getAudioInputStream(targetFormat, sourceStream);
        // }

        // Finds a working audio mixer that supports Clips
        private static Clip getCompatibleClip() {
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();
            for (Mixer.Info mixerInfo : mixers) {
                try {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    if (mixer.isLineSupported(new Line.Info(Clip.class))) {
                        System.out.println("Using Mixer: " + mixerInfo.getName());
                        return (Clip) mixer.getLine(new Line.Info(Clip.class));
                    }
                } catch (Exception e) {
                    System.out.println("Mixer " + mixerInfo.getName() + " does not support Clip.");
                }
            }
            return null; // No compatible mixer found
        }

        // Converts any WAV file to a guaranteed supported format
        private static AudioInputStream convertToCompatibleFormat(AudioInputStream audioStream)
                throws UnsupportedAudioFileException, IOException {
            AudioFormat baseFormat = audioStream.getFormat();
            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    44100, // Standard sample rate
                    16, // Bit depth
                    baseFormat.getChannels(), // Preserve original channels
                    baseFormat.getChannels() * 2, // Frame size (16-bit per channel)
                    44100, // Frame rate
                    false // Little-endian
            );

            return AudioSystem.getAudioInputStream(targetFormat, audioStream);
        }

        public void setVolume(float volume) {
            this.volume = volume;
        }
    }

    public enum Songs {

        INTRO("/mus/intro.mp3"),
        LEVEL_SELECT("/mus/levelSelect.mp3"),

        LELVEL_1_MAIN("/mus/town.mp3"),

        LELVEL_2_MAIN("/mus/mountains.mp3"),
        LELVEL_3_MAIN("/mus/jungle.mp3"),

        LELVEL_4_MAIN("/mus/island.mp3"),
        LELVEL_5_MAIN("/mus/island.mp3");

        private String filePath;
        private byte[] soundData;

        private Player myPlayer;
        private boolean loop = false;

        private Songs(String filePath) {

            this.filePath = filePath;

            // preloadSound();

        }

        public boolean isLooping() {
            return loop;
        }

        private void preloadSound() throws Exception {
            try (InputStream fis = SoundPlayer.class.getResourceAsStream(filePath);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                soundData = baos.toByteArray();
            }
        }

        private void unloadSound() {
            stopSound();
            soundData = null;
            myPlayer = null;
        }

        public void playSound() {
            if (soundData == null) {
                // System.err.println("Sound not loaded. Call preloadSound() first.");
                return;
            }

            new Thread(() -> {
                try (ByteArrayInputStream bais = new ByteArrayInputStream(soundData)) {
                    Player player = new Player(bais);
                    player.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        public void stopSound() {
            loop = false;
            if (myPlayer != null)
                myPlayer.close();
        }

        public void loopSound() {
            loop = true;
            new Thread(() -> {
                while (loop) {
                    try (ByteArrayInputStream bais = new ByteArrayInputStream(soundData)) {
                        myPlayer = new Player(bais);
                        myPlayer.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                        loop = false;
                    }
                }
            }).start();

        }

        public void unloopSound() {
            loop = false;

        }

        public void setVolume() {

        }
    }

    // public static void main(String[] args) {
    // try {
    // SoundPlayer soundManager = new SoundPlayer();

    // // Preload the MP3 file
    // Songs.SE_CLICK.playSound();

    // // Play the sound whenever needed
    // // soundManager.playSound();
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
}