var React = require('react/addons'),
    Card = require('./Card.jsx'),
    ServerRequest = require('../../stores/ServerRequest'),
    ServerActions = require('../../actions/ServerActions');

module.exports = React.createClass({

    submit: function(index) {
        ServerActions.selectCardCombination(index);
    },

    render: function() {
        var self = this;
        return (
            <div className="view_layer card_box">
            {
                self.props.cardMeta.possibles.map(function (cardTriple, index) {
                    return <div>
                            {
                                cardTriple.valueArray.map(function(card) {
                                    return <Card cardType={ card.type } countryName={ card.territory.id } />
                                })
                            }
                            <div className="play_card">
                                <button onClick={ self.submit.bind(null, index) }>Play card combination</button>
                            </div>
                        </div>
                })
            }
            </div>
        )
    }
});
