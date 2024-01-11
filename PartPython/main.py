import json
from datetime import datetime, timedelta
from random import randint
from typing import Dict, Any, Tuple, Set, List, Union

# Constants
JSON_FILE_PATH = "../StoreJSON/Global.json"
DATE_FORMAT = '%d/%m/%Y'
DEFAULT_BEGIN_DATE = "01/11/2023"

"""
using `UPPER_CASE` <const>;
using `camelCase` <global or main variables>;
using `snake_case` <function attributs and function names>.
"""

# Define the set of companies with company name, participation date, and participation value.
participationSet: Set[Tuple[str, str, Union[float, int]]] = {
    ('company_a', '01/11/2023', 7.0),
    ('company_b', '10/11/2023', 7.0),
    ('company_c', '30/11/2023', 7.5),
    ('company_d', '20/12/2023', 7.5),
    ('company_e', '30/12/2023', 7.0)
}


def initialize_global_json(file_path: str) -> None:
    """
    Initialize a new global JSON file with default data structure.

    Parameters:
    - file_path: The path to the JSON file.

    Returns:
    - None
    """
    data: Dict[str, Any] = {
        "saleCoins": {},
        "buyCoins": {},
        "coin": 70.0,
        "coinMap": {},
        "dataMap": {},
        "currDataMap": {}
    }

    with open(file_path, 'w') as file:
        json.dump(data, file)


def read_global_json(file_path: str) -> Dict[str, Any]:
    """
    Read data from the global JSON file. If the file doesn't exist, initialize and then read again.

    Parameters:
    - file_path: The path to the JSON file.

    Returns:
    - A dictionary containing global data.
    """
    try:
        with open(file_path, 'r') as file:
            return json.load(file)
    except FileNotFoundError:
        # File doesn't exist, initialize and then read again
        initialize_global_json(file_path)
        with open(file_path, 'r') as file:
            return json.load(file)


def write_global_json(file_path: str, data: Dict[str, Any]) -> None:
    """
    Write data to the global JSON file.

    Parameters:
    - file_path: The path to the JSON file.
    - data: The data to be written.

    Returns:
    - None
    """
    with open(file_path, 'w') as file:
        json.dump(data, file, indent=2)


def parse_date(date_str: str) -> datetime:
    """
    Parse a date string in 'dd/MM/yyyy' format into a datetime object.

    Parameters:
    - date_str: The input date string.

    Returns:
    - A datetime object representing the parsed date.
    """
    return datetime.strptime(date_str, DATE_FORMAT)


def random_by_1000(min_val: int, max_val: int) -> int:
    """
    Generate a random integer between min_val and max_val (inclusive).
    Used for the per mil.

    Parameters:
    - min_val: The minimum value.
    - max_val: The maximum value.

    Returns:
    - A random integer.
    """
    return randint(min_val, max_val)


def update_curr_data(begin: datetime, curr_data_map: Dict[str, float],
                     participation_set: Set[Tuple[str, str, Union[float, int]]]) -> None:
    """
    Update curr_data_map based on companies with dates before 'begin'.

    Parameters:
    - begin: The starting date.
    - curr_data_map: The current data map to be updated.
    - participation_set: Set of tuples containing company information.

    Returns:
    - None
    """
    for company, date_str, value in participation_set:
        value = float(value) if isinstance(value, int) else value
        date: datetime = parse_date(date_str)
        if date < begin and company not in curr_data_map:
            curr_data_map[company]: float = value


def find_begin_date(data_map: Dict[str, Dict[str, float]]) -> datetime:
    """
    Find the latest date in the given data map.

    Parameters:
    - data_map: The data map.

    Returns:
    - A datetime object representing the latest date in the data map.
    """
    if not data_map:
        # If currDataMap is empty, return "01/11/2023"
        return datetime.strptime(DEFAULT_BEGIN_DATE, "%d/%m/%Y")

    # Find the latest date in currDataMap
    date_objects: List[datetime] = [datetime.strptime(date_str, "%d/%m/%Y") for date_str in data_map.keys()]

    return max(date_objects)


def get_end_date() -> datetime:
    """
    Get the current date and time.

    Returns:,
    - A datetime object representing the current date and time.
    """
    return datetime.now()


# Function to perform the main processing based on given rules
def process_data(begin: datetime, end: datetime,
                 coin_map: Dict[str, float],
                 curr_coin: float,
                 data_map: Dict[str, Dict[str, float]],
                 curr_data_map: Dict[str, float],
                 participation_set: Set[Tuple[str, str, Union[float, int]]]) -> None:
    """
    Perform the main processing based on given rules:
        Update the current coin value and current data map with a random per mil each day.
        Use the current values to update coin map and data map.

    Parameters:
    - begin: The starting date.
    - end: The ending date.
    - coin_map: The coin map to be updated.
    - curr_coin: The current coin value.
    - data_map: The data map to be updated.
    - curr_data_map: The current data map.
    - participation_set: Set of tuples containing company information.

    Returns:
    - None
    """
    while begin <= end:
        # Random per_mil for coinMap
        per_mil_coin: int = random_by_1000(950, 1080)
        curr_coin *= per_mil_coin / 1000
        coin_map[begin.strftime(DATE_FORMAT)]: float = curr_coin

        for company, value in curr_data_map.items():
            if value < 5:
                per_mil_data: int = random_by_1000(980, 1120)
            elif 5 <= value < 10:
                per_mil_data: int = random_by_1000(975, 1100)
            elif 10 <= value < 15:
                per_mil_data: int = random_by_1000(970, 1080)
            else:
                per_mil_data: int = random_by_1000(970, 1050)

            curr_data_map[company]: float = value * per_mil_data / 1000

        # Check if company's date is in participation set
        for company, date_str, value in participation_set:
            value = float(value) if isinstance(value, int) else value
            if begin.strftime(DATE_FORMAT) == date_str:
                curr_data_map[company]: float = value

        # Add current data map to dataMap for the current date
        data_map[begin.strftime(DATE_FORMAT)]: Dict[str, float] = curr_data_map

        # Move to the next date
        begin += timedelta(days=1)


if __name__ == "__main__":
    jsonFilePath = JSON_FILE_PATH

    # Initialize or read global data
    globalData: Dict[str, Any] = read_global_json(jsonFilePath)

    # Use `global_data` for data processing
    coinMap: Dict[str, float] = globalData["coinMap"]
    currDataMap: Dict[str, float] = globalData["currDataMap"]
    dataMap: Dict[str, Dict[str, float]] = globalData["dataMap"]
    coin: float = globalData["coin"]

    beginDate: datetime = find_begin_date(dataMap)
    endDate: datetime = get_end_date()

    # Update currDataMap based on participation set
    update_curr_data(beginDate, currDataMap, participationSet)

    # Perform the main processing
    process_data(beginDate, endDate, coinMap, coin, dataMap, currDataMap, participationSet)

    # Save the modified data back to the file
    write_global_json(jsonFilePath, globalData)
