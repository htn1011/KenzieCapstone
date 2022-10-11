const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyPlugin = require("copy-webpack-plugin");
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

module.exports = {
  optimization: {
    usedExports: true
  },
  entry: {
    summaryPage: path.resolve(__dirname, 'src', 'pages', 'summaryPage.js'),
    userLoginPage: path.resolve(__dirname, 'src', 'pages', 'userLoginPage.js'),
    editSummaryPage: path.resolve(__dirname, 'src', 'pages', 'editSummaryPage.js'),
  },
  output: {
    path: path.resolve(__dirname, 'dist'),
    filename: '[name].js',
  },
  devServer: {
    https: false,
    port: 8080,
    open: true,
    openPage: 'http://localhost:8080/summary.html',
    // diableHostChecks, otherwise we get an error about headers and the page won't render
    disableHostCheck: true,
    contentBase: 'packaging_additional_published_artifacts',
    // overlay shows a full-screen overlay in the browser when there are compiler errors or warnings
    overlay: true,
    // https://stackoverflow.com/questions/31602697/webpack-dev-server-cors-issue
    proxy: {
        '/game':{
            target: {
                host: '127.0.0.1',
                protocol: 'http:',
                port: 5001
            }
        }
    }
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: './src/summary.html',
      filename: 'summary.html',
      inject: false
    }),
    new HtmlWebpackPlugin({
          template: './src/userLogin.html',
          filename: 'userLogin.html',
          inject: false
        }),
    new HtmlWebpackPlugin({
              template: './src/editSummary.html',
              filename: 'editSummary.html',
              inject: false
            }),
    new CopyPlugin({
      patterns: [
        {
          from: path.resolve('src/css'),
          to: path.resolve("dist/css")
        }
      ]
    }),
    new CopyPlugin({
          patterns: [
            {
              from: path.resolve('src/images'),
              to: path.resolve("dist/images")
            }
          ]
        }),
    new CleanWebpackPlugin()
  ]
}
