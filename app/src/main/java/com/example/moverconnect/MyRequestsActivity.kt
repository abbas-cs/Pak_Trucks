package com.example.moverconnect

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class MyRequestsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var emptyState: LinearLayout
    private lateinit var chipGroup: ChipGroup
    private lateinit var adapter: RequestAdapter
    private var currentStatus: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requests)

        initializeViews()
        setupRecyclerView()
        setupSwipeRefresh()
        setupChipGroup()
        loadRequests()
    }

    private fun initializeViews() {
        recyclerView = findViewById(R.id.requestsRecyclerView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        emptyState = findViewById(R.id.emptyState)
        chipGroup = findViewById(R.id.filterChipGroup)

        findViewById<View>(R.id.createRequestButton).setOnClickListener {
            // TODO: Navigate to create request
        }
    }

    private fun setupRecyclerView() {
        adapter = RequestAdapter(emptyList()) { request ->
            // Handle request click
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setColorSchemeResources(R.color.primary)
        swipeRefresh.setOnRefreshListener {
            loadRequests()
        }
    }

    private fun setupChipGroup() {
        chipGroup.setOnCheckedChangeListener { group, checkedId ->
            val chip = group.findViewById<Chip>(checkedId)
            currentStatus = chip?.text?.toString()
            filterRequests()
        }
    }

    private fun loadRequests() {
        // TODO: Load requests from backend
        // For now, using mock data
        val mockRequests = listOf(
            MoveRequest(
                id = "1",
                title = "Moving to New Apartment",
                date = "2024-03-15",
                time = "10:00 AM",
                status = "Pending",
                pickupAddress = "123 Old St, City",
                deliveryAddress = "456 New Ave, City",
                driverName = null
            ),
            MoveRequest(
                id = "2",
                title = "Office Relocation",
                date = "2024-03-20",
                time = "9:00 AM",
                status = "Confirmed",
                pickupAddress = "789 Business Blvd, City",
                deliveryAddress = "321 Corporate Dr, City",
                driverName = "John Driver"
            ),
            MoveRequest(
                id = "3",
                title = "Home Move",
                date = "2024-02-28",
                time = "11:00 AM",
                status = "Completed",
                pickupAddress = "111 Home St, City",
                deliveryAddress = "222 House Ave, City",
                driverName = "Sarah Mover"
            )
        )

        adapter.updateRequests(mockRequests)
        updateEmptyState(mockRequests.isEmpty())
        swipeRefresh.isRefreshing = false
    }

    private fun filterRequests() {
        val status = currentStatus
        if (status == null) {
            adapter.updateRequests(adapter.currentList)
        } else {
            adapter.updateRequests(adapter.currentList.filter { it.status == status })
        }
        updateEmptyState(adapter.currentList.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        emptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
}

data class MoveRequest(
    val id: String,
    val title: String,
    val date: String,
    val time: String,
    val status: String,
    val pickupAddress: String,
    val deliveryAddress: String,
    val driverName: String?
)

class RequestAdapter(
    private var requests: List<MoveRequest>,
    private val onRequestClick: (MoveRequest) -> Unit
) : RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    var currentList: List<MoveRequest> = requests
        private set

    fun updateRequests(newRequests: List<MoveRequest>) {
        requests = newRequests
        currentList = newRequests
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_move_request, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount() = requests.size

    inner class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleView: TextView = itemView.findViewById(R.id.requestTitle)
        private val dateView: TextView = itemView.findViewById(R.id.requestDate)
        private val addressView: TextView = itemView.findViewById(R.id.requestAddress)
        private val driverNameView: TextView = itemView.findViewById(R.id.driverName)
        private val viewDetailsButton: MaterialButton = itemView.findViewById(R.id.viewDetailsButton)

        fun bind(request: MoveRequest) {
            titleView.text = request.title
            dateView.text = "${request.date} at ${request.time}"
            addressView.text = "${request.pickupAddress} → ${request.deliveryAddress}"
            driverNameView.text = request.driverName ?: "No driver assigned"
            viewDetailsButton.setOnClickListener { onRequestClick(request) }
        }
    }
} 