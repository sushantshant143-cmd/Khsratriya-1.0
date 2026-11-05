#!/usr/bin/env python3
"""Generate a PBKDF2 verifier for the KSHATRIYA test Developer PIN."""
import base64, getpass, hashlib, secrets
pin = getpass.getpass('Enter the common six-digit Developer PIN: ')
if len(pin) != 6 or not pin.isdigit():
    raise SystemExit('PIN must contain exactly six digits.')
salt = secrets.token_bytes(16)
digest = hashlib.pbkdf2_hmac('sha256', pin.encode(), salt, 120_000, dklen=32)
print('developerPinVerifier=' + base64.b64encode(salt).decode() + ':' + base64.b64encode(digest).decode())
