package stsc.frontend.zozka.components.simulation.dialogs;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class TestCreateExecutionDescriptionDialogHelper {

	@Test
	public void testCreateExecutionDescriptionDialogHelperParameterNamePattern() {
		final Pattern p = CreateExecutionDescriptionDialogHelper.PARAMETER_NAME_PATTERN;
		Assert.assertTrue(p.matcher("asd_gre_htr34_her_y5hdg_ge57_gerg").matches());
		Assert.assertTrue(p.matcher("JH38fsUJf3_fhwiub__efw2G34575SFEwegwg_wegE_EGE_EWH").matches());
		Assert.assertTrue(p.matcher("_a2sd").matches());
		Assert.assertFalse(p.matcher("asd!").matches());
		Assert.assertFalse(p.matcher("asd.").matches());
		Assert.assertFalse(p.matcher("asd(").matches());
		Assert.assertFalse(p.matcher("asd%FE").matches());
		Assert.assertFalse(p.matcher("3asd").matches());
		Assert.assertFalse(p.matcher("_3asd").matches());
		Assert.assertFalse(p.matcher("").matches());
	}

}
