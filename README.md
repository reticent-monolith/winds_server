# winds_server

The simple backend for a custom system I thought up to make Zip World's zipwire dispatch communication and logging quicker and more efficient.
The clients (https://github.com/reticent-monolith/winds_client and https://github.com/reticent-monolith/bt_client) communicate with each other via MQTT to swap data for the dispatch and get the riders correctly set up without needing to use radios.
Once the dispatch is dispatched the Winds client send it to the backend for it to be prcocessed and added to the database.

It's all currently set up on a droplet (https://winds.reticent-monolith.com and https://bigtop.reticent-monolith.com), but I think would be located entirely on a private network, with maybe the DB hosted in the cloud.
