     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Calcolo tempo impiegato e aggiunta Entry
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  CHAIN su VERAPG0L
     D*  Programma SPL: 01_CHAIN_5Keys1Time_VERAPG0F
     V*=====================================================================
     H/COPY QILEGEN,£INIZH
      * File
     FVERAPG0L  IF   E           K DISK
      *---------------------------------------------------------------
     D $$NOME          S             15
      *
     D $TIMST          S               Z   INZ                                   Tempo iniziale
     D $TIMEN          S               Z   INZ                                   Tempo finale
     D $TIMMS          S             10I 0                                       Tempo millisecondi
      *
     D $MSG            S             52                                          Output
      *
     D MU_TIME         S             10                                          Entry - Tempo
     D MU_TSNAME       S             45                                          Entry - Test name
     D MU_FLNAME       S             10                                          Entry - File name
     D MU_TPOPER       S             15                                          Entry - Type oper
     D VALPAR          S              5  0
      *---------------------------------------------------------------
     I/COPY QILEGEN,£TABB£1DS
     I/COPY QILEGEN,£PDS
      *---------------------------------------------------------------
     D* M A I N
      *---------------------------------------------------------------
     C     *ENTRY        PLIST
     C                   PARM                    MU_TIME
     C                   PARM                    MU_TSNAME
     C                   PARM                    MU_FLNAME
     C                   PARM                    MU_TPOPER
      * Begin time
     C                   TIME                    $TIMST
      *
     C     KEY001        KLIST
     C                   KFLD                    V£IDOJ
      * Prima CHAIN
     C                   EVAL      V£IDOJ='0001172375'
      *
     C     KEY001        CHAIN     VERAPG0L
      *
     C                   IF        %FOUND
    MU* VAL1($$NOME) VAL2('BAGWIL         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$NOME) VAL2('BAGWIL         ') COMP(EQ)
     C                   EVAL      $$NOME='NOT FOUND'
     C                   ENDIF
      * ---------------------
      *  Seconda CHAIN
     C                   EVAL      V£IDOJ='0000993361'
      *
     C     KEY001        CHAIN     VERAPG0L
      *
     C                   IF        %FOUND
    MU* VAL1($$NOME) VAL2('TUBGIU         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$NOME) VAL2('TUBGIU         ') COMP(EQ)
     C                   EVAL      $$NOME='NOT FOUND'
     C                   ENDIF
      * ---------------------
      * Terza CHAIN
     C                   EVAL      V£IDOJ='0001993495'
      *
     C     KEY001        CHAIN     VERAPG0L
      *
     C                   IF        %FOUND
    MU* VAL1($$NOME) VAL2('MATMAN         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$NOME) VAL2('MATMAN         ') COMP(EQ)
     C                   EVAL      $$NOME='NOT FOUND'
     C                   ENDIF
      * ---------------------
      * Quarta CHAIN
     C                   EVAL      V£IDOJ='0002206817'
      *
     C     KEY001        CHAIN     VERAPG0L
      *
     C                   IF        %FOUND
    MU* VAL1($$NOME) VAL2('MAEOLI         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$NOME) VAL2('MAEOLI         ') COMP(EQ)
     C                   EVAL      $$NOME='NOT FOUND'
     C                   ENDIF
      * ---------------------
      * Quinta CHAIN
     C                   EVAL      V£IDOJ='0002214142'
      *
     C     KEY001        CHAIN     VERAPG0L
      *
     C                   IF        %FOUND
    MU* VAL1($$NOME) VAL2('BANGIA         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$NOME) VAL2('BANGIA         ') COMP(EQ)
     C                   EVAL      $$NOME='NOT FOUND'
     C                   ENDIF
      * End Time
     C                   TIME                    $TIMEN
      * Elapsed time
     C     $TIMEN        SUBDUR    $TIMST        $TIMMS:*MS
     C                   EVAL      $TIMMS=$TIMMS/1000
      *
     C*                   EVAL      $MSG = %CHAR($TIMMS)
     C*     $MSG          DSPLY     £PDSNU
      *
     C*                   EVAL      VALPAR = %PARMS
     C*                   IF        %PARMS>0
     C*                   IF        MU_TIME<>''
     C                   EVAL      MU_TIME = %CHAR($TIMMS)
     C                   EVAL      MU_TSNAME = '01_CHAIN_5Keys1Time_VERAPG0F'   COSTANTE
     C                   EVAL      MU_FLNAME = 'VERAPG0F'                       COSTANTE
     C                   EVAL      MU_TPOPER = 'CHAIN'                          COSTANTE
     C*                   ENDIF
     C*                   CLOSE     VERAPG0L
      *
     C                   SETON                                        LR
