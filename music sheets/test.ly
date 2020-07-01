%{
Welcome to LilyPond
===================

Congratulations, LilyPond has been installed successfully.

Now to take it for the first test run.

  1. Save this LilyPond file on your desktop with the name "test.ly".

  2. Pick it up from the desktop with your mouse pointer, drag and drop
     it onto the LilyPond icon.

  3. LilyPond automatically produces a PDF file from the musical scale
     below.

  4. To print or view the result, click on the newly produced file
     called

        test.pdf

  5. If you see a piece of music with a scale, LilyPond is working properly.

Next, you'll want to get started on your own scores.  To do this you'll 
  need to learn about using LilyPond.

LilyPond's interface is text-based, rather than graphical. Please visit the
  help page at http://lilypond.org/introduction.html.  This will
  point you to a quick tutorial and extensive documentation.

Good luck with LilyPond!  Happy engraving.

%}

% \version "2.20.0"  % necessary for upgrading to future LilyPond versions.

% \header{
%   title = "A scale in LilyPond"
%   subtitle = "Tach auch"
% }

\relative c'
{
    \clef "treble" \numericTimeSignature\time 4/4 \tempo 4=40
    c d e f g2 g a4 a a a g1 a4 a a a \break
    g1 f4 f f f e2 e d4 d d d c1
}