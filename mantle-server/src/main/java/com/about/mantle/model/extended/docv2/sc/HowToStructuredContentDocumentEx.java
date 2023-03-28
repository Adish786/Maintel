package com.about.mantle.model.extended.docv2.sc;

import java.util.function.Function;
import java.util.stream.Stream;

import com.about.mantle.model.PriceInfo;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.TimeRangeEx;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class HowToStructuredContentDocumentEx extends StructuredContentBaseDocumentEx {

	private static final long serialVersionUID = 1L;
	private SliceableListEx<AbstractStructuredContentContentEx<?>> intro = SliceableListEx.emptyList();
	private SliceableListEx<AbstractStructuredContentContentEx<?>> instruction = SliceableListEx.emptyList();
	private Long workingTime;
	private Long totalTime;
	private TimeRangeEx workingTimeRange;
	private TimeRangeEx totalTimeRange;
	private Integer skillLevel;
	private PriceInfo estimatedCost;
	private SliceableListEx<MaterialGroup> materialGroups;
	private SliceableListEx<MaterialGroup> toolGroups;
	private String yield;
	
	/**
     * Returns combined stream of {@link #intro} and {@link #instruction}, in that
     * order.
     */
    @JsonIgnore
	@Override
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {
        return Stream.of(ensureSCStream.apply(intro),ensureSCStream.apply(instruction)).flatMap(Function.identity())
                .filter(onNonNullSCData);
    }
	
	
    @JsonIgnore
    public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntroContents() {
        return intro;
    }

    @JsonIgnore
    public SliceableListEx<AbstractStructuredContentContentEx<?>> getInstructionContents() {
        return instruction;
    }
    
    public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntro() {
        return intro;
    }

    public void setIntro(SliceableListEx<AbstractStructuredContentContentEx<?>> intro) {
        this.intro = intro;
    }

    public SliceableListEx<AbstractStructuredContentContentEx<?>> getInstruction() {
        return instruction;
    }

    public void setInstruction(SliceableListEx<AbstractStructuredContentContentEx<?>> instruction) {
        this.instruction = instruction;
    }

	public Long getWorkingTime() {
		return workingTime;
	}

	public void setWorkingTime(Long workingTime) {
		this.workingTime = workingTime;
	}

	public Long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}

	public TimeRangeEx getWorkingTimeRange() {
		return workingTimeRange;
	}

	public void setWorkingTimeRange(TimeRangeEx workingTimeRange) {
		this.workingTimeRange = workingTimeRange;
	}

	public TimeRangeEx getTotalTimeRange() {
		return totalTimeRange;
	}

	public void setTotalTimeRange(TimeRangeEx totalTimeRange) {
		this.totalTimeRange = totalTimeRange;
	}

	public SliceableListEx<MaterialGroup> getMaterialGroups() {
		return materialGroups;
	}

	public void setMaterialGroups(SliceableListEx<MaterialGroup> materials) {
		this.materialGroups = materials;
	}

	public SliceableListEx<MaterialGroup> getToolGroups() {
		return toolGroups;
	}

	public void setToolGroups(SliceableListEx<MaterialGroup> toolGroups) {
		this.toolGroups = toolGroups;
	}
	
	public PriceInfo getEstimatedCost() {
		return estimatedCost;
	}


	public void setEstimatedCost(PriceInfo estimatedCost) {
		this.estimatedCost = estimatedCost;
	}

	public String getYield() {
		return yield;
	}


	public void setYield(String yield) {
		this.yield = yield;
	}


	public Integer getSkillLevel() {
		return skillLevel;
	}


	public void setSkillLevel(Integer skillLevel) {
		this.skillLevel = skillLevel;
	}
}
