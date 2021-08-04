     V*=====================================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V*=====================================================================
     V* 03/08/21  003102  BUSFIO Creazione
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
      *---------------------------------------------------------------
     I/COPY QILEGEN,£TABB£1DS
     I/COPY QILEGEN,£PDS
      *---------------------------------------------------------------
     D* M A I N
      *---------------------------------------------------------------
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
     C                   KFLD                    V£IDOJ
      *
     C                   ENDSR