"use strict";
// automatically generated by the FlatBuffers compiler, do not modify
Object.defineProperty(exports, "__esModule", { value: true });
exports.WinType = exports.VecTable = exports.Vec = exports.UpgradeAction = exports.UnpaintAction = exports.Turn = exports.TransferAction = exports.TeamData = exports.SpawnAction = exports.Round = exports.RobotType = exports.ProfilerProfile = exports.ProfilerFile = exports.ProfilerEvent = exports.PaintAction = exports.MopAction = exports.MessageAction = exports.MatchHeader = exports.MatchFooter = exports.InitialBodyTable = exports.IndicatorStringAction = exports.IndicatorLineAction = exports.IndicatorDotAction = exports.GameplayConstants = exports.GameWrapper = exports.GameMap = exports.GameHeader = exports.GameFooter = exports.EventWrapper = exports.Event = exports.DamageAction = exports.BuildAction = exports.AttackAction = exports.Action = void 0;
var action_1 = require("./schema/action");
Object.defineProperty(exports, "Action", { enumerable: true, get: function () { return action_1.Action; } });
var attack_action_1 = require("./schema/attack-action");
Object.defineProperty(exports, "AttackAction", { enumerable: true, get: function () { return attack_action_1.AttackAction; } });
var build_action_1 = require("./schema/build-action");
Object.defineProperty(exports, "BuildAction", { enumerable: true, get: function () { return build_action_1.BuildAction; } });
var damage_action_1 = require("./schema/damage-action");
Object.defineProperty(exports, "DamageAction", { enumerable: true, get: function () { return damage_action_1.DamageAction; } });
var event_1 = require("./schema/event");
Object.defineProperty(exports, "Event", { enumerable: true, get: function () { return event_1.Event; } });
var event_wrapper_1 = require("./schema/event-wrapper");
Object.defineProperty(exports, "EventWrapper", { enumerable: true, get: function () { return event_wrapper_1.EventWrapper; } });
var game_footer_1 = require("./schema/game-footer");
Object.defineProperty(exports, "GameFooter", { enumerable: true, get: function () { return game_footer_1.GameFooter; } });
var game_header_1 = require("./schema/game-header");
Object.defineProperty(exports, "GameHeader", { enumerable: true, get: function () { return game_header_1.GameHeader; } });
var game_map_1 = require("./schema/game-map");
Object.defineProperty(exports, "GameMap", { enumerable: true, get: function () { return game_map_1.GameMap; } });
var game_wrapper_1 = require("./schema/game-wrapper");
Object.defineProperty(exports, "GameWrapper", { enumerable: true, get: function () { return game_wrapper_1.GameWrapper; } });
var gameplay_constants_1 = require("./schema/gameplay-constants");
Object.defineProperty(exports, "GameplayConstants", { enumerable: true, get: function () { return gameplay_constants_1.GameplayConstants; } });
var indicator_dot_action_1 = require("./schema/indicator-dot-action");
Object.defineProperty(exports, "IndicatorDotAction", { enumerable: true, get: function () { return indicator_dot_action_1.IndicatorDotAction; } });
var indicator_line_action_1 = require("./schema/indicator-line-action");
Object.defineProperty(exports, "IndicatorLineAction", { enumerable: true, get: function () { return indicator_line_action_1.IndicatorLineAction; } });
var indicator_string_action_1 = require("./schema/indicator-string-action");
Object.defineProperty(exports, "IndicatorStringAction", { enumerable: true, get: function () { return indicator_string_action_1.IndicatorStringAction; } });
var initial_body_table_1 = require("./schema/initial-body-table");
Object.defineProperty(exports, "InitialBodyTable", { enumerable: true, get: function () { return initial_body_table_1.InitialBodyTable; } });
var match_footer_1 = require("./schema/match-footer");
Object.defineProperty(exports, "MatchFooter", { enumerable: true, get: function () { return match_footer_1.MatchFooter; } });
var match_header_1 = require("./schema/match-header");
Object.defineProperty(exports, "MatchHeader", { enumerable: true, get: function () { return match_header_1.MatchHeader; } });
var message_action_1 = require("./schema/message-action");
Object.defineProperty(exports, "MessageAction", { enumerable: true, get: function () { return message_action_1.MessageAction; } });
var mop_action_1 = require("./schema/mop-action");
Object.defineProperty(exports, "MopAction", { enumerable: true, get: function () { return mop_action_1.MopAction; } });
var paint_action_1 = require("./schema/paint-action");
Object.defineProperty(exports, "PaintAction", { enumerable: true, get: function () { return paint_action_1.PaintAction; } });
var profiler_event_1 = require("./schema/profiler-event");
Object.defineProperty(exports, "ProfilerEvent", { enumerable: true, get: function () { return profiler_event_1.ProfilerEvent; } });
var profiler_file_1 = require("./schema/profiler-file");
Object.defineProperty(exports, "ProfilerFile", { enumerable: true, get: function () { return profiler_file_1.ProfilerFile; } });
var profiler_profile_1 = require("./schema/profiler-profile");
Object.defineProperty(exports, "ProfilerProfile", { enumerable: true, get: function () { return profiler_profile_1.ProfilerProfile; } });
var robot_type_1 = require("./schema/robot-type");
Object.defineProperty(exports, "RobotType", { enumerable: true, get: function () { return robot_type_1.RobotType; } });
var round_1 = require("./schema/round");
Object.defineProperty(exports, "Round", { enumerable: true, get: function () { return round_1.Round; } });
var spawn_action_1 = require("./schema/spawn-action");
Object.defineProperty(exports, "SpawnAction", { enumerable: true, get: function () { return spawn_action_1.SpawnAction; } });
var team_data_1 = require("./schema/team-data");
Object.defineProperty(exports, "TeamData", { enumerable: true, get: function () { return team_data_1.TeamData; } });
var transfer_action_1 = require("./schema/transfer-action");
Object.defineProperty(exports, "TransferAction", { enumerable: true, get: function () { return transfer_action_1.TransferAction; } });
var turn_1 = require("./schema/turn");
Object.defineProperty(exports, "Turn", { enumerable: true, get: function () { return turn_1.Turn; } });
var unpaint_action_1 = require("./schema/unpaint-action");
Object.defineProperty(exports, "UnpaintAction", { enumerable: true, get: function () { return unpaint_action_1.UnpaintAction; } });
var upgrade_action_1 = require("./schema/upgrade-action");
Object.defineProperty(exports, "UpgradeAction", { enumerable: true, get: function () { return upgrade_action_1.UpgradeAction; } });
var vec_1 = require("./schema/vec");
Object.defineProperty(exports, "Vec", { enumerable: true, get: function () { return vec_1.Vec; } });
var vec_table_1 = require("./schema/vec-table");
Object.defineProperty(exports, "VecTable", { enumerable: true, get: function () { return vec_table_1.VecTable; } });
var win_type_1 = require("./schema/win-type");
Object.defineProperty(exports, "WinType", { enumerable: true, get: function () { return win_type_1.WinType; } });
