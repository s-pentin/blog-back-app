Бэкенд приложения-блога с использованием Spring Framework

## Требования

- Java 21
- PostgreSQL
- Apache Tomcat 11

## Сборка и деплой

| Команда | Описание |
|---------|----------|
| `./gradlew war` | Собрать WAR-архив |
| `./gradlew deploy` | Собрать WAR и скопировать в Tomcat |
| `./gradlew undeploy` | Удалить приложение из Tomcat |
| `./gradlew tomcatStart` | Запустить Tomcat |
| `./gradlew tomcatStop` | Остановить Tomcat |
| `./gradlew restart` | Полный цикл: остановить, задеплоить, запустить |

## Конфигурация Tomcat

Путь к Tomcat задаётся в `gradle.properties` (корень проекта):

```
tomcat.home=/путь/к/apache-tomcat-11
```

Альтернативно через переменную окружения `TOMCAT_HOME`. Если ни то, ни другое не задано — сборка завершится с ошибкой.

## Настройка БД

Конфигурация: `src/main/resources/config/db.properties`

```properties
db.url=jdbc:postgresql://localhost:5432/blog_back_app
db.username=postgres
db.password=пароль
db.driver=org.postgresql.Driver
images.storage.path=/путь/к/директории/картинок
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
