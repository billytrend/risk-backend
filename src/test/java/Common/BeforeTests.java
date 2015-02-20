package Common;

import com.esotericsoftware.minlog.Log;
import org.junit.BeforeClass;
import org.junit.Ignore;

@Ignore
public class BeforeTests {
	@BeforeClass
	public static void setUpBaseClass() {
		Log.NONE();
	}
}
