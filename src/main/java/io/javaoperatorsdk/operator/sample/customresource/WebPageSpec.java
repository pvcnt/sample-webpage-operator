package io.javaoperatorsdk.operator.sample.customresource;

public class WebPageSpec {
    private String html;
    private boolean exposed = false;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public boolean getExposed() {
        return exposed;
    }

    public WebPageSpec setExposed(boolean exposed) {
        this.exposed = exposed;
        return this;
    }

    @Override
    public String toString() {
        return "WebPageSpec{" +
                "html='" + html + '\'' +
                ", exposed=" + exposed +
                '}';
    }
}