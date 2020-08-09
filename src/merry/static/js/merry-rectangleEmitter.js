var rectangleEmitter = {
	/**
	 * The canvas object
	 */
	canvas: null,

	/**
	 * CanvasContext  The canvas context object
	 */
	context: null,

	/**
	 * Object The blast zone for particles.
	 */
	blastZone: {
		x: 0,
		y: 0,
		width: 800,
		height: 600
	},

	/**
	 * Particle The type of particle to create.
	 */
	particle: null,

	/**
	 * array The list of particles in the emitter.
	 */
	particles: [],

	/**
	 * The max number of particles.
	 */
	maxParticles: 700,

	/**
	 * The intervalID for the FPS interval
	 */
	fpsId: null,

	/**
	 * The interval ID for the seconds tick.
	 */
	tickId: null,

	/**
	 * Sets the canvas object.
	 *
	 * @param canvas DOMCanvasElement  The canvas to draw on.
	 */
	setCanvas: function(canvas){
		this.canvas = canvas;
		this.context = canvas.getContext('2d');
	},

	/**
	 * Sets the blast zone.
	 *
	 * @param x      int  The x coord
	 * @param y      int  The y-coor
	 * @param width  int  The width
	 * @param height int  The height
	 */
	setBlastZone: function(x, y, width, height){
		this.blastZone = {
			'x': x,
			'y': y,
			'width': width,
			'height': height
		};
	},

	/** Starts the emitter.
	 *
	 * @param	fps	The frame rate or 30 by default
	 */
	start:function(fps){
		var rate = fps || 30;
		this.fpsId = setInterval(this.frameUpdate, 1000/rate, this); // Framerate update
		this.tickId = setInterval(this.tick, 1000, this); // Every second tick...
	},

	/**
	 * Pauses the emitter but doesn't clear the screen.
	 */
	pause:function(){
		clearInterval(this.intervalId);
	},

	/**
	 * Stops the emitter and clears the screen.
	 */
	stop:function(){
		clearInterval(this.intervalId);
		this.clear();
	},

	/**
	 * Clears off the particles.
	 */
	clear:function(){
		this.context.clearRect(0, 0, this.canvas.width, this.canvas.height);
	},

	/**
	 * Adds a particle to the screen.
	 *
	 * @param	particle	The particle to add
	 */
	addParticle:function(particle){
		if (this.particles.length < this.maxParticles){
			var p = Object.create(particle);
			p.randomize(this.blastZone);

			// Add the particle
			this.particles.push(p);
		}
	},

	/**
	 * Draws the whole canvas.
	 */
	draw:function(){
		this.clear();

		var i = this.particles.length;
		while (i--){
			this.particles[i].draw(this.context);
		}
	},

	/**
	 * Updates the particles on the screen.
	 */
	update:function(){
		var p; 
		var i = this.particles.length;
		
		while(i--){
			p = this.particles[i];
			p.update();

			// Remove the particle if it is "dead"
			if (p.y > this.canvas.height){
				this.particles.splice(i, 1);
			}
		}
	},

	/**
	 * Applies actions to all of the particles.
	 */
	applyActions:function(){
		var i = this.particles.length;
		
		while(i--){
			this.particles[i].action();
		}
	},

	/**
	 * Run the action ahead the number of seconds (so the screen isn't blank on init).
	 *
	 * @param seconds int  The number of seconds to run ahead.
	 */
	runAhead: function(seconds){
		for (i = 0; i < seconds; i += 1){
			this.frameUpdate(this);
		}
	},

	/**
	 * The FPS update
	 *
	 * @param	self	The reference to the emitter that is lost during setInterval.
	 */
	frameUpdate:function(self){
		self.addParticle(self.particle);
		self.update();
		self.draw();
	},

	/**
	 * The seconds "tick" interval
	 *
	 * @param	self	The reference to the emitter that is lost during setInterval.
	 */
	tick:function(self){
		self.applyActions();
	}

};
