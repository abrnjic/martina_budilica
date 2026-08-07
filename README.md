# Dobro jutro, Martina

Osobna Android budilica i jutarnji podsjetnik, izrađena u Kotlinu i Jetpack Composeu.

## Što radi prva verzija

- postavlja svakodnevni alarm
- prikazuje alarm preko zaključanog zaslona
- svake 30 sekundi mijenja šaljivu poruku dok Martina ne pritisne **Budna sam**
- nakon buđenja prikazuje lijepu poruku, trenutačno vrijeme i približnu GPS lokaciju
- dohvaća trenutačnu prognozu bez API ključa preko Open-Metea
- prikazuje osobni jutarnji podsjetnik
- ponovno postavlja alarm nakon restarta mobitela
- ima probni alarm koji zvoni nakon 10 sekundi

## Pokretanje

1. Otvori mapu `MartinaBudilica` u Android Studiju.
2. Pričekaj da završi Gradle Sync.
3. Pokreni aplikaciju na fizičkom Android uređaju (GPS i alarmi pouzdaniji su nego na emulatoru).
4. Pri prvom spremanju dopusti obavijesti, lokaciju i točne alarme.
5. Isključi optimizaciju baterije za aplikaciju ako proizvođač mobitela agresivno uspavljuje aplikacije.

Za najbržu provjeru pritisni **Isprobaj za 10 sekundi**, zaključaj zaslon i pričekaj alarm.

## Privatnost

Lokacija se obrađuje tek nakon potvrde buđenja. Koordinate se šalju samo servisu Open-Meteo radi dohvaćanja lokalne prognoze i ne spremaju se u aplikaciji.

## Važno prije objave na Google Playu

Ovo je osobni MVP. Prije javne objave treba dodati zaslon privatnosti, prilagoditi pravila korištenja full-screen alarma, izraditi potpisani release paket i testirati ponašanje na više proizvođača mobitela.
