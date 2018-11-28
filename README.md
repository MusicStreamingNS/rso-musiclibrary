# RSO: Music-Library microservice

## Prerequisites

```bash
docker run -d --name rso-musiclibrary -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=musiclibrary -p 5432:5432 postgres:10.5
```
## Travis status
[![Build Status](https://travis-ci.org/MusicStreamingNS/rso-musiclibrary.svg?branch=master)](https://travis-ci.org/MusicStreamingNS/rso-musiclibrary)