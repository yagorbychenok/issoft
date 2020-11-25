package by.bychenok.person;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
@Getter
public class Person {

    private final UUID uuid;
    private final int weight;
    private final int currentFloor;
    private final int destinationFloor;

    public Person(UUID uuid, int weight, int floor, int destinationFloor) {
        checkArgument(weight > 0,
                "Weight must be more than 0");
        checkArgument(floor != destinationFloor,
                "Current floor and destination floor must be different");
        this.uuid = uuid;
        this.weight = weight;
        this.currentFloor = floor;
        this.destinationFloor = destinationFloor;
        log.info("User with uuid: {}, weight: {}, floor: {} was successfully created",
                uuid.toString(), weight, floor);
    }

}