# PORT_REPORT — MekaJadeUpgrades 26.1.2

## Сводка

- Дерево: `/root/home/forks/MekaJadeUpgrades` (NL), ветка `port/26.1.2`.
- Апстрим: https://github.com/DevDyna/MekaJadeUpgrades, версия 1.3-21.1, ветка `21.1`, лицензия **MIT** (сохранена, авторство DevDyna).
- Цель: Minecraft **26.1.2**, NeoForge **26.1.2.126**, Java **25**.
- Результат: `compileJava` без ошибок, **BUILD SUCCESSFUL**, самостоятельный JAR:
  `build/libs/mekajadeupgrade-26.1.2-1.3-26.1.2.jar`.
- Runtime QA не выполнялся (по заданию — выполнят другие агенты).

## Перенесённые функции

1. **Jade-плагин** (`JadePlugin`): `@WailaPlugin` регистрирует `UpgradeProvider`
   как block-component (клиент) и block-data-provider (сервер) для всех
   `BlockTile` машин Mekanism — как в апстриме.
2. **Чтение установленных улучшений Mekanism**: серверная часть (`appendServerData`)
   читает `TileComponentUpgrade.getUpgrades(upgrade)` и кладёт счётчики в
   NBT-пейлоад Jade по `SerializedName` каждого `Upgrade`.
3. **Отображение в подсказке**: иконка соответствующего апгрейд-предмета
   (`MekanismItems.*_UPGRADE`) + текст `x<количество>` для каждого установленного
   улучшения. Поддержаны все 7 базовых улучшений: Speed, Energy, Filter,
   Chemical, Muffling, Anchor, Stone Generator.
4. **Корректная работа без улучшений**: если серверный пейлоад не содержит
   ни одного улучшения, в тултип ничего не добавляется; нулевые счётчики не
   попадают в пейлоад. Машины не-Mekanism (не `TileEntityMekanism`) полностью
   игнорируются.
5. **Датаген** (en_us.json для ключа конфига Jade) сохранён и адаптирован.
6. **Gradle → ModDevGradle 2.0.144**, Gradle wrapper 8.9 → **9.2.1** (Java 25,
   class file 69), toolchain Java 25, `neo_version=26.1.2.126`.
7. **MIT LICENSE и README** сохранены без изменений, авторство DevDyna в
   `neoforge.mods.toml` и `PORT_REPORT`.

## Ключевые изменения API (21.1 → 26.1.2)

| Было (1.21.1) | Стало (26.1.2) |
|---|---|
| `ResourceLocation.fromNamespaceAndPath` | `net.minecraft.resources.Identifier.fromNamespaceAndPath` |
| `IElementHelper.get().item(stack, s).size(Vec2).translate(Vec2).message(null)` | `JadeUI.item(stack, s).size(w,h).offset(x,y).narration((String)null)` (элементы теперь `net.minecraft.client.gui.layouts.LayoutElement`) |
| `CompoundTag.getInt(key)` → `int` | `Optional<Integer>` → `.orElse(0)` |
| `TileComponentUpgrade.isUpgradeInstalled(u)` | удалено в Mekanism 10.8.0 → `getUpgrades(u) > 0` |
| `@EventBusSubscriber(bus = Bus.MOD, modid=...)` | `@EventBusSubscriber(modid=...)` (bus-параметр удалён) |
| `GatherDataEvent.includeClient()` + `g.addProvider(bool, p)` | `e.addProvider(p)` |

## Зависимости

- Компиляция: Mekanism `mekanism:Mekanism:26.1.2-10.8.0.homebaked` (mavenLocal,
  дерево `mekanism-stack/Mekanism`), Jade `libs/Jade-mc26.1-NeoForge-26.1.12.jar`
  (скопирован из `pack-libs/compat`).
- В рантайме мод требует `mekanism` `[10.8,)` и `jade` `[26.1,)`.
- **JEI/REI/Kotlin не требуются** — в коде и Gradle их нет (у апстрима были
  транзитивные impl-зависимости на Kotlin-библиотеки для соседних модов, в порт
  они не перенесены).

## Оставшиеся ограничения

1. **mekanism_extras и mekanism_empowered интеграции удалены.** Апстрим
   отображал дополнительные улучшения (Stack, Creative, Ionic Membrane,
   Auto-Insert/Empowered Energy/Speed, Fast Item Eject/Insert, IO Capacity),
   когда эти моды установлены. В флоте нет их портов на 26.1.2, поэтому
   компилировать эти ветки было не против чего. При появлении портов интеграцию
   можно вернуть по образцу апстрима (`UpgradeProvider.getUpgrade`, guard через
   `ModList.get().isLoaded(...)`).
2. **Layout-позиционирование** элементов слегка упрощено: апстрим задавал
   горизонтальный сдвиг `x += 10` для каждой пары иконка+текст; в Jade 26.x
   тултип стал layout-ориентированным, элементы добавляются последовательно
   и выстраиваются Jade автоматически (визуально — та же строка
   «иконка xN», но точный ручной сдвиг `translate` больше не поддерживается API).
3. **Runtime QA не проводился** (по заданию). Требуется проверка: тултип на
   машинах Mekanism с улучшениями, без улучшений и на посторонних блоках.
4. `implementation`-зависимости апстрима на посторонние моды (curse.maven:
   mekaextra/empo/iglee/evo/kotlin) не перенесены — это были только
   dev-environment зависимости, в рантайм-манифесте их никогда не было.

## Коммиты

- `Import upstream MekaJadeUpgrades 1.3-21.1 (MIT, DevDyna)` — исходный импорт.
- `Port to Minecraft 26.1.2 / NeoForge 26.1.2.126 (ModDevGradle 2.x, Java 25)` — порт.
