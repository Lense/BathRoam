#BathRoam by Stalled Development

##Installation instructions
###Android app
- Download APK
- Enable installation of apps from sources other than the Play Store
- Install APK
###Server
- Clone the repository
- Install dependencies
  - nginx
  - python3
  - python3-flask
  - sqlalchemy
  - uwsgi
  - SQL
- Update BathRoam/config.py with database user and password
- Run `uwsgi --ini uwsgi.ini` from the server directory

##API Spec
Draft 4
###Downloads
####Current version signed APK
`GET /BathRoam.apk`

###Get information
####Get all bathrooms
`GET /api/bathrooms/all`
```json
[
	{
		"id": <int>,
		"loc": [<float>, <float>],
		"cleanliness": <float>,
		"novelty": <float>
	},
	...
]
```

####Get specific bathroom information
```
GET /api/bathrooms/
id=<int>
```
```json
{
	"id": <int>,
	"loc": [<float>, <float>],
	"class": <str>,
	"gender": <str>,
	"novelty": <float>,
	"cleanliness": <float>,
	"floor": <int>,
	"public": <bool>,
	"paper": <bool>,
	"dryers": <bool>,
	"stalls": <int>,
	"handicap": <bool>,
	"sinks": <int>,
	"sanitizer": <bool>,
	"baby": <bool>,
	"urinals": <int>,
	"feminine": <bool>,
	"medicine": <bool>,
	"contraceptive": <bool>
}
```

####Get closest bathroom
```
GET /api/bathrooms/nearest
lat=<float>
lon=<float>
```
```json
{
	"id": <int>,
	"loc": [<float>, <float>],
	"cleanliness": <float>,
	"novelty": <float>
}
```

####Get local bathrooms within a bounding box
```
GET /api/bathrooms/within
ne_lat=<float>
ne_lon=<float>
sw_lat=<float>
sw_lon=<float>
class=<str>
gender=<str>
public=<bool>
paper=<bool>
dryers=<bool>
handicap=<bool>
sanitizer=<bool>
baby=<bool>
feminine=<bool>
medicine=<bool>
contraceptive=<bool>
```
```json
[
	{
		"id": <int>,
		"loc": [<float>, <float>],
		"cleanliness": <float>,
		"novelty": <float>
	},
	...
]
```

####Get all bathrooms
`GET /api/ratings/all`
```json
[
	{
		"rating_id": <int>,
		"bathroom_id": <int>,
		"mac_address": rr.mac_address,
		"cleanliness": <float>,
		"novelty": <float>
	},
	...
]

```

####Get all images
`GET /api/bathrooms/images/all`
```json
[
	{
		"image_id": <int>,
		"bathroom_id": <int>,
		"image": <base64>
	},
	...
]
```

####Get image
```
GET /api/bathrooms/<int>/images
```
```json
{
	"image_id": <int>,
	"bathroom_id": <int>,
	"image": <base64>
}
```

###Send information
####Create new bathroom
```
POST /api/bathrooms/create
lat=<float>
lon=<float>
class=<str>
gender=<str>
novelty=<float>
cleanliness=<float>
floor=<int>
public=<bool>
paper=<bool>
dryers=<bool>
stalls=<int>
handicap=<bool>
sinks=<int>
sanitizer=<bool>
baby=<bool>
urinals=<int>
feminine=<bool>
medicine=<bool>
contraceptive=<bool>
```
```json
{
	"id": <int>
}
```

####Create new Rating
```
POST /api/ratings/create
bathroom_id=<int>
mac_address=<str>
novelty=<float>
cleanliness=<float>
```
```json
{
	"id": <int>
}
```

####Submit an image
```
POST /api/bathrooms/images/create
bathroom_id=<int>
image=<base64>
```
```json
{
	"id": <int>
}
```
