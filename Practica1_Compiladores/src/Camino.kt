class Camino(val estadoActual: Int, val simbolo : Char ){

    override fun toString()= "q" + estadoActual + "(" + simbolo + ")"

}