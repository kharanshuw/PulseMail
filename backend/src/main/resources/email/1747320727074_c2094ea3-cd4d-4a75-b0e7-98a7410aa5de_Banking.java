package banking_system;

public class Banking {
    public void show() {
        Account savings = new Account() {
            double finalprice;
            String name;

            @Override
            public void calculateInterest(float rate, double amount, String name) {
                this.name = name;
                finalprice = (amount * ((100 + rate))) / 100;

            }

            @Override
            public void displayDetails() {
                System.out.println("The name of the customer is : " + name);
                System.out.println("The amount after interest rate is : " + finalprice);

            }
        };
        Account fixed = new Account() {
            double finalprice;
            String name;

            @Override
            public void calculateInterest(float rate, double amount, String name) {
                this.name = name;
                finalprice = (amount * ((100 + rate))) / 100;

            }

            @Override
            public void displayDetails() {
                System.out.println("The name of the customer is : " + name);
                System.out.println("The amount after interest rate is : " + finalprice);

            }
        };
        Account current = new Account() {
            double finalprice;
            String name;

            @Override
            public void calculateInterest(float rate, double amount, String name) {
                this.name = name;
                finalprice = (amount * ((100 + rate))) / 100;

            }

            @Override
            public void displayDetails() {
                System.out.println("The name of the customer is : " + name);
                System.out.println("The amount after interest rate is : " + finalprice);

            }

        };

savings.calculateInterest(5,10000,"shubham");
savings.displayDetails();
current.calculateInterest(10,40000,"sneha");
current.displayDetails();
fixed.calculateInterest(30,50000,"pooja");
fixed.displayDetails();
    }

}