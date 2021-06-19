Elasticsearch Analysis Extension
[![Java CI with Maven](https://github.com/codelibs/elasticsearch-analysis-extension/actions/workflows/maven.yml/badge.svg)](https://github.com/codelibs/elasticsearch-analysis-extension/actions/workflows/maven.yml)
=======================

## Overview

Elasticsearch Analysis Extension Plugin provides Tokenizer/CharFilter/TokenFilter.

## Version

[Versions in Maven Repository](https://repo1.maven.org/maven2/org/codelibs/elasticsearch-analysis-extension/)

### Issues/Questions

Please file an [issue](https://github.com/codelibs/elasticsearch-analysis-extension/issues "issue").

## Installation

    $ $ES_HOME/bin/elasticsearch-plugin install org.codelibs:elasticsearch-analysis-extension:6.3.1

## References

### IterationMarkCharFilter (Char Filter)

IterationMarkCharFilter normalizes an iteration mark charcter. 
For example, this char filter replaces "学問のすゝめ" with "学問のすすめ".
The property name is "iteration_mark".

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "kuromoji_tokenizer",
                            "mode" : "extended"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "char_filter":["iteration_mark"]
                        }
                    }
                }
            }
        }
    }'

### ProlongedSoundMarkCharFilter (Char Filter)

ProlongedSoundMarkCharFilter replaces the following prolonged sound mark charcters with '\u30fc' (KATAKANA-HIRAGANA SOUND MARK).

| Unicode | Name |
|:-----:|:-----|
| U002D | HYPHEN-MINUS |
| UFF0D | FULLWIDTH HYPHEN-MINUS |
| U2010 | HYPHEN |
| U2011 | NON-BREAKING HYPHEN |
| U2012 | FIGURE DASH |
| U2013 | EN DASH |
| U2014 | EM DASH |
| U2015 | HORIZONTAL BAR |
| U207B | SUPERSCRIPT MINUS |
| U208B | SUBSCRIPT MINUS |
| U30FC | KATAKANA-HIRAGANA SOUND MARK |

This char filter name is "prolonged_sound_mark" as below.

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "kuromoji_tokenizer",
                            "mode" : "extended"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "char_filter":["prolonged_sound_mark"]
                        }
                    }
                }
            }
        }
    }'

### KanjiNumberFilter (TokenFilter)

KanjiNumberFilter relaces Kanji number character(ex. "一") with a number character(ex. "1").
This token filter name is "kanji_number".

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "kuromoji_tokenizer",
                            "mode" : "extended"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "filter":["kanji_number"]
                        }
                    }
                }
            }
        }
    }'

### CharTypeFilter (TokenFilter)

CharTypeFilter keeps tokens which contains "alphabetic", "digit" or "letter" character.
The following setting is that tokens which contain "letter" character are kept(only "digit" token is removed).

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    ...,
                    "filter" : {
                        "letter_filter" : {
                            "type" : "char_type",
                            "digit" : false
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "filter":["letter_filter"]
                        }
                    }
                }
            }
        }
    }'

"alphabetic", "digit" and "letter" property are true as default.

| Token  | None   | digit:false | letter:false | 
|:-------|:------:|:-----------:|:------------:|
| abc    | keep   | keep        | keep         |
| ab1    | keep   | keep        | keep         |
| abあ   | keep   | keep        | keep         |
| 123    | keep   | remove      | keep         |
| 12あ   | keep   | keep        | keep         |
| あいう | keep   | keep        | remove       |
| #-=    | remove | remove      | remove       |

### NumberConcatenationFilter

NumberConcatenationFilter concatenates a token followed by a number.
For example, "10" and "years" are converted to "10years".

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    ...,
                    "filter" : {
                        "numconcat_filter" : {
                            "type" : "number_concat",
                            "suffix_words_path" : "suffix.txt"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "filter":["numconcat_filter"]
                        }
                    }
                }
            }
        }
    }'

### PatternConcatenationFilter

PatternConcatenationFilter concatenates 2 token matched with pattern1 and pattern2.
For example, "10" and "years" are converted to "10years".

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    ...,
                    "filter" : {
                        "patternconcat_filter" : {
                            "type" : "pattern_concat",
                            "pattern1" : "[0-9]+",
                            "pattern2" : "year(s)?"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict",
                            "filter":["patternconcat_filter"]
                        }
                    }
                }
            }
        }
    }'


### ReloadableKuromojiTokenizer (Tokenizer)

ReloadableKuromojiTokenizer reloads a user-dictionary file when it's updated.
To use this tokenizer, replace "kuromoji\_tokenizer" with "reloadable\_kuromoji" at the "type" property as below.

    curl -XPUT 'http://localhost:9200/sample/' -d'
    {
        "settings": {
            "index":{
                "analysis":{
                    "tokenizer" : {
                        "kuromoji_user_dict" : {
                            "type" : "reloadable_kuromoji",
                            "mode" : "extended",
                            "discard_punctuation" : "false",
                            "user_dictionary" : "userdict_ja.txt"
                        }
                    },
                    "analyzer" : {
                        "my_analyzer" : {
                            "type" : "custom",
                            "tokenizer" : "kuromoji_user_dict"
                        }
                    }
                }
            }
        }
    }'

Note that you might lose documents in a result when updating a dictionary file because of changing terms.
