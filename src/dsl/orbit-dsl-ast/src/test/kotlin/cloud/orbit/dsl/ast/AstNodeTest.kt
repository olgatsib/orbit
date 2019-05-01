/*
 Copyright (C) 2015 - 2019 Electronic Arts Inc.  All rights reserved.
 This file is part of the Orbit Project <https://www.orbit.cloud>.
 See license in LICENSE.
 */

package cloud.orbit.dsl.ast

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AstNodeTest {
    @Test
    fun annotatedAstNodeIsNewInstance() {
        val astNode = TestNode()
        val annotation = TestAnnotation()

        val annotatedAstNode = astNode.annotated(annotation)

        Assertions.assertNotSame(astNode, annotatedAstNode)
        Assertions.assertNull(astNode.getAnnotation<TestAnnotation>())
        Assertions.assertNull(astNode.getAnnotation(TestAnnotation::class.java))
        Assertions.assertSame(annotation, annotatedAstNode.getAnnotation<TestAnnotation>())
        Assertions.assertSame(annotation, annotatedAstNode.getAnnotation(TestAnnotation::class.java))
    }

    @Test
    fun supportsSingleAnnotationPerType() {
        val annotation1 = TestAnnotation()
        val annotation2 = TestAnnotation()
        val annotation3 = AnotherTestAnnotation()
        val astNode = TestNode()
            .annotated(annotation1)
            .annotated(annotation2)
            .annotated(annotation3)

        Assertions.assertSame(annotation2, astNode.getAnnotation<TestAnnotation>())
        Assertions.assertSame(annotation3, astNode.getAnnotation<AnotherTestAnnotation>())
    }

    @Test
    fun getAnnotationReturnsNullWhenAstNodeNotAnnotatedWithType() {
        val astNode = TestNode()

        Assertions.assertNull(astNode.getAnnotation<TestAnnotation>())
    }

    @Test
    fun reportsErrorToErrorListeners() {
        val errorListener1 = TestErrorListener()
        val errorListener2 = TestErrorListener()

        val visitor = ErrorReportingVisitor()
        visitor.addErrorListener(errorListener1)
        visitor.addErrorListener(errorListener2)

        visitor.visitCompilationUnit(CompilationUnit("cloud.orbit.test"))

        assertEquals(1, errorListener1.errorCount)
        assertEquals(1, errorListener2.errorCount)
    }

    @Test
    fun errorReportedOncePerErrorListener() {
        val errorListener = TestErrorListener()

        val visitor = ErrorReportingVisitor()
        visitor.addErrorListener(errorListener)
        visitor.addErrorListener(errorListener)

        visitor.visitCompilationUnit(CompilationUnit("cloud.orbit.test"))

        assertEquals(1, errorListener.errorCount)
    }

    @Test
    fun errorListenerCanBeRemoved() {
        val errorListener = TestErrorListener()

        val visitor = ErrorReportingVisitor()
        visitor.addErrorListener(errorListener)
        visitor.visitCompilationUnit(CompilationUnit("cloud.orbit.test"))

        visitor.removeErrorListener(errorListener)
        visitor.visitCompilationUnit(CompilationUnit("cloud.orbit.test"))

        assertEquals(1, errorListener.errorCount)
    }

    private data class TestNode(val name: String = "") : AstNode<TestNode>() {
        override fun clone() = copy()
    }

    private class TestAnnotation : AstAnnotation

    private class AnotherTestAnnotation : AstAnnotation

    private class TestErrorListener : ErrorListener {
        var errorCount = 0

        override fun onError(astNode: AstNode<*>, message: String) {
            ++errorCount
        }
    }

    private class ErrorReportingVisitor : AstVisitor() {
        override fun visitCompilationUnit(cu: CompilationUnit) {
            reportError(TestNode(), "")
        }
    }
}
