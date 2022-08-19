public final class Announcement implements DatabaseEntity {
    private final String title;
    private final String module;
    private final String link;
    private final String author;
    private final String postedAt;

    public Announcement(String title, String module, String link, String author, String postedAt) {
        this.title = title;
        this.module = module;
        this.link = link;
        this.author = author;
        this.postedAt = postedAt;
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

    public String getAuthor() {
        return author;
    }

    public String getPostedAt() {
        return postedAt;
    }
}
