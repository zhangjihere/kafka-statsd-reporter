
# kafka-statsd-reporter

[![build status](https://api.travis-ci.org/kongo2002/kafka-statsd-reporter.svg)][travis]

Metrics reporter that exposes kafka's metrics to StatsD.

## Build

You can easily build a shadow JAR using the included gradle wrapper script:

    ./gradlew shadowJar


## Requirements

This metrics reporter is by default built against kafka `0.11.0.0` and tested
with that version but should work with kafka >= 0.9 as well.


## Install

After building (or downloading) the shadow JAR you can copy the JAR file to
your kafka broker's `libs` folder.

```bash
# given your kafka broker is installed in /opt/kafka you would
# do something like this:
cp build/libs/kafka-statsd-reporter-0.1-all.jar /opt/kafka/libs
```


## Configuration

You may put some of the configuration values below in your broker's
configuration (typically `server.properties`):

```ini
# register the metrics reporter
metric.reporters=org.kongo.kafka.metrics.KafkaStatsdReporter

# you may additionally enable/disable to reporter (default: false)
external.kafka.statsd.metrics.reporter.enabled=true

# StatsD server host (default: localhost)
external.kafka.statsd.metrics.host=localhost

# StatsD server port (default: 8125)
external.kafka.statsd.metrics.port=8125

# custom metrics prefix (default: kafka)
external.kafka.statsd.metrics.prefix=kafka.metrics

# whitelist metrics pattern (optional)
external.kafka.statsd.metrics.include=(kafka\.network\..*)|(kafka\.server\..*)

# blacklist metrics pattern (optional)
external.kafka.statsd.metrics.exclude=(.*\.channel-metrics\..*)

# you may toggle the supported metrics dimension that should be exported
# by default all dimensions are enabled
external.kafka.statsd.metrics.dimension.enabled.count=true
external.kafka.statsd.metrics.dimension.enabled.meanRate=true
external.kafka.statsd.metrics.dimension.enabled.rate1m=true
external.kafka.statsd.metrics.dimension.enabled.rate5m=true
external.kafka.statsd.metrics.dimension.enabled.rate15m=true
external.kafka.statsd.metrics.dimension.enabled.min=true
external.kafka.statsd.metrics.dimension.enabled.max=true
external.kafka.statsd.metrics.dimension.enabled.mean=true
external.kafka.statsd.metrics.dimension.enabled.sum=true
external.kafka.statsd.metrics.dimension.enabled.stddev=true
external.kafka.statsd.metrics.dimension.enabled.median=true
external.kafka.statsd.metrics.dimension.enabled.p75=true
external.kafka.statsd.metrics.dimension.enabled.p95=true
external.kafka.statsd.metrics.dimension.enabled.p98=true
external.kafka.statsd.metrics.dimension.enabled.p99=true
external.kafka.statsd.metrics.dimension.enabled.p999=true
```


## Motivation

I havn't found any existing metrics reporter that both properly supports the
latest kafka version (`0.11.0.0` as of now) and supports *all* of the metrics
that kafka exposes.

In the current kafka release the metrics are spread across two different metrics
solutions. First there are a lot of metrics exposed by the [yammer metrics
library][yammer] and secondly there are more metrics published to kafka's own
metrics functions. Most of the libraries I have found so far support only one of
those two.


### Other metrics reporters

- [kafka-statsd-metrics2](https://github.com/airbnb/kafka-statsd-metrics2) by
  airbnb
- [kafka-graphite](https://github.com/apakulov/kafka-graphite) by apakulov
- [kafka-metrics-reporter](https://github.com/krux/kafka-metrics-reporter) by
  krux
- [kafka-graphite](https://github.com/damienclaveau/kafka-graphite) by
  damienclaveau


## Maintainer

The project is written by Gregor Uhlenheuer. You can reach me at
[kongo2002@gmail.com][mail]


## License

*kafka-statsd-reporter* is licensed under the [Apache license][apache], Version
2.0

> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
> See the License for the specific language governing permissions and
> limitations under the License.


[yammer]: http://metrics.codahale.com/getting-started/
[travis]: https://travis-ci.org/kongo2002/kafka-statsd-reporter/
[mail]: mailto:kongo2002@gmail.com
[apache]: http://www.apache.org/licenses/LICENSE-2.0
