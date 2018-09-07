#!/usr/bin/perl -w

use Getopt::Long;
use File::Basename;
use strict;
use warnings;

my $prog = basename($0);
my $debug = 0;

my $usage = "
  Program to scrape the Google Anayltics Measurement Protocol from a 
  the parameter reference HTML file:
  
  https://developers.google.com/analytics/devguides/collection/protocol/v1/parameters

Usage:

  $prog input [...]

Options:

  input     [HTML]

  -help     Print this help and exit

  -debug    Debug info

";

my $help;
GetOptions(
        "help" => \$help,
        "debug" => \$debug,
);

die $usage if $help;

my $input = shift or die $usage;

# Store all the required fields for each parameter.
# Ignore the default value.
my ($key,$name,$description,
    $required,$type,
    $length,$supported,$example);
my $td = 0;

# Used to mark missing parameters
my $missing = "\n\nMISSING\n\n";

# Reset all the fields
sub resetFields() {
    $key = $missing;
    $name = $missing;
    $description = $missing;
    $required = $missing;
    # From the table <td> tags
    $type = $missing;
    $length = 0;
    $supported = 'all';
    # Get the example
    $example = $missing;
}

# Turn on when we reach the <body> tag
my $on = 0;
# The section
my $section = '';

printf("%s : %s : %s : %s : %s : %s : %s : %s\n",
    'key', 'name', 'description',
    'required', 'type', 
    'length', 'supported', 'example');

open (IN, $input) or die "Failed to open '$input': $!\n";
while (<IN>)
{
    # start at <body>
    if (m/<body>/) {
        $on = 1;
    }
    next unless $on;

    # Section headers
    # <h2 id="general">General</h2>
    if (m/([^>]+)<\/h2/) {
        print "#Section $1\n";
        $section = $1;
        next;
    }

    # Parameter name
    # href="#v">Protocol Version</a>
    if (m/href="#([^"]+)">([^<]+)<\/a><\/h3>/) {
        if ($key) {
            printf("%s : %s : %s : %s : %s : %d : %s : %s\n",
                $key, $name, $description,
                $required, $type, 
                $length, $supported, $example)
        }
        resetFields();
        $td = 0;
        #print "\nParameter = $1 : $2\n";
        $key = $1;
        $name = $2;
        next;
    }

    # Optional.
    if (m/(Optional.)$/) {
        #print "$1\n";
        $required = $1;
        next;
    }

    # Required for all hit types.
    if (m/(Required for [^<]*)/) {
        #print "$1\n";
        $required = $1;
        next;
    }

    # Description all on one line
    # <p>Specifies the character set used to encode the page / document.</p>
    if (m/<p>(.*)<\/p>$/) {
        #print "$1\n";
        $description = $1;
        next;
    }

    # Parameter definitions:
    # Parameter : Value Type : Default Value : Max Length : Supported Hit Types
    #<td><code>v</code></td>
    #<td>text</td>
    #<td><span class="none">None</span>
    #    </td>
    #<td><span class="none">None</span>
    #    </td>
    #<td>all</td>
    if (m/<td>/) {
        $td++;
        if ($td == 1) {
            # Have this already
            next;
        }
        if (m/<td>(\d+) Bytes/) {
            #print "$1 Bytes\n";
            $length = $1;
            next;
        }
        if (m/<td>.*>(None)</) {
            # Default to none anyway
            next;
        }
        if (m/<td>(.*)<\/td>/) {
            if ($td == 2) {
            $type = $1;
            }
            elsif ($td == 5) {
            $supported = $1;
            }
            next;
        }
        next;
    }

    # Example usage: <code>de=UTF-8</code>
    if (m/Example usage: <code>(.*)</) {
        #print "$1\n";
        $example = $1;
        next;
    }
}
close IN;

