# BlackRock Challenge API — Java Spring Boot (Docker + Compose)

This application implements the REST API contract described in the BlackRock Challenge PDF:
- transaction parsing (round-up savings),
- transaction validation,
- temporal rule application (q/p/k),
- NPS & Index returns calculation,
- performance reporting.

The service listens on **port 5477** (required).

---

## Tech Stack

- Java 25+
- Spring Boot (Web + Validation + Actuator)
- Maven
- Docker / Docker Compose
- Works with Docker Desktop or **Colima** (macOS)

---

## Required Endpoints

Base path: `/blackrock/challenge/v1`

### Transactions
- `POST /transactions:parse`
- `POST /transactions:validator`
- `POST /transactions:filter`

### Returns
- `POST /returns:nps`
- `POST /returns:index`

### Performance
- `GET /performance`

---

## Local Build (Maven)

From repo root:

```bash
mvn clean install
````
This produces the runnable jar in target/.


Docker Build (Image)

Build the image (name must follow challenge convention):

```docker build -t blk-hacking-ind-aman-mishra .```

Run via Docker Compose (Recommended)

Start:

```docker-compose -f compose.yaml up```
OR
```docker-compose -f compose.yaml up```

Stop:

```docker-compose -f compose.yaml down```
OR
```docker-compose -f compose.yaml down```


Verify Service is Up

Health (Actuator)

```curl -v http://localhost:5477/actuator/health```

Performance

```curl -v http://localhost:5477/blackrock/challenge/v1/performance```


Example Requests

1) Parse Transactions (transactions:parse)

Request is a JSON array of expenses.

```
curl -X POST "http://127.0.0.1:5477/blackrock/challenge/v1/transactions:parse" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '[
    { "date": "2023-10-12 20:15:30", "amount": 250.0 },
    { "date": "2023-02-28 15:49:20", "amount": 375.0 },
    { "date": "2023-07-01 21:59:00", "amount": 620.0 },
    { "date": "2023-12-17 08:09:45", "amount": 480.0 }
  ]
```
2) Validate Transactions (transactions:validator)

Returns:
	•	valid[]
	•	invalid[] with a descriptive message
```
curl -X POST "http://127.0.0.1:5477/blackrock/challenge/v1/transactions:validator" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "wage": 600000.0,
    "transactions": [
      { "date": "2023-10-12 20:15:30", "amount": 250.0, "ceiling": 300.0, "remanent": 50.0 },
      { "date": "2023-02-28 15:49:20", "amount": -100.0, "ceiling": 100.0, "remanent": 0.0 }
    ]
  }'
```
3) Apply Temporal Rules (transactions:filter)
	•	q: override remanent with fixed (latest start wins if multiple)
	•	p: add extra (sum all matching)
	•	k: windows used later for returns (validated here too)

```
curl -X POST "http://127.0.0.1:5477/blackrock/challenge/v1/transactions:filter" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "q": [
      { "fixed": 0.0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" }
    ],
    "p": [
      { "extra": 25.0, "start": "2023-10-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "k": [
      { "start": "2023-03-01 00:00:00", "end": "2023-11-30 23:59:59" },
      { "start": "2023-01-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "transactions": [
      { "date": "2023-10-12 20:15:30", "amount": 250.0, "ceiling": 300.0, "remanent": 50.0 },
      { "date": "2023-02-28 15:49:20", "amount": 375.0, "ceiling": 400.0, "remanent": 25.0 },
      { "date": "2023-07-01 21:59:00", "amount": 620.0, "ceiling": 700.0, "remanent": 80.0 },
      { "date": "2023-12-17 08:09:45", "amount": 480.0, "ceiling": 500.0, "remanent": 20.0 }
    ]
  }
```
4) NPS Returns (returns:nps)

Calculations:
	•	Apply q then p on remanent
	•	For each k window: sum remanent
	•	Compute NPS compounding + inflation adjustment
	•	Compute NPS tax benefit (deduction capped)
```
curl -X POST "http://127.0.0.1:5477/blackrock/challenge/v1/returns:nps" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "age": 29,
    "wage": 600000.0,
    "inflation": 5.5,
    "q": [
      { "fixed": 0.0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" }
    ],
    "p": [
      { "extra": 25.0, "start": "2023-10-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "k": [
      { "start": "2023-03-01 00:00:00", "end": "2023-11-30 23:59:59" },
      { "start": "2023-01-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "transactions": [
      { "date": "2023-10-12 20:15:30", "amount": 250.0, "ceiling": 300.0, "remanent": 50.0 },
      { "date": "2023-02-28 15:49:20", "amount": 375.0, "ceiling": 400.0, "remanent": 25.0 },
      { "date": "2023-07-01 21:59:00", "amount": 620.0, "ceiling": 700.0, "remanent": 80.0 },
      { "date": "2023-12-17 08:09:45", "amount": 480.0, "ceiling": 500.0, "remanent": 20.0 }
    ]
  }
```
5) Index Returns (returns:index)

Same pipeline as NPS but:
	•	uses index return rate
	•	taxBenefit is 0

```
curl -X POST "http://127.0.0.1:5477/blackrock/challenge/v1/returns:index" \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "age": 29,
    "wage": 600000.0,
    "inflation": 5.5,
    "q": [
      { "fixed": 0.0, "start": "2023-07-01 00:00:00", "end": "2023-07-31 23:59:59" }
    ],
    "p": [
      { "extra": 25.0, "start": "2023-10-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "k": [
      { "start": "2023-03-01 00:00:00", "end": "2023-11-30 23:59:59" },
      { "start": "2023-01-01 00:00:00", "end": "2023-12-31 23:59:59" }
    ],
    "transactions": [
      { "date": "2023-10-12 20:15:30", "amount": 250.0, "ceiling": 300.0, "remanent": 50.0 },
      { "date": "2023-02-28 15:49:20", "amount": 375.0, "ceiling": 400.0, "remanent": 25.0 },
      { "date": "2023-07-01 21:59:00", "amount": 620.0, "ceiling": 700.0, "remanent": 80.0 },
      { "date": "2023-12-17 08:09:45", "amount": 480.0, "ceiling": 500.0, "remanent": 20.0 }
    ]
  }