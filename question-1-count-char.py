#!/usr/bin/python

import fileinput

def main():
	charcount={}
	totalcount=0
	for line in fileinput.input():
		for char in line.strip():
			totalcount+=1
			if charcount.has_key(char) == False:
				charcount[char] = 1
			else:
				charcount[char] += 1
	for (key,value) in charcount.items():
		print "{0}\t{1}".format(key, value)
	print "total count: {0}".format(totalcount)

if __name__ == "__main__":
	main()
