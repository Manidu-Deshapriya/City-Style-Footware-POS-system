package com.citystyle;

import com.citystyle.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        // Run schema update check
        com.citystyle.database.SchemaUpdate.main(null);

        new LoginFrame().setVisible(true);
    }
}

