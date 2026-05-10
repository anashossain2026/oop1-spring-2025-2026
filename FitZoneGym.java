interface IMemberOperation {
    double discountedFee();
}

abstract class Member implements IMemberOperation {
    String memberID;
    double monthlyFee;

    Member() {
    }

    Member(String memberID, double monthlyFee) {
        this.memberID = memberID;
        this.monthlyFee = monthlyFee;
    }

    abstract void showinfo();

    @Override
    public double discountedFee() {
        if (this.monthlyFee > 8000) {
            return this.monthlyFee - (this.monthlyFee * 0.08); 
        }
        return this.monthlyFee;
    }
}

class PlatinumMember extends Member {
    int freeSessions;

    PlatinumMember() {
        super();
    }

    PlatinumMember(String memberID, double monthlyFee, int freeSessions) {
        super(memberID, monthlyFee);
        this.freeSessions = freeSessions;
    }

    @Override
    void showinfo() {
        System.out.println("Platinum Member ID: " + memberID +  " | Monthly Fee: " + monthlyFee +  " | Free Sessions: " + freeSessions);
    }
}

class StandardMember extends Member {
    boolean groupClassAccess;

    StandardMember() {
        super();
    }

    StandardMember(String memberID, double monthlyFee, boolean groupClassAccess) {
        super(memberID, monthlyFee);
        this.groupClassAccess = groupClassAccess;
    }

    @Override
    void showinfo() {
        System.out.println("Standard Member ID: " + memberID + " | Monthly Fee: " + monthlyFee +  " | Group Class Access: " + groupClassAccess);
    }
}

class Gym {
    String name;
    Member[] mm;
    int currentMemberCount;

    Gym() {
        this.currentMemberCount = 0;
    }

    Gym(String name, int count) {
        this.name = name;
        this.mm = new Member[count];
        this.currentMemberCount = 0;
    }

    void addMember(Member m) {
        if (currentMemberCount < mm.length) {
            mm[currentMemberCount] = m;
            currentMemberCount++;
            System.out.println("Member " + m.memberID + " added successfully.");
        } else {
            System.out.println("Gym is full! Cannot add more members.");
        }
    }

    void removeMember(String memberID) {
        for (int i = 0; i < currentMemberCount; i++) {
            if (mm[i].memberID.equals(memberID)) {
                System.out.println("Member " + mm[i].memberID + " removed successfully.");
                for (int j = i; j < currentMemberCount - 1; j++) {
                    mm[j] = mm[j + 1];
                }
                mm[currentMemberCount - 1] = null;
                currentMemberCount--;
                return;
            }
        }
        System.out.println("Member with ID " + memberID + " not found.");
    }

    void showMembers() {
        System.out.println("\n--- Gym Members (" + this.name + ") ---");
        for (int i = 0; i < currentMemberCount; i++) {
            mm[i].showinfo();
        }
        System.out.println("--------------------");
    }

    void totalRevenue() {
        double total = 0;
        for (int i = 0; i < currentMemberCount; i++) {
            total += mm[i].discountedFee();
        }
        System.out.println("\nTotal Gym Revenue (after discounts): " + total);
    }
}

public class FitZoneGym {
    public static void main(String[] args) {
        Member m1 = new PlatinumMember("P-01", 10000.0, 5);
        Member m2 = new StandardMember("S-01", 5000.0, true);
        
        Gym myGym = new Gym("FitZone Central", 5);
        
        myGym.addMember(m1);
        myGym.addMember(m2);
        
        myGym.showMembers();
        
        myGym.totalRevenue();
        
        System.out.println("Removing a member...");
        myGym.removeMember("S-01");
        
        myGym.showMembers();
        myGym.totalRevenue();
    }
}