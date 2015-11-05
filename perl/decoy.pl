#!/usr/bin/perl
#
# $Id: decoy.pl 61 2012-12-17 12:35:21Z gorka.prieto@gmail.com $
#

# Based on FastaTools version 0.9 - Copyright (C) Copyright 2008-2012 
#                                   Joaquín Abian and David Ovelleiro at 
#                                   Laboratori de Proteomica CSIC/UAB 
#                                   <lp.csic@uab.cat> http://proteomica.uab.cat
# Decoy.pl version 0.1 - Copyright (C) 2012  Gorka Prieto

# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.

# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>


#################################################################################################
#################################################################################################
##########################                Main                      #############################

use strict;

my $num_args = $#ARGV + 1;
my @decoy_types=("Reverse","Random","PseudoReverse Trypsin (KR|P)","PseudoReverse Trypsin (KR)","PseudoReverse GluC (DE)","PseudoReverse GluC (E)","PseudoReverse Endolysin (K)");
my $i;

if ($num_args != 5) {
    usage();
    die "\nIncorrect use\n";
}

decoy($decoy_types[$ARGV[0]], $ARGV[1], $ARGV[2], $ARGV[3], $ARGV[4]);

sub usage {
    print "Usage:\n\tdecoy.pl <decoy-type> <in.fasta> <out.fasta> <append> <prefix>\n";
    print "\nWhere decoy-type is a number according to:\n";
    for ($i=0; $i<=$#decoy_types; $i++) {
        print "\t$i. $decoy_types[$i]\n"
    }
}

#################################################################################################
#################################################################################################
##########################        Decoy functionality               #############################
sub decoy{
    my ($decoy,$out_file1,$out_file2,$append_original_file,$prefix) = @_;
    use File::Copy;
    my $out_file2_short= $out_file2;
    
    #Generation of the complete paths
    my $out_dir=dir_from_file($out_file1);
    die "Error while creating the out file\n" unless($out_dir);
    $out_file2=$out_dir.$out_file2;
    #Fasta file Test to main fasta
    my $lines_predicted= test_file_Fasta($out_file1);
    die "Incorrect Fasta file\n" unless($lines_predicted);
    open FASTA,"$out_file1";
    #Generation of the physical file anf filehandle if 'append' option, or only opening the filehanle if not    
    if ($append_original_file){#if append
        die "Unable to create the out file.\nPlease, check the \'Out file\' name\n" unless(copy( $out_file1, $out_file2 ));
        open FILE_IN,">>$out_file2";
    }
    else{#if not append
        die "Unable to create the out file.\nPlease, check the \'Out file\' name\n" unless(open FILE_IN,">$out_file2");
    }                
    #Treatment of the process arguments
    my $treatment;
    my $enzyme_regex='';
    if($decoy eq 'Reverse'){
        $enzyme_regex='Reverse';
        $treatment=\&reverse_seq;
    }
    elsif($decoy eq 'Random'){
        $enzyme_regex='Random';
        $treatment=\&alea_seq;
    }
    elsif($decoy eq 'PseudoReverse Trypsin (KR|P)'){
        $enzyme_regex='(?<=[KR])(?=[^P])';
        $treatment=\&pseudoreverse_trypsinKRP;
    }
    else{
        if($decoy=~/^PseudoReverse/){
            $treatment=\&pseudoreverse;
                if($decoy eq 'PseudoReverse Trypsin (KR)'){$enzyme_regex='(?<=[KR])';}
                elsif($decoy eq 'PseudoReverse GluC (DE)'){$enzyme_regex='(?<=[DE])';}
                elsif($decoy eq 'PseudoReverse GluC (E)'){$enzyme_regex='(?<=[E])';}
                elsif($decoy eq 'PseudoReverse Endolysin (K)'){$enzyme_regex='(?<=[K])';}    
        }
    }    

    print "Starting Decoy DB generation: $decoy\n";
    
    ##########    Create progress milestones    
    my @ten_line_numbers;
    for(my $i=1;$i<101;$i++){
        push @ten_line_numbers,(($lines_predicted/100)*$i);
    }
    ##########
    
    while(<FASTA>){
        if($_=~/^\>/){
            last;
        }
    }
    
    my $line=0;    
    my $sec="";#
    my $prot=1;#for prot number
    my $progress_num=1;
    my $length_seq;
    while(<FASTA>){
        $line++;
        if ($_=~/^\>/){
            #print FILE_IN '>decoy_'."$prot\n";
            print FILE_IN get_head($prot,$prefix,$decoy);
            $prot++;
            #### Progress
            if ($progress_num<99){
                if($line>$ten_line_numbers[$progress_num]){
                    $progress_num++;
                    #print 'Progress: '.$progress_num.' %';
                }
            }
            elsif($progress_num==99){
                $progress_num++;
                #print 'Progress: Writting to disk.';
            }

            #Reverse sequence
            my $decoy_sequence= &{$treatment}($sec,$enzyme_regex);
            #Format sequence in 60 letters/line
            $length_seq=(length($decoy_sequence))/60;
            for (my $k=0;$k<$length_seq;$k++){
                print FILE_IN (substr $decoy_sequence,$k*60,60);
                print FILE_IN "\n";
            }  
            
            #temporary sequence empty
            $sec="";
        }
        else{
            $_=~s/\W//g;
            $sec.=$_;
        }
    }
##########    LAST SEQUENCE PROCESS
    #Reverse sequence
    #print FILE_IN '>decoy_'."$prot\n";
    print FILE_IN get_head($prot,$prefix,$decoy);
    my $decoy_sequence= &{$treatment}($sec,$enzyme_regex);
    #Format sequence in 60 letters/line
    $length_seq=(length($decoy_sequence))/60;
    for (my $k=0;$k<$length_seq;$k++){
        print FILE_IN (substr $decoy_sequence,$k*60,60);
        print FILE_IN "\n";
    }        
    $sec="";            
##########    END OF LAST SEQUENCE PROCESS
    
    close FASTA;        
    close FILE_IN;
    print "The decoy database \'$out_file2_short\' has been successfully generated"."\n";
    
    return;
}

sub get_head {
    my ($prot,$prefix,$decoy) = @_;
    return ">$prefix|decoy-$prot|decoy-$prot Decoy $decoy\n";
}

sub reverse_seq{
    my $secuencia= $_[0];
    $secuencia=reverse $secuencia;
    return $secuencia;
    }
sub alea_seq{
    my $secuencia= $_[0];
    my @trozos= split //, $secuencia;
    my $secuencia_rand=""; 
    
    my @TempList = ();
    while(@trozos){
        push(@TempList, splice(@trozos, rand(@trozos), 1)) 
    }
    @trozos = @TempList;
    for (@trozos){
        $secuencia_rand=$secuencia_rand.$_;
    }   
    return $secuencia_rand;
    }
sub pseudoreverse_trypsinKRP{
        my ($seq,$enzyme_regex)= @_;
        my $protein_pseudorev="";
        my @peptides=split(qr/$enzyme_regex/, $seq);
        ################################    Process of First peptide
        my $first_pep=$peptides[0];
        if((length $first_pep) >2 ){
            $first_pep=~s/KP/1/g;
            $first_pep=~s/RP/2/g;
            my $last_aa= substr $first_pep,-1,1,'';
            $first_pep= reverse $first_pep;
            $first_pep.=$last_aa;
            $first_pep=~s/1/KP/g;
            $first_pep=~s/2/RP/g;
        }
        $protein_pseudorev=$first_pep;
        ################################     Process of the second, 
        #third,.. (n-1) peptides, only if there are more than 2 peptides
        if($#peptides>1){
            for(my $i=1;$i<$#peptides;$i++){
                if((length $peptides[$i]) <3){
                }
                else{
                    my $last_aa= substr $peptides[$i],-1,1,'';
                    $peptides[$i]=~s/KP/1/g;
                    $peptides[$i]=~s/RP/2/g;
                    if($peptides[$i]=~/P$/){
                        if((length $peptides[$i]) >1){
                            $peptides[$i]=~s/([^P])(.*)(.)$/$2$3$1/;
                            $peptides[$i]=reverse $peptides[$i];
                        }
                    }
                    else{
                        $peptides[$i]= reverse $peptides[$i];
                    }
                    $peptides[$i]=~s/1/KP/g;
                    $peptides[$i]=~s/2/RP/g;    
                    #$peptides[$i]=~s/PK/KP/g;
                    #$peptides[$i]=~s/PR/RP/g;    
                    $peptides[$i].=$last_aa;                
                }
                $protein_pseudorev=$protein_pseudorev.$peptides[$i];
            }
        }
        ################################    Process last peptide, only if 
        # there are at least two peptides    
        if($#peptides>0){
            my $last_pep=$peptides[$#peptides];
            $last_pep=~s/KP/1/g;
            $last_pep=~s/RP/2/g;
            if($last_pep=~/[KRP]$/){
                    if((length $last_pep) >1){
                        $last_pep=~s/([KRP]+)$//;
                        my $tail=$1;
                        $last_pep=reverse $last_pep;
                        $last_pep=$last_pep.$tail;
                    }
            }
            else{
                $last_pep=reverse $last_pep;
            }
            $last_pep=~s/1/KP/g;
            $last_pep=~s/2/RP/g;
            $protein_pseudorev=$protein_pseudorev.$last_pep;
        }
    return $protein_pseudorev;
}
sub pseudoreverse{
    my ($seq,$enzyme_regex)= @_;
    my $protein_pseudorev;
    my @peptides=split(qr/$enzyme_regex/, $seq);
    foreach my $pep(@peptides){
        my @aas= split //,$pep;
        my $aa_last= pop @aas;
        my $aas=join ('',@aas);
        $aas= reverse $aas;
        $protein_pseudorev.=$aas.$aa_last;
        }
    return $protein_pseudorev;
}

#########################################################################################################
#########################################################################################################
##########################    Modules used in all functionalities   #####################################
sub test_file_Fasta{
    #Tests if a string is a text file, and returns a prediction of fasta
    #file size according to the empirical formula lines_Fasta=(Size_in_Mb + 1.7858)/0.00007
    my $test=shift;
    if(-T $test){
        #Test if the first or second line contains a > simbol at the beginning of line
        open TEST,"<$test";            
        my $count_lines=0;
        my $test_fasta_simbol=0;
        my $test_fasta_header=0;
        my $test_sequence=0;
        while(<TEST>){
            $count_lines++;
            if($_=~/^>/){
                $test_fasta_simbol=1;
                my @header_parts= split />/,$_;
                if($header_parts[1]){
                    $test_fasta_header=1;    
                }
            }
            else{#Verify the existence of a sequence
                if($_=~/\w/){
                    $test_sequence=1;    
                }    
            }
        }

        close TEST;
        unless($test_fasta_simbol and $test_fasta_header and $test_sequence){
            return;    
        }
        return $count_lines;
    }
}

sub test_file_Fasta2{
    #Tests if a string is a text file, and returns a prediction of fasta
    #file size according to the empirical formula lines_Fasta=(Size_in_Mb + 1.7858)/0.00007
    my $test=shift;
    if(-T $test){
        #Test if the first or second line contains a > symbol at the beginning of line
        open TEST,"<$test";            
        my $count_lines=0;
        my $test_fasta_simbol=0;
        my $test_fasta_header=0;
        my $test_sequence=0;
        while(<TEST>){
            $count_lines++;
            if($_=~/^>/){
                $test_fasta_simbol=1;
                my @header_parts= split />/,$_;
                if($header_parts[1]){
                    $test_fasta_header=1;    
                }
            }
            else{#Verify the existence of a sequence
                if($_=~/\w/){
                    $test_sequence=1;    
                }    
            }
            if($count_lines>100){
                last;
            }
        }
        close TEST;

        unless($test_fasta_simbol and $test_fasta_header and $test_sequence){
            return;    
        }    
        #predict de number of lines of Fasta file
        my $filesize = -s $test;
        $filesize=$filesize/1000000;
        my $lines_calc =($filesize+1.7858)/0.00007;
        $lines_calc= sprintf ("%.0f",$lines_calc);
        return $lines_calc;
    }
}


sub dir_from_file{
    my $file= shift;
    my @parts_file1= split /\\/,$file;#win32
    my @parts_file2= split /\//,$file;#linux
    pop @parts_file1;
    pop @parts_file2;
    my ($possible_dir1,$possible_dir2);
    $possible_dir1= join '\\',@parts_file1;#win32
    $possible_dir2= join '/',@parts_file2;#linux
    $possible_dir1.='\\';
    $possible_dir2.='/';
    if(-d $possible_dir1){#win32
        return $possible_dir1;
        }
    elsif(-d $possible_dir2){#linux
        return $possible_dir2;
        }
    else{
        return;
        }
    
}

sub format_commas{
    my $num=$_[0];
    my $i=0;
    while ($i<5){#This loop while generate a maximum of 5 commas (10^15)
        $num=~s/(.*)(\d)(\d\d\d)/$1$2,$3/;
        $i++;
        }
    return $num;
}
