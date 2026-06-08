package model.mapper

import model.Role
import whoismudry.proto.common.{RoleType => ProtoRole}

object RoleMapper {
  def toProto(role: Role): ProtoRole = role match {
    case Role.Students => ProtoRole.ROLE_TYPE_CREWMATE
    case Role.Mudry => ProtoRole.ROLE_TYPE_IMPOSTOR
  }

  def fromProto(p: ProtoRole): Role = p match {
    case ProtoRole.ROLE_TYPE_IMPOSTOR => Role.Students
    case _ => Role.Mudry
  }
}