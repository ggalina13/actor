public class Request {
    private Searcher searcher;
    private String text;

    public Request(Searcher searcher, String text) {
        this.searcher = searcher;
        this.text = text;
    }

    public Searcher getSearcher() {
        return searcher;
    }

    public String getText() {
        return text;
    }
}
