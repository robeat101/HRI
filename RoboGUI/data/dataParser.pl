
#!/usr/bin/perl -i
use strict;
use warnings;


#Extracted Slicer Parameters
my @lastBrokenTimes;	#list of times the robots were last broken before being fixed
my @robotIsBroken;	#keep track of what robots are broken

#my $outputFilename = $_+'_parsed';
#open(FH, '<', $filename) or die $!;
$^I = '.bak';	#save a backup file to appease Windows
while(<>){	#loop through lines of file	
	if (/INIT/){
		if (/Robot world initialized with (\d+) robots/){
			@lastBrokenTimes = (0) x $1;
			@robotIsBroken = (0) x $1;		}	}
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
		
				print @lastBrokenTimes[$robotID].", ".$time.", ".$robotID.", ".$cellsToGo.", ".$mana."\n";

		@lastBrokenTimes[$robotID] = 0;
		@robotIsBroken[$robotID] = 0;
	}
	#print or die $!;
}