#!/usr/bin/python3

from flask import Flask, jsonify, request, make_response
from flask_sqlalchemy import SQLAlchemy

from BathRoam.dbClasses import db, Bathroom, Rating

def create_app(config="BathRoam.config"):
	app = Flask("BathRoam")
	app.config.from_object(config)
	db.init_app(app)
	with app.app_context():
		db.create_all()
		@app.errorhandler(404)
		def not_found(error):
			return make_response(jsonify({'error': 'Not found'}), 404)
		@app.route('/')
		def index():
			return "<blink><marquee><h1>~toilets~</h1></marquee></blink>"*10
		@app.route('/api/bathrooms/all', methods=['GET'])
		def get_all_bathrooms():
			b = Bathroom.query.all()
			return jsonify([{
						"id": br.bathroom_id,
						"loc": [str(br.latitude), str(br.longitude)],
						"cleanliness": br.cleanliness,
						"novelty": br.novelty
					} for br in b])
		@app.route('/api/bathrooms/<int:bathroom_id>', methods=['GET'])
		def get_bathroom(bathroom_id):
			b = Bathroom.query.get_or_404(bathroom_id)
			return jsonify({
						"id": b.bathroom_id,
						"loc": [str(b.latitude), str(b.longitude)],
						"class": b.bClass,
						"gender": b.gender,
						"novelty": str(b.novelty),
						"cleanliness": str(b.cleanliness),
						"floor": b.floor,
						"public": b.public,
						"paper": b.paper,
						"dryers": b.dryers,
						"stalls": b.stalls,
						"handicap": b.handicap,
						"sinks": b.sinks,
						"sanitizer": b.sanitizer,
						"baby": b.baby,
						"urinals": b.urinals,
						"feminine": b.feminine,
						"medicine": b.medicine,
						"contraceptive": b.contraceptive
					})
		@app.route('/api/bathrooms', methods=['GET'])
		def get_bathroom_query():
			if "id" in request.args:
				return get_bathroom(request.args["id"])
			return make_response(jsonify({'error': 'Bad request'}), 400)
		@app.route('/api/bathrooms/nearest', methods=['GET'])
		def get_nearest_bathroom():
			try:
				b_id = db.engine.execute('SELECT bathroom_id, latitude, longitude, 111.045*DEGREES(ACOS(COS(RADIANS(' + str(float(request.args["lat"])) + '))* COS(RADIANS(latitude)) * COS(RADIANS(' + str(float(request.args["lon"])) + ') - RADIANS(longitude)) + SIN(RADIANS(' + str(float(request.args["lat"])) + '))* SIN(RADIANS(latitude)))) AS distance_in_km FROM bathroom ORDER BY distance_in_km LIMIT 1;').first()[0]
				b = Bathroom.query.get(b_id)
			except KeyError:
				return make_response(jsonify({'error': 'Bad request'}), 400)
			return jsonify({
						"id": b.bathroom_id,
						"loc": [str(b.latitude), str(b.longitude)],
						"cleanliness": b.cleanliness,
						"novelty": b.novelty
					})
		@app.route('/api/bathrooms/within', methods=['GET'])
		def get_nearby_bathrooms():
			try:
				b = Bathroom.query.filter(Bathroom.latitude.between(float(request.args["sw_lat"]), float(request.args["ne_lat"]))).filter(Bathroom.longitude.between(float(request.args["sw_lon"]), float(request.args["ne_lon"])))
			except KeyError:
				return make_response(jsonify({'error': 'Bad request'}), 400)
			return jsonify([{
						"id": br.bathroom_id,
						"loc": [str(br.latitude), str(br.longitude)],
						"cleanliness": br.cleanliness,
						"novelty": br.novelty
					} for br in b])
		@app.route('/api/bathrooms/create', methods=['POST'])
		def post_new_bathroom():
			try:
				b = Bathroom(bClass=request.form["class"], gender=request.form["gender"], novelty=float(request.form["novelty"]), cleanliness=float(request.form["cleanliness"]), floor=int(request.form["floor"]), public=request.form["public"]=="True", paper=request.form["paper"]=="True", dryers=request.form["dryers"]=="True", stalls=int(request.form["stalls"]), handicap=request.form["handicap"]=="True", sinks=int(request.form["sinks"]), sanitizer=request.form["sanitizer"]=="True", baby=request.form["baby"]=="True", urinals=request.form["urinals"], feminine=request.form["feminine"]=="True", medicine=request.form["medicine"]=="True", contraceptive=request.form["contraceptive"]=="True", latitude=float(request.form["lat"]), longitude=float(request.form["lon"]))
			except (KeyError, ValueError):
				return make_response(jsonify({'error': 'Bad request'}), 400)
			db.session.add(b)
			db.session.commit()
			return jsonify({"id": b.bathroom_id})

		@app.route('/api/ratings/create', methods=['POST'])
		def post_new_rating():
			try:
				r = Rating(bathroom_id=request.form["bathroom_id"], mac_address=request.form["mac_address"], novelty=float(request.form["novelty"]), cleanliness=float(request.form["cleanliness"]))
			except (KeyError, ValueError):
				return make_response(jsonify({'error': 'Bad request'}), 400)
			db.session.add(r)
			db.session.commit()
			return jsonify({"id": r.rating_id})

	return app
