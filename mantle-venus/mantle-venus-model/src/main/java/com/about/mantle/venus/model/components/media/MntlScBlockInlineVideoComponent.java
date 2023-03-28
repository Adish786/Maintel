package com.about.mantle.venus.model.components.media;

import com.about.mantle.venus.model.MntlComponentId;
import com.about.mantle.venus.model.components.MntlJWVideoPlayerComponent;
import com.about.venus.core.driver.WebDriverExtended;
import com.about.venus.core.driver.WebElementEx;

/*
Please use `MntlInlineVideoComponent` instead.
Getter should return MntlJWVideoPlayerComponent as it is in here:
`public MntlJWVideoPlayerComponent player() { return player.get().inlineVideoComponent(); }`
 */
@Deprecated
@MntlComponentId("mntl-sc-block-inlinevideo__video")
public class MntlScBlockInlineVideoComponent extends MntlJWVideoPlayerComponent {
	
	public MntlScBlockInlineVideoComponent(WebDriverExtended driver, WebElementEx element) {
		super(driver, element);
	}

}
