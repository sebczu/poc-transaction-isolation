### **POC-TRANSACTION**
**spring boot jpa + postgresql**

#### 1. Read commited
TransactionReadCommited(start transaction) -> SecondTransaction(add population) -> TransactionReadCommited(read commited population)
```bash
T1 ---(startTransaction)--------------------------------------------------------(readPopulation[2100] | addPopulation)---(saveInDB[2200] | endTransaction)
T2 ---(startTransaction)---(addPopulation)---(saveInDB[2100] | endTransaction)
```
