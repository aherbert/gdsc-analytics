$on = 0;

$missing = "\n\nMISSING\n\n";

my ($key,$name,$description,
$required,$type,$length,$supported,$example);
my $td = 0;

sub reset() {
  $key = $missing;
  $name = $missing;
  $description = $missing;
  $required = $missing;
  # From the table <td> tags
  $type = $missing;
  $default = $missing;
  $length = 0;
  $supported = 'all';
  # Get the example
  $example = $missing;
}

  while (<>) {
    # start at <body>
    if (m/<body>/) {
      $on = 1;
    }
    next unless $on;

    # Section headers
    # <h2 id="general">General</h2>
    if (m/([^>]+)<\/h2/) {
      print "#Section : $1\n";
      $section = $1;
      next;
    }

    # Parameter name
    # href="#v">Protocol Version</a>
    if (m/href="#([^"]+)">([^<]+)<\/a><\/h3>/) {
      printf ("%s : %s : %s : %s : %s : %d : %s : %s\n",
        $key, $name, $description,
        $required, $type, $length,
        $supported, $example) if $key;
      reset();
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
