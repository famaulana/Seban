package com.android.serban

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.serban.model.MessageModel
import com.bumptech.glide.Glide
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.firebase.ui.database.SnapshotParser
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat.*

class Chat : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    val MESSAGE_CHILD = "messages"

    private val REQUEST_INVITE = 1
    private val REQUEST_IMAGE = 2
    private val LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif"
    val ANONYMOUS = "anonymous"
    private var mUsername: String? = null
    private var mPhotoUrl: String? = null
    private var mgoogleApiClient: GoogleApiClient? = null
    private var btnSend: Button? = null

    private var rcView: RecyclerView? = null
    private var llManager: LinearLayoutManager? = null
    private var progressbar: ProgressBar? = null
    private var et_message: EditText? = null
    private var img_message: ImageView? = null
    private var fAuth: FirebaseAuth? = null

    private var fUserAuth: FirebaseUser? = null
    private var dbRef: DatabaseReference? = null
    private var firebaseAdapter: FirebaseRecyclerAdapter<MessageModel, MessageViewHolder>? = null

    class MessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var messageTextView: TextView
        var messageImageView: ImageView
        var messanggerTextView: TextView
        var messageImageCircle: CircleImageView

        init {
            messageTextView = v.findViewById(R.id.cardChat_textMessage) as TextView
            messageImageView = v.findViewById(R.id.cardChat_imageMessage) as ImageView
            messanggerTextView = v.findViewById(R.id.cardChat_textChat) as TextView
            messageImageCircle = v.findViewById(R.id.cardChat_imageUser) as CircleImageView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        btnSend = findViewById(R.id.chat_btn_send)
        et_message = findViewById(R.id.chat_et_message)

        back_button.setOnClickListener {
            onBackPressed()
        }

        mUsername = ANONYMOUS
        mgoogleApiClient =
            GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API).build()

        progressbar = findViewById(R.id.chat_progresbar)
        rcView = findViewById(R.id.chat_recycleView)
        llManager = LinearLayoutManager(this)
        llManager!!.stackFromEnd = true
        rcView!!.layoutManager = llManager
        dbRef = FirebaseDatabase.getInstance().reference

        val parser = SnapshotParser<MessageModel> { dataSnapshot ->
            val messageModel = dataSnapshot.getValue(MessageModel::class.java)
            if (messageModel != null) {
                messageModel.setId(dataSnapshot.key)
            }
            messageModel!!
        }

        val messageRef = dbRef!!.child(MESSAGE_CHILD)
        val options = FirebaseRecyclerOptions.Builder<MessageModel>().setQuery(messageRef, parser).build()

        firebaseAdapter = object : FirebaseRecyclerAdapter<MessageModel, MessageViewHolder>(options) {
            override fun onCreateViewHolder(p0: ViewGroup, p1: Int): MessageViewHolder {
                val inflater = LayoutInflater.from(p0.context)
                return MessageViewHolder(inflater.inflate(R.layout.chat_card, p0, false))
            }

            override fun onBindViewHolder(holder: MessageViewHolder, position: Int, model: MessageModel) {
                progressbar!!.visibility = ProgressBar.INVISIBLE
                if (model.getText() != null) {
                    holder.messageTextView.text = model.getText()
                    holder.messageTextView.visibility = TextView.VISIBLE
                    holder.messageImageView.visibility = ImageView.GONE
                } else if (model.getImageUrl() != null) {
                    val imageUrl = model.getImageUrl()
                    if (imageUrl!!.startsWith("gs://")) {
                        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)
                        storageReference.downloadUrl.addOnCompleteListener {
                            if (it.isSuccessful) {
                                val downloadUrl = it.result!!.toString()
                                Glide.with(holder.messageImageView.context).load(downloadUrl)
                                    .into(holder.messageImageView)
                            } else {
                                Log.e("TAG_ERROR", "error with : " + "${it.exception}")
                            }
                        }
                    } else {
                        Glide.with(holder.messageImageView.context).load(model.getImageUrl()!!)
                            .into(holder.messageImageView)
                    }
                    holder.messageImageView.visibility = ImageView.VISIBLE
                    holder.messageTextView.visibility = TextView.GONE
                }
                holder.messanggerTextView.text = model.getName()
                if (model.getPhotoUrl() == null) {
                    holder.messageImageCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            this@Chat, android.R.drawable.ic_menu_gallery
                        )
                    )
                } else {
                    Glide.with(this@Chat).load(model.getPhotoUrl()).into(holder.messageImageCircle)
                }
            }

        }
        firebaseAdapter!!.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                val messageModelCount = firebaseAdapter!!.itemCount
                val lastVisiblePosition = llManager!!.findLastCompletelyVisibleItemPosition()
                if ((lastVisiblePosition == -1 || (positionStart >= (messageModelCount - 1) && lastVisiblePosition == (positionStart - 1)))) {
                    rcView!!.scrollToPosition(positionStart)
                }
            }
        })
        rcView!!.adapter = firebaseAdapter
        et_message!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (s.toString().trim { it <= ' ' }.length > 0) {
                    btnSend!!.setTextColor(ContextCompat.getColor(this@Chat, R.color.colorAccent))
                    btnSend!!.isEnabled = true
                } else {
                    btnSend!!.setTextColor(Color.GRAY)
                    btnSend!!.isEnabled = false
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.length > 0) {
                    btnSend!!.setTextColor(ContextCompat.getColor(this@Chat, R.color.colorAccent))
                    btnSend!!.isEnabled = true
                } else {
                    btnSend!!.setTextColor(Color.GRAY)
                    btnSend!!.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        btnSend!!.setOnClickListener {
            val messageModelx = MessageModel(et_message!!.text.toString(), mUsername!!, mPhotoUrl!!, null)
            dbRef!!.child(MESSAGE_CHILD).push().setValue(messageModelx)
            et_message!!.setText("")
            btnSend!!.isEnabled = false
        }
        img_message = findViewById(R.id.chat_iv_addMessage)
        img_message!!.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE)
        }
        fAuth = FirebaseAuth.getInstance()
        fUserAuth = fAuth!!.currentUser
        if (fUserAuth == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        } else {
            mUsername = fUserAuth!!.displayName
            if (fUserAuth!!.photoUrl != null) {
                mPhotoUrl = fUserAuth!!.photoUrl.toString()
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this, "Google Play Service Error", Toast.LENGTH_SHORT).show()
    }
}
