/*
 Copyright (C) 2015 - 2019 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <https://www.orbit.cloud>.
 See license in LICENSE.
 */

package cloud.orbit.dsl

import cloud.orbit.dsl.ast.Type
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TypeArityCheckTest {
    private val context = TypeCheck.Context.values().random()
    private val errorReporter = TestErrorReporter()
    private val typeArityCheck = TypeArityCheck(
        mapOf(
            "t0" to TypeDescriptor("t0", 0),
            "t1" to TypeDescriptor("t1", 1),
            "t2" to TypeDescriptor("t2", 2)
        )
    )

    @Test
    fun noOpWhenTypeIsUnknown() {
        typeArityCheck.check(Type("t"), context, errorReporter)

        assertTrue(errorReporter.errors.isEmpty())
    }

    @Test
    fun noErrorsWhenTypeArityIsCorrect() {
        typeArityCheck.check(Type("t0"), context, errorReporter)
        typeArityCheck.check(Type("t1", of = listOf(Type("t"))), context, errorReporter)
        typeArityCheck.check(Type("t2", of = listOf(Type("t"), Type("t"))), context, errorReporter)

        assertTrue(errorReporter.errors.isEmpty())
    }

    @Test
    fun reportsErrorWhenTypeArityIsIncorrect() {
        typeArityCheck.check(Type("t0", of = listOf(Type("t"))), context, errorReporter)
        typeArityCheck.check(Type("t1"), context, errorReporter)
        typeArityCheck.check(Type("t2", of = listOf(Type("t"))), context, errorReporter)

        assertTrue(errorReporter.errors.contains("expected parameter count for type 't0' is 0, found 1"))
        assertTrue(errorReporter.errors.contains("expected parameter count for type 't1' is 1, found 0"))
        assertTrue(errorReporter.errors.contains("expected parameter count for type 't2' is 2, found 1"))
    }
}
