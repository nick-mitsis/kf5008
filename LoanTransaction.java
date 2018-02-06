import java.util.Date;

/**
 * LoanTransaction
 * class to record borrowing of items
 */
public class LoanTransaction {
  protected Borrower bdBy;
  protected Item item;
  private long timeStamp;
  
  public LoanTransaction(Borrower b, Item i, long ts) {
    bdBy = b;
    item = i;
    timeStamp = ts;
  }
  
  public Borrower getBorrower() { return bdBy; }
  public Item getItem()         { return item; }
  public long getTimeStamp()    { return timeStamp; }

  public String toString() {
    return String.format("Item [%s] borrowed by [%s]; timestamp %d",
                          item, bdBy, timeStamp);
  }

  //In most contexts, Item has correct Borrower reference
  public String toStringOmitBwr() {
    return String.format("Item [%s] timestamp %d", item, timeStamp);
  }
}
