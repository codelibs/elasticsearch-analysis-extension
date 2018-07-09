package org.codelibs.elasticsearch.extension.analysis;

import java.io.Reader;

import org.codelibs.analysis.ja.IterationMarkCharFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractCharFilterFactory;

public class IterationMarkCharFilterFactory extends AbstractCharFilterFactory {

    public IterationMarkCharFilterFactory(final IndexSettings indexSettings, final Environment env, final String name, final Settings settings) {
        super(indexSettings, name);
    }

    @Override
    public Reader create(final Reader tokenStream) {
        return new IterationMarkCharFilter(tokenStream);
    }

}
