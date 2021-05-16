package com.learning.employee_management.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JsonConvertionUtils {

    public static String objectToJsonString (Object object){

        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,true);


            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
