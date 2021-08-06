     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Aggiunto calcolo tempo impiegato
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  SETGT READPE su BRARTI0F con chiavi diverse
     D*  Programma SPL: 25_SetGTAndReadPE_DiffKey_100_BRARTI0F
     V*=====================================================================
     H/COPY QILEGEN,£INIZH
      * File
     FBRARTI2L  IF   E           K DISK
      *---------------------------------------------------------------
     D $$TIAR          S              5
     D $$DEAR          S             35
     D $$ARTI          S             15
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
     C                   KFLD                    A§TIAR
      *
     C     KEY002        KLIST
     C                   KFLD                    A§TIAR
     C                   KFLD                    A§ARTI
      *
     C                   EVAL      A§TIAR='ART'
     C                   EVAL      A§ARTI='1599573525396'
      *
     C     KEY002        SETGT     BRARTI2L
      *
     C                   DO        100
      *
     C     KEY001        READPE    BRARTI2L
      *
     C                   ENDDO
      *
     C                   IF        A§ARTI <> ''
    MU* VAL1($$TIAR) VAL2('ART  ') COMP(EQ)
     C                   EVAL      $$TIAR=A§TIAR
    MU* VAL1($$ARTI) VAL2('TWXIB0002      ') COMP(EQ)
     C                   EVAL      $$ARTI=A§ARTI
    MU* VAL1($$DEAR) VAL2('ATTACCO TWINAX SCS INFO') COMP(CN)
     C                   EVAL      $$DEAR=A§DEAR
     C                   ELSE
    MU* VAL1($$TIAR) VAL2('ART  ') COMP(EQ)
     C                   EVAL      $$TIAR='NOT FOUND'
    MU* VAL1($$ARTI) VAL2('TWXIB0002      ') COMP(EQ)
     C                   EVAL      $$ARTI='NOT FOUND'
    MU* VAL1($$DEAR) VAL2('ATTACCO TWINAX SCS INFO') COMP(CN)
     C                   EVAL      $$DEAR='NOT FOUND'
     C                   ENDIF
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
