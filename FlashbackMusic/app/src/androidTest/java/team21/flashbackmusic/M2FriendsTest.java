package team21.flashbackmusic;

import android.support.test.rule.ActivityTestRule;

import com.google.api.services.people.v1.model.EmailAddress;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ryanle on 3/11/18.
 */

public class M2FriendsTest {

    // Mock Friends List
    private List<Person> friendsList;
    private List<Person> notFriendsList;
    // EDIT NAMES IF ADDED NEW FRIENDS IN GMAIL ACCOUNT
    private String[] friends = {"Chenglin Liu", "James Chen", "Petter Narvhus", "Roy Sha", "Ryan Le",
                                "Brian Chen"};
    private String[] friendEmails = {"chl461@ucsd.edu", "yuc279@ucsd.edu", "petter.narvhus@gmail.com",
                                        "yisha@ucsd.edu", "ryanphle@gmail.com", "brchen@ucsd.edu"};
    private String[] notFriends = {"Jim Brown", "Billy Bob", "Gary Gillespie", "Ronald McDonald",
                                    "Joe Smith"};
    private String[] notFriendsEmails = {"JB@gmail.com", "BB@gmail.com", "GG@gmail.com",
                                            "RM@gmail.com", "JS@gmail.com"};

    @Rule
    public ActivityTestRule<MainActivity> mainActivity = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() {
        // Setting up friends with email addresses
        friendsList = new ArrayList<Person>();
        for (int i = 0; i < friends.length; i++) {
            List<Name> names = new ArrayList<Name>();
            Name name = new Name();
            name.setDisplayName(friends[i]);

            List<EmailAddress> emails = new ArrayList<>();
            EmailAddress email = new EmailAddress();
            email.setValue(friendEmails[i]);
            emails.add(email);

            Person p = new Person();
            p.setNames(names);
            p.setEmailAddresses(emails);

            friendsList.add(p);
        }

        notFriendsList = new ArrayList<Person>();
        for (int i = 0; i < notFriends.length; i++) {
            List<Name> names = new ArrayList<Name>();
            Name name = new Name();
            name.setDisplayName(notFriends[i]);

            List<EmailAddress> emails = new ArrayList<>();
            EmailAddress email = new EmailAddress();
            email.setValue(notFriendsEmails[i]);
            emails.add(email);

            Person p = new Person();
            p.setNames(names);
            p.setEmailAddresses(emails);

            notFriendsList.add(p);
        }
    }

    @Test
    public void retrievingFriendsListTest() {

        for (String s : friendEmails) {
            String user = mainActivity.getActivity().hashFunction(s);
            boolean isFriend = (mainActivity.getActivity().isFriend(user, friendsList));
            assertEquals(true, isFriend );
        }

        for (String s : notFriendsEmails) {
            String user = mainActivity.getActivity().hashFunction(s);
            boolean isFriend = (mainActivity.getActivity().isFriend(user, friendsList));
            assertEquals(false, isFriend );
        }

    }

}
