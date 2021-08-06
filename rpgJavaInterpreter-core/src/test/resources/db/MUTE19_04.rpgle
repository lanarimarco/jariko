     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Aggiunto calcolo tempo impiegato
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  CHAIN su BRARTI0L - 5 chain ripetute 10 volte
     D*  Programma SPL: 04_CHAIN_5Keys10Time_BRARTI0F
     V*=====================================================================
     H/COPY QILEGEN,£INIZH
      * File
     FBRARTI0L  IF   E           K DISK
      *---------------------------------------------------------------
     D $DESC           S             35
      *
     D $TIMST          S               Z   INZ                                   Tempo iniziale
     D $TIMEN          S               Z   INZ                                   Tempo finale
     D $TIMMS          S             10I 0                                       Tempo millisecondi
      *
     D $MSG            S             52                                          Output
      *---------------------------------------------------------------
     I/COPY QILEGEN,£TABB£1DS
     I/COPY QILEGEN,£PDS
      *---------------------------------------------------------------
     D* M A I N
      *---------------------------------------------------------------
      * Begin time
     C                   TIME                    $TIMST
      *
     C     KEY001        KLIST
     C                   KFLD                    A§ARTI
      * Prima CHAIN
     C                   EVAL      A§ARTI='ASACC0001'
      *
     C                   DO        10
      *
     C     KEY001        CHAIN     BRARTI0L
      *
     C                   IF        %FOUND
    MU* VAL1($DESC) VAL2('CAVO EIA') COMP(CN)
     C                   EVAL      $DESC=A§DEAR
     C                   ELSE
    MU* VAL1($DESC) VAL2('CAVO EIA') COMP(CN)
     C                   EVAL      $DESC='NOT FOUND'
     C                   ENDIF
      *
     C                   ENDDO
      * ---------------------
      *  Seconda CHAIN
     C                   EVAL      A§ARTI='ASACC0002'
      *
     C                   DO        10
      *
     C     KEY001        CHAIN     BRARTI0L
      *
     C                   IF        %FOUND
    MU* VAL1($DESC) VAL2('CAVO V.35') COMP(CN)
     C                   EVAL      $DESC=A§DEAR
     C                   ELSE
    MU* VAL1($DESC) VAL2('CAVO V.35') COMP(CN)
     C                   EVAL      $DESC='NOT FOUND'
     C                   ENDIF
      *
     C                   ENDDO
      * ---------------------
      * Terza CHAIN
     C                   EVAL      A§ARTI='ASACC0003'
      *
     C                   DO        10
      *
     C     KEY001        CHAIN     BRARTI0L
      *
     C                   IF        %FOUND
    MU* VAL1($DESC) VAL2('ADATTATORE TWINAX') COMP(CN)
     C                   EVAL      $DESC=A§DEAR
     C                   ELSE
    MU* VAL1($DESC) VAL2('ADATTATORE TWINAX') COMP(CN)
     C                   EVAL      $DESC='NOT FOUND'
     C                   ENDIF
      *
     C                   ENDDO
      * ---------------------
      * Quarta CHAIN
     C                   EVAL      A§ARTI='ASACC0004'
      *
     C                   DO        10
      *
     C     KEY001        CHAIN     BRARTI0L
      *
     C                   IF        %FOUND
    MU* VAL1($DESC) VAL2('ESPANSIONE 40 TERMINALI') COMP(CN)
     C                   EVAL      $DESC=A§DEAR
     C                   ELSE
    MU* VAL1($DESC) VAL2('ESPANSIONE 40 TERMINALI') COMP(CN)
     C                   EVAL      $DESC='NOT FOUND'
     C                   ENDIF
      *
     C                   ENDDO
      * ---------------------
      * Quinta CHAIN
     C                   EVAL      A§ARTI='ASACC0005'
      *
     C                   DO        10
      *
     C     KEY001        CHAIN     BRARTI0L
      *
     C                   IF        %FOUND
    MU* VAL1($DESC) VAL2('PCI RAID DISK') COMP(CN)
     C                   EVAL      $DESC=A§DEAR
     C                   ELSE
    MU* VAL1($DESC) VAL2('PCI RAID DISK') COMP(CN)
     C                   EVAL      $DESC='NOT FOUND'
     C                   ENDIF
      *
     C                   ENDDO
      * End Time
     C                   TIME                    $TIMEN
      * Elapsed time
     C     $TIMEN        SUBDUR    $TIMST        $TIMMS:*MS
     C                   EVAL      $TIMMS=$TIMMS/1000
      *
     C                   EVAL      $MSG = %CHAR($TIMMS)
     C     $MSG          DSPLY     £PDSNU
      *
     C                   SETON                                        LR