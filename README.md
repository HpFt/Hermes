[![Build Status](https://travis-ci.org/pTykvin/Hermes.svg?branch=master)](https://travis-ci.org/pTykvin/Hermes)
# Hermes

Слушает 80й порт. Отдает главную страницу на /

## Deployment

Скрипт деплоя протестирован и поддерживаются только для **Ubuntu xenial 16.04 x86_64**

Вручную приложение можно запустить везде где есть **docker**

Для управления деплоем сервиса и работы с ним понадобиться скрипт:
```bash
# curl -L https://raw.githubusercontent.com/pTykvin/Hermes/master/build-scripts/tools.sh?cache=`date +%s` -o tools.sh
```

### Установка сервиса: ###

**Сделать все необходимое (первая установка)**
```bash
# ./tools.sh setup
```

### Поэтапная установка ###

**Настроить окружение.**  Команда идемпотентна.
```bash
# ./tools.sh environment
```

Скачается и установится docker и docker-compose. 


**Установить postgres, создать необходимые базы.** Комада идемпотентна
```bash
# ./tools.sh database
```
Загрузит файл docker-compose.yml и поднимет сервис postgres, который в нем описан. Создаст базу, сконфигурирует сервер

**Шаблон настроек.** Команда **не** идемпотентна. Если конфигурация существует, то ничего не произойдет
```bash
# ./tools.sh configure
```

### Управление сервисом ###

**Базовые команды.**
```bash
# ./tools.sh start
# ./tools.sh stop
# ./tools.sh restart
```

**Обновить.**
```bash
# ./tools.sh update
```
После обновления запустит сервис

**Подключиться к логам.**
```bash
./tools.sh logs
```
Приаттачить output сервиса к консоли. Ctrl+C для деаттача

## Configuring

### hermes.properties ###
**life_time:**
```
Время жизни файлов
Формат описан в ISO8601
Примеры:
    "PT20.345S" -- 20.345 секунд
    "PT15M"     -- 15 минут
    "PT10H"     -- 10 часов
    "P2D"       -- 2 дня
    "P2DT3H4M"  -- 2 дня, 3 часа и 4 минуты
```
**host:**
```
Протокол и доменное имя, которое будет подставлено в ссылке для загрузки файлов.
Пример: http://domain.name.ru
```
## Monitoring
