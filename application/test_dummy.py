import unittest

from .web.server import app


class DummyTest(unittest.TestCase):

    def test_something(self):
        self.assertTrue(True)

    def test_something_else(self):
        self.assertNotEqual(app, None)
