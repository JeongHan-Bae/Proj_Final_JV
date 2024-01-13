#include <iostream>
#include <fstream>
#include <unordered_map>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
#include <rapidjson/stringbuffer.h>
#include <rapidjson/prettywriter.h>
#include <winsock2.h>
#include <thread>
#include <vector>
#include <ctime>
#include <iomanip>
#include <mutex>
#include <cstring>

using namespace rapidjson;
bool launch;
std::vector<std::thread> clientThreads;
std::mutex launchMutex;
std::unordered_map<std::string, int> operationMap = {
        {"createNewAcc", 1},
        {"userLogin", 2},
        {"add", 3},
        {"getBank", 4},
        {"getGlobal", 5},
        {"sellStock", 6},
        {"buyStock", 7},
        {"applyLoan", 8},
        {"transfer", 9},
        {"getCoinVal", 10},
        {"sellCoins", 11},
        {"buyCoins", 12},
        {"pushOrder", 13}
};

void update(const std::string& user, Document& DataBase){
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    std::ostringstream dateStream;
    dateStream << std::put_time(localTime, "%d/%m/%Y");
    std::string currentDate = dateStream.str();

    // Check if 'currentDate' exists in DataBase[user.c_str()]
    Value& userEntry = DataBase[user.c_str()];
    if (userEntry.HasMember(currentDate.c_str())) {
        // Replace existing 'currentDate' entry with new values
        userEntry[currentDate.c_str()].CopyFrom(userEntry["curr"], DataBase.GetAllocator());
    } else {
        // Create 'currentDate' and copy values from 'DataBase[user.c_str()]["curr"]'
        Value currentDateObj(kObjectType);
        currentDateObj.CopyFrom(userEntry["curr"], DataBase.GetAllocator());
        userEntry.AddMember(Value(currentDate.c_str(), DataBase.GetAllocator()), currentDateObj, DataBase.GetAllocator());
    }
}


bool createNewAcc(const std::string& infos, Document& Login, Document& DataBase) {
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    const std::string& user = parts[0];
    if (Login.HasMember(user.c_str())) {
        return false;  // user already exist
    }

    // add user to login
    Value userObject(kObjectType);
    userObject.AddMember("hash_password", Value(parts[1].c_str(), Login.GetAllocator()), Login.GetAllocator());
    userObject.AddMember("first_name", Value(parts[2].c_str(), Login.GetAllocator()), Login.GetAllocator());
    userObject.AddMember("family_name", Value(parts[3].c_str(), Login.GetAllocator()), Login.GetAllocator());
    userObject.AddMember("telephone", Value(parts[4].c_str(), Login.GetAllocator()), Login.GetAllocator());

    std::string e_mail = parts.back();
    userObject.AddMember("e_mail", Value(e_mail.c_str(), Login.GetAllocator()), Login.GetAllocator());

    Value key(user.c_str(), Login.GetAllocator());
    Login.AddMember(key, userObject, Login.GetAllocator());

    // add user to database
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    std::ostringstream dateStream;
    dateStream << std::put_time(localTime, "%d/%m/%Y");
    std::string currentDate = dateStream.str();

    if (!DataBase.HasMember(user.c_str())) {
        Value userDatabaseObject(kObjectType);
        Value currObject(kObjectType);
        userDatabaseObject.AddMember("currency", 500.0, DataBase.GetAllocator());
        userDatabaseObject.AddMember("deposit", 0.0, DataBase.GetAllocator());
        userDatabaseObject.AddMember("debt", 0.0, DataBase.GetAllocator());
        Value investmentObject(kObjectType);
        userDatabaseObject.AddMember("investment", investmentObject, DataBase.GetAllocator());

        currObject.AddMember("currency", 0.0, DataBase.GetAllocator());
        currObject.AddMember("deposit", 0.0, DataBase.GetAllocator());
        currObject.AddMember("debt", 0.0, DataBase.GetAllocator());
        Value currInvestment(kObjectType);
        currObject.AddMember("investment", currInvestment, DataBase.GetAllocator());

        Value data_key(user.c_str(), static_cast<int>(user.length()), DataBase.GetAllocator());
        Value current(currentDate.c_str(), static_cast<int>(currentDate.length()), DataBase.GetAllocator());
        Value userEntry(kObjectType);
        userEntry.AddMember(current, userDatabaseObject, DataBase.GetAllocator());

        userEntry.AddMember("curr", currObject, DataBase.GetAllocator());

        DataBase.AddMember(data_key, userEntry, DataBase.GetAllocator());
    }


    return true;
}


bool userLogin(const std::string& login_infos, const Document& Login, std::string& response) {
    // Split login_infos with space as delimiter
    std::istringstream iss(login_infos);
    std::vector<std::string> loginParts(std::istream_iterator<std::string>{iss},
                                        std::istream_iterator<std::string>());

    if (loginParts.size() < 2) {
        // Insufficient login information
        response = "Insufficient login information\n";
        return false;
    }

    const std::string& user = loginParts[0];
    const std::string& hash_password = loginParts[1];

    auto userIter = Login.FindMember(StringRef(user.c_str()));

    if (userIter != Login.MemberEnd() && userIter->value.HasMember("hash_password")) {
        const Value& storedPassword = userIter->value["hash_password"];

        if (storedPassword.IsString() && storedPassword.GetString() == hash_password) {
            // Valid login
            if (userIter->value.HasMember("first_name") && userIter->value["first_name"].IsString() &&
                userIter->value.HasMember("family_name") && userIter->value["family_name"].IsString() &&
                userIter->value.HasMember("telephone") && userIter->value["telephone"].IsString() &&
                userIter->value.HasMember("e_mail") && userIter->value["e_mail"].IsString()) {

                response = std::string(userIter->value["first_name"].GetString()) + " " +
                           std::string(userIter->value["family_name"].GetString()) + " " +
                           std::string(userIter->value["telephone"].GetString()) + " " +
                           std::string(userIter->value["e_mail"].GetString()) + "\n";
            }
            return true;
        }
    }
    // Invalid login
    response = "Invalid user data\n";
    return false;
}


bool find_user(const std::string& user, const Document& DataBase) {
    if (DataBase.HasMember(user.c_str()) && DataBase[user.c_str()].HasMember("curr")) {
        return true;
    }
    return false;
}


bool add(const std::string& infos, Document& DataBase) {
    // Split infos with space as delimiter
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss}, std::istream_iterator<std::string>());

    if (parts.size() < 2) {
        // Insufficient information
        return false;
    }

    const std::string& user = parts[0];
    const std::string& type = parts[1];
    const std::string& amountStr = parts[2];

    double amount;
    try {
        amount = std::stof(amountStr);
    } catch (const std::invalid_argument& e) {
        // Invalid amount format
        return false;
    }

    // Check if user exists in the database and has "curr" entry
    if (!find_user(user, DataBase)) {
        return false;
    }
    if (type == "currency"){
        double currency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();
        currency += amount;
        DataBase[user.c_str()]["curr"]["currency"].SetDouble(currency);
    } else if (type == "deposit"){
        double deposit = DataBase[user.c_str()]["curr"]["deposit"].GetDouble();
        deposit += amount;
        DataBase[user.c_str()]["curr"]["deposit"].SetDouble(deposit);
    } else if (type == "debt"){
        double debt = DataBase[user.c_str()]["curr"]["debt"].GetDouble();
        debt += amount;
        DataBase[user.c_str()]["curr"]["debt"].SetDouble(debt);
    } else {
        return false;
    }
    update(user, DataBase);

    return true;
}


bool getBank(const std::string& infos, const Document& DataBase, std::string& response) {
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    const std::string& user = parts[0];
    if (find_user(user, DataBase)) {
        for (Value::ConstMemberIterator it = DataBase[user.c_str()].MemberBegin(); it != DataBase[user.c_str()].MemberEnd(); ++it) {
            const std::string& date = it->name.GetString();
            const Value& dateData = it->value;

            response += date + ':' + std::to_string(dateData["currency"].GetDouble()) + ' ' +
                        std::to_string(dateData["deposit"].GetDouble()) + ' ' +
                        std::to_string(dateData["debt"].GetDouble()) + '|';

            // Iterate over investments
            for (Value::ConstMemberIterator investmentIt = dateData["investment"].MemberBegin();
                 investmentIt != dateData["investment"].MemberEnd(); ++investmentIt) {
                const std::string& investmentName = investmentIt->name.GetString();
                const Value& investmentValue = investmentIt->value;

                response += investmentName + ' ' + std::to_string(investmentValue.GetDouble()) + ',';
            }
            if (!response.empty() && response.back() == ',') {
                // Remove the last character
                response.pop_back();
            }
            response += ';';
        }
        if (!response.empty() && response.back() == ';') {
            // Remove the last character
            response.pop_back();
        }
        response += '\n';
        return true;
    }
    response = "Invalid user data\n";
    return false;
}


void processGlobal(const Document& global, std::string& response) {
    // Process "saleCoins"
    if (global.HasMember("saleCoins") && global["saleCoins"].IsObject()) {
        const Value& saleCoins = global["saleCoins"];
        for (Value::ConstMemberIterator it = saleCoins.MemberBegin(); it != saleCoins.MemberEnd(); ++it) {
            response += it->name.GetString();
            response += ' ';
            response += std::to_string(it->value.GetDouble());
            response += ',';
        }
        if (!response.empty() && response.back() == ',') {
            response.pop_back(); // Remove the last character
        }
        response += ';';
    }

    // Process "buyCoins"
    if (global.HasMember("buyCoins") && global["buyCoins"].IsObject()) {
        const Value& buyCoins = global["buyCoins"];
        for (Value::ConstMemberIterator it = buyCoins.MemberBegin(); it != buyCoins.MemberEnd(); ++it) {
            response += it->name.GetString();
            response += ' ';
            response += std::to_string(it->value.GetDouble());
            response += ',';
        }
        if (!response.empty() && response.back() == ',') {
            response.pop_back(); // Remove the last character
        }
        response += ';';
    }

    // Process "coin"
    if (global.HasMember("coin") && global["coin"].IsDouble()) {
        response += std::to_string(global["coin"].GetDouble());
        response += ';';
    }

    // Process "coinMap"
    if (global.HasMember("coinMap") && global["coinMap"].IsObject()) {
        const Value& coinMap = global["coinMap"];
        for (Value::ConstMemberIterator it = coinMap.MemberBegin(); it != coinMap.MemberEnd(); ++it) {
            response += it->name.GetString();
            response += ' ';
            response += std::to_string(it->value.GetDouble());
            response += ',';
        }
        if (!response.empty() && response.back() == ',') {
            response.pop_back(); // Remove the last character
        }
        response += ';';
    }

    // Process "currDataMap"
    if (global.HasMember("currDataMap") && global["currDataMap"].IsObject()) {
        const Value& currDataMap = global["currDataMap"];
        for (Value::ConstMemberIterator it = currDataMap.MemberBegin(); it != currDataMap.MemberEnd(); ++it) {
            response += it->name.GetString();
            response += ' ';
            response += std::to_string(it->value.GetDouble());
            response += ',';
        }
        if (!response.empty() && response.back() == ',') {
            response.pop_back(); // Remove the last character
        }
        response += ';';
    }

    // Process "dataMap"
    if (global.HasMember("dataMap") && global["dataMap"].IsObject()) {
        const Value& dataMap = global["dataMap"];
        for (Value::ConstMemberIterator it = dataMap.MemberBegin(); it != dataMap.MemberEnd(); ++it) {
            response += it->name.GetString();
            response += ':';

            // Process nested values
            const Value& nestedValues = it->value;
            if (nestedValues.IsObject()) {
                for (Value::ConstMemberIterator nestedIt = nestedValues.MemberBegin(); nestedIt != nestedValues.MemberEnd(); ++nestedIt) {
                    response += nestedIt->name.GetString();
                    response += ' ';
                    response += std::to_string(nestedIt->value.GetDouble());
                    response += ',';
                }
                if (!response.empty() && response.back() == ',') {
                    response.pop_back(); // Remove the last character
                }
            }
            response += '|';
        }
        if (!response.empty() && response.back() == '|') {
            response.pop_back(); // Remove the last character
        }
    }
    response += '\n';
}


bool sellStock(const std::string& infos, Document& DataBase, const Document& Global, const Document& Login, std::string& response) {
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    // Ensure that there are at least three parts
    if (parts.size() < 4) {
        response = "Failed\n";
        return false;
    }

    const std::string user = parts[0];
    std::string company = parts[1];
    const double amount = stod(parts[2]);
    const std::string hash_password = parts[3];

    if(Login[user.c_str()]["hash_password"].GetString() != hash_password){
        response = "Wrong Password\n";
        return false;
    }
    // Check if user exists in the database
    if (!find_user(user, DataBase)) {
        response = "Failed\n";
        return false;
    }

    if (!DataBase.HasMember(user.c_str()) || !DataBase[user.c_str()].HasMember("curr") || !DataBase[user.c_str()]["curr"].HasMember("currency")) {
        response = "Failed: User or currency not found\n";
        return false;
    }
    // Get the price from Global["currDataMap"]
    double price = Global["currDataMap"][company.c_str()].GetDouble();

    // Update currency with new value
    double currentCurrency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();
    double newCurrency = currentCurrency + (price * amount);
    DataBase[user.c_str()]["curr"]["currency"].SetDouble(newCurrency);
    response = std::to_string(newCurrency) + '\n';
    Value& investment = DataBase[user.c_str()]["curr"]["investment"];

    double restAmount = DataBase[user.c_str()]["curr"]["investment"][company.c_str()].GetDouble() - amount;
    // Update the investment for the 'company'

    if (restAmount > 0) {
        // If restAmount is greater than 0, update the value
        investment[company.c_str()].SetDouble(restAmount);
    } else {
        // If restAmount is 0 or negative, remove the entry
        investment.RemoveMember(company.c_str());
    }
    update(user, DataBase);

    return true;
}


bool buyStock(const std::string& infos, Document& DataBase, const Document& Global, const Document& Login, std::string& response) {
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    // Ensure that there are at least three parts
    if (parts.size() < 4) {
        response = "Failed\n";
        return false;
    }
    const std::string user = parts[0];
    std::string company = parts[1];
    const double amount = stod(parts[2]);
    const std::string hash_password = parts[3];

    if(Login[user.c_str()]["hash_password"].GetString() != hash_password){
        response = "Wrong Password\n";
        return false;
    }
    // Check if user exists in the database
    if (!find_user(user, DataBase)) {
        response = "Failed\n";
        return false;
    }
    // Get the price from Global["currDataMap"]
    double price = Global["currDataMap"][company.c_str()].GetDouble();

    // Update currency with new value
    double currentCurrency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();
    double newCurrency = currentCurrency - (price * amount);
    DataBase[user.c_str()]["curr"]["currency"].SetDouble(newCurrency);
    response = std::to_string(newCurrency) + '\n';
    // Check if 'investment' exists in DataBase[user.c_str()]["curr"]
    Value& investment = DataBase[user.c_str()]["curr"]["investment"];
    if (investment.HasMember(company.c_str()) && investment[company.c_str()].IsNumber()) {
        // If 'investment' has attribute 'company.c_str()', get the value as double, add the amount, and set the value
        double currentInvestment = investment[company.c_str()].GetDouble();
        double updatedInvestment = currentInvestment + amount;
        investment[company.c_str()].SetDouble(updatedInvestment);
    } else {
        // Else, create the attribute and set the value as amount
        investment.AddMember(Value(company.c_str(), DataBase.GetAllocator()).Move(), amount, DataBase.GetAllocator());
    }
    update(user, DataBase);

    return true;
}


bool applyLoan(const std::string& infos, Document& DataBase, const Document& Login){
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    // Ensure that there are at least three parts
    if (parts.size() < 4) {
        return false;
    }
    const std::string user = parts[0];
    const double amount = stod(parts[1]);
    const double rate = stod(parts[2]);
    const std::string hash_password = parts[3];

    if(Login[user.c_str()]["hash_password"].GetString() != hash_password){
        return false;
    }
    // Get and update currency
    double currency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();
    currency += amount;
    DataBase[user.c_str()]["curr"]["currency"].SetDouble(currency);

    // Get and update debt
    double debt = DataBase[user.c_str()]["curr"]["debt"].GetDouble();
    debt -= amount * (1 + rate);
    DataBase[user.c_str()]["curr"]["debt"].SetDouble(debt);

    return true;
}


bool transfer(const std::string& infos, Document& DataBase, const Document& Login, std::string& response){
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    // Ensure that there are at least three parts
    if (parts.size() < 4) {
        return false;
    }

    const std::string user = parts[0];
    const double amount = stod(parts[1]);
    const std::string beneficiary = parts[2];
    const std::string hash_password = parts[3];

    if(Login[user.c_str()]["hash_password"].GetString() != hash_password){
        response = "Wrong password\n";
        return false;
    }
    if(!find_user(beneficiary, DataBase)){
        response = "Beneficiary doesn't exist\n";
        return false;
    }
    double myCurrency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();
    myCurrency -= amount;
    DataBase[user.c_str()]["curr"]["currency"].SetDouble(myCurrency);

    double benCurrency = DataBase[beneficiary.c_str()]["curr"]["currency"].GetDouble();
    benCurrency += amount;
    DataBase[beneficiary.c_str()]["curr"]["currency"].SetDouble(benCurrency);
    update(user, DataBase);
    update(beneficiary, DataBase);

    response = "success\n";

    return true;
}


void getCoinVal(const Document& Global, std::string& response){
    response = std::to_string(Global["coin"].GetDouble()) + '\n';
}


bool sellCoins(const std::string& infos, Document& DataBase, Document& Global, const Document& Login, std::string& response){
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    // Ensure that there are at least three parts
    if (parts.size() < 3) {
        return false;
    }

    const std::string user = parts[0];
    const std::string client = parts[1];
    const std::string hash_password = parts[2];

    if(Login[user.c_str()]["hash_password"].GetString() != hash_password){
        response = "Wrong password\n";
        return false;
    }
    if(!find_user(client, DataBase)){
        response = "Client doesn't exist\n";
        return false;
    }
    if(!Global["buyCoins"].HasMember(client.c_str())){
        response = "Client doesn't exist\n";
        return false;
    }
    if(!DataBase[user.c_str()]["curr"]["investment"].HasMember("coin")){
        response = "Don't have enough Coins\n";
        return false;
    }
    double coinNum = DataBase[user.c_str()]["curr"]["investment"]["coin"].GetDouble();
    double currency = Global["buyCoins"][client.c_str()].GetDouble();
    double coinVal = Global["coin"].GetDouble();
    double coinAmount = currency / coinVal;

    if(coinNum < coinAmount){
        response = "Don't have enough Coins\n";
        return false;
    }
    coinNum -= coinAmount;
    DataBase[user.c_str()]["curr"]["investment"]["coin"].SetDouble(coinNum);

    double userCurrency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();
    userCurrency += currency;
    DataBase[user.c_str()]["curr"]["currency"].SetDouble(userCurrency);

    response = std::to_string(DataBase[user.c_str()]["curr"]["investment"]["coin"].GetDouble()) +
               " " +
               std::to_string(DataBase[user.c_str()]["curr"]["currency"].GetDouble()) + '\n';

    if(DataBase[client.c_str()]["curr"]["investment"].HasMember("coin")){
        double clientCoins = DataBase[client.c_str()]["curr"]["investment"]["coin"].GetDouble();
        clientCoins += coinAmount;
        DataBase[client.c_str()]["curr"]["investment"]["coin"].SetDouble(clientCoins);
    } else {
        Value coinAmountValue;
        coinAmountValue.SetDouble(coinAmount);
        DataBase[client.c_str()]["curr"]["investment"].AddMember("coin", coinAmountValue, DataBase.GetAllocator());
    }
    Global["buyCoins"].RemoveMember(client.c_str());

    update(user, DataBase);
    update(client, DataBase);

    return true;
}


bool buyCoins(const std::string& infos, Document& DataBase, Document& Global, const Document& Login, std::string& response){
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    // Ensure that there are at least three parts
    if (parts.size() < 3) {
        return false;
    }

    const std::string user = parts[0];
    const std::string client = parts[1];
    const std::string hash_password = parts[2];

    if(Login[user.c_str()]["hash_password"].GetString() != hash_password){
        response = "Wrong password\n";
        return false;
    }
    if(!find_user(client, DataBase)){
        response = "Client doesn't exist\n";
        return false;
    }

    if(!Global["saleCoins"].HasMember(client.c_str())){
        response = "Client doesn't exist\n";
        return false;
    }


    double currency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();
    double coinAmount = Global["saleCoins"][client.c_str()].GetDouble();
    double coinVal = Global["coin"].GetDouble();
    double price = coinAmount * coinVal;

    if(currency < price){
        response = "Don't have enough Currency\n";
        return false;
    }
    currency -= price;
    DataBase[user.c_str()]["curr"]["currency"].SetDouble(currency);

    if(DataBase[user.c_str()]["curr"]["investment"].HasMember("coin")){
        double userCoins = DataBase[user.c_str()]["curr"]["investment"]["coin"].GetDouble();
        userCoins += coinAmount;
        DataBase[user.c_str()]["curr"]["investment"]["coin"].SetDouble(userCoins);
    } else {
        Value coinAmountValue;
        coinAmountValue.SetDouble(coinAmount);
        DataBase[user.c_str()]["curr"]["investment"].AddMember("coin", coinAmountValue, DataBase.GetAllocator());
    }

    double clientCurrency = DataBase[client.c_str()]["curr"]["currency"].GetDouble();
    clientCurrency += currency;
    DataBase[user.c_str()]["curr"]["currency"].SetDouble(clientCurrency);

    response = std::to_string(DataBase[user.c_str()]["curr"]["investment"]["coin"].GetDouble()) +
               " " +
               std::to_string(DataBase[user.c_str()]["curr"]["currency"].GetDouble()) + '\n';


    Global["saleCoins"].RemoveMember(client.c_str());

    update(user, DataBase);
    update(client, DataBase);

    return true;
}


bool pushOrder(const std::string& infos, Document& DataBase, Document& Global, const Document& Login, std::string& response){
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    // Ensure that there are at least three parts
    if (parts.size() < 4) {
        return false;
    }

    const std::string user = parts[0];
    const std::string type = parts[1];
    double amount = std::stod(parts[2]);
    const std::string hash_password = parts[3];

    if(Login[user.c_str()]["hash_password"].GetString() != hash_password){
        response = "Wrong password\n";
        return false;
    }
    double currency = DataBase[user.c_str()]["curr"]["currency"].GetDouble();

    if (type == "Purchase") {
        if (amount > currency) {
            response = "Not enough Liquid\n";
            return false;
        }

        if (Global["buyCoins"].HasMember(user.c_str())) {
            response = "Duplicate\n";
            return false;
        } else {
            Global["buyCoins"].AddMember(Value(user.c_str(), DataBase.GetAllocator()).Move(), amount, DataBase.GetAllocator());
            currency -= amount;
            DataBase[user.c_str()]["curr"]["currency"].SetDouble(currency);
        }

    } else if (type == "~Purchase") {
        if (!Global["buyCoins"].HasMember(user.c_str())) {
            response = "Not exist\n";
            return false;
        }
        amount = Global["buyCoins"][user.c_str()].GetDouble();
        Global["buyCoins"].EraseMember(user.c_str());
        currency += amount;
        DataBase[user.c_str()]["curr"]["currency"].SetDouble(currency);
    } else if (type == "Sale") {
        double coins = 0.0;
        if(DataBase[user.c_str()]["curr"]["investment"].HasMember("coin")){
            coins = DataBase[user.c_str()]["curr"]["investment"]["coin"].GetDouble();
        }
        if (amount > coins) {
            response = "Not enough Coins\n";
            return false;
        }
        if (Global["saleCoins"].HasMember(user.c_str())) {
            response = "Duplicate\n";
            return false;
        } else {
            Global["saleCoins"].AddMember(Value(user.c_str(), DataBase.GetAllocator()).Move(), amount, DataBase.GetAllocator());
            coins -= amount;
            if (coins == 0.0){
                DataBase[user.c_str()]["curr"]["investment"].RemoveMember("coin");
            } else {
                DataBase[user.c_str()]["curr"]["investment"]["coin"].SetDouble(coins);
            }
        }
    } else if (type == "~Sale") {
        if (!Global["saleCoins"].HasMember(user.c_str())) {
            response = "Empty\n";
            return false;
        }
        amount = Global["saleCoins"][user.c_str()].GetDouble();
        Global["saleCoins"].EraseMember(user.c_str());

        if(DataBase[user.c_str()]["curr"]["investment"].HasMember("coin")){
            double coins = DataBase[user.c_str()]["curr"]["investment"]["coin"].GetDouble();
            coins += amount;
            DataBase[user.c_str()]["curr"]["investment"]["coin"].SetDouble(coins);
        } else {
            DataBase[user.c_str()]["curr"]["investment"].AddMember("coin", amount, DataBase.GetAllocator());
        }
    }

    update(user, DataBase);
    response = "1\n";

    return true;
}

// Helper function to read JSON data from a file and parse it
bool readAndParseJson(const char* filePath, Document& document) {
    std::ifstream file(filePath);
    if (!file.is_open()) {
        std::cerr << "Error opening file: " << filePath << std::endl;
        return false;
    }

    std::string jsonString((std::istreambuf_iterator<char>(file)), std::istreambuf_iterator<char>());
    document.Parse(jsonString.c_str());

    if (document.HasParseError()) {
        std::cerr << "Error parsing JSON from file: " << filePath << std::endl;
        return false;
    }

    return true;
}

// Helper function to save JSON data to a file with optional indentation
void saveJsonToFile(const char* filePath, const Document& jsonDoc, int indent = 0) {
    StringBuffer buffer;
    PrettyWriter<StringBuffer> writer(buffer);

    if (indent > 0) {
        writer.SetIndent(' ', indent);
    }

    jsonDoc.Accept(writer);

    std::ofstream file(filePath);
    file << buffer.GetString();
    file.close();
    // std::cout << buffer.GetString() << '\n';
}


void HandleClient(SOCKET clientSocket, Document& logIn, Document& dataBase, Document& global) {
    char buffer[1024];
    int bytesRead;

    while (true) {
        bytesRead = recv(clientSocket, buffer, sizeof(buffer) - 1, 0);
        if (bytesRead <= 0) {
            std::cerr << "Client disconnected." << std::endl;
            break;
        }

        buffer[bytesRead] = '\0';
        std::cout << "Received command from client: " << buffer << std::endl;  // Add this line for debugging

        if (strncmp(buffer, "read", strlen("read")) == 0) {
            // Read the data from files and send it to the client
            readAndParseJson("../../StoreJSON/DB.json", dataBase);
            readAndParseJson("../../StoreJSON/Login.json", logIn);
            readAndParseJson("../../StoreJSON/Global.json", global);
            std::cout << "Successfully read in." << std::endl;
        } else if (strncmp(buffer, "save", strlen("save")) == 0) {
            // Save the data to files with indentation
            saveJsonToFile("../../StoreJSON/DB.json", dataBase, 2);
            saveJsonToFile("../../StoreJSON/Login.json", logIn, 2);
            saveJsonToFile("../../StoreJSON/Global.json", global, 2);

            std::cout << "Data saved successfully." << std::endl;
        } else {
            // Process other commands
            std::string command(buffer);
            size_t pos = command.find(':');
            if (pos != std::string::npos) {
                std::string oper = command.substr(0, pos);
                std::string infos = command.substr(pos + 1);
                auto it = operationMap.find(oper);
                if (it != operationMap.end()) {
                    bool result;
                    std::string response;
                    switch (it->second) {
                        case 1:
                            result = createNewAcc(infos, logIn, dataBase);
                            send(clientSocket, (result ? "1\n" : "0\n"), 2, 0);
                            break;
                        case 2:
                            userLogin(infos, logIn, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 3:
                            result = add(infos, dataBase);
                            send(clientSocket, (result ? "1\n" : "0\n"), 2, 0);
                            break;
                        case 4:
                            getBank(infos, dataBase, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 5:
                            processGlobal(global, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 6:
                            sellStock(infos, dataBase, global, logIn, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 7:
                            buyStock(infos, dataBase, global, logIn, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 8:
                            result = applyLoan(infos, dataBase, logIn);
                            send(clientSocket, (result ? "1\n" : "0\n"), 2, 0);
                            break;
                        case 9:
                            transfer(infos, dataBase, logIn, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 10:
                            getCoinVal(global, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 11:
                            sellCoins(infos, dataBase, global, logIn, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 12:
                            buyCoins(infos, dataBase, global, logIn, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        case 13:
                            pushOrder(infos, dataBase, global, logIn, response);
                            send(clientSocket, response.c_str(), static_cast<int>(response.size()), 0);
                            break;
                        default:
                            send(clientSocket, "None\n", 5, 0);
                            break;
                    }
                } else {
                    send(clientSocket, "None\n", 5, 0);
                }
            }
        }
    }
    closesocket(clientSocket);
}


int main() {
    launch = true;
    Document dataBase, logIn, global;

    if (readAndParseJson("../../StoreJSON/DB.json", dataBase) &&
        readAndParseJson("../../StoreJSON/Login.json", logIn) &&
        readAndParseJson("../../StoreJSON/Global.json", global)) {

        std::cout << "Successfully read and parsed JSON data." << std::endl;

    } else {
        std::cerr << "Failed to read or parse JSON data." << std::endl;
    }

    WSADATA wsaData;
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        std::cerr << "Failed to initialize Winsock" << std::endl;
        return -1;
    }

    SOCKET serverSocket = socket(AF_INET, SOCK_STREAM, 0);
    if (serverSocket == INVALID_SOCKET) {
        std::cerr << "Error creating socket" << std::endl;
        WSACleanup();
        return -1;
    }

    // Bind the socket to an address and port
    sockaddr_in serverAddress{};
    serverAddress.sin_family = AF_INET;
    serverAddress.sin_addr.s_addr = INADDR_ANY;
    serverAddress.sin_port = htons(12345);

    if (bind(serverSocket, reinterpret_cast<struct sockaddr *>(&serverAddress), sizeof(serverAddress)) == SOCKET_ERROR) {
        std::cerr << "Error binding socket" << std::endl;
        closesocket(serverSocket);
        WSACleanup();
        return -1;
    }

    // Listen for incoming connections
    if (listen(serverSocket, 10) == SOCKET_ERROR) {
        std::cerr << "Error listening for connections" << std::endl;
        closesocket(serverSocket);
        WSACleanup();
        return -1;
    }

    // Accept connections and handle data
    std::cout << "Server listening on port 12345" << std::endl;

    while (launch) {
        SOCKET clientSocket = accept(serverSocket, nullptr, nullptr);
        if (clientSocket == INVALID_SOCKET) {
            std::cerr << "Error accepting connection" << std::endl;
            continue;
        }

        // Start a new thread to handle the client and store the thread object
        {
            std::lock_guard<std::mutex> lock(launchMutex);  // Lock the mutex
            if (!launch) {
                closesocket(clientSocket);
                break;  // Exit the loop if launch is false
            }
        }

        clientThreads.emplace_back(HandleClient, clientSocket, std::ref(logIn), std::ref(dataBase), std::ref(global));
    }

    for (auto& thread : clientThreads) {
        thread.join();
    }

    // Close the server socket before joining threads
    closesocket(serverSocket);
    WSACleanup();

    return 0;
}