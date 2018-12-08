package com;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ObjectMappingCustomer extends ObjectMapper{
	public ObjectMappingCustomer()
    {
		super();
		getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

			@Override
			public void serialize(Object arg0, JsonGenerator arg1, SerializerProvider arg2) throws IOException {
				// TODO Auto-generated method stub
				arg1.writeString("");
			}
			
		});
    }
}
