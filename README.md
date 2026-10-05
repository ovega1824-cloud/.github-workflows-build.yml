# Cuatro Vidas (Forge 1.20.1)

Mod para Minecraft Forge 1.20.1 (Forge 47.x, Java 17).

## Qué hace

- Cada jugador empieza con **4 vidas**, mostradas como corazones en la parte superior central de la pantalla (amarillo = vida, gris = vida perdida).
- Al morir pierdes **1 vida**. Al perder todas pasas a **modo espectador** y **no puedes respawnear**.
- Los jugadores sueltan un **Corazón Dorado** solo cuando mueren a manos de otro jugador (PvP). Clic derecho = **+1 vida**.
- El máximo de vidas en la barra es **6** (configurable).
- El **Corazón con Tuerca** abre el menú de configuración (solo operadores).

## Menú de configuración

| Opción | Valor por defecto |
|---|---|
| Vidas iniciales | 4 (1–10) |
| Vidas máximas | 6 (1–10) |
| Vidas por corazón dorado | 1 (1–3) |
| Corazón dorado en PvP | Sí |
| Vida con cualquier muerte (No = solo PvP) | Sí |
| Mostrar barra de vidas | Sí |
| Botón "Restablecer vidas de todos" | — |

Los ítems están en la pestaña creativa **Cuatro Vidas**, o con `/give @s cuatrovidas:config_heart` y `/give @s cuatrovidas:golden_heart`.

## Comandos (nivel de operador)

- `/vidas get <jugador>`
- `/vidas set <jugadores> <0-10>` (con más de 0 vuelve a supervivencia a un jugador eliminado)
- `/vidas add <jugadores> <-10..10>`
- `/vidas reset <jugadores>`

## Cómo obtener el .jar desde GitHub

1. Crea un repositorio nuevo en GitHub y sube **todo el contenido de esta carpeta** (incluida la carpeta oculta `.github`).
2. Entra a la pestaña **Actions**: el workflow "Compilar mod" se ejecuta solo en cada push (tarda unos minutos la primera vez).
3. Cuando termine, el `.jar` aparece en **Releases** (versión `latest`) y también como artifact dentro de la ejecución.

Para publicar una versión con número, crea una etiqueta, por ejemplo `v1.0.0`.

El `.jar` va en la carpeta `mods` del servidor y de cada cliente (todos necesitan el mod).
