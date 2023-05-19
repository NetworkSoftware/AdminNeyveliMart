package pro.network.adminneyvelimart.order;

public interface StatusListener {


    void onDeliveredClick(String id);
    void onWhatsAppClick(String phone);
    void onCallClick(String phone);
    void onCancelClick(String id);
    void onAssignDboy(Order order);
    void onTrackOrder(String id);
    void onCourier(String id);
    void InProgress(Order order );
    void bill(Order position);
    void wallet(Order position);
}
