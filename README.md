# kds4j
Kinetic Data Structures for Java 8+

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

## How? 
1. Kinetic sorted list , [link](https://en.wikipedia.org/wiki/Kinetic_sorted_list)
2. Kinetic priority queue, see [wikipedia](https://en.wikipedia.org/wiki/Kinetic_priority_queue): 
   - A special case of a sort list where it is only necessary to to have persists the current top priority element at any given time
3. Kinetic convex hull, [link](https://en.wikipedia.org/wiki/Kinetic_convex_hull)
4. Kinetic bounding box
   - A less expensive and perhaps just as useful a version of the convex hull data structure
5. [Kinetic closest pair](https://en.wikipedia.org/wiki/Kinetic_closest_pair)
6. [Kinetic minimum spanning tree](https://en.wikipedia.org/wiki/Minimum_spanning_tree)

## Sources
1. Lecture notes from stanford, [link](http://graphics.stanford.edu/courses/cs268-11-spring/notes/kinetic.pdf)
2. [Another lecture](https://pdfs.semanticscholar.org/198f/4826cc2ecee3755c21005a3269d83789a1fe.pdf)
3. [Nice website](http://www.cs.au.dk/~gerth/madalgo/posters/08/pdf/Mohammad.pdf)
4. [A PhD thesis on the topic](http://citeseerx.ist.psu.edu/viewdoc/download;jsessionid=A4A74294C3D7EF67726DF747C12E8A5B?doi=10.1.1.41.2301&rep=rep1&type=pdf)
