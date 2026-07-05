# Pulseline — AI Health Smart Assistant (Spring Boot + MySQL + Gemini AI)

A full-stack health tracking app:

```
├── Frontend  → src/main/resources/static (HTML/CSS/JS, served by Spring Boot)
├── Backend   → src/main/java/com/pulseline/health (Spring Boot REST API)
├── Database  → MySQL (JPA/Hibernate auto-creates tables)
└── AI        → Google Gemini (symptom guidance, with a local emergency-keyword filter in front of it)
```

> **Note on this build:** this project was generated in a sandboxed environment with no
> internet access, so it has **not** been compiled or run here. The code follows standard
> Spring Boot 3.2 / Java 17 conventions, but run `mvn compile` locally first and fix any
> version-specific issues before relying on it.

## 1. Prerequisites

- JDK 17+
- Maven 3.8+ (or use the wrapper if you generate one with `mvn -N wrapper:wrapper`)
- MySQL 8+ running locally (or reachable over the network)
- A Gemini API key from https://aistudio.google.com/app/apikey

## 2. Configure the database

```sql
CREATE DATABASE ai_health_assistant;
```

The app also has `createDatabaseIfNotExist=true` in the JDBC URL, so it will create it
automatically the first time it connects, as long as the MySQL user has permission.

## 3. Configure `src/main/resources/application.properties`

Edit these three lines:

```properties
spring.datasource.username=root
spring.datasource.password=your_mysql_password
gemini.api.key=YOUR_GEMINI_API_KEY_HERE
```

`spring.jpa.hibernate.ddl-auto=update` means Hibernate creates/updates the four tables
(`vitals`, `medications`, `water_logs`, `symptom_logs`) automatically on startup — no manual
schema file needed.

## 4. Run it

```bash
mvn spring-boot:run
```

Then open **http://localhost:8080** — the same Spring Boot app serves both the frontend
(static files) and the REST API, so there's no separate frontend server or CORS setup needed
for local use.

## 5. API overview

| Method | Path                          | Purpose                                   |
|--------|-------------------------------|--------------------------------------------|
| POST   | `/api/symptom-check`          | `{ "message": "..." }` → Gemini-backed reply |
| GET    | `/api/vitals`                 | List logged vitals, newest first           |
| POST   | `/api/vitals`                 | `{ "type": "bp\|hr\|temp\|spo2", "value": 120 }` |
| GET    | `/api/medications`            | List medications                           |
| POST   | `/api/medications`            | `{ "name": "...", "time": "8:00 AM" }`     |
| PUT    | `/api/medications/{id}/toggle`| Toggle taken/not taken                     |
| GET    | `/api/water/today`            | Today's water log                          |
| POST   | `/api/water/increment`        | +1 cup (caps at 8)                         |
| POST   | `/api/water/reset`            | Reset today's cups to 0                    |

## 6. How the symptom assistant stays safe

`GeminiService` checks the message against a small list of emergency keywords (chest pain,
can't breathe, stroke signs, etc.) **before** calling Gemini. If matched, it returns a
hard-coded "contact emergency services" message instantly — it never depends on the model to
catch the most dangerous cases. Otherwise it calls Gemini with a system prompt that restricts
it to general, non-diagnostic wellness guidance and always closes by pointing to a clinician.
If the Gemini call fails (bad key, no network, quota), the service falls back to a generic
safe response instead of erroring out to the user.

## 7. Next steps you may want

- Swap `ddl-auto=update` for a versioned migration tool (Flyway/Liquibase) before production use.
- Add authentication (Spring Security) if this will ever hold more than one person's data.
- Add per-user scoping to vitals/medications/water tables (currently single-user/global).
