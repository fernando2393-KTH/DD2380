rm pipe; rm *.class; mkfifo pipe; clear;
javac *.java; java Main init verbose < pipe | java Main > pipe; rm *.class;
rm -rf kattis;
# mkdir kattis;
# cp Algorithms.java kattis/Algorithms.java
# cp Pair.java kattis/Pair.java
# cp Player.java kattis/Player.java
# cp SortableGameState.java kattis/SortableGameState.java