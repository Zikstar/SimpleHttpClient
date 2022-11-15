import java.util.ArrayList;
import java.util.List;

public class httpc {
    public static void main(String[] args) {
        if(args.length == 1 && args[0].equals("help")){
            displayHelpInfo();
        } else if(args.length == 2 && args[0].equals("help") && args[1].equals("get")){
            displayGetHelpInfo();
        }else if(args.length == 2 && args[0].equals("help") && args[1].equals("post")){
            displayPostHelpInfo();
        }else if(args.length > 1 && args[0].equals("get")){
            List<String> argsL = List.of(args);
            getParser(argsL.subList(1, argsL.size()));
        }else if(args.length > 1 && args[0].equals("post")){
            List<String> argsL = List.of(args);
            postParser(argsL.subList(1, argsL.size()));
        }
    }


    private static void postParser(List<String> argL) {
        ArrayList<String> args = new ArrayList<>(argL);
        String file = null;
        String postBody = null;
        String[] headers = null;
        boolean isVerbose = false;
        boolean redirectionAllowed = false;
        String outputFile = null;

        if(args.contains("-d") && args.contains("-f")){
            System.out.println("You can use either [-d] or [-f] but not both");
        }else {
            if(args.contains("-v")){
                args.remove("-v");
                isVerbose = true;
            }
            if(args.contains("-h")){
                int index = args.indexOf("-h");
                args.remove("-h");
                String headerLine = args.get(index);
                args.remove(headerLine);
                headers = headerLine.split(",");
            }
            if(args.contains("-r")){
                args.remove("-r");
                redirectionAllowed = true;
            }
            if(args.contains("-d")){
                int index = args.indexOf("-d");
                args.remove("-d");
                postBody = args.get(index);
                args.remove(postBody);
            }
            if(args.contains("-f")){
                int index = args.indexOf("-f");
                args.remove("-f");
                file = args.get(index);
                args.remove(file);
            }
            if(args.contains("-o")){
                int index = args.indexOf("-o");
                args.remove("-o");
                outputFile = args.get(index);
                args.remove(outputFile);
            }

            if(args.size() >= 1){
                HttpClient client = new HttpClient(args.get(0), headers, isVerbose,
                        postBody, file, outputFile, redirectionAllowed);
                client.post();
            }else {
                System.out.println("Something went wrong, with input. Please check and try again");
            }
        }

    }


    private static void getParser(List<String> argL) {
        ArrayList<String> args = new ArrayList<>(argL);
        String[] headers = null;
        boolean verbose = false;
        boolean isRedirect = false;
        String outputFile = null;

        if(args.contains("-v")){
            args.remove("-v");
            verbose = true;
        }
        if(args.contains("-h")){
            int index = args.indexOf("-h");
            args.remove("-h");
            String headerLine = args.get(index);
            args.remove(headerLine);
            headers = headerLine.split(",");
        }
        if(args.contains("-r")){
            args.remove("-r");
            isRedirect = true;
        }
        if(args.contains("-o")){
            int index = args.indexOf("-o");
            args.remove("-o");
            outputFile = args.get(index);
            args.remove(outputFile);
        }

        if(args.size() >= 1){
            HttpClient client = new HttpClient(args.get(0), headers, verbose,
                    null, null, outputFile, isRedirect);
            client.get();
        }else {
            System.out.println("Something went wrong, with input. Please check and try again");
        }
    }

    private static void displayHelpInfo() {
        String helpText = "httpc is a curl-like application but supports HTTP protocol only.\n" +
                "    \n" +
                "    Usage:\n" +
                "        httpc command [arguments]\n" +
                "    \n" +
                "    The commands are:\n" +
                "        get     executes a HTTP GET request and prints the response.\n" +
                "        post    executes a HTTP POST request and prints the response.\n" +
                "        help    prints this screen.\n" +
                "    \n" +
                "    Use \\\"httpc help [command]\\\" for more information about a command.";

        System.out.println(helpText);
    }

    private static void displayGetHelpInfo() {
        String getInfo = "Usage:\n" +
                "        httpc get [-v] [-h key:value] URL\n" +
                "    \n" +
                "    Get executes a HTTP GET request for a given URL.\n" +
                "    \n" +
                "        -v              Prints the detail of the response such as protocol, status and headers.\n" +
                "        -h key:value    Associates headers to HTTP Request with the format 'key:value'.";

        System.out.println(getInfo);
    }

    private static void displayPostHelpInfo() {
        String postInfo = "Usage:\n" +
                "        httpc post [-v] [-h key:value] [-d inline-data] [-f file] URL\n" +
                "    \n" +
                "    Post executes a HTTP POST request for a given URL with inline data or from file.\n" +
                "    \n" +
                "        -v              Prints the detail of the response such as protocol, status and headers.\n" +
                "        -h key:value    Associates headers to HTTP Request with the format 'key:value'.\n" +
                "        -d string       Associates an inline data to the body HTTP POST request.\n" +
                "        -f file         Associates the content of a file to the body HTTP POST request.\n" +
                "    \n" +
                "    Either [-d] or [-f] can be used but not both.";

        System.out.println(postInfo);
    }


}
