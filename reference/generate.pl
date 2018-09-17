#!/usr/bin/perl -w

use Getopt::Long;
use File::Basename;
use strict;
use warnings;

my $prog = basename($0);
my $debug = 0;
my $compact = 0;

my $usage = "
  Program to generate Java snippets from the Google Anayltics Measurement
  Protocol parameter reference.

  The reference should be of the format:
  key : name : description : required : type : length : supported : example

Usage:

  $prog input [...]

Options:

  input     The reference

  -help     Print this help and exit

  -debug    Debug info

  -compact  Output the compact version

";

my $help;
GetOptions(
	"help" => \$help,
	"debug" => \$debug,
  "compact" => \$compact,
);

die $usage if $help;

my $input = shift or die $usage;

open (IN, $input) or die "Failed to open '$input': $!\n";
my $header = readline(IN);
my @data;
chomp(@data = <IN>);
close IN;

# Tags for the Eclipse code formatter
my $off = '@formatter:off';
my $on = '@formatter:off';
my $delim = ' : ';

################################################################################
open (OUT, ">ProtocolSpecification.snippet.java") or die $!;
print OUT "  // $off\n";
my %done;
my $n=0;
$"=', ';
for (@data) {
    if (m/^#Section (.*)/) {
        print OUT "\n" if $n++;
        print OUT "  /////////////////////////\n";
        print OUT "  // $1\n";
        print OUT "  /////////////////////////\n";
    } else {
        my ($key,$name,$description,
            $required,$type,
            $length,$supported,$example) = split /$delim/, $_;
            # Avoid duplicates, e.g. Transaction ID
            next if $done{$key}++;
            # Convert to upper-case
            my $upper = $name;
            # Special case for Tracking ID / Web Property ID
            if ($key eq 'tid') {
                $upper = 'Tracking ID';
            }
            $upper =~ tr/a-z \-/A-Z__/;
            # Remove bad characters, e.g.: Is Exception Fatal?
            $upper =~ s/\?//;
            my $upperType = uc($type);
            my $supp = '';
            if ($supported ne 'all') {
                for my $s (split /,/, $supported) {
                    $s =~ s/ //;
                    $s = uc($s);
                    $supp .= ", HitType.$s";
                }
            }
            # Compact version
            if ($compact) {
                print OUT <<__END
  \/\*\* \@see <a href= "http:\/\/goo.gl\/a8d4RP#$key">$name<\/a> \*\/
  $upper("$name", "$key", ValueType.$upperType, $length$supp),
__END
            } else {
                unless ($description =~ m/\.$/) {
                    $description .= '.';
                }
                my $len = ($length) ? " (Max Length = $length)" : '';
                print OUT <<__END
  \/\*\*
   \* $name ($key).
   \*
   \* <p>$required
   \*
   \* <p>$description
   \*
   \* <p>Value Type: $type$len.
   \*
   \* <p>Supported Hit Types: $supported.
   \*
   \* <p>Example usage: $example.
   \*
   \* \@see <a href= "http:\/\/goo.gl\/a8d4RP#$key">$name<\/a>
   \*\/
  $upper("$name", "$key", ValueType.$upperType, $length$supp),
__END
            }
    }
}
print OUT "  // $on\n";
close OUT;

################################################################################
