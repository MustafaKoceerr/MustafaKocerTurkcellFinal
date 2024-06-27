package com.example.mustafakocer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mustafakocer.data.db.entity.BasicUserInfo
import com.example.mustafakocer.data.db.entity.Cart
import com.example.mustafakocer.data.db.entity.LikedProduct


@Database(
    entities =[Cart::class, BasicUserInfo::class, LikedProduct::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun createProductDao(): ProductDao

    abstract fun createUserDao(): UserDao


    companion object {
        @Volatile // this variable is immediately visible to all the other threads
        private var instance: AppDatabase? = null
        private val LOCK = Any()
        // I will use this LOCK to make sure we do not create two instances of our Database.

        //This function takes context as parameter because we need a context to create database
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                // eğer nullsa buraya girecek, null değilse zaten girmeyecek
                // we will assign the returned value of this buildDatabase that is our database actually to this instance
                instance = it
            }
        }
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext, // applicationContext verdik çünkü fragmentta çağırırsak hata almayalım diye
                AppDatabase::class.java,
                "FinalProje.db"
            ).build()
        }

    }

}