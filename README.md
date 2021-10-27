# chatbot-baseline
![chatbot baseline logo](miscellaneous/chatbot-baseline-logo.png)</br>
A baseline for chatbots that does not take into account context (+ with I/O capabilities).</br>
To initialise the question handler create an instance of QuestionHandler and pass in as arguments a .txt file containing the questions and the answers separated by a pipe symbol (|). Second pass in the keyboard layout you expect the user to be using. Note that it is qwerty by default. (For more about the format, just check the qwerty.txt file)</br>
```java
 QuestionHandler questionHandler = new QuestionHandler("QueryDatabase.txt", "qwerty.txt");
```
Then simply write 
```java
questionHandler.answer("Who is Pietro", 0.3, 0.5)
```
to check if the database presumably has an answer to this question. The second argument is the threshold at which the bot "gives up" and answers that it does not know whilst the third argument is the penalty applied for what appears to be a typo in the question. Thereby making it more robust to typos.</br>
```java
System.out.println("Output:" + questionHandler.answer("Who is Pietro", 0.3, 0.5));

System.out.println("Output:" + questionHandler.answer("What is Pietro", 1.0, 0.5));

System.out.println("Output:" + questionHandler.answer("What is the weather in Maastricht today", 0.3, 0.5));

System.out.println("Output:" + questionHandler.answer("dksjqhdjkqshdjqhdkjqsy", 0.3, 0.5));
```
The reader is strongly advised to use this as a baseline for benchmark testing and NOT as an actual chatbot.
Moreover some obvious improvements can be made. Some of which are:
- Making the chatbot's code more verstatile by allowing the user to use multiple fitness functions.
- There are some errors to fix in the probabilities (which technically does not impact the final result)
- One could take advantage of different data structures to make it more scalable.
- The typo detection could be made more robust by not just calculating its neighbours but also by computing the distance to each possible keycap. This would relax the probability of typo as one is still quite likely to press k rather than p in a typo.
