"use strict";
// automatically generated by the FlatBuffers compiler, do not modify
Object.defineProperty(exports, "__esModule", { value: true });
exports.RobotTypeMetadata = void 0;
/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any, @typescript-eslint/no-non-null-assertion */
var flatbuffers = require("flatbuffers");
var robot_type_1 = require("../../battlecode/schema/robot-type");
var RobotTypeMetadata = /** @class */ (function () {
    function RobotTypeMetadata() {
        this.bb = null;
        this.bb_pos = 0;
    }
    RobotTypeMetadata.prototype.__init = function (i, bb) {
        this.bb_pos = i;
        this.bb = bb;
        return this;
    };
    RobotTypeMetadata.getRootAsRobotTypeMetadata = function (bb, obj) {
        return (obj || new RobotTypeMetadata()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
    };
    RobotTypeMetadata.getSizePrefixedRootAsRobotTypeMetadata = function (bb, obj) {
        bb.setPosition(bb.position() + flatbuffers.SIZE_PREFIX_LENGTH);
        return (obj || new RobotTypeMetadata()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
    };
    RobotTypeMetadata.prototype.type = function () {
        var offset = this.bb.__offset(this.bb_pos, 4);
        return offset ? this.bb.readInt8(this.bb_pos + offset) : robot_type_1.RobotType.NONE;
    };
    RobotTypeMetadata.prototype.actionCooldown = function () {
        var offset = this.bb.__offset(this.bb_pos, 6);
        return offset ? this.bb.readInt32(this.bb_pos + offset) : 0;
    };
    RobotTypeMetadata.prototype.movementCooldown = function () {
        var offset = this.bb.__offset(this.bb_pos, 8);
        return offset ? this.bb.readInt32(this.bb_pos + offset) : 0;
    };
    RobotTypeMetadata.prototype.baseHealth = function () {
        var offset = this.bb.__offset(this.bb_pos, 10);
        return offset ? this.bb.readInt32(this.bb_pos + offset) : 0;
    };
    RobotTypeMetadata.prototype.actionRadiusSquared = function () {
        var offset = this.bb.__offset(this.bb_pos, 12);
        return offset ? this.bb.readInt32(this.bb_pos + offset) : 0;
    };
    RobotTypeMetadata.prototype.visionRadiusSquared = function () {
        var offset = this.bb.__offset(this.bb_pos, 14);
        return offset ? this.bb.readInt32(this.bb_pos + offset) : 0;
    };
    RobotTypeMetadata.prototype.bytecodeLimit = function () {
        var offset = this.bb.__offset(this.bb_pos, 16);
        return offset ? this.bb.readInt32(this.bb_pos + offset) : 0;
    };
    RobotTypeMetadata.startRobotTypeMetadata = function (builder) {
        builder.startObject(7);
    };
    RobotTypeMetadata.addType = function (builder, type) {
        builder.addFieldInt8(0, type, robot_type_1.RobotType.NONE);
    };
    RobotTypeMetadata.addActionCooldown = function (builder, actionCooldown) {
        builder.addFieldInt32(1, actionCooldown, 0);
    };
    RobotTypeMetadata.addMovementCooldown = function (builder, movementCooldown) {
        builder.addFieldInt32(2, movementCooldown, 0);
    };
    RobotTypeMetadata.addBaseHealth = function (builder, baseHealth) {
        builder.addFieldInt32(3, baseHealth, 0);
    };
    RobotTypeMetadata.addActionRadiusSquared = function (builder, actionRadiusSquared) {
        builder.addFieldInt32(4, actionRadiusSquared, 0);
    };
    RobotTypeMetadata.addVisionRadiusSquared = function (builder, visionRadiusSquared) {
        builder.addFieldInt32(5, visionRadiusSquared, 0);
    };
    RobotTypeMetadata.addBytecodeLimit = function (builder, bytecodeLimit) {
        builder.addFieldInt32(6, bytecodeLimit, 0);
    };
    RobotTypeMetadata.endRobotTypeMetadata = function (builder) {
        var offset = builder.endObject();
        return offset;
    };
    RobotTypeMetadata.createRobotTypeMetadata = function (builder, type, actionCooldown, movementCooldown, baseHealth, actionRadiusSquared, visionRadiusSquared, bytecodeLimit) {
        RobotTypeMetadata.startRobotTypeMetadata(builder);
        RobotTypeMetadata.addType(builder, type);
        RobotTypeMetadata.addActionCooldown(builder, actionCooldown);
        RobotTypeMetadata.addMovementCooldown(builder, movementCooldown);
        RobotTypeMetadata.addBaseHealth(builder, baseHealth);
        RobotTypeMetadata.addActionRadiusSquared(builder, actionRadiusSquared);
        RobotTypeMetadata.addVisionRadiusSquared(builder, visionRadiusSquared);
        RobotTypeMetadata.addBytecodeLimit(builder, bytecodeLimit);
        return RobotTypeMetadata.endRobotTypeMetadata(builder);
    };
    return RobotTypeMetadata;
}());
exports.RobotTypeMetadata = RobotTypeMetadata;