return new UnitTest('counters')
    .add('postfix increment', function () { var i = 10; i++ === 10; })
    .add('postfix decrement', function () { var i = 10; i-- === 10; })
    .add('prefix decrement', function () { var i = 10; --i === 9; })
    .add('prefix increment', function () { var i = 10; ++i === 11; })
    .add('ostfix increment of non-number', function () { var i = 'hi mom'; isNaN(i++); })
    .add('ostfix decrement of non-number', function () { var i = 'hi mom'; isNaN(i--); })
    .add('prefix increment of non-number', function () { var i = 'hi mom'; isNaN(++i); })
    .add('prefix decrement of non-number', function () { var i = 'hi mom'; isNaN(--i); })
    .add('postfix increment of convertible to number', function () { var i = '10'; i++; i === 11; })