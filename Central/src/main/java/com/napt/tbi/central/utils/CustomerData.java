package com.napt.tbi.central.utils;

import java.util.Random;

public class CustomerData {

    private static Random rand = new Random();

    private static String generateRandomString(int number) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(number);
        for (int i = 0; i < number; i++) {
            // generate a random number between 0 to AlphaNumericString variable length
            int index= (int) (Math.random() * AlphaNumericString.length());
            // add Character one by one in end of sb
            //adde on 27th Jan
            //adde on 27th Jan_1
            //adde on 27th Jan_2
            //adde on 27th Jan_3
            //adde on 27th Jan_4
            //added on 27th Jan_5
            //added on 27th Jan_6
            //added on 27th Jan_7
            //added on 27th Jan_8
            //added on 27th Jan_9
            //added on 27th Jan_10
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    public static String getRandomFirstName() {
        String[] firstNames = {"JAMES", "JOHN", "ROBERT", "MICHAEL", "WILLIAM", "DAVID", "RICHARD", "CHARLES", "JOSEPH",
                "THOMAS", "CHRISTOPHER"};
        return firstNames[rand.nextInt(firstNames.length)];
    }

    public static String getRandomLastName() {
        String[] lastNames = {"SMITH", "JOHNSON", "BROWN", "JONES", "MILLER", "GARCIA", "RODRIGUEZ", "ANDERSON",
                "TAYLOR", "THOMAS", "MOORE"};
        return lastNames[rand.nextInt(lastNames.length)];
    }

    public static String getRandomEmail() {
        return (generateRandomString(5) + getRandomNumber(0, 100) + "@tailoredbrands.com");
    }

    public static int getRandomNumber(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }

}
