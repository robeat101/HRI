
#!/usr/bin/perl -i
use strict;
use warnings;


#Extracted Parameters

my $nRobots=0;
my @lastBrokenTimes;	#list of times the robots were last broken before being fixed
my @robotIsBroken;	#keep track of what robots are broken
my @brokenTimes;
my @fixedTimes;
my @cellsToGoWhenFixed;
my @manaAtFix;
my $nInstances = 0;

my $outputFilename = "parsed.csv";

$^I = '.bak';	#save a backup file to appease Windows
while(<>){	#loop through lines of file	
	if (/INIT/){
		if (/Robot world initialized with (\d+) robots/){
			$nRobots = $1;
			@lastBrokenTimes = (0) x $nRobots;
			@robotIsBroken = (0) x $nRobots;
						@brokenTimes = () x $nRobots;
			@fixedTimes = () x $nRobots;
			@cellsToGoWhenFixed = () x $nRobots;
			@manaAtFix = ()x $nRobots;		}	}
			if (/(\d+): Robot (\d+) broke/){
		my $robotID = $2;
		if ($robotIsBroken[$robotID] == 0){
			@robotIsBroken[$robotID] = 1;
			@lastBrokenTimes[$robotID] = $1;
		}else{
			next;		}	}
	if (/(\d+): Robot (\d+) fixed after \d+ with (\d+) cells in path to go. Mana: (\d+.?\d*)/){
		my $time = $1;
		my $robotID = $2;
		my $cellsToGo = $3;
		my $mana = $4;
		
		$nInstances+=1;
		
		push(@{$brokenTimes[$robotID]}, @lastBrokenTimes[$robotID]);
		push(@{$fixedTimes[$robotID]}, $time);
		push(@{$cellsToGoWhenFixed[$robotID]}, $cellsToGo);
		push(@{$manaAtFix[$robotID]}, $mana);		#print @lastBrokenTimes[$robotID].", ".$time.", ".$robotID.", ".$cellsToGo.", ".$mana."\n";

		@lastBrokenTimes[$robotID] = 0;
		@robotIsBroken[$robotID] = 0;
	}
	

	print or die $!;
}

open(my $fh, '>', $outputFilename) or die $!;
for (my $id=0;$id<$nRobots;$id++){	
	print $fh "\n\nROBOT ".$id."\nBrokenTimes, FixedTimes, CellsToGo, Mana\n";
	for (my $i=0;$i<@{$brokenTimes[$id]};$i++){
		print $fh @{$brokenTimes[$id]}[$i].",".@{$fixedTimes[$id]}[$i].",".@{$cellsToGoWhenFixed[$id]}[$i].",".@{$manaAtFix[$id]}[$i]."\n";
	}
}
close($fh);