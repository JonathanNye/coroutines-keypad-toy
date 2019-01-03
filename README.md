This a toy made using Kotlin+Android to demonstrate how you can use coroutines and suspending functions to encapsulate the state of a numeric pin pad. The underlying motivation was to show that coroutines aren't necessarily about parallelism or threading -- they can solve all kinds of problems.

- Typing a correct digit in the sequence displays an arbitrary message
- Typing an incorrect digit displays an error message and resets the progress
- The code should be obvious to fans of [Tommy Tutone](https://www.youtube.com/watch?v=6WTdTwcmxyo)

It works by suspending execution of the coroutine while waiting for the user to click the each correct digit -- but before doing so, every *other* digit is set to cancel and restart the coroutine. The suspending calls for all the digits are wrapped in a `while (true)` block, so successful entries can be repeated ad nauseum.

I thought it was cool that the correct button sequence is essentially modeled by program flow. In this case, we're doing a `forEach` over a collection of views that defines the code, so it's a simple linear path -- but other built-in control flow statements (or the coroutines `select` expression) can model more complex or branching paths.