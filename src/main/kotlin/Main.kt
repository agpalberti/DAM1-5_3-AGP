import java.lang.Exception
import java.lang.IllegalArgumentException

class Agenda() {
    private val listaDeContactos = mutableSetOf<Contacto>()

    fun add(contacto: Contacto): Boolean = listaDeContactos.add(contacto)

    fun delete(contacto: Contacto): Boolean = listaDeContactos.remove(contacto)

    fun listado(): List<Contacto> = listaDeContactos.sortedBy { it.nombre }

    fun filtra(texto: String): List<Contacto> = listaDeContactos.filter { texto in it.nombre }

    fun consultaNumero(numero: String): Contacto? =
        listaDeContactos.find { contacto -> contacto.numero.filter { !it.isWhitespace() } == numero.filter { !it.isWhitespace() } }

    fun consultaNombre(nombre: String): Contacto? = listaDeContactos.find { it.nombre.uppercase() == nombre.uppercase() }
}

class Contacto(nombre: String) {
    var nombre: String = nombre
        set(value) {
            if (value.isNotEmpty() && value.first().isLetter()) field = value
            else throw IllegalArgumentException("El formato de nombre es incorrecto.")
        }

    var numero: String = ""
        set(value) {
            if ((value[0] == '+' || value[0].isDigit()) && value.subSequence(1, value.length - 1)
                    .all { it.isDigit() || it == ' ' }
            ) {
                field = value
            } else throw IllegalArgumentException("El formato de número es incorrecto")
        }

    constructor(nombre: String, numero: String) : this(nombre) {
        this.numero = numero
    }

    override fun toString(): String {
        return "$nombre: $numero"
    }

}


fun main() {
    val agenda = Agenda()
    val listaComandos = listOf("adios", "listado", "filtra", "ayuda")
    var salir = false
    var input: String

    println("Introduce un contacto o número. Escriba \"ayuda\" para ver una lista de los comandos disponibles")

    while (!salir) {
        input = readLine() ?: ""
        val command = input.split(" ")

        if (input.isNotEmpty()) {
            if (input[0].isDigit() || input[0] == '+') {
                val consulta = agenda.consultaNumero(input)
                if (consulta != null) {
                    println("Este número pertenece a ${consulta.nombre}.")
                } else {
                    println("Contacto no encontrado. Introduzca el nombre para registrarlo.")
                    try {
                        val nombre = readLine() ?: ""
                        agenda.add(Contacto(nombre, input))
                        println("Contacto agregado de forma correcta.")
                    } catch (_: Exception) {
                        println("Se ha introducido algún dato de forma incorrecta. Operación cancelada.")
                    }
                }
            } else if (command.first() !in listaComandos) {
                val consulta = agenda.consultaNombre(input)
                if (consulta != null) {
                    println("El número de ${command.first()} es ${consulta.numero}.")
                } else {
                    println("Contacto no encontrado. Introduzca el número para registrarlo.")
                    try {
                        val numero = readLine() ?: ""
                        agenda.add(Contacto(input, numero))
                        println("Contacto agregado de forma correcta.")
                    } catch (_: Exception) {
                        println("Se ha introducido algún dato de forma incorrecta. Operación cancelada.")
                    }
                }
            } else when (command.first()) {
                "adios" -> salir = true
                "listado" -> {
                    val listaContactos = agenda.listado()
                    if (listaContactos.isNotEmpty()) {
                        println("Lista de contactos:")
                        listaContactos.forEach { println("- $it") }
                    } else println("No hay contactos registrados.")
                }
                "filtra" -> {
                    if (command.size > 1) {
                        val filtro = command[1]
                        val agendaFiltrada = agenda.filtra(filtro)
                        if (agendaFiltrada.isNotEmpty()) {
                            println("Lista de contactos filtrados por \"$filtro\" ")
                            agendaFiltrada.forEach { println("- $it") }
                        } else println("No se han encontrado contactos según ese filtro.")
                    } else println("No has introducido nada después del comando filtra.")
                }
                "ayuda" -> {
                    println(
                        "\nLista de comandos disponibles:\n- adios : Cierra el programa." +
                                "\n- listado : Muestra el listado completo de contactos ordenados por nombre." +
                                "\n- filtra \"texto_a_buscar\" : Muestra el listado de los contactos que contengan \"texto_a_buscar\""
                    )
                }
                else -> {
                }
            }
        } else println("No has introducido nada.")

    }
}