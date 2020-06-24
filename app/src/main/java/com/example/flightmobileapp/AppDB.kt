package com.example.flightmobileapp

import androidx.room.*

@Database (entities = [(UrlEntity::class)],version = 1)

abstract class AppDB : RoomDatabase(){
    abstract fun urlDAO():UrlDAO



    companion object{
//        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                // Since we didn't alter the table, there's nothing else to do here.
//            }
//        }
        @Volatile
        private var INSTANCE : AppDB? = null
        fun getInstance(context: MainActivity):AppDB{
            synchronized(this){
                var instance = INSTANCE
                if(instance==null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDB::class.java,
                        "url_data_database3"
                    //.addMigrations(MIGRATION_3_4)
                    ).build()
                }
                return instance
            }
        }

    }
}