     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Aggiunto calcolo tempo impiegato
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  SETGT READPE su VERAPG0F con chiavi uguali
     D*  Programma SPL: 26_SetGTAndReadPE_SameKey_100_VERAPG0F
     V*=====================================================================
     H/COPY QILEGEN,£INIZH
      * File
     FVERAPG3L  IF   E           K DISK
      *---------------------------------------------------------------
     D $$IDOJ          S             10
     D $$NOME          S             15
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
     C                   KFLD                    V£CDC
      *
     C                   EVAL      V£CDC='SMEGL.001'
      *
     C     KEY001        SETGT     VERAPG3L
      *
     C                   DO        100
      *
     C     KEY001        READPE    VERAPG3L
      *
     C                   ENDDO
      *
     C                   IF        V£IDOJ <> ''
    MU* VAL1($$IDOJ) VAL2('0002209591') COMP(EQ)
     C                   EVAL      $$IDOJ=V£IDOJ
    MU* VAL1($$NOME) VAL2('MOSPAO         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$IDOJ) VAL2('0002209591') COMP(EQ)
     C                   EVAL      $$IDOJ='NOT FOUND'
    MU* VAL1($$NOME) VAL2('MOSPAO         ') COMP(EQ)
     C                   EVAL      $$NOME='NOT FOUND'
      *
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
