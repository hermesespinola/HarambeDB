import java.util.Comparator;

import database.Database;
import structures.list.List;
import database.table.Table;
import database.table.row.Row;
import database.table.column.Column;
import database.table.relation.Relation;

public final class ExpensesDatabase {
	static Database db;
	static Table<String> users;
	static Table<Integer> invoices;
	static Table<String > items;

	// prevent class from being instantiable using reflection.
	private ExpensesDatabase() {
		throw new RuntimeException("ExpensesDatabase not instantiable");
	}

	// initialize database
	static {
		try {
			// create the database
			db = new Database("Expenses");
			// create the tables
			users = db.createTable("Users", String.class);
			invoices = db.createTable("Invoices", Integer.class);
			items = db.createTable("Items", String.class);

			// add columns to the tables
			users.addColumn("Address", String.class);
			Column invoiceCol = users.addColumn("Invoices", Integer[].class);
			invoices.addColumn("Total", Integer.class);
			Column itemCol = invoices.addColumn("Items", String[].class);
			items.addColumn("Expense", Integer.class);

			// create relations between the tables
			invoiceCol.createRelation(invoices, Relation.Type.oneToMany);
			itemCol.createRelation(items, Relation.Type.oneToMany);
		} catch (Exception e) {
			try {
				// maybe the database already exists, try to load it.
				db = Database.load("Expenses");
				users = db.getTable("Users", String.class);
				invoices = db.getTable("Invoices", Integer.class);
				items = db.getTable("Items", String.class);
			} catch (Exception e2) {
				e2.printStackTrace();
				System.exit(-1);
			}
		}
	}

	public static void addItem(String itemName, Integer itemExpense) throws Exception {
		items.addRow(itemName).set(items.getColumn("Expense"), itemExpense);
	}

	// items must be in Items table
	public static void addInvoice(String userName, Integer invoiceUID, String[] itemsNames) throws Exception {
		Row userRow = users.getRow(userName);
		if (userRow == null) {
			throw new RuntimeException("No such user: " + userName);
		}
		int total = 0;
		for (String item : itemsNames) {
			Integer itemExpense = items.getRow(item).get(items.getColumn("Expense"));
			total += itemExpense;
		}
		invoices.addRow(invoiceUID).set(items.getColumn("Total"), total)
			.set(items.getColumn("Items"), itemsNames);

		Integer[] currentInvoices = userRow.get(users.getColumn("Invoices"));
		Integer[] newInvoices = new Integer[currentInvoices.length + 1];
		System.arraycopy(currentInvoices, 0, newInvoices, 0, currentInvoices.length);
		newInvoices[currentInvoices.length] = invoiceUID;
		userRow.set(users.getColumn("Invoices"), newInvoices);
	}

	public static void addUser(String name, String address, Integer[] invoices) throws Exception {
		users.addRow(name).set(users.getColumn("Address"), address).set(users.getColumn("Invoices"), invoices);
	}

	public static void delteUser(String userName, boolean removeInvoices) throws Exception {
		if (removeInvoices) {
			Integer[] uids = users.getRow(userName).get(users.getColumn("Invoices"));
			for (Integer invoiceId : uids) {
				invoices.removeRow(invoiceId);
			}
		}
		users.removeRow(userName);
	}

	public static void deleteInvoice(Integer invoiceUID) throws Exception {
		invoices.removeRow(invoiceUID);
	}

	public static void deleteItem(String itemName) throws Exception {
		items.removeRow(itemName);
	}

	public static Row getUser(String name) throws Exception {
		return users.getRow(name);
	}

	public static List<Row> getUserAndInvoices(String name) throws Exception {
		return user.getRowWithRelation(name);
	}

	public static List<Row> getUserAndExpenses(String name) throws Exception {
		return users.getRowWithRelations(name, db);
	}

	public static Row getInvoice(Integer invoiceUID) throws Exception {
		return invoices.getRow(invoiceUID);
	}

	public static List<Row> getInvoiceAndItems(Integer invoiceUID) throws Exception {
		return invoices.getRowWithRelations(invoiceUID, db);
	}

	public static Row getItem(String itemName) throws Exception {
		return items.getRow(itemName);
	}
}
