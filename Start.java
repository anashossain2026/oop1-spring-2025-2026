
package Task;
interface IMemberOperation {
    double discountedFee();
}

abstract class Member {
    double monthlyFee;
    String memberID;
    
    Member() {
    }

    Member(String memberID, double monthlyFee) {
        this.memberID = memberID;
        this.monthlyFee = monthlyFee;
    }

    abstract void showInfo();
}

class PlatinumMember extends Member implements IMemberOperation {
    int freeSessions;

    PlatinumMember() {
        super();
    }

    PlatinumMember(String memberID, double monthlyFee, int freeSessions) {
        super(memberID, monthlyFee);
        this.freeSessions = freeSessions;
    }

    @Override
    public double discountedFee() {
        if (this.monthlyFee > 8000) {
            return this.monthlyFee - (this.monthlyFee * 0.08);
        }
        return this.monthlyFee;
    }

    // Overriding the abstract method
    @Override
    void showInfo() {
        System.out.println("Platinum Member ID: " + memberID + " : Monthly Fee: " + monthlyFee + " : Free Sessions: " + freeSessions);
    }
}

class StandardMember extends Member implements IMemberOperation {
    boolean groupClassAccess;

    StandardMember() {
        super();
    }

    StandardMember(String memberID, double monthlyFee, boolean groupClassAccess) {
        super(memberID, monthlyFee);
        this.groupClassAccess = groupClassAccess;
    }

    @Override
    public double discountedFee() {
        if (this.monthlyFee > 8000) {
            return this.monthlyFee - (this.monthlyFee * 0.08); 
        }
        return this.monthlyFee;
    }

    @Override
    void showInfo() {
        System.out.println("Standard Member ID: " + memberID +" : Monthly Fee: " + monthlyFee +" : Group Class Access: " + (groupClassAccess ? "Yes" : "No"));
    }
}

class Gym {
    String name;
    Member[] mm;

    Gym() {
        mm = new Member[10]; //
    }

    Gym(String name, int count) {
        this.name = name;
        this.mm = new Member[count];
    }

    void addMember(Member m) {
        for (int i = 0; i < mm.length; i++) {
            if (mm[i] == null) {
                mm[i] = m;
                System.out.println("Successfully added member: " + m.memberID);
                return;
            }
        }
        System.out.println("Gym is at full capacity! Cannot add " + m.memberID);
    }

    void removeMember(String memberID) {
        for (int i = 0; i < mm.length; i++) {
            if (mm[i] != null && mm[i].memberID.equals(memberID)) {
                System.out.println("Successfully removed member: " + mm[i].memberID);
                mm[i] = null;
                return;
            }
        }
        System.out.println("Member ID " + memberID + " not found.");
    }

    void showMembers() {
        System.out.println("\n--- Members of " + name + " ---");
        boolean hasMembers = false;
        for (Member m : mm) {
            if (m != null) {
                m.showInfo();
                hasMembers = true;
            }
        }
        if (!hasMembers) {
            System.out.println("No members currently enrolled.");
        }
        System.out.println("......................");
    }

    void totalRevenue() {
        double total = 0;
        for (Member m : mm) {
            if (m != null) {
                if (m instanceof IMemberOperation) {
                    total += ((IMemberOperation) m).discountedFee();
                } else {
                    total += m.monthlyFee;
                }
            }
        }
        System.out.println("Total Monthly Revenue for " + name + ": $" + total + "\n");
    }
}

public class Start {
    public static void main(String[] args) {
        System.out.println("--- FitZone Gym Management System ---\n");
        Member m1 = new PlatinumMember("P-001", 9000, 5); 
        Member m2 = new StandardMember("S-001", 5000, true); 
        Gym fitZone = new Gym("FitZone City Center", 5);
        System.out.println("[Adding Members]");
        fitZone.addMember(m1);
        fitZone.addMember(m2);

        System.out.println("[Showing Members]");
        fitZone.showMembers();

        System.out.println("[Calculating Revenue]");
        fitZone.totalRevenue();

        System.out.println("[Removing a Member]");
        fitZone.removeMember("P-001");

        System.out.println("[Showing Members after Removal]");
        fitZone.showMembers();
        
        System.out.println("[Calculating Revenue after Removal]");
        fitZone.totalRevenue();
    }
}