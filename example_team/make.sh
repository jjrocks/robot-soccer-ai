javac -cp log4j-1.2.17.jar:atan-0.4.5-SNAPSHOT.jar *.java
jar cf com.github.robocup_atan.example_team *.class
java Simple1Run
java com.github.robocup_atan.example_team.Simple1Run
java -jar com.github.robocup_atan.example_team.jar com.github.robocup_atan.example_team
