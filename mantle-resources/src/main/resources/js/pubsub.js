/**
 * Create a Pub Sub Object that can be attached to any system that needs it.
 * Expose only the methods that make sense for your usage.
 * Example:
 * Mntl.RTB = (function(document) {
 *   var events = new Mntl.PubSub(['onBeforeLoad', 'onAfterLoad', 'onBeforeSetup', 'onAfterSetup']);
 *
 *   // Later functions emit events by calling
 *   function _load() {
 *      events.publish('onBeforeLoad', dataObject);
 *   }
 *
 *   // Return public interface
 *   return {
 *      init: init,
 *      subscribe: events.subscribe.bind(events),
 *      listEvents: events.what.bind(events)
 *   }
 * }());
 *
 * See code for more methods available on PubSub
 *
 * So what the heck is a PubSub and why would you want to use it?
 * Well sometimes you'd like to ineract with your code the same way you interact
 * with the DOM ala addEventListener || $.on. PubSub let's you do that via two
 * main events. The subscribe() function adds a callback function to an array
 * stored in this object. The publish() function fires all of the callbacks added
 * by subscribe(). Calls to the publish method need to be placed in your code as
 * in the example above.
 *
 * PubSub // constructor
 * PubSub.prototype.what // List event types
 * PubSub.prototype.addEvent // Add an event type
 * PubSub.prototype.deleteEvent // Delete an event type
 * PubSub.prototype.publish // Call all listeners on this event
 * PubSub.prototype.subscribe // Add a one time or all times event listener
 * PubSub.prototype.on   // Add an event listener (uses subscribe)
 * PubSub.prototype.once // Add a one time event listener (uses subscribe)
 */
window.Mntl = window.Mntl || {};

window.Mntl.PubSub = (function() {
    var EVENT_ONCE = 'once';
    var EVENT_ON = 'on';
    var ERROR_PUBLISH = 'Attempted to publish an event that doesn\'t exist';
    var ERROR_SUBSCRIBE = 'Attempted to subscribe to an event that doesn\'t exist';
    var ERROR_FREQUENCY = 'Attempted to listen to an event at an incorrect frequency. Please use "on" or "once".';

    /**
     * A function to do nothing
     * @return {undefined}
     */
    // eslint-disable-next-line no-empty-function
    function _noop() {}

    /**
     * Remove an event listener from the store
     * This function is meant to be partially applied and returned from subscribe
     * so the user can unsub without needing to know anything. myVar.unSubscribe()
     * @param  {string}  eventName event being listened to
     * @param  {integer} index     position of the listener in the storage array
     * @return {undefined}
     */
    function unSubscribe(eventName, index) {
        this.events[eventName][index].callback = _noop;
        this.events[eventName][index].frequency = null; // prevent re-assignment in publish methed
    }

    /**
     * [Events description]
     * @param       {[string]} events list of events PubSub will handle
     * @constructor
     */
    function PubSub(events) {
        this.events = events.reduce(function(acc, event) {
            acc[event] = [];

            return acc;
        }, {});
    }

    /**
     * List of all stored events
     * @return {string} comma separated list of events
     */
    PubSub.prototype.what = function() {
        return Object.keys(this.events).join(', ');
    };

    /**
     * Add a new event type to the store
     * @param  {string} eventName event to add
     * @return {boolean}
     */
    PubSub.prototype.addEvent = function(eventName) {
        if (!this.events.hasOwnProperty(eventName)) {
            this.events[eventName] = [];

            return true;
        }

        return false;
    };

    /**
     * Delete an event and all listeners from the store
     * @param  {string} eventName event to delete
     * @return {boolean}
     */
    PubSub.prototype.deleteEvent = function(eventName) {
        if (this.events.hasOwnProperty(eventName)) {
            delete this.events[eventName];

            return true;
        }

        return false;
    };

    /**
     * Run all listeners for a given event
     * @param  {string} eventName event to fire
     * @param  {object} data      provided to the listener
     * @return {undefined}
     */
    PubSub.prototype.publish = function(eventName, data) {
        if (!this.events.hasOwnProperty(eventName)) {
            throw (new ReferenceError(ERROR_PUBLISH));
        }

        data = data || {};
        data.type = data.type || eventName;

        this.events[eventName].forEach(function(listener) {
            if (!listener.instigator || listener.instigator === data.instigator) {
                listener.callback(data);

                if (listener.frequency === EVENT_ONCE) {
                    listener.callback = _noop;
                    listener.frequency = null; // prevent future re-assignment
                }
            }
        });
    };

    /**
     * Subscribe to an event
     * @param  {string}   eventName   event to listen to
     * @param  {string}   instigator  extra dimension to determine if the listener should fire
     * @param  {Function} callback    function to fire when event happens
     * @param  {string}   frequency   on|once
     * @return {object}   unSubscribe call to unsub this event
     */
    PubSub.prototype.subscribe = function(eventName, instigator, callback, frequency) {
        var index;

        if (!this.events.hasOwnProperty(eventName)) {
            throw (new ReferenceError(ERROR_SUBSCRIBE));
        }

        if ([EVENT_ON, EVENT_ONCE].indexOf(frequency) === -1) {
            throw (new ReferenceError(ERROR_FREQUENCY));
        }

        index = this.events[eventName].push({
            callback: callback,
            frequency: frequency,
            instigator: instigator
        }) - 1;

        return { unSubscribe: unSubscribe.bind(this, eventName, index) };
    };

    /**
     * Common interface for subscribing to PubSub.
     * @see subscribe()
     */
    PubSub.prototype.on = function(eventName, instigator, callback) {
        this.subscribe(eventName, instigator, callback, EVENT_ON);
    };

    /**
     * Common interface for subscribing to events one time.
     * @see subscribe()
     */
    PubSub.prototype.once = function(eventName, instigator, callback) {
        this.subscribe(eventName, instigator, callback, EVENT_ONCE);
    };

    return PubSub;
}());
