package expensesdb;

import database.table.relation.Relation;
import database.table.column.Column;
import database.HarambException;
import database.table.row.Row;
import database.table.Table;
import java.util.Comparator;
import structures.list.List;
import database.Database;

/**
* This is an example of using the HarambeDB framework to write a static database
*	class and is the final project of the datastructures class.
*
* <p>This class is a member of the
* <a href="{@docRoot}/index.html" target="_top">
* HarambeDB database framework</a>.
*
* @author  Hermes Espínola
* @author  Miguel Miranda
* @see     Database
* @see     Table
* @see     Row
*/
public final class ExpensesDatabase {
	/**
	*	The database object
	*/
	static Database db;

	/**
	* The Table of users
	* A user consists of a name (primary key) and an address.
	* A user is may have several invoices
	*/
	static Table<String> users;

	/**
	* The Table of invoices
	* An invoice consists of an invoice number (primary key) and a payment
	* An invoice have several items
	*/
	static Table<Integer> invoices;

	/**
	* The Table of items
	* An item consists of a name (primary key) and an expense.
	*/
	static Table<String> items;

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
			users = db.createTable("Users", String.class, "Name");
			invoices = db.createTable("Invoices", Integer.class, "ID");
			items = db.createTable("Items", String.class, "Name");

			// add columns to the tables
			users.addColumn("Address", String.class);
			Column invoiceCol = users.addColumn("Invoices", Integer[].class);
			invoices.addColumn("Payment", Integer.class);
			Column itemCol = invoices.addColumn("Items", String[].class);
			items.addColumn("Expense", Integer.class);

			// create relations between the tables
			db.createRelation("Users", "Invoices", "Invoices", Relation.Type.oneToMany);
			db.createRelation("Invoices", "Items", "Items", Relation.Type.oneToMany);
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

	/**
	* Adds an item to the items table
	* @param	itemName		The name of the item to add
	* @param	itemExpense	The expense of the item to add
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static void addItem(String itemName, Integer itemExpense) throws HarambException {
		items.addRow(itemName).set(items.getColumn("Expense"), itemExpense);
	}

	/**
	* Adds an user to the users table
	* @param	name				The name of the item to add
	* @param	address			The address of the user
	* @param invoices			The invoices the user has
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static void addUser(String name, String address, Integer[] invoices) throws HarambException {
		users.addRow(name).set(users.getColumn("Address"), address).set(users.getColumn("Invoices"), invoices);
	}

	/**
	* Adds an user to the users table
	* @param	name				The name of the item to add
	* @param	address			The address of the user
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static void addUser(String name, String address) throws HarambException {
		users.addRow(name).set(users.getColumn("Address"), address).set(users.getColumn("Invoices"), new Integer[1]);
	}

	/**
	* Removes an user from the users table
	* @param	userName					The name of the item to add
	* @param	removeInvoices		Tells if the invoices related to the user should be removed as well
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static void delteUser(String userName, boolean removeInvoices) throws HarambException {
		if (removeInvoices) {
			Integer[] uids = users.getRow(userName).get(users.getColumn("Invoices"));
			for (Integer invoiceId : uids) {
				invoices.removeRow(invoiceId);
			}
		}
		users.removeRow(userName);
	}

	/**
	* Removes an invoice from the invoices table
	* @param	invoiceUID		The number of invoice to remove
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static void deleteInvoice(Integer invoiceUID) throws HarambException {
		invoices.removeRow(invoiceUID);
	}

	/**
	* Removes an item from the items table
	* @param	itemName	The name of the item to remove
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static void deleteItem(String itemName) throws HarambException {
		items.removeRow(itemName);
	}

	/**
	* Retrieves the user row with the given name
	* @param	name	The name of the user
	* @return				The user row with the given name
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static Row getUser(String name) throws HarambException {
		return users.getRow(name);
	}

	/**
	* Retrieves the user row and its invoices
	* @param	name		The name of the item to add
	* @return					The user row and its invoices where list[0] is the user row and list[1:n] is the list of invoices
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static List<Row> getUserAndInvoices(String name) throws HarambException {
		return users.getRowWithRelation(name, db);
	}

	/**
	* Retrieves the user row, its invoices and its items
	* @param	name		The name of the item to add
	* @return					the user row, its invoices and its items
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static List<Row> getUserAndExpenses(String name) throws HarambException {
		return users.getRowWithRelations(name, db);
	}

	/**
	* Retrieves an invoice from the invoices table
	* @param	invoiceUID		The invoice number
	* @return								The row of the invoice
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static Row getInvoice(Integer invoiceUID) throws HarambException {
		return invoices.getRow(invoiceUID);
	}

	/**
	* Retrieves the invoice row and its items
	* @param	invoiceUID	The invoice number
	* @return							The invoice row and its items where list[0] is the invoice row and list[1:n] is the list of items
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static List<Row> getInvoiceAndItems(Integer invoiceUID) throws HarambException {
		return invoices.getRowWithRelations(invoiceUID, db);
	}


	/**
	* Retrieves the item row with the given name
	* @param	itemName	The name of the item
	* @return						The item row with the given name
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static Row getItem(String itemName) throws HarambException {
		return items.getRow(itemName);
	}

	/**
	* Calculates how similar are two users by expense, similarity is defined as
	*	the absolute value of the difference of the expenses of the two users
	* @param	xName			The name of an user
	* @param	yName			The name of another user
	* @return						The similarity between the users
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public int userSimilarity(String xName, String yName) throws HarambException {
		return Math.abs(getTotalPayments(xName) - getTotalPayments(yName));
	}

	/**
	* Retrieves the total payments of an user
	* @param	userName	The name of the user
	* @return						The sum of all his/her expenses payments
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public int getTotalPayments(String userName) throws HarambException {
		List<Row> userInvoices = users.getRowWithRelation(userName, db);
		int total = 0;
		for (int i = 1; i < userInvoices.size(); i++) {
			Integer invoicePayment = userInvoices.get(i).get(invoices.getColumn("Payment"));
			total += invoicePayment;
		}
		return total;
	}

	/**
	* Adds an invoice to the invoice table
	* @param	userName		The name of the user this invoice belongs to
	* @param	invoiceUID	The invoice number of the this invoice
	* @param	itemsNames	The list of items this invoice have
	*	@throws HarambException		If there is some error reading or writing to the database
	*/
	public static void addInvoice(String userName, Integer invoiceUID, String[] itemsNames) throws HarambException {
		Row userRow = users.getRow(userName);
		if (userRow == null) {
			throw new RuntimeException("No such user: " + userName);
		}
		int total = 0;
		for (String item : itemsNames) {
			Integer itemExpense = items.getRow(item).get(items.getColumn("Expense"));
			total += itemExpense;
		}
		invoices.addRow(invoiceUID).set(invoices.getColumn("Payment"), total)
		.set(invoices.getColumn("Items"), itemsNames);

		Integer[] currentInvoices = userRow.get(users.getColumn("Invoices"));
		Integer[] newInvoices = new Integer[currentInvoices.length + 1];
		System.arraycopy(currentInvoices, 0, newInvoices, 0, currentInvoices.length);
		newInvoices[currentInvoices.length] = invoiceUID;
		userRow.set(users.getColumn("Invoices"), newInvoices);
	}

	public static void main(String[] args) throws HarambException {
		addUser("Hermes", "Aqui");
		addUser("Mike", "Allá");
		addUser("Eros", "Aquí también");
		addItem("Huevos", 30);
		addItem("Pan", 20);
		addItem("Jamon", 15);
		addItem("Queso", 5);
		addItem("Mayonesa", 28);

		addInvoice("Hermes", 123, new String[] {"Pan", "Queso", "Mayonesa", "Jamon"});
		addInvoice("Eros", 234, new String[] {"Jamon", "Huevos"});
		addInvoice("Mike", 765, new String[] {"Mayonesa", "Huevos"});
		addInvoice("Eros", 235, new String[] {"Pan", "Queso"});
	}
}
