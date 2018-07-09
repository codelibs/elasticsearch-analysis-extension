package org.codelibs.elasticsearch.extension;

import java.util.HashMap;
import java.util.Map;

import org.codelibs.elasticsearch.extension.analysis.AlphaNumWordFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.CharTypeFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.DisableGraphFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.FlexiblePorterStemFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.IterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.KanjiNumberFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.KuromojiBaseFormFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.KuromojiIterationMarkCharFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.KuromojiKatakanaStemmerFactory;
import org.codelibs.elasticsearch.extension.analysis.KuromojiPartOfSpeechFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.KuromojiReadingFormFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.NumberConcatenationFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.PatternConcatenationFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.PosConcatenationFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.ProlongedSoundMarkCharFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.ReloadableKeywordMarkerFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.ReloadableKuromojiTokenizerFactory;
import org.codelibs.elasticsearch.extension.analysis.ReloadableStopFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.StopTokenPrefixFilterFactory;
import org.codelibs.elasticsearch.extension.analysis.StopTokenSuffixFilterFactory;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule.AnalysisProvider;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class ExtensionPlugin extends Plugin implements AnalysisPlugin {

    @Override
    public Map<String, AnalysisProvider<CharFilterFactory>> getCharFilters() {
        final Map<String, AnalysisProvider<CharFilterFactory>> extra = new HashMap<>();
        extra.put("iteration_mark", IterationMarkCharFilterFactory::new);
        extra.put("prolonged_sound_mark", ProlongedSoundMarkCharFilterFactory::new);
        extra.put("reloadable_kuromoji_iteration_mark", KuromojiIterationMarkCharFilterFactory::new);
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        final Map<String, AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
        extra.put("reloadable_kuromoji_baseform", KuromojiBaseFormFilterFactory::new);
        extra.put("reloadable_kuromoji_part_of_speech", KuromojiPartOfSpeechFilterFactory::new);
        extra.put("reloadable_kuromoji_readingform", KuromojiReadingFormFilterFactory::new);
        extra.put("reloadable_kuromoji_stemmer", KuromojiKatakanaStemmerFactory::new);
        extra.put("kanji_number", KanjiNumberFilterFactory::new);
        extra.put("kuromoji_pos_concat", PosConcatenationFilterFactory::new);
        extra.put("char_type", CharTypeFilterFactory::new);
        extra.put("number_concat", NumberConcatenationFilterFactory::new);
        extra.put("pattern_concat", PatternConcatenationFilterFactory::new);
        extra.put("stop_prefix", StopTokenPrefixFilterFactory::new);
        extra.put("stop_suffix", StopTokenSuffixFilterFactory::new);
        extra.put("reloadable_keyword_marker", ReloadableKeywordMarkerFilterFactory::new);
        extra.put("reloadable_stop", ReloadableStopFilterFactory::new);
        extra.put("flexible_porter_stem", FlexiblePorterStemFilterFactory::new);
        extra.put("alphanum_word", AlphaNumWordFilterFactory::new);
        extra.put("disable_graph", DisableGraphFilterFactory::new);
        return extra;
    }

    @Override
    public Map<String, AnalysisProvider<TokenizerFactory>> getTokenizers() {
        final Map<String, AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("reloadable_kuromoji_tokenizer", ReloadableKuromojiTokenizerFactory::new);
        extra.put("reloadable_kuromoji", ReloadableKuromojiTokenizerFactory::new);
        return extra;
    }

}
