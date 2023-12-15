#include <iostream>
#include <fstream>
#include <rapidjson/document.h>
#include <rapidjson/writer.h>
#include <rapidjson/stringbuffer.h>
#include <winsock2.h>
#include <thread>
#include <vector>
#include <sstream>
#include <ctime>
#include <iomanip>
#include <mutex>
#include <cstring>

using namespace rapidjson;
std::string curr_user;
bool launch;
std::vector<std::thread> clientThreads;
std::mutex launchMutex;

bool createNewAcc(const std::string& infos, Document& Login, Document& DataBase) {
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss},
                                   std::istream_iterator<std::string>());

    const std::string& user = parts[0];
    if (Login.HasMember(user.c_str())) {
        return false;  // L'utilisateur existe déjà
    }

    // Ajouter l'utilisateur à Login
    Value userObject(kObjectType);
    userObject.AddMember("hash_password", Value(parts[1].c_str(), Login.GetAllocator()), Login.GetAllocator());
    userObject.AddMember("first_name", Value(parts[2].c_str(), Login.GetAllocator()), Login.GetAllocator());
    userObject.AddMember("family_name", Value(parts[3].c_str(), Login.GetAllocator()), Login.GetAllocator());
    userObject.AddMember("telephone", Value(parts[4].c_str(), Login.GetAllocator()), Login.GetAllocator());

    std::string e_mail = parts.back();
    userObject.AddMember("e_mail", Value(e_mail.c_str(), Login.GetAllocator()), Login.GetAllocator());

    Value key(user.c_str(), Login.GetAllocator());
    Login.AddMember(key, userObject, Login.GetAllocator());

    // Ajouter l'utilisateur à DataBase avec la date actuelle
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    std::ostringstream dateStream;
    dateStream << std::put_time(localTime, "%d/%m/%Y");
    std::string currentDate = dateStream.str();

    if (!DataBase.HasMember(user.c_str())) {
        Value userDatabaseObject(kObjectType);
        Value currObject(kObjectType);
        userDatabaseObject.AddMember("currency", 0.0, DataBase.GetAllocator());
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


bool userLogin(const std::string& login_infos, Document& Login) {
    // Split login_infos with space as delimiter
    std::istringstream iss(login_infos);
    std::vector<std::string> loginParts(std::istream_iterator<std::string>{iss},
                                        std::istream_iterator<std::string>());

    if (loginParts.size() < 2) {
        // Insufficient login information
        return false;
    }

    const std::string& user = loginParts[0];
    const std::string& hash_password = loginParts[1];

    auto userIter = Login.FindMember(StringRef(user.c_str()));

    if (userIter != Login.MemberEnd() && userIter->value.HasMember("hash_password")) {
        const Value& storedPassword = userIter->value["hash_password"];

        if (storedPassword.IsString() && storedPassword.GetString() == hash_password) {
            // Valid login
            curr_user = user;
            return true;
        }
    }

    // Invalid login
    return false;
}


bool find_user(const std::string& user, Document& DataBase){
    if (!DataBase.HasMember(user.c_str()) || !DataBase[user.c_str()].HasMember("curr")) {
        return false;
    }
    return true;
}


bool addCurrency(const std::string& infos, Document& DataBase) {
    // Split infos with space as delimiter
    std::istringstream iss(infos);
    std::vector<std::string> parts(std::istream_iterator<std::string>{iss}, std::istream_iterator<std::string>());

    if (parts.size() < 2) {
        // Insufficient information
        return false;
    }

    const std::string& user = parts[0];
    const std::string& amountStr = parts[1];

    // Convert amount to float
    float amount;
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

    // Get the current date
    std::time_t now = std::time(nullptr);
    std::tm* localTime = std::localtime(&now);
    std::ostringstream dateStream;
    dateStream << std::put_time(localTime, "%d/%m/%Y");
    std::string currentDate = dateStream.str();

    // Check if the user has the "curr" entry
    if (!DataBase[user.c_str()].HasMember("curr")) {
        return false;
    }

    // Copy the "curr" entry to the current date
    Value& currObject = DataBase[user.c_str()]["curr"];  // Use 'user' instead of 'curr_user'
    currObject["currency"] = currObject["currency"].GetFloat() + amount;

    // Create a new object for the current date
    Value currentDateObject(kObjectType);
    currentDateObject.CopyFrom(currObject, DataBase.GetAllocator());

    // Add the new object to the user's data under the current date
    const Value& userObject = DataBase[user.c_str()];
    if (userObject.HasMember(currentDate.c_str())) {
        // Update the existing entry for the current date
        Value& currentDateObject = DataBase[user.c_str()][currentDate.c_str()];
        currentDateObject["currency"] = currentDateObject["currency"].GetFloat() + amount;
    } else {
        // Create a new object for the current date
        Value currentDateObject(kObjectType);
        currentDateObject.AddMember("currency", amount, DataBase.GetAllocator());

        // Add the new object to the user's data under the current date
        DataBase[user.c_str()].AddMember(Value(currentDate.c_str(), DataBase.GetAllocator()), currentDateObject, DataBase.GetAllocator());
    }
    return true;
}


void HandleClient(SOCKET clientSocket, Document& logIn, Document& dataBase) {
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

        if (strncmp(buffer, "exit", strlen("exit")) == 0) {
            std::cout << "Received exit command. Closing connection." << std::endl;
            StringBuffer bufferDB;
            Writer<StringBuffer> writerDB(bufferDB);
            dataBase.Accept(writerDB);

            StringBuffer bufferLOGIN;
            Writer<StringBuffer> writerLOGIN(bufferLOGIN);
            logIn.Accept(writerLOGIN);

            std::ofstream fileDB("../../StoreJSON/DB.json");
            fileDB << bufferDB.GetString();
            fileDB.close();
            std::cout <<  bufferDB.GetString() <<'\n';

            std::ofstream fileLOGIN("../../StoreJSON/Login.json");
            fileLOGIN << bufferLOGIN.GetString();
            fileLOGIN.close();
            std::cout <<  bufferLOGIN.GetString() <<'\n';
            std::lock_guard<std::mutex> lock(launchMutex);  // Lock the mutex
            launch = false;
            std::this_thread::sleep_for(std::chrono::milliseconds(1000)); // Adjust the delay as needed
            break;
        } else {
            // Process other commands
            std::string command(buffer);
            size_t pos = command.find(':');
            if (pos != std::string::npos) {
                std::string oper = command.substr(0, pos);
                std::string infos = command.substr(pos + 1);
                std::cout << (command.substr(0, pos) == "addCurrency")<< std::endl;

                if (oper == "createNewAcc") {
                    bool result = createNewAcc(infos, logIn, dataBase);
                    send(clientSocket, (result ? "1\n" : "0\n"), 2, 0);
                } else if (oper == "userLogin") {
                    bool result = userLogin(infos, logIn);
                    if (result) {
                        std::string response;
                        auto userIterator = logIn.FindMember(curr_user.c_str());

                        if (userIterator != logIn.MemberEnd()) {
                            const Value &user = userIterator->value;

                            if (user.HasMember("first_name") && user["first_name"].IsString() &&
                                user.HasMember("family_name") && user["family_name"].IsString() &&
                                user.HasMember("telephone") && user["telephone"].IsString() &&
                                user.HasMember("e_mail") && user["e_mail"].IsString()) {

                                response = std::string(user["first_name"].GetString()) + " " +
                                           std::string(user["family_name"].GetString()) + " " +
                                           std::string(user["telephone"].GetString()) + " " +
                                           std::string(user["e_mail"].GetString()) + "\n";
                            } else {
                                response = "Invalid user data\n";
                            }
                        } else {
                            response = "User not found\n";
                        }

                        send(clientSocket, response.c_str(), response.size(), 0);

                    }
                } else if (oper == "addCurrency") {
                    std::cout << "Received addCurrency command" << std::endl;
                    bool result = addCurrency(infos, dataBase);
                    send(clientSocket, (result ? "1\n" : "0\n"), 2, 0);
                }

            }
        }
    }

    closesocket(clientSocket);
}


int main() {
    launch = true;
    // Read JSON from a fileDB
    std::ifstream readFileDB("../../StoreJSON/DB.json");
    std::string jsonStrDB((std::istreambuf_iterator<char>(readFileDB)), std::istreambuf_iterator<char>());

    std::ifstream readFileLOGIN("../../StoreJSON/Login.json");
    std::string jsonStrLOGIN((std::istreambuf_iterator<char>(readFileLOGIN)), std::istreambuf_iterator<char>());

    // Parse JSON
    Document dataBase;
    dataBase.Parse(jsonStrDB.c_str());

    Document logIn;
    logIn.Parse(jsonStrLOGIN.c_str());

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

        clientThreads.emplace_back(HandleClient, clientSocket, std::ref(logIn), std::ref(dataBase));
    }

    for (auto& thread : clientThreads) {
        thread.join();
    }

    // Close the server socket before joining threads
    closesocket(serverSocket);
    WSACleanup();

    return 0;
}