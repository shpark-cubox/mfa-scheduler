package cubox.aero.cmfa.scheduler.model;

import lombok.Getter;

@Getter
public class Mail {

    private String to;
    private String from;
    private String subject;
    private String text;

    private Mail() {}

    public Mail(String text) {
        this.to = "shpark@cubox.aero";
        this.from = "mfa_scheduler@cubox.aero";
        this.subject = "quartz error";
        this.text = text;
    }
}
