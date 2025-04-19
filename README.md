🎬 FilmLibrary
📖 Описание
FilmLibrary — это Java-приложение для управления коллекцией фильмов. Оно предоставляет функциональность для добавления, редактирования, удаления и поиска фильмов, а также хранения информации о жанрах, актёрах, режиссёрах и других метаданных.

🚀 Возможности
Добавление новых фильмов с подробной информацией

Редактирование и удаление существующих записей

Поиск фильмов по различным критериям (название, жанр, актёр и т.д.)

Хранение данных в базе данных

Удобный пользовательский интерфейс (если применимо)

🛠️ Технологии
Java 17+

Spring Boot

Maven

H2 / PostgreSQL (или другая СУБД)

Lombok

JPA / Hibernate

REST API (если реализовано)

JavaFX или Swing (если есть графический интерфейс)

📦 Установка
Клонируйте репозиторий:

bash
Copy
Edit
git clone https://github.com/Jolire/FilmLibrary.git
cd FilmLibrary
Соберите проект с помощью Maven:

bash
Copy
Edit
./mvnw clean install
Запустите приложение:

bash
Copy
Edit
./mvnw spring-boot:run
Или, если есть графический интерфейс:

bash
Copy
Edit
java -jar target/FilmLibrary.jar
🧪 Тестирование
Для запуска тестов:

bash
Copy
Edit
./mvnw test
📁 Структура проекта
bash
Copy
Edit
FilmLibrary/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/filmlibrary/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── templates/
│   └── test/
│       └── java/
├── pom.xml
└── README.md