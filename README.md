<div align="center">
  <h1>⏰ Martina Budilica</h1>
  <p><b>Personalizirana Android budilica koja jutra čini zabavnijima.</b></p>

  [![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org)
  [![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material%203-4CAF50.svg?style=flat&logo=android)](https://developer.android.com/jetpack/compose)
  [![Build Status](https://github.com/abrnjic/martina_budilica/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/abrnjic/martina_budilica/actions)
</div>

<br>

**Martina Budilica** je posebno dizajnirana, personalizirana Android aplikacija izrađena u Kotlinu i Jetpack Composeu. Njezin je glavni cilj probuditi korisnicu (Martinu) uz dozu humora, a nakon buđenja pružiti joj najvažnije informacije za početak dana – trenutačno vrijeme na njezinoj lokaciji i osobni jutarnji podsjetnik.

---

## ✨ Glavne značajke

- **Humoristični alarmni zaslon** - Svakih 30 sekundi aplikacija prikazuje novu šaljivu poruku preko cijelog zaslona (čak i kada je mobitel zaključan) sve dok se alarm ne isključi.
- **Jutarnja prognoza** - Odmah nakon gašenja alarma, aplikacija putem besplatnog [Open-Meteo API-ja](https://open-meteo.com/) dohvaća trenutačnu temperaturu i vremenske uvjete na temelju GPS lokacije te ispisuje prigodan savjet.
- **Osobni podsjetnik** - Mogućnost unosa prilagođenog podsjetnika (npr. "Danas imaš važan sastanak!") koji se prikazuje na zaslonu dobrodošlice.
- **Probni alarm** - Mogućnost testiranja funkcionalnosti alarma za 10 sekundi kako bi se osiguralo da su sva dopuštenja ispravno dodijeljena.
- **Otpornost na ponovno pokretanje** - Aplikacija automatski iznova postavlja alarme nakon ponovnog pokretanja (reboota) uređaja.

## 🛠️ Tehnologije

Aplikacija je razvijena koristeći moderne Android prakse:
- **[Kotlin](https://kotlinlang.org/)** - Glavni programski jezik.
- **[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Alat za izradu modernog i responzivnog korisničkog sučelja (UI) koristeći **Material 3** dizajn pravila.
- **[OkHttp3](https://square.github.io/okhttp/)** - Za brzo i pouzdno izvođenje mrežnih zahtjeva (dohvaćanje vremenske prognoze).
- **[Google Play Services Location](https://developers.google.com/android/reference/com/google/android/gms/location/package-summary)** - Za precizno i baterijski štedljivo lociranje korisnika.
- **[GitHub Actions](https://github.com/features/actions)** - Kontinuirana integracija (CI) koja automatski izgrađuje novu `.apk` datoteku pri svakom _pushu_ na `main` granu.

## 🚀 Instalacija

Najlakši način za preuzimanje i instalaciju aplikacije je putem trajnog linka:

📥 **[Preuzmi aplikaciju (app-debug.apk)](https://github.com/abrnjic/martina_budilica/releases/latest/download/app-debug.apk)**

**Koraci za instalaciju:**
1. Klikni na gornji link i preuzmi **`app-debug.apk`** datoteku na svoj Android uređaj.
2. Otvori preuzetu datoteku. (Ovisno o postavkama tvog uređaja, možda ćeš morati omogućiti instalaciju iz *nepoznatih izvora*).
3. Pri prvom pokretanju i spremanju alarma, **dopusti** aplikaciji slanje obavijesti, praćenje lokacije i postavljanje točnih alarma kako bi mogla besprijekorno funkcionirati.

## 🛡️ Privatnost i sigurnost

Privatnost korisnika je na prvom mjestu:
- **Lokacija** se koristi isključivo lokalno nakon buđenja kako bi se dohvatila vremenska prognoza. 
- GPS koordinate se **ne prate u pozadini**, ne spremaju trajno i ne dijele ni sa kim osim poslužitelja *Open-Meteo* radi dohvaćanja informacija o vremenu.
- Aplikacija ne sadrži praćenje (tracking) niti koristi API ključeve koje bi mogla kompromitirati korisnika.

## 💻 Pokretanje iz koda (Za developere)

Ako želiš lokalno preuzeti projekt i otvoriti ga u Android Studiju:

```bash
# 1. Kloniraj repozitorij
git clone https://github.com/abrnjic/martina_budilica.git

# 2. Otvori mapu u Android Studiju
# 3. Pričekaj da se završi Gradle Sync
# 4. Pokreni na emulatoru ili još bolje - fizičkom uređaju
```

> **Napomena:** Za optimalno testiranje alarma i lokacijskih usluga, uvijek se preporučuje korištenje pravog (fizičkog) Android uređaja umjesto emulatora.

---
<div align="center">
  <p>Napravljeno s ❤️ za Martinu i sva njezina pospana jutra.</p>
</div>
