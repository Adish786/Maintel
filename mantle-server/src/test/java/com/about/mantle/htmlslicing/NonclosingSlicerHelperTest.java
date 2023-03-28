package com.about.mantle.htmlslicing;

import static com.about.mantle.htmlslicing.HtmlSliceTestUtils.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.about.globe.core.exception.GlobeException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class NonclosingSlicerHelperTest {

	private HtmlSlicerConfig htmlSlicerConfig = new HtmlSlicerConfig(new int[] {300, 600, 900, 1500}, ImmutableSet.of("p", "ul", "ol"), null, true);

	@Test
	public void testEmptyContent() {
		String content = "";
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("empty content", Collections.emptyList(), result);
	}

	@Test
	public void testShortContentXhtmlSelfClosingTags() {
		String content = "<p>The quick brown fox<br/> jumped over the lazy dog</p>";
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("short content xhtml self-closing", ImmutableList.of(new HtmlSlice(content, 44, 9, null)), result);
	}

	@Test
	public void testShortContentHtmlSelfClosingTags() {
		String content = "<p>The quick brown fox<br> jumped over the lazy dog</p>";
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("short content html self-closing", ImmutableList.of(new HtmlSlice(content, 44, 9, null)), result);
	}

	@Test
	public void testShortContentHtmlEntities() {
		String content = "<p>The quick brown fox<br/> jumped over the lazy dog &amp; cat</p>";
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("short content html entities", ImmutableList.of(new HtmlSlice(content, 50, 11, null)), result);
	}

	@Test(expected = GlobeException.class)
	public void testUnescapedAmpersand() {
		String content = "<p>The quick brown fox<br/> jumped over the lazy dog & cat</p>";
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		slicer.slice();
	}

	@Test
	public void testParagraph() {
		String content = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, quo sensus hilaretur. Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b></p>";
		HtmlSlice expectedSlice1 = new HtmlSlice(
				"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, quo sensus hilaretur. ",
				356, 54, null);
		HtmlSlice expectedSlice2 = new HtmlSlice(
				"Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b></p>",
				494, 70, null);
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("paragraph", ImmutableList.of(expectedSlice1, expectedSlice2), result);
	}

	@Test
	public void testLongContent() {
		String content = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, quo sensus hilaretur. Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b> </p>\n" + 
				"\n" + 
				"<p>Sin eam, quam Hieronymus, ne fecisset idem, ut voluptatem illam Aristippi in prima commendatione poneret. <a href='http://loripsum.net/' target='_blank'>Idem iste, inquam, de voluptate quid sentit?</a> Habent enim et bene longam et satis litigiosam disputationem. <b>Si qua in iis corrigere voluit, deteriora fecit.</b> Hic Speusippus, hic Xenocrates, hic eius auditor Polemo, cuius illa ipsa sessio fuit, quam videmus. Quia voluptatem hanc esse sentiunt omnes, quam sensus accipiens movetur et iucunditate quadam perfunditur. Si enim ita est, vide ne facinus facias, cum mori suadeas. <mark>Iam id ipsum absurdum, maximum malum neglegi.</mark> </p>\n" + 
				"\n" + 
				"<ol>\n" + 
				"	<li>Sin kakan malitiam dixisses, ad aliud nos unum certum vitium consuetudo Latina traduceret.</li>\n" + 
				"	<li>Sed id ne cogitari quidem potest quale sit, ut non repugnet ipsum sibi.</li>\n" + 
				"	<li>Et hunc idem dico, inquieta sed ad virtutes et ad vitia nihil interesse.</li>\n" + 
				"</ol>\n" + 
				"\n" + 
				"<p><mark>Age sane, inquam.</mark> Ita enim se Athenis collocavit, ut sit paene unus ex Atticis, ut id etiam cognomen videatur habiturus. Itaque homo in primis ingenuus et gravis, dignus illa familiaritate Scipionis et Laelii, Panaetius, cum ad Q. Quae quidem adhuc peregrinari Romae videbatur nec offerre sese nostris sermonibus, et ista maxime propter limatam quandam et rerum et verborum tenuitatem. Sed quamquam negant nec virtutes nec vitia crescere, tamen utrumque eorum fundi quodam modo et quasi dilatari putant. Haec quo modo conveniant, non sane intellego. </p>\n" + 
				"\n" + 
				"<ul>\n" + 
				"	<li>Quid enim me prohiberet Epicureum esse, si probarem, quae ille diceret?</li>\n" + 
				"	<li>Duo enim genera quae erant, fecit tria.</li>\n" + 
				"</ul>\n" + 
				"\n" + 
				"<p>Ita cum ea volunt retinere, quae superiori sententiae conveniunt, in Aristonem incidunt; Hoc etsi multimodis reprehendi potest, tamen accipio, quod dant. Quare hoc videndum est, possitne nobis hoc ratio philosophorum dare. Huic ego, si negaret quicquam interesse ad beate vivendum quali uteretur victu, concederem, laudarem etiam; Minime id quidem, inquam, alienum, multumque ad ea, quae quaerimus, explicatio tua ista profecerit. In quibus doctissimi illi veteres inesse quiddam caeleste et divinum putaverunt. Haec igitur Epicuri non probo, inquam. Obsecro, inquit, Torquate, haec dicit Epicurus? Commentarios quosdam, inquam, Aristotelios, quos hic sciebam esse, veni ut auferrem, quos legerem, dum essem otiosus; Idne consensisse de Calatino plurimas gentis arbitramur, primarium populi fuisse, quod praestantissimus fuisset in conficiendis voluptatibus? Immo sit sane nihil melius, inquam-nondum enim id quaero-, num propterea idem voluptas est, quod, ut ita dicam, indolentia? </p>\n" + 
				"\n" + 
				"<p>Progredientibus autem aetatibus sensim tardeve potius quasi nosmet ipsos cognoscimus. <mark>Tollenda est atque extrahenda radicitus.</mark> Qua exposita scire cupio quae causa sit, cur Zeno ab hac antiqua constitutione desciverit, quidnam horum ab eo non sit probatum; <b>Cyrenaici quidem non recusant;</b> <b>Nihil enim hoc differt.</b> <a href='http://loripsum.net/' target='_blank'>Si quae forte-possumus.</a> Cum sciret confestim esse moriendum eamque mortem ardentiore studio peteret, quam Epicurus voluptatem petendam putat. Quid censes eos esse facturos, qui omnino virtutem a bonorum fine segregaverunt, Epicurum, Hieronymum, illos etiam, si qui Carneadeum finem tueri volunt? Nam, ut paulo ante docui, augendae voluptatis finis est doloris omnis amotio. <i>Praeclare hoc quidem.</i> Aufidio, praetorio, erudito homine, oculis capto, saepe audiebam, cum se lucis magis quam utilitatis desiderio moveri diceret. At vero illa, quae Peripatetici, quae Stoici dicunt, semper tibi in ore sunt in iudiciis, in senatu. Quis contra in illa aetate pudorem, constantiam, etiamsi sua nihil intersit, non tamen diligat? Satisne ergo pudori consulat, si quis sine teste libidini pareat? </p>\n" + 
				"\n" + 
				"";
		HtmlSlice expectedSlice1 = new HtmlSlice(
				"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, quo sensus hilaretur. ",
				356, 54, null);
		HtmlSlice expectedSlice2 = new HtmlSlice(
				"Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b> </p>\n" + 
				"\n" + 
				"<p>Sin eam, quam Hieronymus, ne fecisset idem, ut voluptatem illam Aristippi in prima commendatione poneret. ",
				602, 85, null);
		HtmlSlice expectedSlice3 = new HtmlSlice(
				"<a href='http://loripsum.net/' target='_blank'>Idem iste, inquam, de voluptate quid sentit?</a> Habent enim et bene longam et satis litigiosam disputationem. <b>Si qua in iis corrigere voluit, deteriora fecit.</b> Hic Speusippus, hic Xenocrates, hic eius auditor Polemo, cuius illa ipsa sessio fuit, quam videmus. Quia voluptatem hanc esse sentiunt omnes, quam sensus accipiens movetur et iucunditate quadam perfunditur. Si enim ita est, vide ne facinus facias, cum mori suadeas. <mark>Iam id ipsum absurdum, maximum malum neglegi.</mark> </p>\n" + 
				"\n" + 
				"<ol>\n" + 
				"	<li>Sin kakan malitiam dixisses, ad aliud nos unum certum vitium consuetudo Latina traduceret.</li>\n" + 
				"	<li>Sed id ne cogitari quidem potest quale sit, ut non repugnet ipsum sibi.</li>\n" + 
				"	<li>Et hunc idem dico, inquieta sed ad virtutes et ad vitia nihil interesse.</li>\n" + 
				"</ol>\n" + 
				"\n" + 
				"<p><mark>Age sane, inquam.</mark> Ita enim se Athenis collocavit, ut sit paene unus ex Atticis, ut id etiam cognomen videatur habiturus. Itaque homo in primis ingenuus et gravis, dignus illa familiaritate Scipionis et Laelii, Panaetius, cum ad Q. ",
				942, 147, null);
		HtmlSlice expectedSlice4 = new HtmlSlice(
				"Quae quidem adhuc peregrinari Romae videbatur nec offerre sese nostris sermonibus, et ista maxime propter limatam quandam et rerum et verborum tenuitatem. Sed quamquam negant nec virtutes nec vitia crescere, tamen utrumque eorum fundi quodam modo et quasi dilatari putant. Haec quo modo conveniant, non sane intellego. </p>\n" + 
				"\n" + 
				"<ul>\n" + 
				"	<li>Quid enim me prohiberet Epicureum esse, si probarem, quae ille diceret?</li>\n" + 
				"	<li>Duo enim genera quae erant, fecit tria.</li>\n" + 
				"</ul>\n" + 
				"\n" + 
				"<p>Ita cum ea volunt retinere, quae superiori sententiae conveniunt, in Aristonem incidunt; Hoc etsi multimodis reprehendi potest, tamen accipio, quod dant. Quare hoc videndum est, possitne nobis hoc ratio philosophorum dare. Huic ego, si negaret quicquam interesse ad beate vivendum quali uteretur victu, concederem, laudarem etiam; Minime id quidem, inquam, alienum, multumque ad ea, quae quaerimus, explicatio tua ista profecerit. In quibus doctissimi illi veteres inesse quiddam caeleste et divinum putaverunt. Haec igitur Epicuri non probo, inquam. Obsecro, inquit, Torquate, haec dicit Epicurus? Commentarios quosdam, inquam, Aristotelios, quos hic sciebam esse, veni ut auferrem, quos legerem, dum essem otiosus; Idne consensisse de Calatino plurimas gentis arbitramur, primarium populi fuisse, quod praestantissimus fuisset in conficiendis voluptatibus? Immo sit sane nihil melius, inquam-nondum enim id quaero-, num propterea idem voluptas est, quod, ut ita dicam, indolentia? </p>\n" + 
				"\n" + 
				"<p>Progredientibus autem aetatibus sensim tardeve potius quasi nosmet ipsos cognoscimus. ",
				1508, 208, null);
		HtmlSlice expectedSlice5 = new HtmlSlice(
				"<mark>Tollenda est atque extrahenda radicitus.</mark> Qua exposita scire cupio quae causa sit, cur Zeno ab hac antiqua constitutione desciverit, quidnam horum ab eo non sit probatum; <b>Cyrenaici quidem non recusant;</b> <b>Nihil enim hoc differt.</b> <a href='http://loripsum.net/' target='_blank'>Si quae forte-possumus.</a> Cum sciret confestim esse moriendum eamque mortem ardentiore studio peteret, quam Epicurus voluptatem petendam putat. Quid censes eos esse facturos, qui omnino virtutem a bonorum fine segregaverunt, Epicurum, Hieronymum, illos etiam, si qui Carneadeum finem tueri volunt? Nam, ut paulo ante docui, augendae voluptatis finis est doloris omnis amotio. <i>Praeclare hoc quidem.</i> Aufidio, praetorio, erudito homine, oculis capto, saepe audiebam, cum se lucis magis quam utilitatis desiderio moveri diceret. At vero illa, quae Peripatetici, quae Stoici dicunt, semper tibi in ore sunt in iudiciis, in senatu. Quis contra in illa aetate pudorem, constantiam, etiamsi sua nihil intersit, non tamen diligat? Satisne ergo pudori consulat, si quis sine teste libidini pareat? </p>\n" + 
				"\n",
				1013, 147, null);
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("long content", ImmutableList.of(expectedSlice1, expectedSlice2, expectedSlice3, expectedSlice4, expectedSlice5), result);
	}

	@Test
	public void testBoundaryLaysOnOpenNonsplittableTag() {
		String content = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, quo sensus hila <strikethrough>retur.</strikethrough>foobar. Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b></p>";
		HtmlSlice expectedSlice1 = new HtmlSlice(
				"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, quo sensus hila <strikethrough>retur.</strikethrough>",
				357, 55, null);
		HtmlSlice expectedSlice2 = new HtmlSlice(
				"foobar. Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b></p>",
				502, 72, null);
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("boundary lays on open nonsplittable tag", ImmutableList.of(expectedSlice1, expectedSlice2), result);
	}

	@Test
	public void testBoundaryLaysOnOpenSplittableTag() {
		String content = "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, <ol><li>quo sensus</li><li>hila retur.</li><li>foobar.</li></ol>Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b></p>";
		HtmlSlice expectedSlice1 = new HtmlSlice(
				"<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit. Non enim, si omnia non sequebatur, idcirco non erat ortus illinc. Illud urgueam, non intellegere eum quid sibi dicendum sit, cum dolorem summum malum esse dixerit. Illud mihi a te nimium festinanter dictum videtur, sapientis omnis esse semper beatos; Omnes enim iucundum motum, <ol><li>quo sensus</li>",
				345, 53, null);
		HtmlSlice expectedSlice2 = new HtmlSlice(
				"<li>hila retur.</li><li>foobar.</li></ol>Quae fere omnia appellantur uno ingenii nomine, easque virtutes qui habent, ingeniosi vocantur. Ego autem tibi, Piso, assentior usu hoc venire, ut acrius aliquanto et attentius de claris viris locorum admonitu cogitemus. Est enim effectrix multarum et magnarum voluptatum. Duo Reges: constructio interrete. Quem enim ardorem studii censetis fuisse in Archimede, qui dum in pulvere quaedam describit attentius, ne patriam quidem captam esse senserit? <b>Nam Pyrrho, Aristo, Erillus iam diu abiecti.</b></p>",
				512, 74, null);
		NonclosingSlicerHelper slicer = new NonclosingSlicerHelper(htmlSlicerConfig, content);
		List<HtmlSlice> result = slicer.slice();
		assertEquals("boundary lays on open splittable tag", ImmutableList.of(expectedSlice1, expectedSlice2), result);
	}

}
