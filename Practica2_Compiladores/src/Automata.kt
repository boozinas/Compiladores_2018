import java.io.File
import kotlin.collections.ArrayList


class Automata(val alfabeto: ArrayList<String>, var inicial: Int, var finales : ArrayList<Int>, var tablaTransisiones: ArrayList<Transicion>, val estados: ArrayList<Estado>) {

    val alfabetoChar = alfabeto.toString().toCharArray()
    //var cadenaChar = "$cadena ".toCharArray()
    var contador = 0
    var caminos = ArrayList<Camino>()
    var errores = ArrayList<Any>()
    var banderaGlobal = false
    val estados1 = estados
    var resultado = ArrayList<ArrayList<Camino>>()


    fun encontrar(cadenaEntrada: String) {
        var cadenaEntradaChar = "$cadenaEntrada ".toCharArray()
        println(estados1[estados1.size-1].numeroEstado)
        caminos.add(Camino(inicial, ' '))
        manejoErroresIniciales(cadenaEntradaChar)
        if (contador < cadenaEntradaChar.size) llenarCaminos(inicial, caminos, contador, errores, cadenaEntradaChar,resultado)
        if (!banderaGlobal)
            println("Cadena no valida")
    }

    fun manejoErroresIniciales(cadenaEntradaChar: CharArray) {
        if ( cadenaEntradaChar[contador] == ' ')
            println("Ningun caracter de la cadena pertenece al alfabeto")
        else if (contador==0){
            if (!alfabetoChar.contains(cadenaEntradaChar[contador])) {
                contador++
                println("\nEliminacion  de caracteres iniciales \nque no pertenecen al alfabeto :")
                print("${cadenaEntradaChar[contador - 1]}")
                manejoErroresIniciales(cadenaEntradaChar)
            }
        }
        else if (contador < cadenaEntradaChar.size) {
            if (!alfabetoChar.contains(cadenaEntradaChar[contador])) {
                contador++
                print("${cadenaEntradaChar[contador - 1]}")
                manejoErroresIniciales(cadenaEntradaChar)
            }
        }
    }

    fun manejoErroresIntermedios(contador: Int, i: Int, errores: ArrayList<String>, cadenaEntradaChar: CharArray): MutableList<Any> {
        var contador2 = contador
        var cadenaError = errores
        val contadorError = mutableListOf<Any>()
        if ((contador2 < cadenaEntradaChar.size)) {
            if (!(alfabetoChar.contains(cadenaEntradaChar[contador2]))) {
                cadenaError.add("Eliminacion de caracter '${cadenaEntradaChar[contador2]}' en el estado ${tablaTransisiones[i].estadoSiguiente}")
                contador2++
                contadorError.add(contador2)
                contadorError.add(cadenaError)
                var falla = manejoErroresIntermedios(contador2, i, cadenaError, cadenaEntradaChar)
                return falla
            } else {
                contadorError.add(contador2)
                contadorError.add(cadenaError)
                return contadorError
            }
        }
        return contadorError
    }

    fun llenarCaminos(siguiente: Int, camino: ArrayList<Camino>, contador: Int, error: ArrayList<Any>, cadenaEntradaChar: CharArray, resultado: ArrayList<ArrayList<Camino>>): ArrayList<Camino> {
        loop@ for (i in tablaTransisiones.indices) {
            if ((this.tablaTransisiones[i].estadoActual == siguiente)  && (contador < cadenaEntradaChar.size)) {
                var contador2 = contador
                var camino2 = ArrayList<Camino>()
                var errores = ArrayList<Any>()
                var cadenasErrores = ArrayList<String>()
                var resultado2 = resultado
                errores.addAll(error)
                camino2.addAll(camino)
                //CAMBIOS CAMBIOS EPSILON


                when{
                    (tablaTransisiones[i].simboloTransicion==cadenaEntradaChar[contador]) -> {
                        camino2.add(Camino(tablaTransisiones[i].estadoSiguiente, tablaTransisiones[i].simboloTransicion))
                        contador2++
                       // println(camino2.toString())
                    }
                    tablaTransisiones[i].simboloTransicion=='E' -> {
                        camino2.add(Camino(tablaTransisiones[i].estadoSiguiente, tablaTransisiones[i].simboloTransicion))
                        if(contador2 == cadenaEntradaChar.size-1)
                            contador2++
                    }
                    else -> continue@loop
                }
                if ( finales.contains(camino2[camino2.size - 1].estadoActual) && (contador2 == cadenaEntradaChar.size - 1) && camino!=camino2) {
                    println("\n\nCadena aceptada*\n->${camino2.toString()}")
                    banderaGlobal = true
                    resultado2.add(camino2)
                    if (errores.isNotEmpty()) {
                        for (j in errores) {
                            if (j.toString() != "[]")
                                println(j)
                        }
                    }
                    //return camino2
                }



                //Manejo de errores intermedios en la cadena
                if(contador2>0) {
                    var supresion = manejoErroresIntermedios(contador2, i, cadenasErrores, cadenaEntradaChar)
                    if (supresion.isNotEmpty()) {
                        contador2 = supresion.get(0).toString().toInt()
                        errores.add(supresion.get(1).toString())
                       // println(contador2)
                  }
                }


                if (contador2 >= cadenaEntradaChar.size) {
                    return camino2
                } else {
                    var res = llenarCaminos(tablaTransisiones[i].estadoSiguiente, camino2, contador2, errores, cadenaEntradaChar, resultado2)
                    if ( finales.contains(res[res.size - 1].estadoActual) && (contador2 == cadenaEntradaChar.size - 1) && (camino2 != res)) {
                        println("\n\nCadena aceptada\n->${res.toString()}")
                        resultado2.add(res)
                        banderaGlobal= true
                        if (errores.isNotEmpty()) {
                            for (j in errores) {
                                if (j.toString() != "[]")
                                    println(j)
                            }
                        }
                        //println("La total ${resultado2.toString()}")
                        continue
                    } else{
                        continue
                    }
                }
            } else
                continue
        }
        return camino
    }
}

fun transiciones(elementosTransicion : ArrayList<String>, size : Int, alfabeto: ArrayList<String>, estados: ArrayList<Estado>):ArrayList<Transicion>{

    var i = 0
    //Funcion de Transicion
    var local: String
    var simbolo: String
    var externo: String
    val transiciones = ArrayList<Transicion>()
    while (i < size * 3) {
        local = elementosTransicion[i]
        i++
        simbolo = elementosTransicion[i]
        i++
        externo = elementosTransicion[i]
        i++
        transiciones.add(Transicion(local.toInt(), simbolo[0], externo.toInt()))
    }
    // Introducir transiciones de Error

    var alfabetoCopia = ArrayList<String>(alfabeto)
    println(alfabetoCopia.toString())
    var alfabetoTransicion = ArrayList<String>()
    for (temp in estados.indices) {
        alfabetoTransicion.clear()
        alfabetoCopia.clear()
        alfabetoCopia.addAll(alfabeto)
        //alfabetoCopia.add("E") //El estado de error contempla E
        for (temp2 in transiciones.indices) {
            if (estados[temp].numeroEstado == transiciones[temp2].estadoActual)
                alfabetoTransicion.add(transiciones[temp2].simboloTransicion.toString())
        }
        if (!(alfabetoTransicion.distinct().equals(alfabeto)) && !alfabetoTransicion.contains("E")) {
            alfabetoCopia.removeAll(alfabetoTransicion) //Contiene solo los que estan en transicion
            for (temp3 in alfabetoCopia.indices) {
                transiciones.add(Transicion(temp, alfabetoCopia[temp3][0], estados.size - 1))
            }
        }
    }


    return transiciones
}


fun main(args: Array<String>) {
    val br = File("src/automata.txt").bufferedReader()
    var size = br.readLines().size //Numero de lineas del archivo
    br.close()
    val bufferedReader = File("src/automata.txt").bufferedReader()
    val estados = ArrayList<Estado>() //Obteniendo el numero y el nombre del estado
    bufferedReader.readLine().split(",").map { estados.add(Estado(false, false, it.trim().toInt())) }
    estados.add(Estado(false, false, estados.size)) //Añadiendo estado de Error

    val alfabeto = ArrayList<String>() //Obteniendo el alfabeto en un arreglo de tipo cadena
    bufferedReader.readLine().split(",").map { alfabeto.add(it.trim()) }

    val inicial = bufferedReader.readLine().toInt() //Obteniendo el estado inicial
    for (temp in estados.indices)
        if (estados[temp].numeroEstado == inicial) estados[temp].Inicial = true

    val final = ArrayList<Int>()//Obteniendo los estados finales
    bufferedReader.readLine().split(",").map { final.add(it.trim().toInt()) }
    for (temp in estados.indices) {
        for (temp2 in final.indices)
            if (estados[temp].numeroEstado == final[temp2]) estados[temp].Final = true
    }

    val elementosTransicion = ArrayList<String>()//Obteniendo todos los elementos de las transiciones del archivo de texto
    var size2 = size - 4
    size = size - 4
    while (size != 0) {
        bufferedReader.readLine().split(",").map { elementosTransicion.add(it.toString()) }
        size--
    }

    var transiciones = transiciones(elementosTransicion, size2, alfabeto, estados)
    var automata = Automata(alfabeto, inicial, final , tablaTransisiones = transiciones, estados = estados)
    var cadena ="bc"
            //readLine()!!
    println("\nTransiciones:\n ")
    for (temp in automata.tablaTransisiones.indices) {
        println("${automata.tablaTransisiones[temp].estadoActual} ${automata.tablaTransisiones[temp].simboloTransicion} ${automata.tablaTransisiones[temp].estadoSiguiente}")
    }
    automata.encontrar(cadena)
}

