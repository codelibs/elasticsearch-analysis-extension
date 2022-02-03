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
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.xcontent.XContentType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DisableGraphFilterFactoryTest {

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
                .pluginTypes("org.codelibs.elasticsearch.extension.ExtensionPlugin,"
                        + "org.codelibs.elasticsearch.extension.kuromoji.plugin.analysis.kuromoji.AnalysisKuromojiPlugin"));

    }

    @After
    public void cleanUp() throws Exception {
        runner.close();
        runner.clean();
    }

    @Test
    public void test_disableGraph() throws Exception {
        runner.ensureYellow();
        Node node = runner.node();

        final String index1 = "dataset1";
        final String index2 = "dataset2";

        final String index1Settings = "{\"index\":{\"analysis\":{"//
                + "\"analyzer\":{"
                + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"kuromoji_tokenizer\",\"filter\":[\"disable_graph\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index1, Settings.builder().loadFromSource(index1Settings, XContentType.JSON).build());
        final String index2Settings = "{\"index\":{\"analysis\":{"//
                + "\"analyzer\":{" + "\"ja_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"kuromoji_tokenizer\"}" + "}"//
                + "}}}";
        runner.createIndex(index2, Settings.builder().loadFromSource(index2Settings, XContentType.JSON).build());
        runner.ensureYellow();
        runner.createMapping(index1, "{\"properties\":{\"content\" : {\"type\" : \"text\",\"analyzer\":\"ja_analyzer\"}}}");
        try (CurlResponse response = EcrCurl.post(node, "/" + index1 + "/_analyze").header("Content-Type", "application/json")
                .body("{\"analyzer\":\"ja_analyzer\",\"text\":\"レッドハウスフーズ\"}").execute()) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
            assertEquals(2, tokens.size());
            assertEquals("レッド", tokens.get(0).get("token").toString());
            assertEquals("ハウスフーズ", tokens.get(1).get("token").toString());
        }

        runner.insert(index1, "1",
                builder -> builder.setSource("{\"content\":\"レッド\"}", XContentType.JSON).setRefreshPolicy(RefreshPolicy.WAIT_UNTIL));

        SearchResponse response = runner.search(index1, builder -> builder.setQuery(QueryBuilders.matchQuery("content", "レッドハウスフーズ")));
        assertEquals(1L, response.getHits().getTotalHits().value);
    }
}
