     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 13/03/20  001693  BMA Created
     V*=====================================================================
     V* OBIETTIVO
     V*  Test program for GOTO
     V*
     V*=====================================================================
     D*----------------------------------------------------------------
     D Msg             S             10
     D TXT             S            100    DIM(10) PERRCD(1) CTDATA             _NOTXT
     D $TIMST          S               Z   INZ
     D $TIMEN          S               Z   INZ
     D $TIMMS          S             10  0
     D$MSG             S             52
     **********************************************************************
      * Start time
     C                   TIME                    $TIMST
     C                   DO        10000000
     C                   EVAL      Msg = 'Test OK'                              COSTANTE
     C                   goto      G8MAIN
     C                   EVAL      Msg = 'Test KO'                              COSTANTE
     **********************************************************************
     C     MSG           DSPLY     £PDSSU
     **********************************************************************
     C     G8MAIN        TAG
     C                   ENDDO
     C     MSG           DSPLY     £PDSSU
     C     G9MAIN        Tag
      * End time
     C                   TIME                    $TIMEN
      * Elapsed time
     C     $TIMEN        SUBDUR    $TIMST        $TIMMS:*MS
     C                   EVAL      $TIMMS=$TIMMS/1000
      * Display message
     C                   EVAL      $MSG=%trim(TXT(1))+' '+
     C                             %TRIM(%EDITC($TIMMS:'Q'))+'ms'
     C     $MSG          DSPLY     £PDSSU
    MU* TIMEOUT(100)
     **********************************************************************
     C                   SETON                                        LR
** TXT
Time spent
