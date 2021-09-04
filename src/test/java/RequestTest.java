import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.google.gson.Gson;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

public class RequestTest {
    @Test
    public void timeOutForAll(){
        ActorSystem system = ActorSystem.create("MySystem");
        StubServer mockServer = mock(StubServer.class);
        doAnswer(invocation -> {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Gson();
        }).when(mockServer).makeRequest(any());

        ActorRef master = system.actorOf(Props.create(MasterActor.class, mockServer), "master");
        Object responces = PatternsCS.ask(master, "text", new Timeout((FiniteDuration) Duration.create("10 second"))).toCompletableFuture().join();
        assertTrue(responces instanceof ArrayList);
        assertEquals(0, ((ArrayList)responces).size());

        system.terminate();
    }

    @Test
    public void allReceived(){
        ActorSystem system = ActorSystem.create("MySystem");
        ActorRef master = system.actorOf(Props.create(MasterActor.class, new StubServer()), "master");
        Object responces = PatternsCS.ask(master, "text", new Timeout((FiniteDuration) Duration.create("3 second"))).toCompletableFuture().join();
        assertTrue(responces instanceof ArrayList);
        ArrayList<Responce> responceList = (ArrayList<Responce>) responces;
        assertEquals(3, responceList.size());
        Set<String> searchers = new HashSet<>();
        for (Responce responce : responceList){
            searchers.add(responce.getSearcher().name());
        }
        assertTrue(searchers.contains("bing"));
        assertTrue(searchers.contains("yandex"));
        assertTrue(searchers.contains("google"));
        system.terminate();
    }

    @Test
    public void timeOutForOne(){
        ActorSystem system = ActorSystem.create("MySystem");
        StubServer mockServer = mock(StubServer.class);
        doAnswer(invocation -> {
            Searcher searcher = ((Request) invocation.getArgument(0)).getSearcher();
            if (searcher.name().equals("google")) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return new Gson();
        }).when(mockServer).makeRequest(any());

        ActorRef master = system.actorOf(Props.create(MasterActor.class, mockServer), "master");
        Object responces = PatternsCS.ask(master, "text", new Timeout((FiniteDuration) Duration.create("10 second"))).toCompletableFuture().join();
        assertTrue(responces instanceof ArrayList);
        ArrayList<Responce> responceList = (ArrayList<Responce>) responces;
        assertEquals(2, responceList.size());
        Set<String> searchers = new HashSet<>();
        for (Responce responce : responceList){
            searchers.add(responce.getSearcher().name());
        }
        assertTrue(searchers.contains("bing"));
        assertTrue(searchers.contains("yandex"));
        system.terminate();
    }

    @Test
    public void timeOutForTwo(){
        ActorSystem system = ActorSystem.create("MySystem");
        StubServer mockServer = mock(StubServer.class);
        doAnswer(invocation -> {
            Searcher searcher = ((Request) invocation.getArgument(0)).getSearcher();
            if (!searcher.name().equals("google")) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return new Gson();
        }).when(mockServer).makeRequest(any());

        ActorRef master = system.actorOf(Props.create(MasterActor.class, mockServer), "master");
        Object responces = PatternsCS.ask(master, "text", new Timeout((FiniteDuration) Duration.create("10 second"))).toCompletableFuture().join();
        assertTrue(responces instanceof ArrayList);
        ArrayList<Responce> responceList = (ArrayList<Responce>) responces;
        assertEquals(1, responceList.size());
        assertEquals("google", responceList.get(0).getSearcher().name());
        system.terminate();
    }
}
