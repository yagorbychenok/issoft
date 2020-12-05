package by.bychenok.building.elevator;

import by.bychenok.building.floor.FloorSystem;
import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static by.bychenok.building.elevator.Direction.*;

@Slf4j
public class ElevatorsManager implements Runnable {
    private final BlockingQueue<ElevatorRequest> requests;
    private final List<Elevator> elevators;
    private boolean isWaitingForNewTask;

    public ElevatorsManager(BlockingQueue<ElevatorRequest> requests,
                            int elevatorCount,
                            int doorOpenCloseTimeSeconds,
                            int floorPassTimeSeconds,
                            int startElevatorFloor,
                            int liftingCapacity,
                            FloorSystem floorSystem) {
        this.requests = requests;
        isWaitingForNewTask = true;
        elevators = ImmutableList.copyOf(IntStream
                .range(0, elevatorCount)
                .mapToObj(i -> new Elevator(i, doorOpenCloseTimeSeconds,
                        floorPassTimeSeconds, startElevatorFloor, liftingCapacity,
                        floorSystem, this))
                .collect(Collectors.toList()));
    }

    public synchronized void manageNewRequest() {
        if (isWaitingForNewTask) {
            notifyAll();
        }
    }

    public void startElevators() {
        elevators.stream()
                .map(Thread::new)
                .forEach(Thread::start);
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            while (!requests.isEmpty()) {
                isWaitingForNewTask = false;
                ElevatorRequest request = requests.take();
                log.info("New request: {} taken by manager. Requests left: {}",
                        request.getId(), requests.size());
                AtomicReference<Boolean> isManaged = new AtomicReference<>(false);
                while (!isManaged.get()) {
                    elevators.stream()
                            .filter(Elevator::isAvailable)
                            .findAny().ifPresent(elevator -> {
                                elevator.pickUpPassenger(request);
                                isManaged.set(true);
                            });

                    if (!isManaged.get()) {
                        if (request.getDirection() == UP) {
                            elevators.stream()
                                    .filter(Elevator::isCarryingUp)
                                    .filter(elevator -> elevator.getCurrentFloor() < request.getFloor())
                                    .findAny()
                                    .ifPresent(elevator -> {
                                        elevator.pickUpPassenger(request);
                                        isManaged.set(true);
                                    });
                        } else {
                            elevators.stream()
                                    .filter(Elevator::isCarryingDown)
                                    .filter(elevator -> elevator.getCurrentFloor() > request.getFloor())
                                    .findAny()
                                    .ifPresent(elevator -> {
                                        elevator.pickUpPassenger(request);
                                        isManaged.set(true);
                                    });
                        }
                    }
                    synchronized (this) {
                        if (!isManaged.get()) {
                            log.info("No elevators to handle request: {}, " +
                                    "waiting for available elevators ...", request.getId());
                            wait();
                        }
                    }
                }
            }
            // DANGER
            synchronized (this) {
                isWaitingForNewTask = true;
                log.info("No requests to handle. Waiting for new request ...");
                wait();
            }
        }
    }
}
