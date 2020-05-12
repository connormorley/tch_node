# tch_node
Attack node component of TCruch - TrueCrypt detection and distributed attack system

# TCrunch
This was devised as a method to more accurately detect TrueCrypt containers on a host than the tools already available as well as provide an efficient distributed attack system in order to maximize available CPU power.

The detection system works on a combination of the Chi-Square and Monte Carlo Pi tests, the results being far more accurate than other detection systems.

The attack system is a dynamic heterogeneous distributed attack structure allowing for the addition and removal of attacker machines at will without loss or degradation of the attack process.

# Component purpose
This is the attack node part of the system which will actively try and crack the password of the file fragment provided to it by the server. Currently this works by deploying a portable instance of the truecrypt executable in order to attempt access through its process. I am aware this is not the most efficient way, but i was short on time to go another route. 

The system will extrapolate the designate password range from the server to determine the permutations to attempt on the file before asking for the next sequence. The host currently only works on CPU, there is scope to integrate CUDA GPU system processing but i doubt i will come back to this. 

On startup, the node will attempt a tiny password crack sequence to determine a baseline for password throughput. This is then relayed to the server in order to determine the heteregenous values the global system will work by. 

The file fragment being used to try and crack the password can be as small as 300KB even if the original container was 1TB in size. 
