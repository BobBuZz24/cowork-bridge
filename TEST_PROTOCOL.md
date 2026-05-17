# CoWork Bridge — Protocole de test v1.0

> À exécuter avant toute publication de notice ou release.  
> Cocher chaque case au fur et à mesure. Consigner les anomalies en bas du document.

---

## Prérequis

| # | Condition | OK |
|---|---|---|
| P1 | APK installé sur le device (Android 8.0+) | ☐ |
| P2 | Device et PC Linux sur le même réseau WiFi | ☐ |
| P3 | `adb` installé sur le PC (`sudo apt install adb`) | ☐ |
| P4 | USB debugging activé dans Options développeur | ☐ |

---

## T1 — Lancement & affichage

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T1.1 | Ouvrir l'app | Écran principal visible, pas de crash | ☐ |
| T1.2 | Observer la carte "Connexion ADB" | L'IP locale s'affiche (`192.168.x.x`) | ☐ |
| T1.3 | Observer le statut WiFi | "WiFi connecté ✓" en vert | ☐ |
| T1.4 | Observer la commande ADB | `adb connect 192.168.x.x:5555` correcte | ☐ |
| T1.5 | Observer le nom WiFi | SSID du réseau affiché | ☐ |
| T1.6 | Observer la carte Appareil | Modèle + version Android corrects | ☐ |

---

## T2 — Copie dans le presse-papier

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T2.1 | Appuyer "Copier" (à côté de l'IP) | Toast "IP copiée !" | ☐ |
| T2.2 | Coller dans une autre app | IP seule collée (sans port) | ☐ |
| T2.3 | Appuyer "Copier" (à côté de la commande ADB) | Toast "Commande ADB copiée !" | ☐ |
| T2.4 | Coller dans une autre app | `adb connect 192.168.x.x:5555` collée | ☐ |

---

## T3 — Navigation Settings

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T3.1 | Appuyer "Ouvrir les Options développeur" | Ouvre les Options développeur Android | ☐ |
| T3.2 | Revenir à l'app (bouton Retour) | App toujours visible, pas de crash | ☐ |
| T3.3 | Appuyer "Paramètres WiFi" | Ouvre les paramètres WiFi Android | ☐ |
| T3.4 | Revenir à l'app | App toujours visible | ☐ |

---

## T4 — Options

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T4.1 | Activer "Garder l'écran allumé" | Toast "Écran toujours allumé" | ☐ |
| T4.2 | Attendre 2 min sans toucher le device | Écran reste allumé | ☐ |
| T4.3 | Désactiver "Garder l'écran allumé" | Toast "Délai d'extinction normal" | ☐ |
| T4.4 | Activer "Service en arrière-plan" | Notification persistante apparaît dans la barre | ☐ |
| T4.5 | Vérifier le contenu de la notification | Affiche `adb connect 192.168.x.x:5555` | ☐ |
| T4.6 | Appuyer sur la notification | Ouvre l'app | ☐ |
| T4.7 | Désactiver "Service en arrière-plan" | Notification disparaît | ☐ |

---

## T5 — Actualisation

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T5.1 | Appuyer "↻ Actualiser" | IP et statut se rafraîchissent | ☐ |
| T5.2 | Désactiver le WiFi, attendre 3 s | Statut passe en rouge "Pas de WiFi" | ☐ |
| T5.3 | Réactiver le WiFi, attendre 3 s | IP réapparaît, statut revient en vert | ☐ |

---

## T6 — Connexion ADB réelle (test bout-en-bout)

> Ce test valide que l'IP affichée est réellement joignable par ADB.

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T6.1 | Brancher le device en USB au PC Linux | — | ☐ |
| T6.2 | Sur le PC : `adb devices` | Device listé avec `device` (autoriser si pop-up) | ☐ |
| T6.3 | Sur le PC : `adb tcpip 5555` | `restarting in TCP mode port: 5555` | ☐ |
| T6.4 | Débrancher le câble USB | — | ☐ |
| T6.5 | Copier la commande dans l'app, la coller dans un terminal | `adb connect 192.168.x.x:5555` | ☐ |
| T6.6 | Exécuter la commande | `connected to 192.168.x.x:5555` | ☐ |
| T6.7 | `adb devices` | Device listé sans câble | ☐ |
| T6.8 | `adb shell input tap 540 960` | Tap visible à l'écran | ☐ |
| T6.9 | Screenshot ADB : `adb exec-out screencap -p > /tmp/screen.png && xdg-open /tmp/screen.png` | Screenshot du device s'ouvre sur le PC | ☐ |

---

## T7 — Robustesse

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T7.1 | Mettre l'app en arrière-plan (Home), attendre 5 min, rouvrir | App repond, IP toujours correcte | ☐ |
| T7.2 | Rotation écran (si non bloquée) | Pas de crash, données conservées | ☐ |
| T7.3 | Redémarrer le device (service BG actif) | Service redémarre auto, notification réapparaît | ☐ |

---

## T8 — Cas limites

| # | Action | Résultat attendu | OK |
|---|---|---|---|
| T8.1 | Lancer sans WiFi (données mobiles seulement) | Statut rouge "Pas de WiFi", pas de crash | ☐ |
| T8.2 | Lancer en mode avion | Même comportement que T8.1 | ☐ |
| T8.3 | Lancer sur Android 8.0 exact (si possible) | Tout fonctionne (minSdk 26) | ☐ |

---

## Anomalies constatées

| # | Test | Description | Sévérité | Statut |
|---|---|---|---|---|
| — | — | — | — | — |

> Sévérité : 🔴 Bloquant / 🟠 Majeur / 🟡 Mineur / 🔵 Cosmétique

---

## Verdict final

- [ ] **GO** — Tous les tests T1→T6 passent, anomalies mineures seulement → publier la notice
- [ ] **NO GO** — Au moins un test bloquant ou majeur → corriger d'abord

_Testé par :_ _______________  _Date :_ _______________  _Version APK :_ _______________
