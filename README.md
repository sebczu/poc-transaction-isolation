### **POC-TRANSACTION-ISOLATION**
**spring boot jpa + postgresql**

#### Database schema:
```bash
CREATE TABLE city (id SERIAL PRIMARY KEY, name VARCHAR(255), population INTEGER);
```

#### 1. READ_COMMITTED
**Specification:**

**- can read entity commited in different transaction**

- transaction T1 can read commited entity from different concurrent transaction
```bash
T1 ---(startTransaction)--------------------------------------------------------(readPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
T2 ---(startTransaction)---(addPopulation)---(saveInDB[1] | endTransaction)
```
- transaction T1 not throw any exception when we update this same entity in different thread
```bash
T1 ---(startTransaction)---(readPopulation[0])---------------------------------------------------------(addPopulation)---(saveInDB[1] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```
- transaction T1 can read entity from L1 cache
```bash
T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(readPopulationFromCache[0] | addPopulation)---(saveInDB[1] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```
- transaction T1 can clean cache and fetch again entity from database 
```bash
T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(cleanCacheAndReadPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```
- transaction T1 can read commited entity from different concurrent transaction
```bash
T1 ---(startTransaction)------------------------------------------------(readCities[2] | endTransaction)
T2 ---(startTransaction)---(addCity)---(saveInDB[2] | endTransaction)
```
- transaction T1 not cache entities by query
```bash
T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(readCities[2])---(endTransaction)
T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
```
- transaction T1 update commited entities in different thread
```bash
T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(updateCities[2])---(endTransaction)
T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
```
- transaction T1 update commited entities in different thread
```bash
T1 ---(startTransaction)---(readCities[1])--------------------------------------------------(updateCities[0])---(endTransaction)
T2 ---(startTransaction)---------------------(removeCity)---(saveInDB[0] | endTransaction)
```

#### 2. REPEATABLE_READ
**Specification:**

**- rollback when different transaction change entity**

**- in single transaction we always read this same entities**
```bash
T1 ---(startTransaction)--------------------------------------------------------(readPopulation[1] | addPopulation)---(saveInDB[2] | endTransaction)
T2 ---(startTransaction)---(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[0])---------------------------------------------------------(addPopulation)---(rollbackTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(readPopulationFromCache[0] | addPopulation)---(rollbackTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readPopulation[0])--------------------------------------------------------(cleanCacheAndReadPopulation[1] | addPopulation)---(rollbackTransaction)
T2 ---(startTransaction)----------------------------(addPopulation)---(saveInDB[1] | endTransaction)
```

```bash
T1 ---(startTransaction)------------------------------------------------(readCities[2] | endTransaction)
T2 ---(startTransaction)---(addCity)---(saveInDB[2] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(readCities[1])---(endTransaction)
T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readCities[1])-----------------------------------------------(updateCities[1])---(endTransaction)
T2 ---(startTransaction)---------------------(addCity)---(saveInDB[2] | endTransaction)
```

```bash
T1 ---(startTransaction)---(readCities[1])--------------------------------------------------(updateCities[1])---(rollbackTransaction)
T2 ---(startTransaction)---------------------(removeCity)---(saveInDB[0] | endTransaction)
```

#### 2. SERIALIZABLE
**Specification: (this same as REPEATABLE_READ)**
**- rollback when different transaction change entity**
**- in single transaction we always read this same entities**