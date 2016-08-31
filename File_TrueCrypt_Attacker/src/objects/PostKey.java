package objects;


/**
 * Created by:  cmorley 10/10/2015
 * Description: This is an object class that is designed specifically to work with the ServerInteraction class as the key value pair. These objects will be funnelled to the class as a list which will then be
 *              broken down in order to create the desired key value pairs for the designated command within the server. This allows for reuse of the objects and the class for all purposes rather than one
 *              method/class for each interaction purpose.
 */
public class PostKey {
    final String key;
    final String value;

    public PostKey(String skey, String svalue)
    {
        this.key = skey;
        this.value =svalue;
    }

    public String getKey(){
        return key;
    }

    public String getValue()
    {
        return value;
    }
}