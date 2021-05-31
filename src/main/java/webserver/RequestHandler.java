package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            String line = bufferedReader.readLine();
            log.debug("request line {}", line);

            if (line == null) {
                return;
            }

            String method = HttpRequestUtils.getMethod(line);
            String url = HttpRequestUtils.getUrl(line);

            HashMap<String, String> headers = HttpRequestUtils.getHeaders(bufferedReader);

            DataOutputStream dos = new DataOutputStream(out);

            if (method.equals("POST")) {
                if (url.startsWith("/user/create")) {
                    String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(headers.get("Content-Length")));
                    Map<String, String> map = HttpRequestUtils.parseQueryString(requestBody);
                    User user = new User(map.get("userId"), map.get("password"), map.get("name"), map.get("email"));

                    log.debug("request body : {}", requestBody);
                    log.debug("user : {}", user);
                    DataBase.addUser(user);

                    response302Header(dos, "/index.html");
                } else if (url.equals("/user/login")) {
                    String requestBody = IOUtils.readData(bufferedReader, Integer.parseInt(headers.get("Content-Length")));
                    Map<String, String> map = HttpRequestUtils.parseQueryString(requestBody);
                    User user = DataBase.findUserById(map.get("userId"));


                    if (user == null) {
                        log.error("user not found!");
                        response302Header(dos, "/index.html");
                        return;
                    }

                    if (user.getPassword().equals(map.get("password"))) {
                        log.error("password success!");
                        response302LoginSuccessHeader(dos);
                    } else {
                        log.error("password mismatch!");
                        response302Header(dos, "/index.html");
                    }
                }
            } else {
                if (url.equals("/user/list")) {
                    boolean logined = Boolean.parseBoolean(headers.get("logined"));
                    if (logined) {
                        Collection<User> all = DataBase.findAll();
                        log.debug("all users : {}", all);

                        StringBuilder sb = new StringBuilder();

                        sb.append("<table>");

                        for (User user : all) {
                            sb.append("<tr>");
                            sb.append("<td>").append(user.getUserId()).append("</td>");
                            sb.append("<td>").append(user.getName()).append("</td>");
                            sb.append("<td>").append(user.getEmail()).append("</td>");
                            sb.append("</tr>");
                        }

                        sb.append("</table>");

                        byte[] body = sb.toString().getBytes();
                        response200Header(dos, body.length);
                        responseBody(dos, body);
                        return;
                    } else {
                        response302Header(dos, "/index.html");
                        return;
                    }
                }

//                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp/" + url).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }


        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200HeaderWithCookie(DataOutputStream dos, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String targetUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + targetUrl + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
