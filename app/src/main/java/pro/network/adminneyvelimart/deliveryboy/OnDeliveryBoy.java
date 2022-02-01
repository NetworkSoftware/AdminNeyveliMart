package pro.network.adminneyvelimart.deliveryboy;

public interface OnDeliveryBoy {

    void onStatusClick(int position, String status);

    void onEditClick(pro.network.adminneyvelimart.deliveryboy.DeliveryBean deliveryBean);

    void onDeleteClick(int position);
}
