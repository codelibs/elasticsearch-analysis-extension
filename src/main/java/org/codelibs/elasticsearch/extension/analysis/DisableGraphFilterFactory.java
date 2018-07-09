package org.codelibs.elasticsearch.extension.analysis;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.miscellaneous.DisableGraphAttribute;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class DisableGraphFilterFactory extends AbstractTokenFilterFactory {

    public DisableGraphFilterFactory(final IndexSettings indexSettings, final Environment environment, final String name,
            final Settings settings) {
        super(indexSettings, name, settings);
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {
        tokenStream.addAttribute(DisableGraphAttribute.class);
        return tokenStream;
    }
}
