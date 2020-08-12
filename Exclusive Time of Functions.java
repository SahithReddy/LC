/* QUESTION: 636. Exclusive Time of Functions

On a single threaded CPU, we execute some functions.  Each function has a unique id between 0 and N-1.

We store logs in timestamp order that describe when a function is entered or exited.

Each log is a string with this format: "{function_id}:{"start" | "end"}:{timestamp}".  For example, "0:start:3" means the function with id 0 started at the beginning of timestamp 3.  "1:end:2" means the function with id 1 ended at the end of timestamp 2.

A function's exclusive time is the number of units of time spent in this function.  Note that this does not include any recursive calls to child functions.

The CPU is single threaded which means that only one function is being executed at a given time unit.

Return the exclusive time of each function, sorted by their function id.
*/
-----------------------------------------------
/* QUESTION EXPLANATION:
Input:
n = 2
logs = ["0:start:0","1:start:2","1:end:5","0:end:6"]
Output: [3, 4]

0-----1-----2-----3-----4-----5-----6----
|-----|-----|-----|-----|-----|-----|---- <-- Time "chunks"
0-----------1-----------------1-----0---- <-- Task being executed

<----2(0)---|-------4(1)------|--1(0)|--->

Function 0 starts at the beginning of time 0, then it executes 2 units of time and reaches the end of time 1.
Now function 1 starts at the beginning of time 2, executes 4 units of time and ends at time 5.
Function 0 is running again at the beginning of time 6, and also ends at the end of time 6, thus executing for 1 unit of time. 
So function 0 spends 2 + 1 = 3 units of total time executing, and function 1 spends 4 units of total time executing.
*/
-------------------------------------------------
/* ALGORITHM: Stack
The idea is simple everytime we see a start, we just push it to the stack. Now when we reach an end, we are guaranteed that the top of the stack is a start with the same id as the current item because all completed start/ends in between this start and end has been removed already. We just add current item timestamp - stack top timestamp + 1 to times[i].

So for example
[..., {0:start:3}] and item = {0:end:6} we add 6 - 3 + 1

However, what if there are function calls in between the start and end of the function of id 0? We can account for this by subtracting the length of the function calls in between the function id 0 whenever we complete an inner function marked by an end.

[..., {0:start:3}, {2:start:4}] and item = {2:end:5} so we increment times[2] by curr_length = 5 - 4 + 1 = 2 and then we subtract times[0] by curr_length as it takes up that amount of time out of the total time

So whenever we see an end, we have to make sure to subtract our curr_length to whatever function is enclosing it if it exists.
*/

class Solution {
    public int[] exclusiveTime(int n, List<String> logs) {
    // separate time to several intervals, add interval to their function
    int[] result = new int[n]; //Final Result
    Stack<Integer> st = new Stack<>(); // store function_id, not timestamp from the function
    // pre means the start timestamp of the interval
    int pre = 0;
    
    for(String log: logs) {
        String[] arr = log.split(":");
        int id = Integer.parseInt(arr[0]);
        String point = arr[1];
        int time = Integer.parseInt(arr[2]);
        if(point.equals("start")) {
            /* If the stack is not empty, we need to increment the sum 
            of the previous function from previous start time to now */
            if(!st.isEmpty()){
                // 'time' is the start of next interval, doesn't belong to current interval.
                result[st.peek()] += time - pre; 
            }
            st.push(id); // Push this function onto the stack
            pre = time; // Set the previous start time to the time now
        } else {
            // Get the last function to start from the top of the stack 
            // 'time' is end of current interval, belong to current interval. That's why we have +1 here
            result[st.pop()] += time - pre + 1;  
            // Set the previous start time to be one after this function ended
            // pre means the start of next interval, so we need to +1
            pre = time + 1;
            
        }
    }
    return result;
}
}
