package mutation.g6;

import java.util.*;
import mutation.sim.Console;
import mutation.sim.Mutagen;

public class Player extends mutation.sim.Player {
    private Random random;
    private int[] beforeCounter;
    private int[] afterCounter;
    private Map<Character, Integer> hash;
    private Map<Integer, Character> antiHash;
    
    public Player() {
        random = new Random();
        hash = new HashMap<>();
        hash.put('a', 0); hash.put('c', 1); hash.put('g', 2); hash.put('t', 3);
        antiHash = new HashMap<>();
        antiHash.put(0, 'a'); antiHash.put(1, 'c'); antiHash.put(2, 'g'); antiHash.put(3, 't');
    }

    private String randomString() {
        char[] pool = {'a', 'c', 'g', 't'};
        String result = "";
        for (int i = 0; i < 1000; ++ i)
            result += pool[Math.abs(random.nextInt() % 4)];
        return result;
    }

    @Override
    public Mutagen Play(Console console, int m) {
        Mutagen result = new Mutagen();
        //result.add("a;c;c", "att");
        //result.add("g;c;c", "gtt");
        for (int i = 0; i < 1; ++ i) {
            String genome = randomString();
            String mutated = console.Mutate(genome);
            char[] input = genome.toCharArray();
            char[] output = mutated.toCharArray();
            Element[] diff = checkDifference(input, output);
            result = getNaive(diff);
            console.Guess(result);
        }
        return result;
    }

    public Mutagen getNaive(Element[] diff) {
        Mutagen result = new Mutagen();
        List<Window> winList = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
        	if(diff[i].isMutated()) {
        		Window temp = new Window(i, i+9, diff);
        		winList.add(temp);
        		i+=10;
        	}
        }

        Window temp = winList.get(0);
        Set<String> left = new HashSet<>();
        int length = getLength(winList.get(0));
        
        String output = getWinInt(winList);
		System.out.println("leftis1: " + output);

		for(Window w: winList) {
            String t = getLeft(w, output);
            System.out.println("left: " + t);
            left.add(t);
        }

        if(left.size() == 1) {
        	String out = "";
            for(String s: left) out = putSemi(s);
            result.add(out, output);
            return result;
        }
        
        else {
        	System.out.println("flag");
            //System.out.println("length " + length );
            for(int i = 0; i < length; i++) {
                Set<Character> c = new HashSet<>();
                for(String s: left) {
                    if(i < s.length())
                        c.add(s.charAt(i));
                }
                output += combine(c);
                //System.out.println("c: " + c);
                //System.out.println(output);
                if(i != length -1) output += ";";
            }
        }
        System.out.println("output: " + getWinInt(winList));
        result.add(output, getWinInt(winList));
        return result;
    }

    public String getLeft(Window w, String output) {
    	Element[] e = w.getWindow();
    	String out = "";
    	if(output == "") return "";
    	for(int i = 0; i < 19; i++) {
    		if(e[i].getAfter() == output.charAt(0)) {
    			out += Character.toString(e[i].getOG());
    			boolean sub = true;
    			for(int j = 1; j < output.length(); j++) {
    				if(i+j > 18) break;
    				if(e[j+i].getAfter() != output.charAt(j)) {
    					sub = false;
    					break;
    				}
    				else out += Character.toString(e[j+i].getOG());
    			}
    			if(sub) return out;
    			else out = "";
    		}
    	}
    	return out;
    }

    public String getWinInt(List<Window> list){
    	String output = "";
    	for(Window w: list) {
    		for(int i = 0; i < 19; i++) {
    			System.out.print(w.getWindow()[i].getAfter());
    		}
    		System.out.println();
    	}
    	for(int i = 0; i < 19; i++) {
    		char temp = list.get(0).getWindow()[i].getAfter();
    		boolean same = true;
    		for(Window w: list) {
    			if(w.getWindow()[i].getAfter() != temp) {
    				same = false;
    				break;
    			}
    		}
    		if(same) output += Character.toString(temp);
    	}
    	return output;
    }

    public String getMaxSubString(String a, String b) {
    	
    }

    public String combine(Set<Character> input) {
        String output = "";
        for(char c: input) {
            output += Character.toString(c);
        }
        return output;
    }

    public String putSemi(String s) {
        String output = "";
        for(int i = 0; i < s.length(); i++) {
            output = output + Character.toString(s.charAt(i));
            if(i != s.length()-1) output += ";";
        }
        return output;
    }

    public int getLength(Window w) {
        return w.mutEnd - w.mutStart + 1;
    }

    public Element[] checkDifference(char[] input, char[] output) {
        Element[] diff = new Element[1000];
        beforeCounter = new int[4];
        afterCounter = new int[4];
        for(int i = 0; i < input.length; i++) {
            if(input[i] != output[i]) {
                diff[i] = new Element(true, input[i], output[i]);
                beforeCounter[hash.get(input[i])]++;
                afterCounter[hash.get(output[i])]++;
            }
            else diff[i] = new Element(input[i]);
        }
        return diff;
    }

    public class Element {
        private boolean mutated;
        private char og;
        private char after;

        public Element() {
            mutated = false;
        }

        public Element(char og) {
            mutated = false;
            this.og = og;
            this.after = og;
        }

        public Element(boolean mutated, char og, char after) {
            this.mutated = mutated;
            this.og = og;
            this.after = after;
            if(!this.mutated) this.after = og;
        }

        public boolean isMutated() {
            return mutated;
        }

        public char getOG() {
            return og;
        }

        public char getAfter() {
            return after;
        }

        public void putOG(char og) {
            this.og = og;
        }

        public void putAfter(char after) {
            this.after = after;
        }
    }

    public class Window {
    	public int start;
    	public int end;
        public int mutStart;
        public int mutEnd;
    	public int mutagenCount;
    	public Element[] window;

    	public Window() {

    	}

    	public Window(int left, int right, Element[] input) {
    		start = left;
    		end = right;
            mutStart = -1;
            mutEnd = -1;
    		mutagenCount = 0;
    		window = new Element[19];
    		int index = 0;

    		for(int i = left-9+1000; i <= right+1000; i++) {
    			window[index++] = input[i%1000];
    			if(input[i%1000].isMutated()) {
                    if(mutStart == -1) mutStart = index-1;
    				mutagenCount++;
                    mutEnd = index-1;
    			}
    		}
    	}

    	public Element[] getWindow() {
    		return window;
    	}

    	public int getMutagenCount() {
    		return mutagenCount;
    	}

    	public boolean isSameLoc(Window temp) {
    		for(int i = 0; i < 10; i++) {
    			if(temp.window[i].isMutated() && this.window[i].isMutated()) {
    				continue;
    				
    			} else return false;
    		}
    		return true;
    	}

        public String getAfter(){
            String temp = "";
            //System.out.println("mutStart!: " + mutStart);
            //System.out.println("mutEnd!: " + mutEnd);
            for(int i = mutStart; i <= mutEnd; i++) {
                temp = temp.concat(Character.toString(window[i].getAfter()));
            }
            return temp;
        }

        public String getOG(){
            String temp = "";
            boolean first = true;
            for(int i = mutStart; i <= mutEnd; i++) {
                temp = temp.concat(Character.toString(window[i].getOG()));
            }
            return temp;
        }
    }


}


