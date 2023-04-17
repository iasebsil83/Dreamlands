#!/usr/bin/python3



# .------------------ DREAMLANDS Python Library -----------------.
# |                                                              |
# |     This library is the official parser tool for DREAMLANDS  |
# | files. It includes 4 functions :                             |
# |   - dreamlands.fromText() #get data from raw text            |
# |   - dreamlands.toText()   #get raw text from data            |
# |   - dreamlands.read()     #get data from file                |
# |   - dreamlands.write()    #write data into file              |
# |                                                              |
# |     For more information about this syntax, please check     |
# | out the main repository for documentation:                   |
# |                                                              |
# |           https://github.com/iasebsil83/DREAMLANDS           |
# |                                                              |
# | Let's Code !                                         By I.A. |
# |                                                              |
# |************************************************************************|
# |   LICENCE :                                                            |
# |                                                                        |
# |   DREAMLANDS_Python3                                                   |
# |   Copyright (C) 2023  Sebastien SILVANO                                |
# |   This program is free software: you can redistribute it and/or modify |
# |   it under the terms of the GNU General Public License as published by |
# |   the Free Software Foundation, either version 3 of the License, or    |
# |   any later version.                                                   |
# |   This program is distributed in the hope that it will be useful,      |
# |   but WITHOUT ANY WARRANTY; without even the implied warranty of       |
# |   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the        |
# |   GNU General Public License for more details.                         |
# |   You should have received a copy of the GNU General Public License    |
# |   along with this program.                                             |
# |                                                                        |
# |  If not, see <https://www.gnu.org/licenses/>.                          |
# '------------------------------------------------------------------------'






# ---- IMPORTATIONS ----

#charsets
import string






# ---- CONSTANTS ----

#special characters
COMMENT_CHARACTER    = '#'
LINE_END_CHARACTER   = '\n'
NEW_FILE_CHARACTER   = '>'
SEPARATION_CHARACTER = ':'
TABULATION_CHARACTER = '\t'

#keywords
KEYWORDS_ALLOWED_CHARACTERS = string.ascii_letters + string.digits






# ---- UTILITIES ----

"""
#keywords
def __checkKeyword(keyword):
		'''
		Verify the integrity of a DREAMLANDS keyword.

		keyword: str
		'''

		#error case
		if len(keyword) == 0:
			raise ValueError("Got empty key name.")

		#other characters
		for k in keyword:
			if k not in KEYWORDS_ALLOWED_CHARACTERS:
				raise ValueError("Forbidden character in keyword.")
"""






# ---- DATA <-> TEXT ----

#data <- from text
def fromText(text):
	'''
	Convert a DREAMLANDStext into dictionnary.

	text: str

	Returns a data structure corresponding to the given DREAMLANDS text (dict/list).
	'''

	#for each instruction
	data = {}

	return data



#data -> to text
def __elementToText(elem, degree):
	'''
	Convert a data element into DREAMLANDS text.

	data: any (except None)
	degree: int

	Returns a chunk of DREAMLANDS text corresponding to the given data.
	'''

	#case 1: [PARENT] element is a dictionnary
	if isinstance(elem, dict):
		text = LINE_END_CHARACTER
		for k in elem.keys():
			text += (degree+1)*TABULATION_CHARACTER + k + SEPARATION_CHARACTER + __elementToText(elem[k], degree+1)
		return text

	#case 2: [PARENT] element is a tuple/list
	elif isinstance(elem, tuple) or isinstance(elem, list):
		text = LINE_END_CHARACTER
		for e in elem:
			text += (degree+1)*TABULATION_CHARACTER + '-' + SEPARATION_CHARACTER + __elementToText(e, degree+1)
		return text

	#case 3: [CHILD] element is standalone
	elif elem is not None:

		#special case 3.1: boolean
		if isinstance(elem, bool):
			if elem:
				return "true" + LINE_END_CHARACTER
			return "false" + LINE_END_CHARACTER

		#special case 3.2: strings
		elif isinstance(elem, str):
			return "\"" + str(elem) + "\"" + LINE_END_CHARACTER

		#regular case 3.3: other
		return str(elem) + LINE_END_CHARACTER

	#case 4: element is None => ERROR
	else:
		raise ValueError("Unable to parse DREAMLANDS text, data contains None value(s).")

def toText(data):
	'''
	Convert a data structure into DREAMLANDS text.

	data: dict, tuple, list

	Returns a DREAMLANDS text corresponding to the given data.
	'''

	#check structure type
	if not isinstance(data, dict) and not isinstance(data, tuple) and not isinstance(data, list):
		raise ValueError("Could not parse DREAMLANDS text, data has incorrect type (dict, tuple or list allowed).")

	#parse data
	return __elementToText(data, -1)[1:] #skip 1st character (useless LINE_END_CHARACTER in global context)






# ---- READ / WRITE ----

#read text from file => return data as dict
def read(filename):
	'''
	Read a DREAMLANDS file.

	filename: str

	Returns a dictionnary representing the data read from the file.
	'''

	#read text from file
	f = open(filename, "r")
	text = f.read()
	f.close()

	#parse
	return fromText(text)




#write data into file
def write(data, filename):
	'''
	Write data into a DREAMLANDS file.

	data: dict
	filename: str

	Write the data respecting the DREAMLANDS syntax.
	'''

	#unparse
	text = toText(data)

	#write out
	f = open(filename, "w")
	f.write(text)
	f.close()
