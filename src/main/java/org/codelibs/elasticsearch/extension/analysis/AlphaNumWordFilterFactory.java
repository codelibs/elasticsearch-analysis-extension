package org.codelibs.elasticsearch.extension.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.en.AlphaNumWordFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class AlphaNumWordFilterFactory extends AbstractTokenFilterFactory {

    private final int maxTokenLength;

    public AlphaNumWordFilterFactory(final IndexSettings indexSettings, final Environment environment, final String name, final Settings settings) {
        super(indexSettings, name, settings);

        maxTokenLength = settings.getAsInt("max_token_length", AlphaNumWordFilter.DEFAULT_MAX_TOKEN_LENGTH);
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {
        final AlphaNumWordFilter alphaNumWordFilter = new AlphaNumWordFilter(tokenStream);
        alphaNumWordFilter.setMaxTokenLength(maxTokenLength);
        return alphaNumWordFilter;
    }
}
