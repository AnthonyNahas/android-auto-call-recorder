package anthonynahas.com.autocallrecorder.classes;

import java.io.File;

/**
 * Created by A on 28.03.16.
 */
public class CallRecordedFile extends File {

    private static final String TAG = CallRecordedFile.class.getSimpleName();
    //private static final long serialVersionUID = -335123512512512L;

    public static final String _3GP = ".3gp";

    public CallRecordedFile(File dir, String name) {
        super(dir, name);
    }

    @Override
    public String toString() {
        String name = getName().toLowerCase();
        name = name.substring(0,name.indexOf(_3GP));
        //
        return "CallRecordedFile{}";
    }
}
