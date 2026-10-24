
package tn.fst.tp;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

public class NumZeroTest {


    private static ArrayOperations NumZero;
    
    @BeforeAll
    static void setup() {
        NumZero = new ArrayOperations();
    }
    private int[] parseArray(String input) {
        if (input.equals("null")) {
            return null;
        }

        if (input.equals("[]")) {
            return new int[0];
        }

        String[] values = input.split(",");
        int[] array = new int[values.length];

        for (int i = 0; i < values.length; i++) {
            array[i] = Integer.parseInt(values[i].trim());
        }

        return array;
    }

    private void verify(String input, String expected) {
        int[] array = parseArray(input);

        if (expected.equals("EXCEPTION")) {
            assertThrows(
                NullPointerException.class,
                () -> NumZero.numZero(array)
            );
        } else {
            assertEquals(
                Integer.parseInt(expected),
                NumZero.numZero(array)
            );
        }
    }


    /*
    
    LES TESTS POUR LES COUVERTURES 
    */

    //Node Coverage (NC)

    @Disabled 
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "null;EXCEPTION",
        "0;1"
    })
    void nodeCoverage(String input, String expected) {
        verify(input, expected);
    }

    /* Coverage = 95% */
    /* En théorie des graphes (cours Amman & Offutt / IGL5) : Chaque instruction/nœud du code a été visité au moins une fois (count++, throw, return), donc le Node Coverage (NC) formel est à 100%.
    Dans les outils réels (JaCoCo) : L'outil mesure la couverture des instructions bytecode et des branches décisionnelles. Dès qu'une décision n'est pas explorée dans ses deux issues (true et false), elle n'est pas considérée comme entièrement couverte. */

    // Edge Coverage (EC)
    @Disabled
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "null;EXCEPTION",
        "0;1",
        "5;0"
    })
    void edgeCoverage(String input, String expected) {
        verify(input, expected);
    }
    /* Coverage = 100% */



    // 3. EDGE-PAIR COVERAGE
    @Disabled 
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "null;EXCEPTION",//18
        "[];0",//1237
        "0;1",//12345637
        "5,7;0",//1234634637
    })
    void edgePairCoverage(String input, String expected) {
        verify(input, expected);
    }
    /* Coverage = 100% */

    

    // 4. PRIME-PATH COVERAGE
    @Disabled 
    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
        "null;EXCEPTION",//18
        "[];0",//1237
        "0;1",//12345637
        "5,7;0",//1234634637
        "5,0;1",//p5
        "5,0,0;2",//p6
    })
    void primePathCoverage(String input, String expected) {
        verify(input, expected);
    }
    /* Coverage = 100% */

   
}
