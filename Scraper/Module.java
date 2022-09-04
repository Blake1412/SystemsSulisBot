public enum Module {
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
