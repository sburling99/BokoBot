package BotPackage;

class BirthdayQueries {

    String insertionQuery(String name, String birthday) {
        String values = "null, '" + name + "', '" + birthday + "'";
        String query = "INSERT INTO BIRTHDAYS (ID,NAME,BIRTHDAY) " +
                "VALUES (" + values + ");";
        return query;
    }

    String getByDateQuery(String date) {
       String query = "SELECT * FROM BIRTHDAYS WHERE BIRTHDAY = '" + date +"'";
       return query;
    }

    String getByNameQuery(String name) {
        String query = "SELECT * FROM BIRTHDAYS WHERE name = '" + name +"'";
        return query;
    }
}