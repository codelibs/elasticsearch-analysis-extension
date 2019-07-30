package org.codelibs.elasticsearch.extension.analysis;

import static org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner.newConfigs;
import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.codelibs.curl.CurlResponse;
import org.codelibs.elasticsearch.runner.ElasticsearchClusterRunner;
import org.codelibs.elasticsearch.runner.net.EcrCurl;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.node.Node;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ReloadableKeywordMarkerFilterFactoryTest {

    private ElasticsearchClusterRunner runner;

    private int numOfNode = 3;

    private File[] keywordFiles;

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
                settingsBuilder.putList("discovery.seed_hosts", "127.0.0.1:9301");
                settingsBuilder.putList("cluster.initial_master_nodes", "127.0.0.1:9301");
            }
        }).build(newConfigs().clusterName(clusterName).numOfNode(numOfNode).pluginTypes("org.codelibs.elasticsearch.extension.ExtensionPlugin"));

        keywordFiles = null;
    }

    @After
    public void cleanUp() throws Exception {
        runner.close();
        runner.clean();
        if (keywordFiles != null) {
            for (File file : keywordFiles) {
                file.deleteOnExit();
            }
        }
    }

    @Test
    public void test_basic() throws Exception {
        keywordFiles = new File[numOfNode];
        for (int i = 0; i < numOfNode; i++) {
            String homePath = runner.getNode(i).settings().get("path.home");
            keywordFiles[i] = new File(new File(homePath, "config"), "keywords.txt");
            updateDictionary(keywordFiles[i], "consisted\nconsists");
        }

        runner.ensureYellow();
        Node node = runner.node();

        final String index = "dataset";

        final String indexSettings = "{\"index\":{\"analysis\":{" + "\"filter\":{"
                + "\"stem1_filter\":{\"type\":\"flexible_porter_stem\",\"step1\":true,\"step2\":false,\"step3\":false,\"step4\":false,\"step5\":false,\"step6\":false},"
                + "\"marker_filter\":{\"type\":\"reloadable_keyword_marker\",\"keywords_path\":\"keywords.txt\",\"reload_interval\":\"1s\"}"
                + "},"//
                + "\"analyzer\":{" + "\"default_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\"},"
                + "\"stem1_analyzer\":{\"type\":\"custom\",\"tokenizer\":\"whitespace\",\"filter\":[\"marker_filter\",\"stem1_filter\"]}"
                + "}"//
                + "}}}";
        runner.createIndex(index, Settings.builder().loadFromSource(indexSettings, XContentType.JSON).build());
        runner.ensureYellow();

        {
            String text = "consist consisted consistency consistent consistently consisting consists";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"stem1_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(7, tokens.size());
                assertEquals("consist", tokens.get(0).get("token").toString());
                assertEquals("consisted", tokens.get(1).get("token").toString());
                assertEquals("consistency", tokens.get(2).get("token").toString());
                assertEquals("consistent", tokens.get(3).get("token").toString());
                assertEquals("consistently", tokens.get(4).get("token").toString());
                assertEquals("consist", tokens.get(5).get("token").toString());
                assertEquals("consists", tokens.get(6).get("token").toString());
            }
        }

        for (int i = 0; i < numOfNode; i++) {
            String homePath = runner.getNode(i).settings().get("path.home");
            keywordFiles[i] = new File(new File(homePath, "config"), "keywords.txt");
            updateDictionary(keywordFiles[i], "consisting\nconsistent");
        }

        Thread.sleep(1100);

        {
            String text = "consist consisted consistency consistent consistently consisting consists";
            try (CurlResponse response = EcrCurl.post(node, "/" + index + "/_analyze").header("Content-Type", "application/json")
                    .body("{\"analyzer\":\"stem1_analyzer\",\"text\":\"" + text + "\"}").execute()) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> tokens = (List<Map<String, Object>>) response.getContent(EcrCurl.jsonParser()).get("tokens");
                assertEquals(7, tokens.size());
                assertEquals("consist", tokens.get(0).get("token").toString());
                assertEquals("consist", tokens.get(1).get("token").toString());
                assertEquals("consistency", tokens.get(2).get("token").toString());
                assertEquals("consistent", tokens.get(3).get("token").toString());
                assertEquals("consistently", tokens.get(4).get("token").toString());
                assertEquals("consisting", tokens.get(5).get("token").toString());
                assertEquals("consist", tokens.get(6).get("token").toString());
            }
        }

    }

    private void updateDictionary(File file, String content) throws IOException, UnsupportedEncodingException, FileNotFoundException {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            bw.write(content);
            bw.flush();
        }
    }
}
