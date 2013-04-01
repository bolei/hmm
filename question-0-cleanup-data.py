#!/usr/bin/python
# -*- coding: utf-8 -*-
import fileinput

#ignorelist = ('!', '-', '_', '(', ')', ',', '.', ':', ';', '"', '\'', '?', '#', '@', '$', '^', '&', '*', '+', '=', '{', '}', '[', ']', '\\', '|', '<', '>', '/', u'—')
ignorelist = ('!', '-', '_', '(', ')', ',', '.', ':', ';', '"', '\'', '?', '#', '@', '$', '^', '&', '*', '+', '=', '{', '}', '[', ']', '\\', '|', '<', '>', '/')

def print_token(token):
#	print "before clean: "+ token
	for punc in ignorelist:
		token = token.replace(punc, ' ')
#		print "after clean: "+token
	for subtoken in token.strip().split():
		print subtoken.upper(),
	
def main():
	for line in fileinput.input():
		items = line.split()
		for token in items:
			print_token(token)

if __name__ == "__main__":
	main()
