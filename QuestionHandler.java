

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class QuestionHandler {
    private File file;
    private Scanner scanner;
    private ArrayList<QATuple> tuples;
    private double threshold = 0.3;
    private double penalty = 0.5;
    private String keyboardLayoutPath;
    /**
     * Creates a question handler.
     * The specified path is the path to the question-answer database.
     * <pre>{@code
     *  QuestionHandler handler = new QuestionHandler("../myDataBasePath");
     * }</pre>
     * @param filePath A path to the question-answer database text file.
     */
    public QuestionHandler(String filePath, String keyboardLayoutPath) {
        this.file = new File(filePath);
        this.tuples = new ArrayList<QATuple>();
        this.keyboardLayoutPath = keyboardLayoutPath;
        try {
            System.out.println("Reading data file...");
            this.scanner = new Scanner(this.file);
            ArrayList<String> lines = new ArrayList<String>();
            while(this.scanner.hasNextLine()) {
                String line = this.scanner.nextLine();
                lines.add(line);
            }
            for (String line : lines) {
                QATuple tuple = new QATuple(line.split("\\|")[0], line.split("\\|")[1]);
                this.tuples.add(tuple);
            }
            scanner.close();
            System.out.println("Data successfully loaded");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Answers a question asked by the user.
     * threshold's value by default is set to 0.3
     * <pre>
     * * {@code
     * * QuestionHandler handler = new QuestionHandler("MyPath");
     * * handler.answer("What's the distance between DSAI and Maastricht's train station?", 0.6)
     * // Will output the question with the highest similarity evaluation or
     * will respond by a pre defined string in case of low values.
     * }
     * * </pre>
     * @param question a question given by the user.
     * @param threshold A value for which method outputs a predefined string meaning that no questions in the database had a sufficient similarity ratio.
     * @return An answer to the most similar question in the database and  if < threshold returns a predefined string.
     */
    public String answer(String question, double threshold, double penalty) {
        QATuple bestEvaluation = this.tuples.get(0);
        bestEvaluation.setEvaluation(evaluate(question, bestEvaluation.getQuestion()));
        this.penalty = penalty;
        for (QATuple tuple : this.tuples) {
            tuple.setEvaluation(evaluate(question, tuple.getQuestion()));
            System.out.println("Evaluation:");
            System.out.println("Question: " + tuple.getQuestion() + " evaluation: " + tuple.getEvaluation());
            if(tuple.getEvaluation() > bestEvaluation.getEvaluation()) {
                bestEvaluation = tuple;
            }
        }
        if(bestEvaluation.getEvaluation() < threshold){
            return failureRandomAnswer();
        }else{
            return bestEvaluation.getAnswer();
        }
    }

    /**
     * Answers a question asked by the user.
     * threshold value by default is set at 0.3
     * <pre>
     * * {@code
     * * QuestionHandler handler = new QuestionHandler("MyPath");
     * * handler.answer("What's the distance between DSAI and Maastricht's train station?")
     * // Will output the question with the highest similarity evaluation or
     * will respond by a pre defined string in case of low values.
     * }
     * * </pre>
     * @param question a question given by the user.
     * @return An answer to the most similar question in the database and  if < treshold returns a predefined string.
     */
    public String answer(String question) {
        QATuple bestEvaluation = this.tuples.get(0);
        bestEvaluation.setEvaluation(evaluate(question, bestEvaluation.getQuestion()));
        for(QATuple tuple : this.tuples) {
            tuple.setEvaluation(evaluate(question, tuple.getQuestion()));
            if(tuple.getEvaluation() > bestEvaluation.getEvaluation()){
                bestEvaluation = tuple;
            }
        }
        if(bestEvaluation.getEvaluation() < threshold){
            return failureRandomAnswer();
        }else{
            return bestEvaluation.getAnswer();
        }
    }

    private double probabilityOfMatchGivenWords(String userQuery, String databaseQuery) {
        // We ignore the last character as it might trouble to the algorithm: (most of the time it is "?")
        //eg Is Pietro a teacher?
        //   Is Pietro a teacher
        // will be seen as different.
        String[] userQueryArr = userQuery.substring(0, userQuery.length()-1).split(" ");
        String[] databaseQueryArr = databaseQuery.substring(0, databaseQuery.length()-1).split(" ");
        int count = 0;
        for(int i = 0; i < userQueryArr.length; i++) {
            for(int j = 0; j < databaseQueryArr.length; j++) {
                if(databaseQueryArr[j].equals(userQueryArr[i])){
                    count++;
                }
            }
        }
        return count*1.0/(databaseQueryArr.length - 1);
    }


    private double probabilityOfMatchGivenIndices(String userQuery, String databaseQuery) {
        double missing = 0;
        KeyboardParser keyboardParser = new KeyboardParser(this.keyboardLayoutPath);

        for (int i = 0; i < databaseQuery.length(); i++) {
            if(!(i > userQuery.length()-1)){
                if(userQuery.charAt(i) != databaseQuery.charAt(i)) {
                    // If the missmatch is a neighbour of the character at that index in the database's question
                    // we relax our penalty by adding 0.5 (or another value) rather than 1.
                    //We find it's neighbour
                    if(keyboardParser.getKeyboard().isNeighbour(userQuery.charAt(i) + "", databaseQuery.charAt(i) + "")){
                        missing += this.penalty;
                    }else{
                        missing++;
                    }
                }
            }
            missing++;
        }
        return 1 - (missing/databaseQuery.length()*1.0);
    }

    private double evaluate (String userQuery, String databaseQuery) {
        return (probabilityOfMatchGivenIndices(userQuery, databaseQuery) + probabilityOfMatchGivenWords(userQuery, databaseQuery) + probabilityOfMatchGivenLevenshteinDistance(userQuery, databaseQuery))/3.0;
    }
    private void test(){
        for (QATuple tuple: this.tuples) {
            System.out.println(tuple.getAnswer());
        }
    }

    private String failureRandomAnswer() {
        double rand = Math.random();
        if(rand < 0.5){
            return "Sorry I did not quite catch what you said.";
        }else {
            return "Sorry I do not understand. Could you repeat please?";
        }
    }

    private double probabilityOfMatchGivenLevenshteinDistance(String userQuery, String databaseQuery) {
        int[][] dp = new int[userQuery.length() + 1][databaseQuery.length() + 1];
            for (int i = 0; i <= userQuery.length(); i++) {
                for (int j = 0; j <= databaseQuery.length(); j++) {
                    if (i == 0) {
                        dp[i][j] = j;
                    }
                    else if (j == 0) {
                        dp[i][j] = i;
                    }
                    else {
                        dp[i][j] = min(dp[i - 1][j - 1]
                                        + costOfSubstitution(userQuery.charAt(i - 1), databaseQuery.charAt(j - 1)),
                                dp[i - 1][j] + 1,
                                dp[i][j - 1] + 1);
                    }
                }
            }
            if (userQuery.length() == databaseQuery.length()) {
                // we get a probability that both are the same so we transform it into 1 - the probability which yields
                // the probability that the complement is the case. The same hold for the else statement.
                return 1 - (dp[userQuery.length()][databaseQuery.length()] * 1.0/userQuery.length());
            } else {
                return 1 - dp[userQuery.length()][databaseQuery.length()]*1.0/ Integer.MAX_VALUE;
            }
    }
    private int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }
    private int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

}