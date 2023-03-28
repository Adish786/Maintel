package com.about.mantle.model.seo.jsonld;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.about.mantle.model.extended.docv2.ImageEx;

public class HowToSection {
	private String name;
	private List<HowToStep> steps;

	public HowToSection(String name, List<HowToStep> steps) {
		this.name = name;
		this.steps = steps;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<HowToStep> getSteps() {
		return steps;
	}

	public void setSteps(List<HowToStep> steps) {
		this.steps = steps;
	}


	public static class Builder {
		private String name;
		private List<HowToStep.Builder> steps;

		public HowToSection.Builder name(String name) {
			this.name = name;
			return this;
		}

		public boolean hasSteps() {
			return steps != null && !steps.isEmpty();
		}
		
		public HowToSection.Builder newStep(String step) {
			if (this.steps == null) {
				this.steps = new ArrayList<HowToStep.Builder>();
			}
			this.steps.add(HowToStep.Builder.fromText(step));
			return this;
		}
		
		public HowToSection.Builder newStepFromName(String step) {
			if (this.steps == null) {
				this.steps = new ArrayList<HowToStep.Builder>();
			}
			this.steps.add(HowToStep.Builder.fromName(step));
			return this;
		}


		public HowToSection.Builder newEmptyStep() {
			if (this.steps == null) {
				this.steps = new ArrayList<HowToStep.Builder>();
			}
			this.steps.add(new HowToStep.Builder());
			return this;
		}

		public HowToSection.Builder appendStep(String step) {
			if (this.steps == null || this.steps.size() == 0) {
				newStep(step);
			} else {
				this.steps.get(this.steps.size() - 1).appendText(step);
			}
			return this;
		}
		
		public HowToSection.Builder appendStepName(String name) {
			if (this.steps == null || this.steps.size() == 0) {
				newStepFromName(name);
			} else {
				this.steps.get(this.steps.size() - 1).name(name);
			}
			
			return this;
		}

		public static HowToSection.Builder fromName(String name) {
			HowToSection.Builder builder = new Builder();
			return builder.name(name);
		}
		
		public static HowToSection.Builder fromImages(List<ImageEx> images) {
			HowToSection.Builder builder = new Builder();
			builder.newStepfromImages(images);
			return builder;
		}
		
		public static HowToSection.Builder fromEmptyStep() {
			HowToSection.Builder builder = new Builder();
			return builder.newEmptyStep();
		}
		
		
		public HowToSection.Builder newStepfromImages(List<ImageEx> images) {
			if (this.steps == null) {
				this.steps = new ArrayList<HowToStep.Builder>();
			}
			this.steps.add(HowToStep.Builder.fromImages(images));
			return this;
		}

		public static HowToSection.Builder fromStep(String step) {
			HowToSection.Builder builder = new Builder();
			return builder.newStep(step);
		}

		public HowToSection build() {
			// name is optional but steps are required
			if (name == null) {
				name = "Steps to Make It"; // placeholder to be defensive because name is required when there are multiple sections
				// ideally the content will label each group of steps with a proper heading
			}
			if (steps == null || steps.isEmpty()) {
				return null;
			}
			return new HowToSection(name, steps.stream().map(HowToStep.Builder::build).collect(Collectors.toList()));
		}

		public static HowToSection.Builder fromStepName(String step) {
			HowToSection.Builder builder = new Builder();
			return builder.newStepFromName(step);
		}

		public HowToSection.Builder appendImagesToStep(List<ImageEx> images) {
			if (this.steps == null) {
				newStepfromImages(images);
			}else{
				this.steps.get(this.steps.size() - 1).images(images);
			}
			return this;
		}
	}
}