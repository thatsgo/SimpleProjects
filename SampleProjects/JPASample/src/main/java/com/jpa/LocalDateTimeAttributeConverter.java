package com.jpa;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
/**
 * The same will work with javax.persistence version 2.1 and hibernate version 4.3.2.Final and higher.
 * 
 *               <dependency>
 *                    <groupId>org.eclipse.persistence</groupId>
 *                     <artifactId>javax.persistence</artifactId>
 *                   <version>2.1.0</version>
 *          </dependency>
 *             <dependency>
 *                    <groupId>org.hibernate</groupId>
 *                    <artifactId>hibernate-entitymanager</artifactId>
 *                    <version>4.3.2.Final</version>
 *             </dependency>
 *          
 *          http://www.thoughts-on-java.org/persist-localdate-localdatetime-jpa/
 *
 * 			http://stackoverflow.com/questions/36363913/unable-to-use-java-time-localdate-in-jpa-entity-with-mysql
 *
 *          http://stackoverflow.com/questions/28897303/persist-java-localdate-in-mysql

 *
 * @author krishna
 *
 */
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




