public enum Module {
    CS4222("https://sulis.ul.ie/portal/site/115662f7-395d-4555-8f54-26b9b59d8da3/tool/",
            "2282618c-8a91-49ea-972b-b58c2ea8298b", "65d3e7f1-5770-4cbd-8883-7963b80e80cb"),
    MA4402("https://sulis.ul.ie/portal/site/038d7ffd-a6fd-4691-b10e-a1ebdd303737/tool/",
            "4b48de9f-e7bb-4bd2-bcc2-92366b1ee61e", "805288de-2769-4bcf-98c3-00c003049fcd"),
    ET4162("https://sulis.ul.ie/portal/site/e92804c5-22c9-4339-b185-fa4895dcbba3/tool/",
            "ed88f051-5720-4bc5-b608-abe26bca40cc", "ef84289a-bd0d-434a-835e-0041165649d6"),
    CS4182("https://sulis.ul.ie/portal/site/039f08da-38ed-467c-8714-c2858149aa00/tool/",
            "09ccb53b-925d-472f-84ae-73ef2cddadaf", "080e9dea-4825-460b-a269-027636c924fe"),
    CS4043("https://sulis.ul.ie/portal/site/7c07dbc7-6a3f-4bc3-90d6-b32cb95e7e5d/tool/",
            "b807c2f2-7fba-4b32-8376-e852763e3b0a", "e823eb59-e214-4cc3-8fcb-08704691e376");

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
