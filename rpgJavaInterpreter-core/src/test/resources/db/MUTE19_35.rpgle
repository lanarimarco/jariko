     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 04/08/21  003102  BUSFIO Creazione
     V* 05/08/21  003102  BUSFIO Aggiunta entry
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  SETLL READE su VERAPG0F con chiavi uguali
     D*  Programma SPL: 35_SetllAndReadE_SameKey_100_VERAPG0F
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
      *
     D MU_TIME         S             10                                          Entry - Tempo
     D MU_TSNAME       S             45                                          Entry - Test name
     D MU_FLNAME       S             10                                          Entry - File name
     D MU_TPOPER       S             15                                          Entry - Type oper
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
     C                   KFLD                    V£CDC
      *
     C                   EVAL      V£CDC='SMEGL.001'
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
    MU* VAL1($$IDOJ) VAL2('0001569098') COMP(EQ)
     C                   EVAL      $$IDOJ=V£IDOJ
    MU* VAL1($$NOME) VAL2('MAEOLI         ') COMP(EQ)
     C                   EVAL      $$NOME=V£NOME
     C                   ELSE
    MU* VAL1($$IDOJ) VAL2('0001569098') COMP(EQ)
     C                   EVAL      $$IDOJ='NOT FOUND'
    MU* VAL1($$NOME) VAL2('MAEOLI         ') COMP(EQ)
     C                   EVAL      $$NOME='NOT FOUND'
      *
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
     C*                   IF        £PDSPR>0
     C                   EVAL      MU_TIME = %CHAR($TIMMS)
     C                   EVAL      MU_TSNAME = '35_SetllAndReadE_SameKey'       COSTANTE
     C                                        +'_100_VERAPG0F'                  COSTANTE
     C                   EVAL      MU_FLNAME = 'VERAPG0F'                       COSTANTE
     C                   EVAL      MU_TPOPER = 'SETLL READE'                    COSTANTE
     C*                   ENDIF
      *
     C                   SETON                                        LR
