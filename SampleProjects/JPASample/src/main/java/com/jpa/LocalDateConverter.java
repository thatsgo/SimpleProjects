package com.jpa;

import java.sql.Date;
import java.time.LocalDate;

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

