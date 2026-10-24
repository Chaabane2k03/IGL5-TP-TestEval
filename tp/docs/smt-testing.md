# SMT based testing with Z3

This TP now contains a set of **SMT based tests**: the expectations are not hard coded in the
test data, they are computed by the [Z3](https://github.com/Z3Prover/z3) theorem prover.

Two complementary usages are illustrated:

| Usage | Question answered by the solver | Example test |
|-------|----------------------------------|--------------|
| **Test generation** | *Which input satisfies this property / path condition ?* | `solverGeneratesAnArrayWithExactlyKZeros` |
| **Proof** | *Does this property hold for **every** input ?* | `solverProvesTheTwoFormulationsAreEquivalent` |

---

## 1. Installing the solver

Z3 is **not** installed as a Maven dependency. The official Java bindings (`com.microsoft.z3`) are
not published on Maven Central, and the matching native library `libz3java.so` is dynamically
linked against a 40 MB `libz3.so` (plus JNI/native access restrictions on recent JDKs such as the
Java 27 used here). The solver is therefore used through its **SMT-LIB2 command line front-end**,
which works with any Z3 installation.

```bash
./scripts/install-z3.sh          # installs Z3 in tools/z3 (git ignored)
```

The script tries, in order:

1. an existing `z3` on the `PATH` (it is then linked into `tools/z3/bin/z3`),
2. the `z3-solver` PyPI wheel (it ships the `z3` executable), installed in `tools/z3-venv`,
3. the official release archive downloaded from GitHub.

The strategy can be forced with `Z3_INSTALL_METHOD=path|pip|release`, and the version of the
official release can be chosen with `Z3_VERSION` (default `4.13.4`):

```bash
Z3_INSTALL_METHOD=release Z3_VERSION=4.13.4 ./scripts/install-z3.sh
```

If you prefer a system wide installation, any of these works as well:

```bash
sudo pacman -S z3        # Arch
sudo apt install z3      # Debian / Ubuntu
brew install z3          # macOS
pip install z3-solver    # any platform
```

The tests locate the binary in this order:

1. the `z3.bin` system property (`./mvnw test -Dz3.bin=/usr/bin/z3`),
2. the `Z3_BIN` environment variable,
3. `tools/z3/bin/z3` inside the project (installed by the script),
4. `z3` on the `PATH`.

When no binary is found the SMT tests are **skipped** (with a warning) instead of breaking the
build, so the rest of the TP still runs.

## 2. Running the tests

```bash
./mvnw test                                  # everything
./mvnw test -Dtest='Smt*Test'                # only the SMT based tests
```

## 3. The harness (`tn.fst.tp.smt`)

| Class | Role |
|-------|------|
| `Z3Solver` | Runs a SMT-LIB2 script with Z3, returns the status (`sat` / `unsat` / `unknown`) and the values of the requested constants. |
| `SExpr` | Tiny reader for the S-expressions printed by Z3 (models, objectives). |
| `SmtScript` | Fluent builder producing SMT-LIB2 scripts. |
| `IntArraySmt` | Encoding of an array of integers (`a0`, `a1`, ...) and of the properties of `numZero` (number of zeros, number of non zeros, domains, ...). |

### Writing a new SMT test

```java
IntArraySmt array = IntArraySmt.ofLength(3);
SmtScript script = SmtScript.create()
        .setLogic("QF_LIA")
        .declareConsts(array.variables(), "Int")
        .assertFormula(array.withinDomain(-3, 3))          // -3 <= ai <= 3
        .assertFormula("(= " + array.zeroCount() + " 2)")  // exactly two zeros
        .checkSat()
        .getValue(array.variables());

Z3Solver.Result result = solver.solve(script.build());

assertTrue(result.isSat());
int[] input = array.toIntArray(result);   // the witness produced by the solver
assertEquals(2, new ArrayOperations().numZero(input));
```

## 4. The tests that are provided

`SmtNumZeroTest` (the code under test is `ArrayOperations#numZero`):

| Test | What the solver does |
|------|----------------------|
| `solverGeneratesAnArrayWithExactlyKZeros` | Generates a witness holding exactly *k* zeros (*k* = 0..3) and checks the implementation on it. |
| `solverGeneratesTheInputsOfEveryPath` | Generates one witness per path of the loop (empty array, no zero, one zero, all zeros) — the SMT counterpart of the prime path coverage tests of `NumZeroTest`. |
| `solverProvesTheTwoFormulationsAreEquivalent` | Proves that `zeros == length - nonZeros` for **every** array (the negation is `unsat`). |
| `solverProvesTheNumberOfZerosNeverExceedsTheLength` | Proves that the number of zeros can never be greater than the length of the array. |
| `solverFindsTheCounterexampleOfAMutant` | Asks for an input where a mutant (counting the non zeros) differs from the specification, and checks that the generated input really kills the mutant. |
| `solverMaximisesTheNumberOfZeros` | Uses `(maximize ...)` to obtain the optimal array. |
| `solverHandlesTheExtremeIntegerValues` | Generates an array holding `Integer.MIN_VALUE`, `Integer.MAX_VALUE` and `0`. |
| `solverGeneratesInputsForEveryLength` | Generates distinct, non zero arrays for every length. |

`SmtCalculatriceTest` (the code under test is `Calculatrice#divide`):

| Test | What the solver does |
|------|----------------------|
| `solverFindsTheRejectedDivisor` | Characterises the inputs rejected by the guard (the divisor must be zero). |
| `solverFindsAnAcceptedDivisor` | Generates a valid divisor and checks the result of the division. |
| `solverShowsThatOnlyZeroIsRejected` | Shows that every divisor different from zero is accepted. |

## 5. Using the official Java API instead

If you prefer to write the formulas with the Java API of Z3, install the bindings manually
(nothing is published on Maven Central):

```bash
# 1. the managed part (z3.jar) in the local Maven repository
curl -LO https://github.com/Z3Prover/z3/releases/download/z3-4.13.4/z3-4.13.4-x64-glibc-2.35.zip
unzip -q z3-4.13.4-x64-glibc-2.35.zip
mvn install:install-file -Dfile=z3-4.13.4-x64-glibc-2.35/java/com.microsoft.z3.jar \
                  -DgroupId=com.microsoft.z3 -DartifactId=z3 -Dversion=4.13.4 -Dpackaging=jar

# 2. the native library (libz3java.so + libz3.so) and the JVM flag
export LD_LIBRARY_PATH=$PWD/z3-4.13.4-x64-glibc-2.35/bin
export MAVEN_OPTS="-Djava.library.path=$PWD/z3-4.13.4-x64-glibc-2.35/bin --enable-native-access=ALL-UNNAMED"
```

Then add the dependency to `pom.xml`:

```xml
<dependency>
    <groupId>com.microsoft.z3</groupId>
    <artifactId>z3</artifactId>
    <version>4.13.4</version>
    <scope>test</scope>
</dependency>
```

The `Z3Solver` class of this TP could then be replaced by a `Z3Solver` built on `com.microsoft.z3.Context`
without changing the tests.
