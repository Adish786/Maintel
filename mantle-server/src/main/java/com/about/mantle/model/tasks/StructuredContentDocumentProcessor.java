package com.about.mantle.model.tasks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.about.mantle.model.extended.docv2.BaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentDataEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentNestedDataEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentNestedEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentBaseDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentCalloutEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentEndGroup;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentFeaturedLinkEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentGroupType;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHeadingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentImageEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentJourneyNavEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSpotlightEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentStarRatingEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentStartGroup;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentSubheadingEx;
import com.about.mantle.model.journey.JourneyRoot;
import org.jsoup.Jsoup;

import com.about.globe.core.definition.common.IterationModel;
import com.about.globe.core.exception.GlobeException;
import com.about.globe.core.http.RequestContext;
import com.about.globe.core.task.annotation.RequestContextTaskParameter;
import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;
import com.about.mantle.htmlslicing.HtmlSlice;
import com.about.mantle.htmlslicing.HtmlSlicer;
import com.about.mantle.htmlslicing.HtmlSlicerConfig;
import com.about.mantle.model.extended.docv2.SliceableListEx;
import com.about.mantle.model.extended.docv2.sc.AbstractStructuredContentContentEx;
import com.about.mantle.model.extended.docv2.sc.StructuredContentDocumentEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentAdSlotEx;
import com.about.mantle.model.extended.docv2.sc.blocks.StructuredContentHtmlEx;
import com.google.common.collect.ImmutableSet;

/**
 * Processes a {@link StructuredContentDocumentEx} document before rendering, along with some other StructuredContent
 * related tasks
 */
@Tasks
public class StructuredContentDocumentProcessor {

    public static final int MAX_NUMBER_OF_POSSIBLE_ADS = 15;
    public static final ImmutableSet<String> HTML_SPLIT_TAGS = ImmutableSet.of("p", "ul", "ol");
    private static final String ADS_SPAN_TAG = "<span class=\"mntl-sc-block-adslot mntl-sc-block-adslot-inline\"></span>";
    private static final String CITATION_CLASS = "ql-inline-citation";
    private static final Pattern dataCiteNumberPattern = Pattern.compile("\\d+");

    private final HtmlSlicer htmlSlicer;
    private final Map<String, StructuredContentAdSlotStrategy> adSlotStrategyMap;
    // Remove in GLBE-7092
    private final boolean subheadingPreprocessorEnabled;
    private final JourneyTask journeyTask;

    public StructuredContentDocumentProcessor(
            HtmlSlicer htmlSlicer, boolean subheadingPreprocessorEnabled,
            Map<String, StructuredContentAdSlotStrategy> adSlotStrategyMap,
            Map<String, StructuredContentAdInsertionStrategy> adInsertionStrategyMap,
            JourneyTask journeyTask) {

        this.htmlSlicer = htmlSlicer;
        this.subheadingPreprocessorEnabled = subheadingPreprocessorEnabled;
        this.journeyTask = journeyTask;
        this.adSlotStrategyMap = adSlotStrategyMap;
        // Convert old adInsertion strategies to adSlot strategies
        if (adInsertionStrategyMap != null) {
            adInsertionStrategyMap.keySet().stream().forEach(key -> {
                if (!adSlotStrategyMap.containsKey(key)) {
                    adSlotStrategyMap.put(key, new AdInsertionStrategyWrapper(adInsertionStrategyMap.get(key)));
                }
            });
        }
    }
    
   /** 
    * Splits large chunks of contents into smaller chunks. Appends ad slots where applicable. Note: This task supports
    * for SliceableListEx of content blocks. Use
    * {@link #processStructuredContentBlocksInList(RequestContext, List, Integer, String)} if you content blocks are in a
    * list
    *
    * @param requestContext
    * @param blockList               SliceableListEx of content blocks
    * @param sliceHtmlAtCharCount    target char count between slices of large content blocks
    * @param adInsertionStrategyName name of the Spring bean that points to an instance of
    * @return
    */
   @Task(name = "processStructuredContentBlocksInSliceableList")
   public List<AbstractStructuredContentContentEx<?>> processStructuredContentBlocksInSliceableList(
           @RequestContextTaskParameter RequestContext requestContext,
           @TaskParameter(name = "blockSliceableListEx") SliceableListEx<AbstractStructuredContentContentEx<?>> blockList,
           @TaskParameter(name = "sliceHtmlAtCharCount") Integer sliceHtmlAtCharCount,
           @TaskParameter(name = "adInsertionStrategyName") String adInsertionStrategyName) {

       return processStructuredContentBlocksInList(requestContext, blockList.getList(), sliceHtmlAtCharCount, adInsertionStrategyName);
   }

   /**
    * Splits large chunks of contents into smaller chunks. Appends ad slots where applicable. Note: This task supports
    * for List of content blocks. Use
    * {@link #processStructuredContentBlocksInSliceableList(RequestContext, SliceableListEx, Integer, String)} if you content blocks are in a
    * SliceableListEx
    *
    * @param requestContext
    * @param blockList               SliceableListEx of content blocks
    * @param sliceHtmlAtCharCount    target char count between slices of large content blocks
    * @param adInsertionStrategyName name of the Spring bean that points to an instance of
    * @return
    */
   @Task(name = "processStructuredContentBlocksInList")
   public List<AbstractStructuredContentContentEx<?>> processStructuredContentBlocksInList(
           @RequestContextTaskParameter RequestContext requestContext,
           @TaskParameter(name = "blockList") List<AbstractStructuredContentContentEx<?>> blockList,
           @TaskParameter(name = "sliceHtmlAtCharCount") Integer sliceHtmlAtCharCount,
           @TaskParameter(name = "adInsertionStrategyName") String adInsertionStrategyName) {

	   List<AbstractStructuredContentContentEx<?>> answer = new ArrayList<>();
   	
	   if(blockList != null && blockList.size() > 0){ 
		   answer = processHtmlBlockSlicing(blockList, sliceHtmlAtCharCount);
           // Remove in GLBE-7092
           if (this.subheadingPreprocessorEnabled) {
               // Process nested structured content types
               answer = processHtmlBlocksToSubheadings(answer);
           }
           answer = unpackNestedBlockContents(answer);
		   answer = processNestedBlocks(answer);
		   answer = processAdSlots(answer, adInsertionStrategyName, requestContext);
		   answer = skipLastAdSlot(answer);
	   }

       return answer;
   }

    /**
     * Adds ad slot to content.  Mutates scBlocks param.
     *
     * @return
     */
    private List<AbstractStructuredContentContentEx<?>> processAdSlots(List<AbstractStructuredContentContentEx<?>> scBlocks,
                                                                       String adSlotStrategyName,
                                                                       RequestContext requestContext) {

        StructuredContentAdSlotStrategy adSlotStrategy = null;
        try {
            adSlotStrategy = getAdSlotStrategyByName(adSlotStrategyName);
        } catch (Exception e) {
            throw new GlobeException("Could not get instance of StructuredContentAdSlotStrategy called "
                    + adSlotStrategyName, e);
        }
        
        ListIterator<AbstractStructuredContentContentEx<?>> it = scBlocks.listIterator();
        adSlotStrategy.processAdBlocks(it, requestContext);
        return scBlocks;
    }

    /**
     * Flattens gallery/slide structure for rendering
     * @return
     */
    private List<AbstractStructuredContentContentEx<?>> unpackNestedBlockContents(List<AbstractStructuredContentContentEx<?>> scBlocks) {
        Deque<StructuredContentGroupType> nestingStack = new LinkedList<>();

        // Process nested structured content types
        for (int i = 0; i < scBlocks.size(); i++) {
            AbstractStructuredContentContentEx<?> currentBlock = scBlocks.get(i);
            String blockTheme = currentBlock.getData().getTheme();
            Boolean isGalleryImageComposite = false;

            // Do not flatten the gallery component if it has the theme THREE-IMAGE-COMPOSITE or TWO-IMAGE-COMPOSITE
            if (blockTheme != null) {
                String blockType = currentBlock.getType();

                isGalleryImageComposite = "GALLERY".equals(blockType) && ("THREE-IMAGE-COMPOSITE".equals(blockTheme) || "TWO-IMAGE-COMPOSITE".equals(blockTheme));
            }

            if (currentBlock instanceof AbstractStructuredContentNestedEx && !isGalleryImageComposite) {
                int nextIndex = i + 1;

                List<AbstractStructuredContentContentEx> nestedContents = ((AbstractStructuredContentNestedEx) currentBlock).getNestingContents();

                /* Unpacking list block
                 * Outer loop index i is not fast-forwarded as nested content may contain additional nested content
                 * that needs to be unpacked. (e.g. gallery contains gallery slides which contain blocks)
                 */
                StructuredContentGroupType nestingType = ((AbstractStructuredContentNestedDataEx)currentBlock.getData()).getGroupType();
                nestingStack.push(nestingType);
                for (AbstractStructuredContentContentEx childBlock : nestedContents) {
                    /* Since we are unpacking nested blocks we are losing the parent/child relationship.
                     * For example, in the case of image galleries, the image goes from being a child of the slide
                     * to a sibling of the slide. This means it's not possible to for-target the image inside a gallery.
                     * This shortcoming of our treatment of nested blocks was noted in https://dotdash.atlassian.net/browse/GLBE-6971.
                     * It turns out however that we _do_ need to differentiate the images inside the gallery from other images.
                     * For example, image blocks generally have lightbox enabled by default but we don't want lightbox on gallery images.
                     * To work around this problem we are going to offer the option of treating nested blocks as separate XML components.
                     * We accomplish this by setting the parentType so that it can be referred to when mapping the block to a component.
                     * For example, instead of mapping IMAGE to mntl-sc-block-image we can map it to mntl-sc-block-galleryslide-image.
                     */
                    childBlock.setParentType(currentBlock.getType()); // NOTE: THIS MODIFIES THE BLOCK IN CACHE!

                    scBlocks.add(nextIndex++, childBlock);
                }
                scBlocks.add(nextIndex++, new StructuredContentEndGroup(nestingStack.pop()));
            }
        }

        return scBlocks;
    }
    
    /**
     * Adds nested blocks to the list of content
     *
     * @return
     */
    private List<AbstractStructuredContentContentEx<?>> processNestedBlocks(List<AbstractStructuredContentContentEx<?>> scBlocks) {

        ListIterator<AbstractStructuredContentContentEx<?>> it = scBlocks.listIterator();
        Deque<StructuredContentGroupType> nestingStack = new LinkedList<>();

        // The 'lastStep' flag is attached to the _first_ SC block of the _last_ step.
        boolean withinLastStep = false;
       

        //Deals with first block being a step
        if (scBlocks.get(0).isStep()) {
            openListBlock(it, nestingStack, scBlocks.get(0));
            openListItemBlock(it, nestingStack);
        }

        while (it.hasNext()) {

            AbstractStructuredContentContentEx<?> currentBlock = it.next();
            AbstractStructuredContentContentEx<?> nextBlock =
                    (scBlocks.size() == it.nextIndex()) ? null : scBlocks.get(it.nextIndex());
            
            boolean isNextBlockAStep =  nextBlock != null && nextBlock.isStep();

            withinLastStep = withinLastStep || currentBlock.isLastStep();

            // This is done in a very specific order.  We close 'deeper' tags (like LI's) first, then close their
            // parents (eg OL / UL), then when we're at the top we _open_ the parents, and then open the deeper tags

            // 1) close LI

            if (currentBlock.isLastBlockOfLastStep() ||
                    (StructuredContentGroupType.LI.equals(nestingStack.peek()) && isNextBlockAStep)) {
                it.add(new StructuredContentEndGroup(nestingStack.pop()));
                withinLastStep = false;
            }

            // 2) close OL/UL

            if (currentBlock.isLastBlockOfLastStep()) {
                it.add(new StructuredContentEndGroup(nestingStack.pop()));
            }

            // 3) open OL/UL

            if (isNextBlockAStep && nestingStack.isEmpty()) {   // isEmpty wouldn't work for OL/ULs that are nested,
                // but our data model doesn't support those.
                openListBlock(it, nestingStack, nextBlock);
            }

            // 4) open LI

            if (isNextBlockAStep) {
                openListItemBlock(it, nestingStack);
            }
        }

        return scBlocks;
    }

    private void openListItemBlock(ListIterator<AbstractStructuredContentContentEx<?>> it, Deque<StructuredContentGroupType> nestingStack) {
        nestingStack.push(StructuredContentGroupType.LI);
        it.add(new StructuredContentStartGroup(StructuredContentGroupType.LI));
    }

    private void openListBlock(ListIterator<AbstractStructuredContentContentEx<?>> it, Deque<StructuredContentGroupType> nestingStack, AbstractStructuredContentContentEx<?> nextBlock) {
        String stepType = nextBlock.getData().getStepType();
        StructuredContentGroupType nestingType = (stepType.equals("NUMBERED")) ?
                StructuredContentGroupType.OL : StructuredContentGroupType.UL;
        nestingStack.push(nestingType);
        it.add(new StructuredContentStartGroup(nestingType));
    }

    private List<AbstractStructuredContentContentEx<?>> processHtmlBlockSlicing(List<AbstractStructuredContentContentEx<?>> blockList, Integer sliceHtmlAtCharCount) {
        // TODO see if we can pull this out of streams, maybe switch to ListIterator
        return blockList.stream().flatMap(content -> sliceLargeContent(content, sliceHtmlAtCharCount)).collect(Collectors.<AbstractStructuredContentContentEx<?>>toList());
    }

    /**
     * Removes an ad slot at the end of the blocks(if it exists) and there are more than 2 blocks
     *
     * @param blocks
     * @return
     */
    private List<AbstractStructuredContentContentEx<?>> skipLastAdSlot(List<AbstractStructuredContentContentEx<?>> blocks) {

        if (blocks == null || blocks.size() <= 2) {
            return blocks;
        }

        AbstractStructuredContentContentEx<?> lastBlock = blocks.get(blocks.size() - 1);

        if (lastBlock instanceof StructuredContentAdSlotEx) {
            blocks.remove(blocks.size() - 1);
        }

        return blocks;
    }

    /**
     * Splits large content up into smaller blocks, if it can.
     *
     * The purpose of splitting large content up into smaller blocks is to ensure that
     * the content stream is granular enough to provide regular and predictable hook-points
     * for the ad insertion logic in `processAdSlots`.
     *
     * @param content
     * @param sliceHtmlAtCharCount
     * @return
     */
    private Stream<? extends AbstractStructuredContentContentEx<?>> sliceLargeContent(
            AbstractStructuredContentContentEx<?> content, int sliceHtmlAtCharCount) {

        Stream<? extends AbstractStructuredContentContentEx<?>> answer;

        // Open to better ways to handle this.  See discussion https://dotdash.slack.com/archives/C67LY10P4/p1512497597000349
        if (content instanceof StructuredContentHtmlEx && !content.isStep() && !content.isLastBlockOfLastStep()) { //don't slice if step, eventually step may be a different type of block see GLBE-5893
            /* In the past we would physically split large html blocks into multiple smaller html blocks which changes the
             * structure of the html content and has visual side-effects regardless of whether or not the created hook-points
             * get used for displaying an ad. As you can imagine this was not ideal because most of the hook-points go unused
             * and we're left with an unnatural break in the copy. To remedy this problem we will now keep the single large
             * html block but inject it with span tags that won't have any visual side effects unless used for serving ads.
             * See https://dotdash.atlassian.net/browse/GLBE-6658 for more info.
             */
            answer = Stream.of(injectSpansForAdsIntoHtmlContent((StructuredContentHtmlEx) content, sliceHtmlAtCharCount));
        } else {
            // No split necessary
            answer = Stream.of(content);
        }

        return answer;
    }

    private static boolean isStep(AbstractStructuredContentContentEx<?> content) {
        return content == null ? false : content.isStep();
    }

    private static boolean isLastStep(AbstractStructuredContentContentEx<?> content) {
        return content == null ? false :content.isLastStep();
    }

    /**
     * Takes a single large HTML block and injects span elements to be used for displaying ads.
     *
     * @param content
     * @param sliceHtmlAtCharCount
     * @return
     */
    private StructuredContentHtmlEx injectSpansForAdsIntoHtmlContent(StructuredContentHtmlEx content, int sliceHtmlAtCharCount) {
        HtmlSlicerConfig config = buildUniformHtmlSlicerConfig(sliceHtmlAtCharCount, content.getData().getHtml().length(), true);
        List<HtmlSlice> slices = htmlSlicer.applyFormatting(content.getData().getHtml(), config);
        String updatedContent = slices.stream().map(slice -> slice.getContent()).collect(Collectors.joining(ADS_SPAN_TAG));
        return new StructuredContentHtmlEx(updatedContent, false);
    }

    /**
     * Creates a HtmlSlicerConfig where the space between every slice is the same
     *
     * @param sliceHtmlAtCharCount target char count between slices
     * @param totalContentLength
     * @param nonclosing
     * @return
     */
    private HtmlSlicerConfig buildUniformHtmlSlicerConfig(int sliceHtmlAtCharCount, int totalContentLength, boolean nonclosing) {
        // Seem strange?  Read https://dotdash.slack.com/archives/C5PHQNL4R/p1512596249000705?thread_ts=1512594025.000395&cid=C5PHQNL4R
        int[] chars = new int[(totalContentLength / sliceHtmlAtCharCount) + 1];
        Arrays.fill(chars, sliceHtmlAtCharCount);
        return new HtmlSlicerConfig(chars, HTML_SPLIT_TAGS, null, nonclosing);
    }

    /**
     * Returns the named strategy or throws an exception
     *
     * @param adSlotStrategyName
     * @return
     */
    private StructuredContentAdSlotStrategy getAdSlotStrategyByName(String adSlotStrategyName) {
        if (adSlotStrategyMap.containsKey(adSlotStrategyName)) {
            return adSlotStrategyMap.get(adSlotStrategyName);
        }

        throw new GlobeException("Could not find StructuredContentAdSlotStrategy with name "
                + adSlotStrategyName);
    }

    /**
     * Creates a {@link IterationModel.IterationModelValue}, used for for-targeting into a structured content
     * block.  Generally only used during testing.
     * <p>
     * Structured Content blocks currently expect that all of their properites have an instance of
     * {@link IterationModel.IterationModelValue} named 'block' in their scope.  This task lets you create a mock
     * instance that you can use to manually for-target into a block
     */
    @Task(name = "processStructuredContentDocument")
    public IterationModel.IterationModelValue mockIterationModelValue(
            @TaskParameter(name = "key") Object key,
            @TaskParameter(name = "value") Object value,
            @TaskParameter(name = "index") Integer index) {

        return new IterationModel.IterationModelValue(key, value, index);

    }

    /*
     * Returns a list of documents from Spotlight and Journeynav content block
     */
    @Task(name = "documentsReferencedByContentBlocks")
    public List<BaseDocumentEx> getDocumentsReferencedByContentBlocks (@TaskParameter(name = "document") BaseDocumentEx document) {
        if (!(document instanceof StructuredContentBaseDocumentEx)) {
            return Collections.EMPTY_LIST;
        }

        List<BaseDocumentEx> docsFromScBlocks = new ArrayList<>();
        StructuredContentBaseDocumentEx structuredContentDocument = (StructuredContentBaseDocumentEx) document;

        for (AbstractStructuredContentContentEx content : structuredContentDocument.getContentsList()) {
            switch (content.getType()) {
                case "SPOTLIGHT":
                    StructuredContentSpotlightEx spotlightContent = (StructuredContentSpotlightEx) content;
                    spotlightContent.getData().getDocuments().stream().forEach(refDoc -> docsFromScBlocks.add(refDoc.getDocument()));
                    break;
                case "JOURNEYNAV":
                    StructuredContentJourneyNavEx journeyNavContent = (StructuredContentJourneyNavEx) content;
                    JourneyRoot journeyRoot = journeyTask.getJourneyStructure(journeyNavContent.getData().getDocument().getDocumentId());

                    if (journeyRoot == null)
                        break;

                    journeyRoot.getSections().get(0).getJourneyDocuments().stream().forEach(journeyDoc -> docsFromScBlocks.add(journeyDoc.getDocument()));
                default:
                    break;
            }
        }

        return docsFromScBlocks;
    }

    /**
     * See {@link #mockIterationModelValue(Object, Object, Integer)}
     */
    @Task(name = "mockIterationModelValue")
    public IterationModel.IterationModelValue mockIterationModelValue(
            @TaskParameter(name = "value") Object value,
            @TaskParameter(name = "index") Integer index) {
        return mockIterationModelValue(null, value, index);
    }

    /**
     * See {@link #mockIterationModelValue(Object, Object, Integer)}
     */
    @Task(name = "mockIterationModelValue")
    public IterationModel.IterationModelValue mockIterationModelValue(
            @TaskParameter(name = "value") Object value,
            @TaskParameter(name = "key") Object key) {
        return mockIterationModelValue(key, value, null);
    }

    /**
     * See {@link #mockIterationModelValue(Object, Object, Integer)}
     */
    @Task(name = "mockIterationModelValue")
    public IterationModel.IterationModelValue mockIterationModelValue(
            @TaskParameter(name = "value") Object value) {
        return mockIterationModelValue(null, value, null);
    }

    // Remove in GLBE-7092
    private List<AbstractStructuredContentContentEx<?>> processHtmlBlocksToSubheadings(List<AbstractStructuredContentContentEx<?>> scBlocks) {
        for (int i = 0; i < scBlocks.size(); i++) {
            AbstractStructuredContentContentEx<?> currentBlock = scBlocks.get(i);

            if (currentBlock instanceof StructuredContentHtmlEx) {
                AbstractStructuredContentDataEx currentBlockData = currentBlock.getData();
                String html = ((StructuredContentHtmlEx) currentBlock).getData().getHtml();
                if(html.substring(0, Math.min(html.length(), 3)).equalsIgnoreCase("<h4")){
                    StructuredContentSubheadingEx subheading = new StructuredContentSubheadingEx();
                    StructuredContentSubheadingEx.StructuredContentSubheadingDataEx subheadingData =
                            new StructuredContentSubheadingEx.StructuredContentSubheadingDataEx();
                    subheadingData.setIsLastBlockOfLastStep(currentBlockData.getIsLastBlockOfLastStep());
                    subheadingData.setIsLastStep(currentBlockData.getIsLastStep());
                    subheadingData.setStepType(currentBlockData.getStepType());
                    subheadingData.setText(Jsoup.parse(html).text());
                    subheading.setData(subheadingData);
                    subheading.setType("SUBHEADING");
                    scBlocks.set(i, subheading);
                }
            }
        }

        return scBlocks;
    }

    /**
     * Used to insert ad slots into a list of StructuredContent blocks.
     *
     * NOTE: This interface is not ideal and will be changed when this functionality is converted to a plugin.
     * This approach is being used as it should support any functionality required between now and when the plugin is
     * created.
     */
    public interface StructuredContentAdSlotStrategy {

        /**
         * Processes a collection of blocks and inserts ad slots where appropriate.
         * Note that an ad slot is a *possible* place for an ad; the client code
         * determines if the ad slot should actually get an ad.
         *
         * @param iterator
         * @param requestContext
         * @return
         */
        void processAdBlocks(ListIterator<AbstractStructuredContentContentEx<?>> iterator,
                                           RequestContext requestContext);
    }

    /**
     * Used to decide if an ad slot should be appended after a given StructuredContent block.
     */
    public interface StructuredContentAdInsertionStrategy {

        /**
         * Decides if an ad slot belongs between the two blocks.  Note that an ad slot is a *possible* place for an
         * ad; the client code determines if the ad slot should actually get an ad.
         *
         * @param blockA
         * @param blockB
         * @param nestedStack
         * @param requestContext
         * @return
         */
        boolean shouldAppendAdBlockBetween(AbstractStructuredContentContentEx<?> blockA,
                                           AbstractStructuredContentContentEx<?> blockB,
                                           Deque<StructuredContentGroupType> nestedStack,
                                           RequestContext requestContext);
    }

    /**
     * Allows legacy AdInsertionStrategy to be called as an AdSlotStrategy
     */
    public static class AdInsertionStrategyWrapper implements StructuredContentAdSlotStrategy {
        private StructuredContentAdInsertionStrategy adInsertionStrategy;

        public AdInsertionStrategyWrapper(StructuredContentAdInsertionStrategy adInsertionStrategy) {
            this.adInsertionStrategy = adInsertionStrategy;
        }

        /**
         * Iterates through the StructuredContent blocks and calls the wrapped AdInsertionStrategy
         * to determine whether an ad slot should be inserted.
         *
         * @param iterator
         * @param requestContext
         * @return
         */
        public void processAdBlocks(ListIterator<AbstractStructuredContentContentEx<?>> iterator,
                                           RequestContext requestContext) {
            Deque<StructuredContentGroupType> nestedStack = new LinkedList<>();
            while (iterator.hasNext()) {

                AbstractStructuredContentContentEx<?> currentBlock = iterator.next();
                AbstractStructuredContentContentEx<?> nextBlock = iterator.hasNext() ? iterator.next() : null;
                // nextBlock advances iterator. To
                if (nextBlock != null) {
                    iterator.previous();
                }

                if (currentBlock.getClass().equals(StructuredContentEndGroup.class)) {
                    nestedStack.pop();
                }

                if (adInsertionStrategy.shouldAppendAdBlockBetween(currentBlock, nextBlock, nestedStack, requestContext)) {
                    iterator.add(new StructuredContentAdSlotEx());
                }

                if (currentBlock.getClass().equals(StructuredContentStartGroup.class)) {
                    StructuredContentStartGroup startGroup = (StructuredContentStartGroup) currentBlock;
                    nestedStack.push(startGroup.getData().getGroupType());
                } else if (currentBlock instanceof AbstractStructuredContentNestedEx) {
                    AbstractStructuredContentNestedDataEx data = (AbstractStructuredContentNestedDataEx)currentBlock.getData();
                    nestedStack.push(data.getGroupType());
                }

            }
        }
    }

    /**
     * Will not include any ad slots at all
     */
    public static class NoAdsStructuredContentAdInsertionStrategy implements StructuredContentAdInsertionStrategy {

        @Override
        public boolean shouldAppendAdBlockBetween(AbstractStructuredContentContentEx<?> blockA,
                                                  AbstractStructuredContentContentEx<?> blockB,
                                                  Deque<StructuredContentGroupType> nestedStack,
                                                  RequestContext requestContext) {
            return false;
        }
    }

    public static class DefaultStructuredContentAdInsertionStrategy implements StructuredContentAdInsertionStrategy {

        private Set<Class<? extends AbstractStructuredContentContentEx<?>>> neverDirectlyAfter;

        @Override
        public boolean shouldAppendAdBlockBetween(AbstractStructuredContentContentEx<?> blockA,
                                                        AbstractStructuredContentContentEx<?> blockB,
                                                        Deque<StructuredContentGroupType> nestedStack,
                                                        RequestContext requestContext) {
            initialize();

            boolean answer = true;

            answer = (!(neverDirectlyAfter.contains(blockA.getClass()))) &&
                    (shouldAppendAdBlockBetweenSpecialCases(blockA, blockB, nestedStack, requestContext));

            return answer;
        }

        /**
         * We only want to call the protected methods once because they're likely creating collections each time,
         * and this is performance-sensitive code
         */
        private void initialize() {

            if (neverDirectlyAfter == null) {
                neverDirectlyAfter = neverDirectlyAfter();
            }
        }

        /**
         * List of StructuredContent classes afterwhich an ad slot cannot be added
         * @return
         */
        protected Set<Class<? extends AbstractStructuredContentContentEx<?>>> neverDirectlyAfter() {

            HashSet<Class<? extends AbstractStructuredContentContentEx<?>>> answer = new HashSet<>();

            // keep alphabetized to help avoid merge conflicts
            answer.add(StructuredContentCalloutEx.class);
            answer.add(StructuredContentHeadingEx.class);
            answer.add(StructuredContentSubheadingEx.class);
            answer.add(StructuredContentImageEx.class);
            answer.add(StructuredContentStartGroup.class);  // NOTE since right now these are only LIs and OLs this
                                                            // is ok, but if we start using StartGroup for other
                                                            // things, this will need to be revisited.
            answer.add(StructuredContentEndGroup.class);    // Same here

            return answer;
        }

        /**
         * Handles all cases not handled by {@link #neverDirectlyAfter()}.
         * Called last.
         *
         * @param blockA
         * @param blockB
         * @param nestedStack
         * @param requestContext
         * @return
         */
        protected boolean shouldAppendAdBlockBetweenSpecialCases(
                AbstractStructuredContentContentEx<?> blockA,
                AbstractStructuredContentContentEx<?> blockB,
                Deque<StructuredContentGroupType> nestedStack,
                RequestContext requestContext) {

            boolean answer = true;
            
            answer = answer &&
                    !(isInLiBlockButIsntTheLastElement(blockB, nestedStack)) &&
                    !(isBetweenAwardFeaturedLinkAndStarRating(blockA, blockB)) &&
                    !(isAfterClosingLiBlock(blockA)) &&
                    !(isBetweenTwoFeaturedLink(blockA, blockB));

            return answer;

        }

        private boolean isAfterClosingLiBlock(AbstractStructuredContentContentEx<?> blockA) {
            return (blockA instanceof StructuredContentEndGroup) && (((StructuredContentEndGroup) blockA).getData().getGroupType().equals(StructuredContentGroupType.LI));
        }

        private boolean isInLiBlockButIsntTheLastElement(AbstractStructuredContentContentEx<?> blockB, Deque<StructuredContentGroupType> nestedStack) {
        	
        	if(nestedStack.peek() == null) return false;
        	
            boolean insideLiBlock = nestedStack.peek().equals(StructuredContentGroupType.LI);
            boolean blockAIsLastBlockInLi = (blockB instanceof StructuredContentEndGroup) && (((StructuredContentEndGroup) blockB).getData().getGroupType().equals(StructuredContentGroupType.LI));

            return (insideLiBlock && !blockAIsLastBlockInLi);
        }

        private boolean isBetweenAwardFeaturedLinkAndStarRating(AbstractStructuredContentContentEx<?> blockA, AbstractStructuredContentContentEx<?> blockB) {
            boolean isFirstBlockAnAwardFeaturedLink = (blockA instanceof StructuredContentFeaturedLinkEx) &&
                    ((StructuredContentFeaturedLinkEx)blockA).getData().getTheme().equals("AWARD");
            return (isFirstBlockAnAwardFeaturedLink && blockB instanceof StructuredContentStarRatingEx);
        }
        
        private boolean isBetweenTwoFeaturedLink(AbstractStructuredContentContentEx<?> blockA, AbstractStructuredContentContentEx<?> blockB) {
        	return (blockA instanceof StructuredContentFeaturedLinkEx) && (blockB instanceof StructuredContentFeaturedLinkEx);
        }

        //private booean isAfterAnImageButNotInsideAStep();
    }
}
