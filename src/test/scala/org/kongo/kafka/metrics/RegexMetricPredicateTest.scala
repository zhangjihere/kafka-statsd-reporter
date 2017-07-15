package org.kongo.kafka.metrics

import java.util.regex.Pattern

import org.junit.Assert
import org.junit.Test

class RegexMetricPredicateTest {

  private val startsWithFoo = Pattern.compile("foo.*")
  private val containsBar = Pattern.compile(".*bar.*")

  @Test
  def allMatchingPredicate(): Unit = {
    val all = RegexMetricPredicate(None, None)

    Assert.assertTrue(all.matches(""))
    Assert.assertTrue(all.matches("some"))
    Assert.assertTrue(all.matches("foo.bar.test"))
  }

  @Test
  def includePatternPredicate(): Unit = {
    val startsWith = RegexMetricPredicate(Some(startsWithFoo), None)

    Assert.assertTrue(startsWith.matches("foo"))
    Assert.assertTrue(startsWith.matches("foo.bar.test"))

    Assert.assertFalse(startsWith.matches(""))
    Assert.assertFalse(startsWith.matches("bar"))
    Assert.assertFalse(startsWith.matches("bar.foo"))
    Assert.assertFalse(startsWith.matches("bar.foo.test"))
  }

  @Test
  def excludePatternPredicate(): Unit = {
    val doesNotStartsWith = RegexMetricPredicate(None, Some(startsWithFoo))

    Assert.assertFalse(doesNotStartsWith.matches("foo"))
    Assert.assertFalse(doesNotStartsWith.matches("foo.bar.test"))

    Assert.assertTrue(doesNotStartsWith.matches(""))
    Assert.assertTrue(doesNotStartsWith.matches("bar"))
    Assert.assertTrue(doesNotStartsWith.matches("bar.foo"))
    Assert.assertTrue(doesNotStartsWith.matches("bar.foo.test"))
  }

  @Test
  def includeExcludePatternsPredicate(): Unit = {
    val matcher = RegexMetricPredicate(Some(startsWithFoo), Some(containsBar))

    Assert.assertTrue(matcher.matches("foo"))

    Assert.assertFalse(matcher.matches("foo.bar.test"))
    Assert.assertFalse(matcher.matches(""))
    Assert.assertFalse(matcher.matches("bar"))
    Assert.assertFalse(matcher.matches("bar.foo"))
    Assert.assertFalse(matcher.matches("bar.foo.test"))
  }
}
