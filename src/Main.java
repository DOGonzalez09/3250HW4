package src;/*
David Gonzalez
Homework 4
This program creates news Feed that will pass the feed into social media platforms who will alert the media's users who are subscribed
*/
import java.util.*;
import java.io.*;

/**********************************************************/
/* Feed                                                   */
/**********************************************************/

class Feed{
    private String _title;
    private String _desc;
    public Feed(){}
    public Feed(String a, String b){
        _title = a; _desc = b;
    }
    public void setTitle(String a){_title = a;}
    public void setDesc(String a){_desc = a;}
    public String getTitle(){return _title;}
    public String getDesc(){return _desc;}
    public String toString(){return _title + " " + _desc;}
}

/**********************************************************/
/* NewsFeed		                		                  */
/**********************************************************/

class NewsFeed {
    private ArrayList<Feed> _newsFeed;
    public NewsFeed(){_newsFeed = new ArrayList<Feed>();}
    public NewsFeed(String fileName) throws IOException {
        Scanner scan = new Scanner(new File(fileName));
        _newsFeed = populateFeed(scan);
    }

    public Feed getRandomFeed(){
        int i = (int) (Math.random()*(_newsFeed.size()));
        return _newsFeed.get(i);
    }

    private ArrayList<Feed> populateFeed(Scanner scan){
        ArrayList<Feed> tempAry = new ArrayList<>();

        String[] str;
        while(scan.hasNextLine()){
            str = scan.nextLine().split(";");
            tempAry.add(new Feed (str[0],str[1]));
        }

        return tempAry;
    }
}
/**********************************************************/
/* Strategy Pattern Interface/Classes                     */
/**********************************************************/

// Provided: Strategy Interface
/**
 * Using double in the interface is a good idea because it leaves the possibility to expand
 * and create more strategy classes that could involve returning doubles while not affecting
 * the ability to return ints.
 */
interface AnalysisBehavior {
    double analyze(String[] words, String searchWord);
}

/**
 * I was thinking of making an int variable named found with value -1 if not found
 * and 1 if found but saw that I could just as well return the 1 and -1 without the variable
 * that saves me from having to loop though the whole array by just returning as well as not needing
 * the variable.
 */
class CountIfAnalysis implements AnalysisBehavior {
    public double analyze(String[] words, String searchWord) {
        // returns 1 if true & returns 0 if not found
        for(String word : words){
            if(word.equals(searchWord)){
                return 1;
            }
        }
        return 0;
    }
}

/**
 * I thought of just doing an if inside a for loop with a counter that will
 * return at the end
 */
class CountAllAnalysis implements AnalysisBehavior {
    public double analyze(String[] words, String searchWord) {
        int count =0;
        for(String word : words){
            if(word.equals(searchWord)){
                count++;
            }
        }
        return count;
    }
}

/**********************************************************/
/* Observer Pattern Interface/Classes                     */
/**********************************************************/

interface Subject {  // Notifying about state changes
    void subscribe(Observer obs);
    void unsubscribe(Observer obs);
    void notifyObservers(Feed f);
}

interface Observer {  // Waiting for notification of state changes
    void update(Feed f, String platformName);
}


abstract class SocialMediaPlatform implements Subject {
    private String _name;
    private ArrayList<Feed> _feed;
    private ArrayList<Observer> _observers;
    private int _updateRate;

    public SocialMediaPlatform(String n, int x){
        _name = n;
        _feed = new ArrayList<Feed>();
        _observers = new ArrayList<Observer>();
        _updateRate = x;
    }
    public void addFeed(Feed f){_feed.add(f);}
    public Feed getFeed(int i){return _feed.get(i);}
    public int getRate(){return _updateRate;}
    public String getName(){return _name;}
    public int size(){return _feed.size();}
    public void subscribe(Observer obs){_observers.add(obs);}
    public void unsubscribe(Observer obs){_observers.remove(obs);}
    public void notifyObservers(Feed f){
        for (Observer observer : _observers)
            observer.update(f, _name);
    }
    public void generateFeed(NewsFeed nf){
        int rand = (int) (Math.random()*100);
        if(rand < _updateRate){
            Feed feed = nf.getRandomFeed();
            _feed.add(feed);
            notifyObservers(feed);
        }
    }

    public double analyzeFeed(String w, AnalysisBehavior ab){
        double count = 0;
        String[] words;
        if(_feed.size() != 0){
            for(Feed f: _feed){
                String str = f.getTitle() + f.getDesc();
                words = createStringAry(str);
                count += ab.analyze(words,w);
            }
        }

        return count;
    }


    public String[] createStringAry(String str){
        int begin=0;
        ArrayList<String> s = new ArrayList<>();

        for(int i=0; i<str.length(); i++){
            if(str.charAt(i) == ' ' || str.charAt(i) == ',' || str.charAt(i) == '.'){
                if(begin != i){
                    s.add(str.substring(begin,i));
                }
                begin =i+1;
            }
        }
        return s.toArray(new String[0]);
    }



    public String toString(){
        String s = "";
        for (Feed f: _feed)
            s = s + f.getTitle() + ", " + f.getDesc() + "\n";
        return s;
    }
}

// Concrete Social Media Platforms
class Instagram extends SocialMediaPlatform {
    public Instagram() {
        super("Instagram", 30);  // 30% update rate
    }
}

/**
 * I added TikTok because it's the newest social media with young people
 * and I would say is the current biggest platform
 *
 * and gave it an 70% update rate because it's a big platform
 */
class TikTok extends SocialMediaPlatform {
    public TikTok() {
        super("TikTok", 70);
    }
}

/**
 * I added BeReal because it's a different social media platform which is now dead
 * it was sold off in 2024, and it's distinguishing feature was that you had to make a post
 * to see the posts and a post was an outwards facing pic as well as a selfie. that way you
 * show your self and what your doing.
 *
 * and gave it an 5% update rate because it's a dead platform now
 */
class BeReal extends SocialMediaPlatform {
    public BeReal() {
        super("BeReal", 5);
    }
}

class User implements Observer{
    private String _name;
    private ArrayList<SocialMediaPlatform> _myfeeds;
    public User(){_myfeeds = new ArrayList<SocialMediaPlatform>();}
    public User(String s){
        _name = s;
        _myfeeds = new ArrayList<SocialMediaPlatform>();
    }
    public void addPlatform(SocialMediaPlatform smp){_myfeeds.add(smp);}
    public void update(Feed f, String s){
        for (int i=0; i<_myfeeds.size(); i++){
            SocialMediaPlatform smp = _myfeeds.get(i);
            if (smp.getName().equals(s))
                _myfeeds.get(i).addFeed(f);
        }
    }
    public String toString(){
        String s = "";
        for (SocialMediaPlatform smp : _myfeeds) {
            for (int i=0; i<smp.size(); i++){
                Feed f = smp.getFeed(i);
                s = s + f.getTitle() + " " + f.getDesc() + "\n";
            }
        }
        return s;
    }
}

/**********************************************************/
/* Factory Pattern Interface/Classes                      */
/**********************************************************/

// Factory Creator Interface
interface SMPFactory {
    SocialMediaPlatform createPlatform();
}

// Concrete Factory classes for each platform
class InstagramFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new Instagram();
    }
}
class TikTokFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new TikTok();
    }
}
class BeRealFactory implements SMPFactory {
    public SocialMediaPlatform createPlatform() {
        return new BeReal();
    }
}


public class Main {
    public static void main(String[] args) throws IOException {
        // Create main newsfeed from file
        NewsFeed nf = new NewsFeed("data.txt");

        // Create SMP factories
        SMPFactory instagramFactory = new InstagramFactory();
        SMPFactory tiktokFactory = new TikTokFactory();
        SMPFactory beRealFactory = new BeRealFactory();

        // Create the platforms container and add SMPs
        ArrayList<SocialMediaPlatform> platforms = new ArrayList<>();
        platforms.add(instagramFactory.createPlatform());
        platforms.add(tiktokFactory.createPlatform());
        platforms.add(beRealFactory.createPlatform());

        // Create Users and subscribe
        User user1 = new User("Alice"); //Instagram
        User user2 = new User("Michal"); //TikTok
        User user3 = new User("Tomas"); //beReal
        User user4 = new User("Adam"); //Instagram TikTok beReal

        user1.addPlatform(instagramFactory.createPlatform());
        user2.addPlatform(tiktokFactory.createPlatform());
        user3.addPlatform(beRealFactory.createPlatform());

        user4.addPlatform(instagramFactory.createPlatform());
        user4.addPlatform(tiktokFactory.createPlatform());
        user4.addPlatform(beRealFactory.createPlatform());

        for(SocialMediaPlatform smp:platforms){
            smp.subscribe(user1);
            smp.subscribe(user2);
            smp.subscribe(user3);
            smp.subscribe(user4);
        }



        // Run a simulation to generate random feeds for the SMPs
        int runTime = 20;
        for(int i=0; i<runTime; i++){
            for (SocialMediaPlatform platform : platforms) {
                platform.generateFeed(nf);
            }
        }



        // Perform analysis
        /**
         * Analyzing the Feeds in every platform with a CountAll & CountIf
         * with a word that is not in and one that is.
         */
        for (SocialMediaPlatform smp : platforms) {
            System.out.println(smp.getName() + " Count All: " +
                    smp.analyzeFeed("guess", new CountAllAnalysis()));
            System.out.println(smp.getName() + " Count If: " +
                    smp.analyzeFeed("guess", new CountIfAnalysis()));
            System.out.println(smp.getName() + " Count All: " +
                    smp.analyzeFeed("in", new CountAllAnalysis()));
            System.out.println(smp.getName() + " Count If: " +
                    smp.analyzeFeed("in", new CountIfAnalysis())+ "\n");
        }


        // Print Users' Contents
        System.out.println("User 1 : \n" + user1);
        System.out.println("User 2 : \n" + user2);
        System.out.println("User 3 : \n" + user3);
        System.out.println("User 4 : \n" + user4);

    }

} 
