/**
 * BorrowDlg
 * Custom dialog class with methods to lend a book to a
 * borrower, generating the loan transaction record 
 *  
 * @author Nick Mitsis
 *
 * @version 1.0 (25/10/2017)
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class BorrowDlg extends JDialog implements ActionListener {
	private MainMenu parent;
	private JTextField txtBorrowerID;
	private JTextField txtItemID;
	private JButton btnSubmit , btnHide;
	
	/**
     * Constructor for objects of class BorrowDlg
     * 
     * @param  parent       instance of MainMenu containing the data structures
     */
	public BorrowDlg (MainMenu p) {
		setTitle("Register Item Loan");
		parent = p; // Data structures are here
		
		// Components
		txtBorrowerID = new JTextField(10);
		txtItemID = new JTextField(10);
		btnSubmit = new JButton("Submit");
		btnHide = new JButton("Hide");
		
		// Layout 
		JPanel panel = new JPanel();
		panel.add(new JLabel("Borrower ID:"));
		panel.add(txtBorrowerID);
		add(panel, BorderLayout.NORTH);
		
		panel = new JPanel();
		panel.add(new JLabel("Item code:"));
		panel.add(txtItemID);
		add(panel, BorderLayout.CENTER);
		
		panel = new JPanel();
		panel.add(btnSubmit);
		panel.add(btnHide);
		add(panel, BorderLayout.SOUTH);
		
		// Dlg Window Size and Position
		setBounds(100, 100, 300, 150);
		
		// Actions 
		btnSubmit.addActionListener(this);
		btnHide.addActionListener(this);
		
	} //end Constructor

	
  /**
   * Actions: on click of 'Submit', find borrower records and
   * 		  add loan record for this book into the database;
   *          on click of 'Hide', hide the dialogue window.
   */
	public void actionPerformed(ActionEvent evt) {
		Object src = evt.getSource();
		if (src == btnHide) {
			setVisible(false);
			txtBorrowerID.setText("");
			txtItemID.setText("");
		}
		else if (src == btnSubmit) {
			proccessLend();
		}
	} //end actionPerformed
	
	public void proccessLend() {
		try {
			Integer bwrID = new Integer(txtBorrowerID.getText());
			Borrower bby = parent.getBorrowers().get(bwrID);
		    if (bby == null) {
		    	JOptionPane.showMessageDialog(this, "Borrower not found",
		    		  "Error", JOptionPane.ERROR_MESSAGE);
		    	txtBorrowerID.setText("");
		    	return;
		    }
			
			Integer itmID = new Integer(txtItemID.getText());
			Item itm = parent.getItems().get(itmID);
		    if (itm == null) {
		    	JOptionPane.showMessageDialog(this, "Item not found",
		              "Error", JOptionPane.ERROR_MESSAGE);
		    	txtItemID.setText("");
		    	return;
		    }
		    
		  //Find LoanTransaction ...
		      LoanTransaction mrkr = null;
		      for (LoanTransaction t: parent.getLoans()) {
		        if (t.getBorrower() == bby) {
		          mrkr = t;
		          break;
		        }
		      }
		      
		      if (mrkr != null && System.currentTimeMillis() - mrkr.getTimeStamp() > parent.LOANMAX) {
		    	  JOptionPane.showMessageDialog(this, "Loan overdue found!\n Return overdue items first.",
							"Error", JOptionPane.ERROR_MESSAGE);
		      }
		      else if (itm.getBorrowedBy() != null) {
		    	  JOptionPane.showMessageDialog(this, "Item already on loan!\n Return this item first.",
							"Error", JOptionPane.ERROR_MESSAGE);
		      }
		      else {
		    	  mrkr = new LoanTransaction(bby, itm, System.currentTimeMillis());
		    	  parent.getLoans().add(mrkr);
		    	  itm.setBorrowedBy(bby);
		    	  txtBorrowerID.setText("");
		    	  txtItemID.setText("");
		    	  System.out.printf("Item borrowed: [%s]\n\n", itm);
		    	  JOptionPane.showMessageDialog(this, "Loan added to database successfully.",
							"Success", JOptionPane.INFORMATION_MESSAGE);
		      }
		      
		}
		catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(),
					"Number format error", JOptionPane.ERROR_MESSAGE);
		}
	}
}
