import json

if __name__ == '__main__':
    # Load the existing Global.json data
    with open("../StoreJSON/Global.json", "r") as file:
        global_data = json.load(file)

    # Get user input for date and coin value
    date_input = input("Enter the date (DD/MM/YYYY): ")

    # Get user input for coin value, handling invalid input
    try:
        coin_value_input = float(input("Enter the coin value: "))
    except ValueError:
        print("Invalid input for coin value. Using the existing coin value.")
        coin_value_input = global_data["coin"]

    # Update Global["coin"] with the new coin value
    global_data["coin"] = coin_value_input

    # Update or add the coin value for the specified date in Global["coinMap"]
    global_data["coinMap"][date_input] = global_data["coin"]
    if date_input not in global_data["dataMap"]:
        global_data["dataMap"][date_input] = {}

    # Get user input for stock values for a list of companies
    company_list = ["company_a", "company_b", "company_c", "company_d", "company_e"]
    for company in company_list:
        # Get user input for stock value, handling invalid input
        try:
            stock_value = float(input(f"Enter stock value for {company}: "))
        except ValueError:
            print(f"Invalid input for {company}'s stock value. Using the existing value.")
            stock_value = global_data["currDataMap"].get(company, 0.0)

        global_data["currDataMap"][company] = stock_value
        global_data["dataMap"][date_input][company] = stock_value

    # Save the updated data back to Global.json
    with open("../StoreJSON/Global.json", "w") as file:
        json.dump(global_data, file)

    print("Global.json has been updated successfully.")
