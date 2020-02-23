package com.bignerdranch.criminalintent

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.DateFormat
import java.util.UUID


private const val TAG = "CI.CrimeListFragment"

class CrimeListFragment: Fragment() {

	private var callbacks: Callbacks? = null

	private var adapter: CrimeAdapter? = CrimeAdapter(emptyList())
	private lateinit var crimeRecyclerView: RecyclerView
	private lateinit var emptyText: TextView
	private lateinit var emptyButton: Button

	private val crimeListViewModel: CrimeListViewModel by lazy {
		ViewModelProvider(this).get(CrimeListViewModel::class.java)
	}

	companion object {
		fun newInstance(): CrimeListFragment {
			return CrimeListFragment()
		}
	}



	/*
		Callbacks
	 */

	interface Callbacks {
		fun onCrimeSelected(crimeId: UUID)
	}

	override fun onAttach(context: Context) {
		super.onAttach(context)
		Log.d(TAG, "On attach")
		callbacks = context as Callbacks?
	}

	override fun onDetach() {
		super.onDetach()
		Log.d(TAG, "On Detach")
		callbacks = null
	}



	/*
	 *	Start fragment
	 */

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		Log.d(TAG, "On Create")
		setHasOptionsMenu(true)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val view = inflater.inflate(R.layout.fragment_crime_list,
			container, false)

		Log.d(TAG, "On create view")

		crimeRecyclerView = view.findViewById(R.id.crime_recycler_view) as RecyclerView
		crimeRecyclerView.layoutManager = LinearLayoutManager(context)
		crimeRecyclerView.adapter = adapter

		emptyButton = view.findViewById(R.id.empty_button) as Button
		emptyText = view.findViewById(R.id.empty_text_view) as TextView

		emptyButton.setOnClickListener {
			createNewCrime()
		}

		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		Log.d(TAG, "On view created")

		crimeListViewModel.crimeListLiveData.observe(
			viewLifecycleOwner,
			Observer { crimes ->
				crimes?.let {
					Log.i(TAG, "Got crimes ${crimes.size}")
					adapter?.submitList(crimes)
					updateUI(crimes)
			}
		})
	}



	/*
	 *	Menu
	 */

	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.fragment_crime_list, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return when (item.itemId) {
			R.id.new_crime -> {
				createNewCrime()
				true
			}
			else -> return super.onOptionsItemSelected(item)
		}
	}



	/*
	 *	Crimes list
	 */

	private inner class CrimeHolder(view: View):	RecyclerView.ViewHolder(view),
													View.OnClickListener,
                                                    View.OnLongClickListener {
		private lateinit var crime: Crime
		private val titleTextView: TextView = itemView.findViewById(R.id.crime_title)
		private val dateTextView: TextView = itemView.findViewById(R.id.crime_date)
		private val solvedImageView: ImageView = itemView.findViewById(R.id.crime_solved)

		init {
			itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
		}

		fun bind(crime: Crime) {
			this.crime = crime
			titleTextView.text = this.crime.title
			dateTextView.text = DateFormat.getDateInstance(DateFormat.FULL).format(this.crime.date)
            solvedImageView.visibility = if (crime.isSolved)
                View.VISIBLE
            else
                View.GONE
		}

		override fun onClick(v: View?) {
			callbacks?.onCrimeSelected(crime.id)
		}

        override fun onLongClick(v: View?): Boolean {
            crimeListViewModel.removeCrime(crime)
            return true
        }
	}

	private inner class CrimeDiffUtil(): DiffUtil.ItemCallback<Crime>() {
		override fun areContentsTheSame(oldCrime: Crime, newCrime: Crime): Boolean =
			oldCrime.isSolved == newCrime.isSolved &&
					oldCrime.date == newCrime.date &&
					oldCrime.title == newCrime.title

		override fun areItemsTheSame(oldCrime: Crime, newCrime: Crime): Boolean =
			oldCrime.id == newCrime.id
	}

	private inner class CrimeAdapter(var crimes: List<Crime>):
		ListAdapter<Crime, CrimeHolder>(CrimeDiffUtil()) {

		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
			val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
			return CrimeHolder(view)
		}

		override fun getItemCount() = crimes.size

		override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
			holder.bind(crimes[position])
		}
	}



	/*
	 *	Additional functions
	 */
	private fun updateUI(crimes: List<Crime>) {
		adapter = CrimeAdapter(crimes)
		crimeRecyclerView.adapter = adapter
		if (crimes.isEmpty()) {
			emptyButton.visibility = View.VISIBLE
			emptyText.visibility = View.VISIBLE
		}
		else {
			emptyButton.visibility = View.GONE
			emptyText.visibility = View.GONE
		}
	}

	private fun createNewCrime() {
		val crime = Crime()
		crimeListViewModel.addCrime(crime)
		callbacks?.onCrimeSelected(crime.id)
	}

}