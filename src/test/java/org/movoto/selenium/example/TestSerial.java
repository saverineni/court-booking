package org.movoto.selenium.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestSerial {

    public static void main(String args[]) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Car car = new Car();
        car.setColor("blue");
        car.setType("ABC");
        //car.setNames(Arrays.asList("suresh"));

        String valueAsString = mapper.writeValueAsString(car);
        System.out.println(valueAsString);

        ObjectNode jsonNode = (ObjectNode) mapper.readTree(valueAsString);
        jsonNode.remove("type");
        valueAsString = mapper.writeValueAsString(jsonNode);
        System.out.println(valueAsString);

        Car newCar = mapper.readValue(valueAsString, Car.class);
        System.out.print(newCar);

    }
}


class Car {
    String color;
    String type;

    List<String> names;

    public void setColor(String color) {
        this.color = color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return this.color;
    }

    public String getType() {
        return type;
    }

    public List<String> getNames() {
        if(names == null) {
            return Collections.emptyList();
        }
        if(names.isEmpty()){
            return Collections.singletonList("");
        }
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }


}
