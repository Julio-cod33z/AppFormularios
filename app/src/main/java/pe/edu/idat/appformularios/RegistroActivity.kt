package pe.edu.idat.appformularios

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import pe.edu.idat.appformularios.comunes.AppMensaje
import pe.edu.idat.appformularios.comunes.TipoMensaje
import pe.edu.idat.appformularios.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity(), View.OnClickListener,
    AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityRegistroBinding
    private var estadocivil = ""
    private var listaPreferencias = ArrayList<String>()
    private var listaPersonas= ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.i("MensajeInfo", "¡App inicializa!")
        binding.btnRegistrar.setOnClickListener(this)
        binding.btnListar.setOnClickListener(this)
        ArrayAdapter.createFromResource(this, R.array.estado_civil, android.R.layout.simple_spinner_item)
            .also { adapter -> adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spEstadoCivil.adapter = adapter }
        binding.spEstadoCivil.onItemSelectedListener = this
        binding.cbDeportes.setOnClickListener(this)
        binding.cbMusica.setOnClickListener(this)
        binding.cbOtros.setOnClickListener(this)
    }

    override fun onClick(vista: View) {
        if(vista is CheckBox) {
            agregarQuitarPreferenciaSeleccionada(vista)
        } else {
            when(vista.id) {
                R.id.btnRegistrar -> registrarPersona()
                R.id.btnListar -> startActivity(Intent(applicationContext, ListadoActivity::class.java)
                    .apply { putExtra("listapersonas", listaPersonas) })
            }
        }
    }

    private fun agregarQuitarPreferenciaSeleccionada(vista: CheckBox) {
        if(vista.isChecked) {
            when(vista.id) {
                R.id.cbDeportes -> listaPreferencias.add(vista.text.toString())
                R.id.cbMusica -> listaPreferencias.add(vista.text.toString())
                R.id.cbOtros -> listaPreferencias.add(vista.text.toString())
            }
        } else {
            when(vista.id) {
                R.id.cbDeportes -> listaPreferencias.remove(vista.text.toString())
                R.id.cbMusica -> listaPreferencias.remove(vista.text.toString())
                R.id.cbOtros -> listaPreferencias.remove(vista.text.toString())
            }
        }
    }

    fun registrarPersona() {
        if(validarFormulario()) {
            val infoPersona = binding.etNombres.text.toString() + " " +
                    binding.etApellidos.text.toString() + " " +
                    obtenerGeneroSeleccionado() + " " +
                    obtenerPreferencias() + " " +
                    estadocivil + " " +
                    binding.swNotificacion.isChecked
            listaPersonas.add(infoPersona)
            AppMensaje.enviarMensaje(binding.root, getString(R.string.mensajeRegistroCorrecto), TipoMensaje.SUCCESFULL)
            setearControles()
        }
    }

    private fun obtenerPreferencias(): String {
        var preferencias = ""
        for(pref in listaPreferencias) {
            preferencias += "$pref -"
        }
        return preferencias
    }

    private fun setearControles() {
        listaPreferencias.clear()
        binding.etNombres.setText("")
        binding.etApellidos.setText("")
        binding.swNotificacion.isChecked = false
        binding.cbDeportes.isChecked = false
        binding.cbMusica.isChecked = false
        binding.cbOtros.isChecked = false
        binding.rgGenero.clearCheck()
        binding.spEstadoCivil.setSelection(0)
        binding.etNombres.isFocusableInTouchMode = true
        binding.etNombres.requestFocus()
    }

    fun obtenerGeneroSeleccionado(): String {
        var genero = ""
        when(binding.rgGenero.checkedRadioButtonId) {
            R.id.rbMasculino -> {
                genero = binding.rbMasculino.text.toString()
            }
            R.id.rbFemenino -> {
                genero = binding.rbFemenino.text.toString()
            }
        }
        return genero
    }

    fun validarFormulario(): Boolean {
        var respuesta = false
        if(!validarNombreApellido()) {
            AppMensaje.enviarMensaje(binding.root, "Ingrese nombre y apellido", TipoMensaje.ERROR)
        } else if (!validarGenero()) {
            AppMensaje.enviarMensaje(binding.root, "Seleccione su género", TipoMensaje.ERROR)
        } else if (!validarPreferencias()) {
            AppMensaje.enviarMensaje(binding.root, "Seleccione al menos una preferencia", TipoMensaje.ERROR)
        } else if (!validarEstadoCivil()) {
            AppMensaje.enviarMensaje(binding.root, "Seleccione su estado civil", TipoMensaje.ERROR)
        } else {
            respuesta = true
        }
        return respuesta
    }

    fun validarEstadoCivil(): Boolean {
        return estadocivil != ""
    }

    fun validarPreferencias(): Boolean {
        var respuesta = false
        if(binding.cbDeportes.isChecked
            || binding.cbMusica.isChecked
            || binding.cbOtros.isChecked) {
            respuesta = true
        }
        return respuesta
    }

    fun validarGenero(): Boolean {
        var respuesta = true
        if(binding.rgGenero.checkedRadioButtonId == -1) {
            respuesta = false
        }
        return respuesta
    }

    fun validarNombreApellido() : Boolean {
        var respuesta = true
        if (binding.etNombres.text.toString().trim().isEmpty()) {
            binding.etNombres.isFocusableInTouchMode = true
            binding.etNombres.requestFocus()
            respuesta = false
        } else if(binding.etApellidos.text.toString().trim().isEmpty()) {
            binding.etApellidos.isFocusableInTouchMode = true
            binding.etNombres.requestFocus()
            respuesta = false
        }
        return respuesta
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, p3: Long) {
        estadocivil = if(position > 0) {
            parent!!.getItemAtPosition(position).toString()
        } else ""
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }
}