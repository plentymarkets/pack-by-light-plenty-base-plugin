package plentybase.plugin.hue.bean;

public class Response {
    public String JSONResponse;
    public int responseCode;

    public Response(String JSONResponse, int responseCode) {
        this.JSONResponse = JSONResponse;
        this.responseCode = responseCode;
    }

    @Override
    public String toString() {
        return "Response{" +
                "JSONResponse='" + JSONResponse + '\'' +
                ", responseCode=" + responseCode +
                '}';
    }

    public String getJSONResponse() {
        return JSONResponse;
    }

    public void setJSONResponse(String JSONResponse) {
        this.JSONResponse = JSONResponse;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }
}
