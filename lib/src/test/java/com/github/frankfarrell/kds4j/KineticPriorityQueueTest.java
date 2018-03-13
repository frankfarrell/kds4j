package com.github.frankfarrell.kds4j;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Created by frankfarrell on 13/03/2018.
 */
public class KineticPriorityQueueTest {

    @Test
    public void itHasTheCorrectHeadElementAsTimeAdvances(){
        KineticPriorityQueue<String> queueUnderTest = new KineticPriorityQueue<>(0L);

        /*
        Priority will be the follow
        time:
        0  -> A, B, C
        2  -> B, A, C
        8  -> B, C, A
        10 -> C, B, A
         */

        queueUnderTest.add(new QueueElement<>("A", x -> 8-x));
        queueUnderTest.add(new QueueElement<>("B", x -> x/2 + 5));
        queueUnderTest.add(new QueueElement<>("C", x -> (x*x)/2 -4*x));

        assertThat(queueUnderTest.peek().element).isEqualTo("A");

        assertThat(queueUnderTest.advance(3L)).isTrue();
        assertThat(queueUnderTest.peek().element).isEqualTo("B");

        //Order does not change from 3->4
        assertThat(queueUnderTest.advance(4L)).isFalse();

        assertThat(queueUnderTest.advance(9L)).isTrue();
        assertThat(queueUnderTest.peek().element).isEqualTo("B");

        assertThat(queueUnderTest.advance(11L)).isTrue();
        assertThat(queueUnderTest.peek().element).isEqualTo("C");
    }

}
