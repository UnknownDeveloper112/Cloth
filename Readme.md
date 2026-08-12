# Cloth

A 2D cloth/fabric physics simulation built in Java with Swing, using Verlet integration and constraint relaxation.

![Cloth simulation preview](Cloth.gif)

## Overview

Points are arranged in a grid and connected by sticks that resist stretching. Every frame, gravity and a periodic gust of wind are applied to each unfixed point, positions are integrated using Verlet integration, and the stick network is relaxed over many iterations to keep the cloth's shape intact. Two top points are pinned in place so the rest of the sheet hangs and sways under gravity and wind.

## Features

- **Verlet integration** — position-based physics with implicit velocity (`v = x - x_old`)
- **Two constraint layers**
    - *Structural sticks* — horizontal/vertical neighbors, resist stretching, drawn on screen
    - *Diagonal sticks* — cross-bracing between diagonal neighbors, very low stiffness, prevents the mesh from shearing into a parallelogram without stiffening the whole cloth
- **Periodic wind gusts** — wind strength ramps up and down on a repeating cycle rather than blowing constantly, with a bit of randomness layered on top
- **Fixed top pins** — two points along the top edge stay fixed, letting the rest of the sheet hang and billow
- **Configurable rendering** — points can optionally be drawn as circles (`ENLARGE_POINTS`); sticks are always drawn as lines

## Project structure

| File | Responsibility |
|---|---|
| `Main.java` | Application entry point, creates the window (`JFrame`) |
| `Drawer.java` | Simulation loop, grid setup, constraint solving, rendering, wind |
| `Point.java` | A single mass point: position, previous position, velocity, fixed state |
| `Stick.java` | A constraint between two points: rest length, stiffness, relaxation |

## How it works

1. **Grid setup** (`init`, runs once on the first paint)
    - Points laid out in a `ROWS × COLUMNS` grid, spaced `LENGTH` pixels apart
    - Structural sticks connect each point to its right and lower neighbor
    - Diagonal sticks cross-connect each 2×2 block of points in both diagonal directions
    - The first point and the last point of the top row (`points[0]`, `points[COLUMNS-1]`) are pinned
2. **Per-frame update** (inside `paintComponent`, driven by repeated `repaint()` calls)
    - Derive velocity from the previous frame's displacement (Verlet)
    - Apply wind (`wind(COUNT)`) and gravity to velocity, then damp with friction
    - Integrate position (`x += vx`, `y += vy`)
    - Relax all structural sticks over 100 iterations, then all diagonal sticks over 100 iterations
    - Sleep `DELAY` ms, then request the next frame
3. **Wind function** (`wind`) — produces a slow ramping gust roughly once every 4 cycles of `COUNT % 200`, rather than constant wind; see [Wind behavior](#wind-behavior) below.

## Wind behavior

```java
public static double wind(int COUNT){
    double base = MAX_WIND * (COUNT % 200) / 200;
    if (!((COUNT / 200) % 4 >= 3)) base = 0;
    return base + ((1 - 2 * Math.random()) * (COUNT % 200) / 2000);
}
```

- `COUNT % 200` creates a 200-frame cycle; wind ramps linearly from `0` to `MAX_WIND` across that cycle.
- `(COUNT / 200) % 4 >= 3` only lets the ramp through on 1 out of every 4 cycles — so wind blows in gusts separated by calmer stretches, rather than continuously.
- The final term adds small random jitter that grows alongside the gust, so stronger gusts are also slightly noisier.

## Configuration

Key constants live at the top of `Drawer.java`:

```java
static final double FRICTION = 0.99;
static final double GRAVITY = 0.25;
static final double MAX_WIND = 0.15;
static final int COLUMNS = 25;
static final int ROWS = 30;
static final int LENGTH = 20;             
static final double STICK_STIFFNESS = 0.5;
static final double DIAGONAL_STIFFNESS = 0.002;
static final int DELAY = 5;               
static final boolean ENLARGE_POINTS = false;
```

Tuning notes:
- `STICK_STIFFNESS` closer to `1.0` - less stretchy, more rigid cloth.
- `DIAGONAL_STIFFNESS` is intentionally tiny as it only needs to resist shear, not dominate the structural sticks. Raising it noticeably stiffens the whole mesh and makes it less cloth-like.
- Lower `DELAY` - faster simulation speed (more frames per second, not physically scaled).
- `ROWS`/`COLUMNS`/`LENGTH` control the size of the cloth in points and pixels, the two fixed points (`points[0]`, `points[COLUMNS-1]`) are always the first and last of the top row regardless of grid size.


## Known limitations / ideas for improvement

- Physics runs inside `paintComponent` itself (using `repaint()` recursively with `Thread.sleep`), rather than a separate timer/thread — this couples simulation speed to rendering and can be triggered by unrelated repaint events (e.g. window resize)
- No delta-time scaling — simulation speed is tied to `DELAY`, not real elapsed time
- No mouse interaction — cloth cannot be grabbed, dragged, or poked
- Wind blows along a single axis; no vertical or gradually-rotating wind direction