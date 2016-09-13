package com.jpa;

import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Date> {

	public LocalDateConverter() {
	
		System.out.println("SSSSSS");
	}
    @Override
    public Date convertToDatabaseColumn(LocalDate date) {
        return java.sql.Date.valueOf(date);
    }

    @Override
    public LocalDate convertToEntityAttribute(Date value) {
        return value.toLocalDate();
    }
}

