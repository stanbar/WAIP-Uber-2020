# INSTRUKCJA:
### Instalacja SDK Ericssona(Windows 10)
1. Instalujemy SDK Ericssona + Java 1.8 (plik: WAIP_CD\Parlay\sdk\files\ericsson_nrgsdk_R5A02_setup.exe - on zainstaluje Jave 1.3 dlatego warto odznaczyć i za wczasu pobrać wersję wymienioną wyżej)
2. Środowisko dla Javy - dowolne aczkolwiek używany podczas projektu był Eclipse.
3. Ustawiamy zmienne środowiskowe:
```
JAVA_HOME = C:\Program Files\Java\jdk1.8.1_15 
NRGSDK = C:\Program Files (x86)\Ericsson\Network Resource Gateway SDK\R5A02
ANT_HOME = %NRGSDK%\tools\ant
PATH += %NRGSDK%\simulator\lib\net\erlang\bin
PATH += %ANT_HOME%\bin
PATH += %JAVA_HOME%\bin
```
Następnie wykonujemy restart komputera

### Sprawdzamy działanie symulatora i połączenia telefoniczne wewnątrz symulatora na przykładzie aplikacji Where Am I
1. Włączamy NRG Simulator (C:\Program Files (x86)\Ericsson\Network Resource Gateway SDK\R5A02\run_simulator.bat) - najpierw wyskoczy nam cmd, następnie po jakiejś chwili zależnie od systemu - symulator.
2. Dodajemy telefon : Edit -> Add phone -> dowolny adres, np 10123, model P800 lub P900 - zależy, który nam się podoba bardziej.
3. Włączamy mapę : Edit -> Show map - powinno być widać dwa telefony na mapie, które można po niej przesuwać - zmieniając ich współrzędne.
4. Odpalamy testową aplikacje: Applications -> Run -> Where Am I -> Run
5. Dostaniemy wiadomość popup z informacją o aplikacji i okno na logi. Na oknie whereAmI naciskamy start. 
6. Naciskamy na nasz telefon, a dokładniej na drugą ikonę, aby wysłać SMS'a. Treść smsa jest dowolna, byle na numer 6666.
7. Jeśli wszystko poszło pomyślnie to pod ikoną koperty będziemy mogli odberać wiadomość MMS z mapą i naszą aktualną lokalizacją.



### Odebranie MMS'a z poprawną lokalizacją potwierdza poprawne działanie symulatora dzięki czemu możemy zająć się naszą aplikacją 
11. Zaciągamy repozytorium i uruchamiamy je przez nasze IDE.
12. Wyłączamy aplikację whereAmI i zamykamy okno z logami - symulator ma być w stanie "idle".
13. Uruchamiamy projekt waipowy w IDE i sprawdzamy logi czy wszystko się uruchomiło.
14. Powinno ukazać się podobne okno z informacją o aplikacji, jeśli tak się stało naciskamy przycisk START.
15. Reszta przebiega według aplikacji i informacji napisanej w okienku, w którym nacisnęliśmy START.


