package com.onoff.heatmap.models;

import com.onoff.heatmap.config.RequestConstraintsProps;
import com.onoff.heatmap.validation.ValidHour;
import com.onoff.heatmap.validation.ValidNumberOfShades;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

public record RequestDto(
        @NotNull
        @ValidNumberOfShades
        Integer numberOfShades,

        @ValidHour
        Integer startHour,

        @ValidHour
        Integer endHour,

        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dateInput
) {

    public RequestDto normalize(RequestConstraintsProps constraints) {
        int normalizedStartHour = (startHour != null) ? startHour : constraints.getHourMin();
        int normalizedEndHour = (endHour != null) ? endHour : constraints.getHourMax();

        if (normalizedEndHour < normalizedStartHour) {
            throw new IllegalArgumentException("endHour must be greater than or equal to startHour");
        }

        return new RequestDto(
                numberOfShades,
                normalizedStartHour,
                normalizedEndHour,
                dateInput
        );
    }

}
