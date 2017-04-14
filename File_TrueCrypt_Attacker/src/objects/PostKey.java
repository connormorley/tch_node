package objects;

/*	Created by:		Connor Morley
 * 	Title:			Default PostKey Object
 *  Version update:	1.1
 *  Notes:			This is an object class that is designed to work with the transmission control class. The object is essential a key and 
 *  				value object which cooperates with the configuration within the transmission class. As the key and value are broken down
 *  				within the transmission class, this object is reusable for all transmission purposes.
 *  
 *  References:		N/A
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