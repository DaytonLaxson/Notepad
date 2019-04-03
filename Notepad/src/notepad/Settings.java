package notepad;


public class Settings {
    double height;
    double width;
    String text;

    public Settings() {
    }

    public Settings(double height, double width, String text) {
        this.height = height;
        this.width = width;
        this.text = text;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    


}
