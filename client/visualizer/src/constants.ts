import { schema } from 'battlecode-playback'
import { Symmetry } from './mapeditor/index'

// Body types
export const HEADQUARTERS = schema.BodyType.HEADQUARTERS
export const CARRIER = schema.BodyType.CARRIER
export const LAUNCHER = schema.BodyType.LAUNCHER
export const AMPLIFIER = schema.BodyType.AMPLIFIER
export const DESTABILIZER = schema.BodyType.DESTABILIZER
export const BOOSTER = schema.BodyType.BOOSTER

export const bodyTypeList: number[] = [HEADQUARTERS, CARRIER, LAUNCHER, AMPLIFIER, DESTABILIZER, BOOSTER]
export const buildingTypeList: number[] = [HEADQUARTERS]
export const initialBodyTypeList: number[] = [HEADQUARTERS]

export const RESOURCENAMES = { 0: "None", 1: "Adamantium", 2: "Mana", 3: "Elixir" }
export const ADAMANTIUM = 1
export const ELIXIR = 3
export const MANA = 2

export const DIRECTIONS: Record<number, Array<number>> = {
  0: [0, 0],
  1: [-1, 0],
  2: [-1, -1],
  3: [0, -1],
  4: [1, -1],
  5: [1, 0],
  6: [1, 1],
  7: [0, 1],
  8: [-1, 1]
}
export function INVDIRECTIONS(dir: { x: number, y: number }): number {
  for (let k in DIRECTIONS) {
    if (DIRECTIONS[k][0] == dir.x && DIRECTIONS[k][1] == dir.y) {
      return parseInt(k)
    }
  }
  return 0;
}

export const bodyTypePriority: number[] = [] // for guns, drones, etc. that should be drawn over other robots

// export const TILE_COLORS: Array<number>[] = [
//   [168, 137, 97],
//   [147, 117, 77],
//   [88, 129, 87],
//   [58, 90, 64],
//   [52, 78, 65],
//   [11, 32, 39],
//   [8, 20, 20]
// ]

export const TILE_COLORS: Array<number>[] = [
  [204, 191, 173],
  // [191, 179, 163],
  // [184, 169, 151],
  // [171, 157, 138],
  // [161, 146, 127],
  // [156, 143, 126],
  // [145, 130, 110],
  // [130, 117, 100],
  // [122, 109, 91],
  // [115, 102, 85],
  [102, 92, 75]
]
// flashy colors
// [0, 147, 83], // turquoise
// [29, 201, 2], // green
// [254, 205, 54], // yellow
// [222, 145, 1], // brown
// [255, 0, 0], // red
// [242, 0, 252] // pink

// Given passability, get index of tile to use.
// export const getLevel = (x: number): number => {
//   const nLev = TILE_COLORS.length
//   const level = Math.floor((x + 9) / 10)
//   return Math.min(nLev - 1, Math.max(0, level))
// }

// export const passiveInfluenceRate = (round: number): number => {
//   //return Math.floor((1/50 + 0.03 * Math.exp(-0.001 * x)) * x); this one's for slanderers
//   return Math.ceil(0.2 * Math.sqrt(round))
// }

// export const buffFactor = (numBuffs: number): number => {
//   return 1 + 0.001 * numBuffs
// }

// Expected bot image size
//export const IMAGE_SIZE = 25

export function bodyTypeToSize(bodyType: schema.BodyType) {
  switch (bodyType) {
    case HEADQUARTERS:
      return 64
    case CARRIER:
      return 64
    case LAUNCHER:
      return 64
    case AMPLIFIER:
      return 64
    case DESTABILIZER:
      return 64
    case BOOSTER:
      return 64
    default: throw new Error("invalid body type")
  }
}

// Game canvas rendering sizes
export const INDICATOR_DOT_SIZE = .3
export const INDICATOR_LINE_WIDTH = .3
export const SIGHT_RADIUS_LINE_WIDTH = .1

export const TEAM_BLUE = "#407496"
export const TEAM_RED = "#D53E43"
export const TEAM_COLORS = [TEAM_RED, TEAM_BLUE]
export const TEAM_NAMES = ["Red", "Blue"]
export const UI_BLUE = "#3C5EBB"
export const UI_GREEN = "#58BA4F"
export const UI_GREEN_DARK = "#4A8058"
export const UI_GREY = "#253B49"
export const ACTION_RADIUS_COLOR = UI_GREEN
export const VISION_RADIUS_COLOR = UI_BLUE

// Game canvas rendering parameters
export const EFFECT_STEP = 200 //time change between effect animations

// Map editor canvas parameters
export const DELTA = .0001
export const MIN_DIMENSION = 20
export const MAX_DIMENSION = 60

// Initial (default) HP of archons, for map editor
export const INITIAL_HP = 100

// Server settings
export const NUMBER_OF_TEAMS = 2
// export const VICTORY_POINT_THRESH = 1000;

// Other constants
// export const BULLET_THRESH = 10000;

// Maps available in the server.
// The key is the map name and the value is the type
export enum MapType {
  DEFAULT,
  SPRINT_1,
  SPRINT_2,
  INTL_QUALIFYING,
  US_QUALIFYING,
  HS_NEWBIE,
  FINAL,
  CUSTOM
};

export const SERVER_MAPS: Map<string, MapType> = new Map<string, MapType>([
  ["AllElements", MapType.DEFAULT],
  ["DefaultMap", MapType.DEFAULT],
  ["maptestsmall", MapType.DEFAULT],
  ["SmallElements", MapType.DEFAULT],
  ["ArtistRendition", MapType.SPRINT_1],
  ["BatSignal", MapType.SPRINT_1],
  ["BowAndArrow", MapType.SPRINT_1],
  ["Cat", MapType.SPRINT_1],
  ["Clown", MapType.SPRINT_1],
  ["Diagonal", MapType.SPRINT_1],
  ["Eyelands", MapType.SPRINT_1],
  ["Frog", MapType.SPRINT_1],
  ["Grievance", MapType.SPRINT_1],
  ["Hah", MapType.SPRINT_1],
  ["Jail", MapType.SPRINT_1],
  ["KingdomRush", MapType.SPRINT_1],
  ["Minefield", MapType.SPRINT_1],
  ["Movepls", MapType.SPRINT_1],
  ["Orbit", MapType.SPRINT_1],
  ["Pathfind", MapType.SPRINT_1],
  ["Pit", MapType.SPRINT_1],
  ["Pizza", MapType.SPRINT_1],
  ["Quiet", MapType.SPRINT_1],
  ["Rectangle", MapType.SPRINT_1],
  ["Scatter", MapType.SPRINT_1],
  ["Sun", MapType.SPRINT_1],
  ["Tacocat", MapType.SPRINT_1],
  ["Turtle", MapType.DEFAULT],
  ["Dreamy", MapType.DEFAULT],
  ["Forest", MapType.DEFAULT],
  ["PairedProgramming", MapType.DEFAULT],
  ["Rewind", MapType.DEFAULT],
  ["BattleSuns", MapType.SPRINT_2],
  ["Checkmate2", MapType.SPRINT_2],
  ["Cornucopia", MapType.SPRINT_2],
  ["Crossword", MapType.SPRINT_2],
  ["Cube", MapType.SPRINT_2],
  ["Divergence", MapType.SPRINT_2],
  ["FourNations", MapType.SPRINT_2],
  ["HideAndSeek", MapType.SPRINT_2],
  ["Lantern", MapType.SPRINT_2],
  ["Lines", MapType.SPRINT_2],
  ["Maze", MapType.SPRINT_2],
  ["Pakbot", MapType.SPRINT_2],
  ["Piglets", MapType.SPRINT_2],
  ["Risk", MapType.SPRINT_2],
  ["Sine", MapType.SPRINT_2],
  ["Snowflake", MapType.SPRINT_2],
  ["SomethingFishy", MapType.SPRINT_2],
  ["Spin", MapType.SPRINT_2],
  ["Spiral", MapType.SPRINT_2],
  ["Squares", MapType.SPRINT_2],
  ["Star", MapType.SPRINT_2],
  ["Sus", MapType.SPRINT_2],
  ["SweetDreams", MapType.SPRINT_2],
  ["TicTacToe", MapType.SPRINT_2],
  ["USA", MapType.SPRINT_2],
  ["Barcode", MapType.INTL_QUALIFYING],
  ["Contraction", MapType.INTL_QUALIFYING],
  ["Flower", MapType.INTL_QUALIFYING],
  ["Grapes", MapType.INTL_QUALIFYING],
  ["IslandHopping", MapType.INTL_QUALIFYING],
  ["Marsh", MapType.INTL_QUALIFYING],
  ["RaceToTheTop", MapType.INTL_QUALIFYING],
  ["Repetition", MapType.INTL_QUALIFYING],
  ["River", MapType.INTL_QUALIFYING],
  ["RockWall", MapType.INTL_QUALIFYING],
  ["Sakura", MapType.INTL_QUALIFYING],
  ["SoundWave", MapType.INTL_QUALIFYING],
  ["ThirtyFive", MapType.INTL_QUALIFYING],
  ["TimesUp", MapType.INTL_QUALIFYING],
  ["TreasureMap", MapType.INTL_QUALIFYING],
  ["AbsoluteW", MapType.US_QUALIFYING],
  ["Buggy", MapType.US_QUALIFYING],
  ["Cave", MapType.US_QUALIFYING],
  ["Cee", MapType.US_QUALIFYING],
  ["Heart", MapType.US_QUALIFYING],
  ["HotAirBalloon", MapType.US_QUALIFYING],
  ["IslandHoppingTwo", MapType.US_QUALIFYING],
  ["LightWork", MapType.US_QUALIFYING],
  ["MassiveL", MapType.US_QUALIFYING],
  ["Potions", MapType.US_QUALIFYING],
  ["Rainbow", MapType.US_QUALIFYING],
  ["Resign", MapType.US_QUALIFYING],
  ["Sneaky", MapType.US_QUALIFYING],
  ["Target", MapType.US_QUALIFYING],
  ["Tightrope", MapType.US_QUALIFYING],
  ["BuildSite", MapType.HS_NEWBIE], 
  ["CrownJewels", MapType.HS_NEWBIE], 
  ["Elephant", MapType.HS_NEWBIE], 
  ["ExtremelyMid", MapType.HS_NEWBIE], 
  ["FishCake", MapType.HS_NEWBIE], 
  ["Fractured", MapType.HS_NEWBIE], 
  ["LookingGlass", MapType.HS_NEWBIE], 
  ["MoonPhases", MapType.HS_NEWBIE], 
  ["Pillars", MapType.HS_NEWBIE], 
  ["PipesAndParabolas", MapType.HS_NEWBIE], 
  ["ReverseFunnel", MapType.HS_NEWBIE], 
  ["Spiderweb", MapType.HS_NEWBIE], 
  ["Spots", MapType.HS_NEWBIE], 
  ["Swooshy", MapType.HS_NEWBIE], 
  ["VeryReasonable", MapType.HS_NEWBIE], 
  ["Zig", MapType.HS_NEWBIE]
])

export function bodyTypeToString(bodyType: schema.BodyType) {
  switch (bodyType) {
    case HEADQUARTERS:
      return "headquarters"
    case CARRIER:
      return "carrier"
    case LAUNCHER:
      return "launcher"
    case AMPLIFIER:
      return "amplifier"
    case DESTABILIZER:
      return "destabilizer"
    case BOOSTER:
      return "booster"
    default: throw new Error("invalid body type")
  }
}

export function symmetryToString(symmetry: Symmetry) {
  switch (symmetry) {
    case Symmetry.ROTATIONAL: return "Rotational"
    case Symmetry.HORIZONTAL: return "Horizontal"
    case Symmetry.VERTICAL: return "Vertical"
    default: throw new Error("invalid symmetry")
  }
}

export function anomalyToString(anomaly: schema.Action) {
  switch (anomaly) {
    case 3:
      return "vortex"
    case 2:
      return "fury"
    case 0:
      return "abyss"
    case 1:
      return "charge"
    default: throw new Error("invalid anomaly")
  }
}

export function abilityToEffectString(effect: number): string | null {
  switch (effect) {
    case 1:
      return "empower"
    case 2:
      return "expose"
    case 3:
      return "embezzle"
    case 4:
      return "camouflage_red"
    case 5:
      return "camouflage_blue"
    default:
      return null
  }
}

// TODO: fix radius (is this vision that can be toggled in sidebar?)
export function radiusFromBodyType(bodyType: schema.BodyType) {
  return -1
  // switch(bodyType) {
  //   case MINER:
  //   case LANDSCAPER:
  //   case DRONE:
  //   case NET_GUN:
  //   case COW:
  //   case REFINERY:
  //   case VAPORATOR:
  //   case HQ:
  //   case DESIGN_SCHOOL:
  //   case FULFILLMENT_CENTER: return 1;
  //   default: throw new Error("invalid body type");
  // }
}

// export function waterLevel(x: number) {
//   return (Math.exp(0.0028*x-1.38*Math.sin(0.00157*x-1.73)+1.38*Math.sin(-1.73))-1)
// }
