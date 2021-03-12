package bil481;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import org.slf4j.LoggerFactory;
import java.util.logging.*; 


public class App
{
    public String getGreeting() {
        return "Hello world.";
    }

    public static boolean search(ArrayList<Integer> array, int e) {
      System.out.println("inside search");
      if (array == null) return false;

      for (int elt : array) {
        if (elt == e) return true;
      }
      return false;
    }

    public static String analyzeList(ArrayList<Integer> list, int first, String oddEven, String primeOrNot) {
        String result = "";
        int max = -9999;
        String primeList = "";
        String notPrimeList = "";
        String maxOdd = "";
        String maxEven = "";

        if (oddEven.equals("even")) {
            for(int i : list) {
                if(i % 2 == 0) {
                    if(i > max)
                    max = i;  
                }  
            }
            maxEven = "En büyük çift sayı: " + max + " \n";
        }
        else if(oddEven.equals("odd")) {
            for(int i : list) {
                if(i % 2 != 0){
                    if(i > max)
                        max = i;
                }
            }
            maxOdd = "En büyük tek sayı:" + max + "\n";
        }

        if(primeOrNot.equals("prime")) {
            primeList = "Asal olan sayılar: ";
            for(int i: list) {
                if(isPrime(i)) {
                    primeList += i + ",";
                }
            }
            primeList += "\n";
        }

        else if(primeOrNot.equals("notPrime")) {
            notPrimeList = "Asal olmayan sayılar: ";
            for(int i : list) {
                if(!isPrime(i))
                    notPrimeList += i + ",";
            }
            notPrimeList += "\n";
        }

        boolean contain = search(list, first);
        if(contain)
            result = "Listede " + first + " sayısı mevcut\n";
        else
            result = "Listede " + first + " sayısı mevcut değil\n";  
        
        String finalStr = result + maxEven + maxOdd + primeList + notPrimeList;
        return finalStr;
    }


    private static boolean isPrime(int num) {
        int factor = 0;
        for(int i = 1; i <= num; i++) {
            if(num % i == 0)
                factor++;
        }

        if(factor == 2)
            return true;
        else
            return false;
    }


    public static void main(String[] args) {
        port(getHerokuAssignedPort());

        Logger logger = Logger.getLogger(App.class.getName());

        int port = Integer.parseInt(System.getenv("PORT"));
        port(port);
        //logger.error("Current port number:" + port);


        get("/", (req, res) -> "Hello, World");

        post("/compute", (req, res) -> {
          //System.out.println(req.queryParams("input1"));
          //System.out.println(req.queryParams("input2"));

          String input1 = req.queryParams("input1");
          java.util.Scanner sc1 = new java.util.Scanner(input1);
          sc1.useDelimiter("[;\r\n]+");
          java.util.ArrayList<Integer> inputList = new java.util.ArrayList<>();
          while (sc1.hasNext())
          {
            int value = Integer.parseInt(sc1.next().replaceAll("\\s",""));
            inputList.add(value);
          }
          sc1.close();
          System.out.println(inputList);


          String input2 = req.queryParams("input2").replaceAll("\\s","");
          int input2AsInt = Integer.parseInt(input2);

          String input3 = req.queryParams("evenOrMax");
          String input4 = req.queryParams("primeOrNot");

          System.out.println(input3);
          System.out.println(input4);

          String result = App.analyzeList(inputList, input2AsInt, input3, input4);

          Map<String, String> map = new HashMap<String, String>();
          map.put("result", result);
          return new ModelAndView(map, "compute.mustache");
        }, new MustacheTemplateEngine());


        get("/compute",
            (rq, rs) -> {
              Map<String, String> map = new HashMap<String, String>();
              map.put("result", "not computed yet!");
              return new ModelAndView(map, "compute.mustache");
            },
            new MustacheTemplateEngine());
    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
