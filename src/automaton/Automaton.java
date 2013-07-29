package automaton;

import java.util.Vector;

import automaton.recognition.SpeechRecognition;
import automaton.recognition.SpeechRecognitionListener;
import automaton.routines.AutomatonRoutines;
import automaton.speech.SpeechSynthesis;

public class Automaton implements AppConstants {
    private HomeAutomation gui;
    // object handles
    private SpeechRecognition recognizer;
    private SpeechSynthesis synthesizer;
    private AutomatonRoutines routines;
    // state
    private Vector<QueuedSpeechPhrase> speechQueue;
    private boolean speaking;
    private boolean dormant;
    private int dormantCommandsGiven;
    private boolean waitingForRecognition;
    
    public Automaton(HomeAutomation ha) {
        gui = ha;
        // initialize members
        synthesizer = new SpeechSynthesis(this);
        routines = new AutomatonRoutines(this);
        recognizer = new SpeechRecognition(this);
        recognizer.addSpeechRecognitionListener(new SpeechRecognitionListener() {
            
            @Override
            public void notify(String phrase) {
                if (phrase != null) {
                    gui.updateStatus("I think you said: " + phrase + '\n');
                }
                parseVerbalCommand(phrase);
            }
        });
        // initialize state
        stopSpeaking();
        speechQueue = new Vector<Automaton.QueuedSpeechPhrase>();
        robotSleep();
        dormantCommandsGiven = 0;
    }

    public void startAutomaton() {
        AutomatonSpeechThread speechThread = new AutomatonSpeechThread();
        AutomatonRecognitionThread recognitionThread = new AutomatonRecognitionThread();
        speechThread.start();
        recognitionThread.start();
    }
    
    public AutomatonRoutines getRoutines() {
        return routines;
    }
    
    public boolean hasSomethingToSay() {
        return speechQueue.size() > 0;
    }
    
    public boolean readyToListen() {
        return !isSpeaking() && !hasSomethingToSay() && !isWaitingForRecognition();
    }
    
    public synchronized void acknowledge() {
        queueSpeech(_LR_ACKNOWLEDGE);
    }
    
    public synchronized boolean queueSpeech(String phrase) {
        return queueSpeech(phrase, PHRASE_TYPE_GENERAL);
    }
    
    public synchronized boolean queueSpeech(String phrase, int phraseType) {
        boolean status = false;
        if (!phrase.equals("")) {
            speechQueue.add(new QueuedSpeechPhrase(phrase, phraseType));
            status = true;
        }
        return status;
    }
    
    public synchronized void speak() {
        if (hasSomethingToSay()) {
            QueuedSpeechPhrase queuedPhrase = speechQueue.remove(0);
            startSpeaking();
            synthesizer.speak(queuedPhrase.getPhrase(), queuedPhrase.getPhraseType());
            stopSpeaking();
        }
    }
    
    public synchronized void listen() {
        if (readyToListen()) {
            waitingForRecognition = true;
            recognizer.microphoneOn();
        }
    }
    
    public synchronized void parseVerbalCommand (String command) {
        waitingForRecognition = false;
        if (readyToListen()) {
            if (isDormant() && command != null) {
                if (command.equals("ADAM WAKE UP")) {
                    robotWake();
                    acknowledge();
                } else if (command.equals("INITIATE SHUTDOWN")) {
                    recognizer.shutdown();
                    System.exit(0);
                } else {
                    dormantCommandsGiven++;
                    if (dormantCommandsGiven >= DORMANT_COMMANDS_THRESHOLD) {
                        queueSpeech(_LR_SLEEPING);
                        dormantCommandsGiven = 0;
                    }
                }
            } else {
                if (command == null) {
                    updateStatus(_LR_TXT_COULD_NOT_UNDERSTAND);
                    queueSpeech(_LR_COULD_NOT_UNDERSTAND);
                } else if (command.isEmpty()) {
                    updateStatus(_LR_TXT_HEARD_SILENCE);
                    queueSpeech(_LR_HEARD_SILENCE);
                } else if (command.equals("GO TO SLEEP")) {
                    dormant = true;
                    acknowledge();
                } else if (command.equals("HELLO ADAM")) {
                    queueSpeech("Hello Jonathan");
                } else if (command.equals("GOOD MORNING ADAM")) {
                    queueSpeech("Good morning Jonathan");
                } else if (command.equals("WHAT TIME IS IT")) {
                    routines.speakTime();
                } else if (command.equals("STOP")) {
                    updateStatus("STOP");
                    speechQueue.clear();
                    try {
                        Thread.sleep(SLEEP_RESET_MILLIS);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }
    }

    public void updateStatus(String status) {
        gui.updateStatus(status);
    }
    
    /**
     * Getters and setters
     */
    public void robotSleep() {
        dormant = true;
    }
    
    public void robotWake() {
        dormant = false;
    }
    
    public void startSpeaking() {
        speaking = true;
    }
    
    public void stopSpeaking() {
        speaking = false;
    }
    
    public void startWaitingForRecogition() {
        waitingForRecognition = true;
    }
    
    public void stopWaitingForRecognition() {
        waitingForRecognition = false;
    }
    
    public boolean isDormant() {
        return dormant;
    }
    
    public boolean isSpeaking() {
        return speaking;
    }
    
    public boolean isWaitingForRecognition() {
        return waitingForRecognition;
    }
    
    /**
     * Internal classes
     */
    
    class QueuedSpeechPhrase {
        private String phrase;
        private int phraseType;
        public QueuedSpeechPhrase(String phrase, int phraseType) {
            this.phrase = phrase;
            this.phraseType = phraseType;
        }
        public String getPhrase() {
            return phrase;
        }
        public int getPhraseType() {
            return phraseType;
        }
    }
    
    
    /**
     * A thread for speech synthesis
     */
    class AutomatonSpeechThread extends Thread {

        public void run() {
            while (true) {
                speak();
                try {
                    Thread.sleep(SLEEP_THREAD_MILLIS);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
    
    /**
     * A thread for speech recognition
     */
    class AutomatonRecognitionThread extends Thread {

        public void run() {
            while (true) {
                listen();
                try {
                    Thread.sleep(SLEEP_THREAD_MILLIS);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
    }
}
