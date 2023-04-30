# Orto Flavoured Markdown

[![Build and Test](https://github.com/orto-app/orto-flavoured-markdown/actions/workflows/main.yml/badge.svg?branch=main)](https://github.com/orto-app/orto-flavoured-markdown/actions/workflows/main.yml)

This project implements an extension to [Github Flavoured Markdown](https://github.github.com/gfm/) to add `#tags`. A tag is any sequence of characters (except `\t`, `\n`, ` `, `#`, `\f` and `\r`) starting with a `#` character.

Here is an example of an Orto Flavoured Markdown note:


```markdown

# Orto

Orto is a #digital-gardening app that allows you to cross reference your thoughts with
a #lean approach to note taking. With Orto you can:

1. Archive and tag your #🐈 pics
2. Monitor your #DnD character progress
3. Enable yourself to stop feeling inadequate to life in late capitalism

```

In the above file, tags would be `DnD`, `🐈`, `digital-gardening` and `lean`.

## Caveats

Be aware that

> [...] a tag is any sequence of characters (except [...]

means exactly that: so if you want to use a tag as the last token inside an "enclosing" markdown element (such as link's descriptions `[Like this #one](http://example.com)`), you have to add a space after the tag otherwise it'll match all of the following characters.

For example:

- Here `[Like this #one](http://example.com)` the tag is `one](http://example.com)`
- While here `[Like this #one ](http://example.com)` the tag is `one`

## Add this library to your project

Add the following line to your `build.gradle.kts`:

```
implementation("garden.orto:ofm:0.1.1")
```

## Contributing

Unless otherwise stated all the files in this repository are to be considered under the EUPL terms. We welcome contributions from anybody. Currently our process is not structured but feel free to open or take issues in case you want to help us.


### `_OFMLexer.kt` generation

To generate `_OFMLexer.kt` a 2019.3 Intellij IDEA is required and then [this procedure](https://github.com/JetBrains/markdown#development-gotchas) will guide you through the generation process.
