import algorithms.GA
import problems.TSP
import utility.RandomUtils

// Tipi povezav -> EDGE_WEIGHT_TYPE
// 1. EXPLICIT (uteži med mesti)
//    EDGE_WEIGHT_FORMAT
//      - FULL_MATRIX (celotna matrika)
// 2. EUC_2D (kooordinate mest) (za razdaljo med mesti uporabi evklidsko razdaljo)

fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    println("Program arguments: ${args.joinToString()}")

    testTSP()
}

fun testTSP() {
    RandomUtils.setSeedFromTime() // nastavi novo seme ob vsakem zagonu main metode (vsak zagon bo drugačen)

    // primer zagona za problem eil101.tsp
//    for (i in 0..99) {
//        val eilTsp = TSP("eil101.tsp", 10000)
        val eilTsp = TSP("eil101.tsp", 10000)
        val ga = GA(100, 0.8, 0.1)
        val bestPath = ga.execute(eilTsp)
        println("Best path: ${bestPath?.distance} \nPath: ${bestPath?.path?.joinToString { it?.index.toString() }}")
        // shrani min, avg in std
//    }
    println("Seed: " + RandomUtils.getSeed()) // izpiše seme s katerim lahko ponovimo zagon
}