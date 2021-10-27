# chatbot-baseline
![chatbot baseline logo](miscellaneous/chatbot-baseline-logo.png)</br>
A baseline for chatbots that does not take into account context (+ with I/O capabilities).</br>
To initialise the question handler create an instance of QuestionHandler and pass in as arguments a .txt file containing the question and the answer separated by a pipe symbol (|). Second pass in the keyboard layout you expect the user to be using. Note that it is qwerty by default.</br>
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
