package com.naya.gameofthrones.lettergrabber.producer;

import com.naya.speedadjuster.mode.Letter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Evgeny Borisov
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class LetterProducerTest {

    @Autowired
    LetterProducerFake producer;

    @Test
    public void testProducer() {
        Letter letter = producer.getLetter();
        Assert.assertNotNull(letter);
        Assert.assertNotNull(letter.getContent());
        Assert.assertNotNull(letter.getLocation());
        Assert.assertNotNull(letter.getSignature());
    }
}
