package pro.network.adminneyvelimart.user;

public interface OnUsers {
    void onCall(Users position);
    void onOrder(Users position);
    void onWallet(Users users);
}
