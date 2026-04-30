package pu.fmi.httpserver;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestHandler {

  private static final String BODY_VALUES_SEPARATOR = ",";
  private static final String PATH_SEPARATOR = "/";

  private static final int TITLE_VALUE_BODY_INDEX = 0;
  private static final int MESSAGE_VALUE_BODY_INDEX = 1;
  private static final int TYPE_VALUE_BODY_INDEX = 2;

  private final List<Notification> notifications = new ArrayList<>();
  private int currentId = 1;

  public HttpResponse<?> handleRequest(HttpMethod method, String path, String body) {

    boolean isBasePath = path.equals("/notifications");
    boolean isIdPath = path.startsWith("/notifications/");
    boolean isValidPath = isBasePath || isIdPath;

    if (!isValidPath) {
      return new HttpResponse<>(HttpStatus.NOT_FOUND, "Not found");
    }

    if (method == HttpMethod.GET && isIdPath) {
      return getById(path);
    }
    if (method == HttpMethod.POST && isBasePath) {
      return create(body);
    }
    if (method == HttpMethod.PUT && isIdPath) {
      return update(path, body);
    }
    if (method == HttpMethod.DELETE && isIdPath) {
      return delete(path);
    }

    return new HttpResponse<>(
        HttpStatus.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED.getPhrase());
  }

  private HttpResponse<?> getById(String path) {
    Notification notification = find(path);

    if (notification == null) {
      return new HttpResponse<>(HttpStatus.NOT_FOUND, "Notification not found");
    }

    return new HttpResponse<>(HttpStatus.OK, notification);
  }

  private HttpResponse<Notification> create(String body) {
    String[] parts = body.split(BODY_VALUES_SEPARATOR);

    Notification notification = new Notification();
    notification.setId(currentId++);
    notification.setTitle(parts[TITLE_VALUE_BODY_INDEX]);
    notification.setMessage(parts[MESSAGE_VALUE_BODY_INDEX]);
    notification.setType(parts[TYPE_VALUE_BODY_INDEX]);
    notification.setRead(false);

    notifications.add(notification);

    return new HttpResponse<>(HttpStatus.CREATED, notification);
  }

  private HttpResponse<String> update(String path, String body) {
    Notification notification = find(path);

    if (notification == null) {
      return new HttpResponse<>(HttpStatus.NOT_FOUND, "Notification not found");
    }

    if (body != null && body.contains("read=true")) {
      notification.setRead(true);
    }

    return new HttpResponse<>(HttpStatus.OK, "Notification updated");
  }

  private HttpResponse<String> delete(String path) {
    Notification notification = find(path);

    if (notification == null) {
        return new HttpResponse<>(HttpStatus.NOT_FOUND, "Notification not found");
    }

    if (notification.isDeleted()) {
        return new HttpResponse<>(HttpStatus.NOT_FOUND, "Notification not found");
    }

    notification.setDeleted(true);

    return new HttpResponse<>(HttpStatus.NO_CONTENT, null);
}

  private Notification find(String path) {
    int id = Integer.parseInt(path.split(PATH_SEPARATOR)[2]);

    for (Notification notification : notifications) {
      if (notification.getId() == id) {
        return notification;
      }
    }

    return null;
  }
}