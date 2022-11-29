package com.example.whisperVoiceRecognition;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.view.View;
import android.widget.ToggleButton;

import com.example.WhisperVoiceKeyboard.R;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;

public class VoiceKeyboardInputMethodService extends InputMethodService {

    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("rust");
        RustLib.init(getApplicationContext(), getAssets());


    }

    private Optional<AudioDeviceConfig> getBottomMicrophone() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        AudioDeviceInfo[] adi = audioManager.getDevices(AudioManager.GET_DEVICES_INPUTS);
        Optional<AudioDeviceInfo> bottomMic = Arrays.stream(adi)
                .filter(audioDeviceInfo -> audioDeviceInfo.getAddress().equals("bottom"))
                .findAny();

        if (bottomMic.isPresent()) {

            OptionalInt maxSampleRate = Arrays.stream(bottomMic.get().getSampleRates())
                    .filter(x -> x == 16000)
                    .findAny();
            OptionalInt minChannels = Arrays.stream(bottomMic.get().getChannelCounts())
                    .filter(x -> x == 2)
                    .findAny();
            if (maxSampleRate.isPresent() && minChannels.isPresent()) {
                AudioDeviceConfig audioDeviceConfig = new AudioDeviceConfig(bottomMic.get().getId(), maxSampleRate.getAsInt(), minChannels.getAsInt());


                return Optional.of(audioDeviceConfig);
            }
            return Optional.empty();
        }
        return Optional.empty();
    }

    @Override
    public void onDestroy() {
        RustLib.uninit();
        super.onDestroy();
    }


    @Override
    public View onCreateInputView() {
        View inputView =
                getLayoutInflater().inflate(R.layout.keyboard, null);
        ToggleButton recordButton = inputView.findViewById(R.id.buttonRecord);

        recordButton.setOnCheckedChangeListener((button, checked) -> {
            if (checked && getBottomMicrophone().isPresent()) {
                AudioDeviceConfig bottomMic = getBottomMicrophone().get();
                RustLib.startRecording(bottomMic.getDeviceId(), bottomMic.getDeviceSampleRate(), bottomMic.getDeviceChannels());
            } else {
                String result = RustLib.endRecording();
                getCurrentInputConnection().commitText(result, result.length());
            }
        });

        return inputView;
    }


}