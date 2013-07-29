package automaton;
import java.awt.Dimension;

public interface AppConstants {
    public final static String APP_TITLE = "Home Automation";
    // labels
    public final static String _NAME = "Hello, my name is ";
    public final static String _AUTOMATON_NAME = "Adam";
    public final static String _CREATED_BY = "I was created by ";
    public final static String _CREATOR = "Jonathan";
    public final static String _AWAITING_COMMAND = "Awaiting command";
    public final static String _REQUEST_COMMAND = "Please say a command.";
    public final static String _REQUEST_COMMAND_TEXT = "Please say or type a command.";
    public final static String _LISTENING_FOR_COMMAND = "Listening for command...";
    public final static String _HYBRID_MODE = "Run in hybrid mode (accept voice and text commands).";
    public final static String _SPEAK_TIME = "Speak Time";
    public final static String _RUN_COMMAND = "Run Command";
    public final static char MN_HYBRID_MODE = 'H';
    public final static char MN_RUN_COMMAND = 'R';
    // listen responses
    public final static String _LR_ACKNOWLEDGE = "OKAY";
    public final static String _LR_COULD_NOT_UNDERSTAND = "I'm sorry, I could not understand that";
    public final static String _LR_HEARD_SILENCE = "I'm sorry, did you say something?";
    public final static String _LR_SLEEPING = "I'm still sleeping, do you want me to wake up?";
    // listen response text messages
    public final static String _LR_TXT_COULD_NOT_UNDERSTAND = "I could not understand what you said. <NO-OP>";
    public final static String _LR_TXT_HEARD_SILENCE = "Heard silence. <NO-OP>";
    // error messages
    public final static String _ERR_NO_SYNTHESIZER = "Cannot create synthesizer";
    public final static String _ERR_NO_MICROPHONE = "Cannot start microphone.";
    // dimensions
    public final static Dimension DIM_APP = new Dimension(800, 600);
    public final static Dimension DIM_TEXT_FIELD_COMMAND = new Dimension(550, 50);
    // timing
    public final static int SLEEP_THREAD_MILLIS = 100;
    public final static int SLEEP_RESET_MILLIS = 3500;
    public final static int SPEECH_DELAY_MILLIS = 500;
    public final static int LISTEN_WAIT_CYCLES = 100;
    // other
    public final static int PHRASE_TYPE_GENERAL = 1;
    public final static int PHRASE_TYPE_TIME = 2;
    public final static int DORMANT_COMMANDS_THRESHOLD = 3;
    // fonts
    public final static int FONT_SIZE = 24;
}
