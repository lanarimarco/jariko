     V*=====================================================================
     V* Indicator test in composite statements
     V*=====================================================================

      * DO_1 must not be displayed
     C                   EVAL     *IN01=*OFF
     C   01              DO          1
     C      'DO_1'       DSPLY
     C                   ENDDO

      * DO_2 must be displayed
     C                   EVAL     *IN01=*ON
     C   01              DO          1
     C      'DO_2'       DSPLY
     C                   ENDDO