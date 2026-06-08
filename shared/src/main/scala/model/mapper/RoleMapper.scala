package model.mapper

import model.Role
import whoismudry.proto.common.{RoleType => ProtoRole}

object RoleMapper {
  def toProto(role: Role): ProtoRole = role match {
    case Role.Students => ProtoRole.ROLE_TYPE_STUDENTS
    case Role.Mudry => ProtoRole.ROLE_TYPE_MUDRY
  }

  def fromProto(p: ProtoRole): Role = p match {
    case ProtoRole.ROLE_TYPE_MUDRY => Role.Mudry
    case _ => Role.Students
  }
}