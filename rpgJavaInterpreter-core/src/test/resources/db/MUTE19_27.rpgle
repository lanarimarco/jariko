     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V* 04/08/21  003102  BUSFIO Aggiunto calcolo tempo impiegato
     V* 05/08/21  003102  BUSFIO Aggiunta entry
     V* 02/09/21  003102  BUSFIO Modificato controllo risultato
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
     D RESULT          S              3  0
     D $N              S              3  0
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
     C                   EVAL      $N = 0

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
     C                   EVAL      $N = $N +1
      *
     C                   ENDDO
      *
    MU* VAL1(RESULT) VAL2(100) COMP(EQ)
     C                   EVAL      RESULT = $N
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
     C                   EVAL      MU_TSNAME = '27_SetGTAndReadPE_SameKey'      COSTANTE
     C                                        +'_100_BRARTI0F'                  COSTANTE
     C                   EVAL      MU_FLNAME = 'BRARTI0F'                       COSTANTE
     C                   EVAL      MU_TPOPER = 'SETGT READPE'                   COSTANTE
     C*                   ENDIF
      *
     C                   SETON                                        LR
