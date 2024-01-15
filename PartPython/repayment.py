<<<<<<< HEAD
import json
from datetime import datetime, timedelta
from time import sleep
import socket
from typing import Dict, Any

# Constants
JSON_FILE_PATH = "../StoreJSON/DB.json"  # Updated path
DEBT_INTEREST_RATE = 0.0025


def read_data_json(file_path: str) -> Dict[str, Dict[str, Any]]:
    """
    Read data from the JSON file.

    Parameters:
    - file_path: The path to the JSON file.

    Returns:
    - A dictionary containing the database.
    """
    with open(file_path, 'r') as file:
        return json.load(file)


def write_data_json(file_path: str, data: Dict[str, Dict[str, Any]]) -> None:
    """
    Write data to the JSON file.

    Parameters:
    - file_path: The path to the JSON file.
    - data: The data to be written.

    Returns:
    - None
    """
    with open(file_path, 'w') as file:
        json.dump(data, file, indent=2)


def process_repayment(database: Dict[str, Dict[str, Any]]) -> None:
    """
    Process repayment for each entry in the database.

    Parameters:
    - database: The database containing repayment information.

    Returns:
    - None
    """
    today_date_str = datetime.now().strftime('%d/%m/%Y')

    for name, entry in database.items():
        curr_entry = entry.get("curr", {})
        debt = curr_entry.get("debt", 0.0)
        currency = curr_entry.get("currency", 0.0)

        if debt < 0:
            if debt + currency >= 0:
                curr_entry["debt"] = 0
                curr_entry["currency"] += debt
            else:
                curr_entry["debt"] += currency
                curr_entry["currency"] = 0
                curr_entry["debt"] *= (1 + DEBT_INTEREST_RATE)

            # Update the database with today's date
            entry[today_date_str] = curr_entry.copy()


def main() -> int:
    jsonFilePath = JSON_FILE_PATH

    # Create a socket connection
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.connect(('localhost', 12345))

    # Send "save" to the socket, to update the local base with current server data
    server_socket.sendall(b'save')

    sleep(2)

    try:
        # Read global data as the database
        database: Dict[str, Dict[str, Any]] = read_data_json(jsonFilePath)

        # Perform repayment processing
        process_repayment(database)

        # Write the updated data back to the JSON file
        write_data_json(jsonFilePath, database)

        sleep(2)

        # Send "read" to the socket, to update the server data with the current local base
        server_socket.sendall(b'read')

        # Close the socket connection
        server_socket.close()

        return 0  # Success
    except Exception as e:
        print(f"Error: {e}")
        return 1  # Failure


if __name__ == "__main__":
    exit_code = main()
    print(f"Exit Code: {exit_code}")
=======
import json
from datetime import datetime, timedelta
from time import sleep
import socket
from typing import Dict, Any

# Constants
JSON_FILE_PATH = "../StoreJSON/DB.json"  # Updated path
DEBT_INTEREST_RATE = 0.0025


def read_data_json(file_path: str) -> Dict[str, Dict[str, Any]]:
    """
    Read data from the JSON file.

    Parameters:
    - file_path: The path to the JSON file.

    Returns:
    - A dictionary containing the database.
    """
    with open(file_path, 'r') as file:
        return json.load(file)


def write_data_json(file_path: str, data: Dict[str, Dict[str, Any]]) -> None:
    """
    Write data to the JSON file.

    Parameters:
    - file_path: The path to the JSON file.
    - data: The data to be written.

    Returns:
    - None
    """
    with open(file_path, 'w') as file:
        json.dump(data, file, indent=2)


def process_repayment(database: Dict[str, Dict[str, Any]]) -> None:
    """
    Process repayment for each entry in the database.

    Parameters:
    - database: The database containing repayment information.

    Returns:
    - None
    """
    today_date_str = datetime.now().strftime('%d/%m/%Y')

    for name, entry in database.items():
        curr_entry = entry.get("curr", {})
        debt = curr_entry.get("debt", 0.0)
        currency = curr_entry.get("currency", 0.0)

        if debt < 0:
            if debt + currency >= 0:
                curr_entry["debt"] = 0
                curr_entry["currency"] += debt
            else:
                curr_entry["debt"] += currency
                curr_entry["currency"] = 0
                curr_entry["debt"] *= (1 + DEBT_INTEREST_RATE)

            # Update the database with today's date
            entry[today_date_str] = curr_entry.copy()


def main() -> int:
    jsonFilePath = JSON_FILE_PATH

    # Create a socket connection
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.connect(('localhost', 12345))

    # Send "save" to the socket, to update the local base with current server data
    server_socket.sendall(b'save')

    sleep(2)

    try:
        # Read global data as the database
        database: Dict[str, Dict[str, Any]] = read_data_json(jsonFilePath)

        # Perform repayment processing
        process_repayment(database)

        # Write the updated data back to the JSON file
        write_data_json(jsonFilePath, database)

        sleep(2)

        # Send "read" to the socket, to update the server data with the current local base
        server_socket.sendall(b'read')

        # Close the socket connection
        server_socket.close()

        return 0  # Success
    except Exception as e:
        print(f"Error: {e}")
        return 1  # Failure


if __name__ == "__main__":
    exit_code = main()
    print(f"Exit Code: {exit_code}")
>>>>>>> 3605e3cbd079cc79bf6646b98f909de800165aca
