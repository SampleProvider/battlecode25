from __future__ import annotations

import flatbuffers
import numpy as np

import flatbuffers
import typing
from ..schema.EventWrapper import EventWrapper

uoffset: typing.TypeAlias = flatbuffers.number_types.UOffsetTFlags.py_type

class GameWrapper(object):
  @classmethod
  def GetRootAs(cls, buf: bytes, offset: int) -> GameWrapper: ...
  @classmethod
  def GetRootAsGameWrapper(cls, buf: bytes, offset: int) -> GameWrapper: ...
  def Init(self, buf: bytes, pos: int) -> None: ...
  def Events(self, i: int) -> EventWrapper | None: ...
  def EventsLength(self) -> int: ...
  def EventsIsNone(self) -> bool: ...
  def MatchHeaders(self, i: int) -> typing.List[int]: ...
  def MatchHeadersAsNumpy(self) -> np.ndarray: ...
  def MatchHeadersLength(self) -> int: ...
  def MatchHeadersIsNone(self) -> bool: ...
  def MatchFooters(self, i: int) -> typing.List[int]: ...
  def MatchFootersAsNumpy(self) -> np.ndarray: ...
  def MatchFootersLength(self) -> int: ...
  def MatchFootersIsNone(self) -> bool: ...
def GameWrapperStart(builder: flatbuffers.Builder) -> None: ...
def Start(builder: flatbuffers.Builder) -> None: ...
def GameWrapperAddEvents(builder: flatbuffers.Builder, events: uoffset) -> None: ...
def GameWrapperStartEventsVector(builder: flatbuffers.Builder, num_elems: int) -> uoffset: ...
def StartEventsVector(builder: flatbuffers.Builder, num_elems: int) -> uoffset: ...
def GameWrapperAddMatchHeaders(builder: flatbuffers.Builder, matchHeaders: uoffset) -> None: ...
def GameWrapperStartMatchHeadersVector(builder: flatbuffers.Builder, num_elems: int) -> uoffset: ...
def StartMatchHeadersVector(builder: flatbuffers.Builder, num_elems: int) -> uoffset: ...
def GameWrapperAddMatchFooters(builder: flatbuffers.Builder, matchFooters: uoffset) -> None: ...
def GameWrapperStartMatchFootersVector(builder: flatbuffers.Builder, num_elems: int) -> uoffset: ...
def StartMatchFootersVector(builder: flatbuffers.Builder, num_elems: int) -> uoffset: ...
def GameWrapperEnd(builder: flatbuffers.Builder) -> uoffset: ...
def End(builder: flatbuffers.Builder) -> uoffset: ...
