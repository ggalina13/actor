import com.google.gson.Gson;

public class StubServer {
    public Gson makeRequest(Request request){
        return new Gson();
    }
}
