     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
     V*=====================================================================
     D*  OBIETTIVO
     D*  Programma finalizzato ai test di ottimizzazione di reload:
     D*  CHAIN su BRARTI0L
     D*  Programma SPL: 02_CHAIN_5Keys1Time_BRARTI0F
     V*=====================================================================
     H/COPY QILEGEN,£INIZH
      * File
     FBRARTI0L  IF   E           K DISK
      *---------------------------------------------------------------
     D $FOUND          S              1
      *---------------------------------------------------------------
     I/COPY QILEGEN,£TABB£1DS
     I/COPY QILEGEN,£PDS
      *---------------------------------------------------------------
     D* M A I N
      *---------------------------------------------------------------
     C     KEY001        KLIST
     C                   KFLD                    A§ARTI
      * Prima CHAIN
     C                   EVAL      A§ARTI='ABBONAEXECUTIVE'
     C                   EVAL      A§ARTI='ASACC0001'
     C                   EVAL      A§ARTI='ASACC0001      '
      *
     C     KEY001        CHAIN     BRARTI0L
      *
     C                   IF        %FOUND
    MU* VAL1($FOUND) VAL2('1') COMP(EQ)
     C                   EVAL      $FOUND='1'
     C                   ELSE
    MU* VAL1($FOUND) VAL2('1') COMP(EQ)
     C                   EVAL      $FOUND='NOT FOUND'
     C                   ENDIF
      *
     C                   SETON                                        LR
      *---------------------------------------------------------------
     C/COPY QILEGEN,£INZSR
      *---------------------------------------------------------------
    RD* ROUTINE INIZIALE
      *--------------------------------------------------------------*
     C     £INIZI        BEGSR
      *
     C     KEY001        KLIST
     C                   KFLD                    A§ARTI
      *
     C                   ENDSR