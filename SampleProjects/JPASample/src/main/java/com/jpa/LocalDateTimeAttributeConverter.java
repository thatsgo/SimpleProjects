package com.jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

	public LocalDateTimeAttributeConverter() {
		System.out.println("@@@@@@");
	}

	@Override
	public Timestamp convertToDatabaseColumn(LocalDateTime locDateTime) {
		System.out.println("Converting....");
		return (locDateTime == null ? null : Timestamp.valueOf(locDateTime));
	}

	@Override
	public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
		System.out.println("Converting....!");
		return (sqlTimestamp == null ? null : sqlTimestamp.toLocalDateTime());
	}
}




