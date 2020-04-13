# FSAE Lap Sim

This is a steady state vehicle dynamics lap simulation. It allows the parameters of the car to be modified
and a custom track to be input. It also allows for sensitivity analysis to be run. 

Made 4/13/2020 by Bryce Cavey
If you have any questions text or call me at 610-973-9485

## Features
-Longitudinal weight transfer
-Lateral weight trasnfer
-Non-constant friction tire model (But not full Pacejka)
-4 wheel cornering model
-bicylcle model for straights
-shift detection
-aerodynamic effects

## Assumptions
-The vehicle is always operating at its limit
-The car will not output more power than it can put down (so it basically assumes traction control)
-The camber angle of the tire is always 0 degrees

## License
[MIT](https://choosealicense.com/licenses/mit/)