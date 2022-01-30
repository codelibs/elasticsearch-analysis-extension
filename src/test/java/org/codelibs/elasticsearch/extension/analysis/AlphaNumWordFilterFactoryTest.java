/*
 * Copyright 2012-2022 CodeLibs Project and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.codelibs.elasticsearch.extension.analysis;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;
import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.codelibs.curl.CurlResponse;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.net.EcrCurl;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.node.Node;
import org.elasticsearch.xcontent.XContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AlphaNumWordFilterFactoryTest {

    private ElasticsearchClusterRunner runner;

    private int numOfNode = 1;

    private String clusterName;

    @Before
    public void setUp() throws Exception {
        clusterName = "es-analysisja-" + System.currentTimeMillis();
        runner = new ElasticsearchClusterRunner();
        runner.onBuild(new ElasticsearchClusterRunner.Builder() {
            @Override
            public void build(final int number, final Builder settingsBuilder) {
                settingsBuilder.put("http.cors.enabled", true);
                settingsBuilder.put("http.cors.allow-origin", "*");
                settingsBuilder.put("discovery.type", "single-node");
                // settingsBuilder.putList("discovery.seed_hosts", "127.0.0.1:9301");
                // settingsBuilder.putList("cluster.initial_master_nodes", "127.0.0.1:9301");
            }
        }).build(newConfigs().clusterName(clusterName).numOfNode(numOfNode)
                .pluginTypes("org.codelibs.elasticsearch.extension.ExtensionPlugin"));

    }

    @After
    public void cleanUp() throws Exception {
        runner.close();
        runner.clean();
    }

    @Test
    public void test_basic() throws Exception {

        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{" //
                + "\"filter\":{" //
                + "\"alphanum_word_filter\":{\"type\":\"alphanum_word\"}" + "}," //
                + "\"tokenizer\":{" //
                + "\"unigram_analyzer\":{\"type\":\"ngram\",\"min_gram\":\"1\",\"max_gram\":\"1\",\"token_chars\":[\"letter\",\"digit\"]}"
                + "},"//
                + "\"analyzer\":{" //
                + "\"ngram_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"unigram_analyzer\"},"
                + "\"ngram_word_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"unigram_analyzer\",\"filter\":[\"alphanum_word_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index, Settings.builder().loadFromSource(indexSettings, XContentType.JSON).build());
        runner.ensureYellow();

        {
            String text = "aaa";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(1, tokens.size());
                assertEquals("aaa", tokens.get(0).get("token").toString());
            }
        }

        {
            String text = "aaa bbb";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(2, tokens.size());
                assertEquals("aaa", tokens.get(0).get("token").toString());
                assertEquals("bbb", tokens.get(1).get("token").toString());
            }
        }

        {
            String text = "aa1 bb2 333";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(3, tokens.size());
                assertEquals("aa1", tokens.get(0).get("token").toString());
                assertEquals("bb2", tokens.get(1).get("token").toString());
                assertEquals("333", tokens.get(2).get("token").toString());
            }
        }

        {
            String text = "aaa亜aaa";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(3, tokens.size());
                assertEquals("aaa", tokens.get(0).get("token").toString());
                assertEquals("亜", tokens.get(1).get("token").toString());
                assertEquals("aaa", tokens.get(2).get("token").toString());
            }
        }

        {
            String text = "嬉しい";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(3, tokens.size());
                assertEquals("嬉", tokens.get(0).get("token").toString());
                assertEquals("し", tokens.get(1).get("token").toString());
                assertEquals("い", tokens.get(2).get("token").toString());
            }
        }

    }

    @Test
    public void test_maxTokenLength() throws Exception {

        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{" //
                + "\"filter\":{" //
                + "\"alphanum_word_filter\":{\"type\":\"alphanum_word\",\"max_token_length\":2}" + "}," //
                + "\"tokenizer\":{" //
                + "\"unigram_analyzer\":{\"type\":\"ngram\",\"min_gram\":\"1\",\"max_gram\":\"1\",\"token_chars\":[\"letter\",\"digit\"]}"
                + "},"//
                + "\"analyzer\":{" //
                + "\"ngram_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"unigram_analyzer\"},"
                + "\"ngram_word_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"unigram_analyzer\",\"filter\":[\"alphanum_word_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index, Settings.builder().loadFromSource(indexSettings, XContentType.JSON).build());
        runner.ensureYellow();

        {
            String text = "aaa";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(1, tokens.size());
                assertEquals("aa", tokens.get(0).get("token").toString());
            }
        }

        {
            String text = "aaa bbb";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(2, tokens.size());
                assertEquals("aa", tokens.get(0).get("token").toString());
                assertEquals("bb", tokens.get(1).get("token").toString());
            }
        }

        {
            String text = "aa1 bb2 333";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(3, tokens.size());
                assertEquals("aa", tokens.get(0).get("token").toString());
                assertEquals("bb", tokens.get(1).get("token").toString());
                assertEquals("33", tokens.get(2).get("token").toString());
            }
        }

        {
            String text = "aaa亜aaa";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(3, tokens.size());
                assertEquals("aa", tokens.get(0).get("token").toString());
                assertEquals("亜", tokens.get(1).get("token").toString());
                assertEquals("aa", tokens.get(2).get("token").toString());
            }
        }

        {
            String text = "嬉しい";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"ngram_word_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(3, tokens.size());
                assertEquals("嬉", tokens.get(0).get("token").toString());
                assertEquals("し", tokens.get(1).get("token").toString());
                assertEquals("い", tokens.get(2).get("token").toString());
            }
        }

    }

}
