package com.example.microcredit.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.microcredit.data.dao.ClientDao;
import com.example.microcredit.data.dao.CreditDao;
import com.example.microcredit.data.dao.EcheancierDao;
import com.example.microcredit.data.entity.Client;
import com.example.microcredit.data.entity.Credit;
import com.example.microcredit.data.entity.Echeancier;
import com.example.microcredit.security.KeystoreHelper;
import net.sqlcipher.database.SupportFactory;

@Database(
        entities  = { Client.class, Credit.class, Echeancier.class },
        version   = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract ClientDao clientDao();
    public abstract CreditDao creditDao();
    public abstract EcheancierDao echeancierDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    String password = KeystoreHelper.getDbPassword(context);
                    SupportFactory factory = new SupportFactory(password.getBytes());
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "microcredit_db"
                            )
                            .openHelperFactory(factory)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}