/*
 * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie können es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
    veröffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es nützlich sein wird, aber
    OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License für weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package org.blockinger.game.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

import static org.blockinger.game.db.HighscoreOpenHelper.*;

public class ScoreDataSource {

    // Database fields
    private SQLiteDatabase database;
    private final HighscoreOpenHelper dbHelper;
    private final String[] allColumns = {
            COLUMN_ID,
            COLUMN_SCORE,
            COLUMN_PLAYERNAME,
            COLUMN_DATE,
    };

    public ScoreDataSource(Context context) {
        dbHelper = new HighscoreOpenHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createScore(long score, String playerName, Date date) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SCORE, score);
        values.put(COLUMN_PLAYERNAME, playerName);
        values.put(COLUMN_DATE, date.getTime());
        long insertId = database.insert(TABLE_HIGHSCORES, null, values);
        Cursor cursor = database.query(TABLE_HIGHSCORES,
                allColumns, COLUMN_ID + " = " + insertId, null,
                null, null, COLUMN_SCORE + " DESC");
        cursor.moveToFirst();
        cursor.close();
    }

    public void deleteScore(Score score) {
        long id = score.id;
        //System.out.println("Comment deleted with id: " + id);
        database.delete(TABLE_HIGHSCORES, COLUMN_ID
                + " = " + id, null);
    }

    public Cursor getCursor() {
        return database.query(TABLE_HIGHSCORES,
                allColumns, null, null, null, null, COLUMN_SCORE + " DESC");
    }

}
