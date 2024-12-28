from __future__ import annotations

import flatbuffers
import numpy as np

import flatbuffers
import typing

uoffset: typing.TypeAlias = flatbuffers.number_types.UOffsetTFlags.py_type

class TeamData(object):
  @classmethod
  def GetRootAs(cls, buf: bytes, offset: int) -> TeamData: ...
  @classmethod
  def GetRootAsTeamData(cls, buf: bytes, offset: int) -> TeamData: ...
  def Init(self, buf: bytes, pos: int) -> None: ...
  def Name(self) -> str | None: ...
  def PackageName(self) -> str | None: ...
  def TeamId(self) -> int: ...
def TeamDataStart(builder: flatbuffers.Builder) -> None: ...
def Start(builder: flatbuffers.Builder) -> None: ...
def TeamDataAddName(builder: flatbuffers.Builder, name: uoffset) -> None: ...
def TeamDataAddPackageName(builder: flatbuffers.Builder, packageName: uoffset) -> None: ...
def TeamDataAddTeamId(builder: flatbuffers.Builder, teamId: int) -> None: ...
def TeamDataEnd(builder: flatbuffers.Builder) -> uoffset: ...
def End(builder: flatbuffers.Builder) -> uoffset: ...

