"use strict";
// automatically generated by the FlatBuffers compiler, do not modify
Object.defineProperty(exports, "__esModule", { value: true });
exports.PaintAction = void 0;
/**
 * Visually indicate a tile has been painted
 */
var PaintAction = /** @class */ (function () {
    function PaintAction() {
        this.bb = null;
        this.bb_pos = 0;
    }
    PaintAction.prototype.__init = function (i, bb) {
        this.bb_pos = i;
        this.bb = bb;
        return this;
    };
    PaintAction.prototype.loc = function () {
        return this.bb.readUint16(this.bb_pos);
    };
    PaintAction.prototype.isSecondary = function () {
        return this.bb.readInt8(this.bb_pos + 2);
    };
    PaintAction.sizeOf = function () {
        return 4;
    };
    PaintAction.createPaintAction = function (builder, loc, isSecondary) {
        builder.prep(2, 4);
        builder.pad(1);
        builder.writeInt8(isSecondary);
        builder.writeInt16(loc);
        return builder.offset();
    };
    return PaintAction;
}());
exports.PaintAction = PaintAction;
