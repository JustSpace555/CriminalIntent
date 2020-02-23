package com.bignerdranch.criminalintent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import java.util.*

private const val TAG = "CI.MainActivity"

class MainActivity : AppCompatActivity(), CrimeListFragment.Callbacks {

    private lateinit var crimeListFragment: CrimeListFragment
    private lateinit var crimeFragment: CrimeFragment

    private fun putInfoToLog(text: String) {
        Log.d(TAG, text)
        Log.d(TAG, "------------------")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        putInfoToLog("On create")

        putInfoToLog("Before transaction:" + supportFragmentManager.fragments.toString())

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {
            crimeListFragment = CrimeListFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, crimeListFragment)
                .commit()
        }

        putInfoToLog("After transaction:" + supportFragmentManager.fragments.toString())
    }

    override fun onCrimeSelected(crimeId: UUID) {
        crimeFragment = CrimeFragment.newInstance(crimeId)
        putInfoToLog("CrimeSelected: $crimeId")
        putInfoToLog("Before transaction:" + supportFragmentManager.fragments.toString())
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, crimeFragment)
            .addToBackStack(null)
            .commit()
        putInfoToLog("After transaction:" + supportFragmentManager.fragments.toString())
    }
}
