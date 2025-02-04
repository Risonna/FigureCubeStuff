module com.risonna.stuff.figurecubestuff {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.management;
    requires java.sql;
    requires mysql.connector.j;

    opens com.risonna.stuff.figurecubestuff to javafx.fxml;
    exports com.risonna.stuff.figurecubestuff;


    // Экспортируем пакет, где находится MainController
    exports com.risonna.stuff.figurecubestuff.controller to javafx.fxml;

    // Открываем пакет, чтобы FXML мог использовать рефлексию
    opens com.risonna.stuff.figurecubestuff.controller to javafx.fxml;
    exports com.risonna.stuff.figurecubestuff.util to javafx.fxml;
    opens com.risonna.stuff.figurecubestuff.util to javafx.fxml;


}