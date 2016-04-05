#!/usr/bin/python3

from flask import Flask, jsonify, request, make_response

def create_app():
	app = Flask("BathRoam")
	with app.app_context():
		@app.errorhandler(404)
		def not_found(error):
			return make_response(jsonify({'error': 'Not found'}), 404)
		@app.route('/')
		def index():
			return "<h2>~toilets~</h2>"
		@app.route('/api/bathrooms/all', methods=['GET'])
		def get_all_bathrooms():
			return jsonify([
    {
        "id": 1,
        "loc": [0.0, -7.9],
        "rating": 3.5
    },
	{
        "id": 2,
        "loc": [0.5, -345.9],
        "rating": 1.1
    }
])
		@app.route('/api/bathrooms/<int:bathroom_id>', methods=['GET'])
		def get_bathroom(bathroom_id):
			return jsonify({
	"id": 1,
    "loc": [0.0, -7.9],
    "class": "pit toilet",
    "gender": "gendered",
    "novelty": 3.5,
    "cleanliness": 3.5,
    "floor": 1,
    "public": True,
    "paper": False,
    "dryers": False,
    "stalls": 1,
    "handicap": True,
    "sinks": 0,
    "sanitizer": True,
    "baby": False,
    "urinals": False,
    "feminine": False,
    "medicine": False,
    "contraceptive": False,
})
		@app.route('/api/bathrooms', methods=['GET'])
		def get_bathroom_query():
			if "id" in request.args:
				return get_bathroom(request.args["id"])
			return jsonify({"error": "bad request"})
		@app.route('/api/bathrooms/nearby', methods=['GET'])
		def get_nearby_bathrooms():
			return jsonify([
    {
        "id": 1,
        "loc": [0.0, -7.9],
        "rating": 3.5
    },
	{
        "id": 2,
        "loc": [0.5, -345.9],
        "rating": 1.1
    }
])
		return app
