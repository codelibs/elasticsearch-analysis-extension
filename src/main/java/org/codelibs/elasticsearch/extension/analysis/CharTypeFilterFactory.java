package org.codelibs.elasticsearch.extension.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.ja.CharTypeFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class CharTypeFilterFactory extends AbstractTokenFilterFactory {

    private final boolean alphabetic;

    private final boolean digit;

    private final boolean letter;

    public CharTypeFilterFactory(final IndexSettings indexSettings, final Environment environment, final String name, final Settings settings) {
        super(indexSettings, name, settings);

        alphabetic = settings.getAsBoolean("alphabetic", true);
        digit = settings.getAsBoolean("digit", true);
        letter = settings.getAsBoolean("letter", true);
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {
        return new CharTypeFilter(tokenStream, alphabetic, digit, letter);
    }
}
