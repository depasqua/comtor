<?php
    // $Id: spl_examples.php,v 1.1 2009-04-28 17:58:30 ssigwart Exp $

    class IteratorImplementation implements Iterator {
        function current() { }
        function next() { }
        function key() { }
        function valid() { }
        function rewind() { }
    }

    class IteratorAggregateImplementation implements IteratorAggregate {
        function getIterator() { }
    }
?>