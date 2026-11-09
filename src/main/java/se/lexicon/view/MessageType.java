package se.lexicon.view;

/** Kind of status message the controller asks a view to show, so each view can style it (e.g. colour). */
public enum MessageType {
    /** Neutral information, e.g. "No balance to return." */
    INFO,
    /** An action worked, e.g. a product was dispensed. */
    SUCCESS,
    /** An action was rejected, e.g. insufficient balance. */
    ERROR
}
