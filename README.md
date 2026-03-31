# ⚖️ Quantity Measurement App  

A **Java-based application** built using **Data-Driven Testing (DDT)** that evolves step-by-step — from a basic unit comparison utility to a fully functional **Spring Boot REST API** with database integration and security.

---

## 🚀 Tech Stack  

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-6DB33F?style=for-the-badge)  
![H2](https://img.shields.io/badge/H2-Database-1F4E79?style=for-the-badge)  
![JPA](https://img.shields.io/badge/Spring%20Data-JPA-4CAF50?style=for-the-badge)  
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge)  
![JUnit](https://img.shields.io/badge/JUnit-5-F7DF1E?style=for-the-badge)  

---

## 📖 Project Evolution  

This project follows a **progressive development approach**, where each use case builds upon the previous one — transforming a simple utility into a **production-ready backend system**.

---

## 🔵 Phase 1 — Length Measurement *(UC1–UC7)*  

| UC | Branch | Description |
|----|--------|-------------|
| UC1 | [`feature/UC1-FeetEquality`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC1-FeetEquality) | Validate equality between two feet values |
| UC2 | [`feature/UC2-InchEquality`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC2-InchEquality) | Validate equality between two inch values |
| UC3 | [`feature/UC3-GenericLength`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC3-GenericLength) | Introduced `LengthUnit` enum with conversion logic |
| UC4 | [`feature/UC4-YardEquality`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC4-YardEquality) | Added `YARDS` unit (1 yard = 3 feet = 36 inches) |
| UC5 | [`feature/UC5-UnitConversion`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC5-UnitConversion) | Enabled unit-to-unit conversion |
| UC6 | [`feature/UC6-UnitAddition`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC6-UnitAddition) | Added support for adding different units |
| UC7 | [`feature/UC7-TargetUnitAddition`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC7-TargetUnitAddition) | Result returned in first operand’s unit |

---

## 🟣 Phase 2 — Generic Units *(UC8–UC12)*  

| UC | Branch | Description |
|----|--------|-------------|
| UC8 | [`feature/UC8-StandaloneUnit`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC8-StandaloneUnit) | Introduced `IMeasurable` for abstraction |
| UC9 | [`feature/UC9-WeightMeasurement`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC9-WeightMeasurement) | Added weight units (KG, Gram, Pound) |
| UC10 | [`feature/UC10-GenericQuantityClass`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC10-GenericQuantityClass) | Implemented generic `Quantity<U>` |
| UC11 | [`feature/UC11-VolumeMeasurement`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC11-VolumeMeasurement) | Added volume units (Litre, mL, Gallon) |
| UC12 | [`feature/UC12-MoreOperations`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC12-MoreOperations) | Added `subtract()` and `divide()` |

---

## 🟠 Phase 3 — System Architecture *(UC13–UC15)*  

| UC | Branch | Description |
|----|--------|-------------|
| UC13 | [`feature/UC13-Centralized-Arithmetic-Logic`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC13-Centralized-Arithmetic-Logic) | Centralized arithmetic using enum |
| UC14 | [`feature/UC14-TemperatureMeasurement`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC14-TemperatureMeasurement) | Added temperature with non-linear conversion |
| UC15 | [`feature/UC15-N-Tier`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC15-NTierArchitecture) | Implemented N-Tier architecture |

---

## 🟡 Phase 4 — Database Integration *(UC16)*  

| UC | Branch | Description |
|----|--------|-------------|
| UC16 | [`feature/UC16-Database-Integration`](https://github.com/anuj266/QuantityMeasurementApp/tree/feature/UC16-Database-Integration) | Integrated H2 DB using JDBC |

### 🔑 Key Learnings  
- JDBC connection handling  
- Connection pooling  
- SQL injection prevention (Parameterized Queries)  
- External configuration via `application.properties`  
- Custom exception handling  

---

## ⚙️ How to Run  

### ✅ Prerequisites  
- Java 17+  
- Maven 3.6+  

### ▶️ Steps  

```bash
# Clone repository
git clone https://github.com/anuj266/QuantityMeasurementApp.git

# Navigate into project
cd QuantityMeasurementApp

# Run application
mvn spring-boot:run