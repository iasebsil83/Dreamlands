#!/usr/bin/python3



# ---- IMPORTATIONS ----

#add library path
import sys
sys.path.append("lib")

#dreamlands
import dreamlands as dl






# ---- MAIN ----

#read a dreamlands file
data = dl.read("example/data_to_read1.dl")

#display
print("Data read from \'example/data_to_read1.saml\' : ", end='')
print(data, "\n\n")

#write a new dreamlands file
data_dict = {
	'smartphone':{
#        battery : in mAh
		'battery':4000.0,
		'OS':"Android"
	},
	'computer':{
		'name':"Nitro",
		'CPU':{
			'brand':"AMD",
			'core_nbr':16
		},
		'GPU':{
			'brand':"Nvidia",
			'fps':60.0
		}
	}
}
print("Writing : ", end='')
print(data_dict, end='')
print(" in file 'example/data_to_write.dl'.")

#write into a dreamlands file
dl.write(data_dict, "example/data_to_write.dl")
