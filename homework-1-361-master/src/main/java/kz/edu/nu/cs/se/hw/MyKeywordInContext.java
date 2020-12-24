package kz.edu.nu.cs.se.hw;

import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class MyKeywordInContext implements KeywordInContext {
	
	private String name;
	private String pathstring;
	
	private List<String> data;

    public MyKeywordInContext(String name, String pathstring) {
        
    	this.name = name;
    	this.pathstring = pathstring;

    	this.data = new ArrayList<>();

    }

    @Override
    public int find(String word) {

        int result = -1;

        for(int i = 0; i < data.size(); i ++){

            if(word.equalsIgnoreCase(data.get(i))){
                result = i + 1;
                break;
            }

        }

        return result;

    }

    @Override
    public Indexable get(int i) {

        int lineNumber = 1;
        String entry = "";

        int check = 0;
        int frequency = 0;

        List<String> list = new ArrayList<String>(data.subList(0, i - 1));
        String name = data.get(i - 1);
        frequency = Collections.frequency(list, name);

        try {

            BufferedReader reader = new BufferedReader(new FileReader(pathstring));
            String line = reader.readLine();

            while(line != null){

                String[] words = line.split(" ");

                for (String word: words){

                    word = word.replaceAll("[^A-Za-z0-9]", "");
                    if (word.equalsIgnoreCase(name)){

                        if(frequency != 0) {

                            frequency --;
                            continue;

                        }
                        check = 1;

                    }

                }
                if(check == 1){
                    break;
                }

                line = reader.readLine();

                lineNumber++;

            }

            entry = line;

            reader.close();

        }

        catch (Exception e) {
            e.printStackTrace();
        }

        Indexable indexable = new MyIndexable(entry, lineNumber);

        return indexable;

    }

    @Override
    public void txt2html() {

    	try {

            int lineNumber = 1;

            BufferedReader reader = new BufferedReader(new FileReader(pathstring));
			BufferedWriter writer = new BufferedWriter(new FileWriter(name + ".html"));

            writer.write("<html><head><meta charset=\"UTF-8\"></head><body>\n" + "<div>\n");
            String line = reader.readLine();

            while(line != null){

                writer.write(line + "<span id=\"line_" + lineNumber + "\">&nbsp&nbsp[" + lineNumber + "]</span><br>\n");

                line = reader.readLine();

                lineNumber ++;

            }

            writer.write("</div></body></html>");

            writer.close();
            reader.close();

        } 
    	catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void indexLines() {

        try {

            BufferedReader reader = new BufferedReader(new FileReader(pathstring));
            String line = reader.readLine();

            while(line != null){

                String[] words = line.split(" ");

                for (String word: words){

                    word = word.replaceAll("[^A-Za-z0-9]", "");

                    if (!data.contains(word) && word.length() > 3){
                        data.add(word);
                    }

                }

                Collections.sort(data, String.CASE_INSENSITIVE_ORDER);
                line = reader.readLine();

            }

            reader.close();

        }
        catch (Exception e){
            e.getStackTrace();
        }

        //for checking purpose:
        writeIndexToFile();

    }

    @Override
    public void writeIndexToFile() {

        try{

            int check = 0;
            int frequency = 0;

            String write = "";
            String usedWord = "";
            String usedLine = "";

            BufferedWriter writer = new BufferedWriter(new FileWriter("kwic-" + name + ".html"));

            writer.write("<html><head><meta charset=\"UTF-8\"></head><body><div style=\"text-align:center;line-height:1.6\">\n");

            for(int i = 0; i < data.size(); i ++){

                String index = data.get(i);
                Indexable indexable = get(i + 1);

                int lineNumber = indexable.getLineNumber();
                String entry = indexable.getEntry();
                String[] words = entry.split(" ");

                //for avoiding repetitions:
                if(!(usedWord.equalsIgnoreCase(index)) && !(usedLine.equals(entry))) {
                    check = 0;
                }
                else{
                    check ++;
                }

                for(String word: words){

                    word = word.replaceAll("[^A-Za-z0-9]", "");

                    if(frequency == check && word.equalsIgnoreCase(index)){

                        frequency ++;

                        write = write + ("<a href=\"" + name + ".html" + "#line_" + lineNumber + "\"> " + word.toUpperCase() + "</a> ");

                    }

                    else {

                        write = write + " " + word;

                        if(word.equalsIgnoreCase(index)) {
                            frequency ++;
                        }

                    }

                }

                writer.write(write);
                writer.write("<br>\n");

                write = "";
                usedLine = entry;
                usedWord = index;

                frequency = 0;

            }

            writer.write("</div></body></html>");

            writer.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

}
