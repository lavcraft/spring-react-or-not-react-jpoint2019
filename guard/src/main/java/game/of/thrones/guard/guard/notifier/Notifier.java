package game.of.thrones.guard.guard.notifier;


import game.of.thrones.guard.guard.model.Notification;

public interface Notifier {
    void sendNotification(Notification notification);
}
