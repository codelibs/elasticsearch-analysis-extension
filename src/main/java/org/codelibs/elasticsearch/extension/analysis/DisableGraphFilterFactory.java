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

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.lucene.analysis.miscellaneous.DisableGraphAttribute;

public class DisableGraphFilterFactory extends AbstractTokenFilterFactory {

    public DisableGraphFilterFactory(final IndexSettings indexSettings, final Environment environment, final String name,
            final Settings settings) {
        super(name, settings);
    }

    @Override
    public TokenStream create(final TokenStream tokenStream) {
        tokenStream.addAttribute(DisableGraphAttribute.class);
        return tokenStream;
    }
}
