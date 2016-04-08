# StalledDevelopment


## Api Spec
Draft 3.2
###Get information
####Get all bathrooms
`GET /api/bathrooms/all`
```json
[
	{
		"id": <int>,
		"loc": [<float>, <float>],
		"cleanliness": <float>
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
	"contraceptive": <bool>,
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
	"cleanliness": <float>
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
```
```json
[
	{
		"id": <int>,
		"loc": [<float>, <float>],
		"cleanliness": <float>
		"novelty": <float>
	},
	...
]
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
