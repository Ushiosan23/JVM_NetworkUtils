# Changelog

## 0.0.2

- Restructure code and change all library
- Send basic http request
    - Change request method to new object named [HttpRequestAction]()
        - This object make http sync request and async requests
        - Change Enum method to simple request method
            - [HttpRequestAction#get]()
            - [HttpRequestAction#post]()
            - [HttpRequestAction#put]()
            - [HttpRequestAction#delete]()
- Download file
    - Download file and check status every time
    - Download file and manage downloads by unique file name
    - Download file and detect finishing
    - Download in another thread
- Check request
    - You can check if url is valid and url exists
        - [HttpRequestAction#exists]()

## 0.0.1

- Send basic http request
- Basic file download
