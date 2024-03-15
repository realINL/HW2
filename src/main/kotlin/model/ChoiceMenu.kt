package model

class ChoiceMenu(keys: List<Int>, values: List<Pair<String, () -> Unit>>) {
    var menu = mutableMapOf<Int, Pair<String, () -> Unit>>()

    init {
        for ((i, key) in keys.withIndex()) {
            menu[key] = values[i]
        }
    }
}