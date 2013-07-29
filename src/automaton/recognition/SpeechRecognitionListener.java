package automaton.recognition;

public interface SpeechRecognitionListener {
    /** Invoked when a new phrase is recognized */
    void notify(String phrase);
}