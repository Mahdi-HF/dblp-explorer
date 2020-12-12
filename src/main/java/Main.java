import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.google.gson.*;

public class Main {
    private static String keyword;
    private Map<Long, Paper> paperMap;
    private List<Long> allPapers;
    private Map<Long, Long> currentPapers;

    private static class Paper {
        private List<String> references;
        private FormattedOutput output;

        public void setOutput( String jsonLine ) {
            Gson gson = new Gson();
            output = gson.fromJson(jsonLine, FormattedOutput.class);
        }

        public boolean containsKeyWord() {
            return output.getTitle().toLowerCase().contains(keyword.toLowerCase());
        }

        public Long getId() {
            return output.getId();
        }

        @Override
        public String toString() {
            return output.toString();
        }
    }

    private void createPaper( String line ) {
        Gson gson = new Gson();
        Paper paper = gson.fromJson(line, Paper.class );
        paper.setOutput( line );
        Long id = paper.getId();
        if( paper.containsKeyWord() ) {
            System.out.println( paper.toString() );
            currentPapers.put( id, id );
        }
        allPapers.add( id );
        paperMap.put( id, paper);
    }

    private void getPapers( List<String> lines ) throws IOException {
        lines.parallelStream().forEach(this::createPaper);
    }

    private boolean checkReferences( Long id ) {
        // check if the references of this paper contain an ID in the currentPapers list
        List<String> references = paperMap.get(id).references;
        if( references != null)
            return references.parallelStream().anyMatch( obj -> currentPapers.containsKey( Long.parseLong( obj.trim() ) ) );
        else return false;
    }

    private void getTiers() {
        List<Long> newPapers = allPapers.parallelStream().filter(this::checkReferences).sorted().collect(Collectors.toList());

        if(!newPapers.isEmpty())
            newPapers.forEach( id -> System.out.println( paperMap.get(id).toString() ) );
        else {
            System.out.println( "Empty tier list, terminating program");
            System.exit( 0 );
        }
        currentPapers  = newPapers.parallelStream().collect( Collectors.toConcurrentMap(p -> p, p -> p) );
    }

    Main() throws IOException {
        paperMap = new ConcurrentHashMap<Long, Paper>();
        allPapers = new ArrayList<Long>();
        currentPapers = new ConcurrentHashMap<Long, Long>();
        Scanner scanner = new Scanner(System.in);

        // input validation
        System.out.print("Enter the file path of the dblp text document: ");
        String filePath = scanner.nextLine();
        while( !new File(filePath).exists() || ( !filePath.endsWith(".txt") && !filePath.endsWith(".json") ) ) {
            System.out.println("Enter a valid file path: ");
            filePath = scanner.nextLine();
            // /Users/mahdi.hf/Documents/dblp_papers_v11.txt
        }

        System.out.print("Enter a keyword: ");
        keyword = scanner.nextLine();
        while( keyword.contains(" ") ) {
            System.out.println("Keyword must be one word, no spaces: ");
            keyword = scanner.nextLine();
            // Cryptanalysis
        }

        System.out.print("Enter a integer n (number of tiers): ");
        String input = scanner.nextLine();
        while( !input.matches("-?(0|[1-9]\\d*)") ) {
            System.out.print("Enter a valid integer: ");
            input = scanner.nextLine();
        }
        int tiers = Integer.parseInt(input);


        System.out.print("Enter a limit for parsing (-1 for complete): ");
        String pageLimitLine = scanner.nextLine();
        while( !pageLimitLine.matches("-?(0|[1-9]\\d*)") ) {
            System.out.print("Enter a valid integer: ");
            pageLimitLine = scanner.nextLine();
        }
        int pagelimit = Integer.parseInt(pageLimitLine);
        if(pagelimit == -1) pagelimit = Integer.MAX_VALUE;

        scanner.close();
        System.out.println("Papers containing the keyword: " + keyword + "\n");

        // process 5,000 lines at a time (anything over seems to run the same speed or slower)
        BufferedReader br = new BufferedReader( new FileReader(filePath) );
        String line;
        List<String> lines;
        boolean eof = false;
        int count = 0;
        while( !eof && pagelimit >= count ) {
            count += 5000;
//            System.out.println(count);
            lines = new ArrayList<String>();
            while( lines.size() < 5000 ) {
                if( (line = br.readLine()) == null) {
                    eof = true;
                    break;
                }
                else lines.add(line);
            }
            getPapers(lines);
        }
        br.close();

        if( currentPapers == null || currentPapers.isEmpty() ) {
            System.out.println("No papers with the keyword: " + keyword);
            return;
        }

        for(int i = 0; i < tiers; i++) {
            System.out.println("Tier " + (i + 1) + ":\n");
            getTiers();
        }
    }

    public static void main( String[] args ) throws IOException {
        Main main = new Main();
    }
}