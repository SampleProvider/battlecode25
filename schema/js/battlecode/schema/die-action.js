"use strict";
// automatically generated by the FlatBuffers compiler, do not modify
Object.defineProperty(exports, "__esModule", { value: true });
exports.DieAction = void 0;
/**
 * Indicates that a robot died and should be removed
 */
var DieAction = /** @class */ (function () {
    function DieAction() {
        this.bb = null;
        this.bb_pos = 0;
    }
    DieAction.prototype.__init = function (i, bb) {
        this.bb_pos = i;
        this.bb = bb;
        return this;
    };
    /**
     * Id of the robot that died
     */
    DieAction.prototype.id = function () {
        return this.bb.readUint16(this.bb_pos);
    };
    DieAction.prototype.dieType = function () {
        return this.bb.readInt8(this.bb_pos + 2);
    };
    DieAction.sizeOf = function () {
        return 4;
    };
    DieAction.createDieAction = function (builder, id, dieType) {
        builder.prep(2, 4);
        builder.pad(1);
        builder.writeInt8(dieType);
        builder.writeInt16(id);
        return builder.offset();
    };
    return DieAction;
}());
exports.DieAction = DieAction;