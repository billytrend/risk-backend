var constants = require('./Actions').constants;

var TodoStore = Fluxxor.createStore({
    initialize: function() {
        this.todos = [];

        this.bindActions(
            constants.ADD_TODO, this.onAddTodo,
        );
    },


    getState: function() {
        return {
            todos: this.todos
        };
    }
});
