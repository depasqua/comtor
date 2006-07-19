#!/opt/local/bin/perl
# Michael E. Locasto
# comtor hello world

use CGI;
use LWP::Simple;
use strict;
#use Net::HTTP;


#gather info
my $query = CGI::new();

print $query->header( "text/html", 200 );
print $query->start_html( " Comment Mentor ");

print $query->h3( "Thank you for your submission." );
print $query->hr();
print $query->p( "A copy of your file appears below." );

my $fileurl = $query->param("fileurl");
my $filetype = $query->param("sourceType");
my $escaped_string;

my $doc = get($fileurl);
die "Couldn't get file URL." unless defined $doc;

# see: http://search.cpan.org/dist/libwww-perl/lib/LWP/Simple.pm
# getstore($fileurl, $tempfile) is exactly what we need.

#$escaped_string = escapeHTML($fileurl);
#print $query->p($escaped_string);
print $query->code($fileurl);
print $query->br();
#$escaped_string = escapeHTML($filetype);
#print $query->p($escaped_string);
print $query->p($filetype);
print $query->hr();

#$doc = escapeHTML($doc);
print $query->pre($doc);

#my $s = Net::HTTP->new(Host => "") || die $@;
#$s->write_request(GET, $fileurl, 'User-Agent' => "Mozilla/5.0");
#my($code, $mess, %h) = $s->read_response_headers;

#while(1)
#{
#    my $buf;
#    my $n = $s->read_entity_body($buf, 1024);
#    last unless $n;
#    print $query->code($buf);
#}

print $query->end_html();




