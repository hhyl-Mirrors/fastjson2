package com.alibaba.fastjson2.benchmark.eishay;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.benchmark.eishay.vo.MediaContent;
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

public class EishayCodecOnlyJSONB {
    static MediaContent mc;
    static byte[] fastjson2JSONBBytes;

    static final JSONWriter.Feature[] jsonbWriteFeatures = {
            JSONWriter.Feature.WriteClassName,
            JSONWriter.Feature.FieldBased,
            JSONWriter.Feature.ReferenceDetection,
            JSONWriter.Feature.WriteNulls,
            JSONWriter.Feature.NotWriteDefaultValue,
            JSONWriter.Feature.NotWriteHashMapArrayListClassName,
            JSONWriter.Feature.WriteNameAsSymbol
    };

    static final JSONReader.Feature[] jsonbReaderFeatures = {
            JSONReader.Feature.SupportAutoType,
            JSONReader.Feature.UseDefaultConstructorAsPossible,
            JSONReader.Feature.UseNativeObject,
            JSONReader.Feature.FieldBased
    };

    static {
        try {
            InputStream is = EishayParseBinary.class.getClassLoader().getResourceAsStream("data/eishay.json");
            String str = IOUtils.toString(is, "UTF-8");
            mc = JSONReader.of(str)
                    .read(MediaContent.class);

            fastjson2JSONBBytes = JSONB.toBytes(mc, jsonbWriteFeatures);

            MediaContent obj = (MediaContent) JSONB.parseObject(fastjson2JSONBBytes, Object.class, jsonbReaderFeatures);
            if (!mc.equals(obj)) {
                throw new JSONException("not equals");
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @Benchmark
    public void deserialize_jsonb(Blackhole bh) {
        MediaContent obj = (MediaContent) JSONB.parseObject(fastjson2JSONBBytes, Object.class, jsonbReaderFeatures);
        bh.consume(obj);
    }

    @Benchmark
    public void serialize_jsonb(Blackhole bh) {
        byte[] bytes = JSONB.toBytes(mc, jsonbWriteFeatures);
        bh.consume(bytes);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(EishayCodecOnlyJSONB.class.getName())
                .mode(Mode.Throughput)
                .warmupIterations(3)
                .timeUnit(TimeUnit.MILLISECONDS)
                .forks(1)
                .build();
        new Runner(options).run();
    }
}
