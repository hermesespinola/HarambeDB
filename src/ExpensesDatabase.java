import java.util.Comparator;

import database.Database;
import database.table.Table;
import database.table.column.Column;
import database.table.relation.Relation;
import database.table.row.Row;

public class ExpensesDatabase {
	Database db;
	Table<String> users;
	Table<Integer> invoices;
	Table<String > items;

	private void getDB() throws Exception {
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
			// maybe the database already exists, try to load it.
			db = Database.load("Expenses");
			users = db.getTable("Users", String.class);
			invoices = db.getTable("Invoices", Integer.class);
			items = db.getTable("Items", String.class);
		}
	}

	public void addItem(String itemName, Integer itemExpense) throws Exception {
		items.addRow(itemName).set(items.getColumn("Expense"), itemExpense);
	}

	// items must be in Items table
	public void addInvoice(String userName, Integer uniqueInvoiceNumber, String[] itemsNames) throws Exception {
		Row userRow = users.getRow(userName);
		if (userRow == null) {
			throw new RuntimeException("No such user: " + userName);
		}
		int total = 0;
		for (String item : itemsNames) {
			Integer itemExpense = items.getRow(item).get(items.getColumn("Expense"));
			total += itemExpense;
		}
		invoices.addRow(uniqueInvoiceNumber).set(items.getColumn("Total"), total)
			.set(items.getColumn("Items"), itemsNames);

		Integer[] currentInvoices = userRow.get(users.getColumn("Invoices"));
		Integer[] newInvoices = new Integer[currentInvoices.length + 1];
		System.arraycopy(currentInvoices, 0, newInvoices, 0, currentInvoices.length);
		newInvoices[currentInvoices.length] = uniqueInvoiceNumber;
		userRow.set(users.getColumn("Invoices"), newInvoices);
	}

	public void addUser(String name, String address, Integer[] invoices) throws Exception {
		users.addRow(name).set(users.getColumn("Address"), address).set(users.getColumn("Invoices"), invoices);
	}

	public static void main(String [] args) {

	}
}
