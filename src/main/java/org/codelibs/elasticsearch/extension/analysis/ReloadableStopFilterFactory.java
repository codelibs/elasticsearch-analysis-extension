package org.codelibs.elasticsearch.extension.analysis;

import java.nio.file.Path;

import org.apache.lucene.analysis.TokenStream;
import org.codelibs.analysis.en.ReloadableStopFilter;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;

public class ReloadableStopFilterFactory extends AbstractTokenFilterFactory {

    private final Path stopwordPath;

    private final long reloadInterval;

    private final boolean ignoreCase;

    public ReloadableStopFilterFactory(final IndexSettings indexSettings, final Environment environment, final String name, final Settings settings) {
        super(indexSettings, name, settings);

        final String path = settings.get("stopwords_path");
        if (path != null) {
            stopwordPath = environment.configFile().resolve(path);
        } else {
            stopwordPath = null;
        }

        ignoreCase = settings.getAsBoolean("ignore_case", false);
        reloadInterval = settings.getAsTime("reload_interval", TimeValue.timeValueMinutes(1)).getMillis();
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {
        if (stopwordPath == null) {
            return tokenStream;
        }
        return new ReloadableStopFilter(tokenStream, stopwordPath, ignoreCase, reloadInterval);
    }

}