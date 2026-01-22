# WhatsApp Service

A Spring Boot application that integrates with Infobip's WhatsApp API to handle incoming and outgoing WhatsApp messages. The service acts as a conversational bot platform that processes customer messages through a menu-driven system with support for interactive buttons, lists, documents, and various media types.

## Features

- Multi-turn conversation management with Redis session persistence
- Interactive message types: buttons, lists, text, documents
- Media support: images, audio, video, documents, voice, location
- Multi-country deployment support
- Event-driven architecture for message handling
- Configurable country-specific settings

## Tech Stack

- **Java**: 17
- **Spring Boot**: 3.2.8
- **Database**: MySQL / PostgreSQL
- **Cache**: Redis with SSL/TLS
- **API Integration**: Infobip WhatsApp API via OpenFeign
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL or PostgreSQL
- Redis
- Stunnel (for SSL/TLS with Redis on Windows)
- Infobip account with WhatsApp API access

## Project Structure

```
src/main/java/com/whatsapp/
├── client/              # Feign HTTP clients for external APIs
├── config/              # Configuration classes (Redis, Country settings)
├── constant/            # Application constants
├── controller/          # REST API endpoints
├── dto/                 # Data Transfer Objects
│   ├── request/
│   └── response/
├── entity/              # Domain entities
├── enumclass/           # Enumerations
├── interfaces/          # Service interfaces and implementations
│   ├── event/           # Event processing
│   ├── infobip/         # Infobip integration
│   └── menu/            # Menu management
├── repository/          # Data access layer
├── service/             # Business logic services
```

## Configuration

### Application Configuration (YAML)

Configure the following in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DB:jwtdb}?createDatabaseIfNotExist=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: ${JPA_DDL_AUTO:none}
    show-sql: ${JPA_SHOW_SQL:false}

redis:
  hostname: localhost
  port: 6380
  auth-token: ""
  database: 0

whatsapp:
  infobip:
    url: https://gg83ee.api.infobip.com
    sms:
      delay: 1500
  infobipApiKey: your-api-key-here
  app-base-url: https://your-ngrok-url.ngrok-free.app
  settings:
    countries:
      india:
        phone-number: "447860099299"
        start-keywords: "hi,hello,hey"
        defaultTerminateMessage: ""
        scenarioKey: your-scenario-key
```

### PostgreSQL Configuration (Alternative)

To use PostgreSQL instead of MySQL, update the datasource configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DB:testdb}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME:postgres}
    password: ${DB_PASSWORD:root}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: ${JPA_DDL_AUTO:none}
    show-sql: ${JPA_SHOW_SQL:false}
```

## Redis Setup with SSL/TLS (Windows)

### Step 1: Install Stunnel

1. Download stunnel from: https://www.stunnel.org/downloads.html
2. Install and start stunnel

### Step 2: Install Redis

1. Download Redis from: https://redis.io/downloads/
2. Install Redis
3. Add Redis installation path to system environment variables

### Step 3: Initial Redis Setup

Open Command Prompt and run:

```cmd
redis-cli.exe
select 0
shutdown
quit
```

### Step 4: Generate SSL Certificates

Navigate to stunnel bin directory and generate certificates:

```cmd
cd "C:\Program Files (x86)\stunnel\bin"

openssl req -x509 -nodes -days 1826 -newkey rsa:2048 -keyout D:\Test\redis-6.2.14\redis-server.key -out D:\Test\redis-6.2.14\redis-server.cert
```

### Step 5: Import Certificate to Java Keystore

Open Command Prompt as Administrator, navigate to JDK bin directory:

**For JDK 11:**
```cmd
keytool -import -alias redis_localhost_certificate -file D:\Test\redis-6.2.14\redis-server.cert -keystore "C:\Program Files\Java\jdk-11.0.12\lib\security\cacerts" -storepass changeit
```

**For JDK 17:**
```cmd
keytool -import -alias redis_localhost_certificate -file D:\Test\redis-6.2.14\redis-server.cert -keystore "C:\Program Files\Java\jdk-17.0.7\lib\security\cacerts" -storepass changeit
```

### Step 6: Configure Stunnel

1. Copy `redis-server.cert` and `redis-server.key` from `D:\Test\redis-6.2.14` to `C:\Program Files (x86)\stunnel`

2. Open stunnel configuration file at `C:\Program Files (x86)\stunnel\config\stunnel.conf`

3. Edit Configuration and add the following (remove other email setups):

```ini
[redis-server]
cert = C:\Program Files (x86)\stunnel\redis-server.cert
key = C:\Program Files (x86)\stunnel\redis-server.key
accept = 127.0.0.1:6380
connect = 127.0.0.1:6379

[redis-client]
client = yes
accept = 127.0.0.1:6397
connect = 127.0.0.1:6380
CAfile = C:\Program Files (x86)\stunnel\redis-server.cert
```

### Step 7: Start Redis Server

Open a new Command Prompt and start Redis:

```cmd
redis-server.exe "C:\Program Files\Redis\redis.windows.conf"
```

### Additional Commands

**List certificates in keystore:**
```cmd
keytool -list -v -keystore "C:\Program Files\Java\jdk-11.0.12\lib\security\cacerts" -storepass changeit
```

**Remove certificate alias:**
```cmd
keytool -delete -alias redis_localhost_certificate -keystore "C:\Program Files\Java\jdk-11.0.12\lib\security\cacerts" -storepass changeit
```

## Building the Application

```bash
mvn clean install
```

## Running the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/whatsapp-service-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Webhook Endpoint

```
POST /api/v1/webhook/infobip-whatsapp/incoming-message
```

Receives incoming WhatsApp messages from Infobip webhook.

---

## API Testing with Postman/cURL

### Base URL
```
http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message
```

### Headers
```
Content-Type: application/json
```

---

### 1. Text Message (Start Session)

Sends a simple text message to start a new session.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:30:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSf",
        "message": {
          "type": "TEXT",
          "text": "hi"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 2. Text Message (Continue Session)

Sends a text message to continue an existing session.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:31:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSg",
        "message": {
          "type": "TEXT",
          "text": "I need help with my account"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 3. Interactive Button Reply

User clicks on an interactive button.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:32:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSh",
        "message": {
          "type": "INTERACTIVE_BUTTON_REPLY",
          "text": "Check Balance",
          "id": "CHECK_BALANCE",
          "title": "Check Balance",
          "description": "View your current account balance"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 4. Interactive List Reply

User selects an option from an interactive list.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:33:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSi",
        "message": {
          "type": "INTERACTIVE_LIST_REPLY",
          "text": "Account Services",
          "id": "ACCOUNT_SERVICES",
          "title": "Account Services",
          "description": "Manage your account settings and preferences"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 5. Image Message

User sends an image.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:34:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSj",
        "message": {
          "type": "IMAGE",
          "url": "https://example.com/image.jpg",
          "caption": "Here is my ID document"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 6. Document Message

User sends a document (PDF, DOC, etc.).

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:35:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSk",
        "message": {
          "type": "DOCUMENT",
          "url": "https://example.com/document.pdf",
          "caption": "My application form"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 7. Audio Message

User sends an audio file.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:36:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSl",
        "message": {
          "type": "AUDIO",
          "url": "https://example.com/audio.mp3",
          "caption": ""
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 8. Video Message

User sends a video.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:37:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSm",
        "message": {
          "type": "VIDEO",
          "url": "https://example.com/video.mp4",
          "caption": "Video proof"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 9. Voice Message

User sends a voice note.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:38:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSn",
        "message": {
          "type": "VOICE",
          "url": "https://example.com/voice.ogg",
          "caption": ""
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### 10. Location Message

User shares their location.

**cURL:**
```bash
curl -X POST http://localhost:8080/api/v1/webhook/infobip-whatsapp/incoming-message \
  -H "Content-Type: application/json" \
  -d '{
    "results": [
      {
        "from": "919876543210",
        "to": "447860099299",
        "integrationType": "WHATSAPP",
        "receivedAt": "2024-01-15T10:39:00.000Z",
        "messageId": "ABGGFlA5FpafAgo6tHcNmNjXmuSo",
        "message": {
          "type": "LOCATION",
          "text": "28.6139,77.2090",
          "caption": "My current location"
        }
      }
    ],
    "messageCount": 1,
    "pendingMessageCount": 0
  }'
```

---

### Response Format

All requests return a success response:

```json
{
  "message": "success"
}
```

---

### Request Fields Reference

| Field | Type | Description |
|-------|------|-------------|
| `results` | Array | List of incoming messages |
| `results[].from` | String | Sender's phone number (with country code) |
| `results[].to` | String | Receiver's phone number (WhatsApp business number) |
| `results[].integrationType` | String | Integration type (WHATSAPP) |
| `results[].receivedAt` | String | Timestamp when message was received (ISO 8601) |
| `results[].messageId` | String | Unique message identifier from Infobip |
| `results[].message.type` | String | Message type (TEXT, IMAGE, DOCUMENT, etc.) |
| `results[].message.text` | String | Text content of the message |
| `results[].message.caption` | String | Caption for media messages |
| `results[].message.url` | String | URL for media content |
| `results[].message.id` | String | Button/List item ID (for interactive replies) |
| `results[].message.title` | String | Title of selected button/list item |
| `results[].message.description` | String | Description of selected button/list item |
| `messageCount` | Integer | Total number of messages in request |
| `pendingMessageCount` | Integer | Number of pending messages |

---

## Infobip API Reference

These are the Infobip APIs used by this service. Replace `{BASE_URL}` with your Infobip API base URL (e.g., `https://gg83ee.api.infobip.com`) and `{API_KEY}` with your Infobip API key.

### 1. Create Scenario Key

Creates a new scenario for WhatsApp messaging. The scenario key is required for sending outbound messages.

**cURL:**
```bash
curl -X POST 'https://{BASE_URL}/omni/1/scenarios' \
  --header 'Authorization: App {API_KEY}' \
  --header 'Content-Type: application/json' \
  --data '{
    "name": "New Scenario Name",
    "flow": [
        {
            "from": "447860088970",
            "channel": "WHATSAPP"
        }
    ]
}'
```

**Response:**
```json
{
  "key": "your-generated-scenario-key",
  "name": "New Scenario Name",
  "flow": [
    {
      "from": "447860088970",
      "channel": "WHATSAPP"
    }
  ],
  "default": false
}
```

---

### 2. Send WhatsApp Interactive Buttons

Sends an interactive message with reply buttons.

#### Option 1: Omni API

**Endpoint:** `POST /omni/1/advanced`

**cURL:**
```bash
curl -X POST 'https://{BASE_URL}/omni/1/advanced' \
  --header 'Authorization: App {API_KEY}' \
  --header 'Content-Type: application/json' \
  --data '{
    "scenarioKey": "{SCENARIO_KEY}",
    "destinations": [
        {
            "to": {
                "phoneNumber": "919876543210"
            }
        }
    ],
    "whatsApp": {
        "type": "BUTTONS",
        "content": {
            "body": "Please select an option:",
            "buttons": [
                {
                    "type": "REPLY",
                    "id": "OPTION_1",
                    "title": "Option 1"
                },
                {
                    "type": "REPLY",
                    "id": "OPTION_2",
                    "title": "Option 2"
                }
            ]
        }
    }
}'
```

#### Option 2: Direct WhatsApp API

**Endpoint:** `POST /whatsapp/1/message/interactive/buttons`

**cURL:**
```bash
curl -X POST 'https://{BASE_URL}/whatsapp/1/message/interactive/buttons' \
  --header 'Authorization: App {API_KEY}' \
  --header 'Content-Type: application/json' \
  --data '{
    "from": "447860099299",
    "to": "919876543210",
    "messageId": "unique-message-id-123",
    "content": {
        "body": {
            "text": "Please select an option:"
        },
        "action": {
            "buttons": [
                {
                    "type": "REPLY",
                    "id": "OPTION_1",
                    "title": "Option 1"
                },
                {
                    "type": "REPLY",
                    "id": "OPTION_2",
                    "title": "Option 2"
                },
                {
                    "type": "REPLY",
                    "id": "OPTION_3",
                    "title": "Option 3"
                }
            ]
        },
        "header": {
            "type": "TEXT",
            "text": "Welcome Header"
        },
        "footer": {
            "text": "Footer text here"
        }
    }
}'
```

**Request Fields (Option 2):**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `from` | String | Yes | WhatsApp business phone number |
| `to` | String | Yes | Recipient phone number |
| `messageId` | String | No | Unique message identifier |
| `content.body.text` | String | Yes | Main message text |
| `content.action.buttons` | Array | Yes | List of buttons (max 3) |
| `content.action.buttons[].type` | String | Yes | Button type (always "REPLY") |
| `content.action.buttons[].id` | String | Yes | Unique button identifier |
| `content.action.buttons[].title` | String | Yes | Button display text (max 20 chars) |
| `content.header.type` | String | No | Header type (TEXT, IMAGE, VIDEO, DOCUMENT) |
| `content.header.text` | String | No | Header text (when type is TEXT) |
| `content.header.mediaUrl` | String | No | Media URL (when type is IMAGE/VIDEO/DOCUMENT) |
| `content.footer.text` | String | No | Footer text |

---

### 3. Send WhatsApp Interactive List

Sends an interactive message with a list of selectable options.

#### Option 1: Omni API

**Endpoint:** `POST /omni/1/advanced`

**cURL:**
```bash
curl -X POST 'https://{BASE_URL}/omni/1/advanced' \
  --header 'Authorization: App {API_KEY}' \
  --header 'Content-Type: application/json' \
  --data '{
    "scenarioKey": "{SCENARIO_KEY}",
    "destinations": [
        {
            "to": {
                "phoneNumber": "919876543210"
            }
        }
    ],
    "whatsApp": {
        "type": "LIST",
        "content": {
            "body": "Please select from the list:",
            "action": {
                "title": "View Options",
                "sections": [
                    {
                        "title": "Section 1",
                        "rows": [
                            {
                                "id": "ROW_1",
                                "title": "First Option",
                                "description": "Description for first option"
                            },
                            {
                                "id": "ROW_2",
                                "title": "Second Option",
                                "description": "Description for second option"
                            }
                        ]
                    }
                ]
            }
        }
    }
}'
```

#### Option 2: Direct WhatsApp API

**Endpoint:** `POST /whatsapp/1/message/interactive/list`

**cURL:**
```bash
curl -X POST 'https://{BASE_URL}/whatsapp/1/message/interactive/list' \
  --header 'Authorization: App {API_KEY}' \
  --header 'Content-Type: application/json' \
  --data '{
    "from": "447860099299",
    "to": "919876543210",
    "messageId": "unique-message-id-456",
    "content": {
        "body": {
            "text": "Please select from the list below:"
        },
        "action": {
            "title": "View Options",
            "sections": [
                {
                    "title": "Account Services",
                    "rows": [
                        {
                            "id": "CHECK_BALANCE",
                            "title": "Check Balance",
                            "description": "View your current account balance"
                        },
                        {
                            "id": "MINI_STATEMENT",
                            "title": "Mini Statement",
                            "description": "Get last 5 transactions"
                        }
                    ]
                },
                {
                    "title": "Support",
                    "rows": [
                        {
                            "id": "CONTACT_SUPPORT",
                            "title": "Contact Support",
                            "description": "Speak with a customer service agent"
                        },
                        {
                            "id": "FAQ",
                            "title": "FAQ",
                            "description": "View frequently asked questions"
                        }
                    ]
                }
            ]
        },
        "header": {
            "type": "TEXT",
            "text": "Main Menu"
        },
        "footer": {
            "text": "Reply with option number or tap to select"
        }
    }
}'
```

**Request Fields (Option 2):**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `from` | String | Yes | WhatsApp business phone number |
| `to` | String | Yes | Recipient phone number |
| `messageId` | String | No | Unique message identifier |
| `content.body.text` | String | Yes | Main message text |
| `content.action.title` | String | Yes | Button text to open list (max 20 chars) |
| `content.action.sections` | Array | Yes | List sections (max 10 sections) |
| `content.action.sections[].title` | String | Yes | Section title |
| `content.action.sections[].rows` | Array | Yes | List items in section (max 10 rows total) |
| `content.action.sections[].rows[].id` | String | Yes | Unique row identifier |
| `content.action.sections[].rows[].title` | String | Yes | Row title (max 24 chars) |
| `content.action.sections[].rows[].description` | String | No | Row description (max 72 chars) |
| `content.header.type` | String | No | Header type (TEXT only for lists) |
| `content.header.text` | String | No | Header text |
| `content.footer.text` | String | No | Footer text |

---

### 4. Send WhatsApp Document

Sends a document (PDF, DOC, etc.) to a WhatsApp user.

#### Option 1: Omni API

**Endpoint:** `POST /omni/1/advanced`

**cURL:**
```bash
curl -X POST 'https://{BASE_URL}/omni/1/advanced' \
  --header 'Authorization: App {API_KEY}' \
  --header 'Content-Type: application/json' \
  --data '{
    "scenarioKey": "{SCENARIO_KEY}",
    "destinations": [
        {
            "to": {
                "phoneNumber": "919876543210"
            }
        }
    ],
    "whatsApp": {
        "type": "DOCUMENT",
        "mediaUrl": "https://example.com/document.pdf",
        "caption": "Here is your document"
    }
}'
```

#### Option 2: Direct WhatsApp API

**Endpoint:** `POST /whatsapp/1/message/document`

**cURL:**
```bash
curl -X POST 'https://{BASE_URL}/whatsapp/1/message/document' \
  --header 'Authorization: App {API_KEY}' \
  --header 'Content-Type: application/json' \
  --data '{
    "from": "447860099299",
    "to": "919876543210",
    "messageId": "unique-message-id-789",
    "content": {
        "mediaUrl": "https://example.com/documents/statement.pdf",
        "filename": "Account_Statement_2024.pdf",
        "caption": "Here is your account statement for January 2024"
    }
}'
```

**Request Fields (Option 2):**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `from` | String | Yes | WhatsApp business phone number |
| `to` | String | Yes | Recipient phone number |
| `messageId` | String | No | Unique message identifier |
| `content.mediaUrl` | String | Yes | URL of the document to send |
| `content.filename` | String | Yes | Display filename for the document |
| `content.caption` | String | No | Caption text for the document |

---

## Supported Message Types

| Type | Description |
|------|-------------|
| TEXT | Plain text messages |
| IMAGE | Image attachments |
| AUDIO | Audio files |
| VIDEO | Video files |
| DOCUMENT | Document attachments |
| BUTTON | Interactive button messages |
| INTERACTIVE_BUTTON_REPLY | Button click responses |
| INTERACTIVE_LIST_REPLY | List selection responses |
| VOICE | Voice messages |
| LOCATION | Location sharing |
| STICKER | Sticker messages |
| CONTACT | Contact sharing |

## Supported Countries

- Africa: Botswana, Nigeria, Namibia, Ghana, Uganda, Mozambique, Lesotho, Eswatini, Kenya, Rwanda, Tanzania
- Asia: India
- Americas: USA

## Event Types

| Event | Description |
|-------|-------------|
| START | Session initiation |
| CONTINUE | Ongoing interaction |
| TERMINATE:SessionTimeOut | Idle timeout |
| TERMINATE:UserInitiated | User quit |
| TERMINATE:EndOfMenu | End of conversation flow |

## Redis Key Structure

```
WA_SESS:{country}:{msisdn}                    # Current session page
WA_COLLECTED_DATA:{country}:{msisdn}          # Form data collected from user
WA_CUSTOMER_DATA                              # Customer master data
WA_EMPLOYERS_DATA:{country}:{msisdn}          # Employer information
WA_LOAN_PURPOSE_DATA:{country}:{msisdn}       # Loan purpose info
WA_PASSWORD_LESS_CUSTOMER_DATA                # Passwordless auth data
```

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `redis.hostname` | Redis server hostname | localhost |
| `redis.port` | Redis server port | 6380 |
| `redis.auth-token` | Redis authentication token | (empty) |
| `redis.database` | Redis database number | 0 |
| `redis.ssl` | Enable SSL for Redis | true |
| `redis.command-timeout` | Redis command timeout (seconds) | 5 |
| `whatsapp.infobip.url` | Infobip API base URL | - |
| `whatsapp.infobipApiKey` | Infobip API key | - |
| `whatsapp.app-base-url` | Application webhook URL | - |

## License

This project is proprietary software.
