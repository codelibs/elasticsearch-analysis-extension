package org.codelibs.elasticsearch.extension.analysis;

import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.StopTokenSuffixFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.index.analysis.Analysis;

public class StopTokenSuffixFilterFactory extends AbstractTokenFilterFactory {

    private final String[] stopwords;

    private final boolean ignoreCase;

    public StopTokenSuffixFilterFactory(final IndexSettings indexSettings, final Environment environment, final String name, final Settings settings) {
        super(indexSettings, name, settings);

        final List<String> wordList = Analysis.getWordList(environment, settings, "stopwords");
        if (wordList != null) {
            stopwords = wordList.toArray(new String[wordList.size()]);
        } else {
            stopwords = new String[0];
        }

        ignoreCase = settings.getAsBoolean("ignore_case", Boolean.FALSE).booleanValue();
        if (ignoreCase) {
            for (int i = 0; i < stopwords.length; i++) {
                stopwords[i] = stopwords[i].toLowerCase(Locale.ROOT);
            }
        }
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {
        return new StopTokenSuffixFilter(tokenStream, stopwords, ignoreCase);
    }
}
