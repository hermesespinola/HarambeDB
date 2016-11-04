package database;

import structures.dict.LinkedDict;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import database.table.Table;

@SuppressWarnings("rawtypes")
public class DataBase implements Serializable {
  private static final long serialVersionUID = 14L;
  protected LinkedDict<String, Table<?>> tables;
  // protected DirectedGraph<Column> relationships;
  protected String name;

}
