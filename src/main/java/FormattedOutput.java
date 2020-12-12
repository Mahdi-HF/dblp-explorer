public class FormattedOutput {
    private String id;
    private String title;
    private int year;
    private int n_citation;
    private String publisher;

    @Override
    public String toString() {
        return "id: " + id + "\ntitle: " + title + "\nyear: " + year + "\nn_citation: " + n_citation + "\npublisher: " + publisher + "\n";
    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return Long.parseLong(id);
    }
}
