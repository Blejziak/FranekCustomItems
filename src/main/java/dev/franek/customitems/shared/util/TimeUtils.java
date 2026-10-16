package dev.franek.customitems.shared.util;

import java.time.Duration;
import java.time.Instant;

public class TimeUtils {
    public static boolean hasPassed(Instant instant) {
        return instant.isBefore(Instant.now());
    }

    public static Duration getRemainingDuration(Instant instant) {
        return Duration.between(Instant.now(), instant);
    }

    public static String getFormattedTime(Duration duration, boolean shortForm) {
        long seconds;
        long minutes;
        long hours;
        if (duration.isNegative() || duration.isZero()) {
            return "teraz";
        }
        long secondsTotal = duration.getSeconds();
        StringBuilder sb = new StringBuilder();
        long days = secondsTotal / 86400L;
        if (days > 0L) {
            sb.append(TimeDivision.DAY.getFormatted(days, shortForm)).append(" ");
        }
        if ((hours = secondsTotal % 86400L / 3600L) > 0L) {
            sb.append(TimeDivision.HOUR.getFormatted(hours, shortForm)).append(" ");
        }
        if ((minutes = secondsTotal % 3600L / 60L) > 0L) {
            sb.append(TimeDivision.MINUTE.getFormatted(minutes, shortForm)).append(" ");
        }
        if ((seconds = secondsTotal % 60L) > 0L) {
            sb.append(TimeDivision.SECOND.getFormatted(seconds, shortForm));
        }
        return sb.toString().trim();
    }

    public static enum TimeDivision {
        SECOND(" sekunda", " sekundy", " sekund", "s"),
        MINUTE(" minuta", " minuty", " minut", "m"),
        HOUR(" godzina", " godziny", " godzin", "h"),
        DAY(" dzien", " dni", " dni", "d"),
        WEEK(" tydzien", " tygodnie", " tygodni", "w"),
        MONTH(" miesiac", " miesiace", " miesiecy", "msc"),
        YEAR(" rok", " lata", " lata", "y");

        private final String singularForm;
        private final String doubleForm;
        private final String pluralForm;
        private final String shortForm;

        private TimeDivision(String singularForm, String doubleForm, String pluralForm, String shortForm) {
            this.singularForm = singularForm;
            this.doubleForm = doubleForm;
            this.pluralForm = pluralForm;
            this.shortForm = shortForm;
        }

        private String getForm(long amount, boolean shortForm) {
            if (shortForm) {
                return this.shortForm;
            }
            if (amount == 1L) {
                return this.singularForm;
            }
            long ones = amount % 10L;
            long tens = amount % 100L;
            if (ones < 2L || ones > 4L || tens >= 12L && tens <= 14L) {
                return this.pluralForm;
            }
            return this.doubleForm;
        }

        public String getFormatted(long amount, boolean shortForm) {
            return amount + this.getForm(amount, shortForm);
        }
    }
}

