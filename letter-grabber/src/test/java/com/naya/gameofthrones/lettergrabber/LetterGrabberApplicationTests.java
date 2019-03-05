package com.naya.gameofthrones.lettergrabber;

import com.github.javafaker.Faker;
import com.github.javafaker.GameOfThrones;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LetterGrabberApplicationTests {

    @Test
    public void testFakeData() {
        Faker faker = new Faker();
        GameOfThrones gameOfThrones = faker.gameOfThrones();
        Stream.generate(gameOfThrones::character).limit(1000000).forEach(System.out::println);
//        boolean b = Stream.generate(gameOfThrones::character).anyMatch("Stark"::contains);
//        System.out.println("b = " + b);
    }

}

