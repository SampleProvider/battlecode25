"use strict";
// automatically generated by the FlatBuffers compiler, do not modify
Object.defineProperty(exports, "__esModule", { value: true });
exports.unionListToAction = exports.unionToAction = exports.Action = void 0;
/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any, @typescript-eslint/no-non-null-assertion */
var attack_action_1 = require("../../battlecode/schema/attack-action");
var build_action_1 = require("../../battlecode/schema/build-action");
var damage_action_1 = require("../../battlecode/schema/damage-action");
var die_exception_action_1 = require("../../battlecode/schema/die-exception-action");
var indicator_dot_action_1 = require("../../battlecode/schema/indicator-dot-action");
var indicator_line_action_1 = require("../../battlecode/schema/indicator-line-action");
var indicator_string_action_1 = require("../../battlecode/schema/indicator-string-action");
var message_action_1 = require("../../battlecode/schema/message-action");
var mop_action_1 = require("../../battlecode/schema/mop-action");
var paint_action_1 = require("../../battlecode/schema/paint-action");
var spawn_action_1 = require("../../battlecode/schema/spawn-action");
var timeline_marker_action_1 = require("../../battlecode/schema/timeline-marker-action");
var transfer_action_1 = require("../../battlecode/schema/transfer-action");
var unpaint_action_1 = require("../../battlecode/schema/unpaint-action");
var upgrade_action_1 = require("../../battlecode/schema/upgrade-action");
var Action;
(function (Action) {
    Action[Action["NONE"] = 0] = "NONE";
    Action[Action["DamageAction"] = 1] = "DamageAction";
    Action[Action["PaintAction"] = 2] = "PaintAction";
    Action[Action["UnpaintAction"] = 3] = "UnpaintAction";
    Action[Action["AttackAction"] = 4] = "AttackAction";
    Action[Action["MopAction"] = 5] = "MopAction";
    Action[Action["BuildAction"] = 6] = "BuildAction";
    Action[Action["TransferAction"] = 7] = "TransferAction";
    Action[Action["MessageAction"] = 8] = "MessageAction";
    Action[Action["SpawnAction"] = 9] = "SpawnAction";
    Action[Action["UpgradeAction"] = 10] = "UpgradeAction";
    Action[Action["DieExceptionAction"] = 11] = "DieExceptionAction";
    Action[Action["TimelineMarkerAction"] = 12] = "TimelineMarkerAction";
    Action[Action["IndicatorStringAction"] = 13] = "IndicatorStringAction";
    Action[Action["IndicatorDotAction"] = 14] = "IndicatorDotAction";
    Action[Action["IndicatorLineAction"] = 15] = "IndicatorLineAction";
})(Action || (exports.Action = Action = {}));
function unionToAction(type, accessor) {
    switch (Action[type]) {
        case 'NONE': return null;
        case 'DamageAction': return accessor(new damage_action_1.DamageAction());
        case 'PaintAction': return accessor(new paint_action_1.PaintAction());
        case 'UnpaintAction': return accessor(new unpaint_action_1.UnpaintAction());
        case 'AttackAction': return accessor(new attack_action_1.AttackAction());
        case 'MopAction': return accessor(new mop_action_1.MopAction());
        case 'BuildAction': return accessor(new build_action_1.BuildAction());
        case 'TransferAction': return accessor(new transfer_action_1.TransferAction());
        case 'MessageAction': return accessor(new message_action_1.MessageAction());
        case 'SpawnAction': return accessor(new spawn_action_1.SpawnAction());
        case 'UpgradeAction': return accessor(new upgrade_action_1.UpgradeAction());
        case 'DieExceptionAction': return accessor(new die_exception_action_1.DieExceptionAction());
        case 'TimelineMarkerAction': return accessor(new timeline_marker_action_1.TimelineMarkerAction());
        case 'IndicatorStringAction': return accessor(new indicator_string_action_1.IndicatorStringAction());
        case 'IndicatorDotAction': return accessor(new indicator_dot_action_1.IndicatorDotAction());
        case 'IndicatorLineAction': return accessor(new indicator_line_action_1.IndicatorLineAction());
        default: return null;
    }
}
exports.unionToAction = unionToAction;
function unionListToAction(type, accessor, index) {
    switch (Action[type]) {
        case 'NONE': return null;
        case 'DamageAction': return accessor(index, new damage_action_1.DamageAction());
        case 'PaintAction': return accessor(index, new paint_action_1.PaintAction());
        case 'UnpaintAction': return accessor(index, new unpaint_action_1.UnpaintAction());
        case 'AttackAction': return accessor(index, new attack_action_1.AttackAction());
        case 'MopAction': return accessor(index, new mop_action_1.MopAction());
        case 'BuildAction': return accessor(index, new build_action_1.BuildAction());
        case 'TransferAction': return accessor(index, new transfer_action_1.TransferAction());
        case 'MessageAction': return accessor(index, new message_action_1.MessageAction());
        case 'SpawnAction': return accessor(index, new spawn_action_1.SpawnAction());
        case 'UpgradeAction': return accessor(index, new upgrade_action_1.UpgradeAction());
        case 'DieExceptionAction': return accessor(index, new die_exception_action_1.DieExceptionAction());
        case 'TimelineMarkerAction': return accessor(index, new timeline_marker_action_1.TimelineMarkerAction());
        case 'IndicatorStringAction': return accessor(index, new indicator_string_action_1.IndicatorStringAction());
        case 'IndicatorDotAction': return accessor(index, new indicator_dot_action_1.IndicatorDotAction());
        case 'IndicatorLineAction': return accessor(index, new indicator_line_action_1.IndicatorLineAction());
        default: return null;
    }
}
exports.unionListToAction = unionListToAction;
