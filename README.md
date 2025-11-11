Config Tracker

Config Tracker is an application for logging and storing configuration changes, allowing you to track the history of configuration rules and receive notifications about critical changes.

Main Purpose

The main purpose of the application is to maintain a configuration change log, providing the ability to:

create configuration rules (RuleType);

create and store configuration changes (ConfigChange);

view change history;

receive notifications for critical changes.

The notification system currently simulates external delivery and saves messages in the file logs/notification.log.

Architecture

Controllers: api/config-changes and api/rule-types

Services: business logic for rules and changes

Repositories: in-memory storage

Notifications: NotificationService for critical changes

Exception Handling: global handler MyGlobalExceptionHandler

Technology Stack

Java 21

Spring Boot 3.5.7

Spring Validation for input validation

Actuator for metrics and health check

Main Functionality
Rule Types (/api/rule-types)

POST – create a new rule (e.g., CREDIT_LIMIT with type INTEGER)

GET – get all rules

GET /{id} – get a rule by ID

PUT /{id} – update a rule

DELETE /{id} – delete a rule

Validation:

Spring Validation annotations (@Valid)

Custom Enum for valueType (INTEGER, STRING, BOOLEAN)

Value type validation in service (validateValueType)

Config Changes (/api/config-changes)

POST – create a new configuration for a rule

GET – get all configurations with optional filters:

typeName – rule name (e.g., CREDIT_LIMIT)

from – date after which to search for changes (ISO 8601, e.g., 2025-11-10T00:00:00)

to – date before which to search for changes (ISO 8601, e.g., 2025-11-12T23:59:59)

critical – true or false to filter critical changes

Example filtered request:

GET /api/config-changes?typeName=CREDIT_LIMIT&from=2025-11-10T00:00:00&to=2025-11-12T23:59:59&critical=true


GET /{id} – get a specific configuration by ID

DELETE /{id} – delete a configuration

Features:

Every new configuration is added to the change log

Duplicate value entries are ignored (excluding changedBy)

Critical changes are logged to logs/notification.log

Input validation is performed in the service based on the rule's valueType

Exception Handling

Global Exception Handler: MyGlobalExceptionHandler

Handles:

APIException

ResourceNotFoundException

MethodArgumentNotValidException

HttpMessageNotReadableException

InvalidValueTypeException

IllegalArgumentException

Provides user-friendly error messages, for example when the value type does not match the rule

Health Check & Metrics

Health check: GET /api/health

Metrics (Actuator): GET /metrics

Unit and Integration tests ensure service logic and endpoint correctness

How to Use

Create a rule via POST /api/rule-types

{
"name": "CREDIT_LIMIT",
"valueType": "INTEGER"
}


Create a configuration for the rule via POST /api/config-changes

{
"ruleTypeId": 1,
"currentValue": "5000",
"changedBy": "admin_user",
"critical": false
}


View change history via GET /api/config-changes

Filter changes by rule, date range, or critical flag

Receive notifications for critical changes in the file logs/notification.log