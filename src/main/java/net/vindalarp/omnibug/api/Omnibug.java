package net.vindalarp.omnibug.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static net.vindalarp.Omnia.MOD_ID;

/**
 *
 * Incredibly minimalistic debugging API.<br />
 * Purpose: Conditional debugging.<br />
 *
 * What does that mean? <br />
 * Instead of adding debug lines every time you want to catch a bug and removing them afterwards, you can add them wherever and whenever you want to and then enable / disable them collectively. <br />
 * This way, you can toggle anything from prints to whole blocks of code!
 */
public class Omnibug {

    /**
     * The logger responsible for actually outputting stuff.
     */
    protected static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Main config / toggle
    protected static volatile boolean enabled = false;
    /**
     * Whether Omnibug is enabled or not. False by default.
     */
    public static boolean isEnabled() { return enabled; }

    /**
     *
     * Disables Omnibug and all related methods and features.<br />
     * Perfect for when you're done with debugging.
     * <br />
     * Hint: This is false by default anyways, so if you're not debugging right now, you don't have to call this.
     */
    public static void disable() { enabled = false; }

    /**
     *
     * Enables Omnibug and all related methods and features.<br />
     * Since Omnibug is disabled by default, you should call this whenever you want to debug something.
     */
    public static void enable() { enabled = true; }

    // Inner classes

    /** Creates a new Omnibug tag. Preferably use <code>new Omnibug.Tag()</code> instead. */
    public static Omnibug.Tag tag(Boolean startEnabled) { return new Omnibug.Tag(startEnabled); }
    /** Creates a new Omnibug tag. Preferably use <code>new Omnibug.Tag()</code> instead. */
    public static Omnibug.Tag createTag(Boolean startEnabled) { return tag(startEnabled); }

    /**
     * An Omnibug Tag.<br />
     * This is basically a toggleable class that runs (or doesn't run) all the debug functions you call on it.<br />
     * Purpose: Avoid having to add and remove debug lines every time you want to try something. Simply toggle instead.
     */
    public static class Tag {

        protected volatile boolean enabled = false;

        /**
         * Creates a new flexible Omnibug Tag
         * @param startEnabled Sets whether this Tag should be enabled or not. Leaving this blank makes it default to false.
         */
        public Tag(Boolean startEnabled) {
            this.enabled = startEnabled != null && startEnabled.booleanValue();
        }
        /** Creates a new flexible Omnibug Tag. Optionally pass <code>true</code> to enable it immediately */
        public Tag() { this(false); }

        /**
         * Whether this Debug Tag is enabled or not.
         */
        public boolean isEnabled() { return enabled; }

        /**
         *
         * Disables ALL methods and features of this tag.<br />
         * Perfect for when you're done with debugging.
         * <br />
         * Hint: This is false by default anyways, so if you're not debugging right now, you don't have to call this.
         */
        public void disable() { enabled = false; }

        /**
         *
         * Enables ALL methods and features of this tag.<br />
         * Since this is disabled by default, you should call this whenever you want to debug something.
         */
        public void enable() { enabled = true; }

        protected boolean mayRun() {
            return this.enabled && Omnibug.enabled;
        }

        /**
         * Prints a basic output message
         * @param message The message to print
         */
        public void out(Supplier<String> message) {
            if (!this.mayRun()) { return; }
            LOGGER.info(message.get());
        }
        /** Overloads Tag.out(). This may be more expensive to run than the Supplier version, so only use this for basic string outputs. */
        public void out(String message) {this.out(() -> message);}

        /**
         * Prints a basic warning message
         * @param message The message to print
         */
        public void warn(Supplier<String> message) {
            if (!this.mayRun()) { return; }
            LOGGER.warn(message.get());
        }
        /** Overloads Tag.warn(). This may be more expensive to run than the Supplier version, so only use this for basic string outputs. */
        public void warn(String message) {this.warn(() -> message);}

        /**
         * Prints an error message
         * @param message The message to print
         */
        public void error(Supplier<String> message) {
            if (!this.mayRun()) { return; }
            LOGGER.error(message.get());
        }
        /** Overloads Tag.error(). This may be more expensive to run than the Supplier version, so only use this for basic string outputs. */
        public void error(String message) {this.error(() -> message);}

        /**
         * Runs code - but only if this Tag is enabled. Perfect for more in-depth testing and tweaking.
         * @param code The lambda to run.
         */
        public void execute(Runnable code) {
            if (!this.mayRun()) { return; }
            code.run();
        }
        /** Alias for Tag.execute() */
        public void ifEnabled(Runnable code) { this.execute(code); }

    }
}