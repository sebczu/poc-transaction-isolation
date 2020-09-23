### **POC-TRANSACTION**
**spring boot jpa + postgresql**

#### Database schema:
```bash
CREATE TABLE city (id SERIAL PRIMARY KEY, name VARCHAR(255), population INTEGER);
```

#### 1. Read commited
Transaction can read commited entity from different concurrent transaction
```bash
T1 ---(startTransaction)--------------------------------------------------------(readPopulation[2100] | addPopulation)---(saveInDB[2200] | endTransaction)
T2 ---(startTransaction)---(addPopulation)---(saveInDB[2100] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[2000])---------------------------------------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[2000])--------------------------------------------------------(readPopulationFromCache[2000] | addPopulation)---(saveInDB[2100] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[2000])--------------------------------------------------------(cleanCacheAndReadPopulation[21000] | addPopulation)---(saveInDB[2200] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[2100] | endTransaction)
```

```bash
T1 ---(startTransaction)----------------------------(readCities)---(endTransaction)
T2 ---(startTransaction)---(addCity)---(saveInDB)
```

