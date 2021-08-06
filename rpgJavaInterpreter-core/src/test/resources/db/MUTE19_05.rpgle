     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Aggiunto calcolo tempo impiegato
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  CHAIN su VERAPG0L - Record non trovati
     D*  Programma SPL: 05_CHAIN_NoFound_VERAPG0F
     V*=====================================================================
     H/COPY QILEGEN,£INIZH
      * File
     FVERAPG3L  IF   E           K DISK
      *---------------------------------------------------------------
     D $FOUND          S              1
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
     C                   KFLD                    V£NOME
      *
     C                   EVAL      V£CDC='SMEGL.001'
     C                   EVAL      V£DATA=20210111
     C                   EVAL      V£NOME='BONMAI'
      *
     C     KEY001        CHAIN     VERAPG3L
      *
     C                   IF        NOT(%FOUND)
    MU* VAL1($FOUND) VAL2('0') COMP(EQ)
     C                   EVAL      $FOUND='0'
     C                   ELSE
    MU* VAL1($FOUND) VAL2('0') COMP(EQ)
     C                   EVAL      $FOUND='FOUND'
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
