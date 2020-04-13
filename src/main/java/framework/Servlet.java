package framework;

public abstract class Servlet {
    public abstract Object respond(Request request) throws Exception;
}
