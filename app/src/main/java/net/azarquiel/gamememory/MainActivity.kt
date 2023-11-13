package net.azarquiel.gamememory

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity(), OnClickListener {
    private var isTapando: Boolean = false
    private var inicioTime: Long = 0
    private lateinit var ivprimera: ImageView
    private lateinit var linearv: LinearLayout
    private lateinit var random: Random
    val vpokemon = Array(809) { i -> i + 1} //en cada posicion meter uno, en el 0 el nº 1, ...
    val vjuego = Array(30){0}
    var isFirst = true
    var aciertos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inicioTime = System.currentTimeMillis() //cogemos la hora de inicio

        linearv = findViewById<LinearLayout>(R.id.linearv)
        random = Random(System.currentTimeMillis()) //ponemos nºs aleatorios

        newGame()
    }

    private fun newGame() {
        isFirst = true //copiamos las de arriba y quitamos var
        aciertos = 0

        //baraja los nºs
        vpokemon.shuffle(random)

        var x = 0

        //con este bucle nos salen 15 parejas de pokemons
        for(v in 0 until 2){
            for (p in 0 until 15){
                vjuego[x] = vpokemon[p]
                x++
            }
        }

        //pokemos en sitios aleatorios
        vjuego.shuffle()

        var c = 0
        for (i in 0 until linearv.childCount){ //el bucle se repite 6 veces, que son los linear que quiene linearv
            var linearh = linearv.getChildAt(i) as LinearLayout
            for (j in 0 until linearh.childCount){ // de esos linears recorremos las 5 imágenes
                var ivpokemon = linearh.getChildAt(j) as ImageView
                ivpokemon.isEnabled = true
                ivpokemon.setOnClickListener(this)
                val foto = "pokemon${vjuego[c]}" //nombre de la foto que tenemos en drawable
                ivpokemon.tag = vjuego[c]
                c++
                val id = resources.getIdentifier(foto, "drawable", packageName) //busca el id de la foto mediante el nombre
                ivpokemon.setBackgroundResource(id) //la pintamos en background
               // ivpokemon.setImageResource(android.R.color.transparent) //quitamos las tapas para hacer una prueba
                ivpokemon.setImageResource(R.drawable.tapa)
            }
        }

    }

    private fun checkgameOver() {
        if (aciertos == 15) { //hay 15 parejas
            val finTime = System.currentTimeMillis() //hora de final
            val segundos = (finTime - inicioTime) / 1000 //sacamos los segundos que hemos tardado

            AlertDialog.Builder(this)
            .setTitle("Fin de la partida")
            .setMessage("Has tardado $segundos segundos")
            .setCancelable(false)
            .setPositiveButton("Nueva partida") { dialog, which ->
                newGame()
            }
            .setNegativeButton("Salir") { dialog, which ->
                finish()
            }
            .show()
        }
    }

    override fun onClick(v: View?) {
        val ivpulsada = v as ImageView
        val pokemonpulsado = ivpulsada.tag as Int

        if(isTapando) return //bloquea para no poder dar la vuelta a más de 2

        ivpulsada.setImageResource(android.R.color.transparent)

        if (isFirst) {
            ivprimera = ivpulsada
        } else {
            if (ivpulsada==ivprimera){
                tostada("Pulse otra carta")
                return
            }
            if (pokemonpulsado == ivprimera.tag as Int){
                aciertos++
                tostada("$aciertos aciertos")
                ivpulsada.isEnabled = false
                ivprimera.isEnabled = false //si acertamos deshabilitamos esas cartas
                checkgameOver()

            } else {
                GlobalScope.launch () {
                    isTapando = true //onClick no funciona cuando levantamos las dos cartas
                    SystemClock.sleep(1000) //dejamos las cartas unos segundos y le damos al vuelta
                    launch (Main){
                        ivprimera.setImageResource(R.drawable.tapa)
                        ivpulsada.setImageResource(R.drawable.tapa)
                        isTapando = false //cuando las volvemos a tapar podemos volver a elegir otras dos cartas
                    }
                }
            }
        }
        isFirst = !isFirst

    }

    fun tostada (msg:String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show() //con puesto show muestra por pantalla el cartel
    }
}