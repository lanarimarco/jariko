     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 04/08/21  003102  BUSFIO Creazione
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  SETLL READE su VERAPG0F con chiavi uguali
     D*  Programma SPL: 30_SetllAndReadE_2SameKey_100_VERAPG0F
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
     C                   KFLD                    V£DATA
      *
     C                   EVAL      V£CDC='AFE'
     C                   EVAL      V£DATA=20190814
      *
     C     KEY001        SETLL     VERAPG3L
      *
     C                   DO        100
      *
     C     KEY001        READE     VERAPG3L
      *
     C                   ENDDO
      *
     C                   IF        V£IDOJ <> ''
    MU* VAL1($$IDOJ) VAL2('0001728518') COMP(EQ)
     C                   EVAL      $$IDOJ=V£IDOJ
    MU* VAL1($$NOME) VAL2('FERCRI         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$IDOJ) VAL2('0001728518') COMP(EQ)
     C                   EVAL      $$IDOJ='NOT FOUND'
    MU* VAL1($$NOME) VAL2('FERCRI         ') COMP(EQ)
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
