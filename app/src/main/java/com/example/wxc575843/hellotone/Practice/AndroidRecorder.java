package com.example.wxc575843.hellotone.Practice;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

/**
 * Created by xhxu on 2016/4/3.
 */
public class AndroidRecorder {
    private static final int MAX_LIMIT = 3333;
    private static AudioRecord recorder;
    private static short[] data;
    private static float[] yinData;
    private static final int SAMPLE_RATE = 48000;
    private static final int SAMPLES = 1024;
    private static boolean shouldStop = false;
    private static final Yin yin = new Yin(44100, 1024, 0.10);
    private static final MPM mpm = new MPM(SAMPLE_RATE, SAMPLES, 0.93);
    private static int N;
    private static UIHelper uiHelper;

    public static void init(UIHelper paramUiHelper) {
        N = AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        data = new short[SAMPLES];
        yinData = new float[SAMPLES];
        uiHelper = paramUiHelper;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, N * 10);
        shouldStop = false;
        recorder.startRecording();
    }
    public static float[] shortToFloat(short[] data){
        float[] temp = new float[data.length];
        for (int i = 0; i < data.length; ++i) {
            temp[i] = Float.valueOf(String.valueOf(data[i]));
        }
        return  temp;
    }

    public static void run() {
        while ((!shouldStop)) {
            try {
                recorder.read(data, 0, data.length);
//                double pitch = mpm.getPitchFromShort(data);
                yinData = shortToFloat(data);
                double pitch =yin.getPitch(yinData).getPitch();
                if (pitch <= MAX_LIMIT) {
                    uiHelper.display(pitch);
                }

            } catch (Throwable x) {
                x.printStackTrace();
                System.exit(-1);
            }
        }
    }

    public static void deinit() {
        shouldStop = true;
        recorder.stop();
        recorder.release();
    }
}
