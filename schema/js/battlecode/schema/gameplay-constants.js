"use strict";
// automatically generated by the FlatBuffers compiler, do not modify
Object.defineProperty(exports, "__esModule", { value: true });
exports.GameplayConstants = void 0;
/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any, @typescript-eslint/no-non-null-assertion */
var flatbuffers = require("flatbuffers");
var GameplayConstants = /** @class */ (function () {
    function GameplayConstants() {
        this.bb = null;
        this.bb_pos = 0;
    }
    GameplayConstants.prototype.__init = function (i, bb) {
        this.bb_pos = i;
        this.bb = bb;
        return this;
    };
    GameplayConstants.getRootAsGameplayConstants = function (bb, obj) {
        return (obj || new GameplayConstants()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
    };
    GameplayConstants.getSizePrefixedRootAsGameplayConstants = function (bb, obj) {
        bb.setPosition(bb.position() + flatbuffers.SIZE_PREFIX_LENGTH);
        return (obj || new GameplayConstants()).__init(bb.readInt32(bb.position()) + bb.position(), bb);
    };
    GameplayConstants.startGameplayConstants = function (builder) {
        builder.startObject(0);
    };
    GameplayConstants.endGameplayConstants = function (builder) {
        var offset = builder.endObject();
        return offset;
    };
    GameplayConstants.createGameplayConstants = function (builder) {
        GameplayConstants.startGameplayConstants(builder);
        return GameplayConstants.endGameplayConstants(builder);
    };
    return GameplayConstants;
}());
exports.GameplayConstants = GameplayConstants;
