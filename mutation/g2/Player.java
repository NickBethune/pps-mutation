/*

right now: random string + mutations, records how many times it has seen something, guesses.
only runs 1/2 experiments. really naive.

TODO:
- Implement Wrap Around
- dont guess the same thing twice
- implement context checking
( a better way to do this might be to record changes as before -> after and
keep a count of how many times we've seen a change. Everytime we see a change
record the possible contexts(sliding window) towards the end pick out common elements
in the context )

goals:
Make smarter test cases
Handle more complicated mutations
Possibly model after the scientific method ( can we model that as a search problem)
*/

package mutation.g2;

import java.util.*;
import mutation.sim.Console;
import mutation.sim.Mutagen;

public class Player extends mutation.sim.Player {
    private Random random;

    public Player() {
      Utilities.alert("Hello world");
      random = new Random();
    }

    private String randomString() {
        char[] pool = {'a', 'c', 'g', 't'};
        String result = "";
        for (int i = 0; i < 1000; ++ i)
            result += pool[Math.abs(random.nextInt() % 4)];
        return result;
    }

    public Mutagen Play(Console console, int m){
      Mutagen result = new Mutagen();
      HashMap<String, Integer> evidence = new HashMap<>();
      List<String> visited = new ArrayList<>();

      for (int i = 0; i < 10; ++ i){
        // run a random experiment
        String genome = randomString();
        String mutated = console.Mutate(genome);
        List<Change> changes = Utilities.diff(genome, mutated);

        // collect evidence
        for(Change c: changes){
          String key = c.getChange();
          if(!evidence.containsKey(key)){
            evidence.put(key, 0);
          }
          evidence.put(key, evidence.get(key)+1);
        }

        //guess strongest evidence
        String maxString = Utilities.argMax(evidence);
        if(visited.contains(maxString)){
          continue;
        }

        if(maxString != null && evidence.get(maxString) > 1){
          Change maxEvidence = Change.fromChangeString(maxString);
          result.add(Utilities.formatPattern(maxEvidence.before),maxEvidence.after);
          boolean guess = console.Guess(result);
          if(guess){
            Utilities.alert("Correctly guessed");
            break;
          }
        }
      }

      Utilities.alert(evidence);
      return result;
    }

}
