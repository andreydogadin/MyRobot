#!/usr/bin/env python
import dbus, sys

def main():
    remote_bus = dbus.SessionBus()
    # Get skype dbus api
    skype_service = remote_bus.get_object('com.Skype.API', '/com/Skype')

    answer = skype_service.Invoke('NAME HomeRobotControlForm')
    if answer != 'OK':
        sys.exit('Could not bind to Skype client')

    # Check if protocol is supported.
    answer = skype_service.Invoke('PROTOCOL 1')
    if answer != 'PROTOCOL 1':
        sys.exit('This test program only supports Skype API protocol version 1')

    # Invoke operations
    command = sys.argv[1]
    command = command.replace('*',' ')
    print command
    res = skype_service.Invoke(command)
    print res
    return
if __name__ == "__main__":
    main()