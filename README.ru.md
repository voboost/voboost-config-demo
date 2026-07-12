# Voboost Config Demo

[English](README.md) | **Русский**

Демо на одной activity для Android, интегрирующее библиотеку
[`voboost-config`](../voboost-config). Загружает `config.yaml` из assets, отображает
разобранную конфигурацию и реагирует на изменения файла в реальном времени через
файловый наблюдатель `ConfigManager`.

Часть экосистемы Voboost; зависит от [`voboost-config`](../voboost-config)
(которая, в свою очередь, зависит от [`voboost-components`](../voboost-components)).

## Что демонстрирует

- Копирование `config.yaml` по умолчанию из assets в каталог данных приложения при первом запуске
- Загрузку и отображение плоской модели `Config`
- Запуск файлового наблюдения `ConfigManager` и получение diff'ов изменений
- Перезагрузку конфигурации по требованию
- Отображение ошибки конфигурации с ожидаемым форматом полей

## config.yaml

Демо поставляет конфигурацию по умолчанию в
[`src/main/assets/config.yaml`](src/main/assets/config.yaml). Используется плоская
схема, ожидаемая моделью `Config`:

```yaml
settings-language: en
settings-theme: free-dark
settings-interface-shift-x: 0
settings-interface-shift-y: 0
settings-active-tab: interface
vehicle-fuel-mode: electric
vehicle-drive-mode: comfort
interface-keyboard: enable-russian
interface-widget-weather: enable-non-chineese-cities
settings-startup: off
settings-car-model: free
vehicle-pedestrian-warning: original
```

Полный список полей и значений enum — в
[`Config.kt`](../voboost-config/src/main/java/ru/voboost/config/models/Config.kt).

## Пример интеграции

```kotlin
class MainActivity : AppCompatActivity(), OnConfigChangeListener {
    private val configManager = ConfigManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configManager.copyDefaultConfigIfNeeded()
        configManager.loadConfig()
        configManager.startWatching(this)
    }

    override fun onConfigChanged(newConfig: Config, diff: Config) {
        if (configManager.isFieldChanged(diff, "settingsTheme")) {
            // применить новую тему
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        configManager.stopWatching()
    }
}
```

## Сборка

- Kotlin `2.0.21`, Android Gradle Plugin `8.7.3`
- `compileSdk`/`targetSdk` 34, `minSdk` 28, Java/JVM target 11
- Только release: debug-вариант отключен через `androidComponents.beforeVariants`,
  поэтому `./gradlew build` собирает один release APK (подписанный debug-keystore).
  `-Pdebuggable=true` переключает флаг `isDebuggable` release-варианта для глубокой
  отладки. Суффикс `-release` убирается из имени выходного APK.
- Unit-тесты выполняются на release-варианте; задача `testUnit` алиасит
  `testReleaseUnitTest`.
- Стиль кода: ktlint, Checkstyle и Spotless, применяемые через
  [`voboost-codestyle`](../voboost-codestyle).

```sh
./gradlew build      # собрать release APK + запустить unit-тесты
./gradlew testUnit   # только unit-тесты
./gradlew lintFix     # форматирование ktlint + spotless
```

## Экосистема

- [`voboost-config`](../voboost-config) — библиотека конфигурации, используемая этим демо
- [`voboost-components`](../voboost-components) — общие UI-компоненты
- [`voboost-codestyle`](../voboost-codestyle) — общая Gradle/стилевая конфигурация

## Лицензия

Двойная лицензия:

- [PolyForm Noncommercial 1.0.0](https://github.com/voboost/voboost-license/blob/main/LICENSE.ru.md) — бесплатно для личного использования
- [Коммерческая лицензия](https://github.com/voboost/voboost-license/blob/main/COMMERCIAL.ru.md) — требуется для любого коммерческого использования
