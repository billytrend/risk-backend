var webpack = require('webpack');

module.exports = {
    entry: [
        './src/index.jsx'
    ],
    output: {
        path: __dirname + '/../ui-build',
        filename: 'bundle.js'
    },
    resolve: {
        extensions: ['', '.js']
    },
    module: {
        loaders: [
            { test: /\.jpg$/, loader: 'file-loader' },
            { test: /\.svg$/, loader: 'file-loader' },
            { test: /\.mp3$/, loader: 'file-loader' },
            { test: /\.css$/, loader: 'style-loader!css-loader!autoprefixer-loader'},
            { test: /\.html/, loader: 'file?name=[name].[ext]' },
            { test: /\.jsx$/, loaders: ['react-hot', 'jsx?harmony'], exclude: /node_modules/ },
        ]
    }
};
