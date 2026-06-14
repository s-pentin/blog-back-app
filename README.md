Бэкенд приложения-блога с использованием Spring Boot Framework

## Требования

- Java 21
- PostgreSQL
- Spring Boot
- Maven

## Настройка БД

Конфигурация: `src/main/resources/config/db.properties`

```properties
spring.application.name=back
spring.datasource.url=jdbc:postgresql://localhost:<port>/<db_name>
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
images.storage.path=/путь/к/директории/картинок
server.port=8080
```

Схема БД: `src/main/resources/schema.sql`

## API эндпоинты

| Метод | Путь | Описание |
|-------|------|----------|
| GET | `/api/posts` | Список постов (пагинация + поиск) |
| POST | `/api/posts/{id}` | Получить пост по ID |
| POST | `/api/posts` | Создать пост |
| PUT | `/api/posts/{id}` | Обновить пост |
| DELETE | `/api/posts/{id}` | Удалить пост |
| PUT | `/api/posts/{id}/image` | Загрузить картинку |
| GET | `/api/posts/{id}/image` | Получить картинку |
| POST | `/api/posts/{id}/likes` | Лайкнуть пост |
| GET | `/api/posts/{postId}/comments` | Список комментариев |
| GET | `/api/posts/{postId}/comments/{id}` | Комментарий по ID |
| POST | `/api/posts/{postId}/comments` | Создать комментарий |
| PUT | `/api/posts/{postId}/comments/{id}` | Обновить комментарий |
| DELETE | `/api/posts/{postId}/comments/{id}` | Удалить комментарий |
