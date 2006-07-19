#!/usr/bin/perl
# Michael E. Locasto
# comtor hello world

use CGI;
use strict;


#gather info
my $query = CGI::new();

#my $samplevar = $query->param("");

#thank usr
print $query->header( "text/html", 200 );
print $query->start_html( " Comment Mentor ");

print $query->h3( "Thank you for your submission." );
print $query->hr();
print $query->p( "A copy of your file appears below." );
#print $query->p( $registration );

my $fileurl = $query->param("fileurl");
my $filetype = $query->param("sourceType");

print $query->p($fileurl);
print $query->br();
print $query->p($filetype);

print $query->hr();
print $query->end_html();




