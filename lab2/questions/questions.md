# Lab 2 - Theory Questions

## 1. In the vacuum cleaner domain in part 1, what were the states and actions? What is the branching factor?

## 2. What is the difference between Breadth First Search and Uniform Cost Search in a domain where the cost of each action is 1?

## 3. Suppose that h1 and h2 are admissible heuristics (used in for example A*). Which of the following are also admissible?
a) (h1+h2)/2
b) 2h1
c) max (h1,h2)

## 4. If one would use A* to search for a path to one specific square in the vacuum domain, what could the heuristic (h) be? The cost function (g)? Is it an admissible heuristic?

## 5. Draw and explain. Choose your three favorite search algorithms and apply them to any problem domain (it might be a good idea to use a domain where you can identify a good heuristic function). Draw the search tree for them, and explain how they proceed in the searching. Also include the memory usage. You can attach a hand-made drawing.

## 6. Look at all the offline search algorithms presented in chapter 3 plus A* search. Are they complete? Are they optimal? Explain why!

breadth first
depth first
uniform cost search
depth limited search
 Iterative deepening depth-first search
Bidirectional search


Optimal solution - The solution has the lowest path cost among all solutions.
Completeness - Is the algorithm garanteed to find a solution when there is one?

A* search 
It could be both optimal and complete but it depends on h(n). To be optimal if it never oversestimates the cost to reach the goal (admissible heuristic). If we for example uses the straight line distance as the heuristic it will never overestimate the distance and therefore the algorithm will be optimal. 

"the tree-search version of A* is optimal if h(n) is admissible, while the graph-search version is optimal if h(n) is consistence"

Breadth-first 
it is complete because all nodes will be visited in the end. It is guranteed optimal only if the step cost is the same since it will return the goal node with fewest steps from the start.

Uniform-cost search
It is optimal because it finds the path to the goal node with minimal cost. It might take longer time than A* because it doesn't consider which direction it is going. As long as the edge cost is positive we believe that it is also complete. 

Depth-first search
It is not complete because it might be loops in the tree which will make it iterate between nodes. It is not optimal either because it stops at the first found goal and doesn't investigate other options. 

Depth-first search limited
If the search is limited it cant be sure we find the goal, it is therefore not complete and since it is not complete it can't be optimal.

Iterative deepening depth-first search
Since it it increasing the depth until the goal is found, the goal will always be found (if we have finite numer of child nodes) and therefore it is complete. It is optimal if the step cost is the same to each level of nodes. 

Bidirectional
The search will be complete if both sides is using breadth first search and optimal if the step cost is the same. 

First Cost First Limited Deepening (if applicable)


## 7. Assume that you had to go back and do lab 1 once more, but this time with obstacles. Remember that the agent did not have perfect knowledge of the environment but had to explore it incrementally. Could you still use the search algorithms you have learned to guide the agent's execution? What would you search for? Give an example.
