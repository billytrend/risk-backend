var webpack = require('webpack');

module.exports = {
    devtool: 'inline-source-map',
    entry: [
        'webpack-dev-server/client?http://0.0.0.0:3000',
        'webpack/hot/only-dev-server',
        './src/index.jsx'
    ],
    output: {
        path: __dirname + '/',
        filename: 'bundle.js',
        publicPath: '/'
    },
    plugins: [
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin()
    ],
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
