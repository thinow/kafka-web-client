import unittest

from .web.server import web_server


class DummyTest(unittest.TestCase):

    def test_something(self):
        self.assertTrue(True)

    def test_something_else(self):
        self.assertNotEqual(web_server, None)
