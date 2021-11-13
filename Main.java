import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class Main {

    private static String url = "http://dnd5e.wikidot.com/spells";

    public static void main(String[] args) throws MalformedURLException, IOException {  
       checkSpells(new URL(url));
    }

    public static void checkSpells(URL url) throws IOException{
        System.out.println(url);
        File file = new File("spells.txt");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
        boolean spellData = false;
        ArrayList<String> array = new ArrayList<>();
        while (reader.ready()){
            String line = reader.readLine();
            
            if(spellData || line.contains("yui-content")){
                String refinedLine = refineLine(line);
                if(array.size() < 6 && !refinedLine.isBlank()) array.add(refinedLine);
                else if(array.size() >= 6) {
                    String formatedLine = formatLine(array);
                    writer.write(formatedLine+"\n");
                    array.clear();
                }
                spellData = !line.equals("</div>");
            }
        }
        writer.close();
        reader.close();
    }

    public static String refineLine(String line){
        String tagless = removeTags(line);
        return tagless.isBlank() ? "" : tagless +" ";
    }

    private static String FORMAT = "%35s|%17s|%21s|%23s|%33s|%5s";
    public static String formatLine(ArrayList<String> array){
        if(array.get(0).contains("Spell Name")) array.set(0, String.format("%35s", array.get(0)).replace(' ', '-'));
        return String.format(FORMAT, array.toArray());
    }

    public static String removeTags(String input){
        String array[] = input.split("<");
        String output = "";
        for (String string : array) {
            if(string.isBlank()) continue;
            output += string.substring(string.indexOf(">")+1, string.length());
        }
        return output;
    }
}