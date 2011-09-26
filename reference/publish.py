#!/usr/bin/python

import run_jsfl
import sys
import re
import os.path
import shutil

def get_options_parser():
	parser = run_jsfl.get_options_parser()

	parser.add_option("-o", "--output",
	                  action="store", dest="output",
					  help="Move published .swfs to FOLDER. Default is to leave "
					       "them in the folder specified in the FLA/XFL's "
						   "publish settings.",
					  metavar="FOLDER")

	parser.add_option("--script",
	                  action="store", dest="script",
					  help="Path to publish.jsfl. Default is the name of the "
					       "executing script with .py replaced with .jsfl")

	return parser


def publish(runner, script, fla, output=None):
	base_dir = os.path.dirname(fla)
	result = runner.run_file(script, [ fla ])
	swf = None

	lines = re.split(r"[\n\r]+", result)

	for line in lines:
		if line.startswith("+++"):
			if fla.lower().endswith(".xfl"):
				# xfls actually publish to ../ + their output path
				swf = os.path.join(base_dir, "..", line[1:])
			else:
				swf = os.path.join(base_dir, line[1:])
			
			swf = os.path.normpath(swf)

			print "OK: {0}".format(swf)
			break
		elif line.startswith("!!!"):
			raise StandardError(line[1:])

	if output is not None:
		swf_dest = os.path.join(output, os.path.basename(swf))

		print "{0} -> {1}".format(swf, swf_dest)
		shutil.move(swf, swf_dest)


if __name__ == "__main__":
	parser = get_options_parser()
	(options, args) = parser.parse_args()
	
	try:
		runner = run_jsfl.create_from_options(options)
	except run_jsfl.InvalidOptionsError as e:
		parser.print_help()
		print e
		sys.exit(2)

	script = options.script
	if script is None:
		script = re.sub(r"\.py$", ".jsfl", sys.argv[0], 1, re.IGNORECASE)

	if len(args) < 1:
		print "No FLA/XFL files specified"
		sys.exit(2)

	for fla in args:
		fla = os.path.abspath(fla)
		publish(runner, script, fla, options.output)
