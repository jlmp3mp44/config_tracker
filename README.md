

# Config Tracker

Config Tracker is an application for logging and storing configuration changes, allowing you to track the history of configuration rules and receive notifications about critical changes.

---

## Main Purpose

The main purpose of the application is to maintain a configuration change log, providing the ability to:

* Create configuration rules (RuleType)
* Create and store configuration changes (ConfigChange)
* View change history
* Receive notifications for critical changes

The notification system currently simulates external delivery and saves messages in the file:

```
logs/notification.log
```

---

## Architecture

* **Controllers:** `api/config-changes`, `api/rule-types`
* **Services:** business logic for rules and changes
* **Repositories:** in-memory storage
* **Notifications:** `NotificationService` for critical changes
* **Exception Handling:** global handler `MyGlobalExceptionHandler`
* **Request Tracing:** correlation ID is added for each request to trace logs across services

---

## Technology Stack

* Java 21
* Spring Boot 3.5.7
* Spring Validation for input validation
* Actuator for metrics and health check

---

## Main Functionality

### Rule Types (`/api/rule-types`)

**Endpoints:**

* `POST` – create a new rule (e.g., CREDIT_LIMIT with type INTEGER)
* `GET` – get all rules
* `GET /{id}` – get a rule by ID
* `PUT /{id}` – update a rule
* `DELETE /{id}` – delete a rule

**Validation:**

* Spring Validation annotations (`@Valid`)
* Custom enum for `valueType` (INTEGER, STRING, BOOLEAN)
* Additional validation logic in service (`validateValueType`)

---

### Config Changes (`/api/config-changes`)

**Endpoints:**

* `POST` – create a new configuration for a rule
* `GET` – get all configurations with optional filters
* `GET /{id}` – get a specific configuration by ID
* `DELETE /{id}` – delete a configuration

**Available filters:**

* `typeName` – rule name (e.g., CREDIT_LIMIT)
* `from` – date after which to search (ISO 8601)
* `to` – date before which to search (ISO 8601)

**Example filtered request:**

```
GET /api/config-changes?typeName=CREDIT_LIMIT&from=2025-11-10T00:00:00&to=2025-11-12T23:59:59&critical=true
```

**Features:**

* Every new configuration is added to the change log
* Duplicate values (except `changedBy`) are ignored
* Critical changes are logged to `logs/notification.log`
* Input validation performed in the service based on `RuleType.valueType`
* Correlation ID is included in logs for tracing requests

---

## Exception Handling

**Global Exception Handler:** `MyGlobalExceptionHandler`

**Handles:**

* `APIException`
* `ResourceNotFoundException`
* `MethodArgumentNotValidException`
* `HttpMessageNotReadableException`
* `InvalidValueTypeException`
* `IllegalArgumentException`

Provides user-friendly error messages, for example when a value type does not match the rule definition.

---

## Health Check and Metrics

* Health check: `GET /api/health`
* Metrics (Actuator): `GET /metrics`

Unit and integration tests ensure service logic and endpoint correctness.

---

## How to Use

### 1. Create a Rule

```
POST /api/rule-types
```

Example body:

```json
{
  "name": "CREDIT_LIMIT",
  "valueType": "INTEGER"
}
```

### 2. Create a Configuration for the Rule

```
POST /api/config-changes
```

Example body:

```json
{
  "ruleTypeId": 1,
  "currentValue": "5000",
  "changedBy": "admin_user",
  "critical": false
}
```

### 3. View Change History

```
GET /api/config-changes
```

Filter changes by rule name or date range

Notifications for critical changes will appear in:

```
logs/notification.log
```

---

## Postman Collection

A ready-to-use **Postman collection** is available in the project at:

```
postman/ConfigTracker.postman_collection.json
```

You can import it in Postman to easily test all endpoints, including filtering and creating configurations.

---

Якщо хочеш, я можу ще зробити **швидку секцію прикладу запитів з кореляційним ID**, щоб було видно, як його використовувати при тестуванні через Postman.

Хочеш, щоб я це додала?
