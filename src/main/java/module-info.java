module com.allamiflorencia.mines {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires javafx.media;
    
    opens com.allamiflorencia.mines.Controller to javafx.fxml;
    
    exports com.allamiflorencia.mines;
    exports com.allamiflorencia.mines.Controller;
}