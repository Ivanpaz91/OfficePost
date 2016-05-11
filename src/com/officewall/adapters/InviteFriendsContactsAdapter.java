
package com.officewall.adapters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.officewall.activities.R;
import com.officewall.constants.DefaultConstants;
import com.officewall.pojo.core.Contact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class InviteFriendsContactsAdapter extends BaseAdapter {

    private Context mContext;
    private List<Contact> listContact;

    // selection list
    private Map<Integer, Boolean> mapContacts = new HashMap<Integer, Boolean>();

    /**
     * Constructor
     * 
     * @param context
     * @param list
     */
    public InviteFriendsContactsAdapter(Context context, List<Contact> list) {
        mContext = context;
        listContact = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listContact.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listContact.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.row_invite_friends_contacts, null);
            holder = new ViewHolder();
            holder.cbSelect = (CheckBox)convertView
                    .findViewById(R.id.cb_invite_friends_contact_select);
            holder.tvName = (TextView)convertView.findViewById(R.id.tv_invite_friends_contact_name);
            holder.tvEmail = (TextView)convertView
                    .findViewById(R.id.tv_invite_friends_contact_email);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        /**/
        /* set Contact data */
        Contact contact = (Contact)getItem(position);

        /* Text */
        holder.tvName.setText(contact.getName());
        holder.tvEmail.setText(contact.getEmail());

        /* set selection */
        if (mapContacts.containsKey(position)) {
            holder.cbSelect.setChecked(mapContacts.get(position));
        } else {
            mapContacts.put(position, false);
            holder.cbSelect.setChecked(false);
        }

        /**/
        /* handle Actions */
        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (holder.cbSelect.isChecked()) {
                    holder.cbSelect.setChecked(false);
                    mapContacts.put(position, false);
                } else {
                    holder.cbSelect.setChecked(true);
                    mapContacts.put(position, true);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * view holder
     */
    static class ViewHolder {
        CheckBox cbSelect;
        TextView tvName, tvEmail;
    }

    /**
     * get selected contacts
     * 
     * @return emails
     */
    public String getSelectedContacts() {
        // TODO Auto-generated method stub
        String emails = null;
        int count = 0;
        for (boolean selected : mapContacts.values()) {
            if (selected) {
                if (emails == null) {
                    emails = "\"" + listContact.get(count++).getEmail() + "\"";
                } else {
                    emails = emails + DefaultConstants.SEPARATOR_COMMA + "\""
                            + listContact.get(count++).getEmail() + "\"";
                }
            }
        }
        return emails;
    }

    /**
     * set all contacts
     */
    public void selectAll() {
        // TODO Auto-generated method stub
        for (int i = 0; i < listContact.size(); i++) {
            mapContacts.put(i, true);
        }
        super.notifyDataSetChanged();
    }

    /**
     * deselect all contacts
     */
    public void selectNone() {
        // TODO Auto-generated method stub
        for (int i = 0; i < listContact.size(); i++) {
            mapContacts.put(i, false);
        }
        super.notifyDataSetChanged();
    }

    /**
     * notify listview on dataset changed
     * 
     * @param list
     */
    public void notifyDataSetChanged(List<Contact> list) {
        // TODO Auto-generated method stub
        listContact = list;
        super.notifyDataSetChanged();
    }
}
