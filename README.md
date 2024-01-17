# Proj_Final_JV

## Navigator 

- [PartC](#partc)
- [PartJava](#partjava)
- [PartPython](#partpython)

# Informations

This project, titled "Proj_Final_JV," is a comprehensive Java application that integrates with components written in C++ and Python. It aims to provide a simulated banking system with features such as user authentication, account management, financial transactions, cryptocurriencies, and investment functionalities.

## Abstract

The project simulates a banking environment, offering users the ability to create accounts, perform financial transactions, invest in various financial products, and explore cryptocurrency trading. It consists of three main components: PartC (C++ server), PartJava (Java client-end), and PartPython (Python data simulation). The communication between these components is facilitated through a custom protocol.

## Author Information

This project was collaboratively developed by:

1. [JeongHan-Bae](https://github.com/JeongHan-Bae)
2. [Mathurinnnnnn](https://github.com/Mathurinnnnnn)
3. [lkzAmine](https://github.com/lkzAmine)

For any inquiries or additional information, please contact us.


# Usage
Download all files and unzip `rapidjson-1.1.0.zip` after installation. (this is the lib for the c++ server)

You can use PartPython to generate simulated data.

1. Launch the C++ server (keep it running indefinitely) in PartC first.
2. Launch the Python project to update the data.
3. Launch the Java client-end project.

Note: You can also call admin functions through the console, but since this is a client-end project, we haven't prepared a specific interface for admin operations.

# PartC

## Overview
PartC serves as the server component of the project, responsible for receiving and handling commands from various client ends, including the Java client end, Java admin end, and Python data progresser. It utilizes the `rapidjson` library for JSON parsing and `winsock2.h` for Windows socket programming.

## Dependencies
- [`RapidJSON`](https://github.com/Tencent/rapidjson): A fast JSON parser/generator for C++.
- `winsock2.h`: Windows Socket API for socket programming (Windows only).

## Commands
PartC processes various commands sent by different client ends. The mapping of command types to their corresponding operation codes is defined as follows:

```cpp
std::unordered_map<std::string, int> operationMap = {
    {"createNewAcc", 1}, {"userLogin", 2}, {"add", 3},
    {"getBank", 4}, {"getGlobal", 5}, {"sellStock", 6},
    {"buyStock", 7}, {"applyLoan", 8}, {"transfer", 9},
    {"getCoinVal", 10}, {"sellCoins", 11}, {"buyCoins", 12},
    {"pushOrder", 13}
};
```

## Building and Running
To build and run PartC, follow these steps:

1. Open the project using CLion or your preferred C++ development environment.
2. Ensure that the necessary dependencies, including `winsock2.h` (for Windows systems), are properly included.
3. Build and run the project using the integrated development environment.

## Usage
PartC accommodates intermittent connections from various client ends. Here's the general usage flow:

1. Start the server, which listens on port 12345.
2. Connect one or more client ends (Java client end, Java admin end, Python data progresser) as needed.
    - Clients can establish connections, send commands, and receive responses.
    - Clients may disconnect after completing their tasks.
3. PartC processes received commands, performs corresponding actions, and updates data accordingly.

Note: As PartC is designed to handle intermittent connections, clients can connect, perform actions, and disconnect as needed without affecting the server's continuous operation.


# PartJava

## Overview
The `PartJava` component of the project includes Java classes responsible for the client-end functionality and administrative tools.

## Dependencies
Ensure you have Java installed on your system to compile and run the Java classes. We recommend using IntelliJ IDEA as the integrated development environment (IDE) for working with the Java project.

## Building and Running
To build and run the Java client-end application using IntelliJ IDEA, follow these steps:

1. Open IntelliJ IDEA.
2. Navigate to the `PartJAVA` directory and open it as an IntelliJ project.
3. In IntelliJ, locate the client application file:
   - `PartJAVA/src/main/java/com/example/partjava/MyApplication.java`
4. Open `MyApplication.java`.
5. Build and run the client application by clicking the Run button or pressing `Shift + F10`.

### Console Admin Tool
To run the console admin tool, follow these steps:

1. Open IntelliJ IDEA.
2. Navigate to the `PartJAVA` directory and open it as an IntelliJ project.
3. In IntelliJ, locate the console admin file:
   - `PartJAVA/src/main/java/Tools/Admin.java`
4. Open `Admin.java`.
5. Build and run the console admin tool by clicking the Run button or pressing `Shift + F10`.

Note: Ensure that the PartC server is running before executing the Java client-end application or the console admin tool. Refer to the PartC documentation for server setup instructions.

## File Structure
- `PartJAVA/src/main/java/com/example/partjava/MyApplication.java`: Java client-end application.
- `PartJAVA/src/main/java/Tools/Admin.java`: Console admin tool for the project.

## Controllers

### LoginController Class

The `LoginController` class, located in the `com.example.partjava` package, serves as the controller for the initial login interface in the program. This controller handles user authentication, retrieves user and bank information, updates the application's data structures, and navigates to the main user interface upon successful login.

#### FXML Elements

- `connectButton:` Button for initiating the login process.
- `newAccButton:` Button for creating a new account.
- `alertLabel:` Label for displaying alerts or messages.
- `usrNameField:` Text field for entering the username.
- `pwField:` Password field for entering the user's password.

#### Methods

- `connexionClick():` Method triggered by clicking the `connectButton`. It handles the login process, communicates with the server, and navigates to the main user interface upon successful authentication.
  
- `UpdateAccInfo(String accInfo):` Static method responsible for connecting to the server, updating the user's bank information, and populating the `UserObj.accountMap`.
  
- `createNewAcc():` Method triggered by clicking the `newAccButton`. It navigates to the NewAccount.fxml interface for creating a new account.

#### Login Process

The `connexionClick()` method performs the following steps:

1. Retrieves the entered username and password from the corresponding text fields.
2. Constructs login and bank information commands using the entered credentials.
3. Initiates a `JavaClient` to communicate with the server.
4. Sends the login command and receives user information.
5. Validates the received user information; displays an alert for invalid credentials.
6. Parses and sets user information in the `UserObj` class.
7. Sends a bank information command, updates account information, and closes the connection.
8. Retrieves global information, initializes `GlobalObj`, and prints user and global information.
9. Navigates to the main user interface using `SceneNavigator`.

#### Usage

The `LoginController` class integrates with JavaFX FXML elements, server communication (`JavaClient`), and data structures (`UserObj`, `GlobalObj`). It provides the necessary logic for user authentication and serves as the entry point to the application.


### NewAccountController Class

The `NewAccountController` class, part of the `com.example.partjava` package, serves as the controller for the NewAccount.fxml interface. This controller handles the creation of a new user account, validates input fields, communicates with the server, and navigates to the login interface upon successful account creation.

#### FXML Elements

- `civilityField:` ChoiceBox for selecting the user's civility (Monsieur, Madame, - -).
- `usernameField:` TextField for entering the desired username.
- `firstNameField:` TextField for entering the user's first name.
- `familyNameField:` TextField for entering the user's family name.
- `telephoneField:` TextField for entering the user's telephone number.
- `emailField:` TextField for entering the user's email address.
- `passwordField:` PasswordField for entering the user's password.
- `repeatPasswordField:` PasswordField for re-entering the user's password for confirmation.
- `createButton:` Button for initiating the account creation process.
- `clearButton:` Button for clearing all input fields.

#### Methods

- `initialize():` Initializes the `civilityField` with predefined choices.
- `onCreateButtonClicked():` Validates input fields, hashes the password, sends registration information to the server, and navigates to the login interface upon successful account creation.
- `onClearButtonClicked():` Clears all input fields.

#### Account Creation Process

The `onCreateButtonClicked()` method performs the following steps:

1. Validates the entered user information and displays appropriate alerts for any errors.
2. Constructs a registration information string using the entered data.
3. Initiates a `JavaClient` to communicate with the server.
4. Sends the registration information to the server and receives a response.
5. Closes the connection with the server.
6. Displays an information alert for a successful account creation and navigates to the login interface.
7. Displays a warning alert if the username already exists.

#### Usage

The `NewAccountController` class integrates with JavaFX FXML elements, server communication (`JavaClient`), and password hashing (`Password2Hash`). It provides the logic for creating a new user account and ensures data integrity through input validation.


### UsersInterfaceController Class

The `UsersInterfaceController` class, part of the `com.example.partjava` package, serves as the controller for the UsersInterface.fxml interface. This controller manages the main graphical user interface, displaying user information and providing navigation to various functionalities.

#### FXML Elements

- `usernameTextArea:` TextArea for displaying the user's username.
- `fullNameTextArea:` TextArea for displaying the user's full name.
- `teleTextArea:` TextArea for displaying the user's telephone number.
- `emailTextArea:` TextArea for displaying the user's email address.
- `currencyTextArea:` TextArea for displaying the user's account currency balance.
- `depositTextArea:` TextArea for displaying the user's account deposit balance.
- `debtTextArea:` TextArea for displaying the user's account debt balance.
- `investmentTextArea:` TextArea for displaying information about the user's investments.
- `investmentButton:` Button for navigating to the investment functionality.
- `cryptoButton:` Button for navigating to the cryptocurrency functionality.
- `virementButton:` Button for navigating to the money transfer functionality.
- `loanButton:` Button for navigating to the loan functionality.
- `logOutButton:` Button for logging out and navigating to the login interface.
- `myAccountButton:` Button for viewing account-related charts.

#### Methods

- `initialize():` Initializes the user interface by updating the displayed user information.
- `onInvestmentButtonClick():` Navigates to the investment interface.
- `onCryptoButtonClick():` Navigates to the cryptocurrency interface.
- `onVirementButtonClick():` Navigates to the money transfer interface.
- `onLoanButtonClick():` Navigates to the loan interface.
- `onLogOutButtonClick():` Logs out the user and navigates to the login interface.
- `onMyAccountButtonClick():` Generates and displays charts related to the user's account.
- `updateInfoTextArea():` Updates the information displayed in the text areas based on the `UserObj` data.

#### Usage

The `UsersInterfaceController` class manages the main graphical user interface, ensuring the accurate display of user information and providing navigation to different functionalities. It integrates with other parts of the application, such as the `ChartGenerator` for chart visualization.


### InvestmentController Class

The `InvestmentController` class, part of the `com.example.partjava` package, serves as the controller for the Investment.fxml interface. This controller manages investment-related functionalities, allowing users to view charts, invest in financial products, and navigate back to the main interface.

#### FXML Elements

- `returnButton:` Button for returning to the main user interface.

#### Methods

- `onViewGlobalButtonClick():` Displays global investment charts for the nearest 30 days.
- `onViewSelfButtonClick():` Displays user-specific investment charts for the nearest 30 days.
- `onInvestButtonClick():` Opens a new window to perform financial product investments.
- `onReturnButtonClick():` Returns to the main user interface.

#### Usage

The `InvestmentController` class facilitates interactions related to investments, including viewing investment charts and initiating financial product transactions. It interacts with the `ChartGenerator` class for chart visualization and utilizes the `SceneNavigator` class for seamless navigation between interfaces.

### InvestController Class

The `InvestController` class, part of the `com.example.partjava` package, serves as the controller for the Invest.fxml interface. This controller handles the buying and selling of financial products, providing functionalities to search for stock prices, make purchases, and sell stocks.

#### FXML Elements

- `currencyLabel:` Label for displaying the user's available currency for investments.
- `companyNameField:` TextField for entering the name of the company for stock-related actions.
- `amountField:` TextField for entering the amount of stocks or currency for transactions.
- `passwordField:` PasswordField for entering the user's password to authorize transactions.

#### Methods

- `initialize():` Initializes the controller, updating the currency label.
- `onSearchButtonClick():` Searches for and displays the current stock price of a specified company.
- `onPurchaseButtonClick():` Processes the purchase of stocks based on user input.
- `onSellButtonClick():` Processes the sale of stocks based on user input.
- `onClearButtonClick():` Clears all input fields.
- `updateCurrencyLabel():` Updates the currency label with the user's available currency.
- `updateBankAcc(String accInfo):` Updates the user's bank account information using the provided string.

#### Usage

The `InvestController` class is responsible for managing user interactions related to financial product transactions. It validates user inputs, performs transactions, and updates relevant information, interacting with the `JavaClient`, `UserObj`, `GlobalObj`, and `SceneNavigator` classes as needed.

### CryptoController Class

The `CryptoController` class, part of the `com.example.partjava` package, serves as the controller for the `Crypto.fxml` interface. This controller handles functionalities related to cryptocurrencies, including displaying sale and purchase lists, managing transactions, and refreshing data.

#### FXML Elements

- `refreshButton:` Button to refresh cryptocurrency data.
- `currencyLabel:` Label for displaying the user's available currency.
- `coinValueLabel:` Label for displaying the value of a cryptocurrency coin.
- `coinAmountLabel:` Label for displaying the amount of cryptocurrencies held by the user.
- `outputArea:` TextArea for displaying sale and purchase lists.
- `returnButton:` Button for returning to the main user interface.

#### Methods

- `showSaleList():` Displays the list of sale orders.
- `showPurchaseList():` Displays the list of purchase orders.
- `action():` Opens a new window to interact with existing cryptocurrency orders.
- `order():` Opens a new window to place a new cryptocurrency order.
- `refresh():` Refreshes cryptocurrency data.
- `onReturnButtonClick():` Returns to the main user interface.
- `initialize():` Initializes the controller and retrieves cryptocurrency data.

#### Usage

The `CryptoController` class handles functionalities related to cryptocurrencies, interacting with the `JavaClient`, `Password2Hash`, `ShowAlert`, and `SceneNavigator` classes as needed.

### CryptoActController Class

The `CryptoActController` class, part of the `com.example.partjava` package, serves as the controller for the `CryptoAct.fxml` interface. This controller manages actions related to existing cryptocurrency orders, including selling and buying actions.

#### FXML Elements

- `clientTextField:` TextField for entering the username of the client.
- `passwordField:` PasswordField for entering the user's password.

#### Methods

- `sellAction():` Processes the sale action based on user input.
- `buyAction():` Processes the buy action based on user input.

#### Usage

The `CryptoActController` class interacts with the `JavaClient`, `Password2Hash`, `ShowAlert`, and `SceneNavigator` classes to manage actions related to existing cryptocurrency orders.


### CryptoOrderController Class

The `CryptoOrderController` class, part of the `com.example.partjava` package, serves as the controller for the `CryptoOrder.fxml` interface. This controller handles placing sale and purchase orders for cryptocurrencies.

#### FXML Elements

- `amountTextField:` TextField for entering the amount of cryptocurrency for the order.
- `passwordField:` PasswordField for entering the user's password.

#### Methods

- `sellOrder():` Places a sale order or withdraws an existing sale order based on user input.
- `buyOrder():` Places a purchase order or withdraws an existing purchase order based on user input.

#### Usage

The `CryptoOrderController` class manages the execution of cryptocurrency orders, allowing users to place new orders or withdraw existing ones. It interacts with the `JavaClient`, `UserObj`, `GlobalObj`, and `SceneNavigator` classes as necessary.

### VirementController Class

The `VirementController` class, part of the `com.example.partjava` package, serves as the controller for the `Virement.fxml` interface. This controller handles functionalities related to fund transfers, allowing users to specify a beneficiary, enter an amount, and complete a transfer.

#### FXML Elements

- `currencyLabel:` Label for displaying the user's available currency.
- `beneficiaryField:` TextField for entering the username of the beneficiary.
- `amountField:` TextField for entering the amount to be transferred.
- `passwordField:` PasswordField for entering the user's password to authorize the transfer.
- `returnButton:` Button for returning to the main user interface.

#### Methods

- `initialize():` Initializes the controller, setting the currency label with the account's currency from `UserObj`.
- `transfer():` Processes the fund transfer based on user input, including beneficiary, amount, and password.
- `onReturnButtonClick():` Returns to the main user interface.

#### Usage

The `VirementController` class facilitates fund transfer interactions, validating user inputs, checking for errors, and updating `UserObj.account.currency` upon successful transfers. It interacts with the `JavaClient`, `Password2Hash`, `ShowAlert`, and `SceneNavigator` classes as needed.

### LoanController Class

The `LoanController` class, part of the `com.example.partjava` package, serves as the controller for the `Loan.fxml` interface. This controller manages functionalities related to loan applications, allowing users to apply for loans, specify the loan amount, and enter the required password for authorization.

#### FXML Elements

- `currencyLabel:` Label for displaying the user's available currency.
- `debtLabel:` Label for displaying the user's current debt.
- `loanAmountField:` TextField for entering the loan amount.
- `passwordField:` PasswordField for entering the user's password to authorize the loan application.
- `returnButton:` Button for returning to the main user interface.

#### Constants

- `rate:` The interest rate for the loan application (0.0025 or 0.25%).
- `loanLimit:` The maximum allowable debt limit for a loan (-3000).

#### Methods

- `initialize():` Initializes the controller, setting labels with the current values from `UserObj`.
- `applyLoan():` Processes the loan application based on user input, including the loan amount and password.
- `onReturnButtonClick():` Returns to the main user interface.

#### Usage

The `LoanController` class facilitates loan application interactions, validating user inputs, checking for errors, and updating `UserObj.account.currency` and `UserObj.account.debt` upon successful loan applications. It interacts with the `JavaClient`, `Password2Hash`, `ShowAlert`, and `SceneNavigator` classes as needed.

# PartPython

## Overview
The `PartPython` component of the project consists of two Python scripts: `main.py` and `repayment.py`. These scripts are responsible for simulating data, updating the global state, and processing repayments.

## Dependencies
Ensure you have Python installed on your system to run the Python scripts. We recommend using PyCharm as the integrated development environment (IDE) for executing the Python files.

## Usage
### Simulating Data (main.py)
To simulate data and update the global state using PyCharm, follow these steps:
1. Open PyCharm.
2. Open the `PartPython` directory as a PyCharm project.
3. Open the `main.py` file.
4. Run the `main.py` script by clicking the Run button.

This script connects to the server (PartC) to exchange data. It sends a "save" command to update the local base with current server data, simulates data processing, and sends a "read" command to update the server data with the current local base.

### Repayment Processing (repayment.py)
To process repayments using PyCharm, follow these steps:
1. Open PyCharm.
2. Open the `PartPython` directory as a PyCharm project.
3. Open the `repayment.py` file.
4. Run the `repayment.py` script by clicking the Run button.

This script connects to the server (PartC) to exchange data. It sends a "save" command to update the local base with current server data, processes repayments, and sends a "read" command to update the server data with the current local base.

Note: Ensure the PartC server is running before executing these scripts. Refer to the PartC documentation for server setup instructions.

## File Structure
- `main.py`: Simulates data and updates the global state.
- `repayment.py`: Processes repayments for each entry in the database.

Feel free to explore and modify these scripts based on your project requirements.

