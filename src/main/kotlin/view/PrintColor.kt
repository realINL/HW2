package view

class PrintColor {
    fun print(text: String, color: Int) {
        val colorString: String = "\u001B[${color}m"
        println(colorString + "${text}\u001B[0m")
    }
}