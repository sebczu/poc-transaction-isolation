### **POC-TRANSACTION**
**spring boot jpa + postgresql**

#### Database schema:
```bash
CREATE TABLE city (id SERIAL PRIMARY KEY, name VARCHAR(255), population INTEGER);
```

#### 1. Read commited
Transaction can read commited entity from different concurrent transaction
```bash
T1 ---(startTransaction)--------------------------------------------------------(readPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
T2 ---(startTransaction)---(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[0])---------------------------------------------------------(addPopulation)---(saveInDB[1] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(readPopulationFromCache[0] | addPopulation)---(saveInDB[1] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(cleanCacheAndReadPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)------------------------------------------------(readCities[2] | endTransaction)
T2 ---(startTransaction)---(addCity)---(saveInDB[2] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(readCities[2])---(endTransaction)
T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
```
