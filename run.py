#!/usr/bin/python3




# ---- IMPORTATIONS ----

#add library path
import sys
sys.path.append("lib")

#dreamlands
import dreamlands as dl

#json (for more readable output only)
import json






# ---- UTILITIES ----

#print data structures prettier
def prettyPrint(data):
	print(json.dumps(data, indent=4))






# ---- EXECUTION ----

# EXAMPLE 1 : READING

#read a dreamlands file
data = dl.read("example/data_to_read1.dl")

#display parsed content
print("EXAMPLE 1 : Data read from \'example/data_to_read1.dl\' : ")
prettyPrint(data)
print()




# EXAMPLE 2 : WRITING

#create a new data structure
new_data_dict = {

	#small devices
	'smartphone':{
        #         in mAh
		'battery':4000.0,
		'OS'     :"Android"
	},

	#big devices
	'computer':{
		'name':"Nitro",
		'CPU':{
			'brand'   :"AMD",
			'core_nbr':16
		},
		'GPU':{
			'brand':"Nvidia",
            #       100 000 000 000 = 100G
			'flops':100000000000,
			'compatibility':[
				"GNU/Linux",
				"Windows",
				"MacOSX"
			]
		},

		#users
		'users':[

			#users information (as 1st element of list)
			{
				'users_nbr':2,
				'titles'   :[ "First Name", "Last Name", "Sex", "Age", "Size" ]
			},

			#undefined user
			False,

  			#users                 (sex)          (in cm)
			#1st name,  last name, is male?, age, size
			['Michel', 'GARLIC',   True,     17,  1.77],
			['Andrea', 'PARMESAN', False,    28,  1.61]
		]
	}
}

#get dreamlands text equivalent
print("EXAMPLE 2 : Writing :")
prettyPrint(new_data_dict)
print("in file 'example/data_to_write.dl'.")

#write into a dreamlands file
dl.write(new_data_dict, "example/data_to_write.dl")
