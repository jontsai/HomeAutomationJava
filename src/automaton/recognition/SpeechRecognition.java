package automaton.recognition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import automaton.AppConstants;
import automaton.Automaton;
import automaton.HomeAutomation;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.recognizer.Recognizer.State;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;

public class SpeechRecognition implements Runnable, AppConstants {
    private Automaton automaton;
    private Recognizer recognizer;
    private ConfigurationManager cm;
    private Microphone microphone;
    private List<SpeechRecognitionListener> speechReconitionListeners;
    
    public SpeechRecognition (Automaton automaton) {
        this.automaton = automaton;
        
        cm = new ConfigurationManager(HomeAutomation.class.getResource("automaton.config.xml"));

        recognizer = (Recognizer) cm.lookup("recognizer");
        try {
            startup();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        microphone = (Microphone) cm.lookup("microphone");
        speechReconitionListeners = new ArrayList<SpeechRecognitionListener>();
    }
    
    /** Turns on the microphone and starts recognition */
    public boolean microphoneOn() {
        boolean status;
        if (microphone.getAudioFormat() == null) {
            status = false;
        } else {
            new Thread(this).start();
            status = true;
        }
        return status;
    }

    /** Turns off the microphone, ending the current recognition in progress */
    public void microphoneOff() {
        microphone.stopRecording();
    }

    /** Allocates resources necessary for recognition. */
    public void startup() throws IOException {
        recognizer.allocate();
    }

    /** Releases recognition resources */
    public void shutdown() {
        microphoneOff();
        if (recognizer.getState() == State.ALLOCATED) {
            recognizer.deallocate();
        }
    }
    
    /**
     * Performs a single recognition
     */
    public void run() {
        if (recognizer != null && microphone != null) {
            automaton.updateStatus(_LISTENING_FOR_COMMAND);
            microphone.clear();
            microphone.startRecording();
            Result result = recognizer.recognize();
            microphone.stopRecording();

            if (result != null) {
                String resultText = result.getBestFinalResultNoFiller();
                String phrase = resultText.trim().toUpperCase();
                fireListeners(phrase);
            }
        }
    }
    
    /**
     * Adds a listener that is called whenever a new phrase is recognized
     *
     * @param srl the SpeechRecognitionListener
     */
    public synchronized void addSpeechRecognitionListener(SpeechRecognitionListener srl) {
        speechReconitionListeners.add(srl);
    }


    /**
     * Removes a previously added SpeechRecognitionListener
     *
     * @param srl the SpeechRecognitionListener
     */
    public synchronized void removeSpeechRecognitionListener(SpeechRecognitionListener srl) {
        speechReconitionListeners.remove(srl);
    }


    /**
     * Invoke all added SpeechRecognitionListeners
     *
     * @param phrase the recognized phrase
     */
    private synchronized void fireListeners(String phrase) {
        for (SpeechRecognitionListener srl : speechReconitionListeners) {
            srl.notify(phrase);
        }
    }
}
