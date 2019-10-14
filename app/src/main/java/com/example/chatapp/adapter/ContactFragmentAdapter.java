package com.example.chatapp.adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.contact_manager.CtManager;
import com.contact_manager.MultiContact;
import com.daimajia.swipe.SwipeLayout;

import com.example.chatapp.R;
import com.example.chatapp.model.ContactsSectionIndexerForFragment;
import com.example.chatapp.model.GlobalClass;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;
import com.sylversky.indexablelistview.scroller.Indexer;
import com.sylversky.indexablelistview.section.AlphabetSection;

import java.util.ArrayList;
import java.util.List;

public class ContactFragmentAdapter extends BaseAdapter implements Indexer {

    private Context context;
    private Boolean chat_flag = false;
    private List<MultiContact> filterList;
    private List<MultiContact> mDataArray;
    public ValueFilter valueFilter;
    private GlobalClass gc;
    //    private List<FavouriteContacts> favContactList = new ArrayList<>();
    private SparseBooleanArray selectedItems;
    private ContactFragmentAdapterListener listener;
    View.OnClickListener onClickListener;
    private ContactsSectionIndexerForFragment indexer = null;
    private boolean inSearchMode = false;
    ViewGroup parentView;
    View itemView;
    private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private AlphabetSection alphabetSection;
    public ContactFragmentAdapter(Context _context, List<MultiContact> _items,
                                  View.OnClickListener onClickListener, ContactFragmentAdapterListener listener) {

        this.context = _context;
        this.filterList = _items;
        mDataArray = _items;
        gc = GlobalClass.getInstance();
        selectedItems = new SparseBooleanArray();
        this.listener = listener;
        this.onClickListener = onClickListener;
        this.alphabetSection = new AlphabetSection(this);
        setIndexer(new ContactsSectionIndexerForFragment(_items));
//        setHasStableIds(true);
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getCount() {
        if (this.filterList != null) {
            return this.filterList.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int position) {
        return (long) filterList.get(position).hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        itemView = view;
        MyViewHolder holder;
        if (view==null){
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.example_contact_item, viewGroup, false);
            holder  = new MyViewHolder(itemView);
            itemView.setTag(holder);

        }else {
            holder = (MyViewHolder) itemView.getTag();
        }

        if (itemView == null) {
            parentView = new LinearLayout(context);
        } else {
            parentView = (LinearLayout) itemView;
        }

        try {

            MultiContact item = filterList.get(position);

            try {
                showSectionViewIfFirstItem(parentView, item, position);

            } catch (Exception e) {
                e.getMessage();
            }

            //holder.setIsRecyclable(false);
            String number = "";

            if (item.contactNumber != null) {
                number = item.contactNumber;
                holder.numberView.setText(number);

            }
            String name = item.fullName;

            holder.nickNameView.setText(item.fullName);

            if (item instanceof MultiContact) {
                MultiContact contactItem = (MultiContact) item;

                if (contactItem != null) {

                    holder.userImg.setVisibility(View.VISIBLE);
                    holder.userImg_2.setVisibility(View.GONE);
                    String imagePlaceholder;
                    if (contactItem.fullName.split(" ").length > 1) {
                        imagePlaceholder = contactItem.fullName.split(" ")[0].substring(0, 1) + contactItem.fullName.split(" ")[contactItem.fullName.split(" ").length-1].substring(0, 1);
                    } else {
                        imagePlaceholder = contactItem.fullName.split(" ")[0].substring(0, 1);
                    }



                    TextDrawable tw = gc.name_image(imagePlaceholder);
                    if (contactItem.photoUri == null) {
                        holder.userImg.setImageDrawable(tw);
                    } else {
                        String newPhotoUri;
                        if (item.isChatAppContact){
                            Picasso.with(context)
                                    .load(Uri.parse(contactItem.photoUri))
                                    .placeholder(tw)
                                    .error(tw)
                                    .fit().centerCrop()
                                    .into(holder.userImg);
                        }else {
                            Picasso.with(context)
                                    .load(contactItem.photoUri)
                                    .placeholder(tw)
                                    .error(tw)
                                    .fit().centerCrop()
                                    .into(holder.userImg);
                        }

                    }

                }
            }

            holder.mainLayout.setTag(position);

            holder.swipeLayout.setPressed(selectedItems.get(position, false));
            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);


            holder.editContact.setTag(position);
            holder.favContact.setTag(position);


            boolean isChatAppContact = CtManager.isChatAppContact(context,number);
            if(!isChatAppContact){
                holder.chat_invite.setText(this.context.getResources().getString(R.string.invite));
                holder.chat_invite.setTextColor(this.context.getResources().getColor(R.color.tabtextselected));
                holder.chat_invite.setBackgroundResource(R.drawable.curved_invite);
            }else{
                holder.chat_invite.setText(this.context.getResources().getString(R.string.title_chat));
                holder.chat_invite.setTextColor(this.context.getResources().getColor(R.color.chat_green));
                holder.chat_invite.setBackgroundResource(R.drawable.curved_chat);
            }

            applyClickEvents(holder, position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return itemView;
    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {

        holder.editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditContact(position, filterList);
            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.swipeLayout.setPressed(true);
                listener.onItemRowClicked(position, filterList);
            }
        });

        holder.chat_invite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.chat_invite.getText().equals("Invite")) {
                    listener.onInviteClick(position, filterList);
                } else {
                    listener.onChatClick(position, filterList);
                }
            }

        });


    }

    @Override
    public int getPositionForSection(int i) {
        return alphabetSection.getPositionForSection(i, getCount());
    }

    @Override
    public String getComponentName(int position) {
        return filterList.get(position).fullName;
    }


    public interface ContactFragmentAdapterListener {

        void onEditContact(int position, List<MultiContact> contactList);

        void onItemRowClicked(int position, List<MultiContact> contactList);

        void onInviteClick(int position, List<MultiContact> contactList);

        void onChatClick(int position, List<MultiContact> contactList);
    }

    public class MyViewHolder /*extends RecyclerView.ViewHolder*/ {
        RelativeLayout mainLayout;
        SwipeLayout swipeLayout;
        ImageView editContact, favContact;
        CircularImageView userImg, userImg_2;
        TextView nickNameView, numberView;
        Button chat_invite;
        LinearLayout section;
        TextView sectionTextView;


        public MyViewHolder(View view) {
//            super(view);

            section = (LinearLayout) view.findViewById(R.id.section);
            sectionTextView = (TextView) view.findViewById(R.id.sectionTextView);
            mainLayout = (RelativeLayout) view.findViewById(R.id.rv_name);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            editContact = (ImageView) view.findViewById(R.id.idEditContact);
            favContact = (ImageView) view.findViewById(R.id.idFavContact);
            userImg = (CircularImageView) view.findViewById(R.id.userImg);
            userImg_2 = (CircularImageView) view.findViewById(R.id.userImg_2);
            nickNameView = (TextView) view.findViewById(R.id.nickNameView);
            numberView = (TextView) view.findViewById(R.id.numberView);
            chat_invite = (Button) view.findViewById(R.id.chat_invite);

        }
    }

    public boolean isInSearchMode() {
        return inSearchMode;
    }

    public void setInSearchMode(boolean inSearchMode) {
        this.inSearchMode = inSearchMode;
    }

    public ContactsSectionIndexerForFragment getIndexer() {
        return indexer;
    }

    public void setIndexer(ContactsSectionIndexerForFragment indexer) {
        this.indexer = indexer;
    }

    public TextView getSectionTextView(View rowView) {
        TextView sectionTextView = (TextView) rowView.findViewById(R.id.sectionTextView);
        return sectionTextView;
    }

    public void showSectionViewIfFirstItem(View rowView, MultiContact item, int position) {
        TextView sectionTextView = getSectionTextView(rowView);

        // if in search mode then dun show the section header
        if (inSearchMode) {
            sectionTextView.setVisibility(View.GONE);
        } else {
            // if first item then show the header

            if (indexer.isFirstItemInSection(position)) {

                String sectionTitle = indexer.getSectionTitle(item.fullName);
                sectionTextView.setText(sectionTitle);
                sectionTextView.setVisibility(View.VISIBLE);

            } else
                sectionTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }


    @Override
    public Object[] getSections() {

        return alphabetSection.getArraySections();
    }


    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter(mDataArray, this);
        }
        return valueFilter;
    }

    public class ValueFilter extends Filter {

        ContactFragmentAdapter adapter;
        private List<MultiContact> mContacts;

        public ValueFilter(List<MultiContact> mContacts, ContactFragmentAdapter adapter) {
            this.adapter = adapter;
            this.mContacts = mContacts;
        }

        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = mContacts;
                results.count = mContacts.size();
            } else {
                ArrayList<MultiContact> filteredContacts = new ArrayList<MultiContact>();
                for (MultiContact c : mContacts) {
                    if (c.fullName.toUpperCase().contains(constraint.toString().toUpperCase())) {// || c.numbers.contains(constraint.toString())) {
                        filteredContacts.add(c);
                    }else if (c.contactNumber.toUpperCase().contains(constraint.toString().toUpperCase())) {// || c.numbers.contains(constraint.toString())) {
                        filteredContacts.add(c);
                    }

                }

                // Finally set the filtered values and size/count
                results.values = filteredContacts;
                results.count = filteredContacts.size();
            }

            // Return our FilterResults object
            return results;
        }

        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.filterList = (ArrayList<MultiContact>) results.values;
            try {
                if (results.count > 0) {
                    Intent intent = new Intent();
                    intent.setAction("contact.no_record_found");
                    intent.putExtra("flag", "0");
                    context.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("contact.no_record_found");
                    intent.putExtra("flag", "1");
                    context.sendBroadcast(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }

    }


}
