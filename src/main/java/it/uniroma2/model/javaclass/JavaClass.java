package it.uniroma2.model.javaclass;

public class JavaClass extends ClassMeta {

    private String content;

    public JavaClass(String pathName, ClassMetrics metrics, String content) {
        super(pathName, metrics);
        this.content = content;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
