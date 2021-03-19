package com.ag.twisterpm.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ag.twisterpm.HomeActivity
import com.ag.twisterpm.LoginActivity
import com.ag.twisterpm.R
import com.ag.twisterpm.NewTwistActivity
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NavigationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NavigationFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var fab: View? = null
    private var homeBTN: View? = null
    private var searchBTN: View? = null
    private var settingsBTN: View? = null
    private var logoutBTN: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab = view.findViewById<View>(R.id.createBTN)
        homeBTN = view.findViewById(R.id.homeBTN)
        searchBTN = view.findViewById(R.id.searchBTN)
        settingsBTN = view.findViewById(R.id.settingsBTN)
        logoutBTN = view.findViewById(R.id.logoutBTN)

        InitiateListeners();
    }

    fun InitiateListeners() {
        fab!!.setOnClickListener { view ->
            if (context !is NewTwistActivity) {
                val intent = Intent(context, NewTwistActivity::class.java)
                startActivity(intent)
                (context as Activity).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }
        }

        homeBTN!!.setOnClickListener {
            if (context !is HomeActivity) {
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                (context as Activity).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            }

        }

        logoutBTN!!.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            (context as Activity).overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NavigationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                NavigationFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}