package BotPackage;

class DateCustom {
    DateFormatter df = new DateFormatter();
    String today = df.getDate();

    String getTodaysDate() {
        return today;
    }

}
