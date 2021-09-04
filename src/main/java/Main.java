import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        makeRequest("ducks");
    }
    public static void makeRequest(String text) {
        ActorSystem system = ActorSystem.create("MySystem");
        ActorRef master = system.actorOf(Props.create(MasterActor.class, new StubServer()), "master");
        Object responces = PatternsCS.ask(master, text, new Timeout((FiniteDuration) Duration.create("10 second"))).toCompletableFuture().join();
        System.out.println(responces);
        try {
            if (responces instanceof ArrayList) {
                for (Object responce : (ArrayList) responces) {
                    if (responce instanceof Responce) {
                        System.out.println(((Responce) responce).getSearcher());
                        System.out.println(((Responce) responce).getGson());
                    } else throw new ActorResponceException("");
                }
            } else throw new ActorResponceException("");
        }
        catch (ActorResponceException e){
            e.printStackTrace();
        }
        finally {
            system.terminate();
        }
    }
    public static class ActorResponceException extends Exception{
        public ActorResponceException(String message){
            super("Responce from actor is of wrong type " + message);
        }
    }
}
