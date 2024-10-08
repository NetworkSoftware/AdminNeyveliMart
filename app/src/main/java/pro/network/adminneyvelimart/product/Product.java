package pro.network.adminneyvelimart.product;

import java.io.Serializable;

/**
 * Created by ravi on 16/11/17.
 */

public class Product implements Serializable {
    String id;
    String brand;
    String model;
    String mrp;
    String name;
    String image;
    String description;
    String category;
    String qty;
    String stock_update;
    String userId;
    String sub_category;
    String createdon;
    public String shopname;
    public String shopid;
    public String latlong, nmPrice, price,availCourier,timing;


    public Product() {
    }

    public String getPrice() {
        return price;
    }


    public void setPrice(String price) {
        this.price = price;
    }

    public String getNmPrice() {
        return nmPrice;
    }

    public void setNmPrice(String nmPrice) {
        this.nmPrice = nmPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Product(String brand, String model, String mrp, String name, String image, String description, String category, String sub_category, String shopname) {
        this.brand = brand;
        this.model = model;
        this.mrp = mrp;
        this.name = name;
        this.image = image;
        this.description = description;
        this.category = category;
        this.sub_category = sub_category;
        this.shopname = shopname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }


    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getStock_update() {
        return stock_update;
    }

    public void setStock_update(String stock_update) {
        this.stock_update = stock_update;
    }

    public String getSub_category() {
        return sub_category;
    }

    public void setSub_category(String sub_category) {
        this.sub_category = sub_category;
    }

    public String getShopname() {
        return shopname;
    }

    public void setShopname(String shopname) {
        this.shopname = shopname;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getLatlong() {
        return latlong;
    }

    public void setLatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getCreatedon() {
        return createdon;
    }

    public void setCreatedon(String createdon) {
        this.createdon = createdon;
    }

    public String getAvailCourier() {
        return availCourier;
    }

    public void setAvailCourier(String availCourier) {
        this.availCourier = availCourier;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }
}