package com.example.luinstagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.luinstagram.*
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.example.luinstagram.EndlessRecyclerViewScrollListener




open class FeedFragment : Fragment() {

    lateinit var postRecyclerView : RecyclerView
    lateinit var adapter: PostAdapter
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var scrollListener: EndlessRecyclerViewScrollListener

    var allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postRecyclerView = view.findViewById(R.id.postRecyclerView)
        adapter = PostAdapter(requireContext(), allPosts)
        postRecyclerView.adapter = adapter


        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer)
        val linearLayoutManager = LinearLayoutManager(requireContext())
        postRecyclerView.layoutManager = linearLayoutManager

        //Endless scrolling
        scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page)
            }
        }

        postRecyclerView.addOnScrollListener(scrollListener)

        queryPosts()

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener {
            queryPosts()
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
            android.R.color.holo_green_light,
            android.R.color.holo_orange_light,
            android.R.color.holo_red_light);
    }

    //Query for all posts in our server
    open fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        //Find all Post objects
        query.include(Post.KEY_USER)
        //Return posts in descencding order: ie newer posts will appear first
        query.addDescendingOrder("createdAt")

        //Only return the most recent 20 posts
        query.limit = 20

        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e != null){
                    //Something went wrong
                    Log.e(TAG, "Error fetching posts")
                }else{
                    if(posts != null){
                        for(post in posts){
                            Log.i(TAG, "Description" + post.getDescription() + ", username " + post.getUser()?.username)
                        }

                        adapter.clear()
                        adapter.addAll(posts)
                        // Now we call setRefreshing(false) to signal refresh has finished
                        swipeContainer.setRefreshing(false)
                    }
                }
            }

        })
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    fun loadNextDataFromApi(offset: Int){
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyItemRangeInserted()`
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)
        //Find all Post objects
        query.include(Post.KEY_USER)
        //Return posts in descencding order: ie newer posts will appear first
        query.addDescendingOrder("createdAt")
        query.whereLessThan("createdAt", allPosts.last().createdAt)
        //Only return the most recent 20*pages posts
        query.limit = 20

        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if(e != null){
                    //Something went wrong
                    Log.e(TAG, "Error fetching posts")
                }else{
                    if(posts != null){
                        allPosts.addAll(posts)
                        adapter.notifyItemRangeInserted(20*offset, 20)
                    }
                }
            }

        })
    }

    companion object{
        const val TAG = "FeedFragment"
    }

}