     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Aggiunto calcolo tempo impiegato
     V* 05/08/21  003102  BUSFIO Aggiunta entry
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
    MU* VAL1($$DEAR) VAL2('ATTACCO TWINAX SCS INFO PRINT      ') COMP(EQ)
     C                   EVAL      $$DEAR=A§DEAR
     C                   ELSE
    MU* VAL1($$TIAR) VAL2('ART  ') COMP(EQ)
     C                   EVAL      $$TIAR='NOT FOUND'
    MU* VAL1($$ARTI) VAL2('TWXIB0002      ') COMP(EQ)
     C                   EVAL      $$ARTI='NOT FOUND'
    MU* VAL1($$DEAR) VAL2('ATTACCO TWINAX SCS INFO PRINT      ') COMP(EQ)
     C                   EVAL      $$DEAR='NOT FOUND'
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
     C                   EVAL      MU_TSNAME = '25_SetGTAndReadPE_DiffKey'      COSTANTE
     C                                        +'_100_BRARTI0F'                  COSTANTE
     C                   EVAL      MU_FLNAME = 'BRARTI0F'                       COSTANTE
     C                   EVAL      MU_TPOPER = 'SETGT READPE'                   COSTANTE
     C*                   ENDIF
      *
     C                   SETON                                        LR
