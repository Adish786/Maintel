window.Mntl = window.Mntl || {};

Mntl.Quiz = (function(utils) {
    /**
     * Update the history to reflect quizResult for sharing
     * @param  {String} id
     * @return {[type]}    [description]
     */
    function _updateHistory(id) {
        if (Mntl.fnUtilities.getDeepValue(window, 'history', 'pushState')) {
            const newLocation = `${window.location.href}${window.location.search.indexOf('?') === -1 ? '?' : '&'}quizResult=${id.substring(0, id.indexOf('-'))}`;

            window.history.pushState({}, '', newLocation);
        }
    }

    /**
     * Push quiz events to the global dataLayer object
     * @param       {string} eventAction string we want to fill in the eventAction field for the GA event
     * @constructor
     */
    function _quizEventTracking(eventAction) {
        dataLayer.push({
            event: 'quizEvent',
            eventLabel: window.location.href,
            eventCategory: 'QuizQuestionAnswer',
            eventAction,
            eventValue: undefined, // eslint-disable-line no-undefined
            nonInteraction: false
        });
    }

    /**
     * Scroll winning result into view
     * @param       {[type]} winningId [description]
     * @constructor
     * @return      {[type]}           [description]
     */
    function _displayWinner(winningId) {
        const eventAction = `quizQuestionAnswer|ResultsShown|${winningId}`;
        const result = document.querySelectorAll(`section[data-value="${winningId}"]`)[0];
        let start;

        result.classList.add('active');

        // Trigger quiz tracking event and show results
        _quizEventTracking(eventAction);

        // const defined here because result offset is set once the active class is added
        // it is hidden initially
        const scrollTo = result.offsetTop - 70;

        // Setup Scroll to the Answer Div when the last answer is clicked
        function scrollToAnswerStep(timeStamp) {
            if (typeof start === 'undefined') {
                start = timeStamp;
            }

            const elapsedTime = timeStamp - start;
            const nextYCoordinate = window.pageYOffset + (0.1 * elapsedTime);

            window.scrollTo(0, nextYCoordinate);

            if (nextYCoordinate < scrollTo) {
                window.requestAnimationFrame(scrollToAnswerStep);
            }
        }

        window.requestAnimationFrame(scrollToAnswerStep);

        _updateHistory(winningId);
    }

    /**
     * Mutate each shareLink data-href attribute
     * @param {[type]} shareLinks       [description]
     * @param {[type]} percentContainer [description]
     */
    function _setHref(shareLinks, percentContainer) {
        [...shareLinks].forEach((link) => {
            const shareHref = link.dataset.href;

            link.dataset.href = shareHref.replace('PERCENTAGE', encodeURIComponent(percentContainer.innerText));
        });
    }

    function init(container) {
        const resultsCalc = container.getElementsByClassName('mntl-quiz')[0].dataset.resultsCalc;
        const questionTotal = container.getElementsByClassName('question').length;

        function calculateBestFitWinner(scoreValue, winningId) {
            const numOfQuestions = container.getElementsByClassName('question').length;

            [...container.getElementsByClassName('result')].forEach((result) => {
                const dimensions = result
                    .dataset.dimensions
                    .replace(/\s+/g, '')
                    .split('.');
                let i = 0;
                let percentContainer;
                let percentCorrect;
                let dimension;
                let dimensionId;
                let min;
                let max;

                for (i; i < dimensions.length; i++) {
                    dimension = dimensions[i].split(',');
                    dimensionId = dimension[0];
                    min = dimension[1];
                    max = dimension[2];

                    // check to see if the answer value falls into the range of the min and max of the dimension
                    if (winningId === dimensionId && scoreValue >= min && scoreValue <= max) {
                        if (resultsCalc === 'PERCENT_CORRECT') {
                            percentContainer = result.getElementsByClassName('percent-display')[0];
                            percentCorrect = Math.round((scoreValue / numOfQuestions) * 100);
                            percentContainer.prepend(percentCorrect);
                            _setHref(result.querySelectorAll('share-link'), percentContainer);
                        }
                        _displayWinner(result.dataset.value);
                    }
                }
            });
        }

        function calculateAnswers() {
            const answerValues = {};
            const selectedAnswers = [];
            const sortedAnswers = [];
            const userAnswers = [...container.getElementsByClassName('checked')];
            let key;

            // push the user's answers into an array of maps
            userAnswers.forEach((answer) => {
                const pairs = answer.dataset.answerValue
                    .replace(/\s+/g, '')
                    .split(',');
                const pairsLength = pairs.length;
                let i;
                let map;
                let pair;

                for (i = 0; i < pairsLength; i++) {
                    map = {};
                    pair = pairs[i].split(':');

                    map[pair[0]] = parseInt(pair[1], 10);
                    selectedAnswers.push(map);
                }
            });

            // sum those maps
            selectedAnswers.forEach((entry) => {
                for (key in entry) {
                    const value = entry[key];

                    if (typeof value === 'number') {
                        // if the key exists, sum the values, otherwise add the key
                        answerValues[key] = key in answerValues ? answerValues[key] + value : value;
                    }
                }
            });

            // sort answerValues
            for (key in answerValues) {
                sortedAnswers.push([key, answerValues[key]]);
            }

            sortedAnswers.sort((a, b) => b[1] - a[1]);

            const userScoreValue = sortedAnswers[0][1];
            const winningId = sortedAnswers[0][0];

            if (resultsCalc === 'BEST_FIT' || resultsCalc === 'PERCENT_CORRECT') {
                calculateBestFitWinner(userScoreValue, winningId);
            } else if (resultsCalc === 'WINNER_TAKE_ALL') {
                _displayWinner(winningId);
            }
        }

        [...container.querySelectorAll('.answer a')].forEach((answer) => {
            const currentQuestion = utils.getClosestMatchingParent(answer, '.question');
            const currentReveal = currentQuestion.getElementsByClassName('reveal')[0];

            // eslint-disable-next-line consistent-return
            function handleAnswerClick() {
                const currentAnswerLi = answer.parentElement; // Current <li> element that contains the answer

                if (resultsCalc === 'PERCENT_CORRECT' && currentAnswerLi.classList.contains('locked')) {
                    return;
                }

                // This block sets the correct checked, x'd, and disabled hover states for all the answers
                // in the question.
                [...currentAnswerLi.parentElement.getElementsByTagName('li')].forEach((answerLi) => {
                    answerLi.classList.remove('checked');
                    answerLi.classList.add('has-hover');
                });
                currentAnswerLi.classList.add('checked');
                currentAnswerLi.classList.remove('has-hover');

                const totalAnswered = container.getElementsByClassName('checked').length;

                // Reveal results if all answers are completed
                if (totalAnswered === questionTotal) {
                    calculateAnswers();
                    // container.removeEventListener('click');
                    container.classList.remove('has-hover');
                    container.style.cursor = 'default';
                }

                const eventAction = `quizQuestionAnswer|Question${currentQuestion.dataset.index}|Answer${currentAnswerLi.dataset.index}`;

                _quizEventTracking(eventAction);

                // Logic block to provide scrolling animation for reveal if mobile
                const scrollPadding = 55; // Magic number to scroll before the element
                const scrollTo = utils.getClosestMatchingParent(answer, '.answers').offsetTop - scrollPadding;
                let start;

                function scrollToRevealStep(timeStamp) {
                    if (typeof start === 'undefined') {
                        start = timeStamp;
                    }

                    const elapsedTime = timeStamp - start;
                    const nextYCoordinate = window.pageYOffset + (0.1 * elapsedTime);

                    window.scrollTo(0, nextYCoordinate);

                    if (nextYCoordinate < scrollTo) {
                        window.requestAnimationFrame(scrollToRevealStep);
                    }
                }

                if (resultsCalc === 'PERCENT_CORRECT') {
                    const answerVal = (currentAnswerLi.dataset.answerValue.indexOf(':0') !== -1) ? 'wrong' : 'correct';

                    // Show reveal
                    currentReveal.classList.remove('collapsed');
                    currentReveal.classList.add('expanded', answerVal);
                    currentReveal.getElementsByClassName(answerVal)[0].classList.remove('inactive');

                    // Scroll to reveal (mobile only)
                    const mq = window.matchMedia('(max-width: 450px)');

                    if (mq.matches && totalAnswered < questionTotal) {
                        // window.scrollIntoView() doesn't scroll properly to reveal view.
                        window.requestAnimationFrame(scrollToRevealStep);
                    }

                    // Lock all answers in the question
                    [...currentQuestion.getElementsByClassName('answer')].forEach((ans) => {
                        const answerLink = ans.getElementsByTagName('a')[0];

                        ans.classList.add('locked');
                        ans.classList.remove('has-hover');
                        answerLink.removeEventListener('click', handleAnswerClick);
                        answerLink.style.cursor = 'default';
                    });
                }
            }

            answer.addEventListener('click', handleAnswerClick);
        });
    }

    return { init };
})(window.Mntl.utilities || {});

Mntl.utilities.readyAndDeferred((element) => {
    const container = (element.jquery) ? element[0].getElementsByClassName('article') : element.getElementsByClassName('article');

    if (container.length > 0) {
        Mntl.Quiz.init(container[0]);
    }
});
