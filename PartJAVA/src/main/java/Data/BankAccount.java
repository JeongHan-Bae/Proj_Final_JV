package Data;

public class BankAccount {
    public float currency;
    public float deposit;
    public float debt;

    public Investment investment;

    public BankAccount(String infos) {
        String[] parts = infos.split("\\|");

        // Process the first part for volume information
        String[] volume = parts[0].split(" ");
        this.currency = Float.parseFloat(volume[0]);
        this.deposit = Float.parseFloat(volume[1]);
        this.debt = Float.parseFloat(volume[2]);

        // Process the second part for investment information
        String investInfos = parts[1];
        this.investment = new Investment(investInfos);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
                "currency=" + currency +
                ",\n deposit=" + deposit +
                ",\n debt=" + debt +
                ",\n investment=" + investment +
                '}';
    }
}
