package com.jigumulmi.place.domain;

import com.jigumulmi.admin.place.dto.request.AdminCreateTemporaryBusinessHourRequestDto;
import com.jigumulmi.common.WeekUtils;
import com.jigumulmi.place.dto.BusinessHour;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"place_id", "date"})})
public class TemporaryBusinessHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    private Integer year;
    private Integer month;
    private Integer weekOfYear;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    private LocalTime openTime;
    private LocalTime closeTime;
    private LocalTime breakStart;
    private LocalTime breakEnd;
    private boolean isDayOff;

    @Builder
    public TemporaryBusinessHour(Place place, Integer year, Integer month, Integer weekOfYear,
        LocalDate date, DayOfWeek dayOfWeek, LocalTime openTime, LocalTime closeTime,
        LocalTime breakStart, LocalTime breakEnd, boolean isDayOff) {
        this.place = place;
        this.year = year;
        this.month = month;
        this.weekOfYear = weekOfYear;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.breakStart = breakStart;
        this.breakEnd = breakEnd;
        this.isDayOff = isDayOff;
    }

    public void adminUpdate(AdminCreateTemporaryBusinessHourRequestDto requestDto) {
        BusinessHour businessHour = requestDto.getBusinessHour();

        LocalDate date = requestDto.getDate();
        int weekOfYear = WeekUtils.getWeekOfYear(date);

        this.year = date.getYear();
        this.month = date.getMonthValue();
        this.weekOfYear = weekOfYear;
        this.date = date;
        this.dayOfWeek = date.getDayOfWeek();
        this.openTime = businessHour.getOpenTime();
        this.closeTime = businessHour.getCloseTime();
        this.breakStart = businessHour.getBreakStart();
        this.breakEnd = businessHour.getBreakEnd();
        this.isDayOff = businessHour.isDayOff();
    }
}
