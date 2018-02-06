/**
 * Borrower
 * Encapsulates a registered user of the library
 */
public class Borrower {
  private int bwrID;
  private String surName;
  private String frstName;
  private int houseNumber;
  private String postCode;
  
  public Borrower(Integer bID, String sn, String fn, int hn, String pcd)
      throws IllegalArgumentException {
    if (!dataValid(bID, sn, fn, hn, pcd)) {
      throw new IllegalArgumentException("Bad arg(s) in Borrower constructor");
    }
    bwrID = bID;
    surName = sn;
    frstName = fn;
    houseNumber = hn;
    postCode = pcd;
  }

  public Integer getBwrID()   {  return bwrID;       }
  public String getSurName()  {  return surName;     }
  public String getFrstName() {  return frstName;    }
  public int getHouseNumber() {  return houseNumber; }
  public String getPostCode() {  return postCode;    }
  
  public String toString() {
    return String.format("%03d: %s %s (%d, %s)",
      bwrID, frstName, surName, houseNumber, postCode);
  }

  public static boolean dataValid(
              int id, String snm, String fnm, int num, String pcd) {
    if (snm.length() == 0) return false;
    if (fnm.length() == 0) return false;
    if (id < 0 || num < 0) return false;
    String pcdRegex = "[A-Z]{2}[0-9]{1,2}\\s[0-9][A-Z]{2}"; 
      //"most" but not all valid postcodes (Google!)
    if (!pcd.matches(pcdRegex)) return false;
    return true;
  }
}
