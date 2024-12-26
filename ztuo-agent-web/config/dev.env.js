'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')
// 天美社区源码网 timibbs.net timibbs.com timibbs.vip
module.exports = merge(prodEnv, {
  NODE_ENV: '"development"'
})
