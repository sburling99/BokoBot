package BotPackage;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.*;
import javax.security.auth.login.LoginException;

public class App extends ListenerAdapter {

    public static String prefix = "-";

    public static void main(String[] args) throws LoginException {
        Logger logger = LoggerFactory.getLogger(App.class);

        JDABuilder builder = JDABuilder.createDefault(System.getenv("BOT_TOKEN"));

        builder.setActivity(Activity.listening("Try typing -waddup"));

        builder.addEventListeners(new BotController());

        logger.info("Testing logger");

        builder.build();
    }
}
