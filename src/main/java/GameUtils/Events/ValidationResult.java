package GameUtils.Events;

public class ValidationResult {

    private boolean valid;
    private String reason;

    public ValidationResult(boolean validity) {
        this.valid = validity;
    }

    public ValidationResult(boolean validity, String reason) {
        this.valid = validity;
        this.reason = reason;
    }

    public boolean isValid() {
        return valid;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
