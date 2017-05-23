# pardonas-bot
Pardona's Bot - Das Schwarze Auge in Discord!

Dieser Bot erlaubt DSA Proben in Discord. Möglich sind Eigenschaftsproben, Talentproben und Schadenswürfe mit Modifikatoren. Proben können offen oder geheim geworfen werden.


## Befehle
Alle Befehle können entweder als mention gesendet werden oder als direct message. Die Befehle folgen dem folgenden Muster.

 - **name** _arg1_ _arg2_ ... \[opt. arg1\] \[opt. arg2\] ...

### Eigenschaftsproben
Es kann eine Probe gegen einen Eigenschaftswert geworfen werden (oder Attacke oder eine andere Probe mit einem w20). Die Erschwernis kann optional als zweites Argument angegeben werden.

 - **e** _Eigenschaftswert_ \[Erschwernis\]
 - **eigenschaft** _Eigenschaftswert_ \[Erschwernis\]

### Talentproben
Es kann eine Talentprobe geworfen werden. Der Talentwert kann als erstes optional Argument angegeben werden. Die Erschwernis kann als zweites optionales Argument angegeben werden. Nur eine Erschwernis ohne Talentwert ist nicht möglich. Ist kein Wert angegeben, wird ein TaW von 0 angenommen.

 - **t** _Eigenschaftswert1_ _Eigenschaftswert2_ _Eigenschaftswert3_ \[Talentwert\] \[Erschwernis\]
 - **talent** _Eigenschaftswert1_ _Eigenschaftswert2_ _Eigenschaftswert3_ \[Talentwert\] \[Erschwernis\]

### Generelle Würfelwürfe
Es können auch beliebige Würfel geworfen werden. Die Ergebnisse können addiert und subtrahiert werden.

 - **r** _Würfelausdruck_
 - **roll** _Würfelausdruck_

Ein Würfelausdruck besteht aus Würfelangaben in der Form _"nwx"_ oder _"ndx"_, wobei _n_ die Anzahl der Würfel ist und _x_ die Anzahl der Seiten der Würfel. Fehlt _n_, wird 1 angenommen. Ebenfalls sind fixe Zahlen möglich. Solche Ausdrücke können mit _+_ oder _-_ verbunden werden. Leerzeichen können benutzt werden, sind aber optional.

Beispiele:
 - 2w6
 - d3 + 2
 - 2w6-1
 - 1w20 + 2d14
 - 3-w3

## Modifikatoren
Bevor einem Befehl können Modifikatoren angegeben werden. Diese beeinflussen, wie das Ergebnis zurückgegeben wird.

### Versteckte Proben
Das Ergebnis einer Probe kann einem Empfänger als direct message mitgeteilt werden.

 - **h** _Empfänger_ _Befehl_
 - **hidden** _Empfänger_ _Befehl_
