package com.alibaba.fastjson2.benchmark.primitves;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.benchmark.primitves.vo.Instant20Field;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class Instant20 {
    static String str;
    static ObjectMapper mapper = new ObjectMapper();

    public Instant20() {
        try {
            InputStream is = Instant20.class.getClassLoader().getResourceAsStream("data/date20.json");
            str = IOUtils.toString(is, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Benchmark
    public void fastjson1(Blackhole bh) {
        bh.consume(
                com.alibaba.fastjson.JSON.parseObject(str, Instant20Field.class)
        );
    }

    @Benchmark
    public void fastjson2(Blackhole bh) {
        bh.consume(
                JSON.parseObject(str, Instant20Field.class)
        );
    }

    @Benchmark
    public void jackson(Blackhole bh) throws Exception {
        bh.consume(
                mapper.readValue(str, Instant20Field.class)
        );
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(Instant20.class.getName())
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupIterations(3)
                .forks(1)
                .build();
        new Runner(options).run();
    }
}
