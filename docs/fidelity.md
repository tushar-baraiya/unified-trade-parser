# Fidelity Transaction Parser Documentation

## Supported File Format
The Fidelity transaction parser supports CSV files with the following headers:
- **Run Date**: The date of the transaction (e.g., `2023-01-01`).
- **Symbol**: The ticker symbol of the security (e.g., `AAPL`).
- **Description**: A description of the transaction.
- **Type**: The transaction type (e.g., `Buy`, `Sell`).
- **Price**: The price per unit of the security.
- **Quantity Currency**: The quantity traded.
- **Amount**: The total amount of the transaction.
- **Commission**: The commission charged (optional).
- **Fees**: Additional fees (optional).
- **Settlement Date**: The date the transaction was settled.

## Parsing Logic
1. Reads the CSV file using Apache Commons CSV.
2. Maps each row to a `StockTransaction` object.
3. Handles missing optional fields (e.g., `Commission`, `Fees`) by setting default values.

## Example Input
```csv
Run Date,Symbol,Description,Type,Price,Quantity Currency,Amount,Commission,Fees,Settlement Date
2023-01-01,AAPL,Apple Inc,Buy,145.67,10,1456.70,1.0,0.5,2023-01-03
