# kds4j
[![Build Status](https://travis-ci.org/frankfarrell/kds4j.svg?branch=master)](https://travis-ci.org/frankfarrell/kds4j)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/ae57f4ca78da48708efdbcd4b6e788b4)](https://www.codacy.com/app/frankfarrell/kds4j?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=frankfarrell/kds4j&amp;utm_campaign=Badge_Grade)


Kinetic Data Structures for Java 8+. Note this is a WIP to learn about kinetic data structures, so no claims about how performant or correct they are! 

## What is it?
Data structures where the ordering is a function of time. 

## Why? 
Typically kinetic data structures are used in graphics applications, in which case it is unlikely
you will be using java. See [CGAL](https://doc.cgal.org/latest/Kinetic_data_structures/index.html) and [this](https://www.ncbi.nlm.nih.gov/pmc/articles/PMC3001684/) 
for a c++ implementation of some kinetic data structures that could be useful in that scenario.
 
However, there is a wider application of use of such alogithms and this package provides a useful variety of them. 
For instance you may want to: 
1. Track relationships between actors moving in space, eg closest pair
2. Run some scheduling system where the priorities of jobs is a function of time, eg priority queue
3. Keep a bounding box of elements moving in space
4. Maintain an index for a set of elements that may change over time

## Use it
All Kinetic Data Structures have a method to advance time. It is the client's repsonsibility to advance time in the data structure. 
It probably shouldn't be done in a constant loop though! 
```java
Boolean advance(final Double t);
```

Creating a KineticSortedList: 
```java
final Double startTime = 0.0;
final KineticSortedList<String> myKineticSortedList = new KineticSortedList<String>(startTime);
```
You can add Strings with an associated priority function to the list
```java
myKineticSortedList.add(new OneDimensionalKineticElement<>("A", x -> 8 - x)); // A simple function
myKineticSortedList.add(new OneDimensionalKineticElement<>("B", x -> (x * x) / 2 - 4 * x)); // Something a bit more complicated
myKineticSortedList.add(new OneDimensionalKineticElement<>("C", x -> (x * x) / 2 * Math.sin(x) - 4 * x +  Math.cos(5-x))); // Something a bit more complicated
```
The lambda function is a plain java 8 Function<Double, Double>. Note that these data structures are only well defined for continuous functions since 
they use numerical methods for solving the intersection of functions. In particular it uses BracketingNthOrderBrentSolver from the Apache Commons Math package. 

You can advance the time to any time in the future
```java
Boolean anyReordering = myKineticSortedList.advance(4.0);
//anyReordering will be true if the order of any elements has changed.
//An exception will be thrown if you try to advance time backwards 
```

You can remove an element at an index
```java
myKineticSortedList.remove(10);
```

Kinetic PriorityQueue: 
```java
KineticPriorityQueue<String> queue = new KineticPriorityQueue<String>(0.0);

queue.add(new OneDimensionalKineticElement<>("A", x -> 8 - x));
queue.add(new OneDimensionalKineticElement<>("B", x -> x / 2 + 5));

assertThat(queueUnderTest.peek().element).isEqualTo("A");
assertThat(queueUnderTest.advance(11.0)).isTrue();
assertThat(queueUnderTest.poll().element).isEqualTo("C");
```

Kinetic Bounding Box 
```java
KineticBoundingBox<String> boundingBox = new KineticBoundingBox<String>(0.0);
boundingBox.add(new TwoDimensionalKineticElement<>("A", x -> 8 - x, x -> 8 + x));
boundingBox.add(new TwoDimensionalKineticElement<>("B", x -> x / 2 + 5, x -> x * x - x/3));

boundingBox.getBoundingBox();
```

## Current Data Structures supported
1. Kinetic sorted list 
   - Maintain a fully sorted list of all elements 
   - [link](https://en.wikipedia.org/wiki/Kinetic_sorted_list)
2. Kinetic priority queue, see [wikipedia](https://en.wikipedia.org/wiki/Kinetic_priority_queue): 
   - A special case of a sort list where it is only necessary to to have persists the current top priority element at any given time
4. Kinetic bounding box
   - Maintain a bounding box of elements moving in a two dimensional space. 

## Future work

3. [Kinetic convex hull](https://en.wikipedia.org/wiki/Kinetic_convex_hull)
5. [Kinetic closest pair](https://en.wikipedia.org/wiki/Kinetic_closest_pair)
6. [Kinetic minimum spanning tree](https://en.wikipedia.org/wiki/Minimum_spanning_tree)

## Sources
1. Lecture notes from stanford, [link](http://graphics.stanford.edu/courses/cs268-11-spring/notes/kinetic.pdf)
2. [Another lecture](https://pdfs.semanticscholar.org/198f/4826cc2ecee3755c21005a3269d83789a1fe.pdf)
3. [Nice website](http://www.cs.au.dk/~gerth/madalgo/posters/08/pdf/Mohammad.pdf)
4. [A PhD thesis on the topic](http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=A4A74294C3D7EF67726DF747C12E8A5B?doi=10.1.1.41.2301&rep=rep1&type=pdf)
