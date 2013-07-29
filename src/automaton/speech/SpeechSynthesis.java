package automaton.speech;

import java.util.Locale;

import javax.speech.EngineCreate;
import javax.speech.EngineList;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

import automaton.AppConstants;
import automaton.Automaton;

import com.sun.speech.freetts.jsapi.FreeTTSEngineCentral;

public class SpeechSynthesis implements AppConstants {
    private Automaton automaton;
    protected Synthesizer synthesizerGeneral;
    protected Synthesizer synthesizerTime;
    
    public SpeechSynthesis(Automaton automaton) {
        this.automaton = automaton;
        synthesizerGeneral = createSynthesizer("general");
        synthesizerTime = createSynthesizer("time");
    }
    
    /**
     * Creates the synthesizer, called by the constructor.
     */
    public Synthesizer createSynthesizer(String mode) {
        Synthesizer synthesizer = null;
        try {
            SynthesizerModeDesc desc = 
                new SynthesizerModeDesc(null, 
                                        mode,
                                        Locale.US, 
                                        Boolean.FALSE,
                                        null);

            FreeTTSEngineCentral central = new FreeTTSEngineCentral();
            EngineList list = central.createEngineList(desc); 
            
            if (list != null && list.size() > 0) { 
                EngineCreate creator = (EngineCreate) list.get(0); 
                synthesizer = (Synthesizer) creator.createEngine(); 
            } 
            if (synthesizer == null) {
                System.err.println(_ERR_NO_SYNTHESIZER);
            } else {
                synthesizer.allocate();
                synthesizer.resume();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return synthesizer;
    }

    /**
     * Speaks the given phrase.
     *
     * @param phrase 
     */
    public void speak(String phrase, int phraseType) {
        Synthesizer synthesizer;
        if (phraseType == PHRASE_TYPE_TIME) {
            synthesizer = synthesizerTime;
        } else {
            synthesizer = synthesizerGeneral;
        }
        automaton.updateStatus("Saying: " + phrase);
        synthesizer.speakPlainText(phrase, null);
        try {
            // Wait until speaking is done
            synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
            Thread.sleep(SPEECH_DELAY_MILLIS);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }
}
