#!/usr/bin/python3

from flask import Flask, jsonify, request
from flask_sqlalchemy import SQLAlchemy

from BathRoam.dbClasses import db, Bathroom, Rating, Image

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
			return "<blink><marquee><h1>BathRoam</h1></marquee></blink>"*10

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
				b_id = db.engine.execute(
					'SELECT bathroom_id, latitude, longitude, 111.045*DEGREES(ACOS(COS(RADIANS(' +
					str(float(request.args["lat"])) + '))* COS(RADIANS(latitude)) * COS(RADIANS(' +
					str(float(request.args["lon"])) + ') - RADIANS(longitude)) + SIN(RADIANS(' +
					str(float(request.args["lat"])) + '))* SIN(RADIANS(latitude)))) AS dist FROM bathrooms ORDER BY dist LIMIT 1;').first()[0]
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
				bClass=request.args["class"]
				gender=request.args["gender"]
				public=request.args["public"]=="true"
				paper=request.args["paper"]=="true"
				dryers=request.args["dryers"]=="true"
				handicap=request.args["handicap"]=="true"
				sanitizer=request.args["sanitizer"]=="true"
				baby=request.args["baby"]=="true"
				feminine=request.args["feminine"]=="true"
				medicine=request.args["medicine"]=="true"
				contraceptive=request.args["contraceptive"]=="true"
				sw_lat=float(request.args["sw_lat"])
				ne_lat=float(request.args["ne_lat"])
				sw_lon=float(request.args["sw_lon"])
				ne_lon=float(request.args["ne_lon"])

				b = Bathroom.query.filter(Bathroom.latitude.between(sw_lat, ne_lat))
				b = b.filter(Bathroom.longitude.between(sw_lon, ne_lon))

				if bClass == "Multi" or bClass == "Single" or bClass == "Portable" or bClass == "Pit":
					b = b.filter(Bathroom.bClass == bClass)
				if gender == "Male" or gender == "Female" or gender == "Unisex":
					b = b.filter(Bathroom.gender == gender)
				if public:
					b = b.filter(Bathroom.public == public)
				if paper:
					b = b.filter(Bathroom.paper == paper)
				if dryers:
					b = b.filter(Bathroom.dryers == dryers)
				if handicap:
					b = b.filter(Bathroom.handicap == handicap)
				if sanitizer:
					b = b.filter(Bathroom.sanitizer == sanitizer)
				if baby:
					b = b.filter(Bathroom.baby == baby)
				if feminine:
					b = b.filter(Bathroom.feminine == feminine)
				if medicine:
					b = b.filter(Bathroom.medicine == medicine)
				if contraceptive:
					b = b.filter(Bathroom.contraceptive == contraceptive)

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
				b = Bathroom(bClass=request.form["class"],
				gender=request.form["gender"],
				novelty=float(request.form["novelty"]),
				cleanliness=float(request.form["cleanliness"]),
				floor=int(request.form["floor"]),
				public=request.form["public"]=="True",
				paper=request.form["paper"]=="True",
				dryers=request.form["dryers"]=="True",
				stalls=int(request.form["stalls"]),
				handicap=request.form["handicap"]=="True",
				sinks=int(request.form["sinks"]),
				sanitizer=request.form["sanitizer"]=="True",
				baby=request.form["baby"]=="True",
				urinals=request.form["urinals"],
				feminine=request.form["feminine"]=="True",
				medicine=request.form["medicine"]=="True",
				contraceptive=request.form["contraceptive"]=="True",
				latitude=float(request.form["lat"]),
				longitude=float(request.form["lon"]))
			except (KeyError, ValueError):
				return make_response(jsonify({'error': 'Bad request'}), 400)
			db.session.add(b)
			db.session.commit()
			return jsonify({"id": b.bathroom_id})

		@app.route('/api/ratings/create', methods=['POST'])
		def post_new_rating():
			try:
				r = Rating(bathroom_id=request.form["bathroom_id"],
				mac_address=request.form["mac_address"],
				novelty=float(request.form["novelty"]),
				cleanliness=float(request.form["cleanliness"]))
			except (KeyError, ValueError):
				return make_response(jsonify({'error': 'Bad request'}), 400)

			existing = Rating.query.filter(Rating.mac_address == request.form["mac_address"]);
			existing = existing.filter(Rating.bathroom_id == request.form["bathroom_id"]);
			if existing.count() == 0:
				db.session.add(r)
			else:
				existing.update({"novelty": request.form["novelty"], "cleanliness": request.form["cleanliness"]})
			db.session.commit()
			return jsonify({"id": r.rating_id})

		@app.route('/api/ratings/all', methods=['GET'])
		def get_all_ratings():
			r = Rating.query.all()
			return jsonify([{
						"rating_id": rr.rating_id,
						"bathroom_id": rr.bathroom_id,
						"mac_address": rr.mac_address,
						"cleanliness": rr.cleanliness,
						"novelty": rr.novelty
					} for rr in r])
		
		@app.route('/api/bathrooms/images/create', methods=['POST'])
		def post_new_photo():
			try:
				i = Image(bathroom_id=request.form["bathroom_id"],
				image=request.form["image"])
			except (KeyError, ValueError):
				return make_response(jsonify({'error': 'Bad request'}), 400)
			
			db.session.add(i)
			db.session.commit()
			return jsonify({"id": i.image_id})

		@app.route('/api/bathrooms/images/all', methods=['GET'])
		def get_all_images():
			i = Image.query.all()
			return jsonify([{
						"image_id": ii.image_id,
						"bathroom_id": ii.bathroom_id,
						"image": ii.image
					} for ii in i])

		@app.route('/api/bathrooms/<int:bathroom_id>/images', methods=['GET'])
		def get_bathroom_images(bathroom_id):
			i = Image.query.filter(Image.bathroom_id == bathroom_id)
			return jsonify([{
						"image_id": ii.image_id,
						"bathroom_id": ii.bathroom_id,
						"image": ii.image
					} for ii in i])
	return app
