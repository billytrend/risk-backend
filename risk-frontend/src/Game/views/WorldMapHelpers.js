var GameStore = require('../stores/GameStore'),
    VelocityJS = require('velocity-animate'),
    TinyColor = require('tinycolor2');

module.exports = {

    mapColourToDefence: function(svg) {
        var gameState = GameStore.getGameState();
        gameState.map.countries.forEach(function(country) {
            var ele = svg.getElementById(country);
            if (ele != null && country in gameState.owners) {
                var colour = gameState.players[gameState.owners[country].player].color;
                var armies = gameState.owners[country].armies;
                var setIntensity = TinyColor(colour).brighten(armies/5);
                ele.setAttribute("fill", setIntensity);
            }
        });
    },

    focusOnCountries: function(doc, boxParent, ids, padding) {
        var self = this;

        var uberBox = ids.reduce(function(prev, cur) {
            var country = doc.getElementById(cur);
            if (country === null) return prev;
            return self.addBoxes(prev, country.getBBox());
        }, undefined);

        this.frame(boxParent, uberBox, padding);
    },

    addBoxes: function(a, b) {

        if(a === undefined) return b;

        var newBox = {};

        var leastX = a.x > b.x ? b.x : a.x,
            mostX = a.x + a.width > b.x + b.width ? a.x + a.width : b.x + b.width;

        newBox.x = leastX;
        newBox.width = mostX - leastX;

        var leastY = a.y > b.y ? b.y : a.y,
            mostY = a.y + a.height > b.y + b.height ? a.y + a.height : b.y + b.height;

        newBox.y = leastY;
        newBox.height = mostY - leastY;

        return newBox;
    },

    frame: function(boxParent, box, padding) {

        if (padding === undefined) {
            padding = 0;
        }

        var frame = boxParent.getBBox()
            // check required zoom levels for each dimension
            , zoomY = frame.height / (box.height + 2 * padding)
            , zoomX = frame.width / (box.width + 2 * padding)
            // set defaults
            , zoom = 1
            , shiftY = -box.y
            , shiftX = -box.x;

        // test which dimension zooms less and use that zoom level
        if (zoomX > zoomY) {
            zoom = zoomY;
            // add an offset so the box is centred in x
            shiftX = shiftX + ((frame.width / zoom) - box.width) / 2;
            shiftY += padding;
        }  else {
            zoom = zoomX;
            // add an offset so the box is centred in y
            shiftY = shiftY + ((frame.height / zoom) - box.height) / 2;
            shiftX += padding;
        }

        // pan and zoom
        VelocityJS(boxParent, {translateX: shiftX * zoom, translateY: shiftY * zoom, scaleX: zoom, scaleY: zoom}, 300);

    },

    reset: function(boxParent) {
        VelocityJS(boxParent, {translateX: 0, translateY: 0, scaleX: 1, scaleY: 1}, 300);
    },

    shake: function(country) {
        for (var i = 0; i < 100; i++) {
            var shake = i % 2 === 0 ? 155 :1;
            VelocityJS(country, {fillRed: shake}, 1, [1]);
        }
        VelocityJS(country, {translateX: 0}, 20);
    }

};