public enum Module {
    CS4416("https://sulis.ul.ie/portal/site/b81b7292-1bd8-4a58-86a9-aff31a73db12/tool/",
            "f0dc7d06-b34d-4d85-b6d4-972acef9b1fe", "7b2268b0-e6af-48d4-82e6-af9b49a06e16"),
    CS4013("https://sulis.ul.ie/portal/site/11454e3b-9102-45f6-8c35-4c70151b7044/tool/",
            "1af4287c-f75b-4e46-8d85-84e38722b957", "ad70c122-7687-4794-aab4-1712752e04e4");

    private final String moduleLink;
    private final String assignmentsLink;
    private final String announcementsLink;

    Module(String moduleLink, String assignmentsLink, String announcementsLink) {
        this.moduleLink = moduleLink;
        this.assignmentsLink = assignmentsLink;
        this.announcementsLink = announcementsLink;
    }

    public String getAssignmentsLink() {
        return moduleLink + assignmentsLink;
    }

    public String getAnnouncementsLink() {
        return moduleLink + announcementsLink;
    }
}
