import akka.actor.UntypedActor;
import com.google.gson.Gson;

public class ChildActor extends UntypedActor {
    private StubServer stubServer;

    public ChildActor(StubServer stubServer) {
        this.stubServer = stubServer;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Request){
            Request request = (Request) message;
            Gson gson = stubServer.makeRequest(request);
            Responce responce = new Responce(request.getSearcher(), gson);
            getSender().tell(responce, self());
            getContext().stop(self());
        }
    }
}
