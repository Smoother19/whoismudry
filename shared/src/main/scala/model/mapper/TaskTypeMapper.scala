package model.mapper

import model.TaskType
import model.tasks._
import whoismudry.proto.common.{TaskType => ProtoTaskType}

object TaskTypeMapper {
  def toProto(task: TaskType): ProtoTaskType = task match {
    case AdminCard => ProtoTaskType.TASK_TYPE_ADMIN_CARD
    case PrierMudry   => ProtoTaskType.TASK_TYPE_PRIERMUDRY
    case Question  => ProtoTaskType.TASK_TYPE_QUESTION
  }

  def fromProto(proto: ProtoTaskType): Option[TaskType] = proto match {
    case ProtoTaskType.TASK_TYPE_ADMIN_CARD => Some(AdminCard)
    case ProtoTaskType.TASK_TYPE_PRIERMUDRY   => Some(PrierMudry)
    case ProtoTaskType.TASK_TYPE_QUESTION   => Some(Question)
    case _ => None
  }
}