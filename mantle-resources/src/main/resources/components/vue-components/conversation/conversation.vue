<template>
    <div :class="[appClasses, 'mntl-conversation']">
        <div
            v-for="(section, sectionIndex) in timeline"
            :key="sectionIndex"
            :class="[{'is-done': section.done}, 'convo-section']"
        >
            <template v-for="text in section.text">
                <transition
                    :key="text.index"
                    name="fade"
                    appear
                >
                    <div
                        v-if="text.type == 'text'"
                        class="convo__response"
                    >
                        <div
                            v-if="text.heading"
                            class="convo__speaker-name"
                        >
                            {{ text.heading }}
                        </div>
                        <span class="convo__text-bubble">{{ text.data }}</span>
                    </div>
                </transition>

                <transition
                    :key="text.index"
                    name="fade"
                    appear
                >
                    <div
                        v-if="text.type == 'advice'"
                        class="convo__advice"
                    >
                        <div
                            v-if="text.user"
                            class="advice__speaker"
                        >
                            <img
                                v-if="text.user.image"
                                class="advice__speaker-img"
                                :src="iconPrePath + text.user.image"
                            >
                            <span>{{ text.user.name }}</span>
                        </div>
                        <div
                            :class="[text.icon ? 'advice__' + text.icon : '', 'advice__text']"
                            v-html="text.data"
                        />
                    </div>
                </transition>

                <transition
                    :key="text.index"
                    name="fade"
                    appear
                >
                    <div
                        v-if="text.type == 'takeaway'"
                        class="convo__takeaway"
                    >
                        <h3 class="takeaway__heading">
                            {{ text.heading }}
                        </h3>
                        <div
                            class="takeaway__text"
                            v-html="text.data"
                        />
                        <div :class="[{'is-multiple': text.buttons && text.buttons.length > 1}, 'takeaway__buttons']">
                            <a
                                v-for="button in text.buttons"
                                :key="button.index"
                                :href="button.url"
                                class="takeaway__button"
                                target="_blank"
                            >
                                <img
                                    v-if="button.icon"
                                    class="takeaway__btn-img"
                                    :src="iconPrePath + button.icon"
                                >
                                <div class="takeaway__btn-text">
                                    {{ button.buttonText }}
                                    <span class="takeaway__btn-filetype">({{ button.fileType }})</span>
                                </div>
                            </a>
                        </div>
                    </div>
                </transition>
            </template>

            <div
                v-if="section.choices.length > 0"
                :class="[!section.heading ? 'convo__path' : null, 'convo__choices']"
            >
                <transition
                    name="fade"
                    appear
                >
                    <h3
                        v-if="section.heading"
                        class="choice__heading"
                    >
                        {{ section.heading }}
                    </h3>
                </transition>
                <div
                    v-for="(choice, choiceIndex) in section.choices"
                    :key="choiceIndex"
                    class="convo__choice"
                >
                    <transition
                        name="fade"
                        appear
                    >
                        <div
                            v-if="choice.selected && section.heading"
                            class="convo__speaker-name"
                        >
                            You
                        </div>
                    </transition>

                    <transition
                        name="fade"
                        appear
                    >
                        <div
                            v-if="choice.selected"
                            class="convo__text-bubble choice__text-bubble"
                        >
                            {{ choice.text }}
                        </div>
                    </transition>

                    <transition
                        name="fade"
                        appear
                    >
                        <a
                            v-if="!section.done"
                            href="#"
                            class="choice__button"
                            @click.prevent.stop="makeChoice(choice, section)"
                        >
                            {{ choice.text }}
                        </a>
                    </transition>
                </div>
            </div>
        </div>

        <div
            v-if="loading"
            class="typing-animation"
        >
            <span class="typing-animation__dot" />
            <span class="typing-animation__dot" />
            <span class="typing-animation__dot" />
        </div>
    </div>
</template>

<script>

module.exports = {
    name: 'ConversationApp',
    data() {
        return {
            script: Array,
            timeline: [],
            loading: false,
            appClasses: String
        };
    },
    mounted() {
        this.start();
    },
    methods: {
        start() {
            const choice = {
                target: 0
            };

            this.makeChoice(choice, null);
        },
        makeChoice(choice, section) {
            let i = 0;
            const page = this.script[choice.target];
            const newSection = {
                text: [],
                choices: [],
                heading: null
            };

            this.timeline.push(newSection);
            const index = this.timeline.length - 1;

            (function buildSection() {
                let delay = 0;

                if (i < page.text.length && page.text[i].type !== 'advice' && page.text[i].type !== 'takeaway') {
                    delay = page.text[i].data.length * 30;
                    this.loading = true;
                } else if (i !== 0) {
                    delay = 1000;
                }

                setTimeout(() => {
                    if (i < page.text.length) {
                        this.timeline[index].text.push(page.text[i]);
                        this.loading = false;
                        i++;
                        buildSection.bind(this)();
                    } else {
                        this.timeline[index].choices = page.choices || [];
                        this.timeline[index].heading = page.heading;
                    }
                    this.$nextTick(() => {
                        document.dispatchEvent(new CustomEvent('sticky_kit:recalc'));
                    });
                }, delay);
            }.bind(this))();

            choice.selected = true;

            if (section) {
                section.done = true;
            }
        }
    }
};
</script>

<style lang="scss">
.mntl-conversation {
    overflow: hidden;
}

.convo-section {
    clear: both;
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.7s;
}

.fade-enter {
    opacity: 0;
}

.convo__speaker-name {
    .convo__choices & {
        text-align: right;
    }
}

.advice__speaker {
    display: flex;
}

.convo__choices {
    float: right;

    &.convo__path {
        float: none;
    }
}

.convo-section.is-done .choice__heading {
    display: none;
}

.typing-animation {
    background-color: rgba(221,221,221,.6);
    will-change: transform;
    width: auto;
    border-radius: 25px;
    padding: .875rem;
    display: table;
    margin-top: 1rem;
    position: relative;
    animation: 2s bulge infinite ease-out;
}

.typing-animation__dot {
    height: 8px;
    width: 8px;
    float: left;
    margin: 0 1px;
    background-color: #9E9EA1;
    display: block;
    border-radius: 50%;
    opacity: 0.4;

    @for $i from 1 through 3 {
        &:nth-of-type(#{$i}) {
            animation: 1s blink infinite ($i * .3333s);
        }
    }
}

// loading animations
@keyframes blink {
    50% {
        opacity: 1;
    }
}

@keyframes bulge {
    50% {
        transform: scale(1.05);
    }
}
</style>
