"use strict";
// automatically generated by the FlatBuffers compiler, do not modify
Object.defineProperty(exports, "__esModule", { value: true });
exports.EventWrapper = void 0;
var flatbuffers = require("flatbuffers");
var event_1 = require("../../battlecode/schema/event");
/**
 * Necessary due to flatbuffers requiring unions to be wrapped in tables.
 */
var EventWrapper = /** @class */ (function () {
    function EventWrapper() {
        this.bb = null;
        this.bb_pos = 0;
    }
    EventWrapper.prototype.__init = function (i, bb) {
        this.bb_pos = i;
        this.bb = bb;
        return this;
    };
    EventWrapper.getRootAsEventWrapper = function (bb, obj) {
        return (obj || new EventWrapper()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
    };
    EventWrapper.getSizePrefixedRootAsEventWrapper = function (bb, obj) {
        bb.setPosition(bb.position() + flatbuffers.SIZE_PREFIX_LENGTH);
        return (obj || new EventWrapper()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
    };
    EventWrapper.prototype.eType = function () {
        var offset = this.bb.__offset(this.bb_pos, 4);
        return offset ? this.bb.readUint8(this.bb_pos + offset) : event_1.Event.NONE;
    };
    EventWrapper.prototype.e = function (obj) {
        var offset = this.bb.__offset(this.bb_pos, 6);
        return offset ? this.bb.__union(obj, this.bb_pos + offset) : null;
    };
    EventWrapper.startEventWrapper = function (builder) {
        builder.startObject(2);
    };
    EventWrapper.addEType = function (builder, eType) {
        builder.addFieldInt8(0, eType, event_1.Event.NONE);
    };
    EventWrapper.addE = function (builder, eOffset) {
        builder.addFieldOffset(1, eOffset, 0);
    };
    EventWrapper.endEventWrapper = function (builder) {
        var offset = builder.endObject();
        return offset;
    };
    EventWrapper.createEventWrapper = function (builder, eType, eOffset) {
        EventWrapper.startEventWrapper(builder);
        EventWrapper.addEType(builder, eType);
        EventWrapper.addE(builder, eOffset);
        return EventWrapper.endEventWrapper(builder);
    };
    return EventWrapper;
}());
exports.EventWrapper = EventWrapper;