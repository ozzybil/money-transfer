# MoneyTransfer

Standalone REST api for money transfer between accounts

Features
---

1. Creating accounts
1. Deposit/withdraw money of accounts
1. Transferring money from one account to another
1. Retrieving all accounts and transactions occurred
1. Standalone executable
1. In-memory data store

How to start the MoneyTransfer application
---

1. Run `mvn clean install` to build your application
1. Change working directory to application local directory
1. Start application with `java -jar target/money-transfer-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8090`

Qucik start for the MoneyTransfer application
---

1. Download money-transfer-1.0-SNAPSHOT.jar and config.yml
1. Start application by the following command: `java -jar money-transfer-1.0-SNAPSHOT.jar server config.yml`

Tech Stack
---

1. Java 8
1. Maven
1. Dropwizard
1. Mockito
1. JUnit

Endpoints
---

| Endpoint              | Method | Payload                             | Description                              | Return                                                             |
|-----------------------|--------|-------------------------------------|------------------------------------------|--------------------------------------------------------------------|
| /account/all          | GET    |                                     | Get all the accounts                     | List of existing accounts                                          |
| /account/id/{id}      | GET    |                                     | Get the account with specified {id}      | An account if found or 404 NOT_FOUND                               |
| /account/save         | POST   | id, balance                         |                                          | Payload back or 400 BAD_REQUEST if something wrong                 |
| /transaction/all      | GET    |                                     | Get all transactions happened so far     | List of transactions                                               |
| /transaction/deposit  | POST   | toAccountId, amount                 | Request for a deposit transaction        | Payload back filled with transactionId, transactionType or 400/404 |
| /transaction/withdraw | POST   | fromAccountId, amount               | Request for a withdraw transaction       | Payload back filled with transactionId, transactionType or 400/404 |
| /transaction/transfer | POST   | fromAccountId, toAccountId, amount  | Request for a transfer transaction       | Payload back filled with transactionId, transactionType or 400/404 |
| /test                 | GET    |                                     | Trigger pre-defined test scenario        | List of existing accounts                                          |

Pre-defined Test Scenario
---
1. Two accounts created; acc_1 with 10 amount of money, acc_2 with 20
1. Deposit 1 box to acc_1 and withdrawn 2 from acc_2
1. Transferred 3 amount of money from acc_1 to acc_2
1. Returned accounts [{"id":"acc_1","balance":8},{"id":"acc_2","balance":21}]

In order to run the scenario call this endpoint: http://localhost:8090/test

Some examples
---

## Creating an account
```
    POST localhost:8090/account/save
    {
        "id": "acc_1",
        "balance": "500"
    }
```
## Transfer 10 boxes from acc_1 to acc_2
```
    POST localhost:8090/transaction/transfer
    {
        "fromAccountId": "acc_1",
        "toAccountId": "acc_2",
        "amount": "10"
    }
```
Response:
```
    HTTP 200 OK
    {
        "id": "trx_4",
        "transactionType": "transfer",
        "fromAccountId": "acc_1",
        "toAccountId": "acc_2",
        "amount": 10
    }
```
