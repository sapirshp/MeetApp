package com.example.meetapp;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {
    Dialog groupActionsDialog;
    Dialog addMemberDialog;

    private RecyclerView contactRecyclerView;
    private RecyclerView.Adapter contactAdapter;

    private ArrayList<String> membersToAdd = new ArrayList<>();
    private Map<String, String> datesToDisplay = new LinkedHashMap<>();
    private ArrayList<Integer> buttonsIdForListeners = new ArrayList<>();
    private int membersAmount = 5;
    String groupMembers;

    private int DAYS_IN_CALENDAR = 7;
    private int TOP_SELECTIONS_TO_DISPLAY = 3;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private HashMap<Integer, String> intsToDays = new HashMap();
    private HashMap<TimeSlot,Integer> slotSelections = new HashMap<>();

    public GroupActivity(){
        createIntToDayMap();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String groupName = getIntent().getExtras().getString("groupName");
            getSupportActionBar().setTitle(groupName);
        }
        groupMembers = getIntent().getExtras().getString("groupMembers");
        toolbar.setSubtitle(groupMembers);
        membersAmount = groupMembers.split(",").length;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setDatesToDisplay();
        setButtonsIdForListeners();
        setListeners();
        groupActionsDialog = new Dialog(this);
        addMemberDialog = new Dialog(this);
        setToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.AddParticipantBtn:
                handleAddParticipant();
                break;
            case R.id.groupDetailsBtn:
                handleGroupDetails();
                break;
            case R.id.resetTimeChoiceBtn:
                handleResetTimeChoice();
                break;
            case R.id.exitGroupBtn:
                handleExitGroup();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleAddParticipant()
    {
        //Toast.makeText(this, " 'Add Participant' Button Pressed ", Toast.LENGTH_LONG).show();
        addMemberDialog.setContentView(R.layout.add_member_popup);
        contactRecyclerView = addMemberDialog.findViewById(R.id.contactsRecyclerView);
        handleExitPopup();
        showContacts();
        chooseMembers();
        addMemberDialog.show();
    }

    private void handleGroupDetails()
    {
        Toast.makeText(this," 'Group Details' Button Pressed ", Toast.LENGTH_LONG).show();
    }


    private void handleResetTimeChoice()
    {
        Toast.makeText(this, " 'Reset Time Choice' Button Pressed ", Toast.LENGTH_LONG).show();
    }

    private void handleExitGroup()
    {
        Toast.makeText(this, " 'Exit Group' Button Pressed ", Toast.LENGTH_LONG).show();
    }

    private void handleExitPopup()
    {
        TextView exitPopupBtn;
        exitPopupBtn = addMemberDialog.findViewById(R.id.addMemberExitBtn);
        exitPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberDialog.dismiss();
                membersToAdd.clear();
            }
        });
    }

    public void onContactClick(View v){
        RelativeLayout contactLayout = v.findViewById(R.id.contactLayout);
        TextView contactName = v.findViewById(R.id.contactName);
        if (contactLayout.getTag() != "chosen") {
            setButtonEnabled();
            contactLayout.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            contactLayout.setTag("chosen");
            membersToAdd.add((String) contactName.getText());
        }else{
            membersToAdd.remove((String) contactName.getText());
            if (membersToAdd.isEmpty()){
                setButtonDisabled();
            }
            contactLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            contactLayout.setTag("notChosen");
        }
    }

    private void setButtonDisabled() {
        Button okButton = addMemberDialog.findViewById(R.id.chooseMembers);
        okButton.setBackgroundResource(R.drawable.disabled_button_background);
    }

    private void setButtonEnabled() {
        Button okButton = addMemberDialog.findViewById(R.id.chooseMembers);
        okButton.setBackgroundResource(R.drawable.green_round_backround);
    }

    private void chooseMembers(){
        Button okBtn = addMemberDialog.findViewById(R.id.chooseMembers);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String memberName : membersToAdd) {
                    addMemberDialog.dismiss();
                    groupMembers += ", " + memberName;
                    String addingMemberMsg = memberName + " was added to group";
                    Toast.makeText(v.getContext(),addingMemberMsg, Toast.LENGTH_LONG).show();
                }
                membersAmount+=membersToAdd.size();
                Toolbar toolbar = findViewById(R.id.groupToolbar);
                toolbar.setSubtitle(groupMembers);
                membersToAdd.clear();
            }
        });
    }

    private void createIntToDayMap() {
        intsToDays.put(1, "Sun");
        intsToDays.put(2, "Mon");
        intsToDays.put(3, "Tue");
        intsToDays.put(4, "Wed");
        intsToDays.put(5, "Thu");
        intsToDays.put(6, "Fri");
        intsToDays.put(7, "Sat");
    }

    public void createDatesMap(){
        Calendar groupCalender= Calendar.getInstance();
        int date = groupCalender.get(Calendar.DAY_OF_MONTH);
        String day;
        datesToDisplay.put(Integer.toString(date), "Today");
        for (int i = 1; i < DAYS_IN_CALENDAR; i++) {
            groupCalender.roll(Calendar.DATE, 1);
            date = groupCalender.get(Calendar.DAY_OF_MONTH);
            day = intsToDays.get(groupCalender.get(Calendar.DAY_OF_WEEK));
            datesToDisplay.put(Integer.toString(date), day);
        }
    }

    public void buttonSelection(TimeSlot timeSlot) {
        if (!timeSlot.getClicked()) {
            clickedOn(timeSlot);
        } else {
            clickedOff(timeSlot);
        }
        slotSelections = sortByValue(slotSelections);
        displayTopSelections();
    }

    public void clickedOn(TimeSlot timeSlot) {
        slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) + 1 : 1);
        String textWithSelectionNumber = slotSelections.get(timeSlot) +
                                         "/" + membersAmount;
        timeSlot.getButton().setText(textWithSelectionNumber);
        timeSlot.setClicked(true);
        float percentage = ((float)slotSelections.get(timeSlot)/(float)membersAmount)*100;
        int bgColor = getColorPercentage(0xe0ffd2, 0x67a34c,(int)percentage);
        timeSlot.getButton().setBackground(setBackGroundColorAndBorder(bgColor));
    }

    public void clickedOff(TimeSlot timeSlot){
        timeSlot.getButton().setBackgroundResource(R.drawable.custom_border);

        timeSlot.setClicked(false);
        String textWithSelectionNumber = timeSlot.getHour();
        timeSlot.getButton().setText(textWithSelectionNumber);
        if (slotSelections.get(timeSlot)>1){
            slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) -1 : 1);
        }else{
            slotSelections.remove(timeSlot);
        }
    }

    public void setButtonsIdForListeners() {
        for (int i = 0; i < DAYS_IN_CALENDAR; i++) {
            int morningBtnId = getResources().getIdentifier("d" + i + "m", "id", getPackageName());
            buttonsIdForListeners.add(morningBtnId);
            int afternoonBtnId = getResources().getIdentifier("d" + i + "a", "id", getPackageName());
            buttonsIdForListeners.add(afternoonBtnId);
            int eveningBtnId = getResources().getIdentifier("d" + i + "e", "id", getPackageName());
            buttonsIdForListeners.add(eveningBtnId);
        }
    }

    public void setListeners(){
        for (int id : buttonsIdForListeners){
            final Button timeSlotButton = findViewById(id);
            int indexOfDate = Integer.valueOf(timeSlotButton.getTag().toString());
            final TimeSlot timeSlot = new TimeSlot(timeSlotButton, datesToDisplay.get(indexOfDate));
            timeSlotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(timeSlot);
                }
            });
        }
    }



    public static HashMap<TimeSlot, Integer> sortByValue(HashMap<TimeSlot, Integer> hm)
    {
        List<Map.Entry<TimeSlot, Integer> > list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<TimeSlot, Integer> >() {
            public int compare(Map.Entry<TimeSlot, Integer> o1,
                               Map.Entry<TimeSlot, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<TimeSlot, Integer> sortedList = new LinkedHashMap<>();
        for (Map.Entry<TimeSlot, Integer> item : list) {
            sortedList.put(item.getKey(), item.getValue());
        }
        return sortedList;
    }

    public void displayTopSelections(){
        String topSelections = "Top Suggesions:\n";
        int iterationNumber = TOP_SELECTIONS_TO_DISPLAY;
        if (iterationNumber > slotSelections.size()){
            iterationNumber = slotSelections.size();
        }
        for (int i = 0; i < iterationNumber; i++){
            TimeSlot topSlot = (TimeSlot) slotSelections.keySet().toArray()[i];
            topSelections = topSelections + topSlot.getDate() + " " + topSlot.getHour() + " - " +
                    + slotSelections.get(topSlot) + "/" + membersAmount + "\n";
        }
    }

    public void setDatesToDisplay(){
        createDatesMap();
        for (int i = 0; i < DAYS_IN_CALENDAR; i++){
            int dayNumTextViewId = getResources().getIdentifier("d" + i + "n" , "id", getPackageName());
            TextView dayNumTextView = findViewById(dayNumTextViewId);
            dayNumTextView.setText((String)datesToDisplay.keySet().toArray()[i]);
            int dayTextViewId = getResources().getIdentifier("d" + i + "d" , "id", getPackageName());
            TextView dayTextView = findViewById(dayTextViewId);
            int dayString = getResources().getIdentifier(datesToDisplay.get((datesToDisplay.keySet().toArray()[i])), "string", getPackageName());
            dayTextView.setText(getString(dayString));
        }
    }

    public static int getColorPercentage(int colorStart, int colorEnd, int percent){
        return Color.rgb(
                ColorPercentageCalculation(Color.red(colorStart), Color.red(colorEnd), percent),
                ColorPercentageCalculation(Color.green(colorStart), Color.green(colorEnd), percent),
                ColorPercentageCalculation(Color.blue(colorStart), Color.blue(colorEnd), percent)
        );
    }

    private static int ColorPercentageCalculation(int colorStart, int colorEnd, int percent){
        return ((Math.max(colorStart, colorEnd)*(100-percent)) + (Math.min(colorStart, colorEnd)*percent)) / 100;
    }


    public Drawable setBackGroundColorAndBorder(int color) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{
                android.R.attr.state_focused, -android.R.attr.state_pressed,
        }, getBackGroundAndBorder(color));
        states.addState(new int[]{
                android.R.attr.state_focused, android.R.attr.state_pressed,
        }, getBackGroundAndBorder(color));
        states.addState(new int[]{
                -android.R.attr.state_focused, android.R.attr.state_pressed,
        }, getBackGroundAndBorder(color));
        states.addState(new int[]{
                android.R.attr.state_enabled
        }, getBackGroundAndBorder(color));

        return states;
    }

    public Drawable getBackGroundAndBorder(int color) {
        Drawable[] drawablesForBackGroundAndBorder = new Drawable[2];
        drawablesForBackGroundAndBorder[0] = getBorder();
        drawablesForBackGroundAndBorder[1] = getBackGround(color);
        LayerDrawable layerDrawable = new LayerDrawable(drawablesForBackGroundAndBorder);
        layerDrawable.setLayerInset(1, 2, 2, 2, 2);
        return layerDrawable.mutate();
    }

    public Drawable getBorder() {
        RectShape rectShape = new RectShape();
        ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
        shapeDrawable.getPaint().setStyle(Paint.Style.STROKE);
        shapeDrawable.getPaint().setStrokeWidth(10f);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return shapeDrawable.mutate();
    }

    public Drawable getBackGround(int color) {
        RectShape rectShape = new RectShape();
        ShapeDrawable shapeDrawable = new ShapeDrawable(rectShape);
        shapeDrawable.getPaint().setColor(color);
        shapeDrawable.getPaint().setStyle(Paint.Style.FILL);
        shapeDrawable.getPaint().setAntiAlias(true);
        shapeDrawable.getPaint().setFlags(Paint.ANTI_ALIAS_FLAG);
        return shapeDrawable.mutate();
    }

    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            final ArrayList<ContactItem> contacts = getContacts();
            contactRecyclerView.setHasFixedSize(true);
            contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            contactAdapter = new ContactsAdapter(contacts);
            contactRecyclerView.setAdapter(contactAdapter);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Comparator<ContactItem> contactsComparator = new Comparator<ContactItem>() {

        @Override
        public int compare(ContactItem o1, ContactItem o2) {
            int res = o1.getContactName().compareTo(o2.getContactName());
            return res;
        }
    };

    private ArrayList<ContactItem> getContacts() {
        ArrayList<ContactItem> contacts = new ArrayList<>();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()){
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null ,ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{ id }, null);
            while (phoneCursor.moveToNext()){
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                contacts.add(new ContactItem(name, phoneNumber));
                break;
            }
        }
        cursor.close();
        Collections.sort(contacts, contactsComparator);
        return contacts;
    }

}