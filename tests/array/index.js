return new UnitTest('Array', function() { []; })
    .add(require('constructor.js'))
    .add(require('length.js'))
    .add(require('reduce.js')('reduce'))
    .add(require('reduce.js')('reduceRight'))
    .add(require('sparse.js'))
    .add(require('concat.js'))
    .add(require('sort.js'))
    .add(require('push.js'))
    .add(require('pop.js'))
    .add(require('fill.js'))
    .add(require('find.js'))
