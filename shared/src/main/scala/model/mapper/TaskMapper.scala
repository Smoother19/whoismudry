package model.mapper

import model.{Task, Vec2}
import whoismudry.proto.common.{TaskState => ProtoTaskState}

object TaskMapper {
  def fromProto(proto: ProtoTaskState): Option[Task] =
    TaskTypeMapper.fromProto(proto.taskType).map { taskType =>
      Task(proto.id, proto.position.map(Vec2Mapper.fromProto).getOrElse(Vec2(0f, 0f)), taskType)
    }
}