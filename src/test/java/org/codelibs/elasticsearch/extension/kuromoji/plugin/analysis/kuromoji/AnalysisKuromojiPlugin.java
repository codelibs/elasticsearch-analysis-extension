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
package org.codelibs.elasticsearch.extension.kuromoji.plugin.analysis.kuromoji;

import static java.util.Collections.singletonMap;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.JapaneseStopTokenFilterFactory;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiAnalyzerProvider;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiBaseFormFilterFactory;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiKatakanaStemmerFactory;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiNumberFilterFactory;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiPartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiReadingFormFilterFactory;
import org.codelibs.elasticsearch.extension.kuromoji.index.analysis.KuromojiTokenizerFactory;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class AnalysisKuromojiPlugin extends Plugin implements AnalysisPlugin {
    @Override
    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        return singletonMap("kuromoji_iteration_mark", KuromojiIterationMarkCharFilterFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        final Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("kuromoji_baseform", KuromojiBaseFormFilterFactory::new);
        extra.put("kuromoji_part_of_speech", KuromojiPartOfSpeechFilterFactory::new);
        extra.put("kuromoji_readingform", KuromojiReadingFormFilterFactory::new);
        extra.put("kuromoji_stemmer", KuromojiKatakanaStemmerFactory::new);
        extra.put("ja_stop", JapaneseStopTokenFilterFactory::new);
        extra.put("kuromoji_number", KuromojiNumberFilterFactory::new);
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        return singletonMap("kuromoji_tokenizer", KuromojiTokenizerFactory::new);
    }

    @Override
    public Map<String, AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        return singletonMap("kuromoji", KuromojiAnalyzerProvider::new);
    }
}
