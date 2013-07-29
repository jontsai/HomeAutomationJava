package automaton.routines;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.sun.speech.freetts.cart.Phraser;

import util.TimeUtils;
import automaton.AppConstants;
import automaton.Automaton;

public class AutomatonRoutines implements AppConstants {
    protected Automaton automaton;
    
    // clock routines
    private GregorianCalendar calendar;
    private SimpleDateFormat dateFormat;

    private long lastSpeakTime;            // in milliseconds
    private int speakInterval = 300000;    // in milliseconds
    
    public AutomatonRoutines(Automaton automaton) {
        this.automaton = automaton;
        createCalendar();
    }
    
    /**
     * Create the GregorianCalendar that keeps track of the time.
     */
    private void createCalendar() {
        calendar = new GregorianCalendar();

        // sets the format to display the current time
        // the format is "3:50 PM"
        dateFormat = new SimpleDateFormat("h:mm a");
        dateFormat.setCalendar(calendar);
    }
    
    /**
     * Updates the calendar and the display with the current time.
     */
    private void updateTime() {
        Date currentTime = new Date();
        calendar.setTime(currentTime);
        //setTimeLabel(dateFormat.format(currentTime));
    }
    
    /**
     * Speaks the current time.
     */
    public void speakTime() {
        updateTime();
        lastSpeakTime = calendar.getTimeInMillis();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);        

        if (hour < 0 || hour > 23) {
            throw new IllegalArgumentException("Bad time format: hour");
        }
        if (min < 0 || min > 59) {
            throw new IllegalArgumentException("Bad time format: min");
        }
        
        String theTime = TimeUtils.timeToString(hour, min);
        automaton.queueSpeech(theTime, PHRASE_TYPE_TIME);
    }
    
    /**
     * Speaks the phrase
     * @param phrase
     */
    public void echo(String phrase) {
        automaton.queueSpeech(phrase, PHRASE_TYPE_GENERAL);
    }
}
