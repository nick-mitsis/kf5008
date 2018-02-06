import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*; //ArrayList; HashMap; LinkedList;


public class MainMenu extends JFrame implements ActionListener {
  public static final long LOANMAX = 1814400000; //ms = 21 days
  // data collections
  private Map<Integer, Borrower> borrowers;
  private Map<Integer, Item> items;
  private List<LoanTransaction> loans;
  
  //GUI
  private ReturnDlg returnDlg;
  private BorrowDlg borrowDlg;
  private JButton btnReadData, btnSaveLoans, btnLendItems, btnReturnItems,
                  btnListLoans, btnListODLoans;

  public static void main(String[] args) {
    MainMenu app = new MainMenu();
    app.setVisible(true);
  }


  public MainMenu() { //constructor
    // Database
    borrowers = new HashMap<Integer, Borrower>();
    items = new HashMap<Integer, Item>();
    loans = new LinkedList<LoanTransaction>();

    // GUI - create custom dialog instances
    returnDlg = new ReturnDlg(this);
    borrowDlg = new BorrowDlg(this);
    
    // GUI - set window properties
    setTitle(" Loan Management System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(200, 100, 350, 300);

    //GUI - main menu buttons    
    JPanel mainPnl = new JPanel();
    mainPnl.setLayout(new GridLayout(3,2));

    btnReadData = new JButton("Read Data");
    btnReadData.addActionListener(this);
    mainPnl.add(btnReadData);
    
    btnListLoans = new JButton("List Loans");
    btnListLoans.addActionListener(this);
    mainPnl.add(btnListLoans);
    
    btnLendItems = new JButton("Lend Items");
    btnLendItems.addActionListener(this);
    mainPnl.add(btnLendItems);
    
    btnListODLoans = new JButton("List Overdue Loans");
    btnListODLoans.addActionListener(this);
    mainPnl.add(btnListODLoans);
    
    btnReturnItems = new JButton("Return Items");
    btnReturnItems.addActionListener(this);
    mainPnl.add(btnReturnItems);
    
    btnSaveLoans = new JButton("Save Loans");
    btnSaveLoans.addActionListener(this);
    btnSaveLoans.setEnabled(false);
    mainPnl.add(btnSaveLoans);

    
    add(mainPnl, BorderLayout.CENTER);
  } //end constructor

  //Accessors for data structures
  public Map<Integer, Borrower>  getBorrowers() { return borrowers; }
  public Map<Integer, Item>      getItems()     { return items; }
  public List<LoanTransaction> getLoans()       { return loans; }

  /**
   * Actions in response to buttons
   */
  public void actionPerformed(ActionEvent evt) {
    Object src = evt.getSource();
    //read borrowers, items, loans JUST ONCE to initialize the system
    if (src == btnReadData) { 
    	readBorrowerData();
    	listBorrowers();
    	readItemData();
    	readLoans(); // saved from a previous session 
    	listItems(); // AFTER loans reloaded
    	btnReadData.setEnabled(false);
    	btnSaveLoans.setEnabled(true);
    } else if (src == btnLendItems) { // borrowDlg dialog will do multiple loans
    	borrowDlg.setVisible(true);
    } else if (src == btnReturnItems) { // returnDlg will do multiple returns
    	returnDlg.setVisible(true);
    } else if (src == btnListLoans) {
    	listLoans();
    } else if (src == btnListODLoans) {
    	listODLoans();
    }else if (src == btnSaveLoans) {
    	saveLoans();
    }

  }
  
  /**
   * Read data from borrowers.txt using a Scanner; unpack and populate
   *   borrowers Map. List displayed on console.  
   */
  public void readBorrowerData() {
    String fnm="", snm="", pcd="";
    int num=0, id=1;
    try {
      Scanner scnr = new Scanner(new File("borrowers.txt"));
      scnr.useDelimiter("\\s*#\\s*");
      while (scnr.hasNextInt()) {
        id  = scnr.nextInt();
        snm = scnr.next();
        fnm = scnr.next();
        num = scnr.nextInt();
        pcd = scnr.next();
        borrowers.put(new Integer(id), new Borrower(id, snm, fnm, num, pcd));
      }
      scnr.close();
    } catch (NoSuchElementException e) {
      System.out.printf("%d %s %s %d %s\n", id, snm, fnm, num, pcd);
      JOptionPane.showMessageDialog(this, e.getMessage(),
          "fetch of next token failed ", JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Borrowers file not found",
          "Unable to open data file", JOptionPane.ERROR_MESSAGE);
    }
  } //end readBorrowerData
  
  // List Borrowers on console
  public void listBorrowers() {
    System.out.println("Borrowers:");
    for (Borrower b: borrowers.values()) {
      System.out.println(b);
    }
    System.out.println();
  }

  /**
   * Read data from items.txt using a Scanner; unpack and populate
   *   items Map. List displayed on console.  
   */
  public void readItemData() {
    String ttl="", aut="";
    int id=1;
    try {
      Scanner scnr = new Scanner(new File("items.txt"));
      scnr.useDelimiter("\\s*#\\s*");
      while (scnr.hasNextInt()) {
        id  = scnr.nextInt();
        ttl = scnr.next();
        aut = scnr.next();
        items.put(new Integer(id), new Item(id, ttl, aut));
      }
      scnr.close();
    } catch (NoSuchElementException e) {
      System.out.printf("%d %s %s\n", id, ttl, aut);
      JOptionPane.showMessageDialog(this, e.getMessage(),
          "fetch of next token failed ", JOptionPane.ERROR_MESSAGE);
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException e) {
        JOptionPane.showMessageDialog(this, e.getMessage(),
            "Error", JOptionPane.ERROR_MESSAGE);
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Items file not found",
          "Unable to open data file", JOptionPane.ERROR_MESSAGE);
    }
  } //end readItemData

  // List Items on console
  public void listItems() {
    System.out.println("Items:" + "\n");
    for (Item i: items.values()) {
      System.out.println(i +"\n");
    }
    System.out.println();
  }

  //Assumes borrowers, items have been loaded
  public void readLoans() {
    if (loans.size() > 0) {
      JOptionPane.showMessageDialog(this, "Already some loans!",
          "Error", JOptionPane.ERROR_MESSAGE);
      return;
    }
    try {
      Scanner scnr = new Scanner(new File("loans.txt"));
      while (scnr.hasNextInt()) {
        Borrower b = borrowers.get(scnr.nextInt());
        Item i     = items.get(scnr.nextInt());
        LoanTransaction t = new LoanTransaction(b, i, scnr.nextLong());
        loans.add(t);
        i.setBorrowedBy(b);
      }
      scnr.close();
      System.out.printf("%d loan records added\n", loans.size());
    } catch (IOException e) {
      JOptionPane.showMessageDialog(this, "Loans file not found",
          "Unable to open data file", JOptionPane.ERROR_MESSAGE);
    }
  } //end readLoans

  public void listLoans() {
	  System.out.println("Loading loan list . . .");
	  System.out.println("Loans List:" + "\n");
	  boolean noLoans = true;
	  for (LoanTransaction t : loans) {
		  System.out.println(t + "\n");
		  noLoans = false;
	  }
	  
	  if (noLoans) {
		  System.out.println("[empty] - No loans found.");
	  }
	  System.out.println("\n\n"); 
  }//end listLoans
  
  
  
  public void listODLoans() {
	  System.out.println("Loading...");
	  System.out.println("Overdue Loans List:" + "\n");
	  boolean noODLoans = true;
	  for (LoanTransaction t : loans) {
		  if(System.currentTimeMillis() - t.getTimeStamp() > LOANMAX) {
			  System.out.println(t + "\n");
			  noODLoans = false;
		  }
	  }
	  
	  if (noODLoans) {
		  System.out.println("[empty] - No overdue loans found.");
	  }
	  System.out.println("\n\n");
  }//end listODLoans
  
  
  
  public void saveLoans() {
	  try {
		  PrintWriter prt = new PrintWriter(new File("loans.txt"));
		  System.out.println("Saving Loans List...");
		  for (LoanTransaction t : loans) {
			  prt.printf("%s %s %d \n", t.bdBy.getBwrID(), t.item.getItemID(), t.getTimeStamp());
		  }
		  
		  prt.close();
		  System.out.println("Saved!\n\n");
	  }
	  catch(FileNotFoundException e){
		  JOptionPane.showMessageDialog(this, "Loans file not found",
		          "Unable to open data file", JOptionPane.ERROR_MESSAGE);
	  }
	  
  }//end saveLoans

} //end class
