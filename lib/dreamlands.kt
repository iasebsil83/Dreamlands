package dreamlands



// .-------------- DREAMLANDS Kotlin Library [0.1.0] -------------.
// |                                                              |
// |     This library is the official parser tool for DREAMLANDS  |
// | files. It includes 4 functions :                             |
// |   - Dreamlands.fromText() //get data from raw text           |
// |   - Dreamlands.toText()   //get raw text from data           |
// |   - Dreamlands.read()     //get data from file               |
// |   - Dreamlands.write()    //write data into file             |
// |                                                              |
// |     For more information about this syntax, please check     |
// | out the main repository for documentation:                   |
// |                                                              |
// |           https://github.com/iasebsil83/DREAMLANDS           |
// |                                                              |
// | Let's Code !                                         By I.A. |
// |                                                              |
// |*********************************************************************|
// |  LICENSE :                                                          |
// |                                                                     |
// |  DREAMLANDS_Kotlin                                                  |
// |  Copyright (C) 2024 Sebastien SILVANO                               |
// |                                                                     |
// |  This library is free software; you can redistribute it and/or      |
// |  modify it under the terms of the GNU Lesser General Public         |
// |  License as published by the Free Software Foundation; either       |
// |  version 2.1 of the License, or (at your option) any later version. |
// |                                                                     |
// |  This library is distributed in the hope that it will be useful,    |
// |  but WITHOUT ANY WARRANTY; without even the implied warranty of     |
// |  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  |
// |  Lesser General Public License for more details.                    |
// |                                                                     |
// |  You should have received a copy of the GNU Lesser General Public   |
// |  License along with this library.                                   |
// |                                                                     |
// |  If not, see <https://www.gnu.org/licenses/>.                       |
// '---------------------------------------------------------------------'

//importations
import java.io.File




//generic shortcut tool
public operator fun Char.times(count:Int) : String {
	return this.toString().repeat(count)
}



//class definition
class Dreamlands {

	companion object {




		// ---- OPTIONS ----

		//allow additional spaces
		const val ADDITIONAL_SPACES_ALLOWED = true

		//debug mode
		const val DEBUG_MODE = false

		//allow external files importation (if False, importations will be skipped)
		const val EXTERNAL_IMPORTATIONS_ALLOWED = true

		//python character optimization (if True, python strings of 1 character long will be interpreted as characters)
		const val PYTHON_CHARACTERS_OPTIMIZATION = true // This is only applicable in function toText()






		// ---- CONSTANTS ----

		//blanks : DO NOT MODIFY
		val BLANKS = listOf(' ', '\t')

		//special characters
		const val COMMENT_CHARACTER    = '#'
		const val LINE_END_CHARACTER   = '\n'
		const val NEW_FILE_CHARACTER   = '>'
		const val SEPARATION_CHARACTER = ':'
		const val TABULATION_CHARACTER = '\t'

		//keys
		val KEYS_CHARACTERS_ALLOWED = ('a'..'z') + ('A'..'Z') + '_' + ('0'..'9')

		//escaped characters (text elements)
		val ESCAPED_CHARACTERS_MAP = mapOf(
			'a'  to (0x07 as Char),
			'b'  to '\b',
			'e'  to (0x1b as Char),
			'f'  to (0x0c as Char),
			'n'  to '\n',
			'r'  to '\r',
			't'  to '\t',
			'v'  to (0x0b as Char),
			'\\' to '\\',
			'\'' to '\'',
			'\"' to '\"'
		)

		//internal items
		private class InstructItem(
			givenLineNbr:UInt   = 0u,
			givenColmNbr:UInt   = 0u,
			givenValue  :Any?   = null,
			givenKey    :String = "",
			givenDepth  :Int    = 0
		) {
			//fields
			var line_nbr :UInt   = givenLineNbr
			var colm_nbr :UInt   = givenColmNbr
			var value    :Any?   = givenValue
			var key      :String = givenKey
			var depth    :Int    = givenDepth

			//debug output only
			override fun toString() : String {
				return "(" +
					this.line_nbr.toString() + ", " +
					this.colm_nbr.toString() + ", " +
					(this.value as String)   + ", " +
					this.key                 + ", " +
					this.depth.toString()    + ", " +
					")"
			}
		}

		private class RawInstructItem(
			givenLineNbr:UInt   = 0u,
			givenColmNbr:UInt   = 0u,
			givenRawText:String = "",
			givenChrText:String = "",
			givenStrText:String = ""
		){
			//fields
			var line_nbr :UInt   = givenLineNbr
			var colm_nbr :UInt   = givenColmNbr
			var raw_text :String = givenRawText
			var chr_text :String = givenChrText
			var str_text :String = givenStrText

			//debug output only
			override fun toString() : String {
				return "(" +
					this.line_nbr.toString() + ", " +
					this.colm_nbr.toString() + ", " +
					this.raw_text            + ", " +
					this.chr_text            + ", " +
					this.str_text            + ", " +
					")"
			}
		}






		// ---- GLOBAL DECLARATIONS ----

		//current index of instructs during unparsing
		var instructsIndex = 0






		// ---- DATA <-> TEXT ----

		//error shortcuts
		private fun checkChrLength(line_nbr:UInt, colm_nbr:UInt, current_buffer:String) {
			if(current_buffer.length != 0){
				throw RuntimeException(
					"Only one character is allowed in character declaration (line " +
					line_nbr.toString() + " column "                                +
					colm_nbr.toString() + ")."
				)
			}
		}

		private fun invalidEscChr(line_nbr:UInt, colm_nbr:UInt, chr:Char) {
			throw RuntimeException(
				"Invalid escaped character found with value " + chr.toInt().toString() + ". (line " +
				line_nbr.toString() + " column " +
				colm_nbr.toString() + ")."
			)
		}



		//data <- from text
		public fun fromText(text:String) : Any? {

			//get instruction list of current text
			var instructs : MutableList<InstructItem> = Dreamlands.textToInstructs(text)

			//complete instruction set with external importations
			var i = 0
			var importedFiles = mutableListOf<String>()
			while(i < instructs.size){
				if(Dreamlands.DEBUG_MODE){
					println("[DEBUG] Completing instructs : [" + i.toString() + "] = " + instructs[i].toString() + " len " + instructs.size.toString())
				}

				//importation detected => add it to current instructs
				if(instructs[i].depth < 0){

					//anti-recursivity check
					if((instructs[i].value as String) in importedFiles){
						throw RuntimeException("Recursive importation detected : file '" + (instructs[i].value as String) + "' already imported once.")
					}
					importedFiles.add(instructs[i].value as String)

					//read external file
					val extIns = Dreamlands.textToInstructs( File(instructs[i].value as String).readText() )

					//for each imported instruction
					for(ei in 0 until extIns.size){
						instructs.add(i+ei-1, extIns[ei])
					}
					instructs.removeAt(i+extIns.size) //remove the importation instruction

				//increment (only on non-import instruction)
				} else { i++ }
			}

			//no data
			if(instructs.size == 0){
				return null
			}

			//check first element
			if(instructs.last().depth != 0){
				throw RuntimeException("Incorrect indent for first element : zero-depth is required.")
			}

			//check last element parentality
			if(instructs.last().value == null){
				throw RuntimeException("Incorrect value for last element : child element required.")
			}

			//debug
			if(Dreamlands.DEBUG_MODE){
				println("[DEBUG] Completed instructs = [")
				for(i in instructs){
					println("\t" + i.toString() + ",")
				}
				println("]")
			}

			//finally translate instructions into data
			Dreamlands.instructsIndex = 0
			return Dreamlands.instructsToData(instructs)
		}


		//parsing
		private fun textToInstructs(text:String) : MutableList<InstructItem> {

			// STEP 1 : SEPARATE INSTRUCTIONS

			//some temporary constants (indexes inside raw_instructs list)

			//field detection
			var inComment = false
			var inChr     = false
			var inStr     = false
			var escaping  = false

			//just separate instructions
			var raw_instructs = mutableListOf<RawInstructItem>( RawInstructItem(1u, 1u) )
			var line_nbr = 1u
			var colm_nbr = 0u
			for(c in 0 until text.length){
				val chr = text[c]

				//line-column indexing algorithm
				colm_nbr++
				if(chr == '\n'){
					line_nbr++
					colm_nbr  = 1u
					inComment = false
				}

				//get latest raw_instruct location for error messages (optimization shortcut)
				var latestRI_lineNbr = raw_instructs.last().line_nbr
				var latestRI_colmNbr = raw_instructs.last().colm_nbr



				//case 1 : IN CHARACTER
				if(inChr){

					//escaped sequences
					if(escaping){
						Dreamlands.checkChrLength(
							latestRI_lineNbr,
							latestRI_colmNbr,
							raw_instructs.last().chr_text
						)
						escaping = false
						if(chr !in Dreamlands.ESCAPED_CHARACTERS_MAP){
							Dreamlands.invalidEscChr(latestRI_lineNbr, latestRI_colmNbr, chr)
						}
						raw_instructs.last().chr_text += Dreamlands.ESCAPED_CHARACTERS_MAP[chr]

					//not escaping (reading normaly)
					}else{

						//start escaping
						if(chr == '\\'){
							escaping = true

						//end of text field detection
						}else if(chr == '\''){
							inChr = false

						//regular character => add it
						}else{
							Dreamlands.checkChrLength(
								latestRI_lineNbr,
								latestRI_colmNbr,
								raw_instructs.last().chr_text
							)
							raw_instructs.last().chr_text += chr
						}
					}



				//case 2 : IN STRING
				}else if(inStr){

					//escaped sequences
					if(escaping){
						escaping = false
						if(chr !in Dreamlands.ESCAPED_CHARACTERS_MAP){
							Dreamlands.invalidEscChr(latestRI_lineNbr, latestRI_colmNbr, chr)
						}
						raw_instructs.last().str_text += Dreamlands.ESCAPED_CHARACTERS_MAP[chr]

					//not escaping (reading normally)
					}else{

						//start escaping
						if(chr == '\\'){
							escaping = true

						//end of text field detection
						}else if(chr == '\"'){
							inStr = false

						//regular character => add it
						}else{
							raw_instructs.last().str_text += chr
						}
					}


				//case 3 : RAW TEXT
				}else if(!inComment){

					//field detection : comment
					if(chr == COMMENT_CHARACTER){
						inComment = true

					//field detection : character
					}else if(chr == '\''){
						inChr = true

						//empty character (double character delimiter)
						if(text.length > c+1 && text[c+1] == '\''){
							throw RuntimeException(
								"Empty characters are not supported (line " +
								latestRI_lineNbr.toString() + " column "    +
								latestRI_colmNbr.toString() + ")."
							)
						}

					//field detection : string
					}else if(chr == '\"'){
						inStr = true

						//empty string (double string delimiter)
						if(text.length > c+1 && text[c+1] == '\"'){
							throw RuntimeException(
								"Empty strings are not supported (line " +
								latestRI_lineNbr.toString() + " column " +
								latestRI_colmNbr.toString() + ")."
							)
						}

					//end of instruction => passing to the new one
					}else if(chr == Dreamlands.LINE_END_CHARACTER){
						raw_instructs.add( RawInstructItem(line_nbr, colm_nbr) )

					//add raw text to current instruction
					}else{ raw_instructs.last().raw_text += chr }
				}
			}

			//missing text field delimiter
			if(inChr){
				throw RuntimeException(
					"Missing character delimiter at end of declaration (line " +
					raw_instructs.last().line_nbr.toString() + " column "      +
					raw_instructs.last().colm_nbr.toString() + ")."
				)
			}
			if(inStr){
				throw RuntimeException(
					"Missing string delimiter at end of declaration (line " +
					raw_instructs.last().line_nbr.toString() + " column "   +
					raw_instructs.last().colm_nbr.toString() + ")."
				)
			}

			//remove additional spaces (optional)
			if(Dreamlands.ADDITIONAL_SPACES_ALLOWED){
				for(a in 0 until raw_instructs.size){

					//remove spaces in raw_instructs[a].raw_text
					var ri_rawText = ""
					for(c in raw_instructs[a].raw_text){
						if(c != ' '){ ri_rawText += c }
					}
					raw_instructs[a].raw_text = ri_rawText
				}
			}

			//debug
			if(Dreamlands.DEBUG_MODE){
				println("[DEBUG] raw_instructs = [")
				for(ri in raw_instructs){
					println("\t" + ri.toString() + ",")
				}
				println("]")
			}



			// STEP 2 : SEPARATE FIELDS (inside each raw instruction)

			//instructions in strict format
			var instructs = mutableListOf<InstructItem>()

			//for each instruction
			for(ri in raw_instructs){



				//phase 1 : PREPARATION & OBVIOUS CASES

				//empty instruction => skip it
				if(ri.raw_text.length == 0 && ri.chr_text.length == 0 && ri.str_text.length == 0){ continue }

				//both character & string declaration => NOT ALLOWED
				if(ri.chr_text.length != 0 && ri.str_text.length != 0){
					throw RuntimeException(
						"Could not have both character and string declaration in only one instruction (line " +
						ri.line_nbr.toString() + " column "                                                   +
						ri.colm_nbr.toString() + ")."
					)
				}



				//phase 2 : IMPORTATIONS

				//importation detected
				if(ri.raw_text.startsWith(Dreamlands.NEW_FILE_CHARACTER)){

					//add it if option enabled
					if(Dreamlands.EXTERNAL_IMPORTATIONS_ALLOWED){
						instructs.add(
							InstructItem(
								ri.line_nbr,              //line_nbr
								ri.colm_nbr,              //colm_nbr
								ri.raw_text.substring(1), //value : here, filename is stored
								"",                       //key
								-1                        //depth : negative means IMPORTATION FLAG
							)
						)
					}

					//else : ignore this instruction
					continue

				//not an importation => create a new empty instruction
				}else{
					instructs.add(
						InstructItem(
							ri.line_nbr, //line_nbr
							ri.colm_nbr, //colm_nbr
							null,        //value (null also means: PARENT)
							"",          //key
							0,           //depth
						)
					)
				}



				//phase 3 : ANALYSIS

				//set depth
				for(c in ri.raw_text){
					if(c == Dreamlands.TABULATION_CHARACTER){
						instructs.last().depth++
						ri.raw_text = ri.raw_text.substring(1) //cut the 1st character
					}else{ break }
				}

				//separate key-value pair
				val pair = ri.raw_text.split(Dreamlands.SEPARATION_CHARACTER)
				if(pair.size != 2){
					throw RuntimeException(
						"One separation character is required per instruction (line " +
						ri.line_nbr.toString() + " column "                           +
						ri.colm_nbr.toString() + ")."
					)
				}
				instructs.last().key = pair[0]
				var value            = pair[1]

				//check key name length
				if(instructs.last().key.length == 0){
					throw RuntimeException(
						"Missing key name : at least one character is required (line " +
						ri.line_nbr.toString() + " column "                            +
						ri.colm_nbr.toString() + ")."
					)
				}

				//check key name
				if(instructs.last().key != "-"){
					for(c in instructs.last().key){
						if(c !in Dreamlands.KEYS_CHARACTERS_ALLOWED){
							throw RuntimeException(
								"Invalid character in key name : use only [a-z], [A-Z], [0-9] & underscores (line " +
								ri.line_nbr.toString() + " column "                                                 +
								ri.colm_nbr.toString() + ")."
							)
						}
					}
				}



				//case 1 : characters, strings or parent
				if(value.length == 0){

					//case 1.1 : character declaration
					if(ri.chr_text.length != 0){                   //can only be 0 or 1
						instructs.last().value = ri.chr_text[0]

					//case 1.2 : string declaration
					}else if(ri.str_text.length != 0){
						instructs.last().value = ri.str_text
					}

					//analysis terminated
					continue
				}



				//case 2 : booleans
				if(value == "false"){
					instructs.last().value = false
					continue
				}
				if(value == "true"){
					instructs.last().value = true
					continue
				}



				//case 3 : numbers

				//negative values
				var negative = false
				if(value[0] == '-'){
					negative = true
					value = value.substring(1) //cut negative sign (performance optimization)
				}

				//check every character of value
				var dotFound = false
				for(v in value){
					if(v == '.'){

						//dot already found => only one coma is allowed
						if(dotFound){
							throw RuntimeException(
								"Multiple dots found in floating point value for key '" + instructs.last().key + "' (line " +
								ri.line_nbr.toString() + " column "                                                         +
								ri.colm_nbr.toString() + ")."
							)
						}

						//1st dot found
						dotFound = true
						continue
					}

					//non-digit character => undefined value
					if(v !in '0'..'9'){
						throw RuntimeException(
							"Undefined value for key '" + instructs.last().key + "' (line " +
							ri.line_nbr.toString() + " column "                             +
							ri.colm_nbr.toString() + ")."
						)
					}
				}

				//floating point number
				if(dotFound){
					instructs.last().value = value.toFloat()

				//integer
				}else{
					instructs.last().value = value.toInt()
				}

				//apply negativity afterwards
				if(negative){
					instructs.last().value = -(instructs.last().value as Int)
				}
			}

			//debug
			if(Dreamlands.DEBUG_MODE){
				println("[DEBUG] instructs = [")
				for(i in instructs){
					println("\t" + i.toString() + ",")
				}
				println("]")
			}
			return instructs
		}



		//translate intructions into data
		private fun getFullValue(instructs:MutableList<InstructItem>, current_depth:Int) : Any? {

			//get brother value : if parent, first process its children
			var value = instructs[Dreamlands.instructsIndex].value
			Dreamlands.instructsIndex++
			if(value == null){
				value = Dreamlands.instructsToData(instructs, current_depth+1)
			}
			return value
		}

		private fun instructsToData(instructs:MutableList<InstructItem>, current_depth:Int=0) : Any {

			//1st instruction is a list element => global data will be a list
			val data_isList = (instructs[Dreamlands.instructsIndex].key == "-")
			var data : Any
			if(data_isList){ data = mutableListOf<Any?>()        }
			else           { data = mutableMapOf<String, Any?>() }

			//for each instruction (mind that instructsIndex is NOT incremented in general scope of loop but in statements)
			while(Dreamlands.instructsIndex < instructs.size){

				//debug
				if(Dreamlands.DEBUG_MODE){
					println("[DEBUG] translating into data " + instructs[Dreamlands.instructsIndex].toString() + "  instructsIndex " + Dreamlands.instructsIndex.toString() + ".")
				}

				//case 1 : too much indent
				if(instructs[Dreamlands.instructsIndex].depth > current_depth+1){
					throw RuntimeException(
						"Too much indent for instruct '" + instructs[Dreamlands.instructsIndex].key + "' (line " +
						instructs[Dreamlands.instructsIndex].line_nbr.toString() + " column "                    +
						instructs[Dreamlands.instructsIndex].colm_nbr.toString() + ")."
					)

				//case 2 : child element
				}else if(instructs[Dreamlands.instructsIndex].depth == current_depth+1){

					//children are processed by their parents (considerated as brothers between them)
					throw RuntimeException(
						"Children element '" + instructs[Dreamlands.instructsIndex].key + "' detected but no parent declared (line " +
						instructs[Dreamlands.instructsIndex].line_nbr.toString() + " column "                                        +
						instructs[Dreamlands.instructsIndex].colm_nbr.toString() + ")."
					)

				//case 3 : brother element
				}else if(instructs[Dreamlands.instructsIndex].depth == current_depth){

					//list element (brothers must be of the same kind)
					if(data_isList){
						if(instructs[Dreamlands.instructsIndex].key != "-"){
							throw RuntimeException(
								"Key name '" + instructs[Dreamlands.instructsIndex].key + "' detected inside a list (line " +
								instructs[Dreamlands.instructsIndex].line_nbr.toString() + " column "                       +
								instructs[Dreamlands.instructsIndex].colm_nbr.toString() + ")."
							)
						}

						//add brother element next to the current one
						(data as MutableList<Any?>).add( Dreamlands.getFullValue(instructs, current_depth) )

					//non-list element (brothers must be of the same kind)
					}else{
						if(instructs[Dreamlands.instructsIndex].key == "-"){
							throw RuntimeException(
								"List element detected outside a list (line "                         +
								instructs[Dreamlands.instructsIndex].line_nbr.toString() + " column " +
								instructs[Dreamlands.instructsIndex].colm_nbr.toString() + ")."
							)
						}
						val key = instructs[Dreamlands.instructsIndex].key

						//check key before adding brother
						if(key in (data as MutableMap<String, Any?>).keys){
							throw RuntimeException(
								"Key '" + key + "' already defined in its parent (line "              +
								instructs[Dreamlands.instructsIndex].line_nbr.toString() + " column " +
								instructs[Dreamlands.instructsIndex].colm_nbr.toString() + ")."
							)
						}

						//add brother element next to the current one (get value even recursively)
						(data as MutableMap<String, Any?>)[key] = Dreamlands.getFullValue(instructs, current_depth)
					}

				//case 4 : brother element of a parent (an uncle / grand-uncle / ...) => not of our business (end of child block)
				}else{ return data }
			}
			return data
		}



		//data -> to text
		private fun elementToText(elem:Any?, depth:Int) : String {
			var newDepth = depth+1
			var text :String = ""

			//case 1: [PARENT] element is a MutableMap
			if(elem is MutableMap<*,*>){
				text += Dreamlands.LINE_END_CHARACTER
				for(k in (elem as MutableMap<*,*>).keys){
					text +=
						TABULATION_CHARACTER*newDepth +
						k + SEPARATION_CHARACTER      +
						Dreamlands.elementToText((elem as MutableMap<String, Any?>)[k], newDepth)
				}
				return text

			//case 2: [PARENT] element is a tuple/list
			}else if(elem is MutableList<*>){
				text += Dreamlands.LINE_END_CHARACTER
				for(e in (elem as MutableList<Any?>)){
					text +=
						TABULATION_CHARACTER*newDepth +
						'-' + SEPARATION_CHARACTER    +
						Dreamlands.elementToText(e, newDepth)
				}
				return text

			//case 3: [CHILD] element is standalone
			}else if(elem != null){
				when(elem){

					//special case 3.1: boolean
					is Boolean -> {
						if(elem as Boolean){ return "true" + Dreamlands.LINE_END_CHARACTER }
						return "false" + Dreamlands.LINE_END_CHARACTER
					}

					//special case 3.2: characters
					is Char -> return "\'" + (elem as Char) + "\'" + Dreamlands.LINE_END_CHARACTER

					//special case 3.3: strings
					is String -> return "\"" + (elem as String) + "\"" + Dreamlands.LINE_END_CHARACTER

					//regular case 3.4: floating point number (including negative sign)
					is Float  -> return (elem as Float).toString()  + Dreamlands.LINE_END_CHARACTER
					is Double -> return (elem as Double).toString() + Dreamlands.LINE_END_CHARACTER

					//regular case 3.5: integer values (including negative sign)
					is Byte   -> return (elem as Byte).toString()   + Dreamlands.LINE_END_CHARACTER
					is UByte  -> return (elem as UByte).toString()  + Dreamlands.LINE_END_CHARACTER
					is Short  -> return (elem as Short).toString()  + Dreamlands.LINE_END_CHARACTER
					is UShort -> return (elem as UShort).toString() + Dreamlands.LINE_END_CHARACTER
					is Int    -> return (elem as Int).toString()    + Dreamlands.LINE_END_CHARACTER
					is UInt   -> return (elem as UInt).toString()   + Dreamlands.LINE_END_CHARACTER
					is Long   -> return (elem as Long).toString()   + Dreamlands.LINE_END_CHARACTER
					is ULong  -> return (elem as ULong).toString()  + Dreamlands.LINE_END_CHARACTER
					else -> throw RuntimeException("Unable to parse DREAMLANDS text, data contains value(s) of undefined type.")
				}

			//element is null => error
			}else{ throw RuntimeException("Unable to parse DREAMLANDS text, data contains null value(s).") }
		}

		fun toText(data:Any) : String {

			//check structure type
			if(data !is MutableMap<*,*> && data !is MutableList<*>){
				throw RuntimeException("Could not parse DREAMLANDS text, data has incorrect type (MutableMap or List allowed).")
			}

			//parse data
			val s = Dreamlands.elementToText(data, -1)
			return s.substring(0, s.length-2) //skip 1st character (useless Dreamlands.LINE_END_CHARACTER in global context)
		}





		// ---- READ / WRITE ----

		//read text from file => return data as MutableMap/MutableList
		fun read(filename:String) : Any? = Dreamlands.fromText( File(filename).readText() )

		//write data into file
		fun write(data:Any, filename:String) {
			File(filename).writeText( Dreamlands.toText(data) )
		}

	}
}

