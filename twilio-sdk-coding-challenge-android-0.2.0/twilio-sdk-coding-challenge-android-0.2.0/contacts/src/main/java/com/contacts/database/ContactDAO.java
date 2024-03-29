package com.contacts.database;

import com.contacts.models.Contact;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
/**
 *  Room database Contact DAO
 */
@Dao
public interface ContactDAO {
    @Query("SELECT * FROM contact")
    List<Contact> getAll();

    @Query("SELECT * FROM contact WHERE id IN (:userIds)")
    List<Contact> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM contact WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    Contact findByName(String first, String last);

    @Query("SELECT * FROM contact WHERE id=:id")
    Contact findById(String id);
    

    @Insert
    void insertAll(Contact... contacts);

    @Delete
    void delete(Contact contact);

    @Query("DELETE FROM contact")
    void nukeContactTable();
}
