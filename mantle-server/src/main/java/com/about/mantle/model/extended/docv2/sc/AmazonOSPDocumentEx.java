package com.about.mantle.model.extended.docv2.sc;

import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentProductRecordEx;
import com.about.mantle.model.services.document.preprocessor.AbstractStructuredContentBlockPreprocessor;
import com.about.mantle.model.services.document.preprocessor.ProductRecordPreprocessor;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class AmazonOSPDocumentEx extends StructuredContentBaseDocumentEx {

	private static final long serialVersionUID = 1L;

	private SliceableListEx<AbstractStructuredContentContentEx<?>> intro = SliceableListEx.emptyList();
	private SliceableListEx<ProductItem> productItems = SliceableListEx.emptyList();
	private SliceableListEx<AbstractStructuredContentContentEx<?>> outro = SliceableListEx.emptyList();
	
	public static class ProductItem {
		private StructuredContentProductRecordEx product;
		private SliceableListEx<AbstractStructuredContentContentEx<?>> contents = SliceableListEx.emptyList();
		
		public ProductItem() {
		}
		
		public ProductItem(StructuredContentProductRecordEx product,
				SliceableListEx<AbstractStructuredContentContentEx<?>> contents) {
			this.product = product;
			this.contents = contents;
		}

		public StructuredContentProductRecordEx getProduct() {
			return product;
		}

		public void setProduct(StructuredContentProductRecordEx product) {
			this.product = product;
		}

		public SliceableListEx<AbstractStructuredContentContentEx<?>> getContents() {
			return contents;
		}

		public void setContents(SliceableListEx<AbstractStructuredContentContentEx<?>> contents) {
			this.contents = contents;
		}
	}

	public SliceableListEx<AbstractStructuredContentContentEx<?>> getIntro() {
		return intro;
	}

	public void setIntro(SliceableListEx<AbstractStructuredContentContentEx<?>> intro) {
		this.intro = intro;
	}
	
	public SliceableListEx<AbstractStructuredContentContentEx<?>> getOutro() {
		return outro;
	}

	public void setOutro(SliceableListEx<AbstractStructuredContentContentEx<?>> outro) {
		this.outro = outro;
	}

	public SliceableListEx<ProductItem> getProductItems() {
		return productItems;
	}

	public void setProductItems(SliceableListEx<ProductItem> productItems) {
		this.productItems = productItems;
	}

	/**
	 * {@linkplain AbstractStructuredContentBlockPreprocessor} uses this method to
	 * pre process all the content blocks. And then stream needs to have all product record
	 * blocks to get pre processed by {@linkplain ProductRecordPreprocessor}
	 */
	@JsonIgnore
	@Override
	public Stream<AbstractStructuredContentContentEx<?>> getContentsStream() {
		Stream<AbstractStructuredContentContentEx<?>> productItemProductRecordStream = Objects.nonNull(productItems)
				? productItems.stream().map(productItem -> productItem.getProduct())
				: Stream.empty();
		
		Stream<AbstractStructuredContentContentEx<?>> productItemContentsStream = Objects.nonNull(productItems)
				? productItems.stream().flatMap(productItem -> productItem.getContents().stream())
				: Stream.empty();
		
		return Stream.of(ensureSCStream.apply(intro), productItemProductRecordStream, productItemContentsStream,
						ensureSCStream.apply(outro))
				.flatMap(Function.identity()) // Effectively concats all streams
				.filter(onNonNullSCData);
	}

}
