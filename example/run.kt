//package example






// ---- IMPORTATIONS ----

//add library path
//import dreamlands.Dreamlands






// ---- UTILITIES ----

//print data structures prettier
fun prettyPrint(data:Any?, depth:Int=0) {
	val indent = '\t'*depth

	//maps
	if(data is MutableMap<*,*>){
		println('{')
		val m = (data as MutableMap<String,Any?>)
		for(e in m.keys){
			print(indent + "\t\"" + e + "\":")
			prettyPrint(m[e], depth+1)
			println(',')
		}
		println(indent + '}')

	//lists
	}else if(data is MutableList<*>){
		val l = (data as MutableList<Any?>)
		println('[')
		for(e in l){
			print(indent + '\t')
			prettyPrint(e, depth+1)
			println(',')
		}
		println(indent + ']')

	//other non null
	}else if(data != null){
		when(data){
			is Boolean -> print((data as Boolean).toString())
			is Int     -> print((data as Int).toString())
			is Double  -> print((data as Double).toString())
			is Char    -> print("'" + (data as Char) + "'")
			is String  -> print("\"" + (data as String) + "\"")
		}

	//null
	}else{
		print("(NULL ELEMENT)")
	}
}






// ---- EXECUTION ----

//main
fun main() {

	// EXAMPLE 1 : READING

	//read a dreamlands file
	var data = Dreamlands.read("example/vehicles1.dl")

	//display parsed content
	println("EXAMPLE 1 : Data read from \'vehicles1.dl\' : ")
	prettyPrint(data)
	println()




	// EXAMPLE 2 : WRITING

	//create a new data structure
	var new_data_dict = mutableMapOf(

		//small devices
		"smartphone" to mutableMapOf<String,Any?>(
	        //         in mAh
			"battery" to 4000.0,
			"OS"      to "Android"
		),

		//big devices
		"computer" to mutableMapOf<String,Any?>(
			"name" to "Nitro",
			"CPU"  to mutableMapOf<String,Any?>(
				"brand"    to "AMD",
				"core_nbr" to 16
			),
			"GPU" to mutableMapOf<String,Any?>(
				"brand" to "Nvidia",
	            //         100 000 000 000 = 100G
				"flops" to 100000000000,
				"compatibility" to mutableListOf<Any?>(
					"GNU/Linux",
					"Windows",
					"MacOSX"
				)
			),

			//users
			"users" to mutableListOf<Any?>(

				//users information (as 1st element of list)
				mutableMapOf<String,Any?>(
					"users_nbr" to 2,
					"titles"    to mutableListOf<Any?>("First Name", "Last Name", "Sex", "Age", "Size")
				),

				//undefined user
				false,

	  			//users                 (sex)          (in cm)
				//1st name,  last name, is male?, age, size
				mutableListOf<Any?>("Michel", "GARLIC",   true,     17,  1.77),
				mutableListOf<Any?>("Andrea", "PARMESAN", false,    28,  1.61)
			)
		)
	)

	//get dreamlands text equivalent
	println("EXAMPLE 2 : Writing :")
	prettyPrint(new_data_dict)
	println("in file 'devices.dl'.")

	//write into a dreamlands file
	Dreamlands.write(new_data_dict, "example/devices.dl")
}
