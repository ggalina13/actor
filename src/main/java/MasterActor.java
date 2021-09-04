import akka.actor.*;
import com.google.gson.Gson;
import scala.concurrent.duration.Duration;

import java.util.ArrayList;

public class MasterActor extends UntypedActor {
    private ArrayList<Responce> responces = new ArrayList<>();
    private StubServer stubServer;

    public MasterActor(StubServer stubServer) {
        this.stubServer = stubServer;
    }
    private ActorRef caller = null;
    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof String) {
            caller = getSender();
            String text = (String) message;
            for (Searcher searcher : Searcher.values()){
                Request request = new Request(searcher, text);
                String searcherName = searcher.name();
                ActorRef childActor = getContext().actorOf(Props.create(ChildActor.class, stubServer), searcherName + "Actor");
                childActor.tell(request, getSelf());
            }
            getContext().setReceiveTimeout(Duration.create("3 second"));
        }
        if (message instanceof Responce) {
            Responce responce = (Responce) message;
            responces.add(responce);
            if (responces.size() == Searcher.values().length){
                caller.tell(responces, getSelf());
                getContext().stop(self());
            }
        }
        if (message instanceof ReceiveTimeout){
            caller.tell(responces, getSelf());
            getContext().stop(self());
        }
    }

}
