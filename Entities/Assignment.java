public final class Assignment implements DatabaseEntity {
    private final String title;
    private final String module;
    private final String link;
    private final String openDate;
    private final String dueDate;

    public Assignment(String title, String module, String link, String openDate, String dueDate) {
        this.title = title;
        this.module = module;
        this.link = link;
        this.openDate = openDate;
        this.dueDate = dueDate;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String getLink() {
        return link;
    }

    public String getOpenDate() {
        return openDate;
    }

    public String getDueDate() {
        return dueDate;
    }
}
