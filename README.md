# FranekCustomItems

![banner](assets/banner.png)

Paczka ponad 50 przedmiotów specjalnych na serwery Anarchia / BoxPvP (w tym m.in. Excalibur z licznikiem zabójstw, Boski Topór, Hydro Klatka, TurboDomek, TurboTrap, Wędka Nielota, Kosa, Korona Anarchii, Plecaki, Sakiewki i wiele innych).

Każdy przedmiot ma osobne ustawienia cooldownu, regionu blokady (wsparcie dla WorldGuard), powiadomień na czacie / pasku akcji oraz własne efekty. Dane graczy, plecaków i sakiewek zapisują się w bazie SQLite albo MySQL.

## Komendy

| Komenda | Co robi |
|---|---|
| `/customitems [strona]` | otwiera GUI ze wszystkimi przedmiotami specjalnymi |
| `/customitems reload` | przeładowuje `pluginConfiguration.yml` na żywo |
| `/customitems kills <ilosc>` | ustawia liczbę zabójstw na trzymanym w ręce Excaliburze |

Uprawnienie do komendy: `franek.customitems.command`.

## Config

`plugins/FranekCustomItems/pluginConfiguration.yml` oraz schematy w folderze `schematics/` (`turbodomek.schem`, `turbotrap.schem`) tworzą się automatycznie przy pierwszym starcie serwera.

```yaml
databaseType: SQLITE
regionsWhereItemsAreBlocked:
  - spawn
```

Do działania `TurboDomek` oraz `TurboTrap` na serwerze wymagany jest WorldEdit lub FastAsyncWorldEdit.

## Instalacja

Jar z folderu [`releases/`](releases/) wrzucasz do `plugins/` na serwerze (Paper / Spigot 1.20+) i restart.

## Budowanie

```bash
mvn package
```

Wychodzi `target/FranekCustomItems-1.0.jar`. Wymaga JDK 21 i Mavena.

## Licencja

Wszelkie prawa zastrzeżone. Autor: franekk ([GitHub](https://github.com/Blejziak)).
