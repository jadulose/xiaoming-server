package org.xjtuse.xiaoming.mapper

import org.apache.ibatis.annotations.Mapper
import org.xjtuse.xiaoming.model.User

@Mapper
interface UserMapper {
    fun findByEmail(email: String): User?
}