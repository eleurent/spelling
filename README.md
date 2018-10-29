spelling
========

A Machine-Learning project for detection of langage and spelling correction.

![IHM](https://github.com/eleurent/spelling/raw/master/doc/IHM_raw.PNG)

# How it works

A misspelled word can be considered as the observation of the real word that was meant to be written. Thus, correcting a spelling mistake is a **classification problem** of finding the correct class among all the existing words of a language.
In this project, a **Naive Bayse Classifier** has been implemented.

Here's how it works: if,
- ``m`` is the word typed by the user
- ``c`` is a possible correction of this word
- ``P(c|m)`` is the probability that the user typed ``m`` while meaning to type the correct word ``c``

The Bayes Formula states that:
``
P(c|m) = P(m|c)*P(c)/P(m)
``

We want to find ``c`` that maximizes ``P(c|m)``, so we can ignore ``P(m)`` (which is constant) and maximize ``P(m|c)`` and ``P(c)``.

- ``P(m|c)`` is the probability of making the mistake ``m`` by meaning to type ``c``. It is the **error model**. 
- ``P(c)`` is the probability that the user wanted to type ``c``. It is the **language model**.

To model the typing errors, we use the **editing distance** ``d(m,c)``, which is the number of elementary operations (deletion, insetion, replacement or transposition of letters) needed to move from ``c`` to ``m``.
The error model can be written ``P(m|c) = Pe^d(m,c)``, with ``Pe`` a fixed error probability for mistyping one letter.

To model the probability of a given word to appear in a text, we can use pragmatic approach: the more this word appears in a large corpus, the greater its probability.
The language model ``P(c)`` is the **frequency** of the word c in the corpus.

Finally, for any given typed word ``m``, we generate a lot of potential correction canditates by generating errors of editing distance <= 2. Then, for each of this candidate words we compute the probability P(c|m) that they are the right correction, and we select the candidate with the highest probability.

![IHM](https://github.com/eleurent/spelling/raw/master/doc/toujours.PNG)

The same approach can be used to determine the language of the sentence.

## Corpus

I used the following corpus for training the language models.

French | English
---------|---------
Émile Zola, *L'argent* | William Shakespeare, *Henry VI*
Émile Zola, *L'assommoir* | William Shakespeare, *Hamlet*
Émile Zola, *Germinal* | William Shakespeare, *MacBeth*
Victor Hugo, *Les Misérables* | Lewis Caroll, *Alice in Wonderland*
Marcel Proust, *Du côté de chez Swann* | Sir Arthur Conan Doyle, *Sherlock Holmes*
Stendhal, *Le rouge et le noir* | Herman Melville, *Moby Dick ; or The Whale*
Alexandre Dumas, *Les trois mousquetaires* | Mary Shelley, *Frankenstein or The Modern Prometheus*
Gustave Flaubert, *Madame Bovary* | Charles Dickens, *Great Expectations*

## Results

The performance of correction reaches about *83%*.

A more detailed analysis can be found in the [project report](https://github.com/eleurent/spelling/raw/master/doc/Rapport.pdf).
