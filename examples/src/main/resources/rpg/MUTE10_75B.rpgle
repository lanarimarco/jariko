   COP* *NOUI
     V* ==============================================================
     V* MODIFICHE Ril.  T Au Descrizione
     V* gg/mm/aa  nn.mm i xx Breve descrizione
     V* ==============================================================
     V* 19/11/20  002266  BMA Created
     V* ==============================================================
      *
     D $COUNT          S             10I 0
      *
      *--------------------------------------------------------------------------------------------*
      * ENTRY
      *--------------------------------------------------------------------------------------------*
     D  $RESPGM        S             50
      *--------------------------------------------------------------------------------------------*
     C                   EVAL      $COUNT=$COUNT+1
     C                   EVAL      $RESPGM='MUTE10_75B '
     C                             +%TRIM(%EDITC($COUNT:'Z'))
      *
     C                   SETON                                        LR
      *--------------------------------------------------------------*
    RD* Initialization routine
      *--------------------------------------------------------------*
     C     *INZSR        BEGSR
      * ENTRY
     C     *ENTRY        PLIST
     C                   PARM                    $RESPGM
      *
     C*                  EVAL      $COUNT=2000
      *
     C                   ENDSR
