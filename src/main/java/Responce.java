import com.google.gson.Gson;

public class Responce {
    private Searcher searcher;
    private Gson gson;

    public Responce(Searcher searcher, Gson gson) {
        this.searcher = searcher;
        this.gson = gson;
    }
    public Searcher getSearcher() {
        return searcher;
    }

    public Gson getGson() {
        return gson;
    }

}
