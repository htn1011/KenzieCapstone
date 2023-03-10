import BaseClass from "./baseClass";

/**
 * Stores all of the data across the state. When the state changes in any way, execute all of the listeners registered
 * with the DataStore. This way pages can listen to changes for specific data and refresh the page accordingly.
 */
export default class DataStore extends BaseClass {

    constructor() {
        super();
        this.bindClassMethods(['getState', 'get', 'setState', 'set', 'addChangeListener', 'clear'], this);
        this.state = localStorage;
        this.listeners = [];
    }

    /**
     * Get all of the data.
     */
    getState() {
        return this.state;
    }

    /**
     * Get a specific attribute out of the DataStore.
     * @param attribute The attribute to get.
     * @returns The current value of that attribute.
     */
    get(attribute) {
        return JSON.parse(this.state.getItem(attribute));
    }

    /**
     * Merge the current state of the DataStore with the new state. If there are any overlapping keys, overwrite the
     * values with the new state. Then execute all of the registered listeners, so they can react to any potential data
     * updates.
     */
    setState(newState) {
        // ... is the spread operator. This allows us to pull out all of the keys and values of the existing state and
        // the new state and combine them into one new object.
//        this.state = {...this.state, ...newState};
        Object.entries(newState)
            .filter(([attribute, value]) => value !== undefined)
            .forEach(([attribute, value]) => {
                this.state.setItem(attribute, JSON.stringify(value));
            });
        this.listeners.forEach(listener => listener());
    }

    /**
     * Set or update the state of a specific attribute. Then execute all of the registered listeners, so they can react
     * to any potential data updates.
     * @param attribute The attribute to set or update.
     * @param value The value to give the attribute.
     */
    set(attribute, value) {
        if (value !== undefined) {
            this.state.setItem(attribute, JSON.stringify(value));
        }
        this.listeners.forEach(listener => listener());
    }

    remove(attribute) {
        if (attribute !== undefined) {
            this.state.removeItem(attribute);
        }
    }

    setSilent(attribute, value) {
        if (value !== undefined) {
            this.state.setItem(attribute, JSON.stringify(value));
        }
    }

    /**
     * Add a listener. Whenever the state is changed in the DataStore all of the listeners will be executed.
     */
    addChangeListener(listener) {
        this.listeners.push(listener);
    }

    clear() {
        this.state.clear();
    }

}
