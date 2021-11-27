package Server;

public class RequestText {
    private String message;
    private Integer requestId;
    private boolean seen;
    private String studentId;

    public RequestText(String message, Integer requestId,String studentId) {
        this.message = message;
        this.requestId = requestId;
        this.studentId = studentId;
    }
    public RequestText(String message) {
        this.message = message;
        this.requestId = 0;
        this.studentId = "-1";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getRequestId() {
        return requestId;
    }

    public void setRequestId(Integer requestId) {
        this.requestId = requestId;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getStudentId() {
        return studentId;
    }

    @Override
    public String toString() {
        return "RequestText{" +
                "message='" + message + '\'' +
                ", requestId=" + requestId +
                '}';
    }
}
