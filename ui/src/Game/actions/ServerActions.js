var Actions = require('./Actions');

module.exports = {

    selectDice: function(number) {
        console.log(number)
        Actions.respondToRequest({
            commandType: "DiceNumberResponse",
            n: number
        });
    },

    selectArmies: function(number) {
        console.log(number)
        Actions.respondToRequest({
            commandType: "ArmyResponse",
            n: number
        });
    },

    selectTerritory: function(territory) {
        console.log(territory)
        Actions.respondToRequest({
            commandType: "TerritoryResponse",
            territory: territory
        });
    },

    selectCardCombination: function(index) {
        Actions.respondToRequest({
            commandType: "CardResponse",
            index: index
        });
    }

};