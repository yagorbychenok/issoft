package by.bychenok.random;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static by.bychenok.random.RandomNumberGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

class RandomNumberGeneratorTest {

    @Test
    void generateNumberInRangeWithoutValue_minGreaterOrEqualsToMax_fail() {
        //GIVEN
        int minGreater = 10;
        int minEquals = 9;
        int max = 9;
        int excludeValue = 9;

        //EXPECT
        assertThrows(IllegalArgumentException.class, () ->
                generateNumberInRangeWithoutValue(minGreater, max, excludeValue));
        //AND
        assertThrows(IllegalArgumentException.class, () ->
                generateNumberInRangeWithoutValue(minEquals, max, excludeValue));
    }

    @Test
    void generateNumberInRangeWithoutValue_excludeValueNotInRange_fail() {
        //GIVEN
        int min = 5;
        int max = 10;
        int excludeValueLow = 4;
        int excludeValueHigh = 10;

        //EXPECT
        assertThrows(IllegalArgumentException.class, () ->
                generateNumberInRangeWithoutValue(min, max, excludeValueLow));
        //AND
        assertThrows(IllegalArgumentException.class, () ->
                generateNumberInRangeWithoutValue(min, max, excludeValueHigh));
    }

    @Test
    void generateNumberInRangeWithoutValue_excludeValueEqualsMin_success() {
        //GIVEN
        int min = 5;
        int max = 10;
        int excludeValue = 5;

        //EXPECT
        assertDoesNotThrow(() -> generateNumberInRangeWithoutValue(min, max, excludeValue));
    }

    @Test
    void generateNumberInRangeWithoutValue_rangeSingleElement_fail() {
        //GIVEN
        int min = 1;
        int max = 2;
        int excludeValue = 1;

        //EXPECT
        assertThrows(IllegalArgumentException.class, () ->
                generateNumberInRangeWithoutValue(min, max, excludeValue));
    }
}