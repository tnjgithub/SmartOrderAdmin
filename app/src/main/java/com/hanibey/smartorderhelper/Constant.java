package com.hanibey.smartorderhelper;

import com.hanibey.smartorder.administration.R;
import com.hanibey.smartordermodel.User;

/**
 * Created by Tanju on 26.09.2017.
 */

public class Constant {

    public static User CurrentUser = null;
    public static final int IMAGE_PICK = 1;
    public static final String IMAGE_FOLDER = "images/";

    public static final class Nodes {
        public static final String AppLog = "AppLog";
        public static final String Customer = "Customer";
        public static final String User = "User";
        public static final String Category = "Category";
        public static final String Product = "Product";
        public static final String Client = "Client";
        public static final String Order = "Order";
        public static final String ChefOrder = "ChefOrder";
        public static final String Report = "Report";
    };

    public static final class Categories{
        public static final int Food = 1;
        public static final int Drink = 2;
        public static final int Dessert = 3;
    };


    public static final class Fragments{
        public static final int Report = 0;
        public static final int User = 1;
        public static final int Category = 2;
        public static final int Product = 3;
    };


    public static final class UserTypes{
        public static final String Admin = "0";
        public static final String CashRegister = "1";
        public static final String Chef = "2";
    };

    public static final class ClientStatus{
        public static final String Offline = "0";
        public static final String Online = "1";
        public static final String Background = "2";
    };

    public static final class OrderStatus{
        public static final String New = "1";
        public static final String Preparing = "2";
        public static final String OnTheTable = "3";
        public static final String AdditionalOrder = "4";
        public static final String Paid = "5";
    };

    public static final class OrderItemStatus{
        public static final String Selected = "0";
        public static final String New = "1";
        public static final String Preparing = "2";
        public static final String OnTheTable = "3";
    };

    public static final class DateTypes{
        public static final int Year = 1;
        public static final int Month = 2;
        public static final int Day = 3;
    };


    //public static final String[] UserTypeList = {"Admin","Kasa","chef"};

}
