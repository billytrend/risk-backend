module.exports = {
  "map": {
    "countries": ["eastern_australia","indonesia", "new_guinea", "alaska", "ontario", "northwest_territory", "venezuela", "madagascar", "north_africa", "greenland", "iceland", "great_britain", "scandinavia", "japan", "yakutsk", "kamchatka", "siberia", "ural", "afghanistan", "middle_east", "india", "siam", "china", "mongolia", "irkutsk", "ukraine", "southern_europe", "western_europe", "northern_europe", "egypt", "east_africa", "congo", "south_africa", "brazil", "argentina", "eastern_united_states", "western_united_states", "quebec", "central_america", "peru", "western_australia", "alberta"],
    "borders": [
      ["test", "iceland"],
      ["great_britain", "iceland"],
      ["great_britain", "scandinavia"],
      ["great_britain", "northern_europe"],
      ["great_britain", "western_europe"],
      ["western_europe", "southern_europe"],
      ["western_europe", "northern_europe"],
      ["ukraine", "northern_europe"],
      ["ukraine", "southern_europe"],
      ["ukraine", "scandinavia"],
      ["sime_land", "scandinavia"],
      ["_land", "scandinavia"]
    ]
  },
  "ownerships" : {
    "eastern_australia" : {
      "player": "alice",
      "armies": 10
    },
    "iceland": {
      "player": "mike",
      "armies": 15
    },
    "northern_europe": {
      "player": "paula",
      "armies": 5
    },
    "north_africa": {
      "player": "paula",
      "armies": 50
    }
  },
  "players": {
    "mike": {},
    "alice": {},
    "paula": {},
    "peter": {}
  },
  "currentGo": 0,
  "deck": []
}

