import java.io.Serializable;

public class BabyName implements Serializable, Comparable<BabyName> {
    private String name;
    private String gender;
    private int count;
    private int rank;

    public BabyName() {
        this("", "", 0, 0); // Порожній конструктор для JavaBean
    }

    public BabyName(String name, String gender, int count, int rank) {
        this.name = name;
        this.gender = gender;
        this.count = count;
        this.rank = rank;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    @Override
    public int compareTo(BabyName other) {
        return Integer.compare(this.rank, other.rank);
    }
}