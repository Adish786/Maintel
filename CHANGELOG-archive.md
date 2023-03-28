# Changelog Archive

(See CHANGELOG.md for more recent changes)

## Release 3.11.x

## 3.11.467
 - Fix NPE when trying to load list item schema for documents without list items. [HPC-518](https://dotdash.atlassian.net/browse/HPC-518)

## 3.11.466 (Skip to 3.11.467)
 - Adding CuratedDomain service and updating nofollow logic. [GLBE-7790] (https://dotdash.atlassian.net/browse/GLBE-7790)

## 3.11.465 (Skip to 3.11.467)
 - Add additional check for pruneUnusedFields in case nutritionAnalysis is an empty object. [FULL-446] (https://dotdash.atlassian.net/browse/FULL-446)

## 3.11.464 (Skip to 3.11.467)
 - Prevent redis deserialization of commerceModel in Retailer to prevent premature staleness  [CMRC-1349](https://dotdash.atlassian.net/browse/CMRC-1349)

## 3.11.463 (Skip to 3.11.467)
 - Add RequestContext to externalConfig task methods for overriding purposes in subclasses [CMRC-1318](https://dotdash.atlassian.net/browse/CMRC-1318)

## 3.11.462 (Skip to 3.11.467)
 - Fixing Product LISTSC schema item handling [GLBE-7828](https://dotdash.atlassian.net/browse/GLBE-7828)

## 3.11.461 (Skip to 3.11.462)
 - Adding LISTSC heading anchors to schema [GLBE-7828](https://dotdash.atlassian.net/browse/GLBE-7828)
 - Vertical related automation refer to [GLBE-7831](https://dotdash.atlassian.net/browse/GLBE-7831)

## 3.11.460
 - Updating Product List Schema Test [CMRC-1342](https://dotdash.atlassian.net/browse/CMRC-1342)

## 3.11.459
 - Update Product schema to include jump link urls [CMRC-1342](https://dotdash.atlassian.net/browse/CMRC-1342)

## 3.11.458
 - Updated mntl-vue-base entrypoint files to use utility function. [GLBE-7577](https://dotdash.atlassian.net/browse/GLBE-7577)

## 3.11.457
 - Changed Amazon Afilliate Tagger script to fire on document ready instead of on-click (race condition) [CMRC-72](https://dotdash.atlassian.net/browse/CMRC-72)

## 3.11.456
 - Updated Mantle Schema Test Code to fix bugs in Mantle-ref [GLBE-7697](https://dotdash.atlassian.net/browse/GLBE-7697)

## 3.11.455
 - Add new retailers to VendorLookupService [CMRC-1337](https://dotdash.atlassian.net/browse/CMRC-1337)

## 3.11.454
 - Add new `annotatedIngredient`, `garnish` fields to structured ingredients; deprecate `displayName` [FULL-399](https://dotdash.atlassian.net/browse/FULL-399)

## 3.11.453
 - Fixed NPE in docSchemaTask. Original ticket: [BEAUT-1975](https://dotdash.atlassian.net/browse/BEAUT-1975)

## 3.11.452  (Skip to 3.11.453)
 - Mantle test for meta tags added [AXIS-366](https://dotdash.atlassian.net/browse/AXIS-366)

## 3.11.451  (Skip to 3.11.453)
 - Fixed bug in Flipboard RSS feed where journeys were throwing class cast exceptions and causing the page to 500 [HLTH-6297](https://dotdash.atlassian.net/browse/HLTH-6297)

## 3.11.450  (Skip to 3.11.453)
 - Add extra validation and logging in the CMP service [AXIS-372](https://dotdash.atlassian.net/browse/AXIS-372)

## 3.11.449
 - Using selene `/docSchema` endpoint for medCond and FAQ schema instead of BOVD [BEAUT-1975](https://dotdash.atlassian.net/browse/BEAUT-1975)

## 3.11.448
 - updated tests for Meta | Dates. [HPC-401](https://dotdash.atlassian.net/browse/HPC-401)

## 3.11.447
 - Add support for toc for list items [CMRC-588](https://dotdash.atlassian.net/browse/CMRC-588)

## 3.11.446
 - CyberInt Copy-Protection Site Beacon Component [AXIS-225](https://dotdash.atlassian.net/browse/AXIS-225)
 - Mantle test for Site Beacon Component added [AXIS-226](https://dotdash.atlassian.net/browse/AXIS-226)

## 3.11.445
 - Update Date Logic on All Templates. [HPC-401](https://dotdash.atlassian.net/browse/HPC-401)
 - Use REST-Assured for Selene
 - Use venus-automation client id for linkman(to support document update)

## 3.11.444
 - Switch prebid library to use Dotdash custom-built bundles [AXIS-87](https://dotdash.atlassian.net/browse/AXIS-87)

## 3.11.443
 - Use updated eslint rules from mantle-grunt to clean up global references [GLBE-7842)](https://dotdash.atlassian.net/browse/GLBE-7842)

## 3.11.442
 - Support duplicated record blocks and retailers from infocat [CMRC-1169](https://dotdash.atlassian.net/browse/CMRC-1169)

## 3.11.441
 - Fixed no follow appearing on TOC links [GLBE-7881](https://dotdash.atlassian.net/browse/GLBE-7881)

## 3.11.440
 - Filter Out Of Stock products from InfoCat [CMRC-1172](https://dotdash.atlassian.net/browse/CMRC-1172)

## 3.11.439
 - Updated Vue to allow for new slots syntax. [GLBE-7497](https://dotdash.atlassian.net/browse/GLBE-7497)

## 3.11.438
 - Updating redis client. [GLBE-7713](https://dotdash.atlassian.net/browse/GLBE-7713)

## 3.11.437
 - Sunset AB Leaderboard Proctor Test and Implemented Prod Version. [GLBE-7458](https://dotdash.atlassian.net/browse/GLBE-7458)

## 3.11.436
 - Unify geo-based rules to use Dotdash geo data [AXIS-278](https://dotdash.atlassian.net/browse/AXIS-278)
 - Only enabled the CMP for expected templates, e.g. GDPR [AXIS-279](https://dotdash.atlassian.net/browse/AXIS-279)
 - Provide Dotdash geo data to OT library so it doesn't call its own service [AXIS-280](https://dotdash.atlassian.net/browse/AXIS-280)
 - Automation model and tests for CMP banner component added [AXIS-73](https://dotdash.atlassian.net/browse/AXIS-73)
 - Disable OT built-in and automatic GA tracking [AXIS-284](https://dotdash.atlassian.net/browse/AXIS-284)
 - Remove hacks for banner after OneTrust 6.3 upgrade [AXIS-177](https://dotdash.atlassian.net/browse/AXIS-177)

## 3.11.435
 - Fix deserialization failures in the prebid configuration cache [AXIS-363](https://dotdash.atlassian.net/browse/AXIS-363)

## 3.11.434
 - Add retailer limit to retailer tasks [CMRC-1154](https://dotdash.atlassian.net/browse/CMRC-1154)

## 3.11.433
 - Fix Caching issue with programmed summaries and breadcrumb schema. [GLBE-7805](https://dotdash.atlassian.net/browse/GLBE-7805)

## 3.11.432
 - Delayed page view event so ab-commerce data can be linked to it. [GLBE-7763](https://dotdash.atlassian.net/browse/GLBE-7763)

## 3.11.431
 - Added recalc trigger to Lock-Billboards to properly keep them sticky to the bottom when refreshing taxonomy page. [GLBE-7798](https://dotdash.atlassian.net/browse/GLBE-7798)

## 3.11.430
 - Hippodrome URL parser update for DNS simplification [GLBE-7806](https://dotdash.atlassian.net/browse/GLBE-7806)

## 3.11.429
 - New definition sc block added with schema support [GLBE-7714](https://dotdash.atlassian.net/browse/GLBE-7714)
 - Vertical related automation refer to [GLBE-7731](https://dotdash.atlassian.net/browse/GLBE-7731)

## 3.11.428
 - Added Vuex [GLBE-7334](https://dotdash.atlassian.net/browse/GLBE-7334)

## 3.11.427
 - Prebid: gate the media grid migration from RTB to prebid on the presence of configs [AXIS-286](https://dotdash.atlassian.net/browse/AXIS-286)

## 3.11.426
 - Fix bug where the structured content.xml wasn't taking into account isLastBlockOfLastStep Flag [GLBE-7673](https://dotdash.atlassian.net/browse/GLBE-7673)

## 3.11.425
 - mantle ref 3.11 regression fixes [GLBE-7812](https://dotdash.atlassian.net/browse/GLBE-7812)

## 3.11.424
 - Prebid: add Media Grid bidder creator [AXIS-12](https://dotdash.atlassian.net/browse/AXIS-12)

## 3.11.423
 - Updated MntlImageGalleryTest [AXIS-206](https://dotdash.atlassian.net/browse/AXIS-206)

## 3.11.422
 - Move right-rail__offset class for round-up layout to the xml [CMRC-1289](https://dotdash.atlassian.net/browse/CMRC-1289)

## 3.11.421
 - Update Java Keystore that stores Lets-Encrypt issued certificates for use in local development

## 3.11.420
 - Add support for A/B testing in the prebid config [AXIS-132](https://dotdash.atlassian.net/browse/AXIS-132)

## 3.11.419
- Vertical QA: meta tags automated tests will need modifications to add meta robot details max-image-preview:large
- Robots meta tag for large images (Google Discover) [GLBE-7700](https://dotdash.atlassian.net/browse/GLBE-7700)

## 3.11.418
 - Fix freemarker error introduced with 3.11.414 on product review schema [PRM-900](https://dotdash.atlassian.net/browse/PRM-900)

## 3.11.417 [SKIP TO 3.11.418]
- Remove PRODUCTRECORD block splitting and add support for new block [CMRC-1140](https://dotdash.atlassian.net/browse/CMRC-1140)

## 3.11.416 [SKIP TO 3.11.418]
 - Fixed error handling for recipeServing in schema-unified-recipe[FULL-359](https://dotdash.atlassian.net/browse/FULL-359)

## 3.11.415 [SKIP TO 3.11.418]
 - Updated codebase to have more inclusive language [GLBE-7712](https://dotdash.atlassian.net/browse/GLBE-7712)

## 3.11.414 [SKIP TO 3.11.418]
 - Exclude reviewRating from product review schema when value is zero [PRM-900](https://dotdash.atlassian.net/browse/PRM-900)

## 3.11.413
 - Prebid: add Teads bidder creator [AXIS-66](https://dotdash.atlassian.net/browse/AXIS-66)

## 3.11.412
 - Added Yield fallback field [FULL-354](https://dotdash.atlassian.net/browse/FULL-354)

## 3.11.411
 - Add new affiliates to hasOffersTagger plugin [PRM-939](https://dotdash.atlassian.net/browse/PRM-939)

## 3.11.410
 - Handling issue where "www-" url is redirected to "www." url. [GLBE-7623](https://dotdash.atlassian.net/browse/GLBE-7623)

## 3.11.409
- Removing pinit macro functionality / prepping for removal of pinit resources. [GLBE-7674](https://dotdash.atlassian.net/browse/GLBE-7674)
- Vertical QA : with this mantle upgrade all verticals with Pinterest buttons on their images will be removed, automation will start failing for these cases

## 3.11.408
 - Split product item list and property item list in schema to fix ftl error. No Ticket.

## 3.11.407
 - only add preconnect if in ixId bucket for prebid test [AXIS-191](https://bitbucket.prod.aws.about.com/plugins/servlet/jira-integration/issues/AXIS-191)

## 3.11.406
 - Fixing issue of multiple urls coming from service discovery for external services [GLBE-7723](https://dotdash.atlassian.net/browse/GLBE-7723)

## 3.11.405
- Updated schema-unified-recipe with new recipeServing min/max [NO TICKET]

## 3.11.404
 - Updating Google news sitemap and feed to pull name, description, and link from config params if present [GLBE-7740](https://dotdash.atlassian.net/browse/GLBE-7740)

## 3.11.403
 - Update auto schema test [NO-TICKET]

## 3.11.402
 - Use title primarily over best title for Amazon RSS [CMRC-1208](https://dotdash.atlassian.net/browse/CMRC-1208)

## 3.11.401
 - Add product schema to round up list items [CMRC-1035](https://dotdash.atlassian.net/browse/CMRC-1035)

## 3.11.400
 - Add component for OneTrust consent banner [AXIS-68](https://dotdash.atlassian.net/browse/AXIS-68)
 - Add display logic for OneTrust consent banner [AXIS-69](https://dotdash.atlassian.net/browse/AXIS-69)
 - Request non-personalized ads from GPT if user consent for targeting is set in OptanonConsent cookie [AXIS-137](https://dotdash.atlassian.net/browse/AXIS-137)
 - Delay mobile page-load ads until consent [AXIS-180](https://dotdash.atlassian.net/browse/AXIS-180)
 - Add user consent signal as page targeting param in ad calls [AXIS-153](https://dotdash.atlassian.net/browse/AXIS-153)
 - Track banner logic scenarios [AXIS-181](https://dotdash.atlassian.net/browse/AXIS-181)

## 3.11.399
- Venus version upgraded to 1.3.31 [AXIS-188](https://dotdash.atlassian.net/browse/AXIS-188)

## 3.11.398
 - Added flipboard feed classes so verticals can have flipboard feeds! [BEAUT-1873](https://dotdash.atlassian.net/browse/BEAUT-1873)

## 3.11.397
 - Updated mantle grunt to support mantle-vue-components [GLBE-7587](https://dotdash.atlassian.net/browse/GLBE-7587)

## 3.11.396
 - Product schema round up list item test [CMRC-1231](https://dotdash.atlassian.net/browse/CMRC-1231)

## 3.11.395
 - Updated RecipeSC with new recipeYield/recipeServing data [FULL-49](https://dotdash.atlassian.net/browse/FULL-49)

## 3.11.394
 - Fix failure with robot txt change [GLBE-7699](https://dotdash.atlassian.net/browse/GLBE-7699)

## 3.11.393
 - Fix traversalStartId query building logic [GREEN-78](https://dotdash.atlassian.net/browse/GREEN-78)

## 3.11.392
 - Added automation for FAQ schema [HLTH-6083](https://dotdash.atlassian.net/browse/HLTH-6083)

## 3.11.391
 - Fix bug where WebPageSchemaType was not checked when reviewer was present [GLBE-7693](https://dotdash.atlassian.net/browse/GLBE-7693)

## 3.11.390
 - Changed Mantle Family SVG Implementation to use mntl-svg [GLBE-7669](https://dotdash.atlassian.net/browse/GLBE-7669)

## 3.11.389
 - Escaping characters for image alt text [GLBE-7606](https://dotdash.atlassian.net/browse/GLBE-7606)
 - Vertical related automation refer to [GLBE-7606](https://dotdash.atlassian.net/browse/GLBE-7633)

## 3.11.388
 - (Changelog fix)

## 3.11.387
 - Defer load unified schema by default [SPEED-48](https://dotdash.atlassian.net/browse/SPEED-48)
 - It breaks the vertical venus automation. The changes are made in mantle. Verticals need to adapt the change located in the notes of this ticket [BEAUT-1590](https://dotdash.atlassian.net/browse/BEAUT-1590)

## 3.11.386
 - Changed FINANCIAL PRODUCT REVIEW to FULL PAGE PRODUCT REVIEW [PRM-893](https://dotdash.atlassian.net/browse/PRM-893)

## 3.11.385
 - Added `readyForThirdPartyTracking` event to window in addition to datalayer push [GLBE-7670](https://dotdash.atlassian.net/browse/GLBE-7670)

## 3.11.384
 - Porting over PRM FAQ schema implementation [HLTH-6083](https://dotdash.atlassian.net/browse/HLTH-6083)

## 3.11.383
 - Update round-up layout to include summary-list carousel [CMRC-1126] (https://dotdash.atlassian.net/browse/CMRC-1126)

## 3.11.382
 - Add metrics support to external service calls. [GLBE-7583] (https://dotdash.atlassian.net/browse/GLBE-7583)

## 3.11.381
 - Update SeleneUtils class for lifestyle error. [NO-TICKET]

## 3.11.380
 - Added fetchDocumentIfValidUrl task which is a DOCUMENT task wrapper that will return null if a url isn't a selene document [HLTH-5952](https://dotdash.atlassian.net/browse/HLTH-5952)

## 3.11.379
 - Cleaned up SVGs to make them AMP compliant. [GLBE-7643](https://dotdash.atlassian.net/browse/GLBE-7643)

## 3.11.378
- Replaced linkClickGAEvent tracking event with transmitInteractiveEvent [PRM-930]((https://dotdash.atlassian.net/browse/PRM-930)

## 3.11.377
 - Update auto taxonomy creation function [FIN-2461](https://dotdash.atlassian.net/browse/FIN-2461)

## 3.11.376
 - Amp Phase 0. mntl-amp-html created for initial usage. [GLBE-7626](https://dotdash.atlassian.net/browse/GLBE-7626)
 - Added Amp attribute [GLBE-7628](https://dotdash.atlassian.net/browse/GLBE-7628)
 - Added Amp boilerplate JS [GLBE-7650](https://dotdash.atlassian.net/browse/GLBE-7650)
 - Added Amp boilerplate code [GLBE-7649](https://dotdash.atlassian.net/browse/GLBE-7649)
 - Consolidated styles to single `<style amp-custom>` for Amp pages [GLBE-7642](https://dotdash.atlassian.net/browse/GLBE-7642)
 - Vertical related automation refer to [GLBE-7630](https://dotdash.atlassian.net/browse/GLBE-7630)

## 3.11.375
 - Add PRM layout for FINANCIALPRODUCTREVIEW documents [PRM-891](https://dotdash.atlassian.net/browse/PRM-891)

## 3.11.374
 - MNTL-GTM and related account id update [GLBE-7637](https://dotdash.atlassian.net/browse/GLBE-7637)

## 3.11.373
 - Only adjust location placement of `native-placeholder` ad unit when an inlineLeaderboard is present on page (in A/B test) [HPC-301](https://dotdash.atlassian.net/browse/HPC-301).

## 3.11.372 [SKIP TO 3.11.373]
- Updated venus version from 1.3.28 to 1.3.30 to support screenshot on assertion failure. [NO-TICKET]

## 3.11.371 [SKIP TO 3.11.373]
- Auto Amazon RSS [CMRC-769](https://dotdash.atlassian.net/browse/CMRC-769)

## 3.11.370 [SKIP TO 3.11.373]
 - Enable use-external-image and link-closest-header toggling on Commerce items. [PRM-862](https://dotdash.atlassian.net/browse/PRM-862)

## 3.11.369 [SKIP TO 3.11.373]
 - Auto add taxonomy creation to custom docs. [FIN-2442](https://dotdash.atlassian.net/browse/FIN-2442)

## 3.11.368 [SKIP TO 3.11.373]
 - Added javax dependency for venus test. [NO-TICKET]

## 3.11.367 [SKIP TO 3.11.373]
 - Prebid core: index exchange, rubicon, and criteo [AXIS-11](https://dotdash.atlassian.net/browse/AXIS-11)
   - business-owned prebid configuration [AXIS-117](https://dotdash.atlassian.net/browse/AXIS-117)

## 3.11.366 [SKIP TO 3.11.373]
 - Added support for 'types' attribute for the Google place model. [TRIP-2460](https://iacpublishing.atlassian.net/browse/TRIP-2460)

## 3.11.365 [SKIP TO 3.11.373]
 - Support Google News sitemap in robots.txt when opting in [LW-2894](https://dotdash.atlassian.net/browse/LW-2894)
 - Breaking change: You must indicate in Venus whether or not your vertical has a Google News sitemap or else automation tests will fail

## 3.11.364 [SKIP TO 3.11.373]
  - Remove references to unused tests lazyAdOffset and lazyAdOffsetMobile [FIN-2353](https://dotdash.atlassian.net/browse/FIN-2353)

## 3.11.363 [SKIP TO 3.11.373]
  - Added retailers array to skimlinkPriceDisplayTest in order to override expected retailers [NO-TICKET]

## 3.11.362 [SKIP TO 3.11.373]
  - Add auto common rss feed test method [FIN-2435](https://dotdash.atlassian.net/browse/FIN-2435)

## 3.11.361 [SKIP TO 3.11.373]
  - Add GeoData related request headers for external component requests [FIN-2426](https://dotdash.atlassian.net/browse/FIN-2426)

## 3.11.360 [SKIP TO 3.11.373]
  - Cache warmup changes updated [AXIS-149](https://dotdash.atlassian.net/browse/AXIS-149)

## 3.11.359 [SKIP TO 3.11.373]
 - Add GREEN to search algorithm enum [NO TICKET]

## 3.11.358 [SKIP TO 3.11.373]
 - Utilize referenceRootId in components using referenceId in classes [CMRC-595](https://dotdash.atlassian.net/browse/CMRC-595)

## 3.11.357 [SKIP TO 3.11.373]
 - Fixing issue serializing TaxeneRelationService data for redis [GLBE-7599](https://dotdash.atlassian.net/browse/GLBE-7599)

## 3.11.356 [SKIP TO 3.11.373]
 - Extending TOC support for LISTSC [GLBE-7581](https://dotdash.atlassian.net/browse/GLBE-7581)

## 3.11.355 [SKIP TO 3.11.373]
 - Add support for webPageSchemaType for Medical Web Page[GLBE-7585](https://dotdash.atlassian.net/browse/GLBE-7585)
 - Vertical related automation refer to [GLBE-7592](https://dotdash.atlassian.net/browse/GLBE-7592)

## 3.11.354 [SKIP TO 3.11.373]
 - Implemented baseline changes for inline leaderboard AB test [GLBE-7501](https://dotdash.atlassian.net/browse/GLBE-7501)

## 3.11.353
 - Update data source for socialPresence and bioUrl for reviewer schema [HLTH-6113](https://dotdash.atlassian.net/browse/HLTH-6113)

## 3.11.352
  - Cache warmup changes added [AXIS-149](https://dotdash.atlassian.net/browse/AXIS-149)

## 3.11.351
 - Port embed styles from beauty and travel to mantle. [GLBE-7472](https://dotdash.atlassian.net/browse/GLBE-7472)

## 3.11.350
 - Add conditional before calling _handleBottom to allow billboard to remain sticky [HLTH-5810](https://dotdash.atlassian.net/browse/HLTH-5810)

## 3.11.349
 - Add support for other aspect ratios such as 9x16 (vertical phone video) for jwplayer for inline video [GLBE-7544](https://dotdash.atlassian.net/browse/GLBE-7544)
 - Vertical related automation refer to [GLBE-7554](https://dotdash.atlassian.net/browse/GLBE-7554)

## 3.11.348
 - Update Venus RTB test for GPT Init changes [NO-TICKET]

## 3.11.347
 - Add PRM layout for PRMLISTSC documents [PRM-756](https://dotdash.atlassian.net/browse/PRM-756)

## 3.11.346
 - Setup GPT Init On Scroll to be turned on for mobile [GLBE-7543](https://dotdash.atlassian.net/browse/GLBE-7543)
 - Vertical Automation refer to [GLBE-7567](https://dotdash.atlassian.net/browse/GLBE-7567)

## 3.11.345
 - Add expanded person schema to authors/reviewers [HLTH-6058](https://dotdash.atlassian.net/browse/HLTH-6058)

## 3.11.344
 - Add `v-mntl-social-share` vue directive component for interfacing between mantle social-share and vue apps. [HPC-12](https://dotdash.atlassian.net/browse/HPC-12)

## 3.11.343
 - Cache Clearance: Don't read from global Kafka topic because Linkman is reading it. [GLBE-7014](https://dotdash.atlassian.net/browse/GLBE-7014)

## 3.11.342
 - Update Hippodrome to 2.3.25 for DNS Changes [GLBE-7579](https://dotdash.atlassian.net/browse/GLBE-7579)

## 3.11.341
 - Adds baseline Vue Props Component in Mantle. [GLBE-7495](https://dotdash.atlassian.net/browse/GLBE-7495)

## 3.11.340
 - Update commerce init to fix commerceScBlock variable overridden by readyAndDeferred [CMRC-1045](https://dotdash.atlassian.net/browse/CMRC-1045)

## 3.11.339
 - Infocat button order sorting [CMRC-1044](https://dotdash.atlassian.net/browse/CMRC-1044)

## 3.11.338
 - Changing the defaultBaseClientConfig method to protected [BEAUT-1596](https://dotdash.atlassian.net/browse/BEAUT-1596)

## 3.11.337
 - Preview requests are always cache-cleared [GLBE-7524](https://dotdash.atlassian.net/browse/GLBE-7524)

## 3.11.336
 - Fixing collapsing native ads in billboard slots styles. No ticket.

## 3.11.335
 - Updating MntlNewsletterSignupComponent [FIN-2321](https://dotdash.atlassian.net/browse/FIN-2321)

## 3.11.334
 - Restore addVersioningInformation method for overriding Verticals [NO TICKET]

## 3.11.333 (SKIP to 3.11.334)
 - mantleVersion is now always added during gtmPageView Task [FIN-2321](https://dotdash.atlassian.net/browse/FIN-2321)

## 3.11.332
 - update privacy request email for privacy request test [GLBE-7568](https://dotdash.atlassian.net/browse/GLBE-7568)

## 3.11.331
 - Checking for and removing null script id for external js resources [GLBE-7354](https://dotdash.atlassian.net/browse/GLBE-7354)

## 3.11.330
 - Pattern Library component test updated, mantle model updated to enhance coverage [AXIS-118](https://dotdash.atlassian.net/browse/AXIS-118)

## 3.11.329
 - Setting queryParam on has-offers-tagger to aff_sub3 [GLBE-7530](https://dotdash.atlassian.net/browse/GLBE-7530)
 - Vertical related automation refer to [GLBE-7555](https://dotdash.atlassian.net/browse/GLBE-7555)

## 3.11.328
 - Pushing new field `authorRole` on bioDocument [HLTH-5998](https://dotdash.atlassian.net/browse/HLTH-5998)

## 3.11.327
 - Update for Linkman reading from global resupply [GLBE-7013](https://dotdash.atlassian.net/browse/GLBE-7013)

## 3.11.326
 - Update GLBE to get latest hippodrome for minor tree hugger bug [GLBE-7535](https://dotdash.atlassian.net/browse/GLBE-7535)

## 3.11.325
 - Deserialization bug fixes for infocat models [GLBE-7533](https://dotdash.atlassian.net/browse/GLBE-7533)
 - Don't show product rating if 0 [GLBE-7538](https://dotdash.atlassian.net/browse/GLBE-7538)
 - Removed html escaping of image owner for Product records [GLBE-7537](https://dotdash.atlassian.net/browse/GLBE-7537)

## 3.11.324
 - Mediagrid timeout bumped to use RTB override [GLBE-7539](https://dotdash.atlassian.net/browse/GLBE-7539)

## 3.11.323
 - Fix SpEL error being logged in commerce block for caption and owner properties [GLBE-7532](https://dotdash.atlassian.net/browse/GLBE-7532)

## 3.11.322 [SKIP TO 3.11.323]
 - Fixed conditional ftl modifier for figcaption on tables and removed whitespace [FIN-2245](https://dotdash.atlassian.net/browse/FIN-2245)

## 3.11.321 [SKIP TO 3.11.323]
 - Add venus method for new analytics event [REF-2507] (https://dotdash.atlassian.net/browse/REF-2507)

## 3.11.320 [SKIP TO 3.11.323]
 - Infocat - switch to use technicalName vs shortName for commerce pages [NO TICKET]

## 3.11.319 [SKIP TO 3.11.323]
 - add venus component for review byline [LW-2851](https://dotdash.atlassian.net/browse/LW-2851)

## 3.11.318 [SKIP TO 3.11.323]
 - Added image gallery component [AXIS-14](https://dotdash.atlassian.net/browse/AXIS-14)
 - Added image gallery component and test [AXIS-106](https://dotdash.atlassian.net/browse/AXIS-106)
 - Refactored structured content end groups to allow special groups for gallery [AXIS-67](https://dotdash.atlassian.net/browse/AXIS-67)
 - Treat nested blocks as distinct XML components [AXIS-84](https://dotdash.atlassian.net/browse/AXIS-84)

## 3.11.317
 - Fixing issue where videos with sponsor tags do not display [GLBE-7516](https://dotdash.atlassian.net/browse/GLBE-7516)

## 3.11.316
 - Infocat Phase I.  See Epic ticket [GLBE-7465](https://dotdash.atlassian.net/browse/GLBE-7465)

## 3.11.315
 - Add venus component for Updated stamp [LW-2847] (https://dotdash.atlassian.net/browse/LW-2847)

## 3.11.314
 - Add task to flatten curated list of list [NO-TICKET]

## 3.11.313
 - Incorporate preconnect part of reducePreconnect sitespeed test into mantle [BEAUT-1523](https://dotdash.atlassian.net/browse/BEAUT-1523)

## 3.11.312
 - Added billboard and dynamic-inline to AddCallExpected params [NO-TICKET]

## 3.11.311
 - Fixed sponsor tracking code error logs [NO-TICKET]

## 3.11.310
 - Add collapseSilent error handler to `mntl-primary-image` for documents that have no primary images. [HPC-136](https://dotdash.atlassian.net/browse/HPC-136)

## 3.11.309
 - Fix scrolling issue with rollaway automated test [GLBE-7510](https://dotdash.atlassian.net/browse/GLBE-7510)

## 3.11.308
 - Updating java keystore file [GLBE-7477](https://dotdash.atlassian.net/browse/GLBE-7477)

## 3.11.307
 - ios 9 and below ignores options argument for addEventListener so GPT inits multiple times [BEAUT-1640](https://dotdash.atlassian.net/browse/BEAUT-1640)

## 3.11.306
 - Always open commerce disclosure links in a new tab [BEAUT-1540](https://dotdash.atlassian.net/browse/BEAUT-1540)

## 3.11.305
 - Inlining css on mobile as separate resources to avoid FTL caching issues [GLBE-6328](https://dotdash.atlassian.net/browse/GLBE-6328)

## 3.11.304
 - Add linkLabel to click tracking as a fallback [BEAUT-1512](https://dotdash.atlassian.net/browse/BEAUT-1512)

## 3.11.303
 - Added description field to HowTo schema test [HPC-122](https://dotdash.atlassian.net/browse/HPC-122)

## 3.11.302
 - Updated Skimlink price api display test[HPC-50](https://dotdash.atlassian.net/browse/HPC-50)

## 3.11.301
 - Updating globe version to fix tablet user agent handling [GLBE-7484](https://dotdash.atlassian.net/browse/GLBE-7484)

## 3.11.300
 - Add Skimlinks Pricing API click url update [CMRC-1066](https://dotdash.atlassian.net/browse/CMRC-1066)

## 3.11.299
 - Add aria-label to print button for accessibility[LW-2710](https://dotdash.atlassian.net/browse/LW-2710)

## 3.11.298
 - Add default .billboard size styles and handle margin for sc-ads placement [HPC-113](https://dotdash.atlassian.net/browse/HPC-113)

## 3.11.297 (SKIP to 3.11.298)
 - Updated howto schema to include description field [HPC-57](https://bitbucket.prod.aws.about.com/plugins/servlet/jira-integration/issues/HPC-57)

## 3.11.296 (SKIP to 3.11.298)
 - Refactored Inline citation component [HPC-88](https://dotdash.atlassian.net/browse/HPC-88)

## 3.11.295 (SKIP to 3.11.298)
 - Create generic method for SeleneUtils [DV-228](https://dotdash.atlassian.net/browse/DV-228)

## 3.11.294 (SKIP to 3.11.298)
 - Add RTB plugin for lotame lightning tag [AXIS-52](https://dotdash.atlassian.net/browse/AXIS-52)

## 3.11.293 (SKIP to 3.11.298)
 - Update Schema for Credit Card Rounup [PRM-796](https://dotdash.atlassian.net/browse/PRM-796)

## 3.11.292 (SKIP to 3.11.298)
 - Update globe version to get hippodrome validation for treehugger and add GREEN to BaseDocumentEx [GREEN-3](https://dotdash.atlassian.net/browse/GREEN-3)

## 3.11.291 (SKIP to 3.11.298)
 - Modifying MntlPrimaryImageTest class to work if when image response count passed from the verticals [AXIS-18](https://dotdash.atlassian.net/browse/AXIS-18)

## 3.11.290 (SKIP to 3.11.298)
 - Remove test-jar plugin from mantle-venus-test and move tests to main package.Will BREAK venus-test poms.Resolution is in ticket [DV-227](https://dotdash.atlassian.net/browse/DV-227)

## 3.11.289 (SKIP to 3.11.298)
 - Removing author from unified taxonomy schema [GLBE-7486](https://dotdash.atlassian.net/browse/GLBE-7486)

## 3.11.288 (SKIP to 3.11.298)
 - Add commerce disable sass [CMRC-1054](https://dotdash.atlassian.net/browse/CMRC-1054)

## 3.11.287 (SKIP to 3.11.298)
 - Sunset scAdsPerf test [BEAUT-1607](https://dotdash.atlassian.net/browse/BEAUT-1607)

## 3.11.286
 - Google news sitemap support added [GLBE-7371](https://dotdash.atlassian.net/browse/GLBE-7371)
 - Vertical related automation refer to [GLBE-7376](https://dotdash.atlassian.net/browse/GLBE-7376)

## 3.11.285
 - Add dynamic locations to .accordion_body in accordion.ftl [PRM-819](https://dotdash.atlassian.net/browse/PRM-819)

## 3.11.284
 - Added projection as an optional parameter to document curated list tasks [HLTH-5895](https://dotdash.atlassian.net/browse/HLTH-5895)

## 3.11.283
 - Updated hippodrome version [GLBE-7478](https://dotdash.atlassian.net/browse/GLBE-7478)

## 3.11.282
 - Modifying MntlPrimaryImageTest class to work if imagesrcset, imagesizes are not present in the verticals [AXIS-18](https://dotdash.atlassian.net/browse/AXIS-18)

## 3.11.281
 - Fix issue with commerce button introduced in 3.11.280 [NO-TICKET]

## 3.11.280 (SKIP to 3.11.281)
 - Implement call to action field for future use [GLBE-7356](https://dotdash.atlassian.net/browse/GLBE-7356)

## 3.11.279
 - Remove susy dependencies from mantle [NO-TICKET]
 - Upgrade mantle-grunt version to 4.1.12 [NO-TICKET]

## 3.11.278
 - Fixed an issue where the embed block didn't update the height properly to cause a in content vertical scroll bar. [GLBE-7310](https://dotdash.atlassian.net/browse/GLBE-7310)

## 3.11.277
 - Fix newsTask for Lithosphere. No ticket.

## 3.11.276
 - Update logger tracking id in abstract structured content to error, but json ignore to protect from noise during redis caching [GLBE-7435](https://dotdash.atlassian.net/browse/GLBE-7435)

## 3.11.275
 - Prepending hero image on Google News RSS feed only if primary image is set on document. [GLBE-7421](https://dotdash.atlassian.net/browse/GLBE-7421)

## 3.11.274
 - Fix proctor failing to start up when no tracking id [GLBE-7447](https://dotdash.atlassian.net/browse/GLBE-7447)
 - Update locking in proctor refresh to avoid situation where we attempt to unlock with no lock [GLBE-7443](https://dotdash.atlassian.net/browse/GLBE-7443)

## 3.11.273
 - Fixed missing closing square bracket. [HPC-69](https://dotdash.atlassian.net/browse/HPC-69)

## 3.11.272
 - Added in ignore error for leaderboard test if missing payload. No ticket.

## 3.11.271
 - Revert the leaderboard animation changes [GLBE-7460](https://dotdash.atlassian.net/browse/GLBE-7460)

## 3.11.270
 - Updated breadcrumbs so now breadcrumbs reflect a max of 3 levels in schema [GLBE-7327](https://dotdash.atlassian.net/browse/GLBE-7327)

## 3.11.269
 - Update primaryImageLinkHeader task method to not require srcset argument [AXIS-18](https://dotdash.atlassian.net/browse/AXIS-18)

## 3.11.268
 - Added support for theme in image blocks [HLTH-5589](https://dotdash.atlassian.net/browse/HLTH-5589)

## 3.11.267
 - Added additional logging and halt-app logic to legacy map implementation [FIN-2051](https://dotdash.atlassian.net/browse/FIN-2051)

## 3.11.266
 - Added in Leaderboard Test 4 and 5 implementation. Test 4: adds an X to close the leaderboard div. Test 5: Uses a payload to determine stickiness. [GLBE-7409](https://dotdash.atlassian.net/browse/GLBE-7409)

## 3.11.265
- Support Review Rating Override for Review Schema. [FIN-2085]https://dotdash.atlassian.net/browse/FIN-2085)

## 3.11.264
 - Handling for no items in materialsGroup and toolsGroup [HPC-2](https://dotdash.atlassian.net/browse/HPC-2)

## 3.11.263
 - Updating taxonomy structure in unified schema. [GLBE-7379](https://dotdash.atlassian.net/browse/GLBE-7379)
 - Vertical related automation refer to [GLBE-7382](https://dotdash.atlassian.net/browse/GLBE-7382)

## 3.11.262
- Update logger for empty Mantle Proctor Supplier to warn from error [NO-TICKET]

## 3.11.261
- Update logger tracking id in abstract structured content to warn [NO-TICKET]

## 3.11.260 (SKIP to 3.11.261)
- Change leaderboard transform animation duration. [NO-TICKET]

## 3.11.259 (SKIP to 3.11.261)
 - Updated Venus Version for new report structure for looker App. [GLBE-7348]https://dotdash.atlassian.net/browse/GLBE-7348)

## 3.11.258 (SKIP to 3.11.261)
 - GLBE-7435: Auto Skimlink price API display update [GLBE-7435]https://dotdash.atlassian.net/browse/GLBE-7435)

## 3.11.257 (SKIP to 3.11.261)
 - Add Latest News Tasks to get the latest news published for a vertical. [GLBE-7432](https://dotdash.atlassian.net/browse/GLBE-7432)

## 3.11.256 (SKIP to 3.11.261)
 - Update Article Sources in inline citation components to use span instead of h3. [GLBE-7395](https://dotdash.atlassian.net/browse/GLBE-7395)

## 3.11.255 (SKIP to 3.11.261)
 - Adding support for global proctor [GLBE-7227](https://dotdash.atlassian.net/browse/GLBE-7227)

## 3.11.254 (SKIP to 3.11.261)
 - Add proctor test support for JWPlayer motion thumbnails [FULL-45](https://dotdash.atlassian.net/browse/FULL-45)

## 3.11.253 (SKIP to 3.11.261)
 - Adding optional deduplicated cache structure for taxene composite and related articles. [GLBE-7406](https://dotdash.atlassian.net/browse/GLBE-7406)

## 3.11.252 (SKIP to 3.11.261)
 - Update StructuredContentContent to use uuid instead of id, depricating id. [GLBE-7350](https://dotdash.atlassian.net/browse/GLBE-7350)

## 3.11.251
 - SOLR 7 upgrade changes [GLBE-7403](https://dotdash.atlassian.net/browse/GLBE-7403)

## 3.11.250
 - Adding more safeguards for the leaderboard to remove the sticky classes when the timeout handler fires. No ticket.

## 3.11.249
 - Updated notification-banner.js to set dismiss cookie based on component data attribute `cookie-prefix`. Falls back to standard `notification_banner_dismissed` if nothing found. [GLBE-7206](https://dotdash.atlassian.net/browse/GLBE-7206)

## 3.11.248
 - Locking Leaderboard Phase 2. Leaderboard collapses on impression or timeout event instead of on scroll. [GLBE-7423](https://dotdash.atlassian.net/browse/GLBE-7423)

## 3.11.247
 - Leaderboard Scroll cleanup to be less agressive. No ticket but part of a fix on [GLBE-7405](https://dotdash.atlassian.net/browse/GLBE-7405)

## 3.11.246 (SKIP to 3.11.247)
 - Locking Leaderboard AB Test setup Phase 1. [GLBE-7405](https://dotdash.atlassian.net/browse/GLBE-7405)

## 3.11.245
 - Always pass in `enableOptimizations` flag to `MantleGTMPageViewTask`. [BEAUT-1535](https://dotdash.atlassian.net/browse/BEAUT-1535)

## 3.11.244
 - Move `sublayout-external-basic.scss` to the `/css/` directory so microverticals can access and extend it. [LQR-145](https://dotdash.atlassian.net/browse/LQR-145)

## 3.11.243
 - Allow leaderboard rollaway timeout to be controllable via data-attribute [BEAUT-1525](https://dotdash.atlassian.net/browse/BEAUT-1525)

## 3.11.242
 - Fixed NPE for guest author in RSS feeds. Improved readability. [BEAUT-1520](https://dotdash.atlassian.net/browse/BEAUT-1520)

## 3.11.241
 - Add `publisher` to `mntl-schema-unified-bio` [NO-TICKET]

## 3.11.240
 - Added Venus test to validate RTB bidders [NO-TICKET]

## 3.11.239
 - Adding support for watermarking thumbor images [GLBE-7121](https://dotdash.atlassian.net/browse/GLBE-7121)

## 3.11.238
 - Added in Programatic HasOffer tracking for PCM [GLBE-7286](https://dotdash.atlassian.net/browse/GLBE-7286)

## 3.11.237
 - Forward original requestId to external services [GLBE-7390](https://dotdash.atlassian.net/browse/GLBE-7390)

## 3.11.236
 - Add new SUBHEADING block in RSS workflow. Change HEADING block tag from <h3> to <h2> in content aggregation [BEAUT-1315](https://dotdash.atlassian.net/browse/BEAUT-1315)

## 3.11.235
 - Adjusted the MantleGTMPageViewTask to correctly resolve mini journeys for the data layer push. [GLBE-7313](https://dotdash.atlassian.net/browse/GLBE-7313)
 - Vertical related automation refer to [GLBE-7367](https://dotdash.atlassian.net/browse/GLBE-7367)

## 3.11.234
 - Document Schema refactored [GLBE-7324](https://dotdash.atlassian.net/browse/GLBE-7324)
 - Vertical related automation refer to [GLBE-7329](https://dotdash.atlassian.net/browse/GLBE-7329)

## 3.11.233
 - Remove underline on hover so tooltip does not inherit style (https://stackoverflow.com/questions/4481318/css-text-decoration-property-cannot-be-overridden-by-child-element/4481356) [HLTH-5909](https://dotdash.atlassian.net/browse/HLTH-5909)
 - Fix logic for appending "data-tooltip" attribute to guest authors and generic review stamps [HLTH-5909](https://dotdash.atlassian.net/browse/HLTH-5909)

## 3.11.232
 - Add commerce layout for FPR [CMRC-935](https://dotdash.atlassian.net/browse/CMRC-935)

## 3.11.231
 - Added in Mutex Argument for Yarn commands for parallelized runs in resources [GLBE-7370](https://dotdash.atlassian.net/browse/GLBE-7370)

## 3.11.230
 - Amendment to mntl-schema-unified-reviewer in order to accept `metaData.review.type` identifier overrides from verticals [BEAUT-1428](https://dotdash.atlassian.net/browse/BEAUT-1428)

## 3.11.229
 - Added legacy getDefaultPresentationTemplateName to top-level interface[GLBE-7373](https://dotdash.atlassian.net/browse/GLBE-7373)

## 3.11.228
 - Removed unused id button-pinterest-image to fix accessibility issues  [LQR-168](https://dotdash.atlassian.net/browse/LQR-168)

## 3.11.227
 - Added Task annotations to interface and abstract classes to see if it solves the issue in the ticket [GLBE-7373](https://dotdash.atlassian.net/browse/GLBE-7373)

## 3.11.226
 - Add freemarker comment to utilities.ftl to prevent pre-load blank space in deferred components [SPEED-52](https://dotdash.atlassian.net/browse/SPEED-52)

## 3.11.225
 - Wrapping logback handler to provide logging around null access log entries and handle MDC context for AsyncEvents [GLBE-7302](https://dotdash.atlassian.net/browse/GLBE-7302)

## 3.11.224
 - Pass correct commerce link click event for optimized GTM tracking [TS-4826](https://dotdash.atlassian.net/browse/TS-4826)

## 3.11.223
 - Remove skimlinks type check for product pricing display [CMRC-854](https://dotdash.atlassian.net/browse/CMRC-854)

## 3.11.222
 - Adding isprod condition to mntl MntlPrivacyRequestTest [GLBE-7357](https://dotdash.atlassian.net/browse/GLBE-7357)

## 3.11.221
 - Pass correct GTM optimization key [TS-4825](https://dotdash.atlassian.net/browse/TS-4825)

## 3.11.220
 - Validate Skimlinks Price API button display price.[CMRC-940](https://dotdash.atlassian.net/browse/CMRC-940)

## 3.11.219
 - Fix bug always passing true for GTM optimization key [TS-4825](https://dotdash.atlassian.net/browse/TS-4825)

## 3.11.218 (Skip to 3.11.219)
 - Wrap performance-refactored sc-ads.js in test [SPEED-45](https://dotdash.atlassian.net/browse/SPEED-45)

## 3.11.217 (Skip to 3.11.219)
 - Strip HTML tags from ShortBio field for schema [HLTH-5793](https://dotdash.atlassian.net/browse/HLTH-5793)

## 3.11.216 (Skip to 3.11.219)
 - Added new DataLayer tests to adjust to new changes(refer to LQR ticket attached) [LQR-131](https://dotdash.atlassian.net/browse/LQR-151)

## 3.11.215 (Skip to 3.11.219)
 - Add back "nopush" to link headers, within a scalable test [SPEED-55](https://dotdash.atlassian.net/browse/SPEED-55)

## 3.11.214 (Skip to 3.11.219)
 - Changed mntl-browserconfig ftl file name to avoid file name collisions and stopping the server from starting. No Ticket.

## 3.11.213 (Skip to 3.11.219)
 - Restore properties removed by accident that should be opt in to remove for GTM [TS-4816](https://dotdash.atlassian.net/browse/TS-4816)

## 3.11.212 (Skip to 3.11.219)
 - Added new specs for favicons. If upgrading past this version, ensure you have the icon sizes specified in **favicons.ftl** and **browserconfig.ftl** [HLTH-5776](https://dotdash.atlassian.net/browse/HLTH-5776)

## 3.11.211 (Skip to 3.11.219)
 - Added primary image link headers support for older versions of Chrome [GLBE-7337](https://dotdash.atlassian.net/browse/GLBE-7337)

## 3.11.210 (Skip to 3.11.219)
 - Revert removal of empty script/style tags, which are used for inserting async scripts [NO-TICKET]

## 3.11.209 (Skip to 3.11.219)
 - Preventing ads from displaying after subheading blocks [GLBE-7323](https://dotdash.atlassian.net/browse/GLBE-7323)
 - vertical related automation refer to [GLBE-7335](https://dotdash.atlassian.net/browse/GLBE-7335)

## 3.11.208 (Skip to 3.11.219)
 - Create the commerce round-up layout [CMRC-751](https://dotdash.atlassian.net/browse/CMRC-751)

## 3.11.207 (Skip to 3.11.219)
 - Don't add empty evaluated script/style tags [SPEED-26](https://dotdash.atlassian.net/browse/SPEED-26)

## 3.11.206 (Skip to 3.11.219)
 - Changing Google News RSS Feed item pub date default to First Published date [GLBE-7250](https://dotdash.atlassian.net/browse/GLBE-7250)

## 3.11.205 (Skip to 3.11.219)
 - Added opt in removal of gtm values off of the consul key com.about.globe.gtm.optimization.enabled [LQR-151](https://dotdash.atlassian.net/browse/LQR-151)

## 3.11.204
 - Adding rss feed builder for Google News [GLBE-7250](https://dotdash.atlassian.net/browse/GLBE-7250)

## 3.11.203
 - Automation regression failure fix with ExternalComponentSvg test[GLBE-7336](https://dotdash.atlassian.net/browse/GLBE-7336)

## 3.11.202
 - Automation for Medical Web Page schema [HLTH-5788](https://dotdash.atlassian.net/browse/HLTH-5788)

## 3.11.201
 - Added a missing Audience field to Medical Web Page schema (https://dotdash.atlassian.net/browse/HLTH-5662)

## 3.11.200
 - Fixing NPE while calculating redis unique cache key [NO-TICKET]

## 3.11.199
 - Reorganized sc-ads _makeRightRailInterface function to perform better on mobile [SPEED-30](https://dotdash.atlassian.net/browse/SPEED-30)

## 3.11.198
 - Remove unused modernizr functions [SPEED-32](https://dotdash.atlassian.net/browse/SPEED-32)

## 3.11.197
 - Update unified article schema to show 'NewsArticle' when the document has a newsType set [GLBE-7252](https://dotdash.atlassian.net/browse/GLBE-7252)
 - Added `newsType` to document [GLBE-7252](https://dotdash.atlassian.net/browse/GLBE-7252)
 - Vertical related automation refer to [GLBE-7299](https://dotdash.atlassian.net/browse/GLBE-7299)

## 3.11.196
 - Making mntl-html click-trackable in GA [GLBE-7273](https://dotdash.atlassian.net/browse/GLBE-7273)
 - vertical related automation refer to [GLBE-7320](https://dotdash.atlassian.net/browse/GLBE-7320)

## 3.11.195
 - Sunset test removing nopush from local resources, now permanent [SPEED-38](https://dotdash.atlassian.net/browse/SPEED-38)

## 3.11.194
 - Renaming commerce-disclosure widget files to mntl-commerce-disclosure to avoid conflicts with similarly-named components in verticals [GLBE-7321](https://dotdash.atlassian.net/browse/GLBE-7321)

## 3.11.193 (Skip to 3.11.194)
 - Fix bug in which deferred elements in the `<head>` were inconsistently loading due to a race condition. [SPEED-8](https://dotdash.atlassian.net/browse/SPEED-8)

## 3.11.192 (Skip to 3.11.194)
 - Medical Web Page schema now has same fields as Web Page schema [HLTH-5730](https://dotdash.atlassian.net/browse/HLTH-5730)
 - Added lastReviewed and reviewedBy schema object to Web Page schema [HLTH-5662](https://dotdash.atlassian.net/browse/HLTH-5662)

## 3.11.191 (Skip to 3.11.194)
 - Remove device regex for lighthouse so it's not always assumed desktop [GLBE-7260](https://dotdash.atlassian.net/browse/GLBE-7260)

## 3.11.190 (Skip to 3.11.194)
 - Adding documentTask to the ProctorGlobeTestFramework [CMRC-917](https://dotdash.atlassian.net/browse/CMRC-917)

## 3.11.189 (Skip to 3.11.194)
 - Do not send empty ad units object to mediagrid to prevent error [GLBE-7248](https://dotdash.atlassian.net/browse/GLBE-7248)

## 3.11.188 (Skip to 3.11.194)
 - Temporarily disable missing recirc logging[GLBE-7303](https://dotdash.atlassian.net/browse/GLBE-7303)

## 3.11.187 (Skip to 3.11.194)
 - Adding commerce-disclosure component. Updating commerce info criteria, and existing disclosure and affiliate checks. [GLBE-7239](https://dotdash.atlassian.net/browse/GLBE-7239)
 - Vertical related automation refer to [GLBE-7276](https://dotdash.atlassian.net/browse/GLBE-7276)

## 3.11.186
 - Use camelCase key to get actual chopHeight value from dataset [LQR-106](https://dotdash.atlassian.net/browse/LQR-106)

## 3.11.185
- Skimlinks test for image,listHeader & button clicks [CMRC-832](https://dotdash.atlassian.net/browse/CMRC-832)

## 3.11.184
 - Use lastPublished for Amazon RSS instead of displayedDate [CMRC-760](https://dotdash.atlassian.net/browse/CMRC-760)

## 3.11.183
 - Allow for deferred loading of elements in `<head>`. Wrap schema-unified component in component tag to allow for deferred loading [SPEED-8](https://dotdash.atlassian.net/browse/SPEED-8)

## 3.11.182
 - Venus Improvements for CI/CD. Updated venus versions [GLBE-7265](https://dotdash.atlassian.net/browse/GLBE-7265)

## 3.11.181
 - Only include picturefill for IE and Opera Mini [SPEED-5](https://dotdash.atlassian.net/browse/SPEED-5)

## 3.11.180
 - Remove old proctor format when using single call version for GA [LQR-112](https://dotdash.atlassian.net/browse/LQR-112)

## 3.11.179
 - Add fields to datalayer and environmental data for gtm [LQR-111](https://dotdash.atlassian.net/browse/LQR-111)
 - Added validation for new fields - when vertical opt in, they would need to override the expected default value "" [LQR-111](https://dotdash.atlassian.net/browse/LQR-111)

## 3.11.178
 - Add component to pass proctor abTest data in improved format to GA [LQR-112](https://dotdash.atlassian.net/browse/LQR-112)

## 3.11.177
 - Tooltip recalculates X and Y positions on each over  [PRM-576](https://dotdash.atlassian.net/browse/PRM-576)

## 3.11.176
 - Utilize url query params in RSS feeds for taxonomy-based Google News feeds in Beauty [BEAUT-1314](https://dotdash.atlassian.net/browse/BEAUT-1314)

## 3.11.175
 - Access logs bug fix - wrong document info was logged [GLBE-7255](https://dotdash.atlassian.net/browse/GLBE-7255)

## 3.11.174
 - Automation for Dotdash global footer [GLBE-7267](https://dotdash.atlassian.net/browse/GLBE-7267)

## 3.11.173
 - Add isprod condition for ccpa automation [GLBE-7284](https://dotdash.atlassian.net/browse/GLBE-7284)

## 3.11.172
 - Simplified Family Nav for SEO requirements [GLBE-7244](https://dotdash.atlassian.net/browse/GLBE-7244)
 - Impacts Automation.[GLBE-7267](https://dotdash.atlassian.net/browse/GLBE-7267)

## 3.11.171
 - Added test for removing "nopush" from local resource link headers [SPEED-1](https://dotdash.atlassian.net/browse/SPEED-1)

## 3.11.170
 - Create functions to read testing URLs from json file that can be used by all verticals. [DV-212](https://dotdash.atlassian.net/browse/DV-212)

## 3.11.169
 - add ability to delay `Mntl.GPT.init()` until first scroll. Controlled via `initOnScroll` property passed into mntl-gpt-definition [SPEED-2](https://dotdash.atlassian.net/browse/SPEED-2)

## 3.11.168
 - Fixing issue of hashset being used in RedisCacheKey [GLBE-6865](https://dotdash.atlassian.net/browse/GLBE-6865)

## 3.11.167
 - Add `LIQUOR` to SearchService `AlgorithmType` for Liquor.com site search [LQR-121](https://dotdash.atlassian.net/browse/LQR-121)

## 3.11.166
 - Fixed Structured Content Image Block in the Pattern Library [GLBE-6948](https://dotdash.atlassian.net/browse/GLBE-6948)

## 3.11.165
 - Moved d3 from package json to cdn for Task Execution Bookmarklet [GLBE-7191](https://dotdash.atlassian.net/browse/GLBE-7191)

## 3.11.164
 - Fixed scriptOnLoad callback not firing when the script already loaded [GLBE-7230](https://dotdash.atlassian.net/browse/GLBE-7230)

## 3.11.163
 - Added breadcrumb object to medical web page schema [HLTH-5678](https://dotdash.atlassian.net/browse/HLTH-5678)

## 3.11.162
 - updated test code in mantle to fix few regression issues  [GLBE-7163](https://dotdash.atlassian.net/browse/GLBE-7163)
 - Also updated the venus version to 1.3.22 which fix recent chrome cert issues

## 3.11.161
 - Addressing issue deserializing JourneyRootIdAndRelationshipType [GLBE-7241](https://dotdash.atlassian.net/browse/GLBE-7241)

## 3.11.160
 - CCPA web form automation [GLBE-7199](https://dotdash.atlassian.net/browse/GLBE-7199)

## 3.11.159
 - Fixed CCPA looking for wrong char code (no ticket)

## 3.11.158
 - Fixed CCPA Accessibility issues [GLBE-7233](https://dotdash.atlassian.net/browse/GLBE-7233)

## 3.11.157
 - Adding default value to Sandboxed Content Base URL [GLBE-7215](https://dotdash.atlassian.net/browse/GLBE-7215)

## 3.11.156
 - Added Venus model component for exclusive byline [LW-2604](https://dotdash.atlassian.net/browse/LW-2604)

## 3.11.155
 - Now using `domain` instead of `seleneVertical` in compliance because seleneVertical isn't supported by external verts.  No ticket.

## 3.11.154
 - CCPA email form class typo fix [GLBE-7216](https://dotdash.atlassian.net/browse/GLBE-7216)

## 3.11.153
 - CCPA Notification Banner Text edit [GLBE-7234](https://dotdash.atlassian.net/browse/GLBE-7234)

## 3.11.152
 - CCPA email form polish. [GLBE-7216](https://dotdash.atlassian.net/browse/GLBE-7216)

## 3.11.151
 - Remove style tag from SVG file and use inline styles instead to avoid unwanted side effects with other SVGs [HLTH-5123](https://dotdash.atlassian.net/browse/HLTH-5123)

## 3.11.150
 - update Mantle common test for ad calls [GLBE-7174](https://dotdash.atlassian.net/browse/GLBE-7174)

## 3.11.149
 - Check for null on nextElementSibling [HLTH-5670](https://dotdash.atlassian.net/browse/HLTH-5670)
 - use @supports feature query for backdrop-filter instead of browser-specific media queries [HLTH-5675](https://dotdash.atlassian.net/browse/HLTH-5675)

## 3.11.148
 - Update CCPA banner with final copy [GLBE-7181](https://dotdash.atlassian.net/browse/GLBE-7181)

## 3.11.147
 - Add in-journey optional document task for journeyDocumentsAfterToBeforeInclusive [TRIP-2256](https://dotdash.atlassian.net/browse/TRIP-2256)

## 3.11.146
 - Mantle change for geo ips tests[GLBE-7217](https://dotdash.atlassian.net/browse/GLBE-7217)

## 3.11.145
 - Automation for new CCPA Footer Banner [GLBE-7207](https://dotdash.atlassian.net/browse/GLBE-7207)

## 3.11.144
 - Added CCPA Web Form Component [GLBE-7189](https://dotdash.atlassian.net/browse/GLBE-7189)

## 3.11.141-143
 - DO NOT USE

## 3.11.140
 - Update Hide Advertisement Label CSS [FIN-1703](https://dotdash.atlassian.net/browse/FIN-1703)

## 3.11.139
 - Add new CCPA Footer Banner [GLBE-7181](https://dotdash.atlassian.net/browse/GLBE-7181)

## 3.11.138
 - byline tooltip bug fixes [HLTH-5594](https://dotdash.atlassian.net/browse/HLTH-5594)
   - IE11 fix for tooltip content not rendering on subsequent hovers
   - collapse bio image if document has guest author
   - handle empty string in guestAuthor uri and shortBio field

## 3.11.137
 - added GTIN field [CMRC-824](https://dotdash.atlassian.net/browse/CMRC-824)Parent story - (https://dotdash.atlassian.net/browse/CMRC-740)

## 3.11.136
 - Review Schema update - Replace all identifierTypes with GTIN for SEO [CMRC-740](https://dotdash.atlassian.net/browse/CMRC-740)

## 3.11.135
 - update Mantle common test for ad calls for google change of iu_parts to iu [GLBE-7174](https://dotdash.atlassian.net/browse/GLBE-7174)

## 3.11.134
 - Removed preload components from embed endpoint [BEAUT-1316](https://dotdash.atlassian.net/browse/BEAUT-1316)

## 3.11.133
 - Fixing the functionality of isMiniJourneyTask [FIN-1283](https://dotdash.atlassian.net/browse/FIN-1283)
 - Adding isSingleSectionJourneyTask [FIN-1283 (same ticket)](https://dotdash.atlassian.net/browse/FIN-1283)

## 3.11.132
 - Added `isCcpaApplicableRequest` task for CCPA [GLBE-7187](https://dotdash.atlassian.net/browse/GLBE-7187)

## 3.11.131
 - Add review component (with logic to display review date) and updated date component [HLTH-5592](https://dotdash.atlassian.net/browse/HLTH-5592)
 - Add mntl-bio-tooltip component [HLTH-5594](https://dotdash.atlassian.net/browse/HLTH-5594)

## 3.11.130
 - Client for s2s RTB implementation [GLBE-6941](https://dotdash.atlassian.net/browse/GLBE-6941)

## 3.11.129
 - Adding MediaGrid to Preconnect List [GLBE-7154](https://dotdash.atlassian.net/browse/GLBE-7154)

## 3.11.128
 - Fixing howToSteps macro to avoid invalid schema json creation [GLBE-7090](https://dotdash.atlassian.net/browse/GLBE-7090)
 - Vertical related automation refer to [GLBE-7185](https://dotdash.atlassian.net/browse/GLBE-7185)

## 3.11.127
 - Added venus utility for posting a custom document qa selene [GLBE-7019](https://dotdash.atlassian.net/browse/GLBE-7019)

## 3.11.126
 - Updating globe-core version for additional cache management options [GLBE-7082](https://dotdash.atlassian.net/browse/GLBE-7082)

## 3.11.125
 - Adding expiry / FIFO eviction to cache clearance candidate list [GLBE-7088](https://dotdash.atlassian.net/browse/GLBE-7088)

## 3.11.124
 - Create Credit Card Roundup list item schema [PRM-506](https://dotdash.atlassian.net/browse/PRM-506)

## 3.11.123
 - Adding primary image component with common task used for link header [GLBE-7103](https://dotdash.atlassian.net/browse/GLBE-7103)
 - Vertical related automation refer to [GLBE-7107](https://dotdash.atlassian.net/browse/GLBE-7107)

## 3.11.122
 - Add sensitive image overlay to image blocks that are tagged as sensitive [HLTH-5123](https://dotdash.atlassian.net/browse/HLTH-5123)

## 3.11.121
 - Update Amazon Rss date format [CMRC-812](https://dotdash.atlassian.net/browse/CMRC-812)

## 3.11.120
 - Sailthru metatag update [GLBE-7111](https://dotdash.atlassian.net/browse/GLBE-7111)
 - Vertical related automation refer to [GLBE-7131](https://dotdash.atlassian.net/browse/GLBE-7131)

## 3.11.119
 - Adding skimlinks (with affiliate check) to preconnect list. [GLBE-7162](https://dotdash.atlassian.net/browse/GLBE-7162)
 - Vertical related automation refer to [GLBE-7179](https://dotdash.atlassian.net/browse/GLBE-7179)

## 3.11.118
 - Added iaws-kw to IAS Header Bidder Targeting values. [GLBE-7137](https://dotdash.atlassian.net/browse/GLBE-7137)

## 3.11.117
 - Remove citation key from schema if there are no citations/sources [HLTH-5620](https://dotdash.atlassian.net/browse/HLTH-5620)

## 3.11.116
 - Fix syntax error with optional tag (no ticket)

## 3.11.115
 - Update masonry card list image to default to empty object [GLBE-7173](https://dotdash.atlassian.net/browse/GLBE-7173)

## 3.11.114
 - update Mantle common test for ad calls for google change of iu_parts to prev_iu_parts [GLBE-7174](https://dotdash.atlassian.net/browse/GLBE-7174)

## 3.11.113
 - Refactor redirect handler for ease and flexibility of extension [LQR-51](https://dotdash.atlassian.net/browse/LQR-51)

## 3.11.112
 - Fix error where static pages and others without disclosure were throwing spel exceptions [TS-4666](https://dotdash.atlassian.net/browse/TS-4666)

## 3.11.111
 - Update Lazy Ad Offset via proctor test for right rail ads [GLBE-7144](https://dotdash.atlassian.net/browse/GLBE-7144)

## 3.11.110 (SKIP to 3.11.111)
 - Added characterData to MutationObserver for IE/Edge bug [BEAUT-1278](https://dotdash.atlassian.net/browse/BEAUT-1278)

## 3.11.109 (SKIP to 3.11.111)
 - Update image caption to enable styling caption and credit differently. [LQR-15](https://dotdash.atlassian.net/browse/LQR-15)

## 3.11.108 (SKIP to 3.11.111)
 - Backend task for server-side RTB [GLBE-6939](https://dotdash.atlassian.net/browse/GLBE-6939)

## 3.11.107 (SKIP to 3.11.111)
 - Added auto test case for trustE badge [FIN-1195](https://dotdash.atlassian.net/browse/FIN-1195)

## 3.11.106 (SKIP to 3.11.111)
 - Adding Date field to GroupCallout block item [GLBE-7086](https://dotdash.atlassian.net/browse/GLBE-7086)
 - Vertical related automation refer to [GLBE-7126](https://dotdash.atlassian.net/browse/GLBE-7126)

## 3.11.105 (SKIP to 3.11.111)
 - Removing headings from TOC if block is flagged as hidden in TOC. [GLBE-7102](https://dotdash.atlassian.net/browse/GLBE-7102)
 - Vertical related automation refer to [GLBE-7130](https://dotdash.atlassian.net/browse/GLBE-7130)

## 3.11.104 (SKIP to 3.11.111)
 - Adding Gallery and GallerySlide blocks [GLBE-7093](https://dotdash.atlassian.net/browse/GLBE-7093)
 - Vertical automation refer to [GLBE-7128](https://dotdash.atlassian.net/browse/GLBE-7128)

## 3.11.103 (SKIP to 3.11.111)
 - Added abilities to have "linked" journeys [GLBE-7097](https://dotdash.atlassian.net/browse/GLBE-7097)

## 3.11.102 (SKIP to 3.11.111)
 - Updated auto dataLayer test [GLBE-7079](https://dotdash.atlassian.net/browse/GLBE-7079)

## 3.11.101 (SKIP to 3.11.111)
 - Make Lazy Ad offset configurable via proctor test: lazyAdOffset [GLBE-7144](https://dotdash.atlassian.net/browse/GLBE-7144)

## 3.11.100
 - Removed hardcoded heading tags from components and replaced them with property references. [GLBE-7072]
 (https://dotdash.atlassian.net/browse/GLBE-7072)

## 3.11.99
 - Added aboutPage schema for corporate viewType [HLTH-5570](https://dotdash.atlassian.net/browse/HLTH-5570)

## 3.11.98
 - Add LIQUOR ENUM to BaseDoc [LQR-28] (https://dotdash.atlassian.net/browse/LQR-28)

## 3.11.97
 - Automation fix MntlDisableSkimlinkTets [CMRC-797] (https://dotdash.atlassian.net/browse/CMRC-797)

## 3.11.96
 - Collapse affiliate components based on affiliate disclosure flag [CMRC-796](https://dotdash.atlassian.net/browse/CMRC-796)

## 3.11.95
 - Automation failure fix with mantle ref [GLBE-7120](https://dotdash.atlassian.net/browse/GLBE-7120)

## 3.11.94
 - Remove disconnect of Instagram and Twitter embed MutationObservers to more ensure the full height of the embed iframe is accounted for [BEAUT-1096](https://dotdash.atlassian.net/browse/BEAUT-1096)

## 3.11.93
 - Update globe version to 3.10.17 to get the updated hippodrome for Liquor support [LQR-28](https://dotdash.atlassian.net/browse/LQR-28)

## 3.11.92
 - Added place holder component in mntl-sources so that verticals can add things before citation sources [HLTH-5485](https://dotdash.atlassian.net/browse/HLTH-5485)

## 3.11.91
 - Adhesive Ad Fix for Mobile Safari [GLBE-7114](https://dotdash.atlassian.net/browse/GLBE-7114)

## 3.11.90
 - Adjusting ad tables to place the ad disclosure above the table. [FIN-1467](https://dotdash.atlassian.net/browse/FIN-1467)

## 3.11.89
  - Added timeout to proxied click event in extended-commerce-sc-info.js to fix skimlinks issue (affects commerce buttons) [CMRC-349](https://dotdash.atlassian.net/browse/CMRC-349)
  - Added timeout to proxied click event in product.js to fix skimlinks issue (affects product buttons) [CMRC-349](https://dotdash.atlassian.net/browse/CMRC-349)

## 3.11.88
 - Fix eventLabel on byline tooltip open [HLTH-5574](https://dotdash.atlassian.net/browse/HLTH-5574)
 - Check document.guestAuthor.link.text and document.guestAuthor.link.uri for empty string as well as null [HLTH-5575](https://dotdash.atlassian.net/browse/HLTH-5575)

## 3.11.87
 - Updating Globe-core version for VerticalUrlData legacy URL changes [GLBE-6908](https://dotdash.atlassian.net/browse/GLBE-6908)

## 3.11.86
 - Adjust logic for collapsing non-existent bio-block images to take into account empty ImageEx objects [TS-4644](https://dotdash.atlassian.net/browse/TS-4644)

## 3.11.85
 - Propagating release/3.10 to release/3.11 (3.10.399-400)
   - Adding filter and support for sandboxed content domain [GLBE-6960](https://dotdash.atlassian.net/browse/GLBE-6960)
   - Vertical related automation for [GLBE-7067](https://dotdash.atlassian.net/browse/GLBE-7067) refer to [GLBE-7079](https://dotdash.atlassian.net/browse/GLBE-7079)

## 3.11.84
 - Changed Video Event Label to the mediaId for easier tracking. [GLBE-7062](https://dotdash.atlassian.net/browse/GLBE-7062)
 - Vertical related automation refer to [GLBE-7112](https://dotdash.atlassian.net/browse/GLBE-7112)

## 3.11.83
 - Get readyForThirdPartyTracking to be pushed to the DataLayer properly for event management. [GLBE-7075](https://dotdash.atlassian.net/browse/GLBE-7075)

## 3.11.82
- Fixing mntl-sponsor-tracking-codes-wrapper check for 404 pages [GLBE-7091](https://dotdash.atlassian.net/browse/GLBE-7091)

## 3.11.81
 - Adding support for using `Rollaway` component with Journeys [CMRC-666](https://dotdash.atlassian.net/browse/CMRC-666)

## 3.11.80
 - Added in third party pixel tracking for jwplayer [GLBE-7010](https://dotdash.atlassian.net/browse/GLBE-7010)

## 3.11.79 (SKIP TO 3.11.88 as eventLabel is not being tracked in byline tooltip)
 - Add mntl-byline component which uses dynamic tooltip [HLTH-5501](https://dotdash.atlassian.net/browse/HLTH-5501)
 - Fix guest author logic when no bio URL present for guest author [HLTH-5542](https://dotdash.atlassian.net/browse/HLTH-5542)

## 3.11.78
 - Recalculate lazy ads list for ads changing offsets within a chop [TS-4486](https://dotdash.atlassian.net/browse/TS-4486)

## 3.11.77
 - Review Schema: Add author info in a review block within itemReviewed [FIN-1449](https://dotdash.atlassian.net/browse/FIN-1449)

## 3.11.76
 - Add support for port.offset in JMXMP bean [GLBE-6595](https://dotdash.atlassian.net/browse/GLBE-6595)

## 3.11.75
 - Fix a bug (an edge case) in rss feed when either duplicate/varying items are returned by SOLR [FIN-1513](https://dotdash.atlassian.net/browse/FIN-1513)

## 3.11.74
 - Use Amazon PAAPI V5 in AmazonProductTask for legacy templates (no ticket)

## 3.11.73
 - Add support for `customPlaceholder` property (svg placeholder) in mntl-image [BEAUT-1176](https://dotdash.atlassian.net/browse/BEAUT-1176)

## 3.11.72
 - MediaGrid Event fixed which maybe interfering with rtbTrackingEvents [GLBE-7070](https://dotdash.atlassian.net/browse/GLBE-7070)

## 3.11.71
 - Switch task used by medical schema to isLocalDateBetween and fixed for targeting wrong id in reviewer [HLTH-5541](https://dotdash.atlassian.net/browse/HLTH-5541)

## 3.11.70 (SKIP to 3.11.71)
 - Remove health specific task from medical schema [TS-4583](https://dotdash.atlassian.net/browse/TS-4583)

## 3.11.69 (SKIP to 3.11.71)
 - Bugfix on lastReviewed schema [HLTH-5489](https://dotdash.atlassian.net/browse/HLTH-5489)

## 3.11.68
 - Add aria-label to truste badge for accessibility [TRIP-2116](https://dotdash.atlassian.net/browse/TRIP-2116)

## 3.11.67
 - Added audience schema component for use in medicalwebpage but also available for use elsewhere [HLTH-5510](https://dotdash.atlassian.net/browse/HLTH-5510)

## 3.11.66
 - Added lastReviewed schema [HLTH-5489](https://dotdash.atlassian.net/browse/HLTH-5489)

## 3.11.65
 - Adding preprocessor to convert H4 HTML blocks to SUBHEADING blocks [GLBE-7063](https://dotdash.atlassian.net/browse/GLBE-7063)

## 3.11.64
 - Reverting prematurely merged GLBE-7063 changes [GLBE-7063](https://dotdash.atlassian.net/browse/GLBE-7063)

## 3.11.63 (SKIP to 3.11.64)
 - Adding preprocessor to convert H4 HTML blocks to SUBHEADING blocks [GLBE-7063](https://dotdash.atlassian.net/browse/GLBE-7063)

## 3.11.62
 - Propagating release/3.10 to release/3.11 (3.10.397-398)
   - Add bounceExchangeId to pageViewDataAsJSON object [GLBE-7067](https://dotdash.atlassian.net/browse/GLBE-7067)
   - Add better error handling to Bio Bock component and wrapper div [GLBE-7087](https://dotdash.atlassian.net/browse/GLBE-7087)

## 3.11.61
 - Add citations schema component which includes citations and sources of document [HLTH-5437](https://dotdash.atlassian.net/browse/HLTH-5437)

## 3.11.60
 - Fix lazy ads loading under a chop before its opened [TS-4376](https://dotdash.atlassian.net/browse/TS-4376)

## 3.11.59
 - Propagating release/3.10 to release/3.11 (3.10.396)
   - Remove short last paragraphs from Amazon RSS feed [CMRC-700](https://dotdash.atlassian.net/browse/CMRC-700)

## 3.11.58
 - Propagating release/3.10 to release/3.11 (3.10.393-395)
   - Add sponsor tracking codes to base layout (TRUSTED ONLY) [GLBE-6851](https://dotdash.atlassian.net/browse/GLBE-6851)
   - Vertical automation refer to [GLBE-7023](https://dotdash.atlassian.net/browse/GLBE-7023)
   - Automation for Content Security Policy
   - Adding support for Group Callout Structured Content block.[GLBE-7039](https://dotdash.atlassian.net/browse/GLBE-7039)
   - Vertical related automation refer to [GLBE-7040](https://dotdash.atlassian.net/browse/GLBE-7040)

## 3.11.57
 - Propagating release/3.10 to release/3.11 (3.10.390-392)
   - Add Bio content block support [GLBE-6823](https://dotdash.atlassian.net/browse/GLBE-6823)
   - Vertical related automation refer to [GLBE-7038](https://dotdash.atlassian.net/browse/GLBE-7038)
   - Using revenueGroup flag to drive commerce logic [GLBE-6980](https://dotdash.atlassian.net/browse/GLBE-6980)
   - Automation Changes: Upgrade to this version will throw compilation error for venus tests which are using isCommerceDocument field from MntlDataLayerTests class.More details in ticket: GLBE-7057(https://dotdash.atlassian.net/browse/GLBE-7057)

## 3.11.56
 - Added how to schema test[TS-4473](https://dotdash.atlassian.net/browse/TS-4473)

## 3.11.55
 - Allow non-verticals (commerce and PRM) to start server without Selene vertical in their config [GLBE-7058](https://dotdash.atlassian.net/browse/GLBE-7058)

## 3.11.54
 - Propagating release/3.10 to release/3.11 (3.10.389)
   - Add maybeGet for new images object in Amazon PAAPI V5 (no ticket)

## 3.11.53
 - Null checking added to structure content document processor where empty lists are possible [TS-4547](https://dotdash.atlassian.net/browse/TS-4547)

## 3.11.52
 - Propagating release/3.10 to release/3.11 (3.10.386-388)
   - Bug-fix - Pointing to fixed globe-core - Removed Catchpoint Regular expressions for evaluating useragents [GLBE-7052](https://dotdash.atlassian.net/browse/GLBE-7052)
   - CORS allow apollo [GLBE-7004](https://dotdash.atlassian.net/browse/GLBE-7004)
   - Automation for MediaGrid Case. [GLBE-6955](https://dotdash.atlassian.net/browse/GLBE-6955)
   - Vertical related automation refer to [GLBE-7049](https://dotdash.atlassian.net/browse/GLBE-7049)

## 3.11.51 (SKIP to 3.11.52)
 - Fix image schema for sbs [TS-4531](https://dotdash.atlassian.net/browse/TS-4531)

## 3.11.50 (SKIP to 3.11.52)
 - Fix missing quote in schema-unified-medical [HLTH-5470](https://dotdash.atlassian.net/browse/HLTH-5470)

## 3.11.49 (SKIP to 3.11.52)
 - Remove stray comma preventing valid schema for unified `mntl-schema-unified-main-entity` [https://dotdash.atlassian.net/browse/GLBE-7051](GLBE-7051)

## 3.11.48 (SKIP to 3.11.52)
 - Propagating release/3.10 to release/3.11 (3.10.385)
   - Fix for MediaGrid case when only partial bids are being returned. [GLBE-7034](https://dotdash.atlassian.net/browse/GLBE-7034)

## 3.11.47 (SKIP to 3.11.52)
 - Propagating release/3.10 to release/3.11 (3.10.384)
   - Globe changes for Linkman, globe reads from vertical specific topics for cache clearance.[GLBE-6952](https://dotdash.atlassian.net/browse/GLBE-6952)

## 3.11.46 (SKIP to 3.11.52)
 - Propagating release/3.10 to release/3.11 (3.10.383)
   - Added IndexExchange Queue so values are set in the correct order after async calls. [GLBE-6944](https://dotdash.atlassian.net/browse/GLBE-6944)

## 3.11.45 (SKIP to 3.11.52)
 - Propagating release/3.10 to release/3.11 (3.10.380-382)
   - Fixed Dynamic-Inline lazy-ads from not incrementing properly. No Ticket.
   - Update deferred svg loading to support IE11 [GLBE-6967](https://dotdash.atlassian.net/browse/GLBE-6967)
   - Add support for Amazon PAAPI V5 [CMRC-639](https://dotdash.atlassian.net/browse/CMRC-639)

## 3.11.44 (SKIP to 3.11.52)
 - add new MedicalWebPage schema component [HLTH-5438](https://dotdash.atlassian.net/browse/HLTH-5438)

## 3.11.43 (SKIP to 3.11.52)
 - Propagating release/3.10 to release/3.11 (3.10.378-379)
   - Updating globe core version for updated user agent db. [GLBE-6949](https://dotdash.atlassian.net/browse/GLBE-6949)
   - Changed taxonomy schema specialty to use document heading instead of title [HLTH-5338](https://dotdash.atlassian.net/browse/HLTH-5338)

## 3.11.42
 - Changed taxonomy schema specialty to use document heading instead of title [HLTH-5338](https://dotdash.atlassian.net/browse/HLTH-5338)

## 3.11.41
- Added check for empty string on Social Images [HLTH-5412](https://dotdash.atlassian.net/browse/HLTH-5412)

## 3.11.40
 - Propagating release/3.10 to release/3.11 (3.10.376-377)
   - Changed image schema logic to migrate away from schema proteus image [HLTH-5416](https://dotdash.atlassian.net/browse/HLTH-5416)
   - Extend cache clearing to secondary requests, e.g. AJAX, deferred/external components, proxies, etc. [GLBE-6877](https://dotdash.atlassian.net/browse/GLBE-6877)

## 3.11.39
 - Propagating release/3.10 to release/3.11 (3.10.374-375)
   - Bug fix for media grid and leaderboard not getting any bids [GLBE-6995](https://dotdash.atlassian.net/browse/GLBE-6995)
   - Adding compression to StructuredContentHtmlEx's string content to reduce cache size [GLBE-6920](https://dotdash.atlassian.net/browse/GLBE-6920)

## 3.11.38
- Add sort parameter to RSS feed search service. Add cache enable property check. Misc refactoring in service cache decorator [BEAUT-1074](https://dotdash.atlassian.net/browse/BEAUT-1074)

## 3.11.37
 - Propagating release/3.10 to release/3.11 (3.10.371-373)
   - Enabled mntl-chop support with lazy-ads.js for LISTSC and LIST templates. [GLBE-6929](https://dotdash.atlassian.net/browse/GLBE-6929)
   - Add validation for venus tests to track recirc doc ids in unified page view. No Ticket
   - Updating globe core version for TemplateComponentLoader improvements. [GLBE-6911](https://dotdash.atlassian.net/browse/GLBE-6911)

## 3.11.36
 - Propagating release/3.10 to release/3.11 (3.10.367-370)
   - Bug fix with slicer where it sliced isLastBlockOfLastStep [GLBE-6994](https://dotdash.atlassian.net/browse/GLBE-6994)
   - Updating globe core version for sonarqube fixes. [GLBE-6940](https://dotdash.atlassian.net/browse/GLBE-6940)
   - Added in dynamic-inline support and edge case resolutions of MediaGrid when the ad units parameters are malformed yet also specified at the same time. [GLBE-6975](https://dotdash.atlassian.net/browse/GLBE-6975)

## 3.11.35
 - Add support for how to template schema. [TS-3879](https://dotdash.atlassian.net/browse/TS-3879)

## 3.11.34
 - Fix nutrition check in recipe schema [TS-4413](https://dotdash.atlassian.net/browse/TS-4413)

## 3.11.33
- Propagating release/3.10 to release/3.11 (3.10.361-366)
 - Revert SVG xlink:href to support IOS Safari Mobile/Tablet for <= 12.1. No Ticket.

## 3.11.32
- Removing includeSummaries from model in schema.xml since it is default to true and is breaking schema on reviewsc on finance. [FIN-1292](https://dotdash.atlassian.net/browse/FIN-1292)

## 3.11.31
 - Add support for structured ingredient nutrition schema [TS-4413](https://dotdash.atlassian.net/browse/TS-4413)

## 3.11.30
- Propagating release/3.10 to release/3.11 (3.10.361-365)
  - Revert some Globe cases due to observed bugs caught in QA. No Ticket.
    - Revert SVG Polyfill (due to SVG bug) [GLBE-6529](https://dotdash.atlassian.net/browse/GLBE-6529)
    - Revert to old Globe version with TemplateComponent Loader improvements [GLBE-6911](https://dotdash.atlassian.net/browse/GLBE-6911)
  - Update Credit Card Schema [PRM-308](https://dotdash.atlassian.net/browse/PRM-308)
  - Allow null for Credit Card attribute values [PRM-306](https://dotdash.atlassian.net/browse/PRM-306)
  - Updating globe core version for TemplateComponentLoader improvements [GLBE-6911](https://dotdash.atlassian.net/browse/GLBE-6911)
  - Updated globe-core version which includes upgrade for commons collections4 to resolve dependency for usage of latest OpenCSV in Travel [TRIP-1888](https://dotdash.atlassian.net/browse/TRIP-1888)

## 3.11.29 (SKIP)
- Propagating release/3.10 to release/3.11 (3.10.360)
 - Updated RTB MediaGrid Plugin to use their provided library [GLBE-6857](https://dotdash.atlassian.net/browse/GLBE-6857)
 - Updated MediaGrid Plugin to pass our first party data. Currently Page Count/Session Depth, Tax0-3. [GLBE-6858](https://dotdash.atlassian.net/browse/GLBE-6858)
 - Updated MediaGrid Plugin key value pairs passed to Google Ad Manager/GPT [GLBE-6856](https://dotdash.atlassian.net/browse/GLBE-6856)

## 3.11.28 (SKIP)
 - Propagating release/3.10 to release/3.11 (3.10.359)
  - Reverting taxonomy composite cache changes from GLBE-6852 [GLBE-6950](https://dotdash.atlassian.net/browse/GLBE-6950)
  - Added click tracking to mntl-sc-block-callouts [CMRC-600](https://dotdash.atlassian.net/browse/CMRC-600)
  - Introduced autoPause into JWPlayer for new autoplay requirements [GLBE-6895](https://dotdash.atlassian.net/browse/GLBE-6895)
  - Vertical related testing/automation refer to [GLBE-6901](https://dotdash.atlassian.net/browse/GLBE-6901)

## 3.11.27 (SKIP)
 - Propagating release/3.10 to release/3.11 (3.10.356)
   - Removing component property and ignoring unit tests for disabling of skimlinks [GLBE-6932](https://dotdash.atlassian.net/browse/GLBE-6932)

## 3.11.26 (SKIP)
 - Propagating release/3.10 to release/3.11 (3.10.348-355)
  - Default null query param values in external component service CMRC-623
  - Updated Venus with exposed Component methods and expose additonal MntlComponent method DV-189
  - Adding per-DocumentTaxeneComposite cache to reduce duplicated objects in cache GLBE-6852
  - Add overloads for MntlUrl to allow any params FIN-1212
  - Adding CREDITCARDROUNDUP view type. HN-8524
  - Adding STANDALONETOOL view type. HN-8525
  - Add customScore field on CreditCardEx model (No ticket)
  - Allow for SVGs to load when used in a deferred component for IE11. GLBE-6529
  - Vertical related automation refer to [GLBE-6925] (https://dotdash.atlassian.net/browse/GLBE-6525)
  - Add methodology and editorial disclaimer to BaseDocumentEx FIN-956
  - Fixed sonarqube maven failures (no ticket)

## 3.11.25
 - Removed thumbor proxy servlet logging for local envs (No ticket)

## 3.11.24
 - Fix NPE bug in RSS feed when dimensions are not set on a IMAGE SC [BEAUT-1061](https://dotdash.atlassian.net/browse/BEAUT-1061)

## 3.11.23
 - Fix bug introduced in last merge up from release/3.10 [NO TICKET]

## 3.11.22 [SKIP]
 - Propagating release/3.10 to release/3.11 (3.10.342-347)
   - Adding common mantle test class for GTM checks. [FIN-1106](https://dotdash.atlassian.net/browse/FIN-1106)
   - Disabling affiliate disclaimer and skimlinks when document affiliate is set to false [GLBE-6731](https://dotdash.atlassian.net/browse/GLBE-6731)
   - Vertical related automation refer to [GLBE-6900] (https://dotdash.atlassian.net/browse/GLBE-6900)
   - Add attribute name into CreditCardAttributeView [PRM-274] (https://dotdash.atlassian.net/browse/PRM-274)
   - Adding ad table widgets to Auction Floor pricing blacklist. [GLBE-6903](https://dotdash.atlassian.net/browse/GLBE-6903)
   - Changing image placeholder background property to background-color so no overwriting other background properties [HTLH-5220] (https://dotdash.atlassian.net/browse/HLTH-5220)
   - DR: Server starts even when Proctor is down. [GLBE-6719](https://dotdash.atlassian.net/browse/GLBE-6719)

## 3.11.21
 - Update Materials and Tools fields for HowTo document [TS-4107](https://dotdash.atlassian.net/browse/TS-4107).

## 3.11.20
 - Add structured ingredient support for recipesc [TS-4405](https://dotdash.atlassian.net/browse/TS-4405)

## 3.11.19
 - Fix slicing issue for legacy templates [GLBE-6905](https://dotdash.atlassian.net/browse/GLBE-6905)

## 3.11.18
 - Propagating release/3.10 to release/3.11 (3.10.339-341)
   - Add taxonomy pages and update for unified schema usage. [GLBE-6538](https://dotdash.atlassian.net/browse/GLBE-6538)
     - Vertical Related automation can refer to [GLBE-6881] (https://dotdash.atlassian.net/browse/GLBE-6881)
   - Reverting house ads as it needs effort from vertical teams to make few changes to the test. Will add it as breaking change[DV-184](https://dotdash.atlassian.net/browse/DV-184)
   - Adding "no-transform" to Cache-Control header on all responses (static resources, images from thumbor and document requests) [GLBE-6763](https://dotdash.atlassian.net/browse/GLBE-6763)

## 3.11.17
 - Propagating release/3.10 to release/3.11 (3.10.334-338)
   - Rename device to deviceCategory for selene auction floor service call. No ticket.
   - Updated .gitignore to ignore Yaku static folder. No ticket.
   - Pass operating system, device category, and tier to auction floor service call [GLBE-6870](https://dotdash.atlassian.net/browse/GLBE-6870)
   - Add "AWARDS" viewType for TripSavvy 2019 awards structured content / taxonomy docs [TRIP-1867](https://dotdash.atlassian.net/browse/TRIP-1867)
   - Collapse iframe caption silently when optional caption textfield does not exist [GLBE-6902](https://dotdash.atlassian.net/browse/GLBE-6902)

## 3.11.16
- Propagating release/3.10 to release/3.11 (3.10.331-333)
 - IPONWEB Plugin into RTB [GLBE-6732](https://dotdash.atlassian.net/browse/GLBE-6738)
 - Added Yaku for Promise Polyfills in IE11 [GLBE-6751](https://dotdash.atlassian.net/browse/GLBE-6751)
 - Use ListSc item quote for amazon rss product summary [CMRC-598](https://dotdash.atlassian.net/browse/CMRC-598)
 - Added support for in house ads[DV-184](https://dotdash.atlassian.net/browse/DV-184)

## 3.11.15
- Propagating release/3.10 to release/3.11 (3.10.322-330)
 - Added recircDocsIdFooter variable to mntlDataLayerTests for datalayer tests [HLTH-5267](https://dotdash.atlassian.net/browse/HLTH-5267)
 - Update MantleThreadPoolExecutor to extend GlobeThreadPoolExecutor to set MDC on child threads automatically [GLBE-6849](https://dotdash.atlassian.net/browse/GLBE-6849)
 - Fixing 'external' property on mntl-text-link [GLBE-6682](https://dotdash.atlassian.net/browse/GLBE-6682)
 - Removing numImages/numInlineLinks from MantleGTM and associated tests [GLBE-6818](https://dotdash.atlassian.net/browse/GLBE-6818)
 - For vertical related automation refer to [GLBE-6854] (https://dotdash.atlassian.net/browse/GLBE-6854)
 - Fix loadAbsoluteUrl method - shouldn't require venusVisualTestTargetUrl (would have failed on venus-test by default) [FIN-1036](https://dotdash.atlassian.net/browse/FIN-1036)
 - Added gtm page view tasks that also accept a list of DocumentTaxeneComposite as an argument [HLTH-5267](https://dotdash.atlassian.net/browse/HLTH-5267)
 - Move advertisement text collapse css into mantle [GLBE-6704](https://dotdash.atlassian.net/browse/GLBE-6704)
 - Add instagram and youtube to list of embed providers [GLBE-6717](https://dotdash.atlassian.net/browse/GLBE-6717)
 - Automation for instagram and you tube embed block [GLBE-6812] (https://dotdash.atlassian.net/browse/GLBE-6812)
 - Vertical related automation can refer to [GLBE-6812] (https://dotdash.atlassian.net/browse/GLBE-6812)

## 3.11.14
- Propagating release/3.10 to release/3.11 (3.10.321-322)
  - Fix loadAbsoluteUrl method for visual test. Deprecated loadAbsoluteUrl and added loadVisualBaseUrl [DV-180](https://dotdash.atlassian.net/browse/DV-180)

## 3.11.13
- Propagating release/3.10 to release/3.11 (3.10.318-321)
  - Legacy url support for external services [CMRC-580](https://dotdash.atlassian.net/browse/CMRC-580)
  - Fixing AdCallObject method - wasn't handling first param properly [DV-178](https://dotdash.atlassian.net/browse/DV-178)
  - implement custom `repeat` functionality for jw-player to solve flash to black frame in between loops [BEAUT-953](https://dotdash.atlassian.net/browse/BEAUT-953)
  - updating venus version to 1.3.17 [DV-176](https://dotdash.atlassian.net/browse/DV-176)

## 3.11.12
- Cherry-picked 3.10.308 - Update StructuredContentDocumentProcessor to input "span" elements with the same class used in our ad placement divs, instead of actually slicing the HTML. [GLBE-6658](https://dotdash.atlassian.net/browse/GLBE-6658)

## 3.11.11
 - Consolidate common RSS code into Mantle [BEAUT-579](https://iacpublishing.atlassian.net/browse/BEAUT-579)

## 3.11.10
- Propagating release/3.10 to release/3.11 (3.10.304-317)
 - Backing out 3.10.308 due to GLBE-6845 [GLBE-6846](https://dotdash.atlassian.net/browse/GLBE-6844)
 - Fix bug where blocks are being casted as htmlblock [GLBE-6844](https://dotdash.atlassian.net/browse/GLBE-6844)
 - Adding AuctionFloorService for selene auction pricing endpoints [GLBE-6733](https://dotdash.atlassian.net/browse/GLBE-6733)
 - Adding AuctionFloorInfo to GptAd model and pass floor and floor_id in ad-call params [GLBE-6734](https://dotdash.atlassian.net/browse/GLBE-6734)
 - For automation refer to [GLBE-6827](https://dotdash.atlassian.net/browse/GLBE-6827)
 - Cleaned up error handling around bovd resources. Server startup now fails if bovd service cannot be created. [GLBE-6831](https://dotdash.atlassian.net/browse/GLBE-6831)
 - Added mantle namespace to proctor rule [HLTH-5226](https://dotdash.atlassian.net/browse/HLTH-5226)
 - Fix for AdcallEntry method. Wasn't handling empty params properly. [DV-175](https://dotdash.atlassian.net/browse/DV-175)
 - Fixing Mantle Schema Null pointer exception. No ticket
 - Exposed taxene config map to Proctor rule [HLTH-5226](https://dotdash.atlassian.net/browse/HLTH-5226)
 - extending off MtnlVenusTest instead of directly VenusTest. Required to leverage isProd inside tests. [BEAUT-541](https://dotdash.atlassian.net/browse/BEAUT-541)
 - Update StructuredContentDocumentProcessor to input "span" elements with the same class used in our ad placement divs, instead of actually slicing the HTML. [GLBE-6658](https://dotdash.atlassian.net/browse/GLBE-6658)
 - Update globe-core version for resolveTemplate performance improvements [GLBE-6743](https://dotdash.atlassian.net/browse/GLBE-6743)
 - Update Mantle publishing schema tests. No ticket.
 - Adding ability to ignore assertions in AccessLog test. [DV-172](https://dotdash.atlassian.net/browse/DV-172)

## 3.11.9
- Vertical Related automation RTB tests will use getLocation instead of ip
- Propagating release/3.10 to release/3.11 (3.10.285-303)
 - Adding in XPath selector for mantle components [DV-173](https://dotdash.atlassian.net/browse/DV-173)
 - Fix proxied cookies so version is not appended to value [CMRC-562](https://dotdash.atlassian.net/browse/CMRC-562)
 - Add null check to next block in StructuredContentDocumentProcessor processNestedBlocks [TS-4304](https://dotdash.atlassian.net/browse/TS-4304)
 - Removing Pickadate JS while cleaning up bower. [GLBE-5679](https://dotdash.atlassian.net/browse/GLBE-5679)
 - Expose display category instead of category for credit card categoryScore [PRM-202](https://dotdash.atlassian.net/browse/PRM-202)
 - Add option for vertical customization of placeholder image for lazy loading [WEDD-248](https://dotdash.atlassian.net/browse/WEDD-248)
 - Removing Brightcove GA JS while cleaning up bower. [GLBE-5679](https://dotdash.atlassian.net/browse/GLBE-5679)
 - Fix AccessLog tests . [DV-172](https://dotdash.atlassian.net/browse/DV-172)
 - Move credit card specific services, task, action, models out of mantle [PRM-180](https://dotdash.atlassian.net/browse/PRM-180)
 - Add ImageTask [PRM-180](https://dotdash.atlassian.net/browse/PRM-180)
 - Used custom events from embeds to trigger sc-ads right rail recalculations to properly fix the number of ads in the rail. [GLBE-6740](https://dotdash.atlassian.net/browse/GLBE-6740)
 - Include author bio page url in sameAs array for bio pages. [GLBE-6744](https://dotdash.atlassian.net/browse/GLBE-6744)
 - Fix sponsored content proxy failure [GLBE-6754](https://dotdash.atlassian.net/browse/GLBE-6754)
 - Refactor Step Processing Code to add Support for lastBlockOfLastStep and any type of Structure Content Block being a steps block in support of HowTo Template [TS-3880](https://dotdash.atlassian.net/browse/TS-3880)
 - Lazy load TrustE badge [HLTH-5231](https://dotdash.atlassian.net/browse/HLTH-5231)
 - Allow templated runner with MntlJsErrorTest [FIN-896](https://dotdash.atlassian.net/browse/FIN-896)
 - Update event tracking key/values for inline citations [HLTH-5197](https://dotdash.atlassian.net/browse/HLTH-5197)
 - Use Predicate wait method instead of sleep for RtbDataLayerTests. [FIN-878](https://dotdash.atlassian.net/browse/FIN-878)
 - Resolve sonarqube blocking issues [GLBE-6583](https://dotdash.atlassian.net/browse/GLBE-6583)
 - Fix Fake Ads in preview covering document header [GLBE-6730](https://dotdash.atlassian.net/browse/GLBE-6730)

## 3.11.8
 - Fix merge up from 3.10 which made Bing Ad and Location services have required api keys [CMRC-585](https://dotdash.atlassian.net/browse/CMRC-585)

## 3.11.7
- Propagating release/3.10 to release/3.11 (3.10.251-284)
 - Fixing sponsorship component pattern library image [GLBE-6620](https://dotdash.atlassian.net/browse/GLBE-6620)
 - Updating mntl-accordion to use unique component IDs for included components [GLBE-6553](https://dotdash.atlassian.net/browse/GLBE-6553)
 - Allow the first ad to be lazy loaded by SC ads instead of required. (GLBE-6657)[https://dotdash.atlassian.net/browse/GLBE-6657]
 - Bumping version to clean up Changelog.md paths to the old JIRA. No ticket. Speak to Daniel Phan about this.
 - Take the Automation/customAds repo code for Visual Tests and move to mantle [FIN-272](https://dotdash.atlassian.net/browse/FIN-272)
 - Changing max age on Strict-Transport-Security header to 6 months (15552000 seconds) [GLBE-6679](https://dotdash.atlassian.net/browse/GLBE-6679)
 - Removing author from schema when authorName is "Staff Author" [GLBE-6402](https://dotdash.atlassian.net/browse/GLBE-6402)
 - For vertical related automation will see removed author details from taxonomy pages
 - Fix supefluous credit card rating value on schema to be as it is specified [PRM-164](https://dotdash.atlassian.net/browse/PRM-164)
 - Updated getBestIntroImage to fallback to summary/primary image if primary tag does not exists in the image map when Proteus is enabled [HLTH-5089](https://dotdash.atlassian.net/browse/HLTH-5089)
 - Update globe-core version for security patch performance mitigation [GLBE-6626](https://dotdash.atlassian.net/browse/GLBE-6626)
 - Create prod environment check for url. [GLBE-6716](https://dotdash.atlassian.net/browse/GLBE-6716)
 - Added null check to getResolvedImgUrl() in SocialTask [HLTH-5189](https://dotdash.atlassian.net/browse/HLTH-5189)
 - Add TRUSTe Badge [HLTH-4935](https://dotdash.atlassian.net/browse/HLTH-4935)
 - Adding code to support additional data attribute that allows an offset for button ads at the bottom of the rail. [FIN-54](https://dotdash.atlassian.net/browse/FIN-54)
 - Add custom=1 key-val to ad call page targeting for sponsored documents [GLBE-6640](https://dotdash.atlassian.net/browse/GLBE-6640)
 - For vertical related automation refer to [GLBE-6650](https://dotdash.atlassian.net/browse/GLBE-6650)
 - Add `shortBio` field to `GuestAuthorEx` [GLBE-6641](https://dotdash.atlassian.net/browse/GLBE-6641)
 - Reworked unified schemas slightly to handle guest authors a bit more seamlessly
 - Added template details for beauty access log automation. [GLBE-6703](https://dotdash.atlassian.net/browse/GLBE-6703)
 - Add promoDescription to BaseDocumentEx [WEDD-26](https://dotdash.atlassian.net/browse/WEDD-26)
 - Add How To Document into Mantle. [TS-3878](https://dotdash.atlassian.net/browse/TS-3878)
 - Add EMBED structured content block [GLBE-6579](https://dotdash.atlassian.net/browse/GLBE-6579)
 - Add embed service [GLBE-6577](https://dotdash.atlassian.net/browse/GLBE-6577)
 - Add support for custom configuration [GLBE-6598](https://dotdash.atlassian.net/browse/GLBE-6598)
 - Vertical related automation can refer to [GLBE-6616] (https://dotdash.atlassian.net/browse/GLBE-6616)
 - Adding class to supply resources from git repository. Updating ads.txt to retrieve content from git. [GLBE-6517](https://dotdash.atlassian.net/browse/GLBE-6517)
 - Added template details for beauty access log automation. [GLBE-6695](https://dotdash.atlassian.net/browse/GLBE-66959)
 - Review Rating should fallback to entity score if missing on schema. [PRM-19](https://dotdash.atlassian.net/browse/PRM-19)
 - Fix NoSuchMethodException from filling logs. [FIN-812](https://dotdash.atlassian.net/browse/FIN-812)
 - Credit Card Schema. [PRM-19](https://dotdash.atlassian.net/browse/PRM-19)
 - Failed build
 - Make iframe embed widget instruction text configurable and toggle button hidden before load. [TS-4190](https://dotdash.atlassian.net/browse/TS-4190)
 - Changelog tweak [NO TICKET]
 - Add Credit Card Services for search operation [PRM-117](https://dotdash.atlassian.net/browse/PRM-117)
- Pass `Mntl` to masonryList IIFE to avoid javascript undefined error [TS-4254](https://dotdash.atlassian.net/browse/TS-4254)
 - Added `Rollaway` component into `components/widgets` [CMRC-394](https://dotdash.atlassian.net/browse/CMRC-394)
- Create tool embed component and scripts. [TS-4160] https://dotdash.atlassian.net/browse/TS-4160
- Update Mantle affiliate disclosure test methods [CMRC-391] https://dotdash.atlassian.net/browse/CMRC-391
- Add Mantle affiliate disclosure test methods [CMRC-391] https://dotdash.atlassian.net/browse/CMRC-391

## 3.11.6
- Propagating release/3.10 to release/3.11 (3.10.186-250)
 - Add support for STEPBYSTEP viewType in BaseDocumentEx [WEDD-36](https://dotdash.atlassian.net/browse/WEDD-36)
 - Changed Inline Citation Sources header to be a link for click tracking [GLBE-6601](https://dotdash.atlassian.net/browse/GLBE-6601)
 - Update globe core version to update hippodrome to support cached Thumbor url hash Macs [GLBE-6555](https://dotdash.atlassian.net/browse/GLBE-6555)
 - Add optional field for external components [CMRC-466](https://dotdash.atlassian.net/browse/CMRC-466)
 - Pass `window.Mntl` to IIFE of tooltip to avoid a javascript error [TS-4176](https://dotdash.atlassian.net/browse/TS-4176)
 - Had to revert some changes made to AdCallParams [FIN-744](https://dotdash.atlassian.net/browse/FIN-744)
 - Refactored sub-components of `mntl-sponsorship` component to allow them to be ref'ed for a more flexible sponsorship layout [LW-2109](https://dotdash.atlassian.net/browse/LW-2109)
 - Update credit card backend models [PRM-113](https://dotdash.atlassian.net/browse/PRM-113)
 - Fix bug where CachedArticleService was constantly missing for in-memory cache [GLBE-6625](https://dotdash.atlassian.net/browse/GLBE-6625)
 - Add vendor name mappings to VendorLookupService [CMRC-378](https://dotdash.atlassian.net/browse/CMRC-378)
 - Update globe core version to update hippodrome to support weddings URLs [WEDD-15](https://dotdash.atlassian.net/browse/WEDD-15)
 - fix for testAdCallParams method - scp param and cust params aren't required. Had to remove check for video templates. [FIN-725](https://dotdash.atlassian.net/browse/FIN-725)
 - Added support to mock ad slots without completing the ad calls. kw=fakeAds will generate these fake ads. [GLBE-6401](https://dotdash.atlassian.net/browse/GLBE-6401)
 - MntlCommonTestMethods had new check for cust slot that was exceptioning, fix for that. [FIN-715](https://dotdash.atlassian.net/browse/FIN-715)
 - Cache flatten creditcard attribute list and its weight list [PRM-92](https://dotdash.atlassian.net/browse/PRM-92)
 - Cleaning up the UrlParams class. Too many instances where cust and scp shared same param. Protect against null pointer exception when fetching values. [DV-160](https://dotdash.atlassian.net/browse/DV-160)
 - Add range facet support to faceted search and deion search service [TS-3923](https://dotdash.atlassian.net/browse/TS-3923)
 - Collapse sources component when no citations and sources are present [HLTH-5112](https://dotdash.atlassian.net/browse/HLTH-5112)
 - Add MARKETING and BLANK viewType enums [HLTH-5083](https://dotdash.atlassian.net/browse/HLTH-5083)
 - Add WEDDINGS enum [WEDD-11](https://dotdash.atlassian.net/browse/WEDD-11)
 - Adding slot custom param for video ads. Value = video. [GLBE-6588](https://dotdash.atlassian.net/browse/GLBE-6588)
 - Automation for Adding slot custom param for video ads. Value = video. Vertical automation can refer to mantle change, mantle ref test
 - Fixed issue where pattern library navigation would extend too far below the copyright block, cutting off the end of the nav (no ticket)
 - Fixed MntlAditionalCitationSource venus model for health test. No Ticket
 - Fix deserialization of credit card as an entity on reviewsc documents (Involves refactoring of ReviewEntityEx models to match with Selene's BaseProduct models) [PRM-95](https://dotdash.atlassian.net/browse/PRM-95)
 - Add Notification Service [CMRC-292](https://dotdash.atlassian.net/browse/CMRC-292)
 - Update `startsWith` JS method with `indexOf` - IE11 Support [HLTH-5099](https://dotdash.atlassian.net/browse/HLTH-5099)
 - update to adcallexpected classes to allow for modification from inherited. [FIN-673](https://dotdash.atlassian.net/browse/FIN-673)
 - Add backend models for CreditCard [PRM-54](https://dotdash.atlassian.net/browse/PRM-54)
  - Flatten attributelist and weightslist of creditcard selene response [PRM-85](https://dotdash.atlassian.net/browse/PRM-85)
 - As part of running mantle ref regression on latest mantle version fixed failure with inline citation tracking automated test, verticals using inline citation tracking automation can refer to test [GLBE-6437](https://dotdash.atlassian.net/browse/GLBE-6437)
 - Fix various inline-citations and tooltip bugs [HLTH-5099, HLTH-5101] (https://dotdash.atlassian.net/browse/HLTH-5099, https://dotdash.atlassian.net/browse/HLTH-5101)
 - Adding slot field for ad param check. [FIN-673](https://dotdash.atlassian.net/browse/FIN-673)
 - Update dynamic-inline slot targeting value to remove the ordinal number [GLBE-6590](https://dotdash.atlassian.net/browse/GLBE-6590)
 - Stop escaping html where it's not necessary [GLBE-6562](https://dotdash.atlassian.net/browse/GLBE-6562)
   - Moved `unescapeHtml` to globe-core
 - Update Anchor Tags for TOC component. [GLBE-6463](https://dotdash.atlassian.net/browse/GLBE-6463)
 - updated the citation model return the component as part of the method. [HLTH-4974](https://dotdash.atlassian.net/browse/HLTH-4974)
 - Add slot key value pair for GPT slot targeting values. [GLBE-6559](https://dotdash.atlassian.net/browse/GLBE-6559)
  - For vertical automation can refer to mantle ref test
 - Automate Author Schema with People. Automate new Bio Schema to unified schema. [GLBE-6540](https://dotdash.atlassian.net/browse/GLBE-6540)
 - Exclude affiliate disclosure component on commerce documents (no ticket)
 - Adding Error collector for missing params in urlParams method  [FIN-647](https://dotdash.atlassian.net/browse/FIN-647)
 - Update Author Schema with People. Added in new Bio Schema to unified schema. [GLBE-6408](https://dotdash.atlassian.net/browse/GLBE-6408)
  - Vertical related automation can refer to [GLBE-6540]
  - Add affiliate disclosure component [CMRC-263](https://dotdash.atlassian.net/browse/CMRC-263)
 - Added blocked bidder details to MntlTaxRtbTest, for automation verticals using blocked bidders for RTB can refer to this test  [GLBE-6554](https://dotdash.atlassian.net/browse/GLBE-6554)
 - Add support for specifying `relatedArticles` algorithm [GLBE-6516](https://dotdash.atlassian.net/browse/GLBE-6516)
  - The existance of `relatedArticlesAlgorithm` proctor test will enable A/B testing the algorithm anywhere the `relatedArticles` task method is used (e.g. recirc)
     https://proctor-lifestyle.prod.aws.thespruce.com/proctor/definition/relatedArticlesAlgorithm?branch=trunk
 - Update globe-core version for caching of Filterable hash codes for performance improvements [GLBE-6546](https://dotdash.atlassian.net/browse/GLBE-6546)
 - some cleanup methods for MntlTabRtbTest. After using it we wanted some cleanup done [FIN-620](https://dotdash.atlassian.net/browse/FIN-620)
 - Enhance inline-citations and tooltips positioning and click logic. [HLTH-4974](https://dotdash.atlassian.net/browse/HLTH-4974)
 - Automation for blocked bidders for RTB. For automation verticals can refer to GLBE-6554 [GLBE-6548](https://dotdash.atlassian.net/browse/GLBE-6548)
 - Clean up non-interaction events in Data layer pushes. [GLBE-6506](https://dotdash.atlassian.net/browse/GLBE-6506)
 - Fixed a null pointer error for getUrlEnv methods [DV-155](https://dotdash.atlassian.net/browse/DV-155)
 - Adding in Blocked Bidders for Amazon in RTB [GLBE-6501](https://dotdash.atlassian.net/browse/GLBE-6501)
 - Added null checks for chop ftl. [GLBE-6522](https://dotdash.atlassian.net/browse/GLBE-6522)
 - updated venus version for enabling conditional skip of test [DV-153](https://dotdash.atlassian.net/browse/DV-153)
 - Added TermSC template type in TOC's javatask [FIN-585](https://dotdash.atlassian.net/browse/FIN-585)
 - Add MntlTaxRtbTest for use with all verticals [FIX-583](https://dotdash.atlassian.net/browse/FIN-583)
 - Update recipeCategory schema to include dish grouping [TS-4121](https://dotdash.atlassian.net/browse/TS-4121)
 - Update recipe schema to include 0 as a valid cook, prep, and total time [TS-4119](https://dotdash.atlassian.net/browse/TS-4119)
 - Separate RTB tax params from URL params, fix custom param for ads tests [GLBE-6542](https://dotdash.atlassian.net/browse/GLBE-6542)
 - Update globe-core version to get changes for removing jetty server version response header [GLBE-6534](https://dotdash.atlassian.net/browse/GLBE-6534)
 - Check size of ancestor nodes in sailthruTask to avoid indexOutOfBound on empty array of nodes [TS-4133](https://dotdash.atlassian.net/browse/TS-4133)
 - Changing max age on Strict-Transport-Security header to 1 week (604800 seconds) [GLBE-6537](https://dotdash.atlassian.net/browse/GLBE-6537)
 - Adding id mntl-ad-table__disclosure-content to disclaimer content on mntl-ad-table component [FIN-459](https://dotdash.atlassian.net/browse/FIN-459)
 - Integrate sonarqube with base configuration [GLBE-6493](https://dotdash.atlassian.net/browse/GLBE-6493)
 - Add support for EXCLUSIVE viewType in BaseDocumentEx [BEAUT-622](https://dotdash.atlassian.net/browse/BEAUT-622)
 - Fix for 3.10.186 - should have been added to MntlPage not MntlBasePage [FIN-542](https://dotdash.atlassian.net/browse/FIN-542)
 - Adding component methods to MntlPage [FIN-542](https://dotdash.atlassian.net/browse/FIN-542)

## 3.11.5
- Propagating release/3.10 to release/3.11 (3.10.173-185)
 - Update for Venus to support Java 11 with BizCategoryListener [DV-151](https://dotdash.atlassian.net/browse/DV-151)
 - Adding Displayed date and replacing usages of LastPublished date [GLBE-6471](https://dotdash.atlassian.net/browse/GLBE-6471)
 - Automation for RTB values [GLBE-6520](https://dotdash.atlassian.net/browse/GLBE-6520)
 - Update sailthru.tags to use primaryParent taxonomy slugs [GLBE-6468](https://dotdash.atlassian.net/browse/GLBE-6468)
 - Passing Taxonomy (Breadcrumb) values to Amazon Header Bidding [GLBE-6499](https://dotdash.atlassian.net/browse/GLBE-6499)
 - Propagating up release/3.9 from (3.9.329)
   - Update ascsubtag to use shortened requestId [CMRC-47](https://dotdash.atlassian.net/browse/CMRC-47)
 - Minor fixes to publisher schema block [GLBE-6519](https://dotdash.atlassian.net/browse/GLBE-6519)
 - Automation for inline citations engagement tracking [GLBE-6502](https://dotdash.atlassian.net/browse/GLBE-6502)
 - Add "Custom Series" (stamp tags) field to DFP [GLBE-6461](https://dotdash.atlassian.net/browse/GLBE-6461)
 - Add automation for publisher schema validation [LW-2299](https://dotdash.atlassian.net/browse/LW-2299)
 - Update globe-core version to get hippodrom changes for url data factory setup for external services [PRM-15](https://dotdash.atlassian.net/browse/PRM-15)
 - Inline citations [GLBE-6426](https://dotdash.atlassian.net/browse/GLBE-6426)
   - Add support for dynamic tooltip
   - Add base component for expandable block
 - Made quiz answer text configurable [REF-1936](https://dotdash.atlassian.net/browse/REF-1936)

## 3.11.4
- Propagating release/3.10 to release/3.11 (3.10.131-172)
 - Added Sponsor to Summary model [TS-3943](https://dotdash.atlassian.net/browse/TS-3943)
 - Don't rely on sponsorship.id in order to display component [TS-3859](https://dotdash.atlassian.net/browse/TS-3859)
 - Updating venus core version to the latest. No Ticket.
 - Propagating up release/3.9 from (3.9.325 - 328)
   - Updated Jquery to 3.4.0 [GLBE-6473](https://dotdash.atlassian.net/browse/GLBE-6473)
   - Added beauty verticals in old Selene API to support backward compatibility for LiveAbout migration [BEAUT-568](https://dotdash.atlassian.net/browse/BEAUT-568)
   - Quick fix for unescaped ampersands to not take down a server (no ticket, see https://dotdash.slack.com/archives/CFDEY09PB/p1553703170057200)
   - Fixed pattern library example for ad tables to reflect actual usage [FIN-181](https://dotdash.atlassian.net/browse/FIN-181)
 - Moved Organization schema inside document schema (webpage). [GLBE-6483](https://dotdash.atlassian.net/browse/GLBE-6483)
 - Added null check for document object in AbstractJourneyNode's toString method [HLTH-4948](https://dotdash.atlassian.net/browse/HLTH-4948)
 - Making getSeleneVertical method protected (no ticket)
 - Fix skimlinks redis deserialization (No ticket)
 - Integrate with Skimlinks Pricing Api [CMRC-110](https://dotdash.atlassian.net/browse/CMRC-110)
 - Wrap skimlinks.evaluated.js in an IIFE and pass in `Mntl` explicitly an an alternative solution to issue addressed in 3.10.161 [BEAUT-278](https://dotdash.atlassian.net/browse/BEAUT-278)
 - Add role="none" to height-change-listener iframe to hide from screen readers [LW-2286](https://dotdash.atlassian.net/browse/LW-2286)
 - Add `Mntl` variable declaration in skimlinks.evaluated.js to prevent errors when executed before global `Mntl` is defined [BEAUT-278](https://dotdash.atlassian.net/browse/BEAUT-278)
 - Add `title` attibute for iframe SC block [REF-1706](https://dotdash.atlassian.net/browse/REF-1706)
 - Adding MntlBingAfsAdsTest [FIN-407](https://dotdash.atlassian.net/browse/FIN-407)
 - Added Automation for Article feedback component [HLTH-4923](https://dotdash.atlassian.net/browse/HLTH-4923)
 - Added new fields for publishing schema and created new organization schema block. [GLBE-6407](https://dotdash.atlassian.net/browse/GLBE-6407)
 - Pulling legacy url handling code in mantle [GLBE-6360](https://dotdash.atlassian.net/browse/GLBE-6360)
 - Video Player expression check fix for non video template pages. [GLBE-6456](https://dotdash.atlassian.net/browse/GLBE-6456)
 - Add Faceted Search Task and supporting classes
 	-[TS-3704](https://dotdash.atlassian.net/browse/TS-3704)
 	-[TS-3705](https://dotdash.atlassian.net/browse/TS-3705)
 	-[TS-3707](https://dotdash.atlassian.net/browse/TS-3707)
 	-[TS-3932](https://dotdash.atlassian.net/browse/TS-3932)
 - Parallelize the loading of external component resources [CMRC-160](https://dotdash.atlassian.net/browse/CMRC-160)
 - Replace `String.split` with `StringUtils.split` [GLBE-6382](https://dotdash.atlassian.net/browse/GLBE-6382)
 - Changes for getting verticals running Java11 VM (Amazon Correto JVM) [GLBE-6387](https://dotdash.atlassian.net/browse/GLBE-6387)
 - Adding Fields and Flags for Grouping for Spruce faceted search [TS-3822](https://dotdash.atlassian.net/browse/TS-3822)
 - Adding MntlAdTableComponent model [FIN-232](https://dotdash.atlassian.net/browse/FIN-232)
 - Fix for MntlSchemaCompareTest where schema sizes fail to match; test should stop but would continue can give index out of bound error and hide issue. [FIN-313](https://dotdash.atlassian.net/browse/FIN-313)
 - Introduce lazy loading via lazy sizes and dataset-bg for the thumbnail. [GLBE-6395](https://dotdash.atlassian.net/browse/GLBE-6395)
 - Cleaned up resource loading pattern in ratings.xml [GLBE-6322](https://dotdash.atlassian.net/browse/GLBE-6322)
 - fix testAnalyticsEvent to be easier to debug as well as use collector instead of just throwing Timeout Exception. [FIN-317](https://dotdash.atlassian.net/browse/FIN-317)
 - Journey cache optimizations [GLBE-6364](https://dotdash.atlassian.net/browse/GLBE-6364)
 - Move taxeneRelationParentsPrimaryAndSecondary task intro mantle [CMRC-182](https://dotdash.atlassian.net/browse/CMRC-182)
 - Add accept header to proxied headers [CMRC-228](https://dotdash.atlassian.net/browse/CMRC-228)
 - Rolled back Venus update due to unexpected breaking change (no ticket).
 - Add productTitle to review entity data [CMRC-170](https://dotdash.atlassian.net/browse/CMRC-170)
 - Propagating release/3.9 to release/3.10 (3.9.323-324)
   - Update ads.txt [GLBE-6400](https://dotdash.atlassian.net/browse/GLBE-6400)
   - Update Selene version to get support for RELIGION vertical [REF-1769](https://dotdash.atlassian.net/browse/REF-1769)
 - Updated JWPlayer Key [GLBE-6389](https://dotdash.atlassian.net/browse/GLBE-6389)
- Fixing log level when kafka consumer creation is failed due to broker unavailability (no ticket)
- Replace `getSecureProperty` taking default value with one taking `isRequired` flag [GLBE-6312](https://dotdash.atlassian.net/browse/GLBE-6312)
 - Added null-check in CachedDocumentTaxeneService [BEAUT-416](https://dotdash.atlassian.net/browse/BEAUT-416)
 - Full cache clearance solution [GLBE-6357](https://dotdash.atlassian.net/browse/GLBE-6357)
 - Propagating release/3.9 to release/3.10 (3.9.322)
   - Added mntl-ad-table component to be used to create performance marketing ad tables [FIN-181](https://dotdash.atlassian.net/browse/FIN-181)
- Parse external component service query params so that the request's input stream is preserved [CMRC-101](https://dotdash.atlassian.net/browse/CMRC-101)
- If no document.summary.description, fallback to document.description for bio-schema description [BEAUT-381](https://dotdash.atlassian.net/browse/BEAUT-381)
- Updated MntlSCBlockHTML model from Mantle-ref to be included in Mantle as part of PL cleanup. [GLBE-6331](https://dotdash.atlassian.net/browse/GLBE-6331)

## 3.11.3
 - Propagating release/3.10 to release/3.11 (3.10.128-130)
  - Updated PL value for Accordion Document List (previous value lost its Journey values). [GLBE-6373](https://dotdash.atlassian.net/browse/GLBE-6373)
  - Added in ComparisonListModel to Mantle for PL testing (and inheritable testing for verticals) GLBE-6331(https://dotdash.atlassian.net/browse/GLBE-6331)
  - Changed Quiz results PL entry to markup preview type. [GLBE-6383](https://dotdash.atlassian.net/browse/GLBE-6383)

## 3.11.2
 - Propagating release/3.10 to release/3.11 (3.10.120-127)
   - Updating MntlSchemaCompareTest to show differences when encountered between schemas [FIN-230](https://dotdash.atlassian.net/browse/FIN-230)
   - Updated test Quiz data and fixed quiz results PL entry. [GLBE-6374](https://dotdash.atlassian.net/browse/GLBE-6374)
   - [HackerOne] stop allowing url query param to hijack the request context [GLBE-6367](https://dotdash.atlassian.net/browse/GLBE-6367)
   - Updating mntl-sc-page to use render utils function for resolving block names [FIN-184](https://dotdash.atlassian.net/browse/FIN-184)
   - Updated globe core version **(3.9.22)** to get changes for resolving actions in order to support rendering of legacy urls for Beauty verticals [BEAUT-255](https://dotdash.atlassian.net/browse/BEAUT-255)
   - Fix flamegraph not being generated after first run until server restart
   - Fixing Commerce Container PL. [GLBE-6365](https://dotdash.atlassian.net/browse/GLBE-6365)
   - Extract absolute image url creation for commerce to override [CMRC-178](https://dotdash.atlassian.net/browse/CMRC-178)
   - Fixing SC Inline Video PL. To use a hardcoded template url [GLBE-6369](https://dotdash.atlassian.net/browse/GLBE-6369)

## 3.11.1
 - Propagating release/3.10 to release/3.11 (3.10.94 - 119)
  - Added more verbose JWPlayer error logging to message field [GLBE-6363](https://dotdash.atlassian.net/browse/GLBE-6363)
  - Changes: Handle alternate previewType config in PL [GLBE-6299](https://dotdash.atlassian.net/browse/GLBE-6299)
  - Handle alternate previewType config in PL [GLBE-6299](https://dotdash.atlassian.net/browse/GLBE-6299)
  - Using Proteus SCHEMA images [GLBE-6361](https://dotdash.atlassian.net/browse/GLBE-6361)
  - Added class to `reveal` section of quiz that shows answer type [REF-1752](https://dotdash.atlassian.net/browse/REF-1752)
  - Brought back Task Execution css/js component functionality [GLBE-6349](https://dotdash.atlassian.net/browse/GLBE-6349)
  - Extract csrfToken from newsletter submit ftl (No ticket)
  - JWPlayer Optimizations for Performance [GLBE-6335](https://dotdash.atlassian.net/browse/GLBE-6335)
  - Cache external resources for external component requests [CMRC-130](https://dotdash.atlassian.net/browse/CMRC-130)
  - Updated Unified Schema to Support Review Type "Product". [CMRC-65](https://dotdash.atlassian.net/browse/CMRC-65)
  - Added nutrition label SC block [HLTH-2905](https://dotdash.atlassian.net/browse/HLTH-2905)
  - Updated globe-core version to add legacyDocument flag in VerticalUrlData in order to support migrated legacy content in beauty verticals [BEAUT-94](https://dotdash.atlassian.net/browse/BEAUT-94)
  - Add product structured content block [GLBE-6310](https://dotdash.atlassian.net/browse/GLBE-6310)
  - [Proteus] Add `taggedImages` to `BaseDocumentEx` [GLBE-6303](https://dotdash.atlassian.net/browse/GLBE-6303)
  - Update globe-core version with RequestContext builder loaded resource getters [CMRC-143](https://dotdash.atlassian.net/browse/CMRC-143)
  - Update heightChangeListener to update on negative heights changes [TS-3753](https://dotdash.atlassian.net/browse/TS-3753)
  - Added test for mntl-sc-block that has class mntl-sc-block-html [BEAUT-134](https://dotdash.atlassian.net/browse/BEAUT-134)
  - Added annotation Css Selector for getting component with css selector [DV-116](https://dotdash.atlassian.net/browse/DV-116)
  - Upgrade venus version to 1.3.12 [REF-1787](https://dotdash.atlassian.net/browse/REF-1787)
  - Making css loading callback optional in jquery utilities file [FIN-117](https://dotdash.atlassian.net/browse/FIN-117)
  - Updating overflow handling for katex elements to allow long formula viewing on mobile / avoid overlap with ads on desktop [FIN-137](https://dotdash.atlassian.net/browse/FIN-137)
  - Assigning webkit scrollbar styling for katex elements to ensure user is aware overflow content exists
  - Updating yarn.lock file to address hash mismatch in customizr library
  - Add twitter typeahead.js library. [TS-3743](https://dotdash.atlassian.net/browse/TS-3743)
  - Updated Newsletter to use proxy query param. [CMRC-101](https://dotdash.atlassian.net/browse/CMRC-101)
  - Update review contentsStream to include intro [CMRC-140](https://dotdash.atlassian.net/browse/CMRC-140)
  - Upgrade venus version to 1.3.12 [REF-1787](https://dotdash.atlassian.net/browse/REF-1787)
  - Fix pom version (No ticket)
  - And intro to REVIEWSC and refactor entity data [CMRC-29](https://dotdash.atlassian.net/browse/CMRC-29)
  - Upgrade globe-core to include Religion in ValidDomainUrlDataFactory [REF-1718](https://dotdash.atlassian.net/browse/REF-1718)
  - Add RELIGION to AlgorithmType and Vertical enum [REF-1718](https://dotdash.atlassian.net/browse/REF-1718)
  - Ensure a unique id for newsletter signup input when there are multiple instances on the same page for accessibility [HLTH-4723](https://dotdash.atlassian.net/browse/HLTH-4723)
  - Fix incorrect concatenation in the Cross-Origin filter that prevented www.[domain].com from being a valid iframe parent [GLBE-6326](https://dotdash.atlassian.net/browse/GLBE-6326)
  - Allow external component if any resource is returned [CMRC-68](https://dotdash.atlassian.net/browse/CMRC-68)
  - Add FINANCE Vertical enum (no ticket)

## 3.11.0
 - 3.10.xx code marked as deprecated removed. Details listed in the [3.11 upgrade instructions](https://dotdash.atlassian.net/wiki/spaces/TECH/pages/397246520/Mantle+3.11+Upgrade+Instructions)
 - Remove `HtmlSlicerConfig` from `DocumentService` interface [GLBE-6201](https://dotdash.atlassian.net/browse/GLBE-6201)
   - Removed `HtmlSlicerDocumentService`
   - Removed `HtmlSlicerTask`
   - Deprecated `documentServiceWrapper` method in `MantleSpringConfiguration`
 - In GPT.js removed prioritizeSlots and SlotsQueue. Created sortSlotsByPriority for legacy article templates used by the verticals to replace prioritizeSlots. [GLBE-6205](https://dotdash.atlassian.net/browse/GLBE-6205)
   - Regression fixes for venus

## Release 3.10.x

## 3.10.400
 - Adding filter and support for sandboxed content domain [GLBE-6960](https://dotdash.atlassian.net/browse/GLBE-6960)

## 3.10.399
 - Vertical related automation for [GLBE-7067](https://dotdash.atlassian.net/browse/GLBE-7067) refer to [GLBE-7079](https://dotdash.atlassian.net/browse/GLBE-7079)

## 3.10.398
 - Add bounceExchangeId to pageViewDataAsJSON object [GLBE-7067](https://dotdash.atlassian.net/browse/GLBE-7067)
 - Vertical related automation refer to [GLBE-7079](https://dotdash.atlassian.net/browse/GLBE-7079)

## 3.10.397
 - Add better error handling to Bio Bock component and wrapper div [GLBE-7087](https://dotdash.atlassian.net/browse/GLBE-7087)

## 3.10.396
 - Remove short last paragraphs from Amazon RSS feed [CMRC-700](https://dotdash.atlassian.net/browse/CMRC-700)

## 3.10.395
 - Add sponsor tracking codes to base layout (TRUSTED ONLY) [GLBE-6851](https://dotdash.atlassian.net/browse/GLBE-6851)
 - Vertical automation refer to [GLBE-7023](https://dotdash.atlassian.net/browse/GLBE-7023)

## 3.10.394
 - Automation for Content Security Policy

## 3.10.393
 - Adding support for Group Callout Structured Content block.[GLBE-7039](https://dotdash.atlassian.net/browse/GLBE-7039)
 - Vertical related automation refer to [GLBE-7040](https://dotdash.atlassian.net/browse/GLBE-7040)

## 3.10.392
 - Add Bio content block support [GLBE-6823](https://dotdash.atlassian.net/browse/GLBE-6823)
 - Vertical related automation refer to [GLBE-7038](https://dotdash.atlassian.net/browse/GLBE-7038)

## 3.10.391
 - Using revenueGroup flag to drive commerce logic [GLBE-6980](https://dotdash.atlassian.net/browse/GLBE-6980)
 - Automation Changes: Upgrade to this version will throw compilation error for venus tests which are using isCommerceDocument field from MntlDataLayerTests class.More details in ticket: GLBE-7057(https://dotdash.atlassian.net/browse/GLBE-7057)

## 3.10.390
 - Added JS Chai Tests for MediaGrid Header Bidding [GLBE-7050](https://dotdash.atlassian.net/browse/GLBE-7050)

## 3.10.389
 - Add maybeGet for new images object in Amazon PAAPI V5 (no ticket)

## 3.10.388
 - Bug-fix - Pointing to fixed globe-core - Removed Catchpoint Regular expressions for evaluating useragents [GLBE-7052](https://dotdash.atlassian.net/browse/GLBE-7052)

## 3.10.387 (skip to 3.10.388)
 - CORS allow apollo [GLBE-7004](https://dotdash.atlassian.net/browse/GLBE-7004)

## 3.10.386 (skip to 3.10.388)
 - Automation for MediaGrid Case. [GLBE-6955](https://dotdash.atlassian.net/browse/GLBE-6955)
 - Vertical related automation refer to [GLBE-7049](https://dotdash.atlassian.net/browse/GLBE-7049)

## 3.10.385 (skip to 3.10.388)
 - Fix for MediaGrid case when only partial bids are being returned. [GLBE-7034](https://dotdash.atlassian.net/browse/GLBE-7034)

## 3.10.384 (skip to 3.10.388)
 - Globe changes for Linkman, globe reads from vertical specific topics for cache clearance.[GLBE-6952](https://dotdash.atlassian.net/browse/GLBE-6952)

## 3.10.383 (skip to 3.10.388)
 - Added IndexExchange Queue so values are set in the correct order after async calls. [GLBE-6944](https://dotdash.atlassian.net/browse/GLBE-6944)

## 3.10.382 (skip to 3.10.388)
 - Fixed Dynamic-Inline lazy-ads from not incrementing properly. No Ticket.

## 3.10.381 (skip to 3.10.388)
 - Update deferred svg loading to support IE11 [GLBE-6967](https://dotdash.atlassian.net/browse/GLBE-6967)

## 3.10.380 (skip to 3.10.388)
 - Add support for Amazon PAAPI V5 [CMRC-639](https://dotdash.atlassian.net/browse/CMRC-639)

## 3.10.379 (skip to 3.10.388)
 - Updating globe core version for updated user agent db. [GLBE-6949](https://dotdash.atlassian.net/browse/GLBE-6949)

## 3.10.379
 - Updating globe core version for updated user agent db. [GLBE-6949](https://dotdash.atlassian.net/browse/GLBE-6949)

## 3.10.378
 - Changed taxonomy schema specialty to use document heading instead of title [HLTH-5338](https://dotdash.atlassian.net/browse/HLTH-5338)

## 3.10.377
 - Changed image schema logic to migrate away from schema proteus image [HLTH-5416](https://dotdash.atlassian.net/browse/HLTH-5416)

## 3.10.376
 - Extend cache clearing to secondary requests, e.g. AJAX, deferred/external components, proxies, etc. [GLBE-6877](https://dotdash.atlassian.net/browse/GLBE-6877)

## 3.10.375
 - Bug fix for media grid and leaderboard not getting any bids [GLBE-6995](https://dotdash.atlassian.net/browse/GLBE-6995)

## 3.10.374
 - Adding compression to StructuredContentHtmlEx's string content to reduce cache size [GLBE-6920](https://dotdash.atlassian.net/browse/GLBE-6920)

## 3.10.373
 - Enabled mntl-chop support with lazy-ads.js for LISTSC and LIST templates. [GLBE-6929](https://dotdash.atlassian.net/browse/GLBE-6929)
 - Vertical related automation refer to [GLBE-6958](https://dotdash.atlassian.net/browse/GLBE-6958)

## 3.10.372
 - Add validation for venus tests to track recirc doc ids in unified page view. No Ticket

## 3.10.371
 - Updating globe core version for TemplateComponentLoader improvements. [GLBE-6911](https://dotdash.atlassian.net/browse/GLBE-6911)

## 3.10.370
 - Bug fix with slicer where it sliced isLastBlockOfLastStep [GLBE-6994](https://dotdash.atlassian.net/browse/GLBE-6994)

## 3.10.369
 - Updating globe core version for sonarqube fixes. [GLBE-6940](https://dotdash.atlassian.net/browse/GLBE-6940)

## 3.10.368
 - Added in dynamic-inline support and edge case resolutions of MediaGrid when the ad units parameters are malformed yet also specified at the same time. [GLBE-6975](https://dotdash.atlassian.net/browse/GLBE-6975)

## 3.10.367
 - Add "The Verdict" prefix to amazon rss product summaries [CMRC-646](https://dotdash.atlassian.net/browse/CMRC-646)

## 3.10.366
 - Revert SVG xlink:href to support IOS Safari Mobile/Tablet for <= 12.1. No Ticket.

## 3.10.365
 - Revert some Globe cases due to observed bugs caught in QA. No Ticket.
   - Revert SVG Polyfill (due to SVG bug) [GLBE-6529](https://dotdash.atlassian.net/browse/GLBE-6529)
   - Revert to old Globe version with TemplateComponent Loader improvements [GLBE-6911](https://dotdash.atlassian.net/browse/GLBE-6911)

## 3.10.364 (SKIP)
 - Update Credit Card Schema [PRM-308](https://dotdash.atlassian.net/browse/PRM-308)

## 3.10.363 (SKIP)
 - Allow null for Credit Card attribute values [PRM-306](https://dotdash.atlassian.net/browse/PRM-306)

## 3.10.362 (SKIP)
 - Updating globe core version for TemplateComponentLoader improvements [GLBE-6911](https://dotdash.atlassian.net/browse/GLBE-6911)

## 3.10.361 (SKIP)
 - Updated globe-core version which includes upgrade for commons collections4 to resolve dependency for usage of latest OpenCSV in Travel [TRIP-1888](https://dotdash.atlassian.net/browse/TRIP-1888)

## 3.10.360 (SKIP)
 - Updated RTB MediaGrid Plugin to use their provided library [GLBE-6857](https://dotdash.atlassian.net/browse/GLBE-6857)
 - Updated MediaGrid Plugin to pass our first party data. Currently Page Count/Session Depth, Tax0-3. [GLBE-6858](https://dotdash.atlassian.net/browse/GLBE-6858)
 - Updated MediaGrid Plugin key value pairs passed to Google Ad Manager/GPT [GLBE-6856](https://dotdash.atlassian.net/browse/GLBE-6856)

## 3.10.359 (SKIP)
 - Reverting taxonomy composite cache changes from GLBE-6852 [GLBE-6950](https://dotdash.atlassian.net/browse/GLBE-6950)

## 3.10.358 (SKIP)
 - Added click tracking to mntl-sc-block-callouts [CMRC-600](https://dotdash.atlassian.net/browse/CMRC-600)

## 3.10.357 (SKIP)
 - Introduced autoPause into JWPlayer for new autoplay requirements [GLBE-6895](https://dotdash.atlassian.net/browse/GLBE-6895)
 - Vertical related testing/automation refer to [GLBE-6901](https://dotdash.atlassian.net/browse/GLBE-6901)

## 3.10.356 (SKIP)
 - Removing component property and ignoring unit tests for disabling of skimlinks [GLBE-6932](https://dotdash.atlassian.net/browse/GLBE-6932)

## 3.10.355 (SKIP)
 - Default null query param values in external component service [CMRC-623](https://dotdash.atlassian.net/browse/CMRC-623)

## 3.10.354 (SKIP)
 - Updated Venus with exposed Component methods and expose additonal MntlComponent method [DV-189](https://dotdash.atlassian.net/browse/DV-189)

## 3.10.353 (SKIP)
 - Adding per-DocumentTaxeneComposite cache to reduce duplicated objects in cache [GLBE-6852](https://dotdash.atlassian.net/browse/GLBE-6852)

## 3.10.352 (SKIP)
 - Add overloads for MntlUrl to allow any params [FIN-1212](https://dotdash.atlassian.net/browse/FIN-1212)

## 3.10.351 (SKIP)
 - Adding `CREDITCARDROUNDUP` view type. [HN-8524](https://dotdash.atlassian.net/browse/HN-8524)
 - Adding `STANDALONETOOL` view type. [HN-8525](https://dotdash.atlassian.net/browse/HN-8525)
 - Add customScore field on CreditCardEx model (No ticket)

## 3.10.350 (SKIP)
 - Allow for SVGs to load when used in a deferred component for IE11. [GLBE-6529](https://dotdash.atlassian.net/browse/GLBE-6529)
 - Vertical related automation refer to [GLBE-6925] (https://dotdash.atlassian.net/browse/GLBE-6525)

## 3.10.349
 - Add methodology and editorial disclaimer to BaseDocumentEx [FIN-956](https://dotdash.atlassian.net/browse/FIN-956)

## 3.10.348
 - Fixed sonarqube maven failures (no ticket)

## 3.10.347
 - Adding common mantle test class for GTM checks. [FIN-1106](https://dotdash.atlassian.net/browse/FIN-1106)

## 3.10.346
 - Disabling affiliate disclaimer and skimlinks when document affiliate is set to false [GLBE-6731](https://dotdash.atlassian.net/browse/GLBE-6731)
 - Vertical related automation refer to [GLBE-6900] (https://dotdash.atlassian.net/browse/GLBE-6900)

## 3.10.345
 - Add attribute name into CreditCardAttributeView [PRM-274] (https://dotdash.atlassian.net/browse/PRM-274)

## 3.10.344
 - Adding ad table widgets to Auction Floor pricing blacklist. [GLBE-6903](https://dotdash.atlassian.net/browse/GLBE-6903)

## 3.10.343
 - Changing image placeholder background property to background-color so no overwriting other background properties [HTLH-5220] (https://dotdash.atlassian.net/browse/HLTH-5220)

## 3.10.342
 - DR: Server starts even when Proctor is down. [GLBE-6719](https://dotdash.atlassian.net/browse/GLBE-6719)

## 3.10.341
 - Add taxonomy pages and update for unified schema usage. [GLBE-6538](https://dotdash.atlassian.net/browse/GLBE-6538)
 - Vertical Related automation can refer to [GLBE-6881] (https://dotdash.atlassian.net/browse/GLBE-6881)

## 3.10.340
 - Reverting house ads as it needs effort from vertical teams to make few changes to the test. Will add it as breaking change[DV-184](https://dotdash.atlassian.net/browse/DV-184)

## 3.10.339
 - Adding "no-transform" to Cache-Control header on all responses (static resources, images from thumbor and document requests) [GLBE-6763](https://dotdash.atlassian.net/browse/GLBE-6763)

## 3.10.338
 - Rename device to deviceCategory for selene auction floor service call. No ticket.

## 3.10.337
 - Updated .gitignore to ignore Yaku static folder. No ticket.

## 3.10.336
 - Pass operating system, device category, and tier to auction floor service call [GLBE-6870](https://dotdash.atlassian.net/browse/GLBE-6870)

## 3.10.335
 - Add "AWARDS" viewType for TripSavvy 2019 awards structured content / taxonomy docs [TRIP-1867](https://dotdash.atlassian.net/browse/TRIP-1867)

## 3.10.334
 - Collapse iframe caption silently when optional caption textfield does not exist [GLBE-6902](https://dotdash.atlassian.net/browse/GLBE-6902)

## 3.10.333
 - IPONWEB Plugin into RTB [GLBE-6732](https://dotdash.atlassian.net/browse/GLBE-6738)
 - Added Yaku for Promise Polyfills in IE11 [GLBE-6751](https://dotdash.atlassian.net/browse/GLBE-6751)

## 3.10.332
 - Use ListSc item quote for amazon rss product summary [CMRC-598](https://dotdash.atlassian.net/browse/CMRC-598)

## 3.10.331
 - Added support for in house ads[DV-184](https://dotdash.atlassian.net/browse/DV-184)

## 3.10.330
 - Added recircDocsIdFooter variable to mntlDataLayerTests for datalayer tests [HLTH-5267](https://dotdash.atlassian.net/browse/HLTH-5267)

## 3.10.329
 - Update MantleThreadPoolExecutor to extend GlobeThreadPoolExecutor to set MDC on child threads automatically [GLBE-6849](https://dotdash.atlassian.net/browse/GLBE-6849)

## 3.10.328
 - Fixing 'external' property on mntl-text-link [GLBE-6682](https://dotdash.atlassian.net/browse/GLBE-6682)

## 3.10.327
 - Removing numImages/numInlineLinks from MantleGTM and associated tests [GLBE-6818](https://dotdash.atlassian.net/browse/GLBE-6818)
 - For vertical related automation refer to [GLBE-6854] (https://dotdash.atlassian.net/browse/GLBE-6854)

## 3.10.326
 - Fix loadAbsoluteUrl method - shouldn't require venusVisualTestTargetUrl (would have failed on venus-test by default) [FIN-1036](https://dotdash.atlassian.net/browse/FIN-1036)

## 3.10.325
 - Added gtm page view tasks that also accept a list of DocumentTaxeneComposite as an argument [HLTH-5267](https://dotdash.atlassian.net/browse/HLTH-5267)

## 3.10.324
 - Move advertisement text collapse css into mantle [GLBE-6704](https://dotdash.atlassian.net/browse/GLBE-6704)

## 3.10.323
 - Add instagram and youtube to list of embed providers [GLBE-6717](https://dotdash.atlassian.net/browse/GLBE-6717)
 - Automation for instagram and you tube embed block [GLBE-6812] (https://dotdash.atlassian.net/browse/GLBE-6812)
 - Vertical related automation can refer to [GLBE-6812] (https://dotdash.atlassian.net/browse/GLBE-6812)

## 3.10.322
 - Fix loadAbsoluteUrl method for visual test. Deprecated loadAbsoluteUrl and added loadVisualBaseUrl [DV-180](https://dotdash.atlassian.net/browse/DV-180)

## 3.10.321
 - Legacy url support for external services [CMRC-580](https://dotdash.atlassian.net/browse/CMRC-580)

## 3.10.320
 - Fixing AdCallObject method - wasn't handling first param properly [DV-178](https://dotdash.atlassian.net/browse/DV-178)

## 3.10.319
- implement custom `repeat` functionality for jw-player to solve flash to black frame in between loops [BEAUT-953](https://dotdash.atlassian.net/browse/BEAUT-953)

## 3.10.318
 - updating venus version to 1.3.17 [DV-176](https://dotdash.atlassian.net/browse/DV-176)

## 3.10.317
 - Backing out 3.10.308 due to GLBE-6845 [GLBE-6846](https://dotdash.atlassian.net/browse/GLBE-6844)

## 3.10.316 (do not use.  Skip to 3.10.317)
 - Fix bug where blocks are being casted as htmlblock [GLBE-6844](https://dotdash.atlassian.net/browse/GLBE-6844)

## 3.10.315 (do not use.  Skip to 3.10.317)
 - Adding AuctionFloorService for selene auction pricing endpoints [GLBE-6733](https://dotdash.atlassian.net/browse/GLBE-6733)
 - Adding AuctionFloorInfo to GptAd model and pass floor and floor_id in ad-call params [GLBE-6734](https://dotdash.atlassian.net/browse/GLBE-6734)
 - For automation refer to [GLBE-6827](https://dotdash.atlassian.net/browse/GLBE-6827)

## 3.10.314 (do not use.  Skip to 3.10.317)
 - Cleaned up error handling around bovd resources. Server startup now fails if bovd service cannot be created. [GLBE-6831](https://dotdash.atlassian.net/browse/GLBE-6831)

## 3.10.313 (do not use.  Skip to 3.10.317)
 - Added mantle namespace to proctor rule [HLTH-5226](https://dotdash.atlassian.net/browse/HLTH-5226)

## 3.10.312 (do not use.  Skip to 3.10.317)
 - Fix for AdcallEntry method. Wasn't handling empty params properly. [DV-175](https://dotdash.atlassian.net/browse/DV-175)

## 3.10.311 (do not use.  Skip to 3.10.317)
 - Fixing Mantle Schema Null pointer exception. No ticket

## 3.10.310 (do not use.  Skip to 3.10.317)
 - Exposed taxene config map to Proctor rule [HLTH-5226](https://dotdash.atlassian.net/browse/HLTH-5226)

## 3.10.309 (do not use.  Skip to 3.10.317)
 - extending off MtnlVenusTest instead of directly VenusTest. Required to leverage isProd inside tests. [BEAUT-541](https://dotdash.atlassian.net/browse/BEAUT-541)

## 3.10.308 (do not use.  Skip to 3.10.317)
 - Update StructuredContentDocumentProcessor to input "span" elements with the same class used in our ad placement divs, instead of actually slicing the HTML. [GLBE-6658](https://dotdash.atlassian.net/browse/GLBE-6658)

## 3.10.307 [DOES NOT EXIST]

## 3.10.306
 - Update globe-core version for resolveTemplate performance improvements [GLBE-6743](https://dotdash.atlassian.net/browse/GLBE-6743)

## 3.10.305
 - Update Mantle publishing schema tests. No ticket.

## 3.10.304
 - Adding ability to ignore assertions in AccessLog test. [DV-172](https://dotdash.atlassian.net/browse/DV-172)

## 3.10.303
 - Adding in XPath selector for mantle components [DV-173](https://dotdash.atlassian.net/browse/DV-173)

## 3.10.302
 - Fix proxied cookies so version is not appended to value [CMRC-562](https://dotdash.atlassian.net/browse/CMRC-562)

## 3.10.301
 - Add null check to next block in StructuredContentDocumentProcessor processNestedBlocks [TS-4304](https://dotdash.atlassian.net/browse/TS-4304)

## 3.10.300
 - Removing Pickadate JS while cleaning up bower. [GLBE-5679](https://dotdash.atlassian.net/browse/GLBE-5679)

## 3.10.299
 - Expose display category instead of category for credit card categoryScore [PRM-202](https://dotdash.atlassian.net/browse/PRM-202)

## 3.10.298
 - Add option for vertical customization of placeholder image for lazy loading [WEDD-248](https://dotdash.atlassian.net/browse/WEDD-248)

## 3.10.297
 - Removing Brightcove GA JS while cleaning up bower. [GLBE-5679](https://dotdash.atlassian.net/browse/GLBE-5679)

## 3.10.296
 - Fix AccessLog tests . [DV-172](https://dotdash.atlassian.net/browse/DV-172)

## 3.10.295
 - Move credit card specific services, task, action, models out of mantle [PRM-180](https://dotdash.atlassian.net/browse/PRM-180)
 - Add ImageTask [PRM-180](https://dotdash.atlassian.net/browse/PRM-180)

## 3.10.294
 - Used custom events from embeds to trigger sc-ads right rail recalculations to properly fix the number of ads in the rail. [GLBE-6740](https://dotdash.atlassian.net/browse/GLBE-6740)

## 3.10.293
 - Include author bio page url in sameAs array for bio pages. [GLBE-6744](https://dotdash.atlassian.net/browse/GLBE-6744)

## 3.10.292
 - Fix sponsored content proxy failure [GLBE-6754](https://dotdash.atlassian.net/browse/GLBE-6754)

## 3.10.291 [SKIP TO 3.10.292]
 - Refactor Step Processing Code to add Support for lastBlockOfLastStep and any type of Structure Content Block being a steps block in support of HowTo Template [TS-3880](https://dotdash.atlassian.net/browse/TS-3880)

## 3.10.290 [SKIP TO 3.10.292]
 - Lazy load TrustE badge [HLTH-5231](https://dotdash.atlassian.net/browse/HLTH-5231)

## 3.10.289 [SKIP TO 3.10.292]
 - Allow templated runner with MntlJsErrorTest [FIN-896](https://dotdash.atlassian.net/browse/FIN-896)

## 3.10.288 [SKIP TO 3.10.292]
 - Update event tracking key/values for inline citations [HLTH-5197](https://dotdash.atlassian.net/browse/HLTH-5197)

## 3.10.287 [SKIP TO 3.10.292]
 - Use Predicate wait method instead of sleep for RtbDataLayerTests. [FIN-878](https://dotdash.atlassian.net/browse/FIN-878)

## 3.10.286 [SKIP TO 3.10.292]
 - Resolve sonarqube blocking issues [GLBE-6583](https://dotdash.atlassian.net/browse/GLBE-6583)

## 3.10.285
 - Fix Fake Ads in preview covering document header [GLBE-6730](https://dotdash.atlassian.net/browse/GLBE-6730)

## 3.10.284
 - Fixing sponsorship component pattern library image [GLBE-6620](https://dotdash.atlassian.net/browse/GLBE-6620)

## 3.10.283
 - Updating mntl-accordion to use unique component IDs for included components [GLBE-6553](https://dotdash.atlassian.net/browse/GLBE-6553)

## 3.10.282
 - Allow the first ad to be lazy loaded by SC ads instead of required. (GLBE-6657)[https://dotdash.atlassian.net/browse/GLBE-6657]

## 3.10.281
 - Bumping version to clean up Changelog.md paths to the old JIRA. No ticket. Speak to Daniel Phan about this.

## 3.10.280
 - Take the Automation/customAds repo code for Visual Tests and move to mantle [FIN-272](https://dotdash.atlassian.net/browse/FIN-272)

## 3.10.279
 - Changing max age on Strict-Transport-Security header to 6 months (15552000 seconds) [GLBE-6679](https://dotdash.atlassian.net/browse/GLBE-6679)

## 3.10.278
 - Removing author from schema when authorName is "Staff Author" [GLBE-6402](https://dotdash.atlassian.net/browse/GLBE-6402)
 - For vertical related automation will see removed author details from taxonomy pages

## 3.10.277
 - Fix supefluous credit card rating value on schema to be as it is specified [PRM-164](https://dotdash.atlassian.net/browse/PRM-164)

## 3.10.277
 - Consolidate common RSS code into Mantle [BEAUT-579](https://iacpublishing.atlassian.net/browse/BEAUT-579)

## 3.10.276
 - Updated getBestIntroImage to fallback to summary/primary image if primary tag does not exists in the image map when Proteus is enabled [HLTH-5089](https://dotdash.atlassian.net/browse/HLTH-5089)

## 3.10.275
 - Update globe-core version for security patch performance mitigation [GLBE-6626](https://dotdash.atlassian.net/browse/GLBE-6626)

## 3.10.274
 - Create prod environment check for url. [GLBE-6716](https://dotdash.atlassian.net/browse/GLBE-6716)

## 3.10.273
 - Added null check to getResolvedImgUrl() in SocialTask [HLTH-5189](https://dotdash.atlassian.net/browse/HLTH-5189)

## 3.10.272
 - Add TRUSTe Badge [HLTH-4935](https://dotdash.atlassian.net/browse/HLTH-4935)

## 3.10.271
 - Adding code to support additional data attribute that allows an offset for button ads at the bottom of the rail. [FIN-54](https://dotdash.atlassian.net/browse/FIN-54)

## 3.10.270
 - Add custom=1 key-val to ad call page targeting for sponsored documents [GLBE-6640](https://dotdash.atlassian.net/browse/GLBE-6640)
 - For vertical related automation refer to [GLBE-6650](https://dotdash.atlassian.net/browse/GLBE-6650)

## 3.10.269
 - Add `shortBio` field to `GuestAuthorEx` [GLBE-6641](https://dotdash.atlassian.net/browse/GLBE-6641)
 - Reworked unified schemas slightly to handle guest authors a bit more seamlessly

## 3.10.268
 - Added template details for beauty access log automation. [GLBE-6703](https://dotdash.atlassian.net/browse/GLBE-6703)

## 3.10.267
 - Add promoDescription to BaseDocumentEx [WEDD-26](https://dotdash.atlassian.net/browse/WEDD-26)

## 3.10.266
 - Add How To Document into Mantle. [TS-3878](https://dotdash.atlassian.net/browse/TS-3878)

## 3.10.265
 - Add EMBED structured content block [GLBE-6579](https://dotdash.atlassian.net/browse/GLBE-6579)
 - Add embed service [GLBE-6577](https://dotdash.atlassian.net/browse/GLBE-6577)
 - Add support for custom configuration [GLBE-6598](https://dotdash.atlassian.net/browse/GLBE-6598)
 - Vertical related automation can refer to [GLBE-6616] (https://dotdash.atlassian.net/browse/GLBE-6616)

## 3.10.264
 - Adding class to supply resources from git repository. Updating ads.txt to retrieve content from git. [GLBE-6517](https://dotdash.atlassian.net/browse/GLBE-6517)

## 3.10.263
 - Added template details for beauty access log automation. [GLBE-6695](https://dotdash.atlassian.net/browse/GLBE-66959)

## 3.10.262
 - Review Rating should fallback to entity score if missing on schema. [PRM-19](https://dotdash.atlassian.net/browse/PRM-19)

## 3.10.261
 - Fix NoSuchMethodException from filling logs. [FIN-812](https://dotdash.atlassian.net/browse/FIN-812)

## 3.10.260
 - Credit Card Schema. [PRM-19](https://dotdash.atlassian.net/browse/PRM-19)

## 3.10.259
 - Failed build

## 3.10.258
 - Make iframe embed widget instruction text configurable and toggle button hidden before load. [TS-4190](https://dotdash.atlassian.net/browse/TS-4190)

## 3.10.257
 - Changelog tweak [NO TICKET]

## 3.10.256
 - Add Credit Card Services for search operation [PRM-117](https://dotdash.atlassian.net/browse/PRM-117)

## 3.10.255
- Pass `Mntl` to masonryList IIFE to avoid javascript undefined error [TS-4254](https://dotdash.atlassian.net/browse/TS-4254)

## 3.10.254
 - Added `Rollaway` component into `components/widgets` [CMRC-394](https://dotdash.atlassian.net/browse/CMRC-394)

## 3.10.253
- Create tool embed component and scripts. [TS-4160] https://dotdash.atlassian.net/browse/TS-4160

## 3.10.252
- Update Mantle affiliate disclosure test methods [CMRC-391] https://dotdash.atlassian.net/browse/CMRC-391

## 3.10.251
- Add Mantle affiliate disclosure test methods [CMRC-391] https://dotdash.atlassian.net/browse/CMRC-391

## 3.10.250
 - Add support for STEPBYSTEP viewType in BaseDocumentEx [WEDD-36](https://dotdash.atlassian.net/browse/WEDD-36)

## 3.10.249
 - Changed Inline Citation Sources header to be a link for click tracking [GLBE-6601](https://dotdash.atlassian.net/browse/GLBE-6601)
 - For automation refer to [GLBE-6615] (https://dotdash.atlassian.net/browse/GLBE-6615)

## 3.10.248
 - Update globe core version to update hippodrome to support cached Thumbor url hash Macs [GLBE-6555](https://dotdash.atlassian.net/browse/GLBE-6555)

## 3.10.247
 - Add optional field for external components [CMRC-466](https://dotdash.atlassian.net/browse/CMRC-466)

## 3.10.246
 - Pass `window.Mntl` to IIFE of tooltip to avoid a javascript error [TS-4176](https://dotdash.atlassian.net/browse/TS-4176)

## 3.10.245
 - Had to revert some changes made to AdCallParams [FIN-744](https://dotdash.atlassian.net/browse/FIN-744)

## 3.10.244
 - Refactored sub-components of `mntl-sponsorship` component to allow them to be ref'ed for a more flexible sponsorship layout [LW-2109](https://dotdash.atlassian.net/browse/LW-2109)

## 3.10.243
 - Update credit card backend models [PRM-113](https://dotdash.atlassian.net/browse/PRM-113)

## 3.10.242
 - Fix bug where CachedArticleService was constantly missing for in-memory cache [GLBE-6625](https://dotdash.atlassian.net/browse/GLBE-6625)

## 3.10.241
 - Add vendor name mappings to VendorLookupService [CMRC-378](https://dotdash.atlassian.net/browse/CMRC-378)

## 3.10.240
 - Update globe core version to update hippodrome to support weddings URLs [WEDD-15](https://dotdash.atlassian.net/browse/WEDD-15)

## 3.10.239
 - fix for testAdCallParams method - scp param and cust params aren't required. Had to remove check for video templates. [FIN-725](https://dotdash.atlassian.net/browse/FIN-725)

## 3.10.238
 - Added support to mock ad slots without completing the ad calls. kw=fakeAds will generate these fake ads. [GLBE-6401](https://dotdash.atlassian.net/browse/GLBE-6401)

## 3.10.237
 - MntlCommonTestMethods had new check for cust slot that was exceptioning, fix for that. [FIN-715](https://dotdash.atlassian.net/browse/FIN-715)

## 3.10.236
 - Cache flatten creditcard attribute list and its weight list [PRM-92](https://dotdash.atlassian.net/browse/PRM-92)

## 3.10.235
 - Cleaning up the UrlParams class. Too many instances where cust and scp shared same param. Protect against null pointer exception when fetching values. [DV-160](https://dotdash.atlassian.net/browse/DV-160)

## 3.10.234
 - Add range facet support to faceted search and deion search service [TS-3923](https://dotdash.atlassian.net/browse/TS-3923)

## 3.10.233
 - Collapse sources component when no citations and sources are present [HLTH-5112](https://dotdash.atlassian.net/browse/HLTH-5112)

## 3.10.232
 - Add MARKETING and BLANK viewType enums [HLTH-5083](https://dotdash.atlassian.net/browse/HLTH-5083)

## 3.10.231
 - Add WEDDINGS enum [WEDD-11](https://dotdash.atlassian.net/browse/WEDD-11)

## 3.10.230
 - Adding slot custom param for video ads. Value = video. [GLBE-6588](https://dotdash.atlassian.net/browse/GLBE-6588)
 - Automation for Adding slot custom param for video ads. Value = video. Vertical automation can refer to mantle change, mantle ref test

## 3.10.229
 - Fixed issue where pattern library navigation would extend too far below the copyright block, cutting off the end of the nav (no ticket)

## 3.10.228
 - Fixed MntlAditionalCitationSource venus model for health test. No Ticket

## 3.10.227
 - Fix deserialization of credit card as an entity on reviewsc documents (Involves refactoring of ReviewEntityEx models to match with Selene's BaseProduct models) [PRM-95](https://dotdash.atlassian.net/browse/PRM-95)

## 3.10.226
 - Add Notification Service [CMRC-292](https://dotdash.atlassian.net/browse/CMRC-292)

## 3.10.225
 - Update `startsWith` JS method with `indexOf` - IE11 Support [HLTH-5099](https://dotdash.atlassian.net/browse/HLTH-5099)

## 3.10.224
 - update to adcallexpected classes to allow for modification from inherited. [FIN-673](https://dotdash.atlassian.net/browse/FIN-673)

## 3.10.223
 - Add backend models for CreditCard [PRM-54](https://dotdash.atlassian.net/browse/PRM-54)
 - Flatten attributelist and weightslist of creditcard selene response [PRM-85](https://dotdash.atlassian.net/browse/PRM-85)

## 3.10.222
 - As part of running mantle ref regression on latest mantle version fixed failure with inline citation tracking automated test, verticals using inline citation tracking automation can refer to test [GLBE-6437](https://dotdash.atlassian.net/browse/GLBE-6437)

## 3.10.221
 - Fix various inline-citations and tooltip bugs [HLTH-5099, HLTH-5101] (https://dotdash.atlassian.net/browse/HLTH-5099, https://dotdash.atlassian.net/browse/HLTH-5101)

## 3.10.220
 - Adding slot field for ad param check. [FIN-673](https://dotdash.atlassian.net/browse/FIN-673)

## 3.10.219
 - Update dynamic-inline slot targeting value to remove the ordinal number [GLBE-6590](https://dotdash.atlassian.net/browse/GLBE-6590)

## 3.10.218
 - Stop escaping html where it's not necessary [GLBE-6562](https://dotdash.atlassian.net/browse/GLBE-6562)
   - Moved `unescapeHtml` to globe-core

## 3.10.217
 - Update Anchor Tags for TOC component. [GLBE-6463](https://dotdash.atlassian.net/browse/GLBE-6463)

## 3.10.216
 - updated the citation model return the component as part of the method. [HLTH-4974](https://dotdash.atlassian.net/browse/HLTH-4974)

## 3.10.215
 - Add slot key value pair for GPT slot targeting values. [GLBE-6559](https://dotdash.atlassian.net/browse/GLBE-6559)
 - For vertical automation can refer to mantle ref test

## 3.10.214
 - Automate Author Schema with People. Automate new Bio Schema to unified schema. [GLBE-6540](https://dotdash.atlassian.net/browse/GLBE-6540)

## 3.10.213
 - Exclude affiliate disclosure component on commerce documents (no ticket)

## 3.10.212
 - Adding Error collector for missing params in urlParams method  [FIN-647](https://dotdash.atlassian.net/browse/FIN-647)

## 3.10.211
 - Update Author Schema with People. Added in new Bio Schema to unified schema. [GLBE-6408](https://dotdash.atlassian.net/browse/GLBE-6408)
 - Vertical related automation can refer to [GLBE-6540]
 - Add affiliate disclosure component [CMRC-263](https://dotdash.atlassian.net/browse/CMRC-263)

## 3.10.209
 - Added blocked bidder details to MntlTaxRtbTest, for automation verticals using blocked bidders for RTB can refer to this test  [GLBE-6554](https://dotdash.atlassian.net/browse/GLBE-6554)

## 3.10.208
 - Add support for specifying `relatedArticles` algorithm [GLBE-6516](https://dotdash.atlassian.net/browse/GLBE-6516)
   - The existance of `relatedArticlesAlgorithm` proctor test will enable A/B testing the algorithm anywhere the `relatedArticles` task method is used (e.g. recirc)
     https://proctor-lifestyle.prod.aws.thespruce.com/proctor/definition/relatedArticlesAlgorithm?branch=trunk

## 3.10.207
 - Update globe-core version for caching of Filterable hash codes for performance improvements [GLBE-6546](https://dotdash.atlassian.net/browse/GLBE-6546)

## 3.10.206
 - some cleanup methods for MntlTabRtbTest. After using it we wanted some cleanup done [FIN-620](https://dotdash.atlassian.net/browse/FIN-620)

## 3.10.205
 - Enhance inline-citations and tooltips positioning and click logic. [HLTH-4974](https://dotdash.atlassian.net/browse/HLTH-4974)

## 3.10.204
 - Automation for blocked bidders for RTB. For automation verticals can refer to GLBE-6554 [GLBE-6548](https://dotdash.atlassian.net/browse/GLBE-6548)

## 3.10.203
 - Clean up non-interaction events in Data layer pushes. [GLBE-6506](https://dotdash.atlassian.net/browse/GLBE-6506)

## 3.10.202
 - Fixed a null pointer error for getUrlEnv methods [DV-155](https://dotdash.atlassian.net/browse/DV-155)

## 3.10.201
 - Adding in Blocked Bidders for Amazon in RTB [GLBE-6501](https://dotdash.atlassian.net/browse/GLBE-6501)

## 3.10.200
 - Added null checks for chop ftl. [GLBE-6522](https://dotdash.atlassian.net/browse/GLBE-6522)

## 3.10.199
 - updated venus version for enabling conditional skip of test [DV-153](https://dotdash.atlassian.net/browse/DV-153)

## 3.10.198
 - Added TermSC template type in TOC's javatask [FIN-585](https://dotdash.atlassian.net/browse/FIN-585)

## 3.10.197
 - Add MntlTaxRtbTest for use with all verticals [FIX-583](https://dotdash.atlassian.net/browse/FIN-583)

## 3.10.196
 - Update recipeCategory schema to include dish grouping [TS-4121](https://dotdash.atlassian.net/browse/TS-4121)

## 3.10.195
 - Update recipe schema to include 0 as a valid cook, prep, and total time [TS-4119](https://dotdash.atlassian.net/browse/TS-4119)

## 3.10.194
 - Separate RTB tax params from URL params, fix custom param for ads tests [GLBE-6542](https://dotdash.atlassian.net/browse/GLBE-6542)

## 3.10.193
 - Update globe-core version to get changes for removing jetty server version response header [GLBE-6534](https://dotdash.atlassian.net/browse/GLBE-6534)

## 3.10.192
 - Check size of ancestor nodes in sailthruTask to avoid indexOutOfBound on empty array of nodes [TS-4133](https://dotdash.atlassian.net/browse/TS-4133)

## 3.10.191
 - Changing max age on Strict-Transport-Security header to 1 week (604800 seconds) [GLBE-6537](https://dotdash.atlassian.net/browse/GLBE-6537)

## 3.10.190
 - Adding id mntl-ad-table__disclosure-content to disclaimer content on mntl-ad-table component [FIN-459](https://dotdash.atlassian.net/browse/FIN-459)

## 3.10.189
 - Integrate sonarqube with base configuration [GLBE-6493](https://dotdash.atlassian.net/browse/GLBE-6493)

## 3.10.188
 - Add support for EXCLUSIVE viewType in BaseDocumentEx [BEAUT-622](https://dotdash.atlassian.net/browse/BEAUT-622)

## 3.10.187
 - Fix for 3.10.186 - should have been added to MntlPage not MntlBasePage [FIN-542](https://dotdash.atlassian.net/browse/FIN-542)

## 3.10.186
 - Adding component methods to MntlPage [FIN-542](https://dotdash.atlassian.net/browse/FIN-542)

## 3.10.185
 - Update for Venus to support Java 11 with BizCategoryListener [DV-151](https://dotdash.atlassian.net/browse/DV-151)

## 3.10.184
 - Adding Displayed date and replacing usages of LastPublished date [GLBE-6471](https://dotdash.atlassian.net/browse/GLBE-6471)

## 3.10.183
 - Automation for RTB values [GLBE-6520](https://dotdash.atlassian.net/browse/GLBE-6520)

## 3.10.182
 - Update sailthru.tags to use primaryParent taxonomy slugs [GLBE-6468](https://dotdash.atlassian.net/browse/GLBE-6468)

## 3.10.181
 - Passing Taxonomy (Breadcrumb) values to Amazon Header Bidding [GLBE-6499](https://dotdash.atlassian.net/browse/GLBE-6499)

## 3.10.180
 - Propagating up release/3.9 from (3.9.329)
   - Update ascsubtag to use shortened requestId [CMRC-47](https://dotdash.atlassian.net/browse/CMRC-47)

## 3.10.179
 - Minor fixes to publisher schema block [GLBE-6519](https://dotdash.atlassian.net/browse/GLBE-6519)

## 3.10.178
 - Automation for inline citations engagement tracking [GLBE-6502](https://dotdash.atlassian.net/browse/GLBE-6502)

## 3.10.177
 - Add "Custom Series" (stamp tags) field to DFP [GLBE-6461](https://dotdash.atlassian.net/browse/GLBE-6461)

## 3.10.176
 - Add automation for publisher schema validation [LW-2299](https://dotdash.atlassian.net/browse/LW-2299)

## 3.10.175
 - Update globe-core version to get hippodrom changes for url data factory setup for external services [PRM-15](https://dotdash.atlassian.net/browse/PRM-15)

## 3.10.174
 - Inline citations [GLBE-6426](https://dotdash.atlassian.net/browse/GLBE-6426)
   - Add support for dynamic tooltip
   - Add base component for expandable block

## 3.10.173
 - Made quiz answer text configurable [REF-1936](https://dotdash.atlassian.net/browse/REF-1936)

## 3.10.172
 - Added Sponsor to Summary model [TS-3943](https://dotdash.atlassian.net/browse/TS-3943)

## 3.10.171
 - Don't rely on sponsorship.id in order to display component [TS-3859](https://dotdash.atlassian.net/browse/TS-3859)

## 3.10.170
 - Updating venus core version to the latest. No Ticket.

## 3.10.169
 - Propagating up release/3.9 from (3.9.325 - 328)
   - Updated Jquery to 3.4.0 [GLBE-6473](https://dotdash.atlassian.net/browse/GLBE-6473)
   - Added beauty verticals in old Selene API to support backward compatibility for LiveAbout migration [BEAUT-568](https://dotdash.atlassian.net/browse/BEAUT-568)
   - Quick fix for unescaped ampersands to not take down a server (no ticket, see https://dotdash.slack.com/archives/CFDEY09PB/p1553703170057200)
   - Fixed pattern library example for ad tables to reflect actual usage [FIN-181](https://dotdash.atlassian.net/browse/FIN-181)

## 3.10.168
 - Moved Organization schema inside document schema (webpage). [GLBE-6483](https://dotdash.atlassian.net/browse/GLBE-6483)

## 3.10.167
 - Added null check for document object in AbstractJourneyNode's toString method [HLTH-4948](https://dotdash.atlassian.net/browse/HLTH-4948)

## 3.10.166
 - Making getSeleneVertical method protected (no ticket)

## 3.10.165
 - Fix skimlinks redis deserialization (No ticket)

## 3.10.164 (skip to 3.10.165)
 - Integrate with Skimlinks Pricing Api [CMRC-110](https://dotdash.atlassian.net/browse/CMRC-110)

## 3.10.163
 -  Wrap skimlinks.evaluated.js in an IIFE and pass in `Mntl` explicitly an an alternative solution to issue addressed in 3.10.161 [BEAUT-278](https://dotdash.atlassian.net/browse/BEAUT-278)

## 3.10.162
 - Add role="none" to height-change-listener iframe to hide from screen readers [LW-2286](https://dotdash.atlassian.net/browse/LW-2286)

## 3.10.161
  - Add `Mntl` variable declaration in skimlinks.evaluated.js to prevent errors when executed before global `Mntl` is defined [BEAUT-278](https://dotdash.atlassian.net/browse/BEAUT-278)

## 3.10.160
  - Add `title` attibute for iframe SC block [REF-1706](https://dotdash.atlassian.net/browse/REF-1706)

## 3.10.159
 - Adding MntlBingAfsAdsTest [FIN-407](https://dotdash.atlassian.net/browse/FIN-407)

## 3.10.158
 - Added Automation for Article feedback component [HLTH-4923](https://dotdash.atlassian.net/browse/HLTH-4923)

## 3.10.157
 - Added new fields for publishing schema and created new organization schema block. [GLBE-6407](https://dotdash.atlassian.net/browse/GLBE-6407)

## 3.10.156
 - Pulling legacy url handling code in mantle [GLBE-6360](https://dotdash.atlassian.net/browse/GLBE-6360)

## 3.10.155
 - Video Player expression check fix for non video template pages. [GLBE-6456](https://dotdash.atlassian.net/browse/GLBE-6456)

## 3.10.154
 - Add Faceted Search Task and supporting classes
 	-[TS-3704](https://dotdash.atlassian.net/browse/TS-3704)
 	-[TS-3705](https://dotdash.atlassian.net/browse/TS-3705)
 	-[TS-3707](https://dotdash.atlassian.net/browse/TS-3707)
 	-[TS-3932](https://dotdash.atlassian.net/browse/TS-3932)

## 3.10.153
 - Parallelize the loading of external component resources [CMRC-160](https://dotdash.atlassian.net/browse/CMRC-160)

## 3.10.152
 - Replace `String.split` with `StringUtils.split` [GLBE-6382](https://dotdash.atlassian.net/browse/GLBE-6382)

## 3.10.151
 - Changes for getting verticals running Java11 VM (Amazon Correto JVM) [GLBE-6387](https://dotdash.atlassian.net/browse/GLBE-6387)

## 3.10.150
 - Adding Fields and Flags for Grouping for Spruce faceted search [TS-3822](https://dotdash.atlassian.net/browse/TS-3822)

## 3.10.149
 - Adding MntlAdTableComponent model [FIN-232](https://dotdash.atlassian.net/browse/FIN-232)

## 3.10.148
 - Fix for MntlSchemaCompareTest where schema sizes fail to match; test should stop but would continue can give index out of bound error and hide issue. [FIN-313](https://dotdash.atlassian.net/browse/FIN-313)

## 3.10.147
 - Introduce lazy loading via lazy sizes and dataset-bg for the thumbnail. [GLBE-6395](https://dotdash.atlassian.net/browse/GLBE-6395)

## 3.10.146
 - Cleaned up resource loading pattern in ratings.xml [GLBE-6322](https://dotdash.atlassian.net/browse/GLBE-6322)

## 3.10.145
 - fix testAnalyticsEvent to be easier to debug as well as use collector instead of just throwing Timeout Exception. [FIN-317](https://dotdash.atlassian.net/browse/FIN-317)

## 3.10.144
 - Journey cache optimizations [GLBE-6364](https://dotdash.atlassian.net/browse/GLBE-6364)

## 3.10.143
 - Move taxeneRelationParentsPrimaryAndSecondary task intro mantle [CMRC-182](https://dotdash.atlassian.net/browse/CMRC-182)
 - Add accept header to proxied headers [CMRC-228](https://dotdash.atlassian.net/browse/CMRC-228)

## 3.10.142
 - Rolled back Venus update due to unexpected breaking change (no ticket).

## 3.10.141
 - Add productTitle to review entity data [CMRC-170](https://dotdash.atlassian.net/browse/CMRC-170)

## 3.10.140
 - Propagating release/3.9 to release/3.10 (3.9.323-324)
   - Update ads.txt [GLBE-6400](https://dotdash.atlassian.net/browse/GLBE-6400)
   - Update Selene version to get support for RELIGION vertical [REF-1769](https://dotdash.atlassian.net/browse/REF-1769)

## 3.10.139
 - Updated JWPlayer Key [GLBE-6389](https://dotdash.atlassian.net/browse/GLBE-6389)

## 3.10.138
- Fixing log level when kafka consumer creation is failed due to broker unavailability (no ticket)

## 3.10.137
- Replace `getSecureProperty` taking default value with one taking `isRequired` flag [GLBE-6312](https://dotdash.atlassian.net/browse/GLBE-6312)

## 3.10.136
 - Added null-check in CachedDocumentTaxeneService [BEAUT-416](https://dotdash.atlassian.net/browse/BEAUT-416)

## 3.10.135
 - Full cache clearance solution [GLBE-6357](https://dotdash.atlassian.net/browse/GLBE-6357)

## 3.10.134
 - Propagating release/3.9 to release/3.10 (3.9.322)
   - Added mntl-ad-table component to be used to create performance marketing ad tables [FIN-181](https://dotdash.atlassian.net/browse/FIN-181)

## 3.10.133
- Parse external component service query params so that the request's input stream is preserved [CMRC-101](https://dotdash.atlassian.net/browse/CMRC-101)

## 3.10.132
- If no document.summary.description, fallback to document.description for bio-schema description [BEAUT-381](https://dotdash.atlassian.net/browse/BEAUT-381)

## 3.10.131
- Updated MntlSCBlockHTML model from Mantle-ref to be included in Mantle as part of PL cleanup. [GLBE-6331](https://dotdash.atlassian.net/browse/GLBE-6331)

## 3.10.130
 - Updated PL value for Accordion Document List (previous value lost its Journey values). [GLBE-6373](https://dotdash.atlassian.net/browse/GLBE-6373)

## 3.10.129
 - Added in ComparisonListModel to Mantle for PL testing (and inheritable testing for verticals) GLBE-6331(https://dotdash.atlassian.net/browse/GLBE-6331)

## 3.10.128
 - Changed Quiz results PL entry to markup preview type. [GLBE-6383](https://dotdash.atlassian.net/browse/GLBE-6383)

## 3.10.127
 - Propagating release/3.9 to release/3.10 (3.9.321)
  - Updating MntlSchemaCompareTest to show differences when encountered between schemas [FIN-230](https://dotdash.atlassian.net/browse/FIN-230)

## 3.10.126
 - Updated test Quiz data and fixed quiz results PL entry. [GLBE-6374](https://dotdash.atlassian.net/browse/GLBE-6374)

## 3.10.125
 - Propagating release/3.9 to release/3.10 (3.9.319-320)
   - [HackerOne] stop allowing url query param to hijack the request context [GLBE-6367](https://dotdash.atlassian.net/browse/GLBE-6367)
   - Updating mntl-sc-page to use render utils function for resolving block names [FIN-184](https://dotdash.atlassian.net/browse/FIN-184)

## 3.10.124
 - Updated globe core version **(3.9.22)** to get changes for resolving actions in order to support rendering of legacy urls for Beauty verticals [BEAUT-255](https://dotdash.atlassian.net/browse/BEAUT-255)

## 3.10.123
 - Fix flamegraph not being generated after first run until server restart

## 3.10.122
 - Fixing Commerce Container PL. [GLBE-6365](https://dotdash.atlassian.net/browse/GLBE-6365)

## 3.10.121
 - Extract absolute image url creation for commerce to override [CMRC-178](https://dotdash.atlassian.net/browse/CMRC-178)

## 3.10.120
 - Fixing SC Inline Video PL. To use a hardcoded template url [GLBE-6369](https://dotdash.atlassian.net/browse/GLBE-6369)

## 3.10.119
 - Propagating release/3.9 to release/3.10 (3.9.318)
  - Added more verbose JWPlayer error logging to message field [GLBE-6363](https://dotdash.atlassian.net/browse/GLBE-6363)

## 3.10.118
 - Changes: Handle alternate previewType config in PL [GLBE-6299](https://dotdash.atlassian.net/browse/GLBE-6299)

## 3.10.117
 - Handle alternate previewType config in PL [GLBE-6299](https://dotdash.atlassian.net/browse/GLBE-6299)

## 3.10.116
 - Using Proteus SCHEMA images [GLBE-6361](https://dotdash.atlassian.net/browse/GLBE-6361)

## 3.10.115
 - Added class to `reveal` section of quiz that shows answer type [REF-1752](https://dotdash.atlassian.net/browse/REF-1752)

## 3.10.114
 - Brought back Task Execution css/js component functionality [GLBE-6349](https://dotdash.atlassian.net/browse/GLBE-6349)

## 3.10.113
 - Extract csrfToken from newsletter submit ftl (No ticket)

## 3.10.112
 - JWPlayer Optimizations for Performance [GLBE-6335](https://dotdash.atlassian.net/browse/GLBE-6335)

## 3.10.111
 - Cache external resources for external component requests [CMRC-130](https://dotdash.atlassian.net/browse/CMRC-130)

## 3.10.110
 - Updated Unified Schema to Support Review Type "Product". [CMRC-65](https://dotdash.atlassian.net/browse/CMRC-65)

## 3.10.109
 - Added nutrition label SC block [HLTH-2905](https://dotdash.atlassian.net/browse/HLTH-2905)

## 3.10.108
 - Updated globe-core version to add legacyDocument flag in VerticalUrlData in order to support migrated legacy content in beauty verticals [BEAUT-94](https://dotdash.atlassian.net/browse/BEAUT-94)

## 3.10.107
 - Add product structured content block [GLBE-6310](https://dotdash.atlassian.net/browse/GLBE-6310)

## 3.10.106
 - [Proteus] Add `taggedImages` to `BaseDocumentEx` [GLBE-6303](https://dotdash.atlassian.net/browse/GLBE-6303)

## 3.10.105
 - Update globe-core version with RequestContext builder loaded resource getters [CMRC-143](https://dotdash.atlassian.net/browse/CMRC-143)

## 3.10.104
 - Update heightChangeListener to update on negative heights changes [TS-3753](https://dotdash.atlassian.net/browse/TS-3753)

## 3.10.103
 - Propagating release/3.9 to release/3.10 (3.9.314 - 317)
  - Added test for mntl-sc-block that has class mntl-sc-block-html [BEAUT-134](https://dotdash.atlassian.net/browse/BEAUT-134)
  - Added annotation Css Selector for getting component with css selector [DV-116](https://dotdash.atlassian.net/browse/DV-116)
  - Upgrade venus version to 1.3.12 [REF-1787](https://dotdash.atlassian.net/browse/REF-1787)
  - Making css loading callback optional in jquery utilities file [FIN-117](https://dotdash.atlassian.net/browse/FIN-117)
  - Updating overflow handling for katex elements to allow long formula viewing on mobile / avoid overlap with ads on desktop [FIN-137](https://dotdash.atlassian.net/browse/FIN-137)
  - Assigning webkit scrollbar styling for katex elements to ensure user is aware overflow content exists
  - Updating yarn.lock file to address hash mismatch in customizr library

## 3.10.102
 - Add twitter typeahead.js library. [TS-3743](https://dotdash.atlassian.net/browse/TS-3743)

## 3.10.101
 - Updated Newsletter to use proxy query param. [CMRC-101](https://dotdash.atlassian.net/browse/CMRC-101)

## 3.10.100
 - Update review contentsStream to include intro [CMRC-140](https://dotdash.atlassian.net/browse/CMRC-140)

## 3.10.99
 - Upgrade venus version to 1.3.12 [REF-1787](https://dotdash.atlassian.net/browse/REF-1787)

## 3.10.98
 - Fix pom version (No ticket)

## 3.10.97
 - And intro to REVIEWSC and refactor entity data [CMRC-29](https://dotdash.atlassian.net/browse/CMRC-29)

## 3.10.96
 - Upgrade globe-core to include Religion in ValidDomainUrlDataFactory [REF-1718](https://dotdash.atlassian.net/browse/REF-1718)
 - Add RELIGION to AlgorithmType and Vertical enum [REF-1718](https://dotdash.atlassian.net/browse/REF-1718)

## 3.10.95
 - Propagating release/3.9 to release/3.10 (3.9.312-313)
   - Ensure a unique id for newsletter signup input when there are multiple instances on the same page for accessibility [HLTH-4723](https://dotdash.atlassian.net/browse/HLTH-4723)
   - Fix incorrect concatenation in the Cross-Origin filter that prevented www.[domain].com from being a valid iframe parent [GLBE-6326](https://dotdash.atlassian.net/browse/GLBE-6326)

## 3.10.94
 - Allow external component if any resource is returned [CMRC-68](https://dotdash.atlassian.net/browse/CMRC-68)
 - Add FINANCE Vertical enum (no ticket)

## 3.10.93
 - Fixing SC Inline Video Settings after JWPlayer Cleanup [GLBE-6335](https://dotdash.atlassian.net/browse/GLBE-6335)

## 3.10.92
 - Propagating release/3.9 to release/3.10 (3.9.309-311)
   - Add aria-label to star ratings for accessibility [HLTH-4669](https://dotdash.atlassian.net/browse/HLTH-4669)
   - Various pattern library fixes
     - Use component macro for setting proper id on quiz mntl-disclaimer [GLBE-6284](https://dotdash.atlassian.net/browse/GLBE-6284)
     - Fix mntl-made-it and mntl-star-rating rendering [GLBE-6285](https://dotdash.atlassian.net/browse/GLBE-6285)
     - Add additional properties to componentList response so ComponentPLTest can filter out based on previewType [GLBE-6293](https://dotdash.atlassian.net/browse/GLBE-6293)
   - Updating mntl-katex js to render formulas [FIN-117](https://dotdash.atlassian.net/browse/FIN-117)

## 3.10.91
 - Update deferred component positions on document resize [TS-3753](https://dotdash.atlassian.net/browse/TS-3753)

## 3.10.90
 - Cleaned up some JWPlayer code and setup AB testable size wrapper settings. [GLBE-6024](https://dotdash.atlassian.net/browse/GLBE-6024)

## 3.10.89
 - Added `lang` attribute on the HTML tag [REF-1712](https://dotdash.atlassian.net/browse/REF-1712)

## 3.10.88
 - Add sponsorship component [HLTH-4401](https://dotdash.atlassian.net/browse/HLTH-4401)

## 3.10.87
 - Propagating release/3.9 to release/3.10 (3.9.305-308)
  - Junit updates for Amzon RSS service  [GLBE-6320](https://dotdash.atlassian.net/browse/GLBE-6320)
  - Better error handling and retry when Amazon product API is unavailable [GLBE-6282](https://dotdash.atlassian.net/browse/GLBE-6282)
  - Fix pom version (No ticket)
  - Remove years from item titles in Amazon RSS bug fix (No ticket)

## 3.10.86
 - Added enum values in Vertical and AlgorithmType for new beauty verticals [BEAUT-124](https://dotdash.atlassian.net/browse/BEAUT-124)

## 3.10.85
 - Add cookies and user agent header to external component request [CMRC-109](https://dotdash.atlassian.net/browse/CMRC-109)

## 3.10.84
 - Fix OriginalRequestApplication field to account for fastly headers [CMRC-94](https://dotdash.atlassian.net/browse/CMRC-94)

## 3.10.83
 - Fixing jnyroot assertion validation for venus tests [LW-2252](https://dotdash.atlassian.net/browse/LW-2252)
 - Re-added debug-level logging for HttpClient (hippodrome) [GLBE-6289](https://dotdash.atlassian.net/browse/GLBE-6289)

## 3.10.82
 - Propagating release/3.9 to release/3.10 (3.9.304)
  - Fix git proctor issue where latest test changes are not pulled at runtime [GLBE-6317](https://dotdash.atlassian.net/browse/GLBE-6317)

## 3.10.81
 - Updated JWPlayer Pattern Library to start PC autoplay with mute. [GLBE-6294](https://dotdash.atlassian.net/browse/GLBE-6294)

## 3.10.80
 - Create custom TOC anchor [CMRC-29](https://dotdash.atlassian.net/browse/CMRC-29)

## 3.10.79
 - Add tool content block [HLTH-4656](https://dotdash.atlassian.net/browse/HLTH-4656)

## 3.10.78
- Propagating release/3.9 to release/3.10 (3.9.298-303)
 - Add all templates to unified schema main entity mapping [GLBE-6292](https://dotdash.atlassian.net/browse/GLBE-6292)
 - Fix server startup issue by defaulting bing appId key to null
 - Removing GPT Tranches and AB test file [GLBE-6207](https://dotdash.atlassian.net/browse/GLBE-6207)
 - Fix for AccessLogs Tests failures due to Session ID/User ID changes (https://dotdash.atlassian.net/browse/HLTH-4709).
 - Updated to jUnit 4.12, Hamcrest 2.1
 - Add `mntl-bing-afs` component and BingAdsSearchService to get text ads for search pages [REF-1646](https://dotdash.atlassian.net/browse/REF-1646)
 - Adding Badge Field to metadata  [TS-3679](https://dotdash.atlassian.net/browse/TS-3679)

## 3.10.77
 - Propagating release/3.9 to release/3.10 (3.9.277-297)
   - Adding "kw_only" querystring to url should remove all targeting on ad calls [GLBE-6217](https://dotdash.atlassian.net/browse/GLBE-6217)
   - Automation changes for Adding "kw_only" querystring to url should remove all targeting on ad calls. [GLBE-6280](https://dotdash.atlassian.net/browse/GLBE-6280)
   - Made 'custParams', 'scpParams' and 'prevScpParams' public in UrlParams class
   - Separate ComparisonList class and have as MntlComponent for code reuse [INV-611](https://dotdash.atlassian.net/browse/INVRP-611)
   - Moving work from 3.9.291 out to test class. Originally had checks as part of model and after discussion figured out better way to run in test class
   - jackson-databind security fix [GLBE-6291](https://dotdash.atlassian.net/browse/GLBE-6291)
   - Removed padding and height calculation "hack" that overrode component height in the pattern library [GLBE-6268](https://dotdash.atlassian.net/browse/GLBE-6268)
   - Backport from 3.10.68: Remove whitespace within anchor and span tags as this causes issues when using with CSS pseudo-elements [HLTH-4613](https://dotdash.atlassian.net/browse/HLTH-4613)
   - Adding tests taking from travel star ratings and putting into mantle for reuse in finance.
   - Auto updating feature for Amazon RSS [CMRC-13](https://dotdash.atlassian.net/browse/CMRC-13)
   - Adding venus component to support Bing ads [REF-1665](https://dotdash.atlassian.net/browse/REF-1665)
   - Adding missing UPNEXT featured link enum [TRIP-1608](https://dotdash.atlassian.net/browse/TRIP-1608)
   - Removed width from dotdash-family-nav so parent containers on the vertical overrides the element for their styling. [GLBE-6277](https://dotdash.atlassian.net/browse/GLBE-6277)
   - RequestId (in turn SessionId, UserId, GA Session Id) is generated using Java UUID [GLBE-6226](https://dotdash.atlassian.net/browse/GLBE-6226)
   - Fixing bug when amazon commerce service is null, this is temporary fix to avoid server start-up failures. Will be better handled soon.
   - Adding location tags to Review SC block links [INVRP-567](https://dotdash.atlassian.net/browse/INVRP-567)
   - Revert incomplete feature
   - Incomplete feature
   - Adding sc block component for Review content [INVRP-567](https://dotdash.atlassian.net/browse/INVRP-567)
   - Adding BROKERAWARDS FeaturedLink theme [INVRP-595](https://dotdash.atlassian.net/browse/INVRP-595)
   - Add `mntl-sc-block-location__pre-content` location in location content block. Rename `top` location to `mntl-sc-block-location__media` [TRIP-1065](https://dotdash.atlassian.net/browse/TRIP-1065)
   - Adding unified schema for Review content [INVRP-568](https://dotdash.atlassian.net/browse/INVRP-568)
   - On article-feeback, alter js scoping of `js-open-form-button` to allow anywhere within component [TRIP-1051](https://dotdash.atlassian.net/browse/TRIP-1051)

## 3.10.76
 - Automation for external component svg resource [GLBE-6276](https://dotdash.atlassian.net/browse/GLBE-6276)

## 3.10.75
 - Added PL component tests [GLBE-6271](https://dotdash.atlassian.net/browse/GLBE-6271)

## 3.10.74
 - Commented out OriginalRequestApplication in RequestContext as workaround for GLBE-6298 (No ticket)

## 3.10.73
 - Stop adslot insertion between two featured link structured content block [LW-2236](https://dotdash.atlassian.net/browse/LW-2236)

## 3.10.72
 - Updating jnyroot key for venus tests [TS-3692](https://dotdash.atlassian.net/browse/TS-3692)

## 3.10.71
 - Change featured link theme type from enum to String [LW-2183](https://dotdash.atlassian.net/browse/LW-2183)

## 3.10.70 (Skip to 3.10.71 because of accidental push)
 - Change featured link theme type from enum to String [LW-2183](https://dotdash.atlassian.net/browse/LW-2183)

## 3.10.69
 - jackson-databind security fix [GLBE-6291](https://dotdash.atlassian.net/browse/GLBE-6291)

## 3.10.68
 - Remove whitespace within anchor and span tags as this causes issues when using with CSS pseudo-elements [HLTH-4613](https://dotdash.atlassian.net/browse/HLTH-4613)

## 3.10.67
 - Use smile/json v1 for DeionSearchFullDocumentServiceImpl

## 3.10.66
 - Add proxy headers to the external request [CMRC-60](https://dotdash.atlassian.net/browse/CMRC-60)

## 3.10.65
 - Update globe-core to include support for Byrdie and Mydomaine in ValidDomainUrlDataFactory [BEAUT-10](https://dotdash.atlassian.net/browse/BEAUT-10)

## 3.10.64
 - Add Table of Content Support for Review Documents [CMRC-64](https://dotdash.atlassian.net/browse/CMRC-64)

## 3.10.63
 - Added support for the journey root check [TS-3692](https://dotdash.atlassian.net/browse/TS-3692)

## 3.10.62
 - Fixing bug when amazon commerce service is null, this is temporary fix to avoid server start-up failures. Will be better handled soon.

## 3.10.61
 - Fix image lightbox to show linked captions [TS-3602](https://dotdash.atlassian.net/browse/TS-3602)

## 3.10.60
 - Add svg resource support to external components [GLBE-6214](https://dotdash.atlassian.net/browse/GLBE-6214)

## 3.10.59
 - Upgrade globe-core to support commerce url parsing [CMRC-41](https://dotdash.atlassian.net/browse/CMRC-41)

## 3.10.58
 - Remove reference to deprecated `Mntl.utilities.customEvent` in the table-of-contents [GLBE-6265](https://dotdash.atlassian.net/browse/GLBE-6265)

## 3.10.57
 - Use standalone `NutritionInfo` class in `RecipeStructredContentDocumentEx.NutritionInfo` and delete redundant `NutritionInfo` static nested class [TS-3700](https://dotdash.atlassian.net/browse/TS-3700)

## 3.10.56
 - Remove reference to deprecated Mntl.utilities.customEvent [TS-3701](https://dotdash.atlassian.net/browse/TS-3701)

## 3.10.55
 - Adding venus model for external component [GLBE-6140](https://dotdash.atlassian.net/browse/GLBE-6140)

## 3.10.54
 - Fixing the bug for external component service registration

## 3.10.53
 - Removed try/catch around proxy filter handler to allow errors to propagate [GLBE-6256](https://dotdash.atlassian.net/browse/GLBE-6256)

## 3.10.52
 - Fixing inline video click tracking venus tests

## 3.10.51
 - Propagating release/3.9 to release/3.10 (3.9.273-276)
   - Remove legacy S3 logs [GLBE-5544](https://dotdash.atlassian.net/browse/GLBE-5544)
   - Making expectedHeaderEntries protected so it can be modified in inherited classes
   - Added in logic inside Mantle Search Task to allow for empty queries [GLBE-6175](https://dotdash.atlassian.net/browse/GLBE-6175)
   - Backport from 3.10.46: Fix itemListOrder macro for freemarker error

## 3.10.50
 - Update model call for `jnyroot` baseSlotTargeting param to include required arguments [TS-3662](https://dotdash.atlassian.net/browse/TS-3662)

## 3.10.49
 - Fix caching ugc service so that it uses the non-caching version when there's nothing in the cache [TS-3557](https://dotdash.atlassian.net/browse/TS-3557)

## 3.10.48 (skip to 3.10.50)
 - Propagating release/3.9 to release/3.10 (3.9.263-272)
   - Remove final keyword from video event click tracking variable
   - Use meta data for intro copy in RSS feed when the Intro is empty [CMRC-11](https://dotdash.atlassian.net/browse/CMRC-11)
   - Add `jnyroot` key to baseSlotTargeting to associate all journey documents under one key/val pair for adops targeting [TS-3662](https://dotdash.atlassian.net/browse/TS-3662)
   - Upgrade to the latest version of proctor and add git support [GLBE-4895](https://dotdash.atlassian.net/browse/GLBE-4895)
     - For information on migrating to git see https://dotdash.atlassian.net/wiki/spaces/TECH/pages/391839786/Migrate+Proctor+from+SVN+to+GIT
   - Fixing duped HTML Unit dependency in 3.9 and upgrade to 2.33.
   - Remove the year and the preposition before it from the Amazon RSS feed [CMRC-10](https://dotdash.atlassian.net/browse/CMRC-10)
   - Added support for 'name' attribute in LocationService. [TRIP-1567](https://dotdash.atlassian.net/browse/TRIP-1567)
   - Changelog Fixes
   - Upgraded to Venus 1.3.11
   - Moved viewType from TaxonomyDocumentEx to super BaseDocumentEx class. Upgraded Selene version to get new "Best For X" Enum from Selene API [INVRP-500](https://dotdash.atlassian.net/browse/INVRP-500)

## 3.10.47
 - CI/CD failure

## 3.10.46
 - Fix itemListOrder function for freemarker error and add missing @bean annotation for JsonLdSchemaTask

## 3.10.45
 - Fix external component to work with addition of SVGs (still not supported) [GLBE-6245](https://dotdash.atlassian.net/browse/GLBE-6245)

## 3.10.44 (Skip to 3.10.45)
 - Support for external components (primarily for Commerce) [GLBE-6140](https://dotdash.atlassian.net/browse/GLBE-6140)

## 3.10.43
 - Add external handler passthrough bean to account for new external server proxy filter in globe-core

## 3.10.42 (Skip to 3.10.43)
 - Fix TOC id when using short heading in content block. [HLTH-4609](https://dotdash.atlassian.net/browse/HLTH-4609)

## 3.10.41 (Skip to 3.10.43)
 - Propagating release/3.9 to release/3.10 (3.9.260-262)
   - Updated Ads.txt to use adtech.com [GLBE-6218](https://dotdash.atlassian.net/browse/GLBE-6218)
   - Add venus test for testing all globe components in the pattern library [GLBE-6135](https://dotdash.atlassian.net/browse/GLBE-6135)
   - Adding Review document and updating globe core for hippodrome change [GLBE-6184](https://dotdash.atlassian.net/browse/GLBE-6184)
   - Adding Structured Content block for Review data [GLBE-6187](https://dotdash.atlassian.net/browse/GLBE-6187)

## 3.10.40
  - DocumentService now will always include document summaries, and disable projections.  [GLBE-6142](https://dotdash.atlassian.net/browse/GLBE-6142)

## 3.10.39
 - Push custom event to dataLayer when liftIgniterBeaconLoad event fires [HLTH-4588](https://dotdash.atlassian.net/browse/HLTH-4588)

## 3.10.38
 - Propagating release 3.9 to release/3.10 (3.9.255-259)
   - Fix bug introduced in 3.9.255 to wiped all svg images.
   - Update stepLists, stepsContent to list and added stepsHeader in Structure Content Project Steps Component
   - Update inline video click functionality
   - SC Tables, remove extra row on the top when title is not set and the First row is heading. [LW-2180](https://dotdash.atlassian.net/browse/LW-2180)
   - Remove Tranches in GPT for AB Testing [GLBE-6129](https://dotdash.atlassian.net/browse/GLBE-6129)

## 3.10.37
 - Added MntlScBlockGuideComponent for Automation support (https://dotdash.atlassian.net/browse/HLTH-4580)

## 3.10.36
 - Propagating release 3.9 to release/3.10 (3.9.253-254)
   - Support `<svg>` tags as includable, deduped resources, similar to handling for `<script>` and `<stylesheet>` tags.
   - Improving existing GPT Javascript unit tests [GLBE-6128](https://dotdash.atlassian.net/browse/GLBE-6128)

## 3.10.35
- Inline video is not showing on IE. Issues with Polyfill support for Object.assign not ignoring nulls and undefined args. [LW-2200](https://dotdash.atlassian.net/browse/LW-2200)

## 3.10.34
 - SC Tables, remove extra row on the top when title is not set and the First row is heading. [LW-2180](https://dotdash.atlassian.net/browse/LW-2180)

## 3.10.33
 - Add guide content block [HLTH-4398](https://dotdash.atlassian.net/browse/HLTH-4398)

## 3.10.32
 - Propagating release 3.9 to release/3.10 (3.9.249-252)
   - Add end points to return all components registered in globe [GLBE-6188](https://dotdash.atlassian.net/browse/GLBE-6188)
   - Move all advertising parameters for video onto the cust_params key. [GLBE-6176](https://dotdash.atlassian.net/browse/GLBE-6176) **Warning** this change will break video tests. Please see (https://bitbucket.prod.aws.about.com/projects/FRON/repos/reference/pull-requests/334/overview) for guidance on how to fix the tests.
   - SC Tables, remove extra row on the top when title is not set. [LW-2180](https://dotdash.atlassian.net/browse/LW-2180)
   - New unified json ld schema [GLBE-6069](https://dotdash.atlassian.net/browse/GLBE-6069)
   - Add MockTaxeneRelationTask and mockBreadcrumb task method
   - Fixing Unified Recipe Rating Section for Schema [GLBE-6172](https://dotdash.atlassian.net/browse/GLBE-6172)

## 3.10.31
 - Propagating release 3.9 to release/3.10 (3.9.248)
   - SC Tables, show the horizontal scroll only when needed. [LW-2176](https://dotdash.atlassian.net/browse/LW-2176)

## 3.10.30
 - Fix bad reference in JW-PLayer.js file [GLBE-6177](https://dotdash.atlassian.net/browse/GLBE-6177)

## 3.10.29
 - Fix broken xml for structured-content.xml

## 3.10.28 (Skip to 3.10.30 for merge fix)
 - Propagating release 3.9 to release/3.10 (3.9.235-247)
   - Ignore optional title and caption properties in SC table component for missing content [INVRP-434](https://dotdash.atlassian.net/browse/INVRP-434)
   - Automation for smart captions to video players [LW-2167](https://dotdash.atlassian.net/browse/LW-2167)
   - Fixed bug in gpt-definition.evaluated.js [GLBE-6173](https://dotdash.atlassian.net/browse/GLBE-6173)
   - Fix inlineVideo inconsistent tests [LW-2170](https://dotdash.atlassian.net/browse/LW-2170)
   - Remove unnecessary mock documents from component info tags [GLBE-6110](https://dotdash.atlassian.net/browse/GLBE-6110)
   - Added KaTeX JS library to render formulas [REF-1546](https://dotdash.atlassian.net/browse/REF-1546)
   - Fix another unescaped character in article-body-sc-block-table [INVRP-431](https://dotdash.atlassian.net/browse/INVRP-431)
   - Allow `document.guestAuthor` to take precedence over `author` in document-schema [INVRP-410](https://dotdash.atlassian.net/browse/INVRP-410)
   - Added captions to jw player [LW-2129](https://dotdash.atlassian.net/browse/LW-2129)
   - Add "/facebookshareredirect.htm" to actions.xml to support post-share redirects without verticals having to re-implement [GLBE-6168](https://dotdash.atlassian.net/browse/GLBE-6168)
   - Fix schema errors for schema-table and article-body-sc-block-table  [INVRP-431](https://dotdash.atlassian.net/browse/INVRP-431)
   - Refactor sticky billboard logic on browser resize, changed reference classname as part of 3.9.223.  [LW-2165](https://dotdash.atlassian.net/browse/LW-2165)

## 3.10.27
 - Allow new form variables with prefix "customVariable_" to be passed in newsletter signup [HLTH-4477](https://dotdash.atlassian.net/browse/HLTH-4477)

## 3.10.26
 - Fix reference to functional utilities

## 3.10.25 (Skip to 3.10.26 for merge fix)
 - Propogating release 3.9 to release/3.10 (3.9.224-234)
   - Update ads.txt for media.net [GLBE-6167](https://dotdash.atlassian.net/browse/GLBE-6167)
   - Add <@component> wrapper to disqus comment count script so it can be deferred [TS-3562](https://dotdash.atlassian.net/browse/TS-3562)
   - Fix logic for adding commas in inline-images-structured-content [INVRP-410](https://dotdash.atlassian.net/browse/INVRP-410)
   - Added venus component for feature link content block  [TS-3586](https://dotdash.atlassian.net/browse/TS-3586)
   - Added nullcheck for missing `showTitle` in videos.  [INVRP-370](https://dotdash.atlassian.net/browse/INVRP-370)
   - Updated venus DragAndDrop Slider method to work for both inline videos and footer videos
   - Added MntlFaviconTest
   - Remove duplicate attribute loop in the <@a> macro [GLBE-6124](https://dotdash.atlassian.net/browse/GLBE-6124)
   - Added script/stylesheet to make embeded youtube videos scale in a 16:9 ratio [REF-1608](https://dotdash.atlassian.net/browse/REF-1608)

## 3.10.24
 - Suppress html comments at the end of components, locations, and a-tag macros unless specificaly enabled by query param [GLBE-6151](https://dotdash.atlassian.net/browse/GLBE-6151)
 - Propogating release/3.9 to release/3.10 (3.9.223)
 	- Resolving multi-ad billboard layout issues [INVRP-321](https://dotdash.atlassian.net/browse/INVRP-321)

## 3.10.23
 - Propogating release 3.9 to release/3.10 (3.9.215-222)
   - Add `readyForThirdPartyTracking` event. Fired from gtm.evaluated into the dataLayer to trigger third party tracking. [GLBE-6089](https://dotdash.atlassian.net/browse/GLBE-6089)
   - Don't give chop-content element the `is-chopped` class when `chop-height` property isn't provided [GLBE-6145](https://dotdash.atlassian.net/browse/GLBE-6145)
   - Add `card__top` location in card.ftl for child component injection [INVRP-335](https://dotdash.atlassian.net/browse/INVRP-335)
   - Improved canonicalization logic for investopedia [INVRP-267](https://dotdash.atlassian.net/browse/INVRP-267)
   - update thumbor proxy to allow headers up to 8k fixing an issue with long atlas cookies
   - Switch to lastPublished from updatedDate in amazon rss service [GLBE-6070](https://dotdash.atlassian.net/browse/GLBE-6070)
   - Change name of field used to generate recipe sc keywords schema [TS-3474](https://dotdash.atlassian.net/browse/TS-3474)
   - Made methods in MntlCommontTestMethods more generic

## 3.10.22
 - Honor state and et query params when applying document redirect logic [GLBE-6146](https://dotdash.atlassian.net/browse/GLBE-6146)
 - Deprecate state and activeDate helper methods operating on RequestContext in favor of ones operating on param map

## 3.10.21
 - Propogating release 3.9 to release/3.10 (3.9.211-214)
   - Add `tickers` field to BaseDocumentEx [INVRP-173](https://dotdash.atlassian.net/browse/INVRP-173)
   - Add mntl-chop data attribute-based initialization back for backwards compatibility. Allow `<property name="chop-height" value="none" />` for mntl-chop to indicate that the height it being totally controlled by external CSS, but still automatically initiaizing. [GLBE-6134](https://dotdash.atlassian.net/browse/GLBE-6134)
   - Conditionally use ?html encoding on tableRow macro cell contents depending on whether the macro is called from schema or from within page content [INVRP-43](https://dotdash.atlassian.net/browse/INVRP-43)
   - Fix chop null result bug. [GLBE-6134](https://dotdash.atlassian.net/browse/GLBE-6134)

## 3.10.20
 - Change LiftIgniter beacon load event name to help with deferred implementation
 - Propagating release/3.9 to release/3.10 (3.9.206-210)
	- Updated RTBDataLayerTest Consumer method from MntlDataLayerTests.Java.
	- Add recipe sc keywords schema [TS-3474](https://dotdash.atlassian.net/browse/TS-3474)
	- update chop height data set value to camel case. [LW-2154](https://dotdash.atlassian.net/browse/LW-2154)
	- Add favicon to print template so it doesn't 404 [TS-3495](https://dotdash.atlassian.net/browse/TS-3495)
	- Support "out of page" GPT slot definition. Configuring `type` attribute on a slot as `"outofpage"` will define slot with `googletag.defineOutOfPageSlot` instead of default `googletag.defineSlot` (https://developers.google.com/doubleclick-gpt/reference#googletag.defineOutOfPageSlot). [INVRP-103](https://dotdash.atlassian.net/browse/INVRP-103)

## 3.10.19
 - Add back filter for chopHeight in Mntl.Chop.init [GLBE-6134](https://dotdash.atlassian.net/browse/GLBE-6134)

## 3.10.18
 - Fixing bad JS references and poorly scoped social share event handler [GLBE-6130](https://dotdash.atlassian.net/browse/GLBE-6130)

## 3.10.17
 - Update new resolution values for video player [HLTH-4323](https://dotdash.atlassian.net/browse/HLTH-4323)

## 3.10.16
 - Propagating release/3.9 to release/3.10 (3.9.204-205)
 	- Refactor liftIgniter tasks to require injected related articles, deprecated old tasks [HLTH-4438](https://dotdash.atlassian.net/browse/HLTH-4438)
 	- Add null check on tax1 param on ad call test

## 3.10.15 (Introduced Bug. Skip to 3.10.18)
 - Propagating release/3.9 to release/3.10 (3.9.194-203)
   - Updated RTBDataLayerTest Consumer method from MntlDataLayerTests.Java (Note: all verticals need to update their Datalayer Tests)
   - Updated CommonTestMethods in venus test so verticals override it, [see example](https://bitbucket.prod.aws.about.com/projects/FRON/repos/travel/pull-requests/714/diff)
   - Use `?c` freemarker built-in to handle longer numerical time / review numbers in recipe schemas [TS-3476](https://dotdash.atlassian.net/browse/TS-3476)
   - Update FeaturedLink Theme enum for EXTERNAL featured links [LW-2091](https://dotdash.atlassian.net/browse/LW-2091)
   - Tooltip stretches mobile page width prior to JS load, causing full-page redraw [GLBE-6125](https://dotdash.atlassian.net/browse/GLBE-6125)
   - Do not include image key in document schema if the document has no images[INVRP-206](https://dotdash.atlassian.net/browse/INVRP-206)
   - Log Amazon rss service stats [GLBE-6101](https://dotdash.atlassian.net/browse/GLBE-6101) Number of minimum required amazon items in a doc of amazon rss feed is changed [GLBE-6108](https://dotdash.atlassian.net/browse/GLBE-6108)
   - Always fallback to "Other" if no template Id match is found from getContentGroup when parsing document for GTM page view [INVRP-230](https://dotdash.atlassian.net/browse/INVRP-230)
   - Allow chop to start open for noscript browsers, and allow better support for media-query height differences by using CSS vars [GLBE-6105](https://dotdash.atlassian.net/browse/GLBE-6105)
   - set isPvExcluded property to true for pattern library action mappings [GLBE-6076](https://dotdash.atlassian.net/browse/GLBE-6076)

## 3.10.14
 - Migration off of hippdrome's truststore/keystore [GLBE-5311](https://dotdash.atlassian.net/browse/GLBE-5311)

## 3.10.13
 - Various fixes made to allow components to display in pattern library [GLBE-6040](https://dotdash.atlassian.net/browse/GLBE-6040)

## 3.10.12
 - Refactor Mantle social share component [HLTH-4308](https://dotdash.atlassian.net/browse/HLTH-4308)

## 3.10.11
 - Update tooltip.js to use utility functions that were moved to Mantle's fn-utilities.js [HLTH-4361](https://dotdash.atlassian.net/browse/HLTH-4361)

## 3.10.10
 - Propagating release/3.9 to release/3.10 (3.9.188-193)
   - Canonicalize product URLs in Amazon RSS feed [GLBE-6102](https://dotdash.atlassian.net/browse/GLBE-6102)
   - Removed support for Maxmind GeoIP database.  Now getting from Fastly.[GLBE-6020](https://dotdash.atlassian.net/browse/GLBE-6020)
   - Use LiftIgniter v3 [HLTH-4366](https://dotdash.atlassian.net/browse/HLTH-4366)
   - Update venus version to 1.3.9
   - Enable flex ad support for Dynamic Lazy Ads [LW-2004](https://dotdash.atlassian.net/browse/LW-2004)
   - Negative test method for adCall test added [REF-1566](https://dotdash.atlassian.net/browse/REF-1566)

## 3.10.9
 - Fix featuredlink theme class bug [HLTH-4188](https://dotdash.atlassian.net/browse/HLTH-4188)

## 3.10.8
 - Fixing ugcrating recursion bug [GLBE-6098](https://dotdash.atlassian.net/browse/GLBE-6098)

## 3.10.7
 - Add jersey-media-multipart maven dependency

## 3.10.6
 - Documents with template types that are not supported by the vertical will redirect to the vertical homepage [GLBE-6059](https://dotdash.atlassian.net/browse/GLBE-6059)

## 3.10.5
 - Make isUseSmile method protected

## 3.10.4
 - Propagating release/3.9 to release/3.10 (3.9.183-187)
   - added model for commerce summary block [TS-3458](https://dotdash.atlassian.net/browse/TS-3458)
   - Add yieldmo to the ads.txt file [GLBE-6086](https://dotdash.atlassian.net/browse/GLBE-6086)
   - Kafka access logs test has been updated [REF-1545] (https://dotdash.atlassian.net/browse/REF-1545)
   - Changed featured link class property to a list [LW-2091](https://dotdash.atlassian.net/browse/LW-2091)
   - Added FINANCE Algotype (by upgrading to latest Selene)

## 3.10.3
 - Propagating release/3.9 to release/3.10 (3.9.176-182)
   - Add Datawrapper auto-height script (usage optional) [HLTH-4360](https://dotdash.atlassian.net/browse/HLTH-4360)
   - Add GA tracking for video event onFirstFrame [GLBE-6067](https://dotdash.atlassian.net/browse/GLBE-6067)
   - Amazon RSS feed for commerce [GLBE-6018](https://dotdash.atlassian.net/browse/GLBE-6018)
   - Refactored RSS2Transformer to pull the RSS2 data models out into a new RSS2 models subpackage [GLBE-6054](https://dotdash.atlassian.net/browse/GLBE-6054)
   - Add different playerId for inline videos [GLBE-6071](https://dotdash.atlassian.net/browse/GLBE-6071)
   - Propagating release/3.8 to release/3.9 (3.8.101)
     - Update the ads.txt file [TTN-1531](https://dotdash.atlassian.net/browse/TTN-1531)

## 3.10.2
 - Propagating release/3.9 to release/3.10 (3.9.175)
   - Split CHANGELOG into current and archive

## 3.10.1
 - Propagating release/3.9 to release/3.10 (3.9.164-3.9.174)
   - Update a the ads.txt file [GLBE-6061](https://dotdash.atlassian.net/browse/GLBE-6061)
   - Added Test to verify Data Layer analyticsEvent values(mantle rtb test).
   - Fix SBS image schema logic [TRIP-1510](https://dotdash.atlassian.net/browse/TRIP-1510)
   - Minor fix to handle commas for long parsing [TS-3422](https://dotdash.atlassian.net/browse/TS-3422)
   - Added Ads.txt tests to Mantle [GLBE-5707](https://dotdash.atlassian.net/browse/GLBE-5707)
   - Added test and model for Mantle iframe block [REF-1547](https://dotdash.atlassian.net/browse/REF-1547)
   - Added deionsearch offset, limit and remaining fields to cache key to fix broken pagination in search on Travel [TRIP-1512](https://dotdash.atlassian.net/browse/TRIP-1512)
   - Added deionsearch query field to cache key to fix broken search on Travel [TRIP-1512](https://dotdash.atlassian.net/browse/TRIP-1512)
   - Update Lift Igniter Document to handle parsing times from the long format introduced in SC while still supporting legacy time format from old Recipes [TS-3407](https://dotdash.atlassian.net/browse/TS-3407)
   - Add better defaults for RTB lib/bid timing; add hooks to Proctor tests. Default timing metric tracking to "on". Doc cleanup. [GLBE-6038](https://dotdash.atlassian.net/browse/GLBE-6038)
   - Add null check to video schema description [GLBE-6011](https://dotdash.atlassian.net/browse/GLBE-6011)

## 3.10.0
 - Add wrapper div to featuredlink content block to allow more layout options (horizontal and vertical alignment) [HLTH-4188](https://dotdash.atlassian.net/browse/HLTH-4188)
 - Removed Optimizely [GLBE-5904](https://dotdash.atlassian.net/browse/GLBE-5904)
 - Refactor redirect handling [GLBE-5370](https://dotdash.atlassian.net/browse/GLBE-5370)
   - Upgrade to globe-core 3.9.0
     - Move PageNotFoundHandler from servlet.util package to web.filter package
   - Remove obsolete classes CategoriesTask and RelatedArticlesTransformer
   - Remove MantleCanonicalUrlRedirectFilter and RedirectRuleFilter
   - Move redirect logic from MantleCanonicalUrlRedirectFilter and RedirectRuleFilter to MantleRedirectHandler
   - Modify signature of AbstractTemplateNameResolveTask to take DocumentService instead of DocumentTask and remove domain parameter
   - Remove obsolete tasks from DocumentTask
 - Converted `class` models from `property` to `list` [GLBE-5719](https://dotdash.atlassian.net/browse/GLBE-5719)
 - Upgraded to mantle-grunt 4.1 to remove the `mntl-modernizr` task. Now has built in custom file. [GLBE-5831](https://dotdash.atlassian.net/browse/GLBE-5831)
 - Delete reference to seleneBaseUrl selene.api.base.url. Base url was not secure. [GLBE-5738](https://dotdash.atlassian.net/browse/GLBE-5738)
 - Delete Brightcove video player and replace references with JWPlayer [GLBE-5822](https://dotdash.atlassian.net/browse/GLBE-5822)
 - Remove Selene API dependencies [GLBE-5658](https://dotdash.atlassian.net/browse/GLBE-5658)
 - Remove Selene client dependency and migrate to the one in Hippodomre.[GLBE-5659](https://dotdash.atlassian.net/browse/GLBE-5659)
 - Use HttpStatus static variable for CSRF failure response code instead of hardcoded 403 int
 - Separated some utilties functions into dom-utilities and fn-utilities [GLBE-5785](https://dotdash.atlassian.net/browse/GLBE-5785)
 - Change template cache durations to 1800s (30 minutes) to match CSRF token duration [GLBE-5188](https://dotdash.atlassian.net/browse/GLBE-5188)
 - Standardize version of the apache commons libs [GLBE-5729](https://dotdash.atlassian.net/browse/GLBE-5729)
 - Remove TemplateType, AboutUrlData, UrlInfoV2 from hippodrome [GLBE-5862](https://dotdash.atlassian.net/browse/GLBE-5862)
 - Replace mntl-layout-html with more opinionated mntl-html component [GLBE-5927](https://dotdash.atlassian.net/browse/GLBE-5927)
 - Removed irrelevant MantleFreeMarkerRenderingEngineTest
 - Big cleanup of Journey code. [GLBE-5610](https://dotdash.atlassian.net/browse/GLBE-5610)

## Release 3.9.x

## 3.9.329
 - Update ascsubtag to use shortened requestId [CMRC-47](https://dotdash.atlassian.net/browse/CMRC-47)

## 3.9.328
 - Updated Jquery to 3.4.0 [GLBE-6473](https://dotdash.atlassian.net/browse/GLBE-6473)

## 3.9.327
 - Added beauty verticals in old Selene API to support backward compatibility for LiveAbout migration [BEAUT-568](https://dotdash.atlassian.net/browse/BEAUT-568)

## 3.9.326
 - Quick fix for unescaped ampersands to not take down a server (no ticket, see https://dotdash.slack.com/archives/CFDEY09PB/p1553703170057200)

## 3.9.325
 - Fixed pattern library example for ad tables to reflect actual usage [FIN-181](https://dotdash.atlassian.net/browse/FIN-181)

## 3.9.324
 - Update ads.txt [GLBE-6400](https://dotdash.atlassian.net/browse/GLBE-6400)

## 3.9.323
 - Update Selene version to get support for RELIGION vertical [REF-1769](https://dotdash.atlassian.net/browse/REF-1769)

## 3.9.322
 - Added mntl-ad-table component to be used to create performance marketing ad tables [FIN-181](https://dotdash.atlassian.net/browse/FIN-181)

## 3.9.321
 - Updating MntlSchemaCompareTest to show differences when encountered between schemas [FIN-230](https://dotdash.atlassian.net/browse/FIN-230)

## 3.9.320
 - [HackerOne] stop allowing url query param to hijack the request context [GLBE-6367](https://dotdash.atlassian.net/browse/GLBE-6367)

## 3.9.319
 - Updating mntl-sc-page to use render utils function for resolving block names [FIN-184](https://dotdash.atlassian.net/browse/FIN-184)

## 3.9.318
 - Added more verbose JWPlayer error logging to message field [GLBE-6363](https://dotdash.atlassian.net/browse/GLBE-6363)

## 3.9.317
 - Added test for mntl-sc-block that has class mntl-sc-block-html [BEAUT-134](https://dotdash.atlassian.net/browse/BEAUT-134)
 - Added annotation Css Selector for getting component with css selector [DV-116](https://dotdash.atlassian.net/browse/DV-116)

## 3.9.316
 - Upgrade venus version to 1.3.12 [REF-1787](https://dotdash.atlassian.net/browse/REF-1787)

## 3.9.315
 - Making css loading callback optional in jquery utilities file [FIN-117](https://dotdash.atlassian.net/browse/FIN-117)

## 3.9.314
 - Updating overflow handling for katex elements to allow long formula viewing on mobile / avoid overlap with ads on desktop [FIN-137](https://dotdash.atlassian.net/browse/FIN-137)
 - Assigning webkit scrollbar styling for katex elements to ensure user is aware overflow content exists
 - Updating yarn.lock file to address hash mismatch in customizr library

## 3.9.313
 - Ensure a unique id for newsletter signup input when there are multiple instances on the same page for accessibility [HLTH-4723](https://dotdash.atlassian.net/browse/HLTH-4723)

## 3.9.312
 - Fix incorrect concatenation in the Cross-Origin filter that prevented www.[domain].com from being a valid iframe parent [GLBE-6326](https://dotdash.atlassian.net/browse/GLBE-6326)

## 3.9.311
 - Add aria-label to star ratings for accessibility [HLTH-4669](https://dotdash.atlassian.net/browse/HLTH-4669)

## 3.9.310
 - Various pattern library fixes
   - Use component macro for setting proper id on quiz mntl-disclaimer [GLBE-6284](https://dotdash.atlassian.net/browse/GLBE-6284)
   - Fix mntl-made-it and mntl-star-rating rendering [GLBE-6285](https://dotdash.atlassian.net/browse/GLBE-6285)
   - Add additional properties to componentList response so ComponentPLTest can filter out based on previewType [GLBE-6293](https://dotdash.atlassian.net/browse/GLBE-6293)

## 3.9.309
 - Updating mntl-katex js to render formulas [FIN-117](https://dotdash.atlassian.net/browse/FIN-117)

## 3.9.308
 - Junit updates for Amzon RSS service  [GLBE-6320](https://dotdash.atlassian.net/browse/GLBE-6320)

## 3.9.307
 - Better error handling and retry when Amazon product API is unavailable [GLBE-6282](https://dotdash.atlassian.net/browse/GLBE-6282)

## 3.9.306
 - Fix pom version (No ticket)

## 3.9.305
 - Remove years from item titles in Amazon RSS bug fix (No ticket)

## 3.9.304
 - Fix git proctor issue where latest test changes are not pulled at runtime [GLBE-6317](https://dotdash.atlassian.net/browse/GLBE-6317)

## 3.9.303
 - Add all templates to unified schema main entity mapping [GLBE-6292](https://dotdash.atlassian.net/browse/GLBE-6292)

## 3.9.302
 - Fix server startup issue by defaulting bing appId key to null

## 3.9.301
 - Removing GPT Tranches and AB test file [GLBE-6207](https://dotdash.atlassian.net/browse/GLBE-6207)

## 3.9.300
 - Fix for AccessLogs Tests failures due to Session ID/User ID changes (https://dotdash.atlassian.net/browse/HLTH-4709).
 - Updated to jUnit 4.12, Hamcrest 2.1

## 3.9.299 (skip to 3.9.302)
 - Add `mntl-bing-afs` component and BingAdsSearchService to get text ads for search pages [REF-1646](https://dotdash.atlassian.net/browse/REF-1646)

## 3.9.298
 - Adding Badge Field to metadata  [TS-3679](https://dotdash.atlassian.net/browse/TS-3679)

## 3.9.297
 - Adding "kw_only" querystring to url should remove all targeting on ad calls [GLBE-6217](https://dotdash.atlassian.net/browse/GLBE-6217)
 - Automation changes for Adding "kw_only" querystring to url should remove all targeting on ad calls. [GLBE-6280](https://dotdash.atlassian.net/browse/GLBE-6280)
 - Made 'custParams', 'scpParams' and 'prevScpParams' public in UrlParams class

## 3.9.296
 - Separate ComparisonList class and have as MntlComponent for code reuse [INV-611](https://dotdash.atlassian.net/browse/INVRP-611)

## 3.9.295
 - Moving work from 3.9.291 out to test class. Originally had checks as part of model and after discussion figured out better way to run in test class

## 3.9.294
 - jackson-databind security fix [GLBE-6291](https://dotdash.atlassian.net/browse/GLBE-6291)

## 3.9.293
 - Removed padding and height calculation "hack" that overrode component height in the pattern library [GLBE-6268](https://dotdash.atlassian.net/browse/GLBE-6268)

## 3.9.292
 - Backport from 3.10.68: Remove whitespace within anchor and span tags as this causes issues when using with CSS pseudo-elements [HLTH-4613](https://dotdash.atlassian.net/browse/HLTH-4613)

## 3.9.291
 - Adding tests taking from travel star ratings and putting into mantle for reuse in finance.

## 3.9.290
 - Auto updating feature for Amazon RSS [CMRC-13](https://dotdash.atlassian.net/browse/CMRC-13)

## 3.9.289
 - Adding venus component to support Bing ads [REF-1665](https://dotdash.atlassian.net/browse/REF-1665)

## 3.9.288
 - Adding missing UPNEXT featured link enum [TRIP-1608](https://dotdash.atlassian.net/browse/TRIP-1608)

## 3.9.287
 - Removed width from dotdash-family-nav so parent containers on the vertical overrides the element for their styling. [GLBE-6277](https://dotdash.atlassian.net/browse/GLBE-6277)

## 3.9.286
 - RequestId (in turn SessionId, UserId, GA Session Id) is generated using Java UUID [GLBE-6226](https://dotdash.atlassian.net/browse/GLBE-6226)

## 3.9.285
 - Fixing bug when amazon commerce service is null, this is temporary fix to avoid server start-up failures. Will be better handled soon.

## 3.9.284
 - Adding location tags to Review SC block links [INVRP-567](https://dotdash.atlassian.net/browse/INVRP-567)

## 3.9.283
 - Revert incomplete feature

## 3.9.282 [DO NOT USE]
 - Incomplete feature

## 3.9.281
 - Adding sc block component for Review content [INVRP-567](https://dotdash.atlassian.net/browse/INVRP-567)

## 3.9.280
 - Adding BROKERAWARDS FeaturedLink theme [INVRP-595](https://dotdash.atlassian.net/browse/INVRP-595)

## 3.9.279
 - Add `mntl-sc-block-location__pre-content` location in location content block. Rename `top` location to `mntl-sc-block-location__media` [TRIP-1065](https://dotdash.atlassian.net/browse/TRIP-1065)

## 3.9.278
 - Adding unified schema for Review content [INVRP-568](https://dotdash.atlassian.net/browse/INVRP-568)

## 3.9.277
 - On article-feeback, alter js scoping of `js-open-form-button` to allow anywhere within component [TRIP-1051](https://dotdash.atlassian.net/browse/TRIP-1051)

## 3.9.276
 - Remove legacy S3 logs [GLBE-5544](https://dotdash.atlassian.net/browse/GLBE-5544)

## 3.9.275
 - Making expectedHeaderEntries protected so it can be modified in inherited classes

## 3.9.274
 - Added in logic inside Mantle Search Task to allow for empty queries [GLBE-6175](https://dotdash.atlassian.net/browse/GLBE-6175)

## 3.9.273
 - Backport from 3.10.46: Fix itemListOrder macro for freemarker error

## 3.9.272
 - Remove final keyword from video event click tracking variable

## 3.9.271
 - Use meta data for intro copy in RSS feed when the Intro is empty [CMRC-11](https://dotdash.atlassian.net/browse/CMRC-11)

## 3.9.270
 - Add `jnyroot` key to baseSlotTargeting to associate all journey documents under one key/val pair for adops targeting [TS-3662](https://dotdash.atlassian.net/browse/TS-3662)

## 3.9.269 [Skip to 3.9.304 _if_ migrating to git]
 - Upgrade to the latest version of proctor and add git support [GLBE-4895](https://dotdash.atlassian.net/browse/GLBE-4895)
   - For information on migrating to git see https://dotdash.atlassian.net/wiki/spaces/TECH/pages/391839786/Migrate+Proctor+from+SVN+to+GIT

## 3.9.268
 - Fixing duped HTML Unit dependency in 3.9 and upgrade to 2.33.

## 3.9.267
 - Remove the year and the preposition before it from the Amazon RSS feed [CMRC-10](https://dotdash.atlassian.net/browse/CMRC-10)

## 3.9.266
 - Added support for 'name' attribute in LocationService. [TRIP-1567](https://dotdash.atlassian.net/browse/TRIP-1567)

## 3.9.265
 - Changelog Fixes

## 3.9.264
 - Upgraded to Venus 1.3.11

## 3.9.263
 - Moved viewType from TaxonomyDocumentEx to super BaseDocumentEx class. Upgraded Selene version to get new "Best For X" Enum from Selene API [INVRP-500](https://dotdash.atlassian.net/browse/INVRP-500)

## 3.9.262
 - Updated Ads.txt to use adtech.com [GLBE-6218](https://dotdash.atlassian.net/browse/GLBE-6218)

## 3.9.261
 - Add venus test for testing all globe components in the pattern library [GLBE-6135](https://dotdash.atlassian.net/browse/GLBE-6135)

## 3.9.260
 - Adding Review document and updating globe core for hippodrome change [GLBE-6184](https://dotdash.atlassian.net/browse/GLBE-6184)
 - Adding Structured Content block for Review data [GLBE-6187](https://dotdash.atlassian.net/browse/GLBE-6187)

## 3.9.259
 - Fix bug introduced in 3.9.255 to wiped all svg images.

## 3.9.258
 - Update stepLists, stepsContent to list and added stepsHeader in Structure Content Project Steps Component

## 3.9.257
 - Update inline video click functionality

## 3.9.256
 - SC Tables, remove extra row on the top when title is not set and the First row is heading. [LW-2180](https://dotdash.atlassian.net/browse/LW-2180)

## 3.9.255
 - Remove Tranches in GPT for AB Testing [GLBE-6129](https://dotdash.atlassian.net/browse/GLBE-6129)

## 3.9.254
 - Support `<svg>` tags as includable, deduped resources, similar to handling for `<script>` and `<stylesheet>` tags.

## 3.9.253
- Improving existing GPT Javascript unit tests [GLBE-6128](https://dotdash.atlassian.net/browse/GLBE-6128)

## 3.9.252
 - Add end points to return all components registered in globe [GLBE-6188](https://dotdash.atlassian.net/browse/GLBE-6188)

## 3.9.251
 - Move all advertising parameters for video onto the cust_params key. [GLBE-6176](https://dotdash.atlassian.net/browse/GLBE-6176) **Warning** this change will break video tests. Please see (https://bitbucket.prod.aws.about.com/projects/FRON/repos/reference/pull-requests/334/overview) for guidance on how to fix the tests.

## 3.9.250
 - SC Tables, remove extra row on the top when title is not set. [LW-2180](https://dotdash.atlassian.net/browse/LW-2180)

## 3.9.249
 - New unified json ld schema [GLBE-6069](https://dotdash.atlassian.net/browse/GLBE-6069)
 - Add MockTaxeneRelationTask and mockBreadcrumb task method
 - Fixing Unified Recipe Rating Section for Schema [GLBE-6172](https://dotdash.atlassian.net/browse/GLBE-6172)

## 3.9.248
 - SC Tables, show the horizontal scroll only when needed. [LW-2176](https://dotdash.atlassian.net/browse/LW-2176)

## 3.9.247
 - Ignore optional title and caption properties in SC table component for missing content [INVRP-434](https://dotdash.atlassian.net/browse/INVRP-434)

## 3.9.246
 - Automation for smart captions to video players [LW-2167](https://dotdash.atlassian.net/browse/LW-2167)

## 3.9.245
 - Fixed bug in gpt-definition.evaluated.js [GLBE-6173](https://dotdash.atlassian.net/browse/GLBE-6173)

## 3.9.244
 - Update README.md

## 3.9.243
 - Fix inlineVideo inconsistent tests [LW-2170](https://dotdash.atlassian.net/browse/LW-2170)
 - Remove unnecessary mock documents from component info tags [GLBE-6110](https://dotdash.atlassian.net/browse/GLBE-6110)

## 3.9.242 (Build failed skip to 3.9.243)

## 3.9.241
 - Added KaTeX JS library to render formulas [REF-1546](https://dotdash.atlassian.net/browse/REF-1546)

## 3.9.240
 - Fix another unescaped character in article-body-sc-block-table [INVRP-431](https://dotdash.atlassian.net/browse/INVRP-431)

## 3.9.239
 - Allow `document.guestAuthor` to take precedence over `author` in document-schema [INVRP-410](https://dotdash.atlassian.net/browse/INVRP-410)

## 3.9.238
 - Added captions to jw player [LW-2129](https://dotdash.atlassian.net/browse/LW-2129)

## 3.9.237
 - Add "/facebookshareredirect.htm" to actions.xml to support post-share redirects without verticals having to re-implement [GLBE-6168](https://dotdash.atlassian.net/browse/GLBE-6168)

## 3.9.236
 - Fix schema errors for schema-table and article-body-sc-block-table  [INVRP-431](https://dotdash.atlassian.net/browse/INVRP-431)

## 3.9.235
 - Refactor sticky billboard logic on browser resize, changed reference classname as part of 3.9.223.  [LW-2165](https://dotdash.atlassian.net/browse/LW-2165)

## 3.9.234
 - Update ads.txt for media.net [GLBE-6167](https://dotdash.atlassian.net/browse/GLBE-6167)

## 3.9.233
 - Add <@component> wrapper to disqus comment count script so it can be deferred [TS-3562](https://dotdash.atlassian.net/browse/TS-3562)

## 3.9.232
 - Fix logic for adding commas in inline-images-structured-content [INVRP-410](https://dotdash.atlassian.net/browse/INVRP-410)

## 3.9.231
 - Added venus component for feature link content block  [TS-3586](https://dotdash.atlassian.net/browse/TS-3586)

## 3.9.230
  - Version skipped due to accidental commit.

## 3.9.229
  - Version skipped due to accidental commit.

## 3.9.228
 - Added nullcheck for missing `showTitle` in videos.  [INVRP-370](https://dotdash.atlassian.net/browse/INVRP-370)

## 3.9.227
 - Updated venus DragAndDrop Slider method to work for both inline videos and footer videos

## 3.9.226
 - Added MntlFaviconTest

## 3.9.225
 - Remove duplicate attribute loop in the <@a> macro [GLBE-6124](https://dotdash.atlassian.net/browse/GLBE-6124)

## 3.9.224
 - Added script/stylesheet to make embeded youtube videos scale in a 16:9 ratio [REF-1608](https://dotdash.atlassian.net/browse/REF-1608)

## 3.9.223
 - Resolving multi-ad billboard layout issues [INVRP-321](https://dotdash.atlassian.net/browse/INVRP-321)

## 3.9.222
 - Add `readyForThirdPartyTracking` event. Fired from gtm.evaluated into the dataLayer to trigger third party tracking. [GLBE-6089](https://dotdash.atlassian.net/browse/GLBE-6089)

## 3.9.221
 - Don't give chop-content element the `is-chopped` class when `chop-height` property isn't provided [GLBE-6145](https://dotdash.atlassian.net/browse/GLBE-6145)

## 3.9.220
 - Add `card__top` location in card.ftl for child component injection [INVRP-335](https://dotdash.atlassian.net/browse/INVRP-335)

## 3.9.219
 - Improved canonicalization logic for investopedia [INVRP-267](https://dotdash.atlassian.net/browse/INVRP-267)

## 3.9.218
 - update thumbor proxy to allow headers up to 8k fixing an issue with long atlas cookies

## 3.9.217
 - Switch to lastPublished from updatedDate in amazon rss service [GLBE-6070](https://dotdash.atlassian.net/browse/GLBE-6070)

## 3.9.216
 - Change name of field used to generate recipe sc keywords schema [TS-3474](https://dotdash.atlassian.net/browse/TS-3474)

## 3.9.215
 - Made methods in MntlCommontTestMethods more generic

## 3.9.214
 - Add `tickers` field to BaseDocumentEx [INVRP-173](https://dotdash.atlassian.net/browse/INVRP-173)

## 3.9.213
 - Add mntl-chop data attribute-based initialization back for backwards compatibility. Allow `<property name="chop-height" value="none" />` for mntl-chop to indicate that the height it being totally controlled by external CSS, but still automatically initiaizing. [GLBE-6134](https://dotdash.atlassian.net/browse/GLBE-6134)

## 3.9.212
 - Conditionally use ?html encoding on tableRow macro cell contents depending on whether the macro is called from schema or from within page content [INVRP-43](https://dotdash.atlassian.net/browse/INVRP-43)

## 3.9.211
 - Fix chop null result bug. [GLBE-6134](https://dotdash.atlassian.net/browse/GLBE-6134)

## 3.9.210
 - Updated RTBDataLayerTest Consumer method from MntlDataLayerTests.Java.

## 3.9.209
 - Add recipe sc keywords schema [TS-3474](https://dotdash.atlassian.net/browse/TS-3474)

## 3.9.208
 - update chop height data set value to camel case. [LW-2154](https://dotdash.atlassian.net/browse/LW-2154)

## 3.9.207
 - Add favicon to print template so it doesn't 404 [TS-3495](https://dotdash.atlassian.net/browse/TS-3495)

## 3.9.206
 - Support "out of page" GPT slot definition. Configuring `type` attribute on a slot as `"outofpage"` will define slot with `googletag.defineOutOfPageSlot` instead of default `googletag.defineSlot` (https://developers.google.com/doubleclick-gpt/reference#googletag.defineOutOfPageSlot). [INVRP-103](https://dotdash.atlassian.net/browse/INVRP-103)

## 3.9.205
 - Refactor liftIgniter tasks to require injected related articles, deprecated old tasks [HLTH-4438](https://dotdash.atlassian.net/browse/HLTH-4438)

## 3.9.204
 - Add null check on tax1 param on ad call test

## 3.9.203
 - Updated RTBDataLayerTest Consumer method from MntlDataLayerTests.Java (Note: all verticals need to update their Datalayer Tests)

## 3.9.202
 - Updated CommonTestMethods in venus test so verticals override it, [see example](https://bitbucket.prod.aws.about.com/projects/FRON/repos/travel/pull-requests/714/diff)

## 3.9.201
 - Use `?c` freemarker built-in to handle longer numerical time / review numbers in recipe schemas [TS-3476](https://dotdash.atlassian.net/browse/TS-3476)

## 3.9.200
 - Update FeaturedLink Theme enum for EXTERNAL featured links [LW-2091](https://dotdash.atlassian.net/browse/LW-2091)

## 3.9.199
 - Tooltip stretches mobile page width prior to JS load, causing full-page redraw [GLBE-6125](https://dotdash.atlassian.net/browse/GLBE-6125)

## 3.9.198
 - Do not include image key in document schema if the document has no images[INVRP-206](https://dotdash.atlassian.net/browse/INVRP-206)

## 3.9.197
 - Log Amazon rss service stats [GLBE-6101](https://dotdash.atlassian.net/browse/GLBE-6101)
   Number of minimum required amazon items in a doc of amazon rss feed is changed [GLBE-6108](https://dotdash.atlassian.net/browse/GLBE-6108)

## 3.9.196
 - Always fallback to "Other" if no template Id match is found from getContentGroup when parsing document for GTM page view [INVRP-230](https://dotdash.atlassian.net/browse/INVRP-230)

## 3.9.195
 - Allow chop to start open for noscript browsers, and allow better support for media-query height differences by using CSS vars [GLBE-6105](https://dotdash.atlassian.net/browse/GLBE-6105)

## 3.9.194
 - set isPvExcluded property to true for pattern library action mappings [GLBE-6076](https://dotdash.atlassian.net/browse/GLBE-6076)

## 3.9.193
 - Canonicalize product URLs in Amazon RSS feed [GLBE-6102](https://dotdash.atlassian.net/browse/GLBE-6102)

## 3.9.192
 - Removed support for Maxmind GeoIP database.  Now getting from Fastly.[GLBE-6020](https://dotdash.atlassian.net/browse/GLBE-6020)

## 3.9.191
 - Use LiftIgniter v3 [HLTH-4366](https://dotdash.atlassian.net/browse/HLTH-4366)

## 3.9.190
 - Update venus version to 1.3.9

## 3.9.189
 - Enable flex ad support for Dynamic Lazy Ads [LW-2004](https://dotdash.atlassian.net/browse/LW-2004)

## 3.9.188
 - Negative test method for adCall test added [REF-1566](https://dotdash.atlassian.net/browse/REF-1566)

## 3.9.187
 - added model for commerce summary block [TS-3458](https://dotdash.atlassian.net/browse/TS-3458)

## 3.9.186
 - Propagating release/3.8 to release/3.9 (3.8.102)
   - Add yieldmo to the ads.txt file [GLBE-6086](https://dotdash.atlassian.net/browse/GLBE-6086)

## 3.9.185
- Kafka access logs test has been updated [REF-1545] (https://dotdash.atlassian.net/browse/REF-1545)

## 3.9.184
 - Changed featured link class property to a list [LW-2091](https://dotdash.atlassian.net/browse/LW-2091)

## 3.9.183
 - Added FINANCE Algotype (by upgrading to latest Selene)

## 3.9.182
 - Add Datawrapper auto-height script (usage optional) [HLTH-4360](https://dotdash.atlassian.net/browse/HLTH-4360)

## 3.9.181
 - Add GA tracking for video event onFirstFrame [GLBE-6067](https://dotdash.atlassian.net/browse/GLBE-6067)

## 3.9.180
 - Amazon RSS feed for commerce [GLBE-6018](https://dotdash.atlassian.net/browse/GLBE-6018)
 - Refactored RSS2Transformer to pull the RSS2 data models out into a new RSS2 models subpackage [GLBE-6054](https://dotdash.atlassian.net/browse/GLBE-6054)

## 3.9.179
 - Add different playerId for inline videos [GLBE-6071](https://dotdash.atlassian.net/browse/GLBE-6071)

## 3.9.178
 - Propagating release/3.8 to release/3.9 (3.8.101)
   - Update the ads.txt file [TTN-1531](https://dotdash.atlassian.net/browse/TTN-1531)

## 3.9.177
 - Version skipped due to build error.

## 3.9.176
 - Adding a method which gives back stream of type AbstractStructuredContentContentEx<?> from outro in ListSc [TRIP-1374](https://dotdash.atlassian.net/browse/TRIP-1374)

## 3.9.175
 - Split CHANGELOG into current and archive

## 3.9.174
 - Propagating release/3.8 to release/3.9 (3.8.100)
   - Update a the ads.txt file [GLBE-6061](https://dotdash.atlassian.net/browse/GLBE-6061)

## 3.9.173
- Added Test to verify Data Layer analyticsEvent values(mantle rtb test).

## 3.9.172
- Fix SBS image schema logic [TRIP-1510](https://dotdash.atlassian.net/browse/TRIP-1510)

## 3.9.171
- Minor fix to handle commas for long parsing [TS-3422](https://dotdash.atlassian.net/browse/TS-3422)

## 3.9.170
- Added Ads.txt tests to Mantle [GLBE-5707](https://dotdash.atlassian.net/browse/GLBE-5707)

## 3.9.169
- Added test and model for Mantle iframe block [REF-1547](https://dotdash.atlassian.net/browse/REF-1547)

## 3.9.168
- Added deionsearch offset, limit and remaining fields to cache key to fix broken pagination in search on Travel [TRIP-1512](https://dotdash.atlassian.net/browse/TRIP-1512)

## 3.9.167
- Added deionsearch query field to cache key to fix broken search on Travel [TRIP-1512](https://dotdash.atlassian.net/browse/TRIP-1512)

## 3.9.166
 - Update Lift Igniter Document to handle parsing times from the long format introduced in SC while still supporting legacy time format from old Recipes [TS-3407](https://dotdash.atlassian.net/browse/TS-3407)

## 3.9.165
 - Add better defaults for RTB lib/bid timing; add hooks to Proctor tests. Default timing metric tracking to "on". Doc cleanup. [GLBE-6038](https://dotdash.atlassian.net/browse/GLBE-6038)

## 3.9.164
 - Add null check to video schema description [GLBE-6011](https://dotdash.atlassian.net/browse/GLBE-6011)

## 3.9.163
- Fixed bug for schema of inline images on SBS template [TRIP-1503](https://dotdash.atlassian.net/browse/TRIP-1503)

## 3.9.162
- Add MntlScBlockTableComponent to venus model [GLBE-5960](https://dotdash.atlassian.net/browse/GLBE-5960)

## 3.9.161
 - Revert premature commit

## 3.9.160 [Failed build]
 - Premature commit

## 3.9.159
 - Schema test added [REF-1529](https://dotdash.atlassian.net/browse/REF-1529)

## 3.9.158
 - Moved cache keys to new Redis cache key to help avoid hash collisions [GLBE-6021](https://dotdash.atlassian.net/browse/GLBE-6021)

## 3.9.157
- Fix bug introduced in 3.9.143 for schema of inline images on list [LW-2085](https://dotdash.atlassian.net/browse/LW-2085)

## 3.9.156
- Use whitelist for location block directions and website links [TRIP-1500](https://dotdash.atlassian.net/browse/TRIP-1500)

## 3.9.155
- Use heading block text as list-sc schema itemListElement name [TRIP-1497](https://dotdash.atlassian.net/browse/TRIP-1497)

## 3.9.154
 - Added Table structured content data model [GLBE-5954](https://dotdash.atlassian.net/browse/GLBE-5954), [GLBE-5952](https://dotdash.atlassian.net/browse/GLBE-5952)

## 3.9.153
 - Added RTB latency prop and Proctor test [GLBE-6026](https://dotdash.atlassian.net/browse/GLBE-6026)

## 3.9.152
- Make the lift igniter widget name configurable [HLTH-4285](https://dotdash.atlassian.net/browse/HLTH-4285)

## 3.9.151
- Add querystring params added by affiliate tagger to click-tracking URLs for commerce buttons [LW-2065](https://dotdash.atlassian.net/browse/LW-2065)

## 3.9.150
 - Fixing ?json_string escaping in schema Ftls for sc blocks [GLBE-6019](https://dotdash.atlassian.net/browse/GLBE-6019)

## 3.9.149
 - Added Click event to background for SC Image Gallery [GLBE-5965](https://dotdash.atlassian.net/browse/GLBE-5965)

## 3.9.148
 - Update robots.txt to noindex globe testing query params [GLBE-6016](https://dotdash.atlassian.net/browse/GLBE-6016)

## 3.9.147
 - Escape single quotes, use doc title [TS-3338](https://dotdash.atlassian.net/browse/TS-3338)

## 3.9.146
 - Fix javascript for ratings pattern library [GLBE-5969](https://dotdash.atlassian.net/browse/GLBE-5969)

## 3.9.145
 - Added FINANCE vertical to older version of Selene API [GLBE-6023] (https://dotdash.atlassian.net/browse/GLBE-6023)

## 3.9.144
 - Removed old jwplayer settings [GLBE-5900](https://dotdash.atlassian.net/browse/GLBE-5900)

## 3.9.143
 - Add images to article schema for legacy content types [GLBE-6009](https://dotdash.atlassian.net/browse/GLBE-6009)

## 3.9.142
 - Silently collapse mntl-schema-breadcrumblist if the node level is 0, e.g. homepage [GLBE-6012](https://dotdash.atlassian.net/browse/GLBE-6012)

## 3.9.141
 - Add support for new nutrition info display property [TS-3236](https://dotdash.atlassian.net/browse/TS-3236)

## 3.9.140
 - Add automation to support commerce blocks in List SC outro and upgrading venus version - 1.3.6 [TS-3325](https://dotdash.atlassian.net/browse/TS-3325)

## 3.9.139
 - Point package.json to proper version of JustifiedColumns [GLBE-6005](https://dotdash.atlassian.net/browse/GLBE-6005)

## 3.9.138
 - Support custom GA tracking category for commerce widget SC block. [LW-1989](https://dotdash.atlassian.net/browse/LW-1989)

## 3.9.137
 - Bumped up globe-core version for bug fix in Ehcache in-memory objects size stats [GLBE-5924](https://dotdash.atlassian.net/browse/GLBE-5924)

## 3.9.136
 - Fix video shema JSON error [GLBE-6010](https://dotdash.atlassian.net/browse/GLBE-6010)

## 3.9.135
 - Dedupe resources by group order [GLBE-5889](https://dotdash.atlassian.net/browse/GLBE-5889)

## 3.9.134
 - Add @location to mntl-sc-block-location ftl [TRIP-1437](https://dotdash.atlassian.net/browse/TRIP-1437)

## 3.9.133
 - Refactor steps rendering to output OL instead of nested DIVs [GLBE-5984](https://dotdash.atlassian.net/browse/GLBE-5984)

## 3.9.132
 - Inline video event tracking test added [LW-2018](https://dotdash.atlassian.net/browse/LW-2018)

## 3.9.131
 - Multiple changes to schema for SEO [GLBE-5977](https://dotdash.atlassian.net/browse/GLBE-5977)

## 3.9.130
 - Add lat/lng to GooglePlaceModel and render as data attributes on mntl-sc-block-location component [TRIP-592](https://dotdash.atlassian.net/browse/TRIP-952)

## 3.9.129
 - Add tooltip component [HLTH-4204] (https://dotdash.atlassian.net/browse/HLTH-4204)

## 3.9.128
- Split out StarRatingComponent into it's own class MntlScBlockStarRatingComponent

## 3.9.127
 - Automation update for MntlJWPlayer component

## 3.9.126
 - Temporary workaround to address a prod bug involving missing headings in article schema. It should be revisited later in GLBE-5982 [GLBE-5977](https://dotdash.atlassian.net/browse/GLBE-5977)

## 3.9.125
 - Add configurable related article/liftIgniter task and make request fields overridable [HLTH-4121](https://dotdash.atlassian.net/browse/HLTH-4121)

## 3.9.124
 - Add LEARNMORE to Theme enum for FeaturedLink [HLTH-4173](https://dotdash.atlassian.net/browse/HLTH-4173)

## 3.9.123
 - Correcting Disqus count.js script id [TS-3147](https://dotdash.atlassian.net/browse/TS-3147)

## 3.9.122
 - Automation updates for MntlDisqusTest [TS-3292](https://dotdash.atlassian.net/browse/TS-3292)

## 3.9.121
 - Added shortBio field to AuthorEx [LW-1990](https://dotdash.atlassian.net/browse/LW-1990)

## 3.9.120
 - Add Term Structured Content Base [GLBE-5932](https://dotdash.atlassian.net/browse/GLBE-5932)

## 3.9.119
 - Replace commerce image with image from Atlas if available [LW-1959](https://dotdash.atlassian.net/browse/LW-1959)

## 3.9.118
 - Automation inline video updates

## 3.9.117
 - Updated liftIgniter script to be evaluated. Set defaults to maintain backwards compatability [HLTH-4121](https://dotdash.atlassian.net/browse/HLTH-4121)

## 3.9.116
 - Removed getElement() Method from MntlListScItemComponent.Java class.

## 3.9.115
 - Added new methods(retailer(),hasCommerceImage(),hasCommerceButtons()) for MntlScBLockCommerceButton and MntlListScItemComponent components.

## 3.9.114
 - Bumped globe core version in prep for 3.10 mantle release.  No material changes

## 3.9.113
 - Added locations after quiz questions for ad insertion. [REF-1477](https://dotdash.atlassian.net/browse/REF-1477)

## 3.9.112
 - Add task method signature for mockDocument task to fix mantle-ref [GLBE-5942](https://dotdash.atlassian.net/browse/GLBE-5942)

## 3.9.111
 - Updated mntl-sc-block-inlinevideo__duration to use mntl-block component. [LW-1948](https://dotdash.atlassian.net/browse/LW-1948)

## 3.9.110
 - Update locators for MntlScBlockCalloutComponent. [LW-1987](https://dotdash.atlassian.net/browse/LW-1987)

## 3.9.109
 - Fixed cursor on img hover when commerce images are in article outro [LW-2000](https://dotdash.atlassian.net/browse/LW-2000)

## 3.9.108
 - Update to globe-core 3.8.29 to add support for getting module by id; can be used to fix fonts being pulled in twice on microverticals [GLBE-5825](https://dotdash.atlassian.net/browse/GLBE-5825)

## 3.9.107
  - Update RTB Amazon bidder to pass in FB Placement values if map object is provided. [GLBE-5928](https://dotdash.atlassian.net/browse/GLBE-5928)

## 3.9.106
 - Add metric for number of in-memory cache objects + ehcache influx metrics format corrected (via globe-core 3.8.28) [GLBE-5922](https://dotdash.atlassian.net/browse/GLBE-5922)

## 3.9.105
 - Propagating release/3.8 to release/3.9 (3.8.98)
   - Add opinionated boilerplate html component [GLBE-5927](https://dotdash.atlassian.net/browse/GLBE-5927)

## 3.9.104
 - Add convenient method that filters for specified content type on LISTSC [LW-1965](https://dotdash.atlassian.net/browse/LW-1965)

## 3.9.103
 - Only run liftIgniter js once beacon has loaded [TS-3204](https://dotdash.atlassian.net/browse/TS-3204)

## 3.9.102
 - Fix video id for pattern library. [GLBE-5920](https://dotdash.atlassian.net/browse/GLBE-5920)

## 3.9.101
- GdprNotificationBanner test has been fixed [LW-1967](https://dotdash.atlassian.net/browse/LW-1967)

## 3.9.100
 - Share mantle scss resources via npm [GLBE-5937](https://dotdash.atlassian.net/browse/GLBE-5937)

## 3.9.99
 - Update to globe-core v3.8.27: Use hippodrome 2.2.4 to support FINANCE vertical url data

## 3.9.98
 - Updating venus version 1.3.5

## 3.9.97
 - Make commerce ajax call return same commerceModel for duplicate Amazon commerce block in SC content well [LW-1953](https://dotdash.atlassian.net/browse/LW-1953)

## 3.9.96
 - added common test to verify the cache Max Age value. [GLBE-5292](https://dotdash.atlassian.net/browse/GLBE-5292)

## 3.9.95
 - Fixed a bug causing components to not display in pattern library [GLBE-5921](https://dotdash.atlassian.net/browse/GLBE-5921)

## 3.9.94
 - Added automation for Disqus component [GLBE-5917](https://dotdash.atlassian.net/browse/GLBE-5917)

## 3.9.93
 - Propagating release/3.8 to release/3.9 (3.8.96-97)
   - Bump globe-core to 3.8.26 for redis failover recovery [GLBE-5860](https://dotdash.atlassian.net/browse/GLBE-5860)
   - Venus Model for SC location Block(dev ticket - GLBE-5906)  [GLBE-5910](https://dotdash.atlassian.net/browse/GLBE-5910)

## 3.9.92
 - added MntlDotdashFamilyNavComponent [REF-1472](https://dotdash.atlassian.net/browse/REF-1472)

## 3.9.91
- Inline video test has been added [LW-1931](https://dotdash.atlassian.net/browse/LW-1931)
   - MntlScBlockComponent added
   - MntlScBlockInlineVideoComponent added
   - Inline video test added to MntlVideoTest class

## 3.9.90
 - Added hasHeading method for MntlScBlockCalloutComponent for automation [https://dotdash.atlassian.net/browse/HLTH-4148)

## 3.9.89
 - Added MntlScBlockCalloutComponent for automation [https://dotdash.atlassian.net/browse/HLTH-4148)

## 3.9.88
 - updated mntlVideoTest methods to be used by verticals and bumped venus version to 1.3.3. [GLBE-5886](https://dotdash.atlassian.net/browse/GLBE-5886)

## 3.9.87
 - Pass arguments to overloaded MantleRenderUtils constructor instead of null. Fixes bug where vendorService and other fields get set to null. [TRIP-1367](https://dotdash.atlassian.net/browse/TRIP-1364)

## 3.9.86
 - Implementation of structured intro block, structured project steps, and custom time property [TS-3026](https://dotdash.atlassian.net/browse/TS-3026)

## 3.9.85
 - Add callout content block to Globe [HLTH-4035] (https://dotdash.atlassian.net/browse/HLTH-4035)

## 3.9.84
 - Remove period and unwanted style from family nav component. [TRIP-1352](https://dotdash.atlassian.net/browse/TRIP-1352)

## 3.9.83 (Introduces bug in MantleRenderUtils. Upgrade to 8.9.87)
 - Update anchor macro external attr functionality and use isValidInternalUrl in core [HLTH-3935](https://dotdash.atlassian.net/browse/HLTH-3935)

## 3.9.82
 - Make inner class Item static and add default constructor for jackson [HLTH-4138](https://dotdash.atlassian.net/browse/HLTH-4138)

## 3.9.81
 - Propagating release/3.8 to release/3.9 (3.8.95)
   - Add disqus comments widget [GLBE-5902](https://dotdash.atlassian.net/browse/GLBE-5902)

## 3.9.80
 - Upgrade mantle-grunt@4.0.6 to allow microverticals to use mantle's sass mixins [GLBE-5913](https://dotdash.atlassian.net/browse/GLBE-5913)

## 3.9.79
 - Add fields on BioDocument for social presence Urls (LinkedIn, Pinterest, Instagram, Website) and for shortBio [GLBE-5914](https://dotdash.atlassian.net/browse/GLBE-5914)

## 3.9.78
 - Add trailing slash to more link in dotdash-family-nav[TRIP-1340](https://dotdash.atlassian.net/browse/TRIP-1340)

## 3.9.77
 - Propagating release/3.8 to release/3.9 (3.8.92, 3.8.93, 3.8.94)
   - Gracefully handle unknown sc-block [GLBE-5899](https://dotdash.atlassian.net/browse/GLBE-5899)
   - Connect to JMX over JMXMP [GLBE-5677](https://dotdash.atlassian.net/browse/GLBE-5677)
   - List Location Component - use adr_address field from Google Places api instead of address components array [TRIP-1334](https://dotdash.atlassian.net/browse/TRIP-1334)

## 3.9.76
 - Lazyloaded iframes are now loaded via src attribute [HLTH-4127](https://dotdash.atlassian.net/browse/HLTH-4127)

## 3.9.75
 - Add dotdash-family-nav component for vertical footers as part of EAT updates. [TRIP-1320](https://dotdash.atlassian.net/browse/TRIP-1320)

## 3.9.74
 - Adding model to support steps implementation for structure content recipe. [TS-3120](https://dotdash.atlassian.net/browse/TS-3120)

## 3.9.73
 - Strip whitespace from sc location headings. Add default sc location address heading. [TRIP-1292](https://dotdash.atlassian.net/browse/TRIP-1292)

## 3.9.72
 - Add SC List Model, common tests on the Mantle level [REF-1435](https://dotdash.atlassian.net/browse/REF-1435)
   - MntlSCListItem model
   - MntlLightBoxTest
   - MntlCommerceButton model
   - MntlFigure Component Updated
   - MntlLightBox model

## 3.9.71
 - Propagating release/3.8 to release/3.9 (3.8.90, 3.8.91)
   - Mock data for LocationTask when there's no API key [GLBE-5898](https://dotdash.atlassian.net/browse/GLBE-5898)
   - List Location Component - bug fix and cleanup [GLBE-5906](https://dotdash.atlassian.net/browse/GLBE-5906)

## 3.9.70
 - Fix pattern library rendering for multiple components [GLBE-5905](https://dotdash.atlassian.net/browse/GLBE-5905)

## 3.9.69
 - Propagating release/3.8 to release/3.9 (3.8.88, 3.8.89)
   - List Location Component - Google Places Task [GLBE-5851](https://dotdash.atlassian.net/browse/GLBE-5851)
   - Added a new StructuredContent Location component along with FTL. Also added supporting Java Models along with a mock Java location task to support front-end dev work. This work task will later get updated by a full functioning task.
   - List Location Component - Java Model [GLBE-5855](https://dotdash.atlassian.net/browse/GLBE-5855)
   - List Location Component - XML and FTL [TRIP-1293](https://dotdash.atlassian.net/browse/TRIP-1293)

## 3.9.68
 - Always toggle toc button text when the component is expanded/collapsed [BLNC-3419](https://dotdash.atlassian.net/browse/BLNC-3419)

## 3.9.67
 - Bug fix for ids in dynamically ref component (via bumped up version of globe-core - 3.8.22) [GLBE-5848](https://dotdash.atlassian.net/browse/GLBE-5848)

## 3.9.66
 - Add JSON video schema for sc inline videos [LW-1892](https://dotdash.atlassian.net/browse/LW-1892)

## 3.9.65
 - Add StructuredContentVideoBlockEx class for inline video sc block [LW-1891](https://dotdash.atlassian.net/browse/LW-1891)
 - Add sc-block-inlinevideo frontend component for inline video sc block [LW-1893](https://dotdash.atlassian.net/browse/LW-1893)

## 3.9.64
 - Added TOC test component [BLNC-3377](https://dotdash.atlassian.net/browse/BLNC-3377)

## 3.9.63
 - Updated TOC to remove default H3 tags [REF-1444](https://dotdash.atlassian.net/browse/REF-1444)
 - Removed rel="nofollow" from TOC links [REF-1445](https://dotdash.atlassian.net/browse/REF-1445)

## 3.9.62
 - Add Vue.js as a Yarn dependency [HLTH-3934](https://dotdash.atlassian.net/browse/HLTH-3934)

## 3.9.61
 - Add getFeedback service method & task to getFeedbackThumbsSignals and CachedUGCFeedbackService is added [LW-1752](https://dotdash.atlassian.net/browse/LW-1752)

## 3.9.60
 - Add iframe content block [HLTH-3770](https://dotdash.atlassian.net/browse/HLTH-3770)

## 3.9.59
 - Fix deserialization issue for structured content documents

## 3.9.58
 - Collapse TOC on click outside element while expanded [BLNC-3407](https://dotdash.atlassian.net/browse/BLNC-3407)

## 3.9.57
 - Remove unnecessary !important from commerce.scss.

## 3.9.56 (Skip to 3.9.59)
 - Add stepType and isLastStep to StructuredContentBlockData [TS-3041](https://dotdash.atlassian.net/browse/TS-3041)

## 3.9.55
 - Add alt text to mntl-sc-block-starrating that reads "x out of 5 stars" [TRIP-1302](https://dotdash.atlassian.net/browse/TRIP-1302)

## 3.9.54
 - Use readAndDeferred for One-tap component [HLTH-3791](https://dotdash.atlassian.net/browse/HLTH-3791)

## 3.9.53
 - Add span inside the heading tag for non-link heading to support inline styling. [LW-1882](https://dotdash.atlassian.net/browse/LW-1882)

## 3.9.52
 - Fix Mntl.utilities.onLoad to ensure callback is fired only once [GLBE-5358](https://dotdash.atlassian.net/browse/GLBE-5358)

## 3.9.51
 - Always copy image caption to commerce block if exists [TRIP-1270](https://dotdash.atlassian.net/browse/TRIP-1270)

## 3.9.50
 - SC Inline Images Fixes [LW-1841](https://dotdash.atlassian.net/browse/LW-1841)
   - Inline Images with missing caption and owner doesn't have lightbox
   - Caption on Portrait images are sometimes cut off.
   - Allow image scroll on portrait images on lightbox.
   - Collapse caption if caption and owner are both empty string.

## 3.9.49
 - Fix mockDocument testing for structured content star rating [GLBE-5888](https://dotdash.atlassian.net/browse/GLBE-5888)

## 3.9.48
 - Fix null img src being added to commerce images when no image is returned from amazon [TS-3047](https://dotdash.atlassian.net/browse/TS-3047)

## 3.9.47
 - Add support for Multiplier support for Star ratings. [LW-1874](https://dotdash.atlassian.net/browse/LW-1874)

## 3.9.46
 - Fixed Product Images from amazon are missing in preview [LW-1881](LW-1881)

## 3.9.45
 - Propagating release/3.8 to release/3.9 (3.8.87)
   - Add dotdash domains to SEO whitelist [GLBE-5887](https://dotdash.atlassian.net/browse/GLBE-5887)

## 3.9.44
 - Update commerce img src check [TS-3047](https://dotdash.atlassian.net/browse/TS-3047)

## 3.9.43
 - Use readAndDeferred for newsletter component [HLTH-3791](https://dotdash.atlassian.net/browse/HLTH-3791)

## 3.9.42
 - Propagating release/3.8 to release/3.9 (3.8.86)
   - Bug fixes for serialization/deserialization of SC base document [GLBE-5885](https://dotdash.atlassian.net/browse/GLBE-5885)

## 3.9.41
 - Display image caption on commerce images missing override image from vendor source. [TRIP-1270](https://dotdash.atlassian.net/browse/TRIP-1270)

## 3.9.40
 - Fix bug in ConfigurableHtmlSlicer where html5 self closing tags are not recognized because is does not contain "/" [GLBE-5865](https://dotdash.atlassian.net/browse/GLBE-5865)

## 3.9.39
 - Create new star rating label property, default value to rating [TS-2808](https://dotdash.atlassian.net/browse/TS-2808)

## 3.9.38
 - Propagating release/3.8 to release/3.9 (3.8.79-3.8.85)
   - Bug fixes (instructions and ingredients) in RecipeSC schema [GLBE-5881](https://dotdash.atlassian.net/browse/GLBE-5881)
   - Fire 'Media external' event when video starts on mobile [GLBE-5847](https://dotdash.atlassian.net/browse/GLBE-5847)
   - Fire interaction events on ad preroll for JWPlayer [GLBE-5846](https://dotdash.atlassian.net/browse/GLBE-5846)
   - RecipeSC schema [GLBE-5863](https://dotdash.atlassian.net/browse/GLBE-5736) and new Selene RecipeSc document changes(introduction of nutritionInfo)
   - Fix missing resources when deferred component has dynamic reference [GLBE-5736](https://dotdash.atlassian.net/browse/GLBE-5736)
   - New RecipeSC template [GLBE-5824](https://dotdash.atlassian.net/browse/GLBE-5824)

## 3.9.37
 - Add featured-link classname with theme name [HLTH-3778](https://dotdash.atlassian.net/browse/HLTH-3778)

## 3.9.36
 - Update commerce tracking on buttons to include the same "eventOther" field as image/heading clicks [LW-1866](https://dotdash.atlassian.net/browse/LW-1866)

## 3.9.35
 - Add updated Table of Contents component. Has more flexible support for contents/design. Has default support for expand / collapse. [BLNC-2847](https://dotdash.atlassian.net/browse/BLNC-2847)

## 3.9.34
 - updated venus version to 1.3.2 [GLBE-5858](https://dotdash.atlassian.net/browse/GLBE-5858)

## 3.9.33
 - Add str.getDomain utility(Globe core update to 3.8.19) [TRIP-1268](https://dotdash.atlassian.net/browse/TRIP-1268)

## 3.9.32
 - Added custom GA click tracking event for SC commerce items [LW-1803](https://dotdash.atlassian.net/browse/LW-1803)
 - Added click tracking for featured-link content block and modified the click-tracking library to allow for passing in custom tracking IDs [LW-1664](https://dotdash.atlassian.net/browse/LW-1664)

## 3.9.31
 - Commerce images size and breakpoint updates [LW-1828](https://dotdash.atlassian.net/browse/LW-1828)
 - "nofollow" attribute missing from link sc heading [LW-1835](https://dotdash.atlassian.net/browse/LW-1835)

## 3.9.30
 - Added host header to thumbor proxy [GLBE-5864](https://dotdash.atlassian.net/browse/GLBE-5864)

## 3.9.29
 - Migrate selene doc calls to mock docs [GLBE-5801](https://dotdash.atlassian.net/browse/GLBE-5801)

## 3.9.28
 - Fixed IE masonry list overflow [GLBE-5821](https://dotdash.atlassian.net/browse/GLBE-5821)

## 3.9.27
 - Remove column-count style from smallest screen masonry-list and only justify when column-count is set on the masonry list in hopes of reducing render start time [TS-2983](https://dotdash.atlassian.net/browse/TS-2983)

## 3.9.26
 - Removed double src attribute on card images [REF-1377](https://dotdash.atlassian.net/browse/REF-1377)

## 3.9.25
 - Propagating release/3.8 to release/3.9 (3.8.78)
 - Fix bug where Mntl.Commerce re-ran on existing blocks [GLBE-5829](https://dotdash.atlassian.net/browse/GLBE-5829)

## 3.9.24
 - Removed lastEditingAuthorId,lastEditingUserId, from List of Common DataLayer Values required for Automation tests.

## 3.9.23
 - Added error handling to Selene JWT token renewal (Selene client update) [GLBE-5836](https://dotdash.atlassian.net/browse/GLBE-5836)

## 3.9.22
 - Propagating release/3.8 to release/3.9 (3.8.77)
   - Fix ads.xml bug where ads stop rendering due to customizable dfpId

## 3.9.21
- Commerce images requesting for a non-extisting image url. 'thumborURL/null' [LW-1827]
  https://dotdash.atlassian.net/browse/LW-1827

## 3.9.20
 - Fix bug in lightbox carousel that breaks functionality if the image is the first content block on the page. [BLNC-3349](https://dotdash.atlassian.net/browse/BLNC-3349)

## 3.9.19
 - Fix the changelog

## 3.9.18
 - Fixed couple of issues for Commerce Block [LW-1791](https://dotdash.atlassian.net/browse/LW-1791)
   - Images from CMS are now clickable
   - Updated the Base Style
   - Added Prefix for Star Rating Block (for adding titles or vertical emblem)
   - Add default layout for Image Galleries on SC Pages

## 3.9.17
 - Propagating release/3.8 to release/3.9 (3.8.76)
   - Propagating release/3.7 to release/3.8 (3.7.138)
     - Add type check before calling method. [GLBE-5808](https://dotdash.atlassian.net/browse/GLBE-5808)

## 3.9.16
 - Case dynamic4 for setPageSpecificValueBillboardMobile method added [LW-1822](https://dotdash.atlassian.net/browse/LW-1822)

## 3.9.15 (Skip to 3.9.22 due to ads not rendering because of dfpId bug)
 - Propagating release/3.8 to release/3.9 (3.8.74-75)
   - Created a variable dfpId for ad networks [GLBE-5656](https://dotdash.atlassian.net/browse/GLBE-5656)
   - Remove YQL robot.txt and tests [GLBE-5754](https://dotdash.atlassian.net/browse/GLBE-5754)

## 3.9.14
 - Upgrading Freemarker to 2.3.28 via globe-core upgrade [GLBE-5786](https://dotdash.atlassian.net/browse/GLBE-5786)

## 3.9.13
 - Fix grunt-modernizr/customizr related build issues

## 3.9.12
 - Updating automation to use latest Robots.txt

## 3.9.11
 - Remove selene basic auth configs [GLBE-5815](https://dotdash.atlassian.net/browse/GLBE-5815)

## 3.9.10
 - Add vertical to BaseDocumentEx

## 3.9.9
 - Propagating release/3.8 to release/3.9 (3.8.72-73)
   - Update to globe-core v3.8.16: Clone cachedForTargeter set in `Builder(TemplateComponent)` constructor to avoid sharing objects (GLBE-5820)[https://dotdash.atlassian.net/browse/GLBE-5820)
   - Fixed starrating background rendering [GLBE-5817](https://dotdash.atlassian.net/browse/GLBE-5817)

## 3.9.8
- Upgraded to latest Spring patch version [GLBE-5798](https://dotdash.atlassian.net/browse/GLBE-5798)

## 3.9.7
 - Propagating release/3.8 to release/3.9 (3.8.67-71)
   - Propagating release/3.7 to release/3.8 (3.7.135-137)
      - Remove OpenX, AOL, OpenXLite and Index from RTB.js. [GLBE-5676](https://dotdash.atlassian.net/browse/GLBE-5676)
      - Add mntl.accordion.end event to accordion.js. Event will fire once at the end of each transition; open, close or open/close. [GLBE-5541](https://dotdash.atlassian.net/browse/GLBE-5541)
      - Update "RTB Timing" event to set "non-interaction" to true. [GLBE-5790](https://dotdash.atlassian.net/browse/GLBE-5790)
   - Additional styles for Structured Content lists [GLBE-5687](https://dotdash.atlassian.net/browse/GLBE-5687)
   - Pattern Library - removed reference to old about.com doc and switched to mock document [GLBE-5811](https://dotdash.atlassian.net/browse/GLBE-5811)
   - Fixed NPE errors for SC Commerce Block on Images. [LW-1767](https://dotdash.atlassian.net/browse/LW-1767)
   - Add testLinkClickDLEvent method with index parameter, for click tracking test [LW-1765](https://dotdash.atlassian.net/browse/LW-1765)

## 3.9.6
 - Fix variable name in proctor-injection-css.ftl [TRIP-1218](https://dotdash.atlassian.net/browse/TRIP-1218)

## 3.9.5
 - Don't inline styles for PC view [GLBE-5818](https://dotdash.atlassian.net/browse/GLBE-5818)

## 3.9.4
 - Fixed too-verbose logging of SecurTransparentProxyServlet [GLBE-5809](https://dotdash.atlassian.net/browse/GLBE-5809)

## 3.9.3
 - Propagating release/3.8 to release/3.9 (3.8.65-66)
   - Propagating release/3.7 to release/3.8 (3.7.132-134)
      - Refactor JWPlayer response handler to save and use last available video in case of a match failure. [GLBE-5807](https://dotdash.atlassian.net/browse/GLBE-5807)
      - Reposition hidden sc ads at the top of the page [GLBE-5755](https://dotdash.atlassian.net/browse/GLBE-5755)
      - Fixed changelog
   - Fixed duped figure caption in structured content images [GLBE-5805](https://dotdash.atlassian.net/browse/GLBE-5805)

## 3.9.2
 - Propagating release/3.8 to release/3.9 (3.8.64)
   - Added `fastlyCountry` and `geoIpCountry` to GTM data [GLBE-5758](https://dotdash.atlassian.net/browse/GLBE-5758)

## 3.9.1
 - Fixed build errors due to wrong globe-core version (no ticket)

## 3.9.0

See [Globe 3.9 Release Notes](https://dotdash.atlassian.net/wiki/spaces/TECH/pages/41648175/Mantle+3.9+Upgrade+Instructions).
 - All changes from 3.8 up to and including 3.8.63
 - Support for new infrastructure
 - Refactor of utilities.js
 - Remove jQuery-jQueryAjaxTransportXDomainRequest
 - Masonry Lists updated to use <component-list>
 - HTTPS + Authentication of Selene calls
 - Upgrade to Node v8.11.1
 - Upgrade to mantle-grunt@4.0.x
 - Replace npm with yarn
 - Deprecated code removed
 - GLBE-5639 - Removed code related to `relatedSearch`
 - GLBE-5657 - Venus Changes for mantle-3.9
 - GLBE-5740 - updated venus version to 1.3.1
 - GLBE-5762 - updated getDomain logic for mantle-39 support
 - Multiple changes to help with performance:
   - Preload resources via "Link" response headers
   - hook for allowing additional "Link" headers
   - preload GTM, GA, and DFP libraries
   - defer a number of 3rd-party libaries and resources until after page load: GTM and GPT libraries, pinitbtn, skimlinks
   - defer "bottom" script and make async. Only execute "bottom" evaluated scripts after external scripts are loaded.
   - use utilities.onLoad for SC ad-load kickoff instead of DOMContentLoaded
   - use "/thmb" prefix for Thumbor images, but use same host. Provide proxy servlet for cases of not having an external load balancer (some QA, local envs). Add /thmb to robots.txt for SEO indexing.
   - add `is-window-loaded` class to `<html>` element on page load
   - add fallback execution of ElementQueries library
   - tune rel=preconnect list to make connections in the same order as resources are requested
   - allow Mantle servers to proxy Thumbor images (for use in QA and on local machines) to deshard requests for H2 connection sharing
   - use "no-request" image url `//:0` for placeholder image on all non-IE browsers (data-image url fallback for IE); use CSS for gray placeholder bg
   - move adhesive ad to Mantle
   - only show adhesive ad after user scrolls. This removes ad load from the visual-completeness timeline.
   - default src to placeholder image for `<#thumborImg>`
   - separate rel=preconnect from rel=dns-prefetch to

## Release 3.8.x

## 3.8.102
 - Add yieldmo to the ads.txt file [GLBE-6086](https://dotdash.atlassian.net/browse/GLBE-6086)

## 3.8.101
 - Update the ads.txt file [TTN-1531](https://dotdash.atlassian.net/browse/TTN-1531)

## 3.8.100
 - Update the ads.txt file [GLBE-6061](https://dotdash.atlassian.net/browse/GLBE-6061)

## 3.8.99
 - This version skipped due to error in build.

## 3.8.98
 - Add opinionated boilerplate html component [GLBE-5927](https://dotdash.atlassian.net/browse/GLBE-5927)

## 3.8.97
 - Bump globe-core to 3.7.22 for redis failover recovery [GLBE-5860](https://dotdash.atlassian.net/browse/GLBE-5860)

## 3.8.96
 - Venus Model for SC location Block(dev ticket - GLBE-5906)  [GLBE-5910](https://dotdash.atlassian.net/browse/GLBE-5910)

## 3.8.95
 - Add disqus comments widget [GLBE-5902](https://dotdash.atlassian.net/browse/GLBE-5902)

## 3.8.94
 - List Location Component - use adr_address field from Google Places api instead of address components array [TRIP-1334](https://dotdash.atlassian.net/browse/TRIP-1334)

## 3.8.93
 - Connect to JMX over JMXMP [GLBE-5677](https://dotdash.atlassian.net/browse/GLBE-5677)

## 3.8.92
 - Gracefully handle unknown sc-block [GLBE-5899](https://dotdash.atlassian.net/browse/GLBE-5899)

## 3.8.91
 - List Location Component - bug fix and cleanup [GLBE-5906](https://dotdash.atlassian.net/browse/GLBE-5906)

## 3.8.90
 - Mock data for LocationTask when there's no API key [GLBE-5898](https://dotdash.atlassian.net/browse/GLBE-5898)

## 3.8.89
 - List Location Component - Google Places Task [GLBE-5851](https://dotdash.atlassian.net/browse/GLBE-5851)

## 3.8.88
 - Added a new StructuredContent Location component along with FTL. Also added supporting Java Models along with a mock Java location task to support front-end dev work. This work task will later get updated by a full functioning task.
 - List Location Component - Java Model [GLBE-5855](https://dotdash.atlassian.net/browse/GLBE-5855)
 - List Location Component - XML and FTL [TRIP-1293](https://dotdash.atlassian.net/browse/TRIP-1293)

## 3.8.87
 - Add dotdash domains to SEO whitelist [GLBE-5887](https://dotdash.atlassian.net/browse/GLBE-5887)

## 3.8.86
 - Bug fixes for serialization/deserialization of SC base document [GLBE-5885](https://dotdash.atlassian.net/browse/GLBE-5885)

## 3.8.85
 - Bug fixes (instructions and ingredients) in RecipeSC schema [GLBE-5881](https://dotdash.atlassian.net/browse/GLBE-5881)

## 3.8.84
 - Fire 'Media external' event when video starts on mobile [GLBE-5847](https://dotdash.atlassian.net/browse/GLBE-5847)

## 3.8.83
 - Fire interaction events on ad preroll for JWPlayer [GLBE-5846](https://dotdash.atlassian.net/browse/GLBE-5846)

## 3.8.82
 - RecipeSC schema [GLBE-5863](https://dotdash.atlassian.net/browse/GLBE-5736) and new Selene RecipeSc document changes(introduction of nutritionInfo)

## 3.8.81
 - Fix missing resources when deferred component has dynamic reference [GLBE-5736](https://dotdash.atlassian.net/browse/GLBE-5736)

## 3.8.80
 - New RecipeSC template [GLBE-5824](https://dotdash.atlassian.net/browse/GLBE-5824)

## 3.8.79
 - skip this version

## 3.8.78
 - Fix bug where Mntl.Commerce re-ran on existing blocks [GLBE-5829](https://dotdash.atlassian.net/browse/GLBE-5829)

## 3.8.77
 - Fix ads.xml bug where ads stop rendering due to customizable dfpId

## 3.8.76
 - Propagating release/3.7 to release/3.8 (3.7.138)
   - Add type check before calling method. [GLBE-5808](https://dotdash.atlassian.net/browse/GLBE-5808)

## 3.8.75 (Skip to v3.8.77 to fix bug where ads stop rendering)
 - Created a variable dfpId for ad networks [GLBE-5656](https://dotdash.atlassian.net/browse/GLBE-5656)

## 3.8.74
 - Remove YQL robot.txt and tests [GLBE-5754](https://dotdash.atlassian.net/browse/GLBE-5754)

## 3.8.73
 - Update to globe-core v3.7.19: Clone cachedForTargeter set in `Builder(TemplateComponent)` constructor to avoid sharing objects (GLBE-5820)[https://dotdash.atlassian.net/browse/GLBE-5820)

## 3.8.72
- Fixed starrating background rendering [GLBE-5817](https://dotdash.atlassian.net/browse/GLBE-5817)

## 3.8.71
 - Propagating release/3.7 to release/3.8 (3.7.135-137)
   - Remove OpenX, AOL, OpenXLite and Index from RTB.js. [GLBE-5676](https://dotdash.atlassian.net/browse/GLBE-5676)
   - Add mntl.accordion.end event to accordion.js. Event will fire once at the end of each transition; open, close or open/close. [GLBE-5541](https://dotdash.atlassian.net/browse/GLBE-5541)
   - Update "RTB Timing" event to set "non-interaction" to true. [GLBE-5790](https://dotdash.atlassian.net/browse/GLBE-5790)

## 3.8.70
 - Additional styles for Structured Content lists [GLBE-5687](https://dotdash.atlassian.net/browse/GLBE-5687)

## 3.8.69
 - Pattern Library - removed reference to old about.com doc and switched to mock document [GLBE-5811](https://dotdash.atlassian.net/browse/GLBE-5811)

## 3.8.68
 - Fixed NPE errors for SC Commerce Block on Images. [LW-1767](https://dotdash.atlassian.net/browse/LW-1767)

## 3.8.67
 - Add testLinkClickDLEvent method with index parameter, for click tracking test [LW-1765](https://dotdash.atlassian.net/browse/LW-1765)

## 3.8.66
 - Propagating release/3.7 to release/3.8 (3.7.132-134)
   - Refactor JWPlayer response handler to save and use last available video in case of a match failure. [GLBE-5807](https://dotdash.atlassian.net/browse/GLBE-5807)
   - Reposition hidden sc ads at the top of the page [GLBE-5755](https://dotdash.atlassian.net/browse/GLBE-5755)
   - Fixed changelog

## 3.8.65
 - Fixed duped figure caption in structured content images [GLBE-5805](https://dotdash.atlassian.net/browse/GLBE-5805)

## 3.8.64
 - Added `fastlyCountry` and `geoIpCountry` to GTM data [GLBE-5758](https://dotdash.atlassian.net/browse/GLBE-5758)

## 3.8.63
 - Propagating release/3.7 to release/3.8 (3.7.129-132)
   - Include utilities.js on mntl-sc-block-image component. [GLBE-5802](https://dotdash.atlassian.net/browse/GLBE-5802)
   - Click Tracking for Inline Image Gallery Experience [LW-1756](https://dotdash.atlassian.net/browse/LW-1756)
   - Added SeleneSignoffTests category to mantle [https://dotdash.atlassian.net/browse/DV-13]

## 3.8.62
 - Add Commerce SC block [GLBE-5732](https://dotdash.atlassian.net/browse/GLBE-5732)

## 3.8.61
 - Setup schema markup for LISTSC [LW-1733](https://dotdash.atlassian.net/browse/LW-1733)

## 3.8.60
 - Add star rating structured content block [GLBE-5683](https://dotdash.atlassian.net/browse/GLBE-5683)

## 3.8.59
 - Added task used to mock IterationModel.IterationModelValue, for unit testing [GLBE-5788](https://dotdash.atlassian.net/browse/GLBE-5788)

## 3.8.58
 - Also observe mutations on flex ad frame's parent and grandparent in order to strip out unwanted inline styles injected from Specless. [BLNC-3262](https://dotdash.atlassian.net/browse/BLNC-3263)

## 3.8.57
 - Upgrade to globe-core 3.7.15: Various improvements around cloning TemplateComponents to improve Globe performance, especially as related to Structured Content and p* for-targeters [GLBE-5748](https://dotdash.atlassian.net/browse/GLBE-5748)

## 3.8.56
 - Extract SC figure-caption into separate component [BLNC-3265](https://dotdash.atlassian.net/browse/BLNC-3265)

## 3.8.55
 - Add base line styles for structured content lists [GLBE-5682](https://dotdash.atlassian.net/browse/GLBE-5682)

## 3.8.54
 - Added automation to support jwPlayer component [TS-2841](https://dotdash.atlassian.net/browse/TS-2841)
 - Updated MntlDatalayertest

## 3.8.53
 - Added Comparison list SC block [GLBE-5684](https://dotdash.atlassian.net/browse/GLBE-5684)

## 3.8.52
 - Propagating release/3.7 to release/3.8 (3.7.122-128)
   - Fixed very misleading comment as to how to for-target into structured content
   - Reverted in 3.8.74: Blocked YQL from scraping and created robots.txt mantle test [GLBE-5754](https://dotdash.atlassian.net/browse/GLBE-5754)
   - Remove private ip disclosure from all verticals [GLBE-5753](https://dotdash.atlassian.net/browse/GLBE-5753)
   - Migrate PC cookie test to mantle [GLBE-5508](https://dotdash.atlassian.net/browse/GLBE-5508)
   - Added previous/next arrows to image lightbox and included image caption [LW-1732](https://dotdash.atlassian.net/browse/LW-1732)
   - Include inline images in the article schema for structured content. [LW-1734](https://dotdash.atlassian.net/browse/LW-1734)
   - Added mantle RTB datalayer test [GLBE-5745](https://dotdash.atlassian.net/browse/GLBE-5745)

## 3.8.51
 - Pointing to bug fixed (equals fix) globe-core version [GLBE-5761](https://dotdash.atlassian.net/browse/GLBE-5761)

## 3.8.50
 - Use primary path only for taxonomy nodes in datalayer [HLTH-3774](https://dotdash.atlassian.net/browse/HLTH-3774)

## 3.8.49
 - Set JWPlayer document template ID to 137 [GLBE-5766](https://dotdash.atlassian.net/browse/GLBE-5766)

## 3.8.48
 - Fix race condition between amazon-affiliate-tagger's link click handler and Amazon API call [BLNC-3255](https://dotdash.atlassian.net/browse/BLNC-3255)

## 3.8.47
 - Propagating release/3.7 to release/3.8 (3.7.121)
   - Add dataLayer events for mute and replay of jwplayer [TS-2851](https://dotdash.atlassian.net/browse/TS-2851), [TS-2852](https://dotdash.atlassian.net/browse/TS-2852)

## 3.8.46
 - Add mainEntityOfPage to schema-static.ftl to support verywell.com [HLTH-3854] (https://dotdash.atlassian.net/browse/HLTH-3854)

## 3.8.45
 - Propagating release/3.7 to release/3.8 (3.7.118-120)
   - Fix bug in JWPlayer video events that was pushing gtm.pageError events [GLBE-5757](https://dotdash.atlassian.net/browse/GLBE-5757)
   - Request 640 x 365 ad size for jwplayer [TS-2846](https://dotdash.atlassian.net/browse/TS-2846)
   - Add includesummaries for document model on SC to get featuredLink summary document [LW-1736](https://dotdash.atlassian.net/browse/LW-1736)

## 3.8.44
 - Added missing venus components GLBE-5750 (https://dotdash.atlassian.net/browse/GLBE-5750).

## 3.8.43
 - Propagating release/3.7 to release/3.8 (3.7.117)
   - Reverted 3.7.89 change to SC for targeting due to performance issues [GLBE-5742](https://dotdash.atlassian.net/browse/GLBE-5742)

## 3.8.42
 - Propagating release/3.7 to release/3.8 (3.7.116)
   - Add 10 second event to JWPlayer Videos [GLBE-5727](https://dotdash.atlassian.net/browse/GLBE-5727)

## 3.8.41
 - Fix masonry list not justifying on IE 11 [BLNC-3139](https://dotdash.atlassian.net/browse/BLNC-3139)

## 3.8.40
 - Propagating release/3.7 to release/3.8 (3.7.111-115)
   - bump globe-core version for SpEL evaluator fix [GLBE-5735](https://dotdash.atlassian.net/browse/GLBE-5735)
   - venus version update for fixing google_nofetch value [GLBE-5740](https://dotdash.atlassian.net/browse/GLBE-5740)
   - Don't escape HTML on links in SC Headings [GLBE-5741](https://dotdash.atlassian.net/browse/GLBE-5741)
   - Fix event and eventLabel values for RTB tracking [GLBE-5725](https://dotdash.atlassian.net/browse/GLBE-5725)
   - Fix unit test by fixing Mntl.Deferred.init to handle non jQuery arguments [GLBE-5209](https://dotdash.atlassian.net/browse/GLBE-5209)

## 3.8.39
 - Add sources to BaseDocumentEx [HLTH-3552](https://dotdash.atlassian.net/browse/HLTH-3552)
 - Add potentialAction to schema-static.ftl to support verywell.com [HLTH-3854] (https://dotdash.atlassian.net/browse/HLTH-3854)

## 3.8.38
 - Always use secure selene url and deprecate the nonsecure url [GLBE-5516](https://dotdash.atlassian.net/browse/GLBE-5516)

## 3.8.37
 - Add ListStructuredContentDocumentEx class [GLBE-5666](https://dotdash.atlassian.net/browse/GLBE-5666)

## 3.8.36
 - Propagating release/3.7 to release/3.8 (3.7.109-110)
   - Fix typo in lightbox JS [GLBE-5734](https://dotdash.atlassian.net/browse/GLBE-5734)
   - Added common data layer test for GDPR banner [GLBE-5730](https://dotdash.atlassian.net/browse/GLBE-5730)

## 3.8.35
 - Propagating release/3.7 to release/3.8 (3.7.106-108)
   - GDPR Banner: Set the Tracking Container on the content level, so component level extension won't affect event Category Name.  [LW-1678](https://dotdash.atlassian.net/browse/LW-1678)
   - Add JS lightbox to structured content image blocks [GLBE-5671](https://dotdash.atlassian.net/browse/GLBE-5671)
   - Added `<@location>` to text-link component to support adding extra content to SC promo links.[LW-1642](https://dotdash.atlassian.net/browse/LW-1642)
   - Rename promo link to featured link [LW-1705](https://dotdash.atlassian.net/browse/LW-1705)

## 3.8.34
 - Add hasTableOfContents flag to BaseDocumentEx and use metadata as a fallback [GLBE-5716](https://dotdash.atlassian.net/browse/GLBE-5716)

## 3.8.33
 - Propagating release/3.7 to release/3.8 (3.7.103-105)
   - Pass gdpr flag to Amazon RTB bidder [GLBE-5723](https://dotdash.atlassian.net/browse/GLBE-5723)
   - Added common test for GDPR banner [GLBE-5709](https://dotdash.atlassian.net/browse/GLBE-5709)
   - Added common validation for iu parameter for ad calls in mantle [BLNC-3088](https://dotdash.atlassian.net/browse/BLNC-3088)

## 3.8.32
 - Updating MntlLeaderboardHeaderComponent(https://dotdash.atlassian.net/browse/BLNC-3116)

## 3.8.31
 - Propagating release/3.7 to release/3.8 (3.7.101-102)
   - Add method to StructuredContentAdSlotEx to return empty data structure [GLBE-5726](https://dotdash.atlassian.net/browse/GLBE-5726)
   - Adjust click-handling to close all notification-banners that may be on a page (not just the first one) [TS-2771](https://dotdash.atlassian.net/browse/TS-2771)

## 3.8.30
 - Propagating release/3.7 to release/3.8 (3.7.100)
   - Add safeguards to RTB plugins [GLBE-5721](https://dotdash.atlassian.net/browse/GLBE-5721)

## 3.8.29
 - Propagating release/3.7 to release/3.8 (3.7.99)
   - Use `<list>` for class property of GDPR close button, to allow extending and preserving the values from mantle [TS-2728](https://dotdash.atlassian.net/browse/TS-2728)

## 3.8.28
 - Propagating release/3.7 to release/3.8 (3.7.98)
   - GDPR: add euTrafficFlag to dataLayer [GLBE-5696](https://dotdash.atlassian.net/browse/GLBE-5696)

## 3.8.27
 - Propagating release/3.7 to release/3.8 (3.7.96-97)
   - Update RTB tracking metrics data layer event object to match Data team's specs [BLNC-3173](https://dotdash.atlassian.net/browse/BLNC-3173)
   - Update preroll error events (JWPlayer) to use `eventOther` instead of `eventValue` [GLBE-5701](https://dotdash.atlassian.net/browse/GLBE-5701)

## 3.8.26
 - Propagating release/3.7 to release/3.8 (3.7.93-95)
   - Create notification-banner, gdpr-notification-banner components. Refactor net-neutrality-banner to use notification-banner component. [BLNC-491](https://dotdash.atlassian.net/browse/BLNC-491)
   - Fix issue where one block pages would not get an ad. [TS-2754] (https://dotdash.atlassian.net/browse/TS-2754)
   - Add passing of detail to Mntl.utilities.customEvent [GLBE-5714](https://dotdash.atlassian.net/browse/GLBE-5714)

## 3.8.25
 - Propagating release/3.7 to release/3.8 (3.7.91-92)
   - GLBE-5700 - Updated to latest version of hippodrome for new GeoIP client
   - JWPlayer: Turn vpaid contols on and set high z-index [GLBE-5689](https://dotdash.atlassian.net/browse/GLBE-5689)

## 3.8.24
- Propagating release/3.7 to release/3.8 (3.7.89-90):
   - Update ads.txt [GLBE-5704](https://dotdash.atlassian.net/browse/GLBE-5704)
   - Cleaned up for targeting around sc-page, injecting into structured content.  [GLBE-5597](https://dotdash.atlassian.net/browse/GLBE-5597)

## 3.8.23
- Propagating release/3.7 to release/3.8 (3.7.88):
 - Make FTL safe for different value types of `model.rtbTimeout`

## 3.8.22
- Propagating release/3.7 to release/3.8 (3.7.84-87):
   - Add maxDocPopulation task parameter to TaxeneRelationTask.getDescendentArticles [REF-1275](https://dotdash.atlassian.net/browse/REF-1275)
   - Refactor the RTB library to wait for bidding partners library's to load before calling for bids. [GLBE-5663](https://dotdash.atlassian.net/browse/GLBE-5663)
   - Cleaned up MDC and added more logging for missing Amazon ASINs [GLBE-5629](https://dotdash.atlassian.net/browse/GLBE-5629)
   - Refactor Amazon RTB plugin to consume either the current response or PMP response. [GLBE-5601](https://dotdash.atlassian.net/browse/GLBE-5601)

## 3.8.21
 - Restore the Globe Task Execution Bookmarklet [GLBE-5174](https://dotdash.atlassian.net/browse/GLBE-5174)

## 3.8.20
 - Add JwPlayerVideoDocumentEx class for JWPLAYERVIDEO templateType [GLBE-5505](https://dotdash.atlassian.net/browse/GLBE-5505)

## 3.8.19
 - Propagating release/3.7 to release/3.8 (3.7.82-83):
   - update url in net-neutrality-banner component [glbe-5672](https://dotdash.atlassian.net/browse/glbe-5672)
   - escape cookie values for boomerang evaluated js [hlth-3772](https://dotdash.atlassian.net/browse/hlth-3772)

## 3.8.18
 - Propagating release/3.7 to release/3.8 (3.7.80-81):
   - Update sc-ads.js to remove all ad sizes that wont fit in remaining right rail space [TS-2466](https://dotdash.atlassian.net/browse/TS-2466)
   - Inline image srcset should use image size as max width [LW-1670](https://dotdash.atlassian.net/browse/LW-1670)

## 3.8.17
 - Fixing Mantle Datalayer tests

## 3.8.16
 - Propagating release/3.7 to release/3.8 (3.7.79):
   - Removed vpaid controls from JW player [REF-1269](https://dotdash.atlassian.net/browse/REF-1269)
   - Added adBreakStart event tracking for JW player [REF-1270](https://dotdash.atlassian.net/browse/REF-1270)

## 3.8.15
 - Propagating release/3.7 to release/3.8 (3.7.78):
   - Update net neutrality component for May 2018 campaign [GLBE-5672](https://dotdash.atlassian.net/browse/GLBE-5672)

## 3.8.14
 - Update selene and globe-core version for vwhealth url support [HLTH-3600](https://dotdash.atlassian.net/browse/HLTH-3600)

## 3.8.13
 - Propagating release/3.7 to release/3.8 (3.7.77):
   - Add TOC-LIST component, task and model [HLTH-3732](https://dotdash.atlassian.net/browse/HLTH-3732)

## 3.8.12
 - Propagating release/3.7 to release/3.8 (3.7.74-76):
   - Update commerce retailer whitelist [TS-2119](https://dotdash.atlassian.net/browse/TS-2119)
   - Add new SC block type: PROMO_LINK [LW-1642](https://dotdash.atlassian.net/browse/LW-1642)
   - Don't escape SC heading text because it can have HTML in it now (for emphasis, etc) [GLBE-5665](https://dotdash.atlassian.net/browse/GLBE-5665)

## 3.8.11
 - Handle missing result.title gracefully for quiz results component. [BLNC-3065](https://dotdash.atlassian.net/browse/BLNC-3065)

## 3.8.10
 - Propagating release/3.7 to release/3.8 (3.7.71-73):
   - Make Mntl.Rtb.Tracking opt in [GLBE-5664](https://dotdash.atlassian.net/browse/GLBE-5664)
   - Pass state and active date values to multi retailer if available [HLTH-3736](https://dotdash.atlassian.net/browse/HLTH-3736)
   - Revert Image orientation logic for backward compatibility. square images should have an image ratio of 1 - 1.25.

## 3.8.9
 - No material changes.  Bumped globe-core version because of a reverted commit in globe-core

## 3.8.8
- Propagating release/3.7 to release/3.8 (3.7.66-70):
   - Ads not fully clickable on SC content [LW-1645](https://dotdash.atlassian.net/browse/LW-1645)
   - Collect RTB load Metrics and send them to GA [GLBE-5542](https://dotdash.atlassian.net/browse/GLBE-5542)
   - Structured Content - Removed 30px height provision for header on inline images.
   - Revert accidental merge
   - Accidental commerce bugfix merge

## 3.8.7
 - Parametrized TaxeneRelationTask.getDescendantArticles method to accept caller specific maxDocPopulation value

## 3.8.6
 - Propagating release/3.7 to release/3.8 (3.7.64-65):
   - Propagating release/3.5 to release/3.7 (3.5.171):
     - Components nested inside iterated components now collapse correctly [GLBE-5530](https://dotdash.atlassian.net/browse/GLBE-5530)
   - Structured Content - add link to image to open a css only lightbox; all images are lazy loaded [GLBE-5596](https://dotdash.atlassian.net/browse/GLBE-5596) [GLBE-5618](https://dotdash.atlassian.net/browse/GLBE-5618)

## 3.8.5
 - Propagating release/3.7 to release/3.8 (3.7.57-63):
   - Add round to nutrition data in schema to the nearest whole number [TS-2539](https://dotdash.atlassian.net/browse/TS-2539)
   - Fix free marker errors from empty nutrition information [TS-2592](https://dotdash.atlassian.net/browse/TS-2539)b
   - Fix frequency setting for JWPlayer [GLBE-5648](https://dotdash.atlassian.net/browse/GLBE-5648)
   - Additional default setting for JWPlayer [GLBE-5643](https://dotdash.atlassian.net/browse/GLBE-5643), [GLBE-5644](https://dotdash.atlassian.net/browse/GLBE-5644)
   - Fixed `retailers` being null for list templates [GLBE-5637](https://dotdash.atlassian.net/browse/GLBE-5637)
   - Add pre-roll and delete resize event for JWPlayer analytics. [GLBE-5638](https://dotdash.atlassian.net/browse/GLBE-5638)
   - Fix method that creates setup object. [GLBE-5633](https://dotdash.atlassian.net/browse/GLBE-5633)
   - Fix googletag event assignment before it's available. [GLBE-5635](https://dotdash.atlassian.net/browse/GLBE-5635)

## 3.8.4
 - Propagating release/3.7 to release/3.8 (3.7.49-56):
   - Various under-the-cover redis improvements (globe-core update)
   - Fix 500 when commerceInfo is missing on one of the items [GLBE-5623](https://dotdash.atlassian.net/browse/GLBE-5623)
   - Fix Bug introduced in 3.7.49 with rollaway leaderboard merge from 3.5 [GLBE-5620](https://dotdash.atlassian.net/browse/GLBE-5620)
   - SC Ads overlap when resizing window [LW-1602](https://dotdash.atlassian.net/browse/LW-1602)
   - Use docId instead of url for pattern library document requests as vw domain will change
   - Propagating release/3.5 to release/3.7 (3.5.170):
     - Add JWPlayer to mantle [GLBE-5506](https://dotdash.atlassian.net/browse/GLBE-5506)
   - Add css overrides for basic Structured Content appearance. [GLBE-5594](https://dotdash.atlassian.net/browse/GLBE-5594)
   - Propagating release/3.5 to release/3.7 (3.5.168-169):
     - Add listener and timer for rollaway leaderboard to confirm it does not roll away before a viewable impression. [GLBE-5586](https://dotdash.atlassian.net/browse/GLBE-5586)
     - Venus Changes: Make Vertical Test URL agnostic of env and domain GLBE-5527

## 3.8.3
 - Bumped globe-core -> hippodrome for latest URL rewrite changes (part II)

## 3.8.2
 - Bumped globe-core -> hippodrome for latest URL rewrite changes

## 3.8.1
 - Remove amp values from list of reserved seo whitelist params [BLNC-2017](https://dotdash.atlassian.net/browse/BLNC-3017)

## 3.8.0

 - Remove deprecated constructors from CommerceService,DocumentTaxeneService,DocumentTaxeneCuratedListTask
 - Upgraded to latest hippodrome and Selene Client
   - Now using SeleneHttpServiceClient instead of AbstractHttpServiceClient as parent for Selene based services

## Release 3.7.x

## 3.7.138
 - Add type check before calling method. [GLBE-5808](https://dotdash.atlassian.net/browse/GLBE-5808)

## 3.7.137
 - Remove OpenX, AOL, OpenXLite and Index from RTB.js. [GLBE-5676](https://dotdash.atlassian.net/browse/GLBE-5676)

## 3.7.136
 - Add mntl.accordion.end event to accordion.js. Event will fire once at the end of each transition; open, close or open/close. [GLBE-5541](https://dotdash.atlassian.net/browse/GLBE-5541)

## 3.7.135
 - Update "RTB Timing" event to set "non-interaction" to true. [GLBE-5790](https://dotdash.atlassian.net/browse/GLBE-5790)

## 3.7.134
 - Refactor JWPlayer response handler to save and use last available video in case of a match failure. [GLBE-5807](https://dotdash.atlassian.net/browse/GLBE-5807)

## 3.7.133
 - Reposition hidden sc ads at the top of the page [GLBE-5755](https://dotdash.atlassian.net/browse/GLBE-5755)

## 3.7.132
 - Fixed changelog

## 3.7.131
 - Include utilities.js on mntl-sc-block-image component. [GLBE-5802](https://dotdash.atlassian.net/browse/GLBE-5802)

## 3.7.130
 - Click Tracking for Inline Image Gallery Experience [LW-1756](https://dotdash.atlassian.net/browse/LW-1756)

## 3.7.129
- Added SeleneSignoffTests category to mantle [https://dotdash.atlassian.net/browse/DV-13]

## 3.7.128
- Fixed very misleading comment as to how to for-target into structured content

## 3.7.127
 - Blocked YQL from scraping and created robots.txt mantle test [GLBE-5754](https://dotdash.atlassian.net/browse/GLBE-5754)

## 3.7.126
 - Remove private ip disclosure from all verticals [GLBE-5753](https://dotdash.atlassian.net/browse/GLBE-5753)

## 3.7.125
 - Migrate PC cookie test to mantle [GLBE-5508](https://dotdash.atlassian.net/browse/GLBE-5508)

## 3.7.124
 - Added previous/next arrows to image lightbox and included image caption [LW-1732](https://dotdash.atlassian.net/browse/LW-1732)

## 3.7.123
 - Include inline images in the article schema for structured content. [LW-1734](https://dotdash.atlassian.net/browse/LW-1734)

## 3.7.122
 - Added mantle RTB datalayer test [GLBE-5745](https://dotdash.atlassian.net/browse/GLBE-5745)

## 3.7.121
 - Add dataLayer events for mute and replay of jwplayer [TS-2851](https://dotdash.atlassian.net/browse/TS-2851), [TS-2852](https://dotdash.atlassian.net/browse/TS-2852)

## 3.7.120
 - Fix bug in JWPlayer video events that was pushing gtm.pageError events [GLBE-5757](https://dotdash.atlassian.net/browse/GLBE-5757)

## 3.7.119
 - Request 640 x 365 ad size for jwplayer [TS-2846](https://dotdash.atlassian.net/browse/TS-2846)

## 3.7.118
 - Add includesummaries for document model on SC to get featuredLink summary document [LW-1736](https://dotdash.atlassian.net/browse/LW-1736)

## 3.7.117
 - Reverted 3.7.89 change to SC for targeting due to performance issues [GLBE-5742](https://dotdash.atlassian.net/browse/GLBE-5742)

## 3.7.116
 - Add 10 second event to JWPlayer Videos [GLBE-5727](https://dotdash.atlassian.net/browse/GLBE-5727)

## 3.7.115
 - bump globe-core version for SpEL evaluator fix [GLBE-5735](https://dotdash.atlassian.net/browse/GLBE-5735)

## 3.7.114
 - venus version update for fixing google_nofetch value [GLBE-5740](https://dotdash.atlassian.net/browse/GLBE-5740)

## 3.7.113
- Don't escape HTML on links in SC Headings [GLBE-5741](https://dotdash.atlassian.net/browse/GLBE-5741)

## 3.7.112
 - Fix event and eventLabel values for RTB tracking [GLBE-5725](https://dotdash.atlassian.net/browse/GLBE-5725)

## 3.7.111
 - Fix unit test by fixing Mntl.Deferred.init to handle non jQuery arguments [GLBE-5209](https://dotdash.atlassian.net/browse/GLBE-5209)

## 3.7.110
 - Fix typo in lightbox JS [GLBE-5734](https://dotdash.atlassian.net/browse/GLBE-5734)

## 3.7.109
 - Added common data layer test for GDPR banner [GLBE-5730](https://dotdash.atlassian.net/browse/GLBE-5730)

## 3.7.108
 - GDPR Banner: Set the Tracking Container on the content level, so component level extension won't affect event Category Name.  [LW-1678](https://dotdash.atlassian.net/browse/LW-1678)

## 3.7.107
 - Add JS lightbox to structured content image blocks [GLBE-5671](https://dotdash.atlassian.net/browse/GLBE-5671)

## 3.7.106
 - Added `<@location>` to text-link component to support adding extra content to SC promo links.[LW-1642](https://dotdash.atlassian.net/browse/LW-1642)
 - Rename promo link to featured link [LW-1705](https://dotdash.atlassian.net/browse/LW-1705)

## 3.7.105
 - Pass gdpr flag to Amazon RTB bidder [GLBE-5723](https://dotdash.atlassian.net/browse/GLBE-5723)

## 3.7.104
 - Added common test for GDPR banner [GLBE-5709](https://dotdash.atlassian.net/browse/GLBE-5709)

## 3.7.103
 - Added common validation for iu parameter for ad calls in mantle [BLNC-3088](https://dotdash.atlassian.net/browse/BLNC-3088)

## 3.7.102
 - Add method to StructuredContentAdSlotEx to return empty data structure [GLBE-5726](https://dotdash.atlassian.net/browse/GLBE-5726)

## 3.7.101
 - Adjust click-handling to close all notification-banners that may be on a page (not just the first one) [TS-2771](https://dotdash.atlassian.net/browse/TS-2771)

## 3.7.100
 - Add safeguards to RTB plugins [GLBE-5721](https://dotdash.atlassian.net/browse/GLBE-5721)

## 3.7.99
 - Use `<list>` for class property of GDPR close button, to allow extending and preserving the values from mantle [TS-2728](https://dotdash.atlassian.net/browse/TS-2728)

## 3.7.98
 - GDPR: add euTrafficFlag to dataLayer [GLBE-5696](https://dotdash.atlassian.net/browse/GLBE-5696)

## 3.7.97
 - Update RTB tracking metrics data layer event object to match Data team's specs [BLNC-3173](https://dotdash.atlassian.net/browse/BLNC-3173)

## 3.7.96
 - Update preroll error events (JWPlayer) to use `eventOther` instead of `eventValue` [GLBE-5701](https://dotdash.atlassian.net/browse/GLBE-5701)

## 3.7.95
 - Create notification-banner, gdpr-notification-banner components. Refactor net-neutrality-banner to use notification-banner component. [BLNC-491](https://dotdash.atlassian.net/browse/BLNC-491)

## 3.7.94
 - Fix issue where one block pages would not get an ad. [TS-2754] (https://dotdash.atlassian.net/browse/TS-2754)

## 3.7.93
 - Add passing of detail to Mntl.utilities.customEvent [GLBE-5714](https://dotdash.atlassian.net/browse/GLBE-5714)

## 3.7.92
 - GLBE-5700 - Updated to latest version of hippodrome for new GeoIP client

## 3.7.91
 - JWPlayer: Turn vpaid contols on and set high z-index [GLBE-5689](https://dotdash.atlassian.net/browse/GLBE-5689)

## 3.7.90
 - Update ads.txt [GLBE-5704](https://dotdash.atlassian.net/browse/GLBE-5704)

## 3.7.89
 - Cleaned up for targeting around sc-page, injecting into structured content.  [GLBE-5597](https://dotdash.atlassian.net/browse/GLBE-5597)

## 3.7.88
 - Make FTL safe for different value types of `model.rtbTimeout`

## 3.7.87
 - Refactor Amazon RTB plugin to consume either the current response or PMP response. [GLBE-5601](https://dotdash.atlassian.net/browse/GLBE-5601)

## 3.7.86
 - Add maxDocPopulation task parameter to TaxeneRelationTask.getDescendentArticles [REF-1275](https://dotdash.atlassian.net/browse/REF-1275)

## 3.7.85 (Skip to v3.7.93 to fix bug in Mntl.utilities that breaks RTB tracking)
 - Refactor the RTB library to wait for bidding partners library's to load before calling for bids. [GLBE-5663](https://dotdash.atlassian.net/browse/GLBE-5663)

## 3.7.84
  - Cleaned up MDC and added more logging for missing Amazon ASINs [GLBE-5629](https://dotdash.atlassian.net/browse/GLBE-5629)

## 3.7.83
 - update url in net-neutrality-banner component [glbe-5672](https://dotdash.atlassian.net/browse/glbe-5672)

## 3.7.82
 - Escape cookie values for Boomerang evaluated JS [HLTH-3772](https://dotdash.atlassian.net/browse/HLTH-3772)

## 3.7.81
 - Update sc-ads.js to remove all ad sizes that wont fit in remaining right rail space [TS-2466](https://dotdash.atlassian.net/browse/TS-2466)

## 3.7.80
 - Inline image srcset should use image size as max width [LW-1670](https://dotdash.atlassian.net/browse/LW-1670)

## 3.7.79
 - Removed vpaid controls from JW player [REF-1269](https://dotdash.atlassian.net/browse/REF-1269)
 - Added adBreakStart event tracking for JW player [REF-1270](https://dotdash.atlassian.net/browse/REF-1270)

## 3.7.78
 - Update net neutrality component for May 2018 campaign [GLBE-5672](https://dotdash.atlassian.net/browse/GLBE-5672)

## 3.7.77
 - Add TOC-LIST component, task and model [HLTH-3732](https://dotdash.atlassian.net/browse/HLTH-3732)

## 3.7.76
 - Update commerce retailer whitelist [TS-2119](https://dotdash.atlassian.net/browse/TS-2119)

## 3.7.75
 - Add new SC block type: PROMO_LINK [LW-1642](https://dotdash.atlassian.net/browse/LW-1642)

## 3.7.74
 - Don't escape SC heading text because it can have HTML in it now (for emphasis, etc) [GLBE-5665](https://dotdash.atlassian.net/browse/GLBE-5665)

## 3.7.73
 - Make Mntl.Rtb.Tracking opt in [GLBE-5664](https://dotdash.atlassian.net/browse/GLBE-5664)

## 3.7.72
 - Pass state and active date values to multi retailer if available [HLTH-3736](https://dotdash.atlassian.net/browse/HLTH-3736)

## 3.7.71
 - Revert Image orientation logic for backward compatibility. square images should have an image ratio of 1 - 1.25.

## 3.7.70
 - Ads not fully clickable on SC content [LW-1645](https://dotdash.atlassian.net/browse/LW-1645)

## 3.7.69 (Go directly to 3.7.73 got opt in tracking)
 - Collect RTB load Metrics and send them to GA [GLBE-5542](https://dotdash.atlassian.net/browse/GLBE-5542)

## 3.7.68
 - Structured Content - Removed 30px height provision for header on inline images.

## 3.7.67
 - Revert accidental merge

## 3.7.66 (skip to 3.7.67)
 - Accidental commerce bugfix merge

## 3.7.65
 - Propagating release/3.5 to release/3.7 (3.5.171):
   - Components nested inside iterated components now collapse correctly [GLBE-5530](https://dotdash.atlassian.net/browse/GLBE-5530)

## 3.7.64
 - Structured Content - add link to image to open a css only lightbox; all images are lazy loaded [GLBE-5596](https://dotdash.atlassian.net/browse/GLBE-5596) [GLBE-5618](https://dotdash.atlassian.net/browse/GLBE-5618)

## 3.7.63
 - Add round to nutrition data in schema to the nearest whole number [TS-2539](https://dotdash.atlassian.net/browse/TS-2539)
 - Fix free marker errors from empty nutrition information [TS-2592](https://dotdash.atlassian.net/browse/TS-2539)b

## 3.7.62
 - Fix frequency setting for JWPlayer [GLBE-5648](https://dotdash.atlassian.net/browse/GLBE-5648)

## 3.7.61
 - Additional default setting for JWPlayer [GLBE-5643](https://dotdash.atlassian.net/browse/GLBE-5643), [GLBE-5644](https://dotdash.atlassian.net/browse/GLBE-5644)

## 3.7.60
 - Fixed `retailers` being null for list templates [GLBE-5637](https://dotdash.atlassian.net/browse/GLBE-5637)

## 3.7.59
 - Add pre-roll and delete resize event for JWPlayer analytics. [GLBE-5638](https://dotdash.atlassian.net/browse/GLBE-5638)

## 3.7.58
 - Fix method that creates setup object. [GLBE-5633](https://dotdash.atlassian.net/browse/GLBE-5633)

## 3.7.57
 - Fix googletag event assignment before it's available. [GLBE-5635](https://dotdash.atlassian.net/browse/GLBE-5635)

## 3.7.56
 - Various under-the-cover redis improvements (globe-core update)

## 3.7.55
 -  Fix 500 when commerceInfo is missing on one of the items [GLBE-5623](https://dotdash.atlassian.net/browse/GLBE-5623)

## 3.7.54 (go directly to 3.7.57 for a bugfree rollaway leaderboard)
 -  Fix Bug introduced in 3.7.49 with rollaway leaderboard merge from 3.5 [GLBE-5620](https://dotdash.atlassian.net/browse/GLBE-5620)

## 3.7.53
 -  SC Ads overlap when resizing window [LW-1602](https://dotdash.atlassian.net/browse/LW-1602)

## 3.7.52
 - Use docId instead of url for pattern library document requests as vw domain will change

## 3.7.51 (go directly to 3.7.38 for bug fixes)
 - Propagating release/3.5 to release/3.7 (3.5.170):
   - Add JWPlayer to mantle [GLBE-5506](https://dotdash.atlassian.net/browse/GLBE-5506)

## 3.7.50
 - Add css overrides for basic Structured Content appearance. [GLBE-5594](https://dotdash.atlassian.net/browse/GLBE-5594)

## 3.7.49 (bug introduced go directly to 3.7.54)
 - Propagating release/3.5 to release/3.7 (3.5.168-169):
   - Add listener and timer for rollaway leaderboard to confirm it does not roll away before a viewable impression. [GLBE-5586](https://dotdash.atlassian.net/browse/GLBE-5586)
   - Venus Changes: Make Vertical Test URL agnostic of env and domain GLBE-5527

## 3.7.48
 - SC ads shouldn't lock on short articles [REF-1225](https://dotdash.atlassian.net/browse/REF-1225)

## 3.7.47
 - Added link capability to structured content headers [GLBE-5600](https://dotdash.atlassian.net/browse/GLBE-5600)

## 3.7.46
 - Update the preconnect list to add some new domains that have started showing up (mostly google); re-order to reflect actual inclusion order; remove Doubleverify since most verticals don't use it.

## 3.7.45
 - Automation: Add generic commerce model to MntlCommerceWidgetComponent and update commerceWidgetImage css selector

## 3.7.44
 - Add filterChildren method to TaxeneRelationTask to allow for custom removal of taxene relationships from a node. Useful for removing redirects from breadcrumbs results, for instance.

## 3.7.43
 - Update CommerceService,DocumentTaxeneService,DocumentTaxeneCuratedListTask to support passing in an executorservice to avoid using forkjoinpool parallel streams, the old constructors are deprecated.

## 3.7.42
 - [3.5.167] Update to globe-core 3.6.15 to allow script/style resources to be removed by path [GLBE-5589](https://dotdash.atlassian.net/browse/GLBE-5589)

## 3.7.41
  - Added content field to quizzes [REF-1133](https://dotdash.atlassian.net/browse/REF-1133)

## 3.7.40
  - Do not add the `sliced` class on the first item of the sliced block [GLBE-5566](https://dotdash.atlassian.net/browse/GLBE-5566)
  - SC _ Article with list content numbers are broken  [LW-1591](https://dotdash.atlassian.net/browse/LW-1591)

## 3.7.39
  - Propagating release/3.5 to release/3.7 (3.5.164-166):
    - Moving Datalayer tests to mantle [TS-2104] (https://dotdash.atlassian.net/browse/TS-2104)
    - CHANGELOG fixes

## 3.7.38
 - Check for 'right-rail__item' class in _handleResize() before placing ad in right rail. This fixes ad placement issues when resizing to right rail breakpoint (https://dotdash.atlassian.net/browse/HLTH-3529)

## 3.7.37
 - Add TaxonomyDocumentEx constructor for image cascading support (https://dotdash.atlassian.net/browse/HLTH-3433)

## 3.7.36
 - Removed css for Structured Content HTML blocks (no ticket)

## 3.7.35
 - Update to globe-core 3.6.14: Fix bug where unconcatenated resource requests were being duplicated on deferred loading [GLBE-5570](https://dotdash.atlassian.net/browse/GLBE-5570)

## 3.7.34
  - Add Sailthru event trigger (e.g. Welcome Email Series) for Newsletter signup [REF-1136](https://dotdash.atlassian.net/browse/REF-1136)

## 3.7.33
  - Add a class to Sliced HTML content blocks [GLBE-5566](https://dotdash.atlassian.net/browse/GLBE-5566)

## 3.7.32
  - Fix caching issue on star rating where user submissions would not update cache [TS-2325](https://dotdash.atlassian.net/browse/TS-2325)

## 3.7.31
  - Propagating release/3.5 to release/3.7 (3.5.163):
   - [rollback] Added a SC prerequisite class on `mntl-gpt-dynamic-adunit` component.

## 3.7.30
  - Fix caching for curatedlist with regards to DocumentTaxeneComposite[TS-2371](https://dotdash.atlassian.net/browse/TS-2371)

## 3.7.29
  - Updated to globe-core 3.6.13 which has Redis 2x TTL issue quick-fix [GLBE-5557](https://dotdash.atlassian.net/browse/GLBE-5557)

## 3.7.28
  - Added caching capabilities to DocumentTaxeneComposite[TS-2354](https://dotdash.atlassian.net/browse/TS-2354)

## 3.7.27
  - Updated globe-core version to 3.6.12

## 3.7.26
  - Fix caching issue on star rating where user submissions would not update cache [TS-2325](https://dotdash.atlassian.net/browse/TS-2325)

## 3.7.25
  - Null Check for reading accept headers for thumbor macro
  - Added a Toggle for webp optimization for thumbor macro (param: webpAuto)
  - Removed default thumborfilter for images on cards.

## 3.7.24
  - Propagating release/3.5 to release/3.7 (3.5.162):
    - - [rollback] Added a SC prerequisite class on `mntl-gpt-dynamic-adunit` component.


## 3.7.23
  - Updated Leaderboard spacer selector specificity to allow easier overrides [LW-1574]     
    (https://dotdash.atlassian.net/browse/LW-1574)
  - Propagating release/3.5 to release/3.7 (3.5.162):
    - - Updated SC Ads, inline content class name.
    - - Added a SC prerequisite class on `mntl-gpt-dynamic-adunit` component. )

## 3.7.22
  - Propagating release/3.5 to release/3.7 (3.5.161):
    -  - Fix structured content ads placement so events fire at the proper times when the script is in the head of the document. [GLBE-5538](https://dotdash.atlassian.net/browse/GLBE-5538)

## 3.7.21
  - Allowed leaderboard to be collapsed only after impressionViewable event through config (https://dotdash.atlassian.net/browse/LW-1549)

## 3.7.20
  - Add simplified journey task methods to cleanup vertical code  [HLTH-3421](https://dotdash.atlassian.net/browse/HLTH-3421)

## 3.7.19
  - Rollaway Leaderboard leaves a space between the content and the header on smaller breakpoints [LW-1574](https://dotdash.atlassian.net/browse/LW-1574)

## 3.7.18
  - Changed "commerce-info-loaded" event trigger to pass commerce data to event handlers

## 3.7.17
  - Use ftl comments instead of html comments in utility macros to prevent bugs when using.

## 3.7.16
  - Propagating release/3.5 to release/3.7 (3.5.159 - 160):
    - Add event to sc-ads to toggle the scroll handler [GLBE-5531](https://dotdash.atlassian.net/browse/GLBE-5531)
    - Trigger an event when inline-chop opens [TS-2304](https://dotdash.atlassian.net/browse/TS-2304)

## 3.7.15
  - Propagating release/3.5 to release/3.7 (3.5.156 - 158):
    - Quality of life update to utilties.js and its tests to simplify merges into master.
    - Implemented Walmart API (price only) [GLBE-5488](https://dotdash.atlassian.net/browse/GLBE-5488)
    - add filter that rejects malformed URLs with 400 response code [GLBE-5532](https://dotdash.atlassian.net/browse/GLBE-5532)

## 3.7.14
  - Implement default thumbor optimization filters
   (https://dotdash.atlassian.net/browse/LW-1550)

## 3.7.13
 - Add calculateImageCount to BaseDocumentEx instead of getImages (https://dotdash.atlassian.net/browse/HLTH-3407)

## 3.7.12
 - Fix TaxeneRelationTask so it does not update previously cached values when setting descendants (https://dotdash.atlassian.net/browse/HLTH-3432) Fixes 3.7.0

## 3.7.11
  - Propagating release/3.5 to release/3.7 (3.5.155):
  - update gtm to include recircDocIdsFooter metric [TS-2156](https://dotdash.atlassian.net/browse/TS-2156)
   - Prevent inline-chop button click from bubbling up [TS-2280](https://dotdash.atlassian.net/browse/TS-2280)

## 3.7.10
  - Propagating release/3.5 to release/3.7 (3.5.153):
  - Change og meta tag from meta name to meta property. [TS-2283](https://dotdash.atlassian.net/browse/TS-2283)

## 3.7.9
  - Allowed a class to be passed into the chop-content location though configuration

## 3.7.8
 - Propagating release/3.5 to release/3.7 (3.5.151 - 3.5.152):
   - Fix eclipse warnings by switching to parameterized types (using `<?>`)
   - Add support for sending slotName in the Amazon RTB plugin. Set property amazonSlotName to `true` in mntl-rtb-definition. [GLBE-5435](https://dotdash.atlassian.net/browse/GLBE-5435)

## 3.7.7
 - Add subheading field to StructuredContentDocumentEx model

## 3.7.6
 - Propagating release/3.5 to release/3.7 (3.5.150):
   - Fix line breaks in class definition of component macro as introduced in 3.5.138. [GLBE-5523](https://dotdash.atlassian.net/browse/GLBE-5523) Fixes 3.5.138

## 3.7.5
 - Propagating release/3.5 to release/3.7 (3.5.147 - 3.5.149):
   - Fix commas in liftIgniter metadata tags. [TS-2274](https://dotdash.atlassian.net/browse/TS-2274) Fixes 3.5.147
   - Only pass documentId if the request is a document request (eg ignore resource requests).  Fixes 3.5.146
   - Add Front End and Back End Lift Igniter support [TS-2070](https://dotdash.atlassian.net/browse/TS-2070) [TS-2069](https://dotdash.atlassian.net/browse/TS-2069)

## 3.7.4
 - Propagating release/3.5 to release/3.7 (3.5.136 - 3.5.146):
   - Added requestId, documentId, and url to Kibana logs. [GLBE-5486](https://dotdash.atlassian.net/browse/GLBE-5486)
   - Fixes utilities.js method getData that returned falsy values as strings. [GLBE-5520](https://dotdash.atlassian.net/browse/GLBE-5520)
   - Fix effective template by removing changeUrl functionality in contributor for effectivetemplate [HLTH-3393](https://dotdash.atlassian.net/browse/HLTH-3393)
   - Selene auth - make 3.7 change backward compatible with 3.5 ( switch to HTTPS and removing one of the selene key is not part of this ticket)
   - Add mechanism to mark an item as not click-tracked and completely exit out of the click-tracking flow [TS-2134](https://dotdash.atlassian.net/browse/TS-2134)
   - Added resource versions to serverStatus, and removed unnecessary environment comments in main layout (GLBE-5489 and GLBE-5191)
   - Exempt "/1" from setting PC cookie, to avoid [LW-1526](https://dotdash.atlassian.net/browse/LW-1526)
   - Ads logic to structured content ads code to load non-billboard ads such as native ads and renames several functions and variables to be more human readable [GLBE-5496](https://dotdash.atlassian.net/browse/GLBE-5496)
   - Refactor component macro to to handle classes set in xml as a list (`<list>`) or string. [GLBE-5192](https://dotdash.atlassian.net/browse/GLBE-5192)
   - Add IAS view ability measurements [GLBE-5503](https://dotdash.atlassian.net/browse/GLBE-5503)
   - mntl-schema-document image property is now configurable via xml. Defaults to document's primaryImage. [BLNC-2621](https://dotdash.atlassian.net/browse/BLNC-2621)

## 3.7.3
 - Add docId null check for breadcrumb (https://dotdash.atlassian.net/browse/HLTH-3402)(https://dotdash.atlassian.net/browse/HLTH-3403)

## 3.7.2
 - Make CuratedDocumentEx comparable to allow deserialization when contained in an ordered structure (https://dotdash.atlassian.net/browse/HLTH-3400)

## 3.7.1
 - ImageEx.isEmpty should check for objectId instead of url (https://dotdash.atlassian.net/browse/GLBE-5483)

## 3.7.0

  See [Globe 3.7 Release Notes](https://dotdash.atlassian.net/wiki/spaces/TECH/pages/4327748/Mantle+3.7+Upgrade+Instructions).

  - Redis support
    - Add persistent external cache template with built-in support for Redis
    - Yet more Amazon Serialization errors
    - (Globe core update) Added flag to remove in-memory wrapper from persistent caching (eg redis)[GLBE-5437](https://dotdash.atlassian.net/browse/GLBE-5437)
  - Use taxene v2 and remove support for v1 [HLTH-3292](https://dotdash.atlassian.net/browse/HLTH-3292)
  - Migrate metrics logging from InfluxDB to Telegraf (update to Hippodrome version 1.58) [GLBE-5415](https://dotdash.atlassian.net/browse/GLBE-5415)
  - Flex Impact Leaderboard
    - overall expansion of mantle's leaderboard functionality, esp. as it relates to "Flex v2" ads [GLBE-5443](https://dotdash.atlassian.net/browse/GLBE-5443)
    - Instantiate leaderboard rollaway behavior based off of the correct component [GLBE-5498](https://dotdash.atlassian.net/browse/GLBE-5498)
  - Require publisher logoWidth and logoHeight sizes for schemas (https://dotdash.atlassian.net/browse/BLNC-2244)
  - Remove all DFPLineItem-related classes and config from Mantle. (https://dotdash.atlassian.net/browse/GLBE-5389)
  - Improve output of "optionalTag" and "component" macros (https://dotdash.atlassian.net/browse/GLBE-5407)
  - Fix minor issues with amazon ecommerce [TS-1948](https://dotdash.atlassian.net/browse/TS-1948)
  - [3.5.82-3.5.136] Propagating release/3.5 to release/3.6 (see below for details)

## Release 3.6.x

Dead release.  Do not use.

## Release 3.5.x

## 3.5.171
  - Components nested inside iterated components now collapse correctly [GLBE-5530](https://dotdash.atlassian.net/browse/GLBE-5530)

## 3.5.170
 - Add JWPlayer to mantle [GLBE-5506](https://dotdash.atlassian.net/browse/GLBE-5506)

## 3.5.169
 - Add listener and timer for rollaway leaderboard to confirm it does not roll away before a viewable impression. [GLBE-5586](https://dotdash.atlassian.net/browse/GLBE-5586)

## 3.5.168
 - Venus Changes: Make Vertical Test URL agnostic of env and domain GLBE-5527

## 3.5.167
 - Update to globe-core 3.5.35 to allow script/style resources to be removed by path [GLBE-5589](https://dotdash.atlassian.net/browse/GLBE-5589)

## 3.5.166
 - CHANGELOG fixes only.  No functional changes.

## 3.5.165
 - Moving Datalayer tests to mantle [TS-2104] (https://dotdash.atlassian.net/browse/TS-2104)

## 3.5.164
 - [rollback] Added a SC prerequisite class on `mntl-gpt-dynamic-adunit` component.

## 3.5.163
 - BROKEN DO NOT USE

## 3.5.162
 - Added a SC prerequisite class on `mntl-gpt-dynamic-adunit` component.
 - Updated SC Ads, inline content class name.

## 3.5.161
 - Fix structured content ads placement so events fire at the proper times when the script is in the head of the document. [GLBE-5538](https://dotdash.atlassian.net/browse/GLBE-5538)

## 3.5.160
 - Add event to sc-ads to toggle the scroll handler [GLBE-5531](https://dotdash.atlassian.net/browse/GLBE-5531)

## 3.5.159
 - Trigger an event when inline-chop opens [TS-2304](https://dotdash.atlassian.net/browse/TS-2304)

## 3.5.158
 - Quality of life update to utilties.js and its tests to simplify merges into master.

## 3.5.157
 - Implemented Walmart API (price only) [GLBE-5488](https://dotdash.atlassian.net/browse/GLBE-5488)

## 3.5.156
 - add filter that rejects malformed URLs with 400 response code [GLBE-5532](https://dotdash.atlassian.net/browse/GLBE-5532)

## 3.5.155
 - update gtm to include recircDocIdsFooter metric [TS-2156](https://dotdash.atlassian.net/browse/TS-2156)

## 3.5.154
 - Prevent inline-chop button click from bubbling up [TS-2280](https://dotdash.atlassian.net/browse/TS-2280)

## 3.5.153
 - Change og meta tag from meta name to meta property. [TS-2283](https://dotdash.atlassian.net/browse/TS-2283)

## 3.5.152
 - Fix eclipse warnings by switching to parameterized types (using `<?>`)

## 3.5.151
 - Add support for sending slotName in the Amazon RTB plugin. Set property amazonSlotName to `true` in mntl-rtb-definition. [GLBE-5435](https://dotdash.atlassian.net/browse/GLBE-5435)

## 3.5.150
 - Fix line breaks in class definition of component macro as introduced in 3.5.138. [GLBE-5523](https://dotdash.atlassian.net/browse/GLBE-5523) Fixes 3.5.138

## 3.5.149
 - Fix commas in liftIgniter metadata tags. [TS-2274](https://dotdash.atlassian.net/browse/TS-2274) Fixes 3.5.147

## 3.5.148
 - Only pass documentId if the request is a document request (eg ignore resource requests).  Fixes 3.5.146

## 3.5.147 (breaking, skip to 3.5.149)
 - Add Front End and Back End Lift Igniter support [TS-2070](https://dotdash.atlassian.net/browse/TS-2070) [TS-2069](https://dotdash.atlassian.net/browse/TS-2069)

## 3.5.146 (breaking, skip to 3.5.148)
 - Added requestId, documentId, and url to Kibana logs. [GLBE-5486](https://dotdash.atlassian.net/browse/GLBE-5486)

## 3.5.145
 - Fixes utilities.js method getData that returned falsy values as strings. [GLBE-5520](https://dotdash.atlassian.net/browse/GLBE-5520)

## 3.5.144
 - Fix effective template by removing changeUrl functionality in contributor for effectivetemplate [HLTH-3393](https://dotdash.atlassian.net/browse/HLTH-3393)

## 3.5.143
 - Selene auth - make 3.7 change backward compatible with 3.5 ( switch to HTTPS and removing one of the selene key is not part of this ticket)

## 3.5.142
 - Add mechanism to mark an item as not click-tracked and completely exit out of the click-tracking flow [TS-2134](https://dotdash.atlassian.net/browse/TS-2134)

## 3.5.141
 - Added resource versions to serverStatus, and removed unnecessary environment comments in main layout (GLBE-5489 and GLBE-5191)

## 3.5.140
 - Exempt "/1" from setting PC cookie, to avoid [LW-1526](https://dotdash.atlassian.net/browse/LW-1526)

## 3.5.139
 - Ads logic to structured content ads code to load non-billboard ads such as native ads and renames several functions and variables to be more human readable [GLBE-5496](https://dotdash.atlassian.net/browse/GLBE-5496)

## 3.5.138 (breaking, skip to 3.5.150)
 - Refactor component macro to to handle classes set in xml as a list (`<list>`) or string. [GLBE-5192](https://dotdash.atlassian.net/browse/GLBE-5192)

## 3.5.137
 - Add IAS view ability measurements [GLBE-5503](https://dotdash.atlassian.net/browse/GLBE-5503)

## 3.5.136
 - mntl-schema-document image property is now configurable via xml. Defaults to document's primaryImage. [BLNC-2621](https://dotdash.atlassian.net/browse/BLNC-2621)

## 3.5.135
 -  Undo merged code in 3.5.134

## 3.5.134 DO NOT USE GO TO 3.5.135
 - Ad slot name key value pair to amazon bidder [GLBE-5435](https://dotdash.atlassian.net/browse/GLBE-5435)

## 3.5.133
 - Improve commerce URL-tagging integration by explicitly calling externalize-links [GLBE-5501](https://dotdash.atlassian.net/browse/GLBE-5501)

## 3.5.132
 - Fix load time of sc-ads. Decouple placement and loading of ads. [GLBE-5466](https://dotdash.atlassian.net/browse/GLBE-5466)

## 3.5.131
 - Fixed changelog, no material changes.

## 3.5.130
 - component-list now collapses when appropriate. [GLBE-5442](https://dotdash.atlassian.net/browse/GLBE-5442)

## 3.5.129
 - Updated to latest Selene client [GLBE-5480](https://dotdash.atlassian.net/browse/GLBE-5480)

## 3.5.128
 - Bad proctor tests will cause server to fail to start.[GLBE-5438](https://dotdash.atlassian.net/browse/GLBE-5438)

## 3.5.127
 - skip last ad slot from structure content ads (https://dotdash.atlassian.net/browse/GLBE-5468)

## 3.5.126
 - Added `onError="halt"` for Structured Content "Document" model to deal with redirects [LW-1492](https://dotdash.atlassian.net/browse/LW-1492)

## 3.5.125
 - Change regex in amazon asin regex to catch % in the url for special characters.

## 3.5.124
 - Improve rail calculation for structured content ads [GLBE-5465] (https://dotdash.atlassian.net/browse/GLBE-5465)

## 3.5.123
 - Made all DFP line item calls into no-ops [GLBE-5460](https://dotdash.atlassian.net/browse/GLBE-5460)

## 3.5.122
 - Add arbitrary add load to sc-ads.js allowing for arbitrary ad loads. [GLBE-5464](https://dotdash.atlassian.net/browse/GLBE-5464)

## 3.5.121
 - Added commerce widget

## 3.5.120
 - Add in experienceType for Mini Journeys and experienceTypeName (journey root short heading) for all journeys.[TS-2090] (https://dotdash.atlassian.net/browse/TS-2090) [LW-1447] (https://dotdash.atlassian.net/browse/LW-1447)

## 3.5.119
 - Added a remaining time coutdown (hidden by default) to the video ad player (https://dotdash.atlassian.net/browse/REF-1044)

## 3.5.118 (Don't use, skip to 3.5.119)
 - Added a remaining time coutdown (hidden by default) to the video ad player (https://dotdash.atlassian.net/browse/REF-1044)

## 3.5.117 (Don't use, skip to 3.5.119)
 - Unneeded changes. Skip to 3.5.119

## 3.5.116
 - Remove addition of `data-click-tracked` to commerce-widget buttons (added in 3.5.113) [TS-2131](https://dotdash.atlassian.net/browse/TS-2131)

## 3.5.115
 - Add flag to allow click-tracking container to send a child url as a targetURL.  Add this flag to commerce-widget container. [TS-2138](https://dotdash.atlassian.net/browse/TS-2138)

## 3.5.114
 - Update commerce buttons with urls returned from commerceModel [TS-2125](https://dotdash.atlassian.net/browse/TS-2125)

## 3.5.113
 - Add escape hatch to click-tracking library -- any event marked as `data-click-tracked`, regardless of element type, will not fire click-tracking.
 - Temporarily set `data-click-tracked` attrs false for commerce-widget links before clicking, in order to prevent multiple tracking events firing. [TS-2131](https://dotdash.atlassian.net/browse/TS-2131)

## 3.5.112
 - Refactor extended-commerce-info javascript, add standalone skimlinks component that loads the skimlinks JS instead of loading as part of the extended-commerce javascript [TS-2129](https://dotdash.atlassian.net/browse/TS-2129)

## 3.5.111
 - Respond to flexible ads once ratio has been set. Harden flexible-ad code to better prevent style overrides and match frames when frameElement property is security-restricted. [GLBE-5443](https://dotdash.atlassian.net/browse/GLBE-5443)

## 3.5.110
 - Set a timeout for programmatically triggering click event on commerce-buttons from the container to ensure all tracking and 3rd party event handlers fire [TS-2125](https://dotdash.atlassian.net/browse/TS-2125)

## 3.5.109
 - Handle potential null commerceModel objects on commerceTask api response [TS-2117](https://dotdash.atlassian.net/browse/TS-2117)

## 3.5.108
 - Uncomment return of null of VendorLookupService but leave logging commented.
 - Stop throwing exception on failure to find an asin so it doesn't blow up the lookup for others.
 - Updated regex so that it catches latin and greek characters when matching for asin (TS-2107) [https://dotdash.atlassian.net/browse/TS-2107]

## 3.5.107
 - Trigger the ```commerce-info-loaded``` event only once after all data has been processed.
 - Read the computed ```price``` field from API response in commerce-widgets [TS-2106](https://dotdash.atlassian.net/browse/TS-2106)

## 3.5.106
 - Add sticky kit for structured content ads [GLBE-5416](https://dotdash.atlassian.net/browse/GLBE-5416)

## 3.5.105
 - Added Consul key to disable DoS filters.  Useful for PE Testing.  (https://dotdash.atlassian.net/browse/GLBE-5446)

## 3.5.104
 - Delete erroneous TODO [GLBE-5420](https://dotdash.atlassian.net/browse/GLBE-5420)

## 3.5.103
 - Removed noisy ecom logging (no ticket)

## 3.5.102
 - Backport of GLBE-5406 from mantle 3.6

## 3.5.101
 - Updating Venus to 1.2.20. Fixed bug with BizCategory Junit listener that was throwing NPE [LW-1433](https://dotdash.atlassian.net/browse/LW-1433)

## 3.5.100
 - Backporting of ecommerce changes from 3.6

## 3.5.99
 - New log testing endpoint [GLBE-5430](https://dotdash.atlassian.net/browse/GLBE-5430)

## 3.5.98
 - Add extended-commerce-info component to request api-based commerce data and update the UI [TS-1962](https://dotdash.atlassian.net/browse/TS-1962)

## 3.5.97
 - Refactor Commerce Widget to always include commerce button list and remove button list component. [TS-1961](https://dotdash.atlassian.net/browse/TS-1961)

## 3.5.96
 - Check for null port in redirect resolver and fix `ptax` and `tier` in GPT ad slots(https://dotdash.atlassian.net/browse/HLTH-3264)

## 3.5.95
 - Add null safety to sc-ads method \_getInlineAds (https://dotdash.atlassian.net/browse/GLBE-5411)

## 3.5.94
 - Minor bug fixes to sc-ads.js. (https://dotdash.atlassian.net/browse/GLBE-5423)

## 3.5.93
 - Update util method for in MantleRenderUtils for inline-chop (https://dotdash.atlassian.net/browse/TRIP-826,https://dotdash.atlassian.net/browse/TS-279)

## 3.5.92
 - Added `ptax` and `tier` to GPT ad slots [GLBE-5242](https://dotdash.atlassian.net/browse/GLBE-5242)
 - Check for isRemoteSecure when resolving redirect filter requests (https://dotdash.atlassian.net/browse/HLTH-3242)

## 3.5.91
 - Amazon ecom hotfix - CommerceModel now Serializable (no ticket)

## 3.5.90
 - (globe-core update) Components injected via <for> targeting can break loading order [GLBE-5433](https://dotdash.atlassian.net/browse/GLBE-5433)

## 3.5.89
 - Add isCommerceDocument to unifiedPageView [TS-1990](https://dotdash.atlassian.net/browse/TS-1990)

## 3.5.88
 - Fixed ecomm caching bug (https://dotdash.atlassian.net/browse/GLBE-5429)

## 3.5.87
 - pass in pageTargeting keys to buildGptUrl() so that we can use use tax0 value when building the ad call url for VW + microverticals (https://dotdash.atlassian.net/browse/HLTH-3232)

## 3.5.86
 - Made accordian nav titles toggleable through the config to support hiding titles for mini-journeys (https://dotdash.atlassian.net/browse/LW-1399)

## 3.5.85
- Fixed right clicking links in Firefox not opening the context menu due to an interaction in our click-tracking script (https://dotdash.atlassian.net/browse/LW-1406)

## 3.5.84
- Add commerce task to handle multibutton commerce tasks. (https://dotdash.atlassian.net/browse/TS-1963)

## 3.5.83
- Slice all documents, even if config string is null in order to generate character count (https://dotdash.atlassian.net/browse/HLTH-3217)


## 3.5.82
- Add util method for in MantleRenderUtils for inline-chop (https://dotdash.atlassian.net/browse/TRIP-826,https://dotdash.atlassian.net/browse/TS-279)

## 3.5.81
 - Update to globe-core 3.5.29, which fixes a bug introduced in 3.5.75 for microverticals, where rendered component ids would contain "null" ()https://dotdash.atlassian.net/browse/TS-2041)

## 3.5.80
 - Add ad placement for structured content (https://dotdash.atlassian.net/browse/GLBE-5373)

## 3.5.79
 - Count structured content images for GTM (https://dotdash.atlassian.net/browse/GLBE-5388)

## 3.5.78
 - Set numOfImages for unifiedpageview in GTM irrespective of Chapters experience (https://dotdash.atlassian.net/browse/REF-1017)

## 3.5.77
 - Automation-Refactor billboard ad call test (https://dotdash.atlassian.net/browse/TRIP-808)

## 3.5.76
 - updating venus to 1.2.19 for reference headless automation tests (https://dotdash.atlassian.net/browse/REF-1058)

## 3.5.75
 - Update to globe-core 3.5.28. Fixes bug with duped components in Pattern Library. (https://dotdash.atlassian.net/browse/GLBE-5339)

## 3.5.74
 - [3.4.184] Update ads.txt. (https://dotdash.atlassian.net/browse/GLBE-5393)

## 3.5.73
 - [3.4.183] Fixes bug in 3.4.181

## 3.5.72 (Don't use, skip to 3.5.73)
 - [3.4.182] Add keys "iasId" and "fr" to IAS RTB plugin. (https://dotdash.atlassian.net/browse/GLBE-5381)
 - [3.4.181]

## 3.5.71
- Move JS error tests to mantle (https://dotdash.atlassian.net/browse/TS-2017)

## 3.5.70
 - Add commerce widget outer container and commerce widget button list components (https://dotdash.atlassian.net/browse/TS-1959 & https://dotdash.atlassian.net/browse/TS-1960)
 - Add support for new commerceInfo field on list items from selene and updated to the 2.3.9.0 jar (https://dotdash.atlassian.net/browse/TS-1963)
 - Add access to map model entry in xml, add method to ContextRootObject.Str to capitalize string, update to globe core 3.5.27 jar (https://dotdash.atlassian.net/browse/TS-1959)
 - Add text-link component

## 3.5.69
 - Update to globe-core 3.5.26 and selene 2.38.0 for family and mind vertical setup (https://dotdash.atlassian.net/browse/HLTH-3099)

## 3.5.68
 - [3.4.180] Add isMiniJourney task to determine whether Journey only has one section (https://dotdash.atlassian.net/browse/BLNC-2195)

## 3.5.67
 - Encode fallback pinterest image url  (https://dotdash.atlassian.net/browse/HLTH-3076)

## 3.5.66
 - [3.4.179] Complete previously unusable slideshow component (https://dotdash.atlassian.net/browse/TRIP-779)

## 3.5.65
 - MISSING!

## 3.5.64
 - Added article-body schema for structured content documents (https://dotdash.atlassian.net/browse/LW-1377)

## 3.5.63
 - Update to globe-core 3.5.24, which fixes a bug with for-targeting into dynamic children of a component-list (https://dotdash.atlassian.net/browse/GLBE-5380)

## 3.5.62
 - Fix bug getting correct component top value in deferred scrolled components [TS-1681] (https://dotdash.atlassian.net/browse/TS-1681)

## 3.5.61
 - [3.4.178] update mantle print button test to support new chrome [LW-1363] (https://dotdash.atlassian.net/browse/LW-1363)

## 3.5.60
 - Forced module reload isolated to DirectoryModules only (https://dotdash.atlassian.net/browse/GLBE-5375)

## 3.5.59
 - MISSING!

## 3.5.58
 - [3.4.177] Added an event that is triggered when article is chopped (https://dotdash.atlassian.net/browse/LW-1334)

## 3.5.57
 - [3.4.176] Made video-player-autoplay-all-ads wait for loadedmetadata event to fire before playing to fix issues with preroll timeouts and event logging (https://dotdash.atlassian.net/browse/REF-962)

## 3.5.56
 - [3.4.175] Migrate amazon RTB AFTs to mantle-venus so that vertical just use same [LW-1355] (https://dotdash.atlassian.net/browse/LW-1355)
 - [3.4.174] Redirects now work when Vertical does not support underlying template type [GLBE-5364](https://dotdash.atlassian.net/browse/GLBE-5364)

## 3.5.55
 - [3.4.173] Updated venus Version (https://dotdash.atlassian.net/browse/REF-1021)
 - [3.4.172] Update IndexUniversal to suffix mobile ad ids with `_mobile` (https://dotdash.atlassian.net/browse/GLBE-5356)

## 3.5.55
 - [3.4.173] Updated venus Version (https://dotdash.atlassian.net/browse/REF-1021)
 - [3.4.172] Update IndexUniversal to suffix mobile ad ids with `_mobile` (https://dotdash.atlassian.net/browse/GLBE-5356)

## 3.5.54
 - [3.4.171] Remove resize events from Brightcove Player (https://dotdash.atlassian.net/browse/GLBE-5360)

## 3.5.53
 - [3.4.170] Remove redirect errors from Kibana (https://dotdash.atlassian.net/browse/GLBE-5239)

## 3.5.52
 - [3.4.169] Increase unit test reliablity for masonry-list.js [GLBE-5366](https://dotdash.atlassian.net/browse/GLBE-5366)

## 3.5.51
 - [3.4.168] Fix bug introduced in 3.4.166
 - [3.4.168] Add unit tests for masonry-list.js [GLBE-5176](https://dotdash.atlassian.net/browse/GLBE-5176)
 - [3.4.168] Add additional Journeys worktasks to support populating Travel Journeys carousel (https://dotdash.atlassian.net/browse/TRIP-825)

## 3.5.50 (Bug introduced from 3.4.166, use 3.5.51)

## 3.5.49
 - Fix CHANGELOG for 3.5.47

## 3.5.48
 - Update globe-core to 3.5.19: check dynamic component-lists' cachedForTargeters for IterationModel-dependant dependencies (https://dotdash.atlassian.net/browse/GLBE-5309)

## 3.5.47
 - Fix bug in journeyDocumentsAfter task by using .equals() instead of == to compare two docIds of type Long (https://dotdash.atlassian.net/browse/TRIP-779)

## 3.5.46
 - Merge 3.4 into 3.5
 - [3.4.165] Fix bug in journeyDocumentsAfter task by using .equals() instead of == to compare two docIds of type Long (https://dotdash.atlassian.net/browse/TRIP-779)

## 3.5.45
 - [3.4.164] Don't execute RTB libraries when google_nofetch is a url param (https://dotdash.atlassian.net/browse/GLBE-5341)

## 3.5.44
 - [3.4.163] Changed list/sbs schema to have item URLs with anchor tags for individual list items (https://dotdash.atlassian.net/browse/LW-1324)

## 3.5.43
 - [3.4.162] Add video player error events to data layer [GLBE-5338](https://dotdash.atlassian.net/browse/GLBE-5338)

## 3.5.42
 - Merged w/ 3.4.161

## 3.5.41
 - Add guestAuthor field for StructuredContentDocument model (https://dotdash.atlassian.net/browse/GLBE-5353)

## 3.5.40
 - Merge of 3.4 into 3.5:
   - Fix double comma schema bug introduced in 3.4.157
   - [3.4.157] Support configurable publisher logo size in schema (https://dotdash.atlassian.net/browse/BLNC-2244)
   - [3.4.158] js / css for deferred components now filtered by viewable / tests. (https://dotdash.atlassian.net/browse/GLBE-5343)

## 3.5.39
 - [3.4.157] Support configurable publisher logo size in schema (https://dotdash.atlassian.net/browse/BLNC-2244)

## 3.5.38
 - [3.4.156] Add ratio based flexible header ads with roll-away functionality (https://dotdash.atlassian.net/browse/GLBE-5235)

## 3.5.37
 - [3.4.155] updated venus version to latest changes for health micro verticalization (https://dotdash.atlassian.net/browse/HLTH-2989)

## 3.5.36
 - [3.4.154] Fix filepath to instructions.md and brand-guidelines.md for pattern library (https://dotdash.atlassian.net/browse/HLTH-3049)

## 3.5.33
 - [3.4.152] Fix bug where Lotame url params were not added to initial deferred slots [GLBE-5348](https://dotdash.atlassian.net/browse/GLBE-5348)

## 3.5.32
 - Update globe-core version for verywellfit support (https://dotdash.atlassian.net/browse/HLTH-3034)

## 3.5.31
 - Add servername and datacenter to kibana logs (https://dotdash.atlassian.net/browse/OPSAD-3907)

## 3.5.30
 - Update selene version and remove selene dependencies for content blocks and taxonomy/taxcon (https://dotdash.atlassian.net/browse/HLTH-3044)

## 3.5.29
 - Revert to parsing referer as URI and warn on failure (https://dotdash.atlassian.net/browse/HLTH-3046)

## 3.5.28
 - [3.4.150] Add image to accordion for journey icons [TS-1655](https://dotdash.atlassian.net/browse/TS-1655)

## 3.5.27
 - [3.4.149] Use lastPublished instead of updated (https://dotdash.atlassian.net/browse/HLTH-3000)
 - [3.4.148] Add ampPixel to SEO whitelist (https://dotdash.atlassian.net/browse/BLNC-2158)

## 3.5.26
 - [3.4.147] Add IAS to RTB Plugins [GLBE-5310](https://dotdash.atlassian.net/browse/GLBE-5310)

## 3.5.25
 - [Reverted in 3.5.29] Parse referrer as URL instead of URI in JSON access log (https://dotdash.atlassian.net/browse/HLTH-3012)

## 3.5.24
 - Updated to latest globe (3.5.11) and Hippodrome (1.42.1)

## 3.5.23
 - TODO!

## 3.5.22
 - [3.4.145] Amazon affiliate - Added support for items that Amazon marks as on sale but aren't actually on sale.  [GLBE-5335|https://dotdash.atlassian.net/browse/GLBE-5335]

## 3.5.21
 - Log request url and referrer on failure to parse CSRF token from post body (https://dotdash.atlassian.net/browse/HLTH-3001)

## 3.5.19
 - [3.4.144] Fix weird logic in doesLinkLeavePage (https://dotdash.atlassian.net/browse/GLBE-5330)

## 3.5.18
 - [3.4.143] Amazon affiliate - More null checking [GLBE-5331|https://dotdash.atlassian.net/browse/GLBE-5331]
 - [3.4.142] Amazon affiliate - remove parens around percents by default [GLBE-5326|https://dotdash.atlassian.net/browse/GLBE-5326]
 - [3.4.141] Prevent unwanted error logging in amazon affiliate widget when document missing
 - [3.4.140] Fix click tracking errors resulting from AdBlock Chrome Plugin (https://dotdash.atlassian.net/browse/GLBE-5129)
 - [3.4.139] Better handling of bad Amazon prices GLBE-5326 (https://dotdash.atlassian.net/browse/GLBE-5326)
 - [3.4.138] Add additional overloaded gtmPageViewWithJourneys tasks to meet all use cases. (https://dotdash.atlassian.net/browse/BLNC-2178)

## 3.5.17
 - [3.4.137] Create gtmPageViewWithJourneys task and deprecate gtmPageViewWithJourney task. The new task ensures that all journeys pages properly track gtm `taxonomyNodes` property (https://dotdash.atlassian.net/browse/BLNC-2178)

## 3.5.16
 - Add sizes property to Mantle Video Player (https://dotdash.atlassian.net/browse/GLBE-5299)

## 3.5.15
 - [3.4.136] Add support for flexible ad units (https://dotdash.atlassian.net/browse/GLBE-5234)

## 3.5.14
 - Propagating 3.4 into 3.5

## 3.5.13
 - [HLTH-2986] Add null check for quiz result task https://dotdash.atlassian.net/browse/HLTH-2986

## 3.5.12
 - [3.4.132] ContentCachingRequestWrapper no being passed to subsequent filters https://dotdash.atlassian.net/browse/HLTH-2979

## 3.5.10
 - [GLBE-5304] Encode only the href query param for quiz results social share urls https://dotdash.atlassian.net/browse/GLBE-5304

## 3.5.9
 - [GLBE-5260](https://dotdash.atlassian.net/browse/GLBE-5260) - Gave Info components a real ID

## 3.5.8
 - [3.4.125] Refactor IPDetector to use CIDR ranges (https://dotdash.atlassian.net/browse/GLBE-5194)

## 3.5.7
 - __Build Failure__ go directly to 3.5.8

## 3.5.6
 - [3.4.124] Add brand guidelines page to pattern library (https://dotdash.atlassian.net/browse/GLBE-5106)

## 3.5.5
 - Fix template reference for robots.txt [GLBE-5282](https://dotdash.atlassian.net/browse/GLBE-5282)

## 3.5.4
 - [3.4.123] updated venus version to 1.2.8 (https://dotdash.atlassian.net/browse/GLBE-5278)
 - [3.4.122] Update ads.txt [GLBE-5276](https://dotdash.atlassian.net/browse/GLBE-5276)

## 3.5.3
 - [3.4.121] Compare testIds to string when filtering out placeholder testids in the amazon affiliate tagger [TS-1710](https://dotdash.atlassian.net/browse/TS-1710)
 - [3.4.120] Improved error messages for failed CSRF token parsing

## 3.5.2
 - [3.4.119] Created a GPT function to add a size to an existing GPT slot (https://dotdash.atlassian.net/browse/LW-1208)

## 3.5.1
 - [3.4.118] Cache imgWidth and heightProp variables for masonry-list items to boost client performance [TS-1709](https://dotdash.atlassian.net/browse/TS-1709)
 - [3.4.117] Return null if blank id is passed to author task
 - [3.4.116] Add default scope to `Mntl.LazyAds.init` to prevent silent errors when `init` called with no scope (https://dotdash.atlassian.net/browse/BLNC-2083)
 - [3.4.115] Add 'amp' to seo whitelist so we can support amp url patterns (https://dotdash.atlassian.net/browse/BLNC-2081)

## 3.5.0
 - Basic Structured Content support
 - Global robots.txt file ([GLBE-5008](https://dotdash.atlassian.net/browse/GLBE-5008)).  Upgrade instructions on [the wiki](https://wiki.iacpublishing.com/display/TECH/Mantle+3.5+Upgrade+Instructions#Mantle3.5UpgradeInstructions-Globalrobots.txt)
 - Remove `references` from bio document model (https://dotdash.atlassian.net/browse/GLBE-5021)
 - Split out methods dependent on jquery from `utilities.js` to `utilities-built-with-jquery.js` ([GLBE-5077](https://dotdash.atlassian.net/browse/GLBE-5077))
 - Remove Taxene Task class deprecated constructor (https://dotdash.atlassian.net/browse/BLNC-1684)
 - Fix Facebook sharing for quiz results (https://dotdash.atlassian.net/browse/GLBE-5139)
 - Update to use globe-core 3.5.4, which adds basic support for the `<component-list>` tag (https://dotdash.atlassian.net/browse/GLBE-5116)
 - Thumbor URL - Removed deprecated 'bucket' field from config
 - Selene version upgrade from 2.25.1 to 2.30.2.  GLBE-5249 (Selene upgrade to latest client)
 - Renamed Content Blocks to HtmlSlice ([GLBE-5258](https://dotdash.atlassian.net/browse/GLBE-5258)).  Upgrade instructions on [the wiki](https://wiki.iacpublishing.com/display/TECH/Mantle+3.5+Upgrade+Instructions)

## Release 3.4.x

## 3.4.184
 - Update ads.txt. (https://dotdash.atlassian.net/browse/GLBE-5393)

## 3.4.183
 - Fixes bug in 3.4.181 -- only use journey root node for GTM when in journeys (not mini journeys) [TS-2030](https://dotdash.atlassian.net/browse/TS-2030)

## 3.4.182
 - Add keys "iasId" and "fr" to IAS RTB plugin. (https://dotdash.atlassian.net/browse/GLBE-5381)

## 3.4.181 (Don't use, skip ahead to 3.4.183)
 - Don't use journey root node for setting taxonomyNodes GTM value if journey root is a programmed summary. Specifically, this is useful for Mini Journeys where root nodes are programmed summaries with no taxonomy relationships. (https://dotdash.atlassian.net/browse/BLNC-2359)

## 3.4.180
 - Add isMiniJourney task to determine whether Journey only has one section (https://dotdash.atlassian.net/browse/BLNC-2195)

## 3.4.179
 - Complete previously unusable slideshow component (https://dotdash.atlassian.net/browse/TRIP-779)

## 3.4.178
 - update mantle print button test to support new chrome [LW-1363] (https://dotdash.atlassian.net/browse/LW-1363)

## 3.4.177
 - Added an event that is triggered when article is chopped (https://dotdash.atlassian.net/browse/LW-1334)

## 3.4.176
 - Made video-player-autoplay-all-ads wait for loadedmetadata event to fire before playing to fix issues with preroll timeouts and event logging (https://dotdash.atlassian.net/browse/REF-962)

## 3.4.175
 - Migrate amazon RTB AFTs to mantle-venus so that vertical just use same [LW-1355] (https://dotdash.atlassian.net/browse/LW-1355)

## 3.4.174
 - GLBE-5364 - Redirects now work when Vertical does not support underlying template type [GLBE-5364](https://dotdash.atlassian.net/browse/GLBE-5364)

## 3.4.173
 - Updated venus Version (https://dotdash.atlassian.net/browse/REF-1021)

## 3.4.172
 - Update IndexUniversal to suffix mobile ad ids with `_mobile` (https://dotdash.atlassian.net/browse/GLBE-5356)

## 3.4.171
 - Remove resize events from Brightcove Player (https://dotdash.atlassian.net/browse/GLBE-5360)

## 3.4.170
 - Remove redirect errors from Kibana (https://dotdash.atlassian.net/browse/GLBE-5239)

## 3.4.169
 - Increase unit test reliablity for masonry-list.js [GLBE-5366](https://dotdash.atlassian.net/browse/GLBE-5366)

## 3.4.168
 - Fix bug introduced in 3.4.166

## 3.4.167
 - Add unit tests for masonry-list.js [GLBE-5176](https://dotdash.atlassian.net/browse/GLBE-5176)

## 3.4.166 (Bug introduced, use 3.4.168)
 - Add additional Journeys worktasks to support populating Travel Journeys carousel (https://dotdash.atlassian.net/browse/TRIP-825)

## 3.4.165
 - Fix bug in journeyDocumentsAfter task by using .equals() instead of == to compare two docIds of type Long (https://dotdash.atlassian.net/browse/TRIP-779)

## 3.4.164 ???
 - Fix to Amazon caching (https://dotdash.atlassian.net/browse/GLBE-5343)

## 3.4.163
 - Don't execute RTB libraries when google_nofetch is a url param (https://dotdash.atlassian.net/browse/GLBE-5341)

## 3.4.162
 - Changed list/sbs schema to have item URLs with anchor tags for individual list items (https://dotdash.atlassian.net/browse/LW-1324)

## 3.4.161
 - Add video player error events to data layer [GLBE-5338](https://dotdash.atlassian.net/browse/GLBE-5338)
 - Update to Amazon caching to provide a specialized cache to deal with errors better (https://dotdash.atlassian.net/browse/TS-1886)

## 3.4.160
 - Fix to Amazon caching (https://dotdash.atlassian.net/browse/GLBE-5343)

## 3.4.159x
 - Fix double comma schema bug introduced in 3.4.157
 - [3.4.157] Support configurable publisher logo size in schema (https://dotdash.atlassian.net/browse/BLNC-2244)
 - [3.4.158] js / css for deferred components now filtered by viewable / tests. (https://dotdash.atlassian.net/browse/GLBE-5343)

## 3.4.157 (Bug introduced, skip ahead to 3.4.159)

## 3.4.156
 - Add ratio based flexible header ads with roll-away functionality (https://dotdash.atlassian.net/browse/GLBE-5235)

## 3.4.155
 - updated venus version to latest changes for health micro verticalization (https://dotdash.atlassian.net/browse/HLTH-2989)

## 3.4.154 [3.4.153]
 - Fix filepath to instructions.md and brand-guidelines.md for pattern library (https://dotdash.atlassian.net/browse/HLTH-3049)

## 3.4.152
 - Fix bug where Lotame url params were not added to initial deferred slots [GLBE-5348](https://dotdash.atlassian.net/browse/GLBE-5348)

## 3.4.151
 - automation test for publisher tag in schema [LW-1329](https://dotdash.atlassian.net/browse/LW-1329)

## 3.4.150
 - Add image to accordion for journey icons [TS-1655](https://dotdash.atlassian.net/browse/TS-1655)

## 3.4.149
 - Use lastPublished instead of updated (https://dotdash.atlassian.net/browse/HLTH-3000)

## 3.4.148
 - Add ampPixel to SEO whitelist (https://dotdash.atlassian.net/browse/BLNC-2158)

## 3.4.147
 - Add IAS to RTB Plugins [GLBE-5310](https://dotdash.atlassian.net/browse/GLBE-5310)

## 3.4.146
 - AUTOMATION: Lifewire Content Journeys (https://dotdash.atlassian.net/browse/LW-1237)
 - venus 1.2.9

## 3.4.145
 - Amazon affiliate - Added support for items that Amazon marks as on sale but aren't actually on sale.  [GLBE-5335|https://dotdash.atlassian.net/browse/GLBE-5335]

## 3.4.144
 - Fix weird logic in doesLinkLeavePage (https://dotdash.atlassian.net/browse/GLBE-5330)

## 3.4.143
 - Amazon affiliate - More null checking [GLBE-5331|https://dotdash.atlassian.net/browse/GLBE-5331]

## 3.4.142
 - Amazon affiliate - remove parens around percents by default [GLBE-5326|https://dotdash.atlassian.net/browse/GLBE-5326]

## 3.4.141
 - Prevent unwanted error logging in amazon affiliate widget when document missing

## 3.4.140
 - Fix click tracking errors resulting from AdBlock Chrome Plugin (https://dotdash.atlassian.net/browse/GLBE-5129)

## 3.4.139
 - Better handling of bad Amazon prices GLBE-5326 (https://dotdash.atlassian.net/browse/GLBE-5326)

## 3.4.138
 - Add additional overloaded gtmPageViewWithJourneys tasks to meet all use cases. (https://dotdash.atlassian.net/browse/BLNC-2178)

## 3.4.137
 - Create gtmPageViewWithJourneys task and deprecate gtmPageViewWithJourney task. The new task ensures that all journeys pages properly track gtm `taxonomyNodes` property (https://dotdash.atlassian.net/browse/BLNC-2178)

## 3.4.136
- Add support for flexible ad units (https://dotdash.atlassian.net/browse/GLBE-5234)

## 3.4.135
 - [GLBE-5283](https://dotdash.atlassian.net/browse/GLBE-5283) - Amazon affiliate link now uses URL provided by Amazon as opposed to the one provided by Selene.

## 3.4.134
 - Added promotional pricing capabilities to Amazon Advertising API. [GLBE-5274](https://dotdash.atlassian.net/browse/GLBE-5274)

## 3.4.133
 - Refactored Amazon Product Advertising API (aka Ecomm, Affiliate API) to use WSDL instead of REST. [GLBE-5306](https://dotdash.atlassian.net/browse/GLBE-5306)

## 3.4.132
 - [HLTH-2979] ContentCachingRequestWrapper no being passed to subsequent filters https://dotdash.atlassian.net/browse/HLTH-2979

## 3.4.131
- Support custom event handling for accordion toggle(https://dotdash.atlassian.net/browse/LW-1273)

## 3.4.130
 - Allow masonry-list to set height of images before fontReady and wait for fontReady before justifying the columns to speed up render performance [TS-1770](https://dotdash.atlassian.net/browse/TS-1770)

## 3.4.129
 - Add GTM pageview tasks that take into account journeys and updates the experienceType when in a journey [TS-1733](https://dotdash.atlassian.net/browse/TS-1733)

## 3.4.128
 - Added unique IDs on Accordion Headers for click tracking (https://dotdash.atlassian.net/browse/LW-1214)

## 3.4.127
 - Add copy markdown task to Gruntfile (https://dotdash.atlassian.net/browse/GLBE-5297)
 - Accordion Widget and Accordion Document List Widget (https://dotdash.atlassian.net/browse/LW-1210)

## 3.4.126
 - Added parameter for image size to the amazon-affiliate-component (https://dotdash.atlassian.net/browse/LW-1185)

## 3.4.125
 - Refactor IPDetector to use CIDR ranges (https://dotdash.atlassian.net/browse/GLBE-5194)

## 3.4.124
  - Add brand guidelines page to pattern library (https://dotdash.atlassian.net/browse/GLBE-5106)

## 3.4.123
 - updated venus version to 1.2.8 (https://dotdash.atlassian.net/browse/GLBE-5278)

## 3.4.122
 - Update ads.txt [GLBE-5276](https://dotdash.atlassian.net/browse/GLBE-5276)

## 3.4.121
 - Compare testIds to string when filtering out placeholder testids in the amazon affiliate tagger [TS-1710](https://dotdash.atlassian.net/browse/TS-1710)

## 3.4.120
 - Improved error messages for failed CSRF token parsing

## 3.4.119
 - Created a GPT function to add a size to an existing GPT slot (https://dotdash.atlassian.net/browse/LW-1208)

## 3.4.118
 - Cache imgWidth and heightProp variables for masonry-list items to boost client performance [TS-1709](https://dotdash.atlassian.net/browse/TS-1709)

## 3.4.117
 - Return null if blank id is passed to author task

## 3.4.116
 - Add default scope to `Mntl.LazyAds.init` to prevent silent errors when `init` called with no scope (https://dotdash.atlassian.net/browse/BLNC-2083)

## 3.4.115
 - Add 'amp' to seo whitelist so we can support amp url patterns (https://dotdash.atlassian.net/browse/BLNC-2081)

## 3.4.114
 - Update globe-core version to 3.4.9 to fix info component extension bug (https://dotdash.atlassian.net/browse/HLTH-2907)

## 3.4.113
 - Fixed quiz result issues where answers could be re-selected after quiz completion or after being answered for the first time in the case of Percent Correct quizzes. (https://dotdash.atlassian.net/browse/GLBE-5171)

## 3.4.112
 - Add new Amazon KVPs amznsz and amznp (https://dotdash.atlassian.net/browse/GLBE-5213)

## 3.4.111
 - Moved Money Journey Tasks to Mantle (https://dotdash.atlassian.net/browse/TS-1669)
 - Fixed issue where 404 page would throw error trying to route to document id 404 (https://dotdash.atlassian.net/secure/RapidBoard.jspa?rapidView=190&selectedIssue=TS-1573)

## 3.4.110
 - Dechain jquery addition/removal of event listeners to avoid javascript errors on certain versions of brightcove [TS-1675](https://dotdash.atlassian.net/browse/TS-1675)

## 3.4.109
 - Added video-player-autoplay-all-ads component to mantle that uses new video player script which will autoplay any video with preroll, and click to play
   any video without preroll (https://dotdash.atlassian.net/browse/REF-926)

## 3.4.108
 - Added article-feedback component for saving user's rating/feedback of an article (https://dotdash.atlassian.net/browse/LW-681)

## 3.4.107
 - Add method to handle JSON parse Syntax Errors (https://dotdash.atlassian.net/browse/GLBE-5247)

## 3.4.106
 - Check for additional data attribute so that we can track clicks on elements other than anchor tags and buttons (https://dotdash.atlassian.net/browse/BLNC-2045)

## 3.4.105
 - Add apple article ids to MetaDataEx

## 3.4.104
 - Remove reference to nodeType (taxene v1) in schema-breadcrumblist (https://dotdash.atlassian.net/browse/GLBE-5236)

## 3.4.103
- Update .nvmrc to point to the correct node version (https://dotdash.atlassian.net/browse/GLBE-5205)

## 3.4.102
- Remove dependency on author model for bio page schema (https://dotdash.atlassian.net/browse/GLBE-5201)

## 3.4.101
- Migrate Rollaway Leaderboard to Mantle (https://dotdash.atlassian.net/browse/GLBE-5038)

## 3.4.100
 - Add js closest function to utilities to use in place of jquery .closest().

## 3.4.99
 - Add component to inject CSS & JS test payloads from Proctor.

## 3.4.98
- Update builder for document taxene composite to include setting taxonomyDocAllLevelList.

## 3.4.97
- Added Overlap Location comparison, upgrade to venus 1.2.6  (https://dotdash.atlassian.net/browse/REF-902)

## 3.4.96
- Add dailyValue field to NutritionFact model (https://dotdash.atlassian.net/browse/HLTH-1464)

## 3.4.95
 - Add ability to avoid running masonry-list js by adding ```data-no-js=true``` as an attribute to any masonry-list component ([TS-1148](https://dotdash.atlassian.net/browse/TS-1148))

## 3.4.94
 - Add unit tests for deferred.js (https://dotdash.atlassian.net/browse/GLBE-4855)

## 3.4.93
 - Return primaryImage field from selene doc as introImage. To be renamed to primaryImage in v3.5 (https://dotdash.atlassian.net/browse/LW-1171)

## 3.4.92
 - Add Review field to MetaDataEx (https://dotdash.atlassian.net/browse/HLTH-2409)

## 3.4.91
 - Fixed issue with ad audio playing for a few seconds before muting. (https://dotdash.atlassian.net/browse/TS-1543)
 - Make amazon-affiliate-widget prime image configurable and add a class to it for styling at vertical-level [TS-1411](https://dotdash.atlassian.net/browse/TS-1411)
 - Adding fatContent property to the nutrition json schema (https://dotdash.atlassian.net/browse/HLTH-2806)

## 3.4.88-3.4.90
 - *Corrupted builds!  Do not use! See https://dotdash.slack.com/conversation/C2MLDF9NX/p1504803687000436 for details*

## 3.4.87
 - moving Scheme Attribute to mantle actions.xsd (https://dotdash.atlassian.net/browse/GLBE-5190)

## 3.4.86
 - Fix mantle unit test for readyAndDeferred (https://dotdash.atlassian.net/browse/GLBE-5047)

## 3.4.85
 - Don't apply CSRF filter to deferred-component requests (https://dotdash.atlassian.net/browse/GLBE-5187)

## 3.4.84
 - Add "atlas.local.[vertical]" to list of domains that can act as iframe parents (https://dotdash.atlassian.net/browse/GLBE-5134)

## 3.4.83
 - Fix deferred component load point (https://dotdash.atlassian.net/browse/GLBE-5164)

## 3.4.82
 - Schema markup test fix (https://dotdash.atlassian.net/browse/REF-859)
 - Future proofing Thumbor URL generator by allowing for buckets or no buckets.  (https://dotdash.atlassian.net/browse/GLBE-5172)

## 3.4.81
 - BUILD FAILED VERSION SKIPPED

## 3.4.80
 - Allow setting background/theme colors and overriding icon sizes in application manifest (https://dotdash.atlassian.net/browse/HLTH-2768)

## 3.4.79
 - Verify the size of the input list in the Utils.subList method in MantleContextRootObject class (https://dotdash.atlassian.net/browse/BLNC-1955)

## 3.4.78
 - Fix card mis-alignment in masonry list (https://dotdash.atlassian.net/browse/GLBE-5140)

## 3.4.77
 - Remove programmed summary template from sitemap solr query (https://dotdash.atlassian.net/browse/BLNC-1941)

## 3.4.76
 - Add a second constructor in the taxene task class, so that it will not break any vertical that extends this class. Note: this constructor will be removed in mantle 3.5 (https://dotdash.atlassian.net/browse/BLNC-1684)

## 3.4.75 (GO TO 3.4.76)
 - Add journey root to the breadcrumb (https://dotdash.atlassian.net/browse/BLNC-1684)

## 3.4.74
 - Updated selene version to 2.25.1
 - Add Flipboard social sharing (https://dotdash.atlassian.net/browse/REF-581)

## 3.4.73
 - Mantle print button automation component and test (https://dotdash.atlassian.net/browse/LW-1156)

## 3.4.72
 - Handle proxied responses without body content (https://dotdash.atlassian.net/browse/GLBE-5136)

## 3.4.71
 - Add Index Universal Wrapper. _Note: Not compatible with current index implementation_ (https://dotdash.atlassian.net/browse/GLBE-5087)

## 3.4.70
 - updated venus version. This change will allow vertical to create their test jars (https://dotdash.atlassian.net/browse/GLBE-4997)

## 3.4.69
 - Refactor Journey model files to include getTaxonomyDocAllLevelList method to Journey documents (https://dotdash.atlassian.net/browse/BLNC-1868)
 - Remove accidental commit to card.ftl for debugging purposes

## 3.4.68 (contains debug code, do not use)

## 3.4.67
 - Fixed pattern-library iframes not resizing correctly by removing height-change-listener iframe from page (https://dotdash.atlassian.net/browse/REF-828)

## 3.4.66
 - Updated amazon-affiliate-widget to use a backup image if none is returned by the Amazon API (https://dotdash.atlassian.net/browse/LW-1138)

## 3.4.65
 - Migrate Print Template and Button Component (https://dotdash.atlassian.net/browse/REF-812)

## 3.4.64
 - Handle CrossOriginFilter when AllowedOrigins is not supplied (extension of GLBE-5134)

## 3.4.63
 - Ad call expected objects for devices containing common values (https://dotdash.atlassian.net/browse/LW-1027)

## 3.4.62
 - Allow an additional init param for CrossOriginFilter, DOMAIN_NAME, that sets a number of "standard" subdomains into the CORS and Content Security Policy configs.  Add Atlas subdomains to that list (https://dotdash.atlassian.net/browse/GLBE-5134)

## 3.4.61
 - https://dotdash.atlassian.net/browse/HLTH-2585 https://dotdash.atlassian.net/browse/GLBE-5127
 - Remove one-tap AJAX call because mobile safari blocks automatically composing an email unless it is directly associated with a user gesture.
 - Automatically hide the one-tap url by base64 encoding it and decoding it on click.
 - Re-add target=_blank on the one-tap button.

## 3.4.60
 - Add tasks for retrieving and filtering journey documents (https://dotdash.atlassian.net/browse/BLNC-1588)

## 3.4.59
 - Pattern Library switch to iframe fix, venus update 1.2.4 (https://dotdash.atlassian.net/browse/LW-1132)

## 3.4.58
 - Added recirc taxonomy to Journey docs (https://dotdash.atlassian.net/browse/BLNC-1827)

## 3.4.57
 - Revert change to masonry-list.js introduced in 3.4.41 (https://dotdash.atlassian.net/browse/BLNC-1829)

## 3.4.56
 - Fail DataEng Api endpoint check gracefully and continue with startup (https://dotdash.atlassian.net/browse/REF-799)

## 3.4.55
 - Actually implement cache support for Instart LogicJavascript requests (https://dotdash.atlassian.net/browse/GLBE-5132)

## 3.4.54
 - Expose newsletter init function to fix newsletter signup in native ad (https://dotdash.atlassian.net/browse/HLTH-2743)

## 3.4.53
 - Fixed lazy-ads loading early after expanding chop by adding an event that gets triggered when the content height changes (https://dotdash.atlassian.net/browse/LW-1091)

## 3.4.52
 - Updated lazy-ad.js to use custom data-attribute offset for slots (https://dotdash.atlassian.net/browse/LW-1098)

## 3.4.51
 - Add short heading for Journey Root, and adding addition check for non-journey documents (https://dotdash.atlassian.net/browse/BLNC-1812)

## 3.4.50
 - Point `/sponsored` route to new AWS ELB for the Sponsored Content servers (https://dotdash.atlassian.net/browse/GLBE-5111)

## 3.4.49
 - Add. F.I.T.T. to our list of cb split exceptions (https://dotdash.atlassian.net/browse/HLTH-2716)

## 3.4.48
 - Use shortHeading in journey documents, by adding node attributes property on TaxeneNodeEx (https://dotdash.atlassian.net/browse/BLNC-1723)

## 3.4.47
 - Updated venus version to 1.2.3

## 3.4.46
 - Automation: stop page load improvements (https://dotdash.atlassian.net/browse/REF-809)

## 3.4.44
 - Always redirects to HTTPS (https://dotdash.atlassian.net/browse/HLTH-5124)

## 3.4.42
 - [3.3.72] Inserted a required pattern for email inputs (https://dotdash.atlassian.net/browse/HLTH-2575)
 - Remove tag property (h4) and use div for newsletter heading as per SEO request

## 3.4.41
 - ads.txt - removed a few entries and sorted the rest (https://dotdash.atlassian.net/browse/GLBE-5125)
 - [3.3.71] Fix alignment bug in masonry-list (https://dotdash.atlassian.net/browse/GLBE-5117)

## 3.4.40
 - One-tap mailto url fallback on base url when location is not a taxene node

## 3.4.39
 - Use shortHeading for Breadcrumb Schema (https://dotdash.atlassian.net/browse/GLBE-5097)

## 3.4.38
 - Fix Bug with 3.2.115. Trigger event in GPT with vanilla JS (https://dotdash.atlassian.net/browse/GLBE-5110)

## 3.4.37 [GO TO 3.3.38]
 - [3.2.115] Trigger event in GPT with vanilla JS (https://dotdash.atlassian.net/browse/GLBE-5110)

## 3.4.36
 - MntlCardComponent automation model update (https://dotdash.atlassian.net/browse/LW-1056)

## 3.4.35
 - Fix failing unit tests (https://dotdash.atlassian.net/browse/GLBE-5094)

## 3.4.34
 - Bounce exchange automation test (https://dotdash.atlassian.net/browse/REF-777)

## 3.4.33
 - Add ads.txt (https://dotdash.atlassian.net/browse/GLBE-5083)

## 3.4.32
 - Breaking change. Generate one-tap mailto url onclick (https://dotdash.atlassian.net/browse/HLTH-2585)

## 3.4.31
 - Updated to latest version of hippodrome (1.42).  Requires that influxdb password be encrypted

## 3.4.30
 - [3.3.68, 3.2.114] Added custom event to video-player-autoplay-ads when the preroll is a direct ad. For the event to push to data layer on all devices, the views attribute should be removed from the "autoplayAdIds" property. A different config setting to autoplay direct ads has been added to only autoplay ads on PC (https://dotdash.atlassian.net/browse/REF-756)

## 3.4.29
 - [3.3.67, 3.2.113] Allow arbitrary GPT slot targeting via xml config / data-attributes (https://dotdash.atlassian.net/browse/GLBE-5080)

## 3.4.28
 - [3.3.66] Fix slot ID for billboard for AOL bid (https://dotdash.atlassian.net/browse/LW-1088)

## 3.4.27
 - fix CHANGELOG for 3.4.26

## 3.4.26
 - [3.3.65, 3.2.111] Scroll slowly to component (https://dotdash.atlassian.net/browse/REF-775)

## 3.4.25
 - [3.3.63, 3.2.110] Allow the Specless ad-preview tool to iframe verticals: more domains (https://dotdash.atlassian.net/browse/GLBE-5093)

## 3.4.24
 - Upgrade to globe-core 3.3.10 for `<for>` targeting on micro-vertical override component fix (https://dotdash.atlassian.net/browse/GLBE-5081)
 - [3.3.61] Filter by taxonomy documents, when calling breadcrumb tasks in TaxeneTask.java (https://dotdash.atlassian.net/browse/BLNC-1695)

## 3.4.23
 - [3.3.60, 3.2.109] Fix CHANGELOG
 - [3.3.60, 3.2.108] Allow the Specless ad-preview tool to iframe verticals (https://dotdash.atlassian.net/browse/GLBE-5093)

## 3.4.22
 - [3.2.105] Remove videos from sitemap query (https://dotdash.atlassian.net/browse/GLBE-5043)

## 3.4.21
 - Remove reference to window.jQuery in click-tracking.js and allow $ global for ESLint (https://dotdash.atlassian.net/browse/HLTH-2685)

## 3.4.20
 - Bugfix - removed accidentally-added Spring debugging to logger

## 3.4.19
 - Updated to latest and greatest globe-core / hippodrome

## 3.4.18
 - Fixed video player merge issues (GLBE-5086)

## 3.4.17
 - [3.2.104] Don't allow lazyAds.init to run with no container (https://dotdash.atlassian.net/browse/REF-773)

## 3.4.16
 - [3.3.55, 3.2.102] Allow SEO domains to iframe verticals (https://dotdash.atlassian.net/browse/GLBE-5082)
 - [3.3.55, 3.2.101] Replace $.ajax with native XMLHttpRequest as to not rely on jQuery for RTB and prevent race condition (https://dotdash.atlassian.net/browse/GLBE-5074)

## 3.4.15
 - Add video player unit tests (https://dotdash.atlassian.net/browse/GLBE-5028)

## 3.4.14
 - Moved logging config to Mantle. Verticals will need to update their logging configs, described [here](https://wiki.iacpublishing.com/display/TECH/Mantle+3.4+Upgrade+Instructions#Mantle3.4UpgradeInstructions-LoggingConfigsmovedtoMantle) (https://dotdash.atlassian.net/browse/GLBE-5018)

## 3.4.13
 - Breaking change.  Migrated log configs to mantle ([GLBE-5018](https://dotdash.atlassian.net/browse/GLBE-5018)).  Instructions for verticals to update [here](https://wiki.iacpublishing.com/display/TECH/Mantle+3.4+Upgrade+Instructions#Mantle3.4UpgradeInstructions-LoggingConfigsmovedtoMantle).

## 3.4.12
 - [3.2.95] New: Net Neutrality banner (https://dotdash.atlassian.net/browse/GLBE-5072)

## 3.4.11
 - Revert change to pass in jquery object to utitlites.js IIFE

## 3.4.10
 - [3.3.51] Added alt to amazon-affiliate-widget product image (https://dotdash.atlassian.net/browse/HLTH-2638)

## 3.4.9
 - [3.3.50] Move journey tasks from The Balance to Mantle, and adding a filter to make sure to track only taxonomy documents in GTM Page View (https://dotdash.atlassian.net/browse/BLNC-1682)
 - [3.3.49] added more urls to StaticUrl.java

## 3.4.8
 - Remove Tax-config as a potential source for crop setting in image resizer

## 3.4.7
 - [3.3.48] updated venus version to 1.2.2

## 3.4.6
 - [3.3.46, 3.2.93] Remove mrb from mantle (https://dotdash.atlassian.net/browse/HLTH-2589)

## 3.4.5
 - Updated to globe 3.4.5 (https://dotdash.atlassian.net/browse/GLBE-5069)

## 3.4.4
 - [3.3.42, 3.2.92] Add usStateCode and deviceType to envData.client object that is pushed to dataLayer (https://dotdash.atlassian.net/browse/HLTH-2627)

## 3.4.0
 - Upgrade to jQuery Version 3.2.1 (https://dotdash.atlassian.net/browse/GLBE-4900)
 - Upgrade to globe-core 3.4.2, which supports the following changes:
   - Make extension of component models much more type-agnostic, so that `<property>` tags can override `<config>` or `<model>` tags, etc. (https://dotdash.atlassian.net/browse/GLBE-4933)
   - Change the inheritance mode of Pattern Library components such that inclusion-specific properties (e.g., the "card" component's "cta" property when it is being referenced inside the "masonry-list" component) take precedence over that child component's Pattern Library values (https://dotdash.atlassian.net/browse/GLBE-4937)
   - Support for using `<for>` tags to add/replace/remove `<script>` and `<style>` tags in components. (https://dotdash.atlassian.net/browse/GLBE-4898)
 - [GLBE-4979](https://dotdash.atlassian.net/browse/GLBE-4979) Bugfix - FTL no longer trying to compile stylesheet shown in PL dev panel
 - Replace JsHint with ESLint see [wiki](https://wiki.iacpublishing.com/display/TECH/Mantle+3.4+Upgrade+Instructions) for details (https://dotdash.atlassian.net/browse/GLBE-4939)
 - [GLBE-5022](https://dotdash.atlassian.net/browse/GLBE-5022) Add "X-Content-Type-Options: nosniff" Header
 - Decrypt configuration keys that we store encrypted (https://dotdash.atlassian.net/browse/GLBE-4948)

## Release 3.3.x

## 3.3.72
 - Inserted a required pattern for email inputs (https://dotdash.atlassian.net/browse/HLTH-2575)
 - Remove tag property (h4) and use div for newsletter heading as per SEO request

## 3.3.71
 - Fix alignment bug in masonry-list (https://dotdash.atlassian.net/browse/GLBE-5117)

## 3.3.70
 - Fix Bug with 3.2.115. Trigger event in GPT with vanilla JS (https://dotdash.atlassian.net/browse/GLBE-5110)

## 3.3.69 [GO TO 3.3.70]
 - [3.2.115] Trigger event in GPT with vanilla JS (https://dotdash.atlassian.net/browse/GLBE-5110)

## 3.3.68
 - [3.2.114] Added custom event to video-player-autoplay-ads when the preroll is a direct ad. For the event to push to data layer on all devices, the views attribute should be removed from the "autoplayAdIds" property. A different config setting to autoplay direct ads has been added to only autoplay ads on PC (https://dotdash.atlassian.net/browse/REF-756)

## 3.3.67
 - [3.2.113] Allow arbitrary GPT slot targeting via xml config / data-attributes (https://dotdash.atlassian.net/browse/GLBE-5080)

## 3.3.66
 - [3.2.112] Fix slot ID for billboard for AOL bid (https://dotdash.atlassian.net/browse/LW-1088)

## 3.3.65
 - fix CHANGELOG for 3.3.64

## 3.3.64
 - [3.2.111] Scroll slowly to component (https://dotdash.atlassian.net/browse/REF-775)

## 3.3.63
 - [3.2.110] Allow the Specless ad-preview tool to iframe verticals: more domains (https://dotdash.atlassian.net/browse/GLBE-5093)

## 3.3.62

 - Upgrade to globe-core 3.3.10 for `<for>` targeting on micro-vertical override component fix (https://dotdash.atlassian.net/browse/GLBE-5081)

## 3.3.61
 - Filter by taxonomy documents, when calling breadcrumb tasks in TaxeneTask.java (https://dotdash.atlassian.net/browse/BLNC-1695)

## 3.3.60
 - [3.2.109] Fix CHANGELOG
 - [3.2.108] Allow the Specless ad-preview tool to iframe verticals (https://dotdash.atlassian.net/browse/GLBE-5093)

## 3.3.59
 - [3.2.107] Remove videos from sitemap query (https://dotdash.atlassian.net/browse/GLBE-5043)

## 3.3.58
 - Added wait() for ScriptInclusionTest for Health automation tests failures(https://dotdash.atlassian.net/browse/HLTH-2687)

## 3.3.56
 - [3.2.106] Don't allow lazyAds.init to run with no container (https://dotdash.atlassian.net/browse/REF-773)

## 3.3.55
 - [3.2.102] Allow SEO domains to iframe verticals (https://dotdash.atlassian.net/browse/GLBE-5082)
 - [3.2.101] Replace $.ajax with native XMLHttpRequest as to not rely on jQuery for RTB and prevent race condition (https://dotdash.atlassian.net/browse/GLBE-5074)

## 3.3.54
 - [3.2.100] Defer lazy ads underneath mobile chop (https://dotdash.atlassian.net/browse/REF-721)
 - [3.2.99] Add html unescape function which can be used in xml configs

## 3.3.53
 - Syntax fix for Net Neutrality banner (https://dotdash.atlassian.net/browse/GLBE-5072)

## 3.3.52
 - [3.2.95] New: Net Neutrality banner (https://dotdash.atlassian.net/browse/GLBE-5072)

## 3.3.51
 - Added alt to amazon-affiliate-widget product image (https://dotdash.atlassian.net/browse/HLTH-2638)

## 3.3.50
 - Move journey tasks from The Balance to Mantle, and adding a filter to make sure to track only taxonomy documents in GTM Page View (https://dotdash.atlassian.net/browse/BLNC-1682)

## 3.3.49
 - added more urls to StaticUrl.java

## 3.3.48
 - updated venus version to 1.2.2

## 3.3.46
 - [3.2.93] Remove mrb from mantle (https://dotdash.atlassian.net/browse/HLTH-2589)

## 3.3.45
- update to Selene version 2.19.7

## 3.3.44
 - [3.2.92] Add usStateCode and deviceType to envData.client object that is pushed to dataLayer (https://dotdash.atlassian.net/browse/HLTH-2627)

## 3.3.41
 - JS escape RTB amazonSection string (https://dotdash.atlassian.net/browse/TS-1267)
 - Upgrade venus to 1.2.0

## 3.3.40 (Bug introduced, upgrade to 3.3.41)

## 3.3.39
 - [3.2.84] Updated to globe 3.3.9
 - [3.2.82] Project away hiddenFields on document calls (https://dotdash.atlassian.net/browse/GLBE-4964)

## 3.3.38
 - Fix newsletter migration bugs introduced in 3.2.77/3.3.35
 - [3.3.37, 3.2.80] Add X-Frame-Options header to CORS filter (https://dotdash.atlassian.net/browse/GLBE-5004)
 - [3.3.36, 3.2.79] Remove cross vertical 404 logic except for about.com documents (https://dotdash.atlassian.net/browse/HLTH-2586)
 - [3.3.36, 3.2.78] Add "X-Content-Type-Options: nosniff" Header (https://dotdash.atlassian.net/browse/GLBE-5022)
 - [3.3.35, 3.2.77] Migrate newsletter-signup component (https://dotdash.atlassian.net/browse/GLBE-4906)

## 3.3.35 - 3.3.37 (Bugs introduced, upgrade to 3.3.38)

## 3.3.34
 - Created new service for reading journey structure (https://dotdash.atlassian.net/browse/BLNC-1593)

## 3.3.33
 - [3.2.76] Fix pc cookie value incrementing due to manifest.json (https://dotdash.atlassian.net/browse/HLTH-2397)

## 3.3.32
 - [USE 3.3.33 FOR BUGFIX 3.2.75] Add-to-homescreen component (https://dotdash.atlassian.net/browse/HLTH-2397)

## 3.3.31
 - [3.2.74] Pass taxonomy section parameter to Amzaon RTB to allow for insights (https://dotdash.atlassian.net/browse/GLBE-4951)

## 3.3.30
 - [3.2.73] Fix namespace deserialization in Amazon Product Service (https://dotdash.atlassian.net/browse/LW-1063)

## 3.3.29
 - [3.2.72] Update AOL Header Bidder (https://dotdash.atlassian.net/browse/GLBE-4950)
 - Updated Venus to `1.0.4` from `1.1.1` (https://dotdash.atlassian.net/browse/GLBE-5026).  `1.1.1` was created erroneously

## 3.3.28
 - [3.2.70] Add Vary header to prevent caching of CORS Origin header at the netscaler level (https://dotdash.atlassian.net/browse/HLTH-2545)

## 3.3.27
 - [3.2.69] Implement rate limiting in Mantle via an extension of Jetty's DoS filter.  Filter all requests at ~25 req/s with delays; filter POST requests at ~3 req/s and reject (https://dotdash.atlassian.net/browse/GLBE-5009)

## 3.3.26
 - Update Amazon Script test in mantle which can be used in  verticals (https://dotdash.atlassian.net/browse/GLBE-4910)

## 3.3.25
 - [3.2.68] in article video automation - add big play button to MntlVideoPlayerComponent (https://dotdash.atlassian.net/browse/REF-613)

## 3.3.24
 - [3.2.67] If present, append the _cc_id cookie value to the end of Lotame RTB library requests to support 1st-party cookie setting (https://dotdash.atlassian.net/browse/GLBE-4987)

## 3.3.23
 - fixed changelog for multiple 3.3.15 entries

## 3.3.22
 - fixed typo in changelog for 3.3.21

## 3.3.21
 - [3.2.66] Move Billboard ads test to mantle (https://dotdash.atlassian.net/browse/LW-964)

## 3.3.20
 - Allow passage of thumborFilters to mantle cards; allow injection of distinct class to masonry-list item (https://dotdash.atlassian.net/browse/TRIP-209)

## 3.3.19
 - [3.2.65] Add Unit tests for GA/GTM (https://dotdash.atlassian.net/browse/GLBE-4854)

## 3.3.18
 - Set default favicon path to append "/icons/favicons" to staticPath (https://dotdash.atlassian.net/browse/BLNC-1575)

## 3.3.17
 - [3.2.63] Update Mantle "/sponsored" proxy to new target (https://dotdash.atlassian.net/browse/GLBE-4959)

## 3.3.16
 - [3.2.62] Fix undefined function breaking OpenXLite (https://dotdash.atlassian.net/browse/GLBE-4980)

## 3.3.15
 - [3.2.61] Fix bug where site name is not being passed to email share (https://dotdash.atlassian.net/browse/HLTH-2512)
 - [3.2.61] Fix for iOS 8+ SMS social share: use & for SMS url scheme
 - [3.2.61] Allow all caps for social share button label

## 3.3.14
- [3.2.60] Fixed breaking call to check currentAd beforeit exists in video-player.js

## 3.3.13
 - [3.2.59] Prevent Brightcove players that do not have IMA3 enabled from throwing Javascript errors (https://dotdash.atlassian.net/browse/GLBE-4978)

## 3.3.12 [introduced bug, please go to 3.3.13]
 - [3.2.58] Added video player functionality to start the player on scroll. (https://dotdash.atlassian.net/browse/REF-560)
 - [3.2.58] Updated defer scroll to load components either by passing in either a custom offset, or by a scrollTop position.

## 3.3.11
 - [3.2.57] Fix deliver domain bug that prevented OpenXLite from working (https://dotdash.atlassian.net/browse/GLBE-4965)

## 3.3.10
 - [3.2.55] Added chop callback to update ad position for lazy ads to fix ads loading too early after chop is opened. (https://dotdash.atlassian.net/browse/LW-1023)

## 3.3.9 [introduced bug, please go to 3.3.11]
 - [3.2.54] Prevent multiple OpenXLite HeaderBidder objects per page (https://dotdash.atlassian.net/browse/GLBE-4955)
 - [3.2.53] Add Table of Contents flag to MetaDataEx (https://dotdash.atlassian.net/browse/HLTH-2319)

## 3.3.8
- [3.2.52] Fix HttpRequestContextContributor to stop redirecting servemodel tasks (https://dotdash.atlassian.net/browse/TRIP-386)

## 3.3.7
 - [3.2.51] Add unit tests for utilities.js (https://dotdash.atlassian.net/browse/GLBE-4856)

## 3.3.3 - 3.3.4
 - [3.2.49] Add curated list and curated list of list that based on the DocumentTaxeneComposite model and refactor the DocumentTaxeneCompositeTask to accept any document type (https://dotdash.atlassian.net/browse/BLNC-1470)

## 3.3.2
 - [3.2.48] Extract document service wrapper into its own method to allow overriding in spring config (Table of Contents: https://dotdash.atlassian.net/browse/HLTH-2423)

## 3.3.1
 - [3.2.47] Use caret matching to pull latest 0.3.X version of justified columns from bower

## 3.3.0
 - Consolidate static resource path logic into utility methods for drier code (https://dotdash.atlassian.net/browse/GLBE-4871)
 - Allow locale to be configurable for future localization (https://dotdash.atlassian.net/browse/GLBE-4808)
 - Implement Phase 2 ("clever CSS tricks") of Mantle card/masonry-card-list SEO optimizations (https://dotdash.atlassian.net/browse/GLBE-4912)

## Release 3.2.x

## 3.2.116
 - Fix Bug with 3.2.115. Trigger event in GPT with vanilla JS (https://dotdash.atlassian.net/browse/GLBE-5110)

## 3.2.115 [GO TO 3.2.116]
 - Trigger event in GPT with vanilla JS (https://dotdash.atlassian.net/browse/GLBE-5110)

## 3.2.114
 - Added custom event to video-player-autoplay-ads when the preroll is a direct ad. For the event to push to data layer on all devices, the views attribute should be removed from the "autoplayAdIds" property. A different config setting to autoplay direct ads has been added to only autoplay ads on PC (https://dotdash.atlassian.net/browse/REF-756)

## 3.2.113
 - Allow arbitrary GPT slot targeting via xml config / data-attributes (https://dotdash.atlassian.net/browse/GLBE-5080)

## 3.2.112
 - Fix slot ID for billboard for AOL bid (https://dotdash.atlassian.net/browse/LW-1088)

## 3.2.111
 - Scroll slowly to component (https://dotdash.atlassian.net/browse/REF-775)

## 3.2.110
 - Allow the Specless ad-preview tool to iframe verticals: more domains (https://dotdash.atlassian.net/browse/GLBE-5093)

## 3.2.109
 - Fix CHANGELOG

## 3.2.108
 - Allow the Specless ad-preview tool to iframe verticals (https://dotdash.atlassian.net/browse/GLBE-5093)

## 3.2.107
 - Remove videos from sitemap query (https://dotdash.atlassian.net/browse/GLBE-5043)

## 3.2.106
 - Don't allow lazyAds.init to run with no container (https://dotdash.atlassian.net/browse/REF-773)

## 3.2.103-105 (Jenkins failure, skip)

## 3.2.102
 - Allow SEO domains to iframe verticals (https://dotdash.atlassian.net/browse/GLBE-5082)

## 3.2.101
 - Replace $.ajax with native XMLHttpRequest as to not rely on jQuery for RTB and prevent race condition (https://dotdash.atlassian.net/browse/GLBE-5074)

## 3.2.100
 - Defer lazy ads underneath mobile chop (https://dotdash.atlassian.net/browse/REF-721)

## 3.2.99
 - Add html unescape function which can be used in xml configs

## 3.2.98 (No changes)

## 3.2.97 (Jenkins failure, skip)

## 3.2.96
 - Syntax fix for Net Neutrality banner (https://dotdash.atlassian.net/browse/GLBE-5072)

## 3.2.95
 - New: Net Neutrality banner (https://dotdash.atlassian.net/browse/GLBE-5072)

## 3.2.94
 - Fixed: RTB external scripts being double-included (https://dotdash.atlassian.net/browse/GLBE-5070)

## 3.2.93
 - Remove mrb from mantle (https://dotdash.atlassian.net/browse/HLTH-2589)

## 3.2.92
 - Add usStateCode and deviceType to envData.client object that is pushed to dataLayer (https://dotdash.atlassian.net/browse/HLTH-2627)

## 3.2.91
 - Fix bug introduced in 3.2.90, add 'self' to list of frame-ancestors so that iframe components display correctly in pattern library

## 3.2.90
  - Switch from `X-Frame-Options` response header with reflected Referer/Origin value to `Content-Security-Policy: frame-ancestors` with a static list of approved origins (https://dotdash.atlassian.net/browse/GLBE-5048)

## 3.2.89
 - Replace pattern library login with IP whitelist for authorizing model changes, increasing ease of use (https://dotdash.atlassian.net/browse/GLBE-5032)

## 3.2.88
 - Refactored video player. Added new logic for autoplaying video based on ad IDs. Added new service and task to get direct ad IDs from Data API (https://dotdash.atlassian.net/browse/REF-643)

## 3.2.87
 - JS escape RTB amazonSection string (https://dotdash.atlassian.net/browse/TS-1267)

## 3.2.86 (Bug introduced, upgrade to 3.2.87)

## 3.2.85 (No changes)

## 3.2.84
 - Updated to globe 3.3.9

## 3.2.83 (Jenkins failure, skip)

## 3.2.82
 - Project away hiddenFields on document calls (https://dotdash.atlassian.net/browse/GLBE-4964)

## 3.2.81
 - Fix newsletter migration bugs introduced in 3.2.77
 - [3.2.80] Add X-Frame-Options header to CORS filter (https://dotdash.atlassian.net/browse/GLBE-5004)
 - [3.2.79] Remove cross vertical 404 logic except for about.com documents (https://dotdash.atlassian.net/browse/HLTH-2586)
 - [3.2.78] Add "X-Content-Type-Options: nosniff" Header (https://dotdash.atlassian.net/browse/GLBE-5022)
 - [3.2.77] Migrate newsletter-signup component (https://dotdash.atlassian.net/browse/GLBE-4906)

## 3.2.77 - 3.2.80 (Bugs introduced, upgrade to 3.2.81)

## 3.2.76
 - Fix pc cookie value incrementing due to manifest.json (https://dotdash.atlassian.net/browse/HLTH-2397)

## 3.2.75
 - [USE 3.2.76 FOR BUGFIX] Add-to-homescreen component (https://dotdash.atlassian.net/browse/HLTH-2397)

## 3.2.74
 - Pass taxonomy section parameter to Amzaon RTB to allow for insights (https://dotdash.atlassian.net/browse/GLBE-4951)

## 3.2.73
 - Fix namespace deserialization in Amazon Product Service (https://dotdash.atlassian.net/browse/LW-1063)

## 3.2.72
 - Update AOL Header Bidder (https://dotdash.atlassian.net/browse/GLBE-4950)

## 3.2.71
 - Upgraded to globe-core 3.3.8. (https://bitbucket.prod.aws.about.com/projects/FRON/repos/globe-core/browse/CHANGELOG.md?at=refs%2Ftags%2F3.3.8)

## 3.2.70
 - Add Vary header to prevent caching of CORS Origin header at the netscaler level (https://dotdash.atlassian.net/browse/HLTH-2545)

## 3.2.69
 - Implement rate limiting in Mantle via an extension of Jetty's DoS filter.  Filter all requests at ~25 req/s with delays; filter POST requests at ~3 req/s and reject (https://dotdash.atlassian.net/browse/GLBE-5009)

## 3.2.68
 - in article video automation - add big play button to MntlVideoPlayerComponent (https://dotdash.atlassian.net/browse/REF-613)

## 3.2.67
 - If present, append the _cc_id cookie value to the end of Lotame RTB library requests to support 1st-party cookie setting (https://dotdash.atlassian.net/browse/GLBE-4987)

## 3.2.66
 - Move Billboard ads test to mantle (https://dotdash.atlassian.net/browse/LW-964)

## 3.2.65
 - Add Unit tests for GA/GTM (https://dotdash.atlassian.net/browse/GLBE-4854)

## 3.2.64
 - Update changelog

## 3.2.63
 - Update Mantle "/sponsored" proxy to new target (https://dotdash.atlassian.net/browse/GLBE-4959)

## 3.2.62
  - Fix undefined function breaking OpenXLite (https://dotdash.atlassian.net/browse/GLBE-4980)

## 3.2.61
 - Fix bug where site name is nto being passed to email share (https://dotdash.atlassian.net/browse/HLTH-2512)
 - Fix for iOS 8+ SMS social share: use & for SMS url scheme
 - Allow all caps for social share button label

## 3.2.60
 - Fixed breaking call to check currentAd beforeit exists in video-player.js

## 3.2.58 - 3.2.59 (Breaking change unintentionally introduced in 3.2.58. Upgrade directly to 3.2.60)

## 3.2.59
 - Prevent Brightcove players that do not have IMA3 enabled from throwing Javascript errors (https://dotdash.atlassian.net/browse/GLBE-4978)

## 3.2.58
 - Added video player functionality to start the player on scroll. (https://dotdash.atlassian.net/browse/REF-560)
 - Updated defer scroll to load components either by passing in either a custom offset, or by a scrollTop position.

## 3.2.57
 - Fix deliver domain bug that prevented OpenXLite from working (https://dotdash.atlassian.net/browse/GLBE-4965)

## 3.2.56
 - Make updates to support mantle-ref upgrade to Mantle 3.2, including pattern library fixes (https://dotdash.atlassian.net/browse/GLBE-4925)

## 3.2.55
 - Added chop callback to update ad position for lazy ads to fix ads loading too early after chop is opened. (https://dotdash.atlassian.net/browse/LW-1023)

## 3.2.54 [introduced bug, please go to 3.2.57]
 - Prevent multiple OpenXLite HeaderBidder objects per page (https://dotdash.atlassian.net/browse/GLBE-4955)

## 3.2.53
 - Add Table of Contents flag to MetaDataEx (https://dotdash.atlassian.net/browse/HLTH-2319)

## 3.2.52
- Fix HttpRequestContextContributor to stop redirecting servemodel tasks (https://dotdash.atlassian.net/browse/TRIP-386)

## 3.2.51
 - Add unit tests for utilities.js (https://dotdash.atlassian.net/browse/GLBE-4856)

## 3.2.49
 - Add curated list and curated list of list that based on the DocumentTaxeneComposite model and refactor the DocumentTaxeneCompositeTask to accept any document type (https://dotdash.atlassian.net/browse/BLNC-1470)

## 3.2.48
 - Extract document service wrapper into its own method to allow overriding in spring config (Table of Contents: https://dotdash.atlassian.net/browse/HLTH-2423)

## 3.2.47
 - Use caret matching to pull latest 0.3.X version of justified columns from bower

## 3.2.46
 - [3.1.74] Update dockerfile to remove optimized base image and prevent failing builds
 - [3.1.74] Thread that stops page from loading for long time. (https://dotdash.atlassian.net/browse/LW-1003)

## 3.2.45
 - Re-upgrade globe-core to 3.3.5 for hippodrome 1.29.0 for InfluxDB patch (https://dotdash.atlassian.net/browse/GLBE-4936)

## 3.2.44
 - [3.1.71] Updated amazon-affiliate-widget to allow for lists containing duplicate amazon items. (https://dotdash.atlassian.net/browse/REF-572)

## 3.2.43
 - [3.1.70] Use "local" directive in mantle macros to prevent accidental variable overriding (https://dotdash.atlassian.net/browse/GLBE-4897)

## 3.2.42
 - Upgrade globe-core to 3.3.4 to collapse components with unprepared config models (https://dotdash.atlassian.net/browse/GLBE-4931)
 - Upgrade globe-core to 3.3.3 for hippodrome 1.29.0 for InfluxDB patch (https://dotdash.atlassian.net/browse/GLBE-4936)

## 3.2.41
 - Fixed lazy-loaded billboard ads from being requested a second time on window load. (https://dotdash.atlassian.net/browse/LW-1013)
 - [3.1.66] Fixed click-to-play video player using the correct volume passed in from the config. (https://dotdash.atlassian.net/browse/REF-548)
 - [3.1.64] Fixed automation failure for footer deferred leaderboard (https://dotdash.atlassian.net/browse/LW-1007)
 - [3.1.63] Changed image placeholder to not require a lazyloaded image. (https://dotdash.atlassian.net/browse/REF-476)
 - [3.1.62] Added automation tests for post content leaderboard. (https://dotdash.atlassian.net/browse/LW-987)
 - [3.1.61] Add OpenX lite to Header bidders (https://dotdash.atlassian.net/browse/GLBE-4908)
 - Update mantle-grunt and other npm dependencies to latest (https://dotdash.atlassian.net/browse/GLBE-4915)
 - [3.1.59] Add font loading utility function, and only justify masonry list after font load. (https://dotdash.atlassian.net/browse/LW-978)
- [3.1.58] Include mapTemplateType, rewriteHref macros needed for masonry-list and card FTLs; inject grid classes to masonry-list to allow for clobbering; allow any img inside masonry-list to be included in justified-columns image stretching (not tied to card-img class anymore) (https://dotdash.atlassian.net/browse/TRIP-209)

## 3.2.33 - 3.2.40 (Breaking change unintentionally introduced in 3.2.33 / 3.1.58 / TRIP-209 related to uncompressed macros in utilities.ftl. Upgrade directly to 3.2.41)

## 3.2.32
- Fix grunt task order (https://dotdash.atlassian.net/browse/GLBE-4902)

## 3.2.31
- [3.1.57] Fixed deferred videos loading in and being paused, even when autoplay is on. (https://dotdash.atlassian.net/browse/REF-546)
- [3.1.57] Fixed video volume being on for a split second, even when volume is set to 0. (https://dotdash.atlassian.net/browse/REF-545)
- [3.1.57] Fixed an issue in the deferred library breaking onScroll deferred components on IE10. (https://dotdash.atlassian.net/browse/REF-551)

## 3.2.30
- Fix paths in deferred loaded resource map (https://dotdash.atlassian.net/browse/GLBE-4928)

## 3.2.29
- Change implementation of feedbackify's tab in order to be consistent across desktop, tablet, and mobile. Remove use of feedbackify's showTab method to generate feedback button (as Feedbackify's version has different HTML/CSS for mobile and desktop) and instead create component on our end and bind click handler to feedbackify's showForm method (https://dotdash.atlassian.net/browse/HLTH-2160)

## 3.2.28
- [3.1.56] Implement Phase 1 of SEO best practices on mntl-card (remove excerpt, call-to-action, and taxonomy/categorization; change h2 to h4) to improve SERP results (https://dotdash.atlassian.net/browse/GLBE-4911)
 - [3.1.54] Moved masonrylist test and footer deferred leaderboard test to mntl. (https://dotdash.atlassian.net/browse/REF-497)

## 3.2.27
- Changed GTM unifiedPageview key 'instartLogicDelivered' from boolean to Integer (0 or 1) (https://dotdash.atlassian.net/browse/HLTH-2310)

## 3.2.26
- [3.1.53] Fixed several bugs with the video player library. Added default values for volume and autoplay for mobile and tablet videos. (https://dotdash.atlassian.net/browse/REF-410)

## 3.2.25
- Update to globe-core 3.3.2, which fixes potentially broad XSS security holes by automatically escaping the value used to override a `<property>` via the queryOverride attribute. (https://dotdash.atlassian.net/browse/GLBE-4907)

## 3.2.24
 - Changed chop to require having a minimum percent of content behind the chop (https://dotdash.atlassian.net/browse/LW-902)

## 3.2.23
 - [3.0.32] Fix cookie filter no pageview pattern (https://dotdash.atlassian.net/browse/HLTH-2374)

## 3.2.22
 - Added mntl-lazy-ad component and script for lazy loading ads

## 3.2.21
 - Move CSRFFilter so it is only used on the RequestHandlerServlet to prevent rekon failures
 - [3.0.31] Fix PC cookie increment (https://dotdash.atlassian.net/browse/GLBE-4865)
 - Fix RTB unit test; add mntl-coverage-report to dev grunt tasks (https://dotdash.atlassian.net/browse/GLBE-4857)
 - [3.0.31] Add simple search query params. (https://dotdash.atlassian.net/browse/GLBE-4873)
 - [3.0.29] - Remove excerpt from card in masonry list item. (https://dotdash.atlassian.net/browse/REF-511)
 - Fix for CSRF which broke POSTs on IE (https://dotdash.atlassian.net/browse/HLTH-2365)
 - Add additional GPT unit tests (https://dotdash.atlassian.net/browse/GLBE-4852)
 - Add RTB unit tests (https://dotdash.atlassian.net/browse/GLBE-4853)
 - Updated the path of the debugEffectiveTemplate task to accept camel case, or all lowercase, to be compatible with verticals that get re-directed to lowercase URLs
 - [3.1.44] Support null values for rtbConfigIds plugins, so that with additional xml logic and `mode="ignore"` we can filter out specific plugins, instead of collapsing the whole component (e.g. conditionally adding lotame based on IP)
 - [3.1.43] Prevent multiple bcp calls per page for lotameCollection RTB plugin (https://dotdash.atlassian.net/browse/BLNC-1362)
 - [3.1.41] Added docId as a baseSlotTargeting property in mntl-gpt-definition (https://dotdash.atlassian.net/browse/HLTH-2237)
 - Update justified-columns to v0.3.4 (adds an extra px of height for IE fixing to avoid sub-pixel rounding shenanigans)
 - [3.1.40] Update absoluteHref macro to use requestContext.urlData.scheme instead of requestContext.requestScheme to be more reliable
 - [3.1.39] Updates to masonry-list to make script more efficient and available to all devices. (https://dotdash.atlassian.net/browse/BLNC-1179)
 - [3.1.38] Fix for quiz results sharing (https://dotdash.atlassian.net/browse/REF-484)
 - [3.1.37] Add lotameCollection RTB plugin. Previously this has been a script fired on DOM ready through GTM but adding to RTB will allow us to test whether firing the script even earlier will improve revenue. (https://dotdash.atlassian.net/browse/GLBE-4867)
 - [3.1.35] Include masonry list tag changes.
 - [3.1.32] Updated preconnect default domain list to include fthmb.tqn.com
 - [3.1.31] Migrated amazon-affiliate-widget component to Mantle (https://dotdash.atlassian.net/browse/LW-867). Requires updating the following CIA keys in your vertical: AWS_ECOMM_ACCESS_KEY, AWS_ECOMM_SECRET_KEY, AWS_ECOMM_ASSOCIATE_TAG.
 - Requires datadog api key in cia/consul and datadog enable flag
 - Move Replace AbstractMantleCanonicalUrlRedirectFilter with a concrete filter MantleCanonicalUrlRedirectFilter ported from health, meant to replace all canonical url redirect filters in verticals to address code duplication.
 - Use CSRF filter to block all POSTs which do not pass valid token (https://dotdash.atlassian.net/browse/HLTH-2181)
 - Move Lotame to Mntl RTB for better ad/page performance (https://dotdash.atlassian.net/browse/GLBE-4849)
 - Renamed mntl-amazon-affiliate to mntl-amazon-affiliate-tagger, and JS name from amazonAffiliate to amazonAffiliateTagger to be less ambiguous with the amazon-affiliate-widget (https://dotdash.atlassian.net/browse/LW-867)
 - Migrate justified-columns bower dependency to Mantle
 - Remove generic subdomain logic from mantle and globe-core. This was bad design from about.com and led to a RCE/XSS security hole (https://dotdash.atlassian.net/browse/GLBE-4840)
 - Update masonry-list.js to wait for image load in IE to avoid various layout complications.  (https://dotdash.atlassian.net/browse/LW-896)

## 3.2.0 - 3.2.20 (Breaking change due to CSRFFilter causing rekon failures during deployments, upgrade directly to 3.2.21)

## 3.1.74
 - Update dockerfile to remove optimized base image and prevent failing builds
 - Thread that stops page from loading for long time. (https://dotdash.atlassian.net/browse/LW-1003)

## 3.1.72 & 3.1.73 (Git merge issues. Upgrade directly to 3.1.74)

## 3.1.71
 - Updated amazon-affiliate-widget to allow for lists containing duplicate amazon items. (https://dotdash.atlassian.net/browse/REF-572)

## 3.1.70
 - Use "local" directive in mantle macros to prevent accidental variable overriding (https://dotdash.atlassian.net/browse/GLBE-4897)

## 3.1.69
 - Update changelog

## 3.1.68
 - Document complete timing out on scroll for lazyload ads. (https://dotdash.atlassian.net/browse/LW-1015)
 - Move utilities.ftl macros inside compress to fix bug introduced in 3.1.58
 - Upgrade to globe-core 3.2.14 for hippodrome 1.29.0 for InfluxDB patch (https://dotdash.atlassian.net/browse/GLBE-4936)
 - Fixed click-to-play video player using the correct volume passed in from the config. (https://dotdash.atlassian.net/browse/REF-548)
 - Fixed automation failure for footer deferred leaderboard (https://dotdash.atlassian.net/browse/LW-1007)
 - Changed image placeholder to not require a lazyloaded image. (https://dotdash.atlassian.net/browse/REF-476)
 - Added automation tests for post content leaderboard. (https://dotdash.atlassian.net/browse/LW-987)
 - Added OpenX Lite to Header bidders. (https://dotdash.atlassian.net/browse/GLBE-4908)
 - Added more automation tests for video player. (https://dotdash.atlassian.net/browse/REF-530)
 - Add font loading utility function, and only justify masonry list after font load. (https://dotdash.atlassian.net/browse/LW-978)
 - Include mapTemplateType, rewriteHref macros needed for masonry-list and card FTLs; inject grid classes to masonry-list to allow for clobbering; allow any img inside masonry-list to be included in justified-columns image stretching (not tied to card-img class anymore) (https://dotdash.atlassian.net/browse/TRIP-209)

## 3.1.58 - 3.1.67 (Breaking change unintentionally introduced in 3.1.58 / TRIP-209 related to uncompressed macros in utilities.ftl. Upgrade directly to 3.1.67)

## 3.1.57
 - Fixed deferred videos loading in and being paused, even when autoplay is on. (https://dotdash.atlassian.net/browse/REF-546)
 - Fixed video volume being on for a split second, even when volume is set to 0. (https://dotdash.atlassian.net/browse/REF-545)
 - Fixed an issue in the deferred library breaking onScroll deferred components on IE10. (https://dotdash.atlassian.net/browse/REF-551)

## 3.1.56
 - Implement Phase 1 of SEO best practices on mntl-card (remove excerpt, call-to-action, and taxonomy/categorization; change h2 to h4) to improve SERP results (https://dotdash.atlassian.net/browse/GLBE-4911)

## 3.1.55
 - Update changelog

## 3.1.54
 - Moved masonrylist test and footer deferred leaderboard test to mntl. (https://dotdash.atlassian.net/browse/REF-497)

## 3.1.53
 - Fixed several bugs with the video player library. Added default values for volume and autoplay for mobile and tablet videos. (https://dotdash.atlassian.net/browse/REF-410)

## 3.1.52
 - Update to globe-core 3.2.13, which fixes potentially broad XSS security holes by automatically escaping the value used to override a `<property>` via the queryOverride attribute. (https://dotdash.atlassian.net/browse/GLBE-4907)

## 3.1.51
 - [3.0.32] Fix cookie filter no pageview pattern (https://dotdash.atlassian.net/browse/HLTH-2374)

## 3.1.50
 - [3.0.31] Fix PC cookie increment (https://dotdash.atlassian.net/browse/GLBE-4865)

## 3.1.48
 - [3.0.30] Add SimpleReach parameters (sr_source, sr_share) to the SEO parameter whitelist (https://dotdash.atlassian.net/browse/GLBE-4873)

## 3.1.47
 - [3.0.29] Remove excerpt from card in masonry list item. (https://dotdash.atlassian.net/browse/REF-511)

## 3.1.46
 - Update changelog

## 3.1.45
 - Improve String casting logic using toString() as @document.documentId is a Long object. This also fixes a bug on non-document pages where the '' + `<Long>` trick passes in "null".

## 3.1.44
 - [3.0.28] Support null values for rtbConfigIds plugins, so that with additional xml logic and `mode="ignore"` we can filter out specific plugins, instead of collapsing the whole component (e.g. conditionally adding lotame based on IP)

## 3.1.43
 - [3.0.27] Prevent multiple bcp calls per page for lotameCollection RTB plugin (https://dotdash.atlassian.net/browse/BLNC-1362)

## 3.1.42
 - Update changelog

## 3.1.41
 - Added docId as a baseSlotTargeting property in mntl-gpt-definition (https://dotdash.atlassian.net/browse/HLTH-2237)

## 3.1.40
 - [3.0.26] Update absoluteHref macro to use requestContext.urlData.scheme instead of requestContext.requestScheme to be more reliable

## 3.1.39
 - Updates to masonry-list to make script more efficient and available to all devices. (https://dotdash.atlassian.net/browse/BLNC-1179)

## 3.1.38
 - Fix for quiz results sharing (https://dotdash.atlassian.net/browse/REF-484)
 - [3.0.25] Add lotameCollection RTB plugin. Previously this has been a script fired on DOM ready through GTM but adding to RTB will allow us to test whether firing the script even earlier will improve revenue. (https://dotdash.atlassian.net/browse/GLBE-4867)
 - [3.0.24] Include masonry list tag changes.
 - Updated preconnect default domain list to include fthmb.tqn.com
 - Add xInstartVia request header. Pass GTM and GPT tracking values for when page is served through Instart. (https://dotdash.atlassian.net/browse/HLTH-2310)
 - Migrated amazon-affiliate-widget component to Mantle. In order to use the mantle version, you will be required to add the following CIA keys in your vertical: AWS_ECOMM_ACCESS_KEY, AWS_ECOMM_SECRET_KEY, AWS_ECOMM_ASSOCIATE_TAG (https://dotdash.atlassian.net/browse/LW-867)

## 3.1.30 - 3.1.37 (Breaking changes related to quiz results sharing and mntl-masonry-list. Upgrade directly to 3.1.38)

## 3.1.29
 - Update masonry-list.js to wait for image load in IE to avoid various layout complications.  (https://dotdash.atlassian.net/browse/LW-896)

## 3.1.28
 - Added serveResource to MantleActionTasks to allow serving arbitrary resources from src/main/resources in a server project (https://dotdash.atlassian.net/browse/HLTH-2225)

## 3.1.27
 - Updated venus version from 1.0.0 to 1.1.0

## 3.1.26
 - [3.0.19] Added DocumentTaxeneTask for recirc card rebranding (https://dotdash.atlassian.net/browse/LW-884)

## 3.1.25
 - Update changelog

## 3.1.24
 - Upgrade to globe-core 3.2.11 for micro-vertical component clobbering bugfix

## 3.1.23
 - Make RTB more flexible by allowing RTB to be used or not used for each slot individually (https://dotdash.atlassian.net/browse/GLBE-4851)

## 3.1.22
 - Filter out non-vertical templates from sitemap and start allowing quizzes (https://dotdash.atlassian.net/browse/HLTH-2149)
 - Fix bug in 3.1.18 for Instart prioritized js location which conflicted with vertical specific locations. Added unique Id.  
 - Add Instart component (https://dotdash.atlassian.net/browse/HLTH-2249). To use this component, simply add the following component to your html component (which ref mntl-layout-html) <component location="head" ref="mntl-instart" />. Most likely, a Proctor test needs to be setup for this component.
 - Revise default configuration for mntl-preconnect (https://dotdash.atlassian.net/browse/BLNC-1284)

## 3.1.16 - 3.1.21 (Breaking change due to justified-columns, upgrade directly to 3.1.22)

## 3.1.15
 - Fix changelog

## 3.1.14
 - Update changelog

## 3.1.13
 - Add masonry-list component to Mantle

## 3.1.12
 - Add pause event to Chapters scrollHandler (https://dotdash.atlassian.net/browse/GLBE-4858)

## 3.1.11
 - Include common cross-browser domains as a default configuration for mntl-preconnect (https://dotdash.atlassian.net/browse/BLNC-1284)

## 3.1.10
 - Update globe-core to 3.2.9

## 3.1.9
 - Update globe-core to 3.2.8

## 3.1.8
 - Update globe-core to 3.2.7

## 3.1.7
 - Update redirection in DocumentTask to include environment to fix AWS redirection, update mantle version to fix NPEs from datadog

## 3.1.6
 - Update changelog

## 3.1.5
 - Exclude out-of-date selene-client metrics dependency
 - Update globe-core to 3.2.5

## 3.1.4
 - Update globe-core to 3.2.4 for hippodrome update

## 3.1.3
 - Grab missing code from release/3.0

## 3.1.2
 - Pull out logic necessary for filters from DomainRequestContextContributor (https://dotdash.atlassian.net/browse/GLBE-4846)

## 3.1.1
 - Update globe-core to 3.2.1

## 3.1.0
 - Update globe-core to 3.2.0

## 3.0.32
 - Fix cookie filter no pageview pattern (https://dotdash.atlassian.net/browse/HLTH-2374)

## 3.0.31
 - Fix PC cookie increment (https://dotdash.atlassian.net/browse/GLBE-4865)

## 3.0.30
- Add SimpleReach parameters (sr_source, sr_share) to the SEO parameter whitelist (https://dotdash.atlassian.net/browse/GLBE-4873)

## 3.0.29
 - Remove excerpt from card in masonry list item. (https://dotdash.atlassian.net/browse/REF-511)

## 3.0.28
 - Support null values for rtbConfigIds plugins, so that with additional xml logic and `mode="ignore"` we can filter out specific plugins, instead of collapsing the whole component (e.g. conditionally adding lotame based on IP)

## 3.0.27
 - Prevent multiple bcp calls per page for lotameCollection RTB plugin (https://dotdash.atlassian.net/browse/BLNC-1362)

## 3.0.26
 - Update absoluteHref macro to use requestContext.urlData.scheme instead of requestContext.requestScheme to be more reliable

## 3.0.25
 - Add lotameCollection RTB plugin. Previously this has been a script fired on DOM ready through GTM but adding to RTB will allow us to test whether firing the script even earlier will improve revenue. (https://dotdash.atlassian.net/browse/GLBE-4867)

## 3.0.24
 - Add mntl-card heading and mntl-card tag with tax nodes.
 - Migrate modSort and subList list util methods to mantle (https://dotdash.atlassian.net/browse/REF-474)
 - Add taxonomyDocAllLevelList to DocumentTaxeneComposite model and DocumentTaxeneCompositeTask refactoring (https://dotdash.atlassian.net/browse/REF-473)

## 3.0.22, 3.0.23 (Breaking changes due to new taxonomy list, upgrade directly to 3.0.24)

## 3.0.21
 - Update masonry-list.js to wait for image load in IE to avoid various layout complications.  (https://dotdash.atlassian.net/browse/LW-896)

## 3.0.20
 - Added serveResource to MantleActionTasks to allow serving arbitrary resources from src/main/resources in a server project (https://dotdash.atlassian.net/browse/HLTH-2225)

## 3.0.19
 - Added DocumentTaxeneTask for recirc card rebranding (https://dotdash.atlassian.net/browse/LW-884)

## 3.0.18
 - Update changelog

## 3.0.17
 - Migrate masonry list component to mantle
 - Add mantle card component

## 3.0.14, 3.0.15, 3.0.16 (Breaking changes due to justified-columns, upgrade directly to 3.0.17)

## 3.0.13
 - Add method getDomainName to utilties.js (https://dotdash.atlassian.net/browse/GLBE-4767)

## 3.0.12
 - Increase default RTB timeout to 800ms (https://dotdash.atlassian.net/browse/BLNC-1276).

## 3.0.11
 - Allow the X-Fastly-Device header (if present) to set the requestContext.userAgent.deviceCategory.  This will allow us to safely let Fastly cache our HTML page variations.

## 3.0.10
 - Rewrite amazon links on page load with tag param set to amazon affiliate id (https://dotdash.atlassian.net/browse/HLTH-2209)
 - Handle amazon links with encoded query params (https://dotdash.atlassian.net/browse/HLTH-2211)

## 3.0.9
 - Update changelog

## 3.0.8
 - Update changelog

## 3.0.7
 - Update to globe-core 3.1.5 for hippodrome 1.10.1

## 3.0.6
 - Update changelog

## 3.0.5
 - Update globe-core to 3.1.3 to include component lookup fix

## 3.0.4
 - Update globe-core to 3.1.2 for white label component extension bug fix

## 3.0.3
 - Update changelog

## 3.0.2
 - Upgrade globe-core to 3.1.1 for hippodrome 1.10.0 for internal AWS support
 - Support injecting dates in document schema (https://dotdash.atlassian.net/browse/HLTH-1343)

## 3.0.1 (Jenkins failure, skipped)

## 3.0.0
 - Remove rubicon from RTB plugins (https://dotdash.atlassian.net/browse/LW-806)
 - Update Amazon header bidder (https://dotdash.atlassian.net/browse/GLBE-4827) : BREAKS AUTOMATION - HeaderBidderTest as 'https://c.amazon-adsystem.com/aax2/amzn_ads.js' is updated to 'https://c.amazon-adsystem.com/aax2/apstag.js'
 - Build amazon affiliate basic tagging functionality (https://dotdash.atlassian.net/browse/HLTH-1828)

## 2.12.15
  - Update globe-core to 2.4.8 for white-label support and hippodrome upgrade (Netflix library memory leak fix)
  - Update venus to 0.0.113 for hippodrome upgrade (Netflix library memory leak fix)

## 2.12.14
 - Revert the inline chop changes

## 2.12.13 (Breaking in some verticals. Upgrade to 2.12.14)
 - Fix bug related to inline chop, throwing a ConcurrentModificationException (https://dotdash.atlassian.net/browse/TS-1023)

## 2.12.12 (Contains Bug related to inline chop. Upgrade to 2.12.13)
 - Update venus version from 128 to 131 (fixing a browsermob venus bug for unsecure connections)
 - Add optional placeholder to thumborImg macro. Adds a placeholder around images that matches the actual size of the resized thumbor image (https://dotdash.atlassian.net/browse/REF-384)
 - Add chaptersEnabled to MntlConfigurationProperties in mantle-venus
 - Move Index RTB siteID map to xml for easier configuration. Also add siteID for mobile slots (https://dotdash.atlassian.net/browse/HLTH-2111)
 - Add descendant relation articles to TaxeneNodeEx (https://dotdash.atlassian.net/browse/HLTH-2006)
 - Convert inline chop to use content block splitting (https://dotdash.atlassian.net/browse/TS-279)
 - [2.11.23] Fix schema FTL error in logs when list items have no description

## 2.12.11 (Jenkins failure, skipped)

## 2.12.10
 - Migrate chapters component to mantle (https://dotdash.atlassian.net/browse/GLBE-4610)

## 2.12.9
 - [2.11.21] Remove one query string appending from proxyRequestPattern to prevent duplicate query params
 - [2.11.20] Use dashes instead of underscores in Index RTB site IDs, so that Index can parse properly and return correct bids
 - Add no-upscale filter as default option on thumborImg macro to prevent having to pass that parameter everytime we use the macro, reducing code redundency (https://dotdash.atlassian.net/browse/TS-934)

## 2.12.8 (Skipped, Jenkins failure)

## 2.12.7
 - [2.11.19] Revert "Add closing comment to @component macro. Added to help debugging html." from 2.10.30

## 2.12.6 (Contains Chapters bug. Do not use. Upgrade to 2.12.7)
 - Hotfix [vertical domain]/sponsored/[path] requests to handle multiple subdomains. (https://dotdash.atlassian.net/browse/GLBE-4816)

## 2.12.5 (Contains Chapters bug. Introduces regex bug. Do not use. Upgrade to 2.12.7)
 - Forwarding [vertical domain]/sponsored/[path] requests to [vertical name].specials.about.com is a business standard that should properly live in mantle rather than in individual verticals. (https://dotdash.atlassian.net/browse/GLBE-4816)
 - Fix bug where star did not show. (https://dotdash.atlassian.net/browse/TS-919)

## 2.12.4 (Introduces Chapters bug. Do not use. Upgrade to 2.12.7)
 - Allow optional configuration for threshold of lazyloadimages to be passed via data-attr on <html> so we can better control when to load lazy images (https://dotdash.atlassian.net/browse/TS-903)
 - Gracefully collapse silent on UGC rating components when no rating exists to avoid errors (https://dotdash.atlassian.net/browse/TS-850)
 - [2.11.18] Escape og:url to fix XSS vulnerability (https://dotdash.atlassian.net/browse/GLBE-4815)
 - [2.11.17] Upgrade to Index RTB API in order to better control bid request callbacks and avoid missed targeting opporunities (https://dotdash.atlassian.net/browse/GLBE-4669)
 - [2.11.17] Add document id to slot targeting for ads (https://dotdash.atlassian.net/browse/GLBE-4813)
 - [2.11.17] Add closing comment to @component macro. Added to help debugging html.

## 2.12.3
 - Use active date parameter for documents only when State is PREVIEW (https://dotdash.atlassian.net/browse/TS-830)
 - Using Request Context to get user Id, instead of using cookies in the UGCRatings Task. (https://dotdash.atlassian.net/browse/TS-850)

## 2.12.2
 - Account for no image on quiz results and avoid freemarker error (https://dotdash.atlassian.net/browse/REF-303)

## 2.12.1
 - Support string ad sizes, specifically "fluid" size for ThoughtCo, by replacing single quote with double quote in ad sizes to avoid JSON.parse failure in Mantle GPT (https://dotdash.atlassian.net/browse/REF-147)
 - Improve mobile experience by replacing pinitbtn.js with a more optimized script from Verywell (https://dotdash.atlassian.net/browse/REF-129)
 - Add prepTime and cookTime to the CuratedDocumentEx in order to display total cook time on curated list cards (https://dotdash.atlassian.net/browse/TS-558)
 - [2.11.14] Change taxonomyNodes type in MntlDatalayerObject to support datalyer taxonomy node object. This fixes automation failure introduced in 2.11.5.
 - [2.11.14] Fix bug related to bio page data layer introduced in 2.11.5 by adding null check to taxeneNodes in TaxeneRelationTask (https://dotdash.atlassian.net/browse/REF-272)
 - Silently collapse primary video schema when document is null in order to prevent error messages
 - [2.11.13] Add new schema for document's primary video (https://dotdash.atlassian.net/browse/TS-620)
 - [2.11.13] Round nutrition data in schema to the nearest tenth to look more presentable in search results (https://dotdash.atlassian.net/browse/TS-674)

## 2.12.0
 - Updated Venus version from 0.0.128 to 0.0.129  - This brings in the BrowserMob version upgrade from 2.0.0 to 2.1.4 (https://dotdash.atlassian.net/browse/GLBE-4701)
 - Remove Circulationsettings and recircOnly flag as part of upgrading to Selene 1.66.4, needed by Thoughtco to set algoType to REFERENCE and get only reference documents back in search results (https://dotdash.atlassian.net/browse/REF-213)

## 2.11.23
 - Update venus version from 128 to 131 (fixing a browsermob venus bug for unsecure connections)
 - Fix schema FTL error in logs when list items have no description

## 2.11.22
 - Migrate chapters component to mantle (https://dotdash.atlassian.net/browse/GLBE-4610)

## 2.11.21
 - [2.10.34] Remove one query string appending from proxyRequestPattern to prevent duplicate query params

## 2.11.20
 - [2.10.33] Use dashes instead of underscores in Index RTB site IDs, so that Index can parse properly and return correct bids

## 2.11.19
 - [2.10.32] Revert "Add closing comment to @component macro. Added to help debugging html." from 2.10.30

## 2.11.18 (Contains bug introduced in 2.11.17. Do not use. Upgrade to 2.11.19)
 - [2.10.31] Escape og:url to fix XSS vulnerability (https://dotdash.atlassian.net/browse/GLBE-4815)

## 2.11.17 (Introduces Chapters bug. Do not use. Upgrade to 2.11.19)
 - [2.10.29] Upgrade to Index RTB API in order to better control bid request callbacks and avoid missed targeting opporunities (https://dotdash.atlassian.net/browse/GLBE-4669)
 - [2.10.28] Add document id to slot targeting for ads (https://dotdash.atlassian.net/browse/GLBE-4813)
 - [2.10.30] Add closing comment to @component macro. Added to help debugging html.

## 2.11.16
 - Use active date parameter for documents only when State is PREVIEW (https://dotdash.atlassian.net/browse/TS-830)

## 2.11.15
 - Account for no image on quiz results and avoid freemarker error (https://dotdash.atlassian.net/browse/REF-303)

## 2.11.14
 - Change taxonomyNodes type in MntlDatalayerObject to support datalyer taxonomy node object. This fixes automation failure introduced in 2.11.5.
 - Fix bug related to bio page data layer introduced in 2.11.5 by adding null check to taxeneNodes in TaxeneRelationTask (https://dotdash.atlassian.net/browse/REF-272)

## 2.11.13
 - Add new schema for document's primary video (https://dotdash.atlassian.net/browse/TS-620)
 - Round nutrition data in schema to the nearest tenth to look more presentable in search results (https://dotdash.atlassian.net/browse/TS-674)

## 2.11.12
 - Fix rating casting bug (https://dotdash.atlassian.net/browse/TS-609)
 - Add additional action for servemodel (all lowercase)

## 2.11.11
 - Add nutrition data to schema (https://dotdash.atlassian.net/browse/TS-504)
 - Allow rating to output alternate tag (https://dotdash.atlassian.net/browse/TS-547)

## 2.11.10
 - Updated Venus version from 0.0.123 to 0.0.128 to get latest Venus updates (requested by Reference team for ticket : https://dotdash.atlassian.net/browse/REF-135)

## 2.11.9 (Jenkins failure, do not use)

## 2.11.8
 - Pass UTM params into DFP (https://dotdash.atlassian.net/browse/GLBE-4709)
 - Changed the schema xml to handle a no rating scenario gracefully (https://dotdash.atlassian.net/browse/TS-412)

## 2.11.7 (Change introduced bug. Please upgrade directly to 2.11.8)
 - Pass UTM params into DFP (https://dotdash.atlassian.net/browse/GLBE-4709)
 - Changed the schema xml to handle a no rating scenario gracefully (https://dotdash.atlassian.net/browse/TS-412)

## 2.11.6
 - Adding Taxene Level (5) to the Taxene Level Types (https://dotdash.atlassian.net/browse/TS-402)
 - Update RSS link utm params (https://dotdash.atlassian.net/browse/GLBE-4713)

## 2.11.5 (Change introduced bug related to bio page data layer and automation failure related to taxonomyNodes type in MntlDataLayerObject. Please upgrade directly to 2.11.14)
 - Adding new task for creating GTM tracking value that supports multiparent taxonomy (https://dotdash.atlassian.net/browse/TS-238)
 *This will require your GTM config to be updated!*

## 2.11.4
 - RSS Feed bugfixes and improvements
   - Make description null-safe (https://dotdash.atlassian.net/browse/HLTH-1886)
   - Pass activeDate from `et` param to Selene to support preview (https://dotdash.atlassian.net/browse/GLBEX-4928)
   - Add new endpoint where item.pubdate reflects date the doc was added to the list (https://dotdash.atlassian.net/browse/GLBE-4698)
   - Better sorting, escape XML output in tags

## 2.11.3
 - Add new attributes to allow inline muted autoplay on ios devices (https://dotdash.atlassian.net/browse/TS-155)

## 2.11.2
 - Pass reference to jquery node of component in deferred BTF scroll items (https://dotdash.atlassian.net/browse/TS-233)

## 2.11.1
 - Add gpt unit tests and support for sinon-chai (https://dotdash.atlassian.net/browse/GLBE-4696)
 - Remove genericSubDomain determination from Mntl.Deferred (https://dotdash.atlassian.net/browse/GLBE-4702)
 - Set RSS `<enclosure>` width/height to the image defaults; fall back to 400x300 if unset
 - fix OpenX RTB plugin for refresh case (https://dotdash.atlassian.net/browse/GLBE-4671)
 - Prevent duplicate Amazon RTB calls (https://dotdash.atlassian.net/browse/GLBE-4703)
 - Check if noopener attr exists before appending it (https://dotdash.atlassian.net/browse/LW-746)
 - Add missing flexbox vendor prefixes (https://dotdash.atlassian.net/browse/GLBE-4706)
 - Clone scroll event instead of modifying it to fix ads not locking in Edge / IE11 (https://dotdash.atlassian.net/browse/GLBE-4707)

## 2.11.0
 - Update selene version which alters DocumentRequestContext
 - Add support for tags in site search (https://dotdash.atlassian.net/browse/HLTH-1789)
 - Add suggestion tasks and services for autocomplete (https://dotdash.atlassian.net/browse/HLTH-901)
 - Add support for data-defer-params (https://dotdash.atlassian.net/browse/GLBE-4695)-
 - Add support for taxene relations (https://dotdash.atlassian.net/browse/TS-90)
 - Add support for primary video (https://dotdash.atlassian.net/browse/TS-127)
 - Make MantleSpringConfiguration.defaultTemplateNameResolveTask abstract (https://dotdash.atlassian.net/browse/GLBE-4691)
 - Ensure uniqueId is present for all child components in PL (https://dotdash.atlassian.net/browse/GLBE-4697)
 - Add whitespace clearing tags (https://dotdash.atlassian.net/browse/GLBE-4699)

## 2.10.35
 - Fix schema FTL error in logs when list items have no description

## 2.10.34
 - Remove one query string appending from proxyRequestPattern to prevent duplicate query params

## 2.10.33
 - Use dashes instead of underscores in Index RTB site IDs, so that Index can parse properly and return correct bids

## 2.10.32
 - Revert "Add closing comment to @component macro. Added to help debugging html." from 2.10.30

## 2.10.31 (Contains bug introduced in 2.10.30. Do not use. Upgrade to 2.10.32)
 - [2.9.70] Escape og:url to fix XSS vulnerability (https://dotdash.atlassian.net/browse/GLBE-4815)

## 2.10.30 (Introduces Chapters bug. Do not use. Upgrade to 2.10.32)
 - Add closing comment to @component macro. Added to help debugging html.

## 2.10.29
 - Upgrade to Index RTB API in order to better control bid request callbacks and avoid missed targeting opporunities (https://dotdash.atlassian.net/browse/GLBE-4669)

## 2.10.28
 - [2.9.69] Add document id to slot targeting for ads (https://dotdash.atlassian.net/browse/GLBE-4813)

## 2.10.27
 - Use active date parameter for documents only when State is PREVIEW (https://dotdash.atlassian.net/browse/TS-830)

## 2.10.26
 - Account for no image on quiz results and avoid freemarker error (https://dotdash.atlassian.net/browse/REF-303)

## 2.10.25
 - Fix Pass UTM params into DFP_ (https://dotdash.atlassian.net/browse/GLBE-4709)

## 2.10.24 (Change introduced bug. Please upgrade directly to 2.10.25)
 - Pass UTM params into DFP (https://dotdash.atlassian.net/browse/GLBE-4709)

## 2.10.23
 - Set RSS `<enclosure>` width/height to the image defaults; fall back to 400x300 if unset.
 - Update globe-core to 2.3.19 to support asynchronous log cleaning (https://dotdash.atlassian.net/browse/HLTH-1818)
 - fix OpenX RTB plugin for refresh case (https://dotdash.atlassian.net/browse/GLBE-4671)
 - Prevent duplicate Amazon RTB calls (https://dotdash.atlassian.net/browse/GLBE-4703)
 - Check if noopener attr exists before appending it (https://dotdash.atlassian.net/browse/LW-746)
 - Add missing flexbox vendor prefixes (https://dotdash.atlassian.net/browse/GLBE-4706)
 - Clone scroll event instead of modifying it to fix ads not locking in Edge / IE11 (https://dotdash.atlassian.net/browse/GLBE-4707)

## 2.10.22
 - Add missing break in TaxonomyConfigServiceImpl switch statement

## 2.10.21 (Jenkins failure, don't use)

## 2.10.20
 - Avoid XSS problem with generic subdomains and reference loaders (https://dotdash.atlassian.net/browse/GLBEX-4828)
 - Fix curated list projections and taxonomyListOfList, remove authorListTask (https://dotdash.atlassian.net/browse/GLBEX-4905)
 - Add accordion to PL nav (https://dotdash.atlassian.net/browse/GLBE-4693)
 - Moved datalayer automation code to Mantle (https://dotdash.atlassian.net/browse/GLBE-4680)

## 2.10.19
 - Fix bug in loadComponentsOnScroll where elements are being saved as a normal element object rather than the expected jquery element

## 2.10.18
 - Move AboutIPDetector into mantle from globe-core (https://dotdash.atlassian.net/browse/GLBE-4666)

## 2.10.18-AUTOCOMPLETE
 - Update selene version which alters DocumentRequestContext
 - Add support for tags in site search (https://dotdash.atlassian.net/browse/HLTH-1789)
 - Add suggestion tasks and services for autocomplete (https://dotdash.atlassian.net/browse/HLTH-901)

## 2.10.17
 - Remove noreferrer from external links (https://dotdash.atlassian.net/browse/GLBE-4679)
 - Disable ftl parsing for inline css and non-evaluated js (https://dotdash.atlassian.net/browse/GLBE-4687)
 - Support "documentTemplateType" variable in Proctor rules (https://dotdash.atlassian.net/browse/GLBE-4677)
 - Rename "Dependencies" tab to "Model" (https://dotdash.atlassian.net/browse/GLBE-4688)
 - Added actorId , leaid leuid and removed actorGid ID from accessLog tests (https://dotdash.atlassian.net/browse/HLTH-1840)
 - PL Search Icon to label element (https://dotdash.atlassian.net/browse/GLBE-4694)

## 2.10.16
 - Fix bug where addToBatch expects jquery element or array of elements but was being passed an array of IDs
 - Add overloaded socialLink method to support defaultImageId (https://dotdash.atlassian.net/browse/BLNC-1057)
 - Remove noreferrer from external links (https://dotdash.atlassian.net/browse/GLBE-4679)
 - Remove override clean:js grunt configuration that was clearing bower dependency jQueryAjaxTransportXDomainRequest min files (https://dotdash.atlassian.net/browse/GLBE-4682)

## 2.10.15
 - Add quotations around url in search schema (https://dotdash.atlassian.net/browse/HLTH-1764)

## 2.10.14
 - Fix JS error in Mantle.GPT.registerCallback when match() returns null

## 2.10.13
 - Fix pattern library component failures (https://dotdash.atlassian.net/browse/GLBE-4672)
 - Add search and filtering to pattern library navigation (https://dotdash.atlassian.net/browse/GLBE-4676)
 - Add previewType tag for pattern library (https://dotdash.atlassian.net/browse/GLBE-4675)

## 2.10.12
 - Shorten lastEditingAuthorId and lastEditingUserId keys to leaid and leuid respectively (https://dotdash.atlassian.net/browse/HLTH-1656)

## 2.10.11
 - Add quotations around vertical name in publisher schema (https://dotdash.atlassian.net/browse/HLTH-1764)

## 2.10.10
 - Use async script tag instead of javascript function for GPT script (https://dotdash.atlassian.net/browse/HLTH-1816)

## 2.10.9
 - Fix a/b test serialization for boomerang test (https://dotdash.atlassian.net/browse/GLBE-4674)

## 2.10.8
 - fix OpenX RTB plugin for refresh case (https://dotdash.atlassian.net/browse/GLBE-4671)
 - Prevent empty or empty string params sent to component macro from breaking subsequent params (https://dotdash.atlassian.net/browse/TS-84)

## 2.10.7
 - Set RSS `<enclosure>` width/height to the image defaults; fall back to 400x300 if unset.
 - Update implemntation of mntl-chop
 - strip info tags from Pattern Library's dev-panel XML page
 - Upgrade to globe-core 2.4.4

## 2.10.6
 - Fix publisher schema config (https://dotdash.atlassian.net/browse/TS-59)

## 2.10.5
  - Update globe-core/hippodrome to support The Spruce as a vertical (https://dotdash.atlassian.net/browse/TS-8)

## 2.10.4
 - Add more documentation for mantle components (https://dotdash.atlassian.net/browse/GLBE-4659)

## 2.10.3
 - Fix syntax error in schema config

## 2.10.2
 - Upgrade to globe-core 2.4.2
 - Update wildcard support in Mntl.GPT.registerCallback to fix callbacks not firing for dynamic ads (https://dotdash.atlassian.net/browse/GLBE-4655)
 - Merge Social Share Button (https://dotdash.atlassian.net/browse/GLBE-4606)
 - Use window.breakpoints to partial-lock the pattern library draggable width iframes (https://dotdash.atlassian.net/browse/GLBE-4660)

## 2.10.1
 - Bower build process improvements (https://dotdash.atlassian.net/browse/GLBE-4639)
 - Better info tag handling for model injection and nested components (https://dotdash.atlassian.net/browse/GLBE-4571)
 - Migrate schema components to Mantle (https://dotdash.atlassian.net/browse/GLBE-4605)
 - Add lastEditingAuthorId and lastEditingUserId to GA/DFP/logs to support GA-based comp (https://dotdash.atlassian.net/browse/GLBE-4635)
 - Update to globe-core 2.4.1 and support retrieving conf from (non-CIA) CI/CD compatible mechanism (https://dotdash.atlassian.net/browse/CFS-51)

## 2.9.70
 - Escape og:url to fix XSS vulnerability (https://dotdash.atlassian.net/browse/GLBE-4815)

## 2.9.69
 - Add document id to slot targeting for ads (https://dotdash.atlassian.net/browse/GLBE-4813)

## 2.9.68
 - Account for no image on quiz results and avoid freemarker error (https://dotdash.atlassian.net/browse/REF-303)

## 2.9.67
 - Fix Pass UTM params into DFP_ (https://dotdash.atlassian.net/browse/GLBE-4709)

## 2.9.66 (Jenkins Failure DO NOT USE)

## 2.9.65 (Change introduced bug. Please upgrade directly to 2.9.67)
 - Pass UTM params into DFP (https://dotdash.atlassian.net/browse/GLBE-4709)

## 2.9.64
 - Set RSS `<enclosure>` width/height to the image defaults; fall back to 400x300 if unset.
 - Update globe-core to 2.3.19 to support asynchronous log cleaning (https://dotdash.atlassian.net/browse/HLTH-1818)
 - fix OpenX RTB plugin for refresh case (https://dotdash.atlassian.net/browse/GLBE-4671)
 - Prevent duplicate Amazon RTB calls (https://dotdash.atlassian.net/browse/GLBE-4703)
 - Check if noopener attr exists before appending it (https://dotdash.atlassian.net/browse/LW-746)
 - Add missing flexbox vendor prefixes (https://dotdash.atlassian.net/browse/GLBE-4706)
 - Clone scroll event instead of modifying it to fix ads not locking in Edge / IE11 (https://dotdash.atlassian.net/browse/GLBE-4707)

## 2.9.63
 - Update wildcard support in Mntl.GPT.registerCallback to fix callbacks not firing for dynamic ads (https://dotdash.atlassian.net/browse/GLBE-4655)

## 2.9.62
 - Merge Social Share Button (https://dotdash.atlassian.net/browse/GLBE-4606)
 - Use window.breakpoints to partial-lock the pattern library draggable width iframes (https://dotdash.atlassian.net/browse/GLBE-4660)

## 2.9.61
 - Fix dns-prefetch path
 - Fix persistent XML tab in pattern library dev panel (https://dotdash.atlassian.net/browse/GLBE-4653)

## 2.9.60
 - Use relative pathing for favicons rather than screwing around with genericUrl
 - avoid errors in the logs when viewing a PL page by declaring a new component using meta.ftl
 - Add chop button to mantle (https://dotdash.atlassian.net/browse/GLBE-4609)
 - Migrate Sticky Kit into Mantle (https://dotdash.atlassian.net/browse/GLBE-4607)
 - Remove unnecessary touchend event (https://dotdash.atlassian.net/browse/HLTH-1745)
 - *[REQUIRES WORK: delete local version of the file]* Migrate externalize-links.js to mantle (https://dotdash.atlassian.net/browse/GLBE-4592)

## 2.9.59
 - use readyAndDeferred for Mntl.Deferred.init() (https://dotdash.atlassian.net/browse/HLTH-1582)
 - Add component to support <link rel="preconnect"> tags (https://dotdash.atlassian.net/browse/GLBE-4644)
 - update globe-core to 2.3.18 to support inline CSS (https://dotdash.atlassian.net/browse/GLBE-4641)

## 2.9.58
 - Add background color to transparent PNG for sharing (https://dotdash.atlassian.net/browse/HLTH-1711)
 - Apply SEO whitelist filter to GET requests only (https://dotdash.atlassian.net/browse/GLBE-4636)
 - support both index and "index_queued" RTB options (for debugging of performance) problems
 - Run filter only when the HTTP request method is GET (https://dotdash.atlassian.net/browse/GLBE-4636)
 - Add newsletter validation service to verify that request to Sailthru should be allowed (https://dotdash.atlassian.net/browse/HLTH-1599)

## 2.9.57
 - Support embeddable tools (https://dotdash.atlassian.net/browse/HLTH-1582)

## 2.9.56
 - Handle touch screens for made it button (https://dotdash.atlassian.net/browse/HLTH-1703)
 - Fix extra comma in feedbackify ID (https://dotdash.atlassian.net/browse/HLTH-1216)

## 2.9.55
 - Allow getW to loop through all possible breakpoint matches before returning (https://dotdash.atlassian.net/browse/BLNC-936)
 - Update ssh to https

## 2.9.54
 - Migrate getW() to mantle and explicitly set pageTargeting.w (https://dotdash.atlassian.net/browse/GLBE-4613)

## 2.9.53
 - Revert "Upgrade mantle-grunt for bower build process improvements (https://dotdash.atlassian.net/browse/GLBE-4639)"

## 2.9.52 (Bower dependency errors, do not use)
 - Migrate HSTSFilter to mantle (https://dotdash.atlassian.net/browse/GLBE-4611)
 - Upgrade mantle-grunt for bower build process improvements (https://dotdash.atlassian.net/browse/GLBE-4639)
 - Stop propagation of mantle rating event (https://dotdash.atlassian.net/browse/HLTH-1665)
 - Re-introduce support for Service Performance testing and Evaluated Template rendering (http://dotdash.atlassian.net/browse/GLBE-4640)

## 2.9.51
 - Update globe-core to 2.3.14 for avoiding evaluating nested expressions (https://dotdash.atlassian.net/browse/BLNC-856)
 - Fix startup error introduced in 2.9.50

## 2.9.50 (Contains startup error, do not use)
 - Remove jQuery usage from RTB, eliminating race condition (https://dotdash.atlassian.net/browse/GLBE-4642)
 - Add feedbackify (https://dotdash.atlassian.net/browse/HLTH-1216)
 - Add data ordinal to rating ftl (https://dotdash.atlassian.net/browse/HLTH-1665)

## 2.9.49
 - Add 404 handler (https://dotdash.atlassian.net/browse/GLBE-4612)
 - Migrate shared baseSlotTargeting properties to mantle (https://dotdash.atlassian.net/browse/GLBE-4638)

## 2.9.48
 - Fix XSS vulnerability in convertHashToJs macro (https://dotdash.atlassian.net/browse/GLBE-4634)

## 2.9.47
 - Use Projection V2 for all services
 - Update node version in readme

## 2.9.46
 - Remove "http:" from amazon rtb url

## 2.9.45
 - Fix a couple of bugs related to quizzes (https://dotdash.atlassian.net/browse/BLNC-863 & https://dotdash.atlassian.net/browse/BLNC-865)

## 2.9.44
 - Added skipVideo method to mantle video component
 - updated venus version to 0.0.122 (https://dotdash.atlassian.net/browse/GLBE-4598)

## 2.9.43
 - Add UGC star and made it ratings (https://dotdash.atlassian.net/browse/GLBE-4602 https://dotdash.atlassian.net/browse/GLBE-4603)

## 2.9.42
 - Trim extra whitespace at beginning of rel attribute (https://dotdash.atlassian.net/browse/HLTH-1630)

## 2.9.41
 - Pattern library improvements (https://dotdash.atlassian.net/browse/GLBE-4267)

## 2.9.40
 - Add pickadate as bower dependency (https://dotdash.atlassian.net/browse/HLTH-1614)
 - Filter out pickadate js which fails uglify
 - Update mantle-grunt to 0.1.228
 - Add more cb-split exceptions (https://dotdash.atlassian.net/browse/HLTH-1576)

## 2.9.39
 - Fix duplicate OpenX calls (https://dotdash.atlassian.net/browse/GLBE-4623)
 - Update globe-core to 2.3.12 to add SpEL support for `str.htmlEscape()` method

## 2.9.38
 - Upgrade venus to 0.0.120

## 2.9.37
 - Update globe-core to 2.3.11 for proper view attributes in toXmlString() so PL edits don't fail (https://dotdash.atlassian.net/browse/GLBE-4617)

## 2.9.36
 - Refactor GTM to simplify vertical implementation and allow for override templateId (https://dotdash.atlassian.net/browse/GLBE-4595)

## 2.9.35
 - Upgrade css-element-queries version (BLNC-721)

## 2.9.34
 - Fix target="\_blank" security vulnerability (https://dotdash.atlassian.net/browse/GLBE-4592)

## 2.9.33
 - Remove modernizr as bower dependency; Upgrade mantle-grunt for modernizr updates; Update touchevents check (https://dotdash.atlassian.net/browse/GLBE-4622)

## 2.9.32
 - Upgrade globe-core to 2.3.10

## 2.9.31
 - Upgrade venus to 0.0.119

## 2.9.30
 - Quiz Updates: SVG on Firefox, scrolling issues and design updates

## 2.9.29
 - Add quiz component (https://dotdash.atlassian.net/browse/HLTH-1489)

## 2.9.28
 - Fix issue with log containing carriage return in path (https://dotdash.atlassian.net/browse/GLBEX-4601)

## 2.9.27
 - Update mantle-grunt to 0.1.197 for asyc clean up and spaces in project name support

## 2.9.26
 - Don't require caching CIA keys for Article services (https://dotdash.atlassian.net/browse/GLBE-4600)
 - Update mantle-grunt to 0.1.196 to avoid modernizr files getting copied by grunt

## 2.9.25
 - Fix untracked clicks on Microsoft Surface Desktop mode by listening for DOM pointer events, making use of the latest modernizr build
 - Upgrade to globe-core 2.3.9 / update instantiations of ModuleEntry to match new signature

## 2.9.24 (Jenkins failure, skipped)

## 2.9.23
 - Filter XML sitemap (https://dotdash.atlassian.net/browse/BLNC-657)
 - Migrate AFS to Mantle (https://dotdash.atlassian.net/browse/BLNC-625)
 - Fix extra rubicon calls (https://dotdash.atlassian.net/browse/GLBEX-4811)
 - Update slot object name to slotObj as slot is now a reserved element attribute in Chrome (https://dotdash.atlassian.net/browse/HLTH-1532)
 - Downgrade mantle-grunt to fix build issues
 - Pass slots to RTB preload functions
 - Add OpenX RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4580)
 - Add Aol RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4578)
 - Update Rubicon RTB plugin to only define main sizes for each slot

## 2.9.22
 - Update transformer to determine mime type based on non-thumbor url (https://dotdash.atlassian.net/browse/HLTH-1524)

## 2.9.21
 - Update slot object name to slotObj as slot is now a reserved element attribute in Chrome

## 2.9.20
 - Update mantle-grunt to 0.1.178 (includes fix for build failures)

## 2.9.19
 - Update mantle-grunt to 0.1.170

## 2.9.18
 - Remove OpenX and Aol RTB plugins

## 2.9.17
 - Refactored TaxeneConfig Service class

## 2.9.16
 - Support for RSS feeds (https://dotdash.atlassian.net/browse/HLTH-1447)
 - Remove deprecated CuratedListTask methods
 - Move vertical implementations of getStateFromRequestContext and getDateFromRequestContext into CuratedListTask and DocumentTask
 - Add OpenX RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4580)
 - Add Aol RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4578)
 - Update Rubicon RTB plugin to only define main sizes for each slot
 - Upgrade venus to 0.0.118
 - Added SchemaMarkup Tests ( https://dotdash.atlassian.net/browse/GLBE-4590 )

## 2.9.15
 - Updated curated list to use SliceableListEx instead of SliceableList
 - Updated selene version to latest

## 2.9.14
 - Access log changes for backtracking (https://dotdash.atlassian.net/browse/HLTH-1429)
 - Change source to source1 (https://dotdash.atlassian.net/browse/HLTH-1378)
 - Use SliceableList in place of List to fix runtime errors resulting from curated list v3 service updates (https://dotdash.atlassian.net/browse/GLBE-4538)

## 2.9.13
 - Add null check for relatedArticles (https://dotdash.atlassian.net/browse/HLTH-1477)
 - Fixing blank pattern library colors component (https://dotdash.atlassian.net/browse/GLBE-4583)
 - Use ssh instead of https for new jenkins release job

## 2.9.12
 - Use v3 curated list service (https://dotdash.atlassian.net/browse/GLBE-4538)
 - Fix failing test related to Windows support & add Windows support instructions to readme (https://dotdash.atlassian.net/browse/GLBE-4551)

## 2.9.11
 - Add kicker field to FB instant article header template and prevent error in log when subheading doesn't exist (https://dotdash.atlassian.net/browse/HLTH-1409)

## 2.9.10
 - updated venus version to 0.0.116  - (https://dotdash.atlassian.net/browse/HLTH-1430)
 - Added PL test with 'isPV' =false validation -  this can be extended to each vertical (https://dotdash.atlassian.net/browse/GLBE-4576)

## 2.9.9
 - Added accesslog validation feature to mantle - (https://dotdash.atlassian.net/browse/GLBE-4569)
 - updated venus version to 0.0.115  - (https://dotdash.atlassian.net/browse/HLTH-1430)
 - Add noop and Rubicon RTB plugins (https://dotdash.atlassian.net/browse/GLBEX-4807 & https://dotdash.atlassian.net/browse/GLBEX-4807)
 - Add getter and setter for image in AuthorEx (https://dotdash.atlassian.net/browse/GLBEX-4757)

## 2.9.8
- Added accesslog validation feature to mantle - (https://dotdash.atlassian.net/browse/GLBE-4569)
- Added jsKeys map in GTM component to allow JavaScript code to be passed in as a property
- Updated globe-core version to latest

## 2.9.7
 - Remove caching rendered output for sitemaps and add cache wrapper to sitemap task  (https://dotdash.atlassian.net/browse/HLTH-1379)

## 2.9.6
 - Fix missing SASS in pattern library development panels (https://dotdash.atlassian.net/browse/GLBE-4572)

## 2.9.5
 - Put resolve template back into deion search service client (https://dotdash.atlassian.net/browse/HLTH-1416)
 - Update venus to 0.0.114 (https://dotdash.atlassian.net/browse/HLTH-1405)
 - Focus on using BaseSlotTargeting (scp) as the means for communicating Amazon ad targeting parameters (https://dotdash.atlassian.net/browse/GLBEX-4782)

## 2.9.4
 - Change author service media type to v1 (https://dotdash.atlassian.net/browse/HLTH-1416 https://dotdash.atlassian.net/browse/HLTH-1417)

## 2.9.3
 - Service layer flatten with clientsMove Selene Clients into Globe service implementations [HLTH-1256](https://dotdash.atlassian.net/browse/HLTH-1256)
 - Unify curated list models and tasks [GLBE-4538](https://dotdash.atlassian.net/browse/GLBE-4538)
 - Gnome updates to remove redundant execution of source plugin
 - Add document to deion result item
 - Make the Pageview regex in CookieFilter class case-insensitive

## 2.9.0 - 2.9.2 (Jenkins failure, skipped)

## 2.8.27
 - Update slot object name to slotObj as slot is now a reserved element attribute in Chrome (https://dotdash.atlassian.net/browse/HLTH-1532)
 - Fix extra rubicon calls (https://dotdash.atlassian.net/browse/GLBEX-4811)
 - Update slot object name to slotObj as slot is now a reserved element attribute in Chrome (https://dotdash.atlassian.net/browse/HLTH-1532)
 - Downgrade mantle-grunt to fix build issues
 - Pass slots to RTB preload functions
 - Add OpenX RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4580)
 - Add Aol RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4578)
 - Update Rubicon RTB plugin to only define main sizes for each slot
 - Fix issue with log containing carriage return in path (https://dotdash.atlassian.net/browse/GLBEX-4601)
 - Fix target="\_blank" security vulnerability (https://dotdash.atlassian.net/browse/GLBE-4592)
 - Fix duplicate OpenX calls (https://dotdash.atlassian.net/browse/GLBE-4623)
 - Fix XSS vulnerability in convertHashToJs macro (https://dotdash.atlassian.net/browse/GLBE-4634)
 - Remove jQuery usage from RTB, eliminating race condition (https://dotdash.atlassian.net/browse/GLBE-4642)
 - fix OpenX RTB plugin for refresh case (https://dotdash.atlassian.net/browse/GLBE-4671)

## 2.8.26
 - Upgrade venus to 0.0.118
 - Added SchemaMarkup Tests ( https://dotdash.atlassian.net/browse/GLBE-4590 )

## 2.8.25
 - Access log changes for backtracking (https://dotdash.atlassian.net/browse/HLTH-1429)

## 2.8.24
 - updated venus version to 0.0.116  - (https://dotdash.atlassian.net/browse/HLTH-1430)
 - Added PL test with 'isPV' =false validation -  this can be extended to each vertical (https://dotdash.atlassian.net/browse/GLBE-4576)

## 2.8.23 (Skipped, Jenkins failure)

## 2.8.22
 - Added accesslog validation feature to mantle - (https://dotdash.atlassian.net/browse/GLBE-4569)
 - updated venus version to 0.0.115  - (https://dotdash.atlassian.net/browse/HLTH-1430)
 - Add noop and Rubicon RTB plugins (https://dotdash.atlassian.net/browse/GLBEX-4807 & https://dotdash.atlassian.net/browse/GLBEX-4807)
 - Add getter and setter for image in AuthorEx (https://dotdash.atlassian.net/browse/GLBEX-4757)

## 2.8.21
 - Focus on using BaseSlotTargeting (scp) as the means for communicating Amazon ad targeting parameters (https://dotdash.atlassian.net/browse/GLBEX-4782)

## 2.8.20
 - Make the Pageview regex in CookieFilter class case-insensitive

## 2.8.19
 - Add document to deion result item

## 2.8.18 (Jenkins failure, skipped)

## 2.8.17
 - Gnome updates to remove redundant execution of source plugin

## 2.8.16
 - Remove noIndex and redirect documents from Category
 - Add document service for followSummaryRedirects parameter
 - Remove port rewrite from redirect target
 - Upgrade venus to 0.0.113

## 2.8.15
 - Use Brightcove's getAdId method
 - Respond with 405 for unsupported methods (upgrade to globe core 2.3.6)

## 2.8.14
 - Update pattern library colors component to support sass variables and mixins
 - Adjust AFC client and channel for SEM tracking
 - Add sponsor object to topicDocumentEx
 - Fix amazon affiliate subtag losing utm_params on chapter switch
 - Upgrade venus to 0.0.111
 - Update selene version to 1.51.11 to get new sponsor object data

## 2.8.13
 - Upgrade globe core to 2.3.5
 - Continue redirect if not correct header

## 2.8.12
 - Auto updating cache template support and type safety

## 2.8.11
 - Update mantle-venus to 0.0.110
 - Add sample test for custom url
 - Add pattern library form automation
 - Upgrade selene to 1.51.1
 - Transport layer change for smile
 - Add ability to specify isPageview / don't count pattern library as pageview
 - Escape deion search queries
 - Add more freemarker null checking for afc
 - Hide CPC refresh iframe that is needed for infinite scroll afc
 - Make ```google_safe``` in afc setup freemarker null safe

## 2.8.10
 - Add search filter for state for sitemap
 - Escape js strings for hints to prevent js error
 - Wrap readyAndDeferred callback in function to prevent extra argument from jQuery
 - Add selector argument to readyAndDeferred so callback isn't potentially executing multiple times in a defer batch

## 2.8.9
 - Add actorId back to MDC

## 2.8.8
 - Add data-ab attribute to html tag
 - Fix amazon affiliate subtag handler skipping some amazon links
 - Mark AFC as loaded to prevent refresh

## 2.8.7
 - Fix several bugs in pattern library: missing components; iframe resizing; unique form input ids
 - Migrate abstracted AFC code to mantle from abtnetowkr/thebalance
 - Fix missing tile scp param on refreshed gpt slots
 - Fix Amazon Affiliate freemarker error when document does not exist

## 2.8.6 (Skipped due to Jenkins failure)

## 2.8.5 (Skipped due to Jenkins failure)

## 2.8.4
 - Allow blank and null values to be passed into pageTargeting and baseSlotTargeting

## 2.8.3
 - refactor amazon affiliate to be more generic for use in verticals

## 2.8.1
 - Merge changes from hotfix/2.7 to develop

## 2.8.0
 - script/style tag filterable with views and tests

## 2.7.38
 - Update slot object name to slotObj as slot is now a reserved element attribute in Chrome (https://dotdash.atlassian.net/browse/HLTH-1532)
 - Fix extra rubicon calls (https://dotdash.atlassian.net/browse/GLBEX-4811)
 - Update slot object name to slotObj as slot is now a reserved element attribute in Chrome (https://dotdash.atlassian.net/browse/HLTH-1532)
 - Downgrade mantle-grunt to fix build issues
 - Pass slots to RTB preload functions
 - Add OpenX RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4580)
 - Add Aol RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4578)
 - Update Rubicon RTB plugin to only define main sizes for each slot
 - Fix issue with log containing carriage return in path (https://dotdash.atlassian.net/browse/GLBEX-4601)
 - Fix target="\_blank" security vulnerability (https://dotdash.atlassian.net/browse/GLBE-4592)
 - Fix duplicate OpenX calls (https://dotdash.atlassian.net/browse/GLBE-4623)
 - Fix XSS vulnerability in convertHashToJs macro (https://dotdash.atlassian.net/browse/GLBE-4634)
 - Remove jQuery usage from RTB, eliminating race condition (https://dotdash.atlassian.net/browse/GLBE-4642)
 - fix OpenX RTB plugin for refresh case (https://dotdash.atlassian.net/browse/GLBE-4671)

## 2.7.37
 - Upgrade venus to 0.0.118
 - Added SchemaMarkup Tests ( https://dotdash.atlassian.net/browse/GLBE-4590 )

## 2.7.36
 - Revert: TaxeneCon setup (https://dotdash.atlassian.net/browse/BLNC-233)
 - Access log changes for backtracking (https://dotdash.atlassian.net/browse/HLTH-1429)

## 2.7.35 (Jenkins failure, skipped)

## 2.7.34 (Bug introduced. Don't use this version)
 - TaxeneCon setup (https://dotdash.atlassian.net/browse/BLNC-233)
 - Access log changes for backtracking (https://dotdash.atlassian.net/browse/HLTH-1429)

## 2.7.33
 - updated venus version to 0.0.116  - (https://dotdash.atlassian.net/browse/HLTH-1430)
 - Added PL test with 'isPV' =false validation -  this can be extended to each vertical (https://dotdash.atlassian.net/browse/GLBE-4576)

## 2.7.32
 - Add noop and Rubicon RTB plugins (https://dotdash.atlassian.net/browse/GLBEX-4807 & https://dotdash.atlassian.net/browse/GLBEX-4807)
 - Add getter and setter for image in AuthorEx (https://dotdash.atlassian.net/browse/GLBEX-4757)

## 2.7.31
 - Added accesslog validation feature to mantle - (https://dotdash.atlassian.net/browse/GLBE-4569)
 - updated venus version to 0.0.115  - (https://dotdash.atlassian.net/browse/HLTH-1430)

## 2.7.30
 - Focus on using BaseSlotTargeting (scp) as the means for communicating Amazon ad targeting parameters (https://dotdash.atlassian.net/browse/GLBEX-4782)

## 2.7.29
 - Make the Pageview regex in CookieFilter class case-insensitive

## 2.7.28
 - Remove noIndex and redirect documents from Category
 - Add document service for followSummaryRedirects parameter
 - Remove port rewrite from redirect target
 - Upgrade venus to 0.0.113

## 2.7.27 (Jenkins failure, skipped)

## 2.7.26
 - Use Brightcove's getAdId method

## 2.7.25
 - Update pattern library colors component to support sass variables and mixins

## 2.7.24
 - Adjust AFC client and channel for SEM tracking

## 2.7.23
 - Fix amazon affiliate subtag losing utm_params on chapter switch

## 2.7.22
 - Continue redirect if not correct header

## 2.7.21
 - Cherry pick pattern library form automation and isPageview changes from 2.8.11

## 2.7.20
 - Add more freemarker null checking for afc

## 2.7.19
 - Hide CPC refresh iframe that is needed for infinite scroll afc

## 2.7.18
 - Make ```google_safe``` in afc setup freemarker null safe

## 2.7.17
 - Escape js strings for hints to prevent js error
 - Port amazon affiliate refactor from 2.8

## 2.7.16
 - Mark AFC as loaded to prevent refresh

## 2.7.15
 - Do not require document for amazon affiliate

## 2.7.14
 - Fix missing tile scp param on refreshed gpt slots

## 2.7.13
 - Allow blank and null values to be passed into pageTargeting and baseSlotTargeting maps
 - Fix several bugs in pattern library: missing components; iframe resizing; unique form input ids
 - Migrate abstracted AFC code to mantle from abtnetwork/thebalance

## 2.7.12
 - Pass actor id from selene to dfp
 - Fix unified pageview's browserUA reporting

## 2.7.11
 - Stretch HTML5 video ad container wrapper & iframe to the height of the overall video-js wrapper

## 2.7.10
 - Update selene to 1.50.9

## 2.7.9
 - Execute onLoad callback function immediately if readyState is complete

## 2.7.8
 - Add mantle spring evaluator and context root object
 - Fix javadoc

## 2.7.7
 - Add Filters parameter to image render classes
 - Add Mantle Spring Evaluator and Mantle Context Root Object classes
 - Add test cases for image macros
 - Update globe-core to 2.2.18

## 2.7.6
 - Update selene to 1.50.7

## 2.7.5
 - Merge changes from hotfix/2.6 to hotfix/2.7
 - Add getRemainingLength to TaxonomyDocumentEx
 - Pass actor id from selene to dfp

## 2.7.4
 - Merged changes from hotfix/2.6
 - Upgrade to Node 4.2.6

## 2.7.3
 - Update globe-core to 2.2.15
 - Update hippodrome to 0.3.111

## 2.7.0
 - Update globe-core to 2.2.14
 - Update freemarker from 2.3.20 to 2.3.24-incubating

## 2.6.60
 - fix OpenX RTB plugin for refresh case (https://dotdash.atlassian.net/browse/GLBE-4671)

## 2.6.59 (failed concrete build, do not use)

## 2.6.58
 - Remove jQuery usage from RTB, eliminating race condition (https://dotdash.atlassian.net/browse/GLBE-4642)

## 2.6.57
 - Fix XSS vulnerability in convertHashToJs macro (https://dotdash.atlassian.net/browse/GLBE-4634)

## 2.6.56
 - Fix duplicate OpenX calls (https://dotdash.atlassian.net/browse/GLBE-4623)

## 2.6.55
 - Fix target="\_blank" security vulnerability (https://dotdash.atlassian.net/browse/GLBE-4592)

## 2.6.54
 - Fix issue with log containing carriage return in path (https://dotdash.atlassian.net/browse/GLBEX-4601)

## 2.6.53
 - Fix extra rubicon calls (https://dotdash.atlassian.net/browse/GLBEX-4811)
 - Update slot object name to slotObj as slot is now a reserved element attribute in Chrome (https://dotdash.atlassian.net/browse/HLTH-1532)

## 2.6.52
 - Downgrade mantle-grunt to fix build issues

## 2.6.51
 - Pass slots to RTB preload functions

## 2.6.50
 - Add OpenX RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4580)
 - Add Aol RTB plugin (https://dotdash.atlassian.net/browse/GLBE-4578)
 - Update Rubicon RTB plugin to only define main sizes for each slot
 - Upgrade venus to 0.0.118
 - Added SchemaMarkup Tests ( https://dotdash.atlassian.net/browse/GLBE-4590 )

## 2.6.49
 - Add noop and Rubicon RTB plugins (https://dotdash.atlassian.net/browse/GLBEX-4807 & https://dotdash.atlassian.net/browse/GLBEX-4807)
 - Add getter and setter for image in AuthorEx (https://dotdash.atlassian.net/browse/GLBEX-4757)

## 2.6.48
 - Focus on using BaseSlotTargeting (scp) as the means for communicating Amazon ad targeting parameters (https://dotdash.atlassian.net/browse/GLBEX-4782)

## 2.6.47
 - Remove noIndex and redirect documents from Category
 - Add document service for followSummaryRedirects parameter
 - Remove port rewrite from redirect target

## 2.6.46
 - Upgrade venus to 0.0.113

## 2.6.45
 - Use Brightcove's getAdId method

## 2.6.44
 - Add iuParts method to UrlParams

## 2.6.43
 - Fix amazon affiliate subtag losing utm_params on chapter switch

## 2.6.42
 - Continue redirect if not correct header

## 2.6.41
 - Port amazon affiliate refactor from 2.8

## 2.6.40
 - Do not require document for amazon affiliate

## 2.6.30 (2.6.20 base)
 - Fix missing tile scp param on refreshed gpt slots

## 2.6.24
 - Update venus version

## 2.6.23
 - Fix unified pageview's browserUA reporting

## 2.6.22 (Skipped)

## 2.6.21
 - Stretch HTML5 video ad container wrapper & iframe to the height of the overall video-js wrapper

## 2.6.20
 - Upgrade to globe-core 2.2.17

## 2.6.19
 - Add updatePageview method to GPT to update correlator
 - Add updateTargeting method to GPT Slot prototype
 - Merge changes from hotfix/2.5 into hotfix/2.6

## 2.6.18
 - Add hints() method to UrlParams
 - Call collapseEmptyDivs on GPT init

## 2.6.17 (Jenkins build failure)

## 2.6.16
 - Bugfix for SBS/IG content blocks splitting

## 2.6.15
 - Merged changes from hotfix/2.5 into hotfix/2.6

## 2.6.14
 - Remove resource timing plugin from boomerang evaluated plugins; update timestamp to use new boomerang dist file without res timing plugin

## 2.6.10
 - Redirect Filter added, see HLTH-1130

## 2.6.8
 - Remove deion caching from sitemap task
 - Split the deion client out into a separate bean
 - Revert targeting update on previously defined slots within updateBaseSlotTargeting

## 2.6.7
 - Expose ```removeRequestSizeFromSlot``` method in GPT library

## 2.6.6
 - Allow extension of pageview data through extraKeys property

## 2.6.5
 - Update globe-core version to 2.2.13
    - Add optional resizable tag to xml component info
 - Update globe-core version to 2.2.12
    - smart bundles model collapse issue fix, causes missing scripts and styles

## 2.6.4
 - Move content block splitting to mantle

## 2.6.3
 - Smart bundles

## 2.6.2 (jenkins failure, skipped version)

## 2.6.1
 - Updated methods in urlParam class and updated venus version for veryWell

## 2.6.0
 - Refactor GPT library
 - Client-side improvements for tracking resources across deferred requests

## 2.5.71
 - Stretch HTML5 video ad container wrapper & iframe to the height of the overall video-js wrapper

## 2.5.70
 - Fix EOS previews for documents programmed for a future date

## 2.5.69
 - Fix video player direct ad ids splitting

## 2.5.68
 - Added hints method to UrlParams

## 2.5.67
 - Session ID cookie filter to reset 30 min cookie expiry on each request (cherry picked from 2.7)

## 2.5.66
 - Site category projection support

## 2.5.63
 - XSD change for script and stylesheet tags for filterable
 - Fix video pausing when going to fullscreen
 - Set volume based on ad volume if current state is ad state
 - Remove frequency capping

## 2.5.62 (jenkins failure, skipped release)

## 2.5.61
 - Recirc keywords task

## 2.5.60
 - Override character count method from BaseDocumentEx

## 2.5.59
 - Add venus boomerang test
 - Bugfix for module-version data-attributes on mntl-html tag

## 2.5.58
 - HLTH-1108 - Changes for dynamic sitemaps

## 2.5.57
 - Update venus version to 0.0.101
 - Add videojsGaVideocloud to bower dependencies for video player plugins

## 2.5.56
 - HLTH-1138 - Remove header / footer tags from mntl-layout-body, allowing verticals to supply tags when implementing those components
 - Add volume level to MntlVideoPlayerComponent

## 2.5.55
 - Add ads to facebook instant

## 2.5.51 - 2.5.54 (jenkins failures, skipped versions)

## 2.5.50
 - Update video player and video ad player volume/muting logic

## 2.5.49
 - Set globe-core version to 2.2.11 to pick up rekon environment enum update to add EE env

## 2.5.48
 - Change Facebook Instant property name to match ftl

## 2.5.47
 - Change scope to public for getStateFromRequestContext and getDateFromRequestContext methods in DocumentTask

## 2.5.46
 - Updates to parameters passed to boomerang
    - Pass different taxonomy data (tax0,tax1, etc) to boomerang
    - Pass globe server timestamp
    - Rename browser timestamp to clientTimestamp
 - GLBEX-4441 - Add ```<script>``` tags to facebook instant tracking
 - HLTH-997 - Modify video player pause method to pause ad players as well as video players, and sync the volume between the two

## 2.5.45
 - GLBEX-4348 - Add Facebook Instant components

## 2.5.44
 - Update globe-core version to 2.2.10 for jolokia fix
 - GLBEX-4427 - Fix NPE thrown from relatedArticles task

## 2.5.43 (jenkins failure, skipped release)

## 2.5.42
 - Update venus version to 0.0.100
 - Update globe-core to 2.2.9, updates to subclasses calling Filterable.toXMLString method needed for cleaner XML output in the pattern library
 - Add all module and parent module versions as data-attributes to mntl-html tag
 - HLTH-1088 - Update CookieFilter to ignore comscore URL patterns for accurate PC cookie updating

## 2.5.41
 - Update globe-core to 2.2.8
 - Add deion search task to get documents by author key
 - Add ciaConfig taskModel to get cia config value by key name and allow for defaulting through XML
 - Update pattern library header to be configurable via CIA

## 2.5.40
 - Merged hotfix from abtnetwork

## 2.5.30
 - Hotfix for abtnetwork
    - New task to get module versions
    - Update Mntl.utilities.resourceVersion to accept an optional parameter specifying which module's resource version to look for on the html tag

## 2.5.22
 - HLTH-342 - Change lotame meta tag name format to look like "lotame:" and hide empty meta values

## 2.5.21
 - HLTH-978 - Updates to serve all resources out of the /static/ path, including:
    - mntl-favicons
    - indexHeaderTag loaded from RTB
    - taskExecution css loaded from the taskExecution library
    - pinitbtn.png

## 2.5.20
 - Make globe-core dependencies take precedence over selene dependencies

## 2.0.10 *\<PROPOSED\>*
 - add color swatches to pattern library
 - update pattern library to render each component in an iframe
 - added proctor test to remove scrollup delay on chapters
 - added task for sorting bulleted list alphabetically

## 2.0.9
 - update pattern library to source default.ftl from /components/base

## 2.0.8
 - fix bugs in RequestContextFilter that were omitted from globe merge
 - reorganize components into more specific folder hierarchy
 - rename mntl-layout-defalt to mntl-layout-asides
 - fixes to mantle grunt tasks
