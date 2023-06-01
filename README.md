# Connection
Connection is an application that allows the creation of a wireless mesh network between android phones and allows them to communicate with each other through tcp and udp.
# Our idea
Our idea was to create a social network where people could understand who they had close to them and get to know each other.
# Functionality
The application allows you to create multiple networks in communication with each other without using bluetooth but only with wifi direct(multigroup).
# How
Normally android does not allow multigroup, we have circumvented the problem by using the wifi of the device (Client) and at the same time the wifi direct in legacy mode (GO).
When we created the first double network we realized that communications did not work if we used ipv4 because the devices conflicted due to identical ip addresses, so we used ipv6.
# Credits
giangiu25307-rikyfaso-Lucal09


