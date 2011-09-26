#!/usr/bin/python

import sys
import os
import shutil
import tempfile
import subprocess
import time
import json
import codecs

from optparse import OptionParser

wrapper = """
(function() {{
	fl.outputPanel.clear();
	var argv={argv};

	(function() {{
{source}
	}}());

	fl.outputPanel.save(FLfile.platformPathToURI({log_path}));
	FLfile.remove(FLfile.platformPathToURI({lock_path}));
}}());
"""


def write_file(path, contents, codec="utf_8"):
	stream = codecs.open(path, 'w', codec)
	stream.write(contents)
	stream.close()


def read_file(path, codec="utf_8"):
	stream = codecs.open(path, 'r', codec)
	contents = stream.read()
	stream.close()
	return contents


class TempProvider(object):
	def __init__(self, folder=None):
		self.counter = 0
		self.path = folder
		self.remove = False
	
	def get_path(self, file_suffix):
		if (self.path is None):
			self.path = tempfile.mkdtemp()
			self.remove = True
		name = "{counter:x}{suffix}".format(counter=self.counter, suffix=file_suffix)
		self.counter += 1
		return os.path.abspath(os.path.join(self.path, name))

	def destroy(self):
		if (self.remove):
			shutil.rmtree(self.path)


class ExecutorWin(object):
	def __init__(self, flash_exe):
		self.flash_exe = flash_exe

	def run_script(self, script_path):
		return subprocess.call(["cmd", "/c", "start", self.flash_exe, script_path])


class ExecutorMac(object):
	def __init__(self, flash_app):
		self.flash_app = flash_app

	def run_script(self, script_path):
		return subprocess.call(["open", "-a", self.flash_app, script_path])


class PrintLogger(object):
	def __init__(self, out_stream=None):
		self.out_stream = out_stream
	
	def log(self, message):
		print >> self.out_stream, message


class NullLogger(object):
	def log(self, message):
		pass


class TimeoutError(EnvironmentError):
	def __init__(self, time, file):
		self.time = time
		self.file = file


class UnsupportedOSError(StandardError):
	pass


class JSFLRunner(object):
	def __init__(self, executable, logger=None, temp_provider=None):
		self.temp_provider = temp_provider
		if logger is None:
			self.logger = NullLogger()
		else:
			self.logger = logger

		if sys.platform in ('win32','cygwin'):
			self.executor = ExecutorWin(executable)
		elif sys.platform == 'darwin':
			self.executor = ExecutorMac(executable)
		else:
			raise UnsupportedOSError()
        
	def __enter__(self):
		return self
								
	def __exit__(self, excType, excValue, stack):
		self.destroy()
		return False

	def destroy(self):
		self.temp_provider.destroy()
		self.temp_provider = None

	def log(self, message):
		self.logger.log(message)

	def run_text(self, jsfl_src, cmd_name="", arguments=None, timeout=120, poll_freq=3):
		waited_time = 0

		src_path = self.temp_provider.get_path(".jsfl")
		log_path = self.temp_provider.get_path(".txt")
		lock_path = self.temp_provider.get_path(".lock")

		self.log("Writing lock to {0}".format(lock_path))
		write_file(lock_path, "JSFLRunner lock")

		argv = [ cmd_name ]

		if arguments is not None:
			argv.extend(arguments)

		wrapped_src = wrapper.format(
				source=jsfl_src, 
				log_path=repr(log_path), 
				lock_path=repr(lock_path),
				argv=json.dumps(argv))

		self.log("Writing source to {0}".format(src_path))
		write_file(src_path, wrapped_src)

		ret_code = self.executor.run_script(src_path)

		while os.path.exists(lock_path):
			if (waited_time > timeout):
				raise TimeoutError(timeout, lock_path)
			time.sleep(poll_freq)
			waited_time += poll_freq

		return read_file(log_path, "utf_8_sig")

	def run_file(self, jsfl_path, arguments=None, timeout=120, poll_freq=3):
		return self.run_text(read_file(jsfl_path), jsfl_path, arguments, timeout, poll_freq)


def get_options_parser():
	parser = OptionParser()

	parser.add_option("-f", "--flash", 
			          action="store", dest="flash",
					  help="Path to Flash CS4+ executable (.exe or .app). Required.")

	parser.add_option("--temp", 
			          action="store", dest="temp",
					  help="Create temporary files in PATH. The default is to "
					       "automatically create a temporary folder and delete it "
						   "when the script exits. If this option is specified, "
						   "PATH will not be deleted",
					  metavar="PATH")

	parser.add_option("--timeout", 
			          action="store", dest="timeout", type="int", default=120,
					  help="Assume an error if the JSFL hasn't completed in "
					       "NUM seconds. Default is 120 (two minutes)",
					  metavar="NUM")

	parser.add_option("--poll", 
			          action="store", dest="poll", type="int", default=3,
					  help="Check for script completion every NUM seconds. "
					       "Default is 3.",
					  metavar="NUM")

	parser.add_option("-v", "--verbose",
			          action="store_true", dest="verbose", default=False,
					  help="Log detailed messages to STDERR")

	return parser


class InvalidOptionsError(StandardError):
	pass


def create_from_options(options):
	if options.flash is None:
		raise InvalidOptionsError("Missing --flash value")

	flash = os.path.expandvars(options.flash)

	if options.verbose:
		logger = PrintLogger(sys.stderr)
	else:
		logger = None

	temp = TempProvider(options.temp)

	return JSFLRunner(flash, logger, temp)


if __name__ == "__main__":
	parser = get_options_parser()
	(options, args) = parser.parse_args()

	try:
		runner = create_from_options(options)
	except InvalidOptionsError as e:
		parser.print_help()
		print e
		sys.exit(2)

	if len(args) < 1:
		parser.print_help()
		print "No script specified"
		sys.exit(2)

	with runner:
		print(runner.run_file(args[0], args[1:], options.timeout, options.poll))
