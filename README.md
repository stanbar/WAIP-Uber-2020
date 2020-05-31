# INSTRUKCJA:
### Instalacja SDK Ericssona(Windows 10)
1. Instalujemy SDK Ericssona + Java 1.8 (plik: WAIP_CD\Parlay\sdk\files\ericsson_nrgsdk_R5A02_setup.exe)
2. Ogarniamy środowisko do Javy : Eclipse/IntelliJ, z konsoli nie damy rady. Preferowane: IntelliJ IDEA Community
3. Ustawiamy zmienne środowiskowe:
```
JAVA_HOME = C:\Program Files\Java\jdk1.8.1_15 
NRGSDK = C:\Program Files (x86)\Ericsson\Network Resource Gateway SDK\R5A02
ANT_HOME = %NRGSDK%\tools\ant
PATH += %NRGSDK%\simulator\lib\net\erlang\bin
PATH += %ANT_HOME%\bin
PATH += %JAVA_HOME%\bin
```
Reset kompa



### Najpierw sprawdzamy czy działa nam ogólnie symulator i połączenie z telefonami wewnątrz symulatora!
1. Odpalamy NRG Simulator (C:\Program Files (x86)\Ericsson\Network Resource Gateway SDK\R5A02\run_simulator.bat) - najpierw wyskoczy cmd'ek i wam pobuduje jakieś śmieszne rzeczy, potem wystartuje GUI - cierpliwości.
2. Dodajemy telefon : Edit -> Add phone -> dowolny adres, np 10123, model P800 może być - obojętne w sumie.
3. Odpalamy mape : Edit -> Show map - powinno widać dwa telefony na mapie, które można po niej przesuwać - zmieniając ich współrzędne.
4. Odpalamy testową aplikacje: Applications -> Run -> Where Am I -> Run
5. Dostaniemy popup z informacją o aplikacji i okno na logi. Na oknie whereAmI naciskamy start. 
6. Naciskamy na nasz magiczny fajfon, a dokładniej na drugą ikonę, aby wysłać SMS'a. Treść smsa dowolna, byle na numer 6666.
7. Jeżeli wszystko poszło dobrze, to pod kopertą (ostatnia ikona), będzie do odebrania MMS z mapą i naszą aktualną lokalizacją.



### Odebranie MMSa z poprawną lokalizacją potwierdza, iż symulator bangla i można przejść do testowania projektu 
11. Zaciągamy to repo (po lewej jest przycisk Download) i wrzucić projekt do IDE
12. Wyłączamy whereAmI i zamykamy okno z logami - symulator ma być w stanie "idle".
13. Uruchamiamy projekt waipowy w IDE i paczymy w logi, czy wszystko wstało.
14. Powinno ukazać się podobne okno z informacją o aplikacji, po czym wciśnięcie START nie powinno skutkować wybuchem aplikacji
15. Jeżeli uruchomiony projekt w IDE śmiga, można normalnie bawić się w symulatorze, z tym, że aplikacja rusza z IDE, a nie prosto z tego śmiesznego Ericsson Network Resource Gateway Simulator.


