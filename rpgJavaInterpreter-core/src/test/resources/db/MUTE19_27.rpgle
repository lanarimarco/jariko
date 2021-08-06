     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Aggiunto calcolo tempo impiegato
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  SETGT READPE su BRARTI0F con chiavi uguali
     D*  Programma SPL: 27_SetGTAndReadPE_SameKey_100_BRARTI0F
     V*=====================================================================
     H/COPY QILEGEN,£INIZH
      * File
     FBRARTI2L  IF   E           K DISK
      *---------------------------------------------------------------
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
     C                   EVAL      A§TIAR='ART'
      *
     C     KEY001        SETGT     BRARTI2L
      *
     C                   DO        100
      *
     C     KEY001        READPE    BRARTI2L
      *
     C                   ENDDO
      *
     C                   IF        A§ARTI <> ''
    MU* VAL1($$ARTI) VAL2('TWXIB0002      ') COMP(EQ)
     C                   EVAL      $$ARTI=A§ARTI
    MU* VAL1($$DEAR) VAL2('ATTACCO TWINAX SCS') COMP(CN)
     C                   EVAL      $$DEAR=A§DEAR
     C                   ELSE
    MU* VAL1($$ARTI) VAL2('TWXIB0002      ') COMP(EQ)
     C                   EVAL      $$ARTI='NOT FOUND'
    MU* VAL1($$DEAR) VAL2('ATTACCO TWINAX SCS') COMP(CN)
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
