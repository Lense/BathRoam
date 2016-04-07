# StalledDevelopment


## Api Spec
####Get all bathrooms
`GET /api/bathrooms/all`
```json
[
	{
		"id": <int>,
		"loc": [<float>, <float>],
		"rating": <float>
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
	"urinal": <bool>
}
```

####Get local bathrooms
```
GET /api/bathrooms/nearby
ne_lat=<float>
ne_lon=<float>
sw_lat=<float
sw_lon=<float>
```
```json
[
	{
		"id": <int>,
		"loc": [<float>, <float>],
		"rating": <float>
	},
	...
]
```