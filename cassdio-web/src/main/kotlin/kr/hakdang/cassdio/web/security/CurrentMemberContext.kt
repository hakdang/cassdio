package kr.hakdang.cassdio.web.security

import kr.hakdang.cassdio.core.identity.Member

object CurrentMemberContext {
    private val holder = ThreadLocal<Member>()

    fun set(member: Member) = holder.set(member)

    fun get(): Member? = holder.get()

    fun clear() = holder.remove()
}
