package models;

public class Response {
  public int code = 0;
  public String msg = null;

  public Response() {
  }
  
  public Response(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
