#!/usr/bin/python3

from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class Bathroom(db.Model):
	bathroom_id = db.Column(db.Integer, primary_key=True)
	bClass = db.Column(db.String(32))
	gender = db.Column(db.String(32))
	novelty = db.Column(db.Float())
	cleanliness = db.Column(db.Float())
	floor = db.Column(db.Integer())
	public = db.Column(db.Boolean())
	paper = db.Column(db.Boolean())
	dryers = db.Column(db.Boolean())
	stalls = db.Column(db.Integer())
	handicap = db.Column(db.Boolean())
	sinks = db.Column(db.Integer())
	sanitizer = db.Column(db.Boolean())
	baby = db.Column(db.Boolean())
	urinals = db.Column(db.Integer())
	feminine = db.Column(db.Boolean())
	medicine = db.Column(db.Boolean())
	contraceptive = db.Column(db.Boolean())
	latitude = db.Column(db.Numeric(precision=9, scale=6, asdecimal=True))
	longitude = db.Column(db.Numeric(precision=9, scale=6, asdecimal=True))

	def __init__(self, bClass, gender, novelty, cleanliness, floor, public, paper, dryers, stalls, handicap, sinks, sanitizer, baby, urinals, feminine, medicine, contraceptive, latitude, longitude):
		self.bClass = bClass
		self.gender = gender
		self.novelty = novelty
		self.cleanliness = cleanliness
		self.floor = floor
		self.public = public
		self.paper = paper
		self.dryers = dryers
		self.stalls = stalls
		self.handicap = handicap
		self.sinks = sinks
		self.sanitizer = sanitizer
		self.baby = baby
		self.urinals = urinals
		self.feminine = feminine
		self.medicine = medicine
		self.contraceptive = contraceptive
		self.latitude = latitude
		self.longitude = longitude

	def __repr__(self):
		return '<Bathroom %r>' % self.bathroom_id

class Rating(db.Model):
	rating_id = db.Column(db.Integer, primary_key=True)
	bathroom_id = db.Column(db.Integer)
	mac_address = db.Column(db.String(18))
	novelty = db.Column(db.Float())
	cleanliness = db.Column(db.Float())

	def __init__(self, bathroom_id, mac_address, novelty, cleanliness):
		self.bathroom_id = bathroom_id
		self.mac_address = mac_address
		self.novelty = novelty
		self.cleanliness = cleanliness

	def __repr__(self):
		return '<Rating %r for %r>' % self.rating_id, self.bathroom_id
